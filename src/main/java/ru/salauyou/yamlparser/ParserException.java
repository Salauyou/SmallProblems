package ru.salauyou.yamlparser;

public class ParserException extends Exception {

  public enum Reason {   
    WRONG_INTENDATION,
    UNEXPECTED_SYMBOL,
    MISSING_SYMBOL,
    UNEXPECTED_EOL,
    OTHER;
  }
  
  public interface Description {
    int getLine();
    int getColumn();    
    Reason getReason();
    String getWhat();
  }
  
  
  // ------
  
  private static final long serialVersionUID = 2563542347L;
  
  final Description description;
  
  public ParserException(Description description) {
    this.description = description;
  }
  
  public Description getDescription() {
    return description;
  }
  
}
