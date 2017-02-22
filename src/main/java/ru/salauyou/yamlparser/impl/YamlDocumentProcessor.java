package ru.salauyou.yamlparser.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

import ru.salauyou.yamlparser.ObjectHandler;
import ru.salauyou.yamlparser.ObjectHandler.KeyHandle;
import ru.salauyou.yamlparser.ObjectHandler.ObjectHandle;


public class YamlDocumentProcessor extends ProcessorImpl {

  Deque<ObjectParser> parsers = Queues.newArrayDeque();
  Map<ObjectParser, KeyHandle> keyHandles = Maps.newHashMap();
  KeyHandle currentKeyHandle;
  
  Queue<Character> returnedChars = Queues.newArrayDeque();
  Deque<Character> processedChars = Queues.newArrayDeque();
  
  Character current;
  ObjectHandle objHandle;
  
  String input;
  int cursor = -1;
  
  public YamlDocumentProcessor() {}
  
  
  public YamlDocumentProcessor(@Nonnull ObjectHandler handler) {
    setObjectHandler(handler);
  }
  
  
  @Override
  public void parse(InputStream input) throws IOException {
    // TODO: implement
    throw new UnsupportedOperationException();
  }
  
  
  @Override
  public void parse(String source) {
    input = source;
    
    // parse using blocked parser
    parsers.addLast(new BlockedObjectParser(this, null, 0));
    parsers.peekLast().go();
    
    // close currently open keys
    ObjectParser p;
    KeyHandle h;
    while ((p = parsers.pollLast()) != null) {
      if ((h = keyHandles.get(p)) != null) {
        handler.closeKey(h);
      }
    }
    // close object
    if (objHandle != null) {
      handler.closeObject(objHandle);
      objHandle = null;
    }
  }

  
  @Override
  void skipLine() {
    int c;
    while ((c = nextChar()) >= 0 && c != '\n');
  }


  @Override
  int nextChar() {
    if (!returnedChars.isEmpty()) {
      processedChars.addLast(returnedChars.poll());
      return processedChars.peekLast();
    } 
    if (input.length() <= ++cursor) {
      return -1;
    } else {
      processedChars.addLast(input.charAt(cursor));
      return processedChars.peekLast();
    }
  }
  
  
  Deque<Character> toReturn = Queues.newArrayDeque();
  
  @Override
  public void returnChars(int i) {
    for (; i > 0 && !processedChars.isEmpty(); --i) {
      toReturn.addFirst(processedChars.pollLast());
    }
    returnedChars.addAll(toReturn);
    toReturn.clear();
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
    
    // try to find parser in stack
    // closing child parser keys
    if (parsers.contains(parser)) {
      it = parsers.descendingIterator();
      while (it.hasNext() && parser != (p = it.next())) {
        it.remove();
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


  
  
}
