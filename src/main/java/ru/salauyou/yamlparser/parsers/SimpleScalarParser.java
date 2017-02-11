package ru.salauyou.yamlparser.parsers;

import ru.salauyou.yamlparser.ItemParser;
import ru.salauyou.yamlparser.Processor;

public class SimpleScalarParser implements ItemParser {

  StringBuilder buffer = new StringBuilder();
  
  boolean spaceMet = true;
  boolean comment = false;
  
  @Override
  public ItemParser acceptChar(Processor processor, char c) {
    if (c == BR) {
      acceptIfNeeded(processor);
      processor.returnChar();
      return null;
    }
    if (comment) {
      return this;
    }
    if (spaceMet && c == '#') {
      comment = true;
      return this;
    }
    if (c == ',' || c == ':' || c == '}' || c == '{') {
      acceptIfNeeded(processor);
      processor.returnChar();
      return null;
    }
    spaceMet = Character.isWhitespace(c);
    buffer.append(c);
    return this;
  }

  
  void acceptIfNeeded(Processor processor) {
    String result = buffer.toString().trim();
    if (!result.isEmpty()) {
      processor.acceptResult(result);
    }
  }
  
  
  @Override
  public void acceptResult(Object result) {
    throw new UnsupportedOperationException();
  }

}
