package ru.salauyou.yamlparser.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
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
import ru.salauyou.yamlparser.ParserException.Reason;


public class LineProcessor extends ProcessorImpl {

  Deque<ItemParser> parsers = Queues.newArrayDeque();
  Map<ItemParser, KeyHandle> keyHandles = Maps.newHashMap();
  KeyHandle currentKeyHandle;
  
  Queue<Character> returnedChars = Queues.newArrayDeque();
  
  Character current;
  ObjectHandle objHandle;
  
  
  public LineProcessor() {}
  
  
  public LineProcessor(@Nonnull ObjectHandler handler) {
    setObjectHandler(handler);
  }
  
  
  @Override
  public void parse(InputStream input) throws IOException {
    // TODO: implement
    throw new UnsupportedOperationException();
  }
  
  
  @Override
  public void parse(String input) {
    if (!input.endsWith("\n")) {
      input += '\n';
    }
    // process chars
    for (int i = 0; i < input.length(); ++i) {
      processChar(current = input.charAt(i));
      while ((current = returnedChars.poll()) != null) {
        processChar(current);
      }
    }
    if (!parsers.isEmpty()) {
      acceptParserError(1, 1, Reason.UNEXPECTED_EOL); // TODO: real position
    }
    // close currently open keys
    ItemParser p;
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

  
  void processChar(char c) {
    ItemParser parser = parsers.peekLast();
    if (parser == null) {
      parser = new KeyValueParser(false);
      parsers.add(parser);
    }
    ItemParser res = null;
    try {
      res = parser.acceptChar(this, c);
    } catch (ParseException e) {
      acceptParserError(1, 1, // TODO: real position
          Reason.UNEXPECTED_SYMBOL, e.getMessage());
      returnedChars.clear();
      // TODO: skip line
    }
    if (res == null) {
      KeyHandle h;
      ItemParser p = parsers.pollLast();
      if ((h = keyHandles.remove(p)) != null) {
        handler.closeKey(h);
      }
    } else if (res != parser) {
      parsers.add(res);
    }
  }
  

  @Override
  public void returnChars(int chars) {
    // TODO: implement
    throw new UnsupportedOperationException();
  }
  
  
  @Override
  public void returnChar() {
    returnedChars.add(current);
  }


  @Override
  public void acceptKey(ItemParser parser, String key) {
    // close currently opened key if any
    KeyHandle h = keyHandles.remove(parser);
    if (h != null) {
      handler.closeKey(h);
    }
    // find parent key handle
    KeyHandle parent = null;
    ItemParser p;
    Iterator<ItemParser> it = parsers.descendingIterator();
    while (it.hasNext() && (p = it.next()) != null) {
      if ((parent = keyHandles.get(p)) != null) {
        break;
      }
    }
    // open key in handler
    h = newKeyHandle(key, parent);
    keyHandles.put(parser, h);
    currentKeyHandle = h;
    if (objHandle == null) {
      objHandle = new ObjectHandle(){};
      handler.openObject(objHandle, h);
    }
    handler.openKey(h);
  }


  @Override
  public void acceptValue(ItemParser parser, Object value) {
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
