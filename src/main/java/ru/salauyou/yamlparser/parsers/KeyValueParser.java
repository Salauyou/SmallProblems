package ru.salauyou.yamlparser.parsers;

import static ru.salauyou.yamlparser.ItemParser.throwUnexpected;

import ru.salauyou.yamlparser.ItemParser;
import ru.salauyou.yamlparser.Processor;

public class KeyValueParser implements ItemParser {

  final boolean bracketed;
  Processor processor;
  
  boolean spaceMet = true;
  boolean expectValue = false;
    
  
  public KeyValueParser(boolean bracketed) {
    this.bracketed = bracketed;
  }
  

  @Override
  public ItemParser acceptChar(Processor processor, char c) {
    this.processor = processor;
    if (c == BR && !bracketed) {
      processor.returnChar();
      return null;
    } 
    if (Character.isWhitespace(c)) {
      return this;
    } 
    switch (c) {
    case ':':
      if (expectValue) {
        throwUnexpected(c);
      }
      expectValue = true;
      return this;

    case ',':
      if (!bracketed || expectValue) {
        throwUnexpected(c);
      }
      expectValue = false;
      return this;

    case '}':
      if (!bracketed || expectValue) {
        throwUnexpected(c);
      }
      return null;

    case '{':
      if (!expectValue) {
        throwUnexpected(c);
      }
      return new KeyValueParser(true);
      
    case '\'':
      return new EscapedSingleQuoteParser(this);

    case '"':
      return new EscapedDoubleQuoteParser(this);

    default:
      processor.returnChar();
      return new SimpleScalarParser(this);
    }
  }
  

  @Override
  public void acceptScalarResult(CharSequence result) {
    if (expectValue) {
      processor.acceptValue(this, result.toString());
    } else {
      processor.acceptKey(this, result.toString());
    }
    expectValue = !expectValue;
  }

}
