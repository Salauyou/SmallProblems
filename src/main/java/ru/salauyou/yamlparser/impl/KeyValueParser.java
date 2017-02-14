package ru.salauyou.yamlparser.impl;

import java.text.ParseException;

public class KeyValueParser implements ItemParser {
  
  final boolean bracketed;
  ProcessorImpl processor;
  
  boolean spaceMet = true;
  
  boolean expectKey = true;
  boolean expectValue = false;
  
  
  public KeyValueParser(boolean bracketed) {
    this.bracketed = bracketed;
  }
  

  @Override
  public ItemParser acceptChar(ProcessorImpl processor, char c) throws ParseException {
    this.processor = processor;
    
    if (c == '\n' && !bracketed) {
      return null;
    
    } else if (Character.isWhitespace(c)) {
      return this;
    
    } else if (c == ':') {
      if (expectKey || expectValue) {
        throwUnexpected(c);
      }
      expectValue = true;
      return this;

    } else if (c == ',') { 
      if (!bracketed || expectKey || expectValue) {
        throwUnexpected(c);
      }
      expectKey = true;
      return this;

    } else if (c == '}') {
      if (!bracketed || expectKey || expectValue) {
        throwUnexpected(c);
      }
      return null;

    } else if (c == '{') {
      if (!expectValue) {
        throwUnexpected(c);
      }
      expectValue = false;
      return new KeyValueParser(true);

    } else if (c == '#') {
      processor.returnChar();
      return new SimpleScalarParser(this);
      
    } else if (!expectKey && !expectValue) {
      throwUnexpected(c);
      return null;
      
    } else if (c == '\'') {
      return new EscapedSingleQuoteParser(this);
      
    } else if (c == '"') {
      return new EscapedDoubleQuoteParser(this);
      
    } else {
      processor.returnChar();
      return new SimpleScalarParser(this);
    }
  }
  

  @Override
  public void acceptScalarResult(CharSequence result) {
    if (expectValue) {
      processor.acceptValue(this, result.toString());
      expectValue = false;
    } else if (expectKey) {
      processor.acceptKey(this, result.toString());
      expectKey = false;
    }
  }
  
  
  static void throwUnexpected(char c) throws ParseException {
    throw new ParseException(String.valueOf(c), 0);
  }

}
