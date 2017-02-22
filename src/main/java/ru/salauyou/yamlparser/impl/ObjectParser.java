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

  
}
