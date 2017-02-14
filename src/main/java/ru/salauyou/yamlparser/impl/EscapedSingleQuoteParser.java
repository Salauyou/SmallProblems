package ru.salauyou.yamlparser.impl;

public class EscapedSingleQuoteParser implements ItemParser {

  final ItemParser parent;
  final StringBuilder buffer = new StringBuilder();
  
  boolean quoteMet = false;
  
  
  EscapedSingleQuoteParser(ItemParser parent) {
    this.parent = parent;
  }
  
  @Override
  public ItemParser acceptChar(ProcessorImpl processor, char c) {
    if (quoteMet) {
      if (c == '\'') {
        buffer.append(c);
        quoteMet = false;
      } else {
        parent.acceptScalarResult(buffer);
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
  public void acceptScalarResult(CharSequence result) {
    throw new UnsupportedOperationException(
        "I don't accept anything!");
  }

}
