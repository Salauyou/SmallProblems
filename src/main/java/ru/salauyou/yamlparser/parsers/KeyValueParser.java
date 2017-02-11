package ru.salauyou.yamlparser.parsers;

import static ru.salauyou.yamlparser.ItemParser.throwUnexpected;

import java.util.Map;

import com.google.common.collect.Maps;

import ru.salauyou.yamlparser.ItemParser;
import ru.salauyou.yamlparser.Processor;

public class KeyValueParser implements ItemParser {

  final boolean bracketed;
  
  boolean spaceMet = true;
  
  Map<String, Object> result = Maps.newLinkedHashMap();
  String key = null;

  boolean expectValue = false;
  
  public KeyValueParser(boolean bracketed) {
    this.bracketed = bracketed;
  }
  
  
  @Override
  public ItemParser acceptChar(Processor processor, char c) {
    if (c == BR && !bracketed) {
      if (key != null 
          && !result.containsKey(key)) {
        result.put(key, null);
      }
      processor.acceptResult(result);
      processor.returnChar();
      return null;
    } 
    if (Character.isWhitespace(c)) {
      return this;
    } 
    switch (c) {
    case ':':
      if (key == null || expectValue) {
        throwUnexpected(c);
      }
      expectValue = true;
      return this;

    case ',':
      if (!bracketed || key != null || expectValue) {
        throwUnexpected(c);
      }
      return this;

    case '}':
      if (!bracketed || expectValue) {
        throwUnexpected(c);
      }
      processor.acceptResult(result);
      return null;

    case '{':
      if (!expectValue) {
        throwUnexpected(c);
      }
      return new KeyValueParser(true);
      
    case '\'':
      return new EscapedSingleQuoteParser();

    case '"':
      return new EscapedDoubleQuoteParser();

    default:
      processor.returnChar();
      return new SimpleScalarParser();
    }
  }
  

  @Override
  public void acceptResult(Object result) {
    if (result instanceof CharSequence) {
      result = result.toString();
    }
    if (expectValue) {
      this.result.put(key, result);
      key = null;
    } else {
      if (result instanceof String) {
        key = result.toString();
        if (this.result.containsKey(key)) {
          throw new IllegalStateException(
              String.format("Duplicate key '%s'", key));
        }
      } else {
        throw new IllegalStateException(
            "Only scalar may be a key");
      }
    }
    expectValue = false;
  }

}
