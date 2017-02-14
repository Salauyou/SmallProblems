package ru.salauyou.yamlparser.impl;

import java.text.ParseException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ItemParser {

  
  /**
   * Accepts next char from processor. Returns 
   * who must process next char (may be `this`, 
   * or some another underlying parser, 
   * or `null` if this parser finished its work 
   * and next chars should be sent to
   * overlying parser) 
   */
  @Nullable ItemParser acceptChar(
      @Nonnull ProcessorImpl processor, char c) throws ParseException;
  
  
  /**
   * Accepts result from underlying parser
   */
  void acceptScalarResult(@Nonnull CharSequence result);
  
  
}
