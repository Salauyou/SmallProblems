package ru.salauyou.yamlparser.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

import ru.salauyou.yamlparser.ObjectHandler;
import ru.salauyou.yamlparser.ObjectHandler.KeyHandle;
import ru.salauyou.yamlparser.ObjectHandler.ObjectHandle;


public class YamlDocumentProcessor extends ProcessorImpl {

  final Deque<ObjectParser> parsers = Queues.newArrayDeque();
  final Map<ObjectParser, KeyHandle> keyHandles = Maps.newHashMap();
  
  KeyHandle currentKeyHandle;
  ObjectHandle objHandle;
  
  LineIterator lines;
  
  
  public YamlDocumentProcessor() {}
  
  
  public YamlDocumentProcessor(@Nonnull ObjectHandler<?> handler) {
    setObjectHandler(Objects.requireNonNull(handler));
  }
  
  
  @Override
  public void parse(@Nonnull File input, 
      @Nullable Charset charset) throws IOException {
    
    lines = FileUtils.lineIterator(
        Objects.requireNonNull(input), 
        charset == null ? DEFAULT_CHARSET : charset.name());
    doParse();
    lines.close();
  }
  
  
  @Override
  public void parse(@Nonnull String source) {
    Objects.requireNonNull(source);
    lines = new LineIterator(new StringReader(source));
    doParse();
    lines.close();
  }
  
  
  static final int BUFFER_MAX_SIZE = 300;
  final Deque<Character> returnedChars = Queues.newArrayDeque();
  final Deque<Character> processedChars = Queues.newArrayDeque();

  String currentLine = "";
  int line = 0, column = 0;
  boolean eol = true;
  
  
  void doParse() {
    // reset
    currentLine = "";
    line = 0;
    column = 0;
    eol = true;
    
    // parse using blocked parser
    parsers.addLast(new BlockObjectParser(this, null, 0));
    parsers.peekLast().go();
    
    // close currently open keys
    ObjectParser p;
    while ((p = parsers.pollLast()) != null) {
      if (keyHandles.containsKey(p)) {
        handler.closeKey(keyHandles.remove(p));
      }
    }
    // close object
    if (objHandle != null) {
      handler.closeObject(objHandle);
      objHandle = null;
    }
  }

  
  @Override
  int nextChar() {
    if (!returnedChars.isEmpty()) {
      return addProcessed(returnedChars.poll());
    }
    for (;;) {
      if (column == currentLine.length()) {
        if (eol) {   // line break processed
          if (lines.hasNext()) {
            currentLine = lines.next();
            eol = false;
            ++line;
            column = 0;
            continue;
          } else {   // no more lines
            return -1;
          }
        } else {     // line break not processed
          eol = true;
          return addProcessed(BR);
        }
      } else {
        return addProcessed(currentLine.charAt(column++));
      }
    }
  }
  
  
  int addProcessed(int c) {
    processedChars.addLast((char) c);
    while (processedChars.size() > BUFFER_MAX_SIZE) {
      processedChars.pollFirst();
    }
    return c;
  }
  
  
  @Override
  public void returnChars(int i) {
    for (; i > 0 && !processedChars.isEmpty(); --i) {
      returnedChars.addFirst(processedChars.pollLast());
    }
  }

  
  @Override
  public void acceptKey(ObjectParser parser, String key) {
    // close currently opened key if any
    KeyHandle h = keyHandles.remove(parser);
    if (h != null) {
      handler.closeKey(h);
    }
    // find parent key handle
    KeyHandle parent = null;
    ObjectParser p;
    Iterator<ObjectParser> it = parsers.descendingIterator();
    while (it.hasNext() && (p = it.next()) != null) {
      if ((parent = keyHandles.get(p)) != null) {
        break;
      }
    }
    // open key in handler
    h = newKeyHandle(key, parent);
    currentKeyHandle = h;
    keyHandles.put(parser, h);
    
    // find or add this parser in the queue
    // (closing child parsers if any)
    if (parsers.contains(parser)) {
      while ((p = parsers.peekLast()) != parser) {
        parsers.pollLast();
        if (keyHandles.containsKey(p)) {
          handler.closeKey(keyHandles.remove(p));
        }
      }
    } else {
      parsers.addLast(parser);
    }
    // open object if not opened
    if (objHandle == null) {
      objHandle = new ObjectHandle(){};
      handler.openObject(objHandle, h);
    }
    // open key
    handler.openKey(h);
  }


  @Override
  public void acceptValue(ObjectParser parser, Object value) {
    // values for the same key must be produced 
    // by the same parser, and the key must be 
    // currently open
    KeyHandle h = keyHandles.get(parser); 
    if (h == null || h != currentKeyHandle) {
      throw new IllegalStateException(String.valueOf(h));
    }
    handler.acceptValue(h, value);
  }

  
  @Override
  public void skipObject() {
    // TODO: implement
    throw new UnsupportedOperationException();
  }

  
  @Override
  public void skipAll() {
    // TODO: implement
    throw new UnsupportedOperationException();
  }


  // TODO: count more accurately
  // considering returned chars
  
  @Override
  int line() {
    return line;
  }

  
  @Override
  int column() {
    return Math.max(1, column - returnedChars.size());
  }
  
}
