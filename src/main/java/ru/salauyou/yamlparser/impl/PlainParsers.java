package ru.salauyou.yamlparser.impl;

import ru.salauyou.yamlparser.ParserException.Reason;

public class PlainParsers {

  abstract static class PlainParser {
    
    protected final StringBuilder buffer;
    protected final ProcessorImpl processor;
    
    PlainParser(ProcessorImpl processor) {
      this.processor = processor;
      this.buffer = new StringBuilder();
    }
    
    abstract String parse();
    
  }
  
  
  static class DoubleQuoted extends PlainParser {

    DoubleQuoted(ProcessorImpl processor) {
      super(processor);
    }
    
    @Override
    String parse() {
      return "";
    }

  }
  
  
  static class SingleQuoted extends PlainParser {
    
    boolean quoteMet = false;
    
    SingleQuoted(ProcessorImpl processor) {
      super(processor);
    }  

    @Override
    String parse() {
      int c;
      while ((c = processor.nextChar()) >= 0) {
        if (quoteMet) {
          if (c == '\'') {
            buffer.append((char) c);
            quoteMet = false;
          } else {
            processor.returnChars(1);
            return buffer.toString();
          }
        } else if (c == '\'') {
          quoteMet = true;
        } else if (c > 0x1f) {
          buffer.append((char) c);
        }
      }
      if (!quoteMet) {
        processor.acceptParserError(
            Reason.MISSING_SYMBOL, "'");
      }
      return buffer.toString();
    }
  }
  
  
  static class Scalar extends PlainParser {
    
    boolean spaceMet;
    
    Scalar(ProcessorImpl processor) {
      super(processor);
    }

    @Override
    String parse() {
      for (;;) {
        int c = processor.nextChar();
        if (c < 0) {
          return result();
        } else if (spaceMet && c == '#') {
          processor.skipLine();
          return result();
        } else if (c == '\n' || c == ',' 
            || c == ':' || c == '}' || c == '{') {
          processor.returnChars(1);
          return result();
        } else {
          // TODO: fold multiple whitespaces into ' '
          spaceMet = Character.isWhitespace(c);
          if (c > 0x1f) {
            buffer.append((char) c);
          }
        }
      }
    }
      
    String result() {
      String result = buffer.toString().trim();
      return result.isEmpty() ? null : result;
    }
      
  }
  
  
  private PlainParsers() {}
  
}
