package ru.salauyou.yamlparser;

import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

import ru.salauyou.yamlparser.ObjectHandler.KeyHandle;
import ru.salauyou.yamlparser.ObjectHandler.ObjectHandle;
import ru.salauyou.yamlparser.parsers.KeyValueParser;

public class LineProcessor implements Processor {

  final ObjectHandler handler;
  
  Deque<ItemParser> parsers = Queues.newArrayDeque();
  Map<ItemParser, KeyHandle> keyHandles = Maps.newHashMap();
  KeyHandle currentKeyHandle;
  
  Queue<Character> returnedChars = Queues.newArrayDeque();
  
  Character current;
  ObjectHandle objHandle;
  
  
  public LineProcessor(@Nonnull ObjectHandler handler) {
    this.handler = Objects.requireNonNull(handler);
  }
  
  
  public void parseString(String input) {
    if (!input.endsWith("\n")) {
      input += ItemParser.BR;
    }
    // process chars
    for (int i = 0; i < input.length(); ++i) {
      processChar(current = input.charAt(i));
      while ((current = returnedChars.poll()) != null) {
        processChar(current);
      }
    }
    if (!parsers.isEmpty()) {
      // TODO: replace by warning
      throw new IllegalStateException(
          "Unexpected end of line");
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
    ItemParser res = parser.acceptChar(this, c);
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
  public void returnChars(CharSequence chars) {
    for (int i = 0; i < chars.length(); ++i) {
      returnedChars.add(chars.charAt(i));
    }
  }
  
  
  @Override
  public void returnChar() {
    returnedChars.add(current);
  }


  @Override
  public void acceptKey(ItemParser parser, String key) {
    // find parent key handle
    KeyHandle parent = null;
    ItemParser p;
    Iterator<ItemParser> it = parsers.descendingIterator();
    while (it.hasNext() && (p = it.next()) != null) {
      if (p != parser && (parent = keyHandles.get(p)) != null) {
        break;
      }
    }
    // open key in handler
    KeyHandle h = newHandle(key, parent);
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

  
  static KeyHandle newHandle(final String key, final KeyHandle parent) {
    return new KeyHandle() {
      @Override public String key() {
        return key;
      }
      @Override public KeyHandle parent() {
        return parent;
      }
    };
  }
  
}
