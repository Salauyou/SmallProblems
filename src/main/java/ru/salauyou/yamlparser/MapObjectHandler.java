package ru.salauyou.yamlparser;

import java.util.Deque;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

public class MapObjectHandler implements ObjectHandler {
  
  final boolean ignoreParserErrors;
  boolean open = true;
  
  Deque<Map<String, Object>> result = Queues.newArrayDeque();
  Map<String, Object> valueConsumer;
 
  
  public MapObjectHandler(boolean ignoreParserErrors) {
    this.ignoreParserErrors = ignoreParserErrors;
  }
  
  
  @Override
  public void openObject(
       ObjectHandle objectHandle, 
       KeyHandle firstKey) {
    if (!open) {
      throw new IllegalStateException("Already closed!");
    }
    valueConsumer = Maps.newLinkedHashMap();
    result.add(valueConsumer);
  }

  
  @Override
  public void closeObject(ObjectHandle objectHandle) {
    open = false;
  }

  
  @Override
  public void openKey(KeyHandle key) {
    valueConsumer = result.getLast();
    if (valueConsumer.containsKey(key.key())) {
      throw new IllegalStateException("Duplicate key!");
    }
    Map<String, Object> m = Maps.newLinkedHashMap();
    valueConsumer.put(key.key(), m);
    result.addLast(m);  // ready to accept nested key
  }

  
  @Override
  public void acceptValue(KeyHandle key, Object value) {
    if (!(valueConsumer.get(key.key()) instanceof Map)) {
      throw new IllegalStateException("Another value for a key!");
    }
    valueConsumer.put(key.key(), value);
  }
  

  @Override
  public void closeKey(KeyHandle key) {
    // if value has not arrived, put null
    Object v;
    if (valueConsumer != null 
        && (v = valueConsumer.get(key.key())) instanceof Map
        && ((Map<?,?>) v).isEmpty()) {
      valueConsumer.put(key.key(), null);
    }
    result.removeLast();
    valueConsumer = null;
  }
  
  
  @Override
  public Map<String, Object> getResult() {
    return result.peekFirst();
  }


  @Override
  public void acceptParserError(ParserException error) {
    if (!ignoreParserErrors) {
      throw new RuntimeException(
          error.getDescription().toString(), error);
    }
  }
  
}
