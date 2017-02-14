package ru.salauyou.yamlparser.impl;

public class EscapedDoubleQuoteParser implements ItemParser {

  final ItemParser parent;
  
  public EscapedDoubleQuoteParser(ItemParser parent) {
    this.parent = parent;
  }
  
  
  @Override
  public ItemParser acceptChar(ProcessorImpl processor, char c) {
    // TODO Auto-generated method stub
    return null;
  }

  
  @Override
  public void acceptScalarResult(CharSequence result) {
    // TODO Auto-generated method stub
  }

}
