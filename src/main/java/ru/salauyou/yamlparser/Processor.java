package ru.salauyou.yamlparser;

public interface Processor {

  /**
   * Callback to return back sequence of chars
   */
  void returnChars(CharSequence chars);
  
  
  /**
   * Callback to return back next offered char
   */
  void returnChar();
  
  
  /**
   * Accepts result from parser and
   * sends it into overlying parser or
   * processor
   */
  void acceptResult(Object result);
  
  
}
