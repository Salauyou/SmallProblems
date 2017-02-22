package ru.salauyou.yamlparser.impl;

import ru.salauyou.yamlparser.ParserException.Reason;

public abstract class ObjectParser {

  final protected ProcessorImpl processor;
  
  protected ObjectParser(ProcessorImpl processor) {
    this.processor = processor;
  }
 
  protected abstract void go();
  
  
  protected void reportUnexpected(int c) {
    processor.acceptParserError(
        Reason.UNEXPECTED_SYMBOL, String.valueOf((char) c));
  }
  
  
  protected void reportMissing(int c) {
    processor.acceptParserError(
        Reason.MISSING_SYMBOL, String.valueOf((char) c));
  }

  
  protected void skipSpaces() {
    countSpaces(false);
  }
  
  
  protected int countSpaces(boolean strict) {
    int c = 0, i = 0;
    while ((c = processor.nextChar()) >= 0 && c != '\n') {
      if (Character.isWhitespace(c)) {
        if (strict && c != ' ') {
          reportUnexpected(c);
        } else {
          ++i;
        }
      } else {
        break;
      }
    }
    processor.returnChars(1);
    return i;
  }
  
}
