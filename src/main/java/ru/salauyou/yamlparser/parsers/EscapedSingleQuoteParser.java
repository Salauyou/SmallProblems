package ru.salauyou.yamlparser.parsers;

import ru.salauyou.yamlparser.ItemParser;
import ru.salauyou.yamlparser.Processor;

public class EscapedSingleQuoteParser implements ItemParser {

  boolean quoteMet = false;
  StringBuilder buffer = new StringBuilder();
  
  @Override
  public ItemParser acceptChar(Processor processor, char c) {
    if (quoteMet) {
      if (c == '\'') {
        buffer.append(c);
        quoteMet = false;
      } else {
        processor.acceptResult(buffer);
        processor.returnChar();
        return null;
      }
    } else {
      if (c == '\'') {
        quoteMet = true;
      } else {
        buffer.append(c);
      }
    }
    return this;
  }


  @Override
  public void acceptResult(Object result) {
    throw new UnsupportedOperationException();
  }

}
