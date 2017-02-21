package ru.salauyou.yamlparser.impl;

import static ru.salauyou.yamlparser.ParserException.Reason.MISSING_SYMBOL;
import static ru.salauyou.yamlparser.ParserException.Reason.UNEXPECTED_EOL;
import static ru.salauyou.yamlparser.ParserException.Reason.UNEXPECTED_SYMBOL;
import static ru.salauyou.yamlparser.ParserException.Reason.WRONG_INTENDATION;

import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import ru.salauyou.yamlparser.ObjectHandler;
import ru.salauyou.yamlparser.ObjectHandler.KeyHandle;
import ru.salauyou.yamlparser.ParserException;
import ru.salauyou.yamlparser.ParserException.Description;
import ru.salauyou.yamlparser.ParserException.Reason;
import ru.salauyou.yamlparser.Processor;


public abstract class ProcessorImpl implements Processor {

  ObjectHandler handler;
  
  @Override
  public void setObjectHandler(ObjectHandler handler) {
    this.handler = Objects.requireNonNull(handler);
  }
  
  
  abstract void acceptKey(
      @Nonnull Object parser, 
      @Nonnull String key);
  
  
  abstract void acceptValue(
      @Nonnull Object parser, 
      @Nullable Object value);
  
  
  /**
   * Returns back given number of chars
   */
  abstract void returnChars(int chars);

  
  /**
   * Skips a line
   */
  abstract void skipLine();
  
  
  /**
   * Returns next char, 
   * or -1 if no chars left
   */
  abstract int nextChar();
  
  
  
  void acceptParserError(Reason reason) {
    acceptParserError(reason, null);
  }
  
  
  void acceptParserError(Reason reason, String what) {
    handler.acceptParserError(new ParserException(
        new SimpleDescription(-1, -1, reason, what))); // TODO: actual line, column
  }

  
  static KeyHandle newKeyHandle(
       final String key, final KeyHandle parent) {
    
    return new KeyHandle() {
      @Override public String key() {
        return key;
      }
      @Override public KeyHandle parent() {
        return parent;
      }
    };
  }

  
  static class SimpleDescription implements Description {
    
    static final Map<Reason, String> REASONS 
      = ImmutableMap.of(
          MISSING_SYMBOL, "Missing", 
          UNEXPECTED_SYMBOL, "Unexpected", 
          UNEXPECTED_EOL, "Unexpected EOL",
          WRONG_INTENDATION, "Wrong intendation");
      
    final int line;
    final int column;
    final String what;
    final Reason reason;
    
    SimpleDescription(int line, int column, 
        Reason reason, String what) {
      this.line = line;
      this.column = column;
      this.reason = reason;
      this.what = (reason == UNEXPECTED_EOL 
          || reason == WRONG_INTENDATION)
         ? null : what;
    }

    @Override public int getLine() { return line; }

    @Override public int getColumn() { return column; }
 
    @Override public String getWhat() { return what; }

    @Override public Reason getReason() { return reason; }
    
    @Override public String toString() {
      String msg = (reason == Reason.OTHER) ? what
          : (what == null ? REASONS.get(reason)
              : String.format("%s '%s'", REASONS.get(reason), what));
      return msg + String.format(" at line %d, column %d", line, column);
    }
    
    
  }
  
}