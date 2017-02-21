package ru.salauyou.yamlparser.impl;

import ru.salauyou.yamlparser.ParserException.Reason;

public abstract class ObjectParser {

  final protected ProcessorImpl processor;
  
  protected ObjectParser(ProcessorImpl processor) {
    this.processor = processor;
  }
 
  protected abstract void go();
  
  
  protected void throwUnexpected(int c) {
    processor.acceptParserError(
        Reason.UNEXPECTED_SYMBOL, String.valueOf((char) c));
  }
  

  protected void skipSpaces() {
    countSpaces(false);
  }
  
  protected int countSpaces(boolean strict) {
    int c = 0, i = 0;
    while ((c = processor.nextChar()) >= 0) {
      if (Character.isWhitespace(c)) {
        ++i;
      }
      if (strict && c != ' ') {
        throwUnexpected(c); 
      }
    }
    processor.returnChars(1);
    return i;
  }
  
}
