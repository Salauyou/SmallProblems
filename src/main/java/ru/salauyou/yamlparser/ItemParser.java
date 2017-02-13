package ru.salauyou.yamlparser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ItemParser {

  char BR = '\n';
  
  /**
   * Accepts next char from processor. Returns 
   * who must process next char (may be `this`, 
   * or some another underlying parser, 
   * or `null` if this parser finished its work 
   * and next chars should be sent to
   * overlying parser) 
   */
  @Nullable ItemParser acceptChar(
      @Nonnull Processor processor, char c);
  
  
  /**
   * Accepts result from underlying parser
   */
  void acceptScalarResult(@Nonnull CharSequence result);
  
  
  static void throwUnexpected(char c) {
    throw new IllegalStateException(
        String.format("Unexpected char '%s'", c));
  }
  
}
