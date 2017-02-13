package ru.salauyou.yamlparser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Processor {

  void acceptKey(@Nonnull ItemParser parser, @Nonnull String key);
  
  void acceptValue(@Nonnull ItemParser parser, @Nullable Object value);
  
  
  /**
   * Callback to return back sequence of chars
   */
  void returnChars(CharSequence chars);
  
  
  /**
   * Callback to return back next offered char
   */
  void returnChar();
  
  
}
