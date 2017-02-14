package ru.salauyou.yamlparser.impl;

import ru.salauyou.yamlparser.Processor;

public class SimpleScalarParser implements ItemParser {

  final ItemParser parent;
  final StringBuilder buffer = new StringBuilder();
  
  boolean spaceMet = true;
  boolean comment = false;
  
  
  SimpleScalarParser(ItemParser parent) {
    this.parent = parent;
  }
  
  
  @Override
  public ItemParser acceptChar(ProcessorImpl processor, char c) {
    if (c == '\n') {
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
      parent.acceptScalarResult(result);
    }
  }


  @Override
  public void acceptScalarResult(CharSequence result) {
    throw new UnsupportedOperationException(
        "I don't accept scalars");
  }

}
