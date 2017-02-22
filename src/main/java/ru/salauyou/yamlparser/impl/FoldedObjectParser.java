package ru.salauyou.yamlparser.impl;

public class FoldedObjectParser extends ObjectParser {
  
  
  protected FoldedObjectParser(ProcessorImpl processor) {
    super(processor);
  }

  
  @Override
  protected void go() {
    for (;;) {
      int c;
      // read key
      String key = null;
      while (key == null) {
        processor.skipSpaces();
        c = processor.nextChar();
        if (c < 0) {
          reportMissing('}');
          return;
        } else if (c == '\n') {
          continue;
        } else if (c == '#') {
          processor.skipLine();
        } else if (c == '}') {
          return;
        } else if (c == ':' || c == '{' || c == ',') {
          reportUnexpected(c);
        } else if (c == '\'') {
          key = new PlainParsers.SingleQuoted(processor).parse();
        } else if (c == '"') {
          key = new PlainParsers.DoubleQuoted(processor).parse();
        } else {
          processor.returnChars(1);
          key = new PlainParsers.Scalar(processor).parse();
        }
      }
      
      // read colon separator
      if (!findSeparator(':')) {
        return;
      }
      processor.acceptKey(this, key);
      
      // read value
      boolean found = false;
      String value = null;
      while (!found) {
        processor.skipSpaces();
        c = processor.nextChar();
        if (c < 0) {
          reportMissing('}');
          return;
        } else if (c == '#') {
          processor.skipLine();
        } else if (c == '\n') {
          continue;
        } else if (c == '}') {
          return;
        } else if (c == ',') {
          found = true;  // null value
        } else if (c == ':') {
          reportUnexpected(c);
        } else if (c == '{') {
          found = true;
          new FoldedObjectParser(processor).go();
        } else if (c == '\'') {
          value = new PlainParsers.SingleQuoted(processor).parse();
        } else if (c == '"') {
          value = new PlainParsers.SingleQuoted(processor).parse();
        } else {
          processor.returnChars(1);
          value = new PlainParsers.Scalar(processor).parse();
          found = value != null;  // if empty, try on next line
        }
      }
      
      if (value != null) {
        processor.acceptValue(this, value);
      }
      
      // read comma separator
      if (!findSeparator(',')) {
        return;
      }
    }
  }
  
  
  // returns true if separator found,
  // false if not found (thus parser must exit)
  boolean findSeparator(char separator) {
    for (;;) {
      // need to iterate, because in folded 
      // object separator may appear on anther line
      processor.skipSpaces();
      int c = processor.nextChar();
      if (c < 0) {
        reportMissing(separator);
        return false;
      } else if (c == '#') {
        processor.skipLine();
      } else if (c == '\n') {
        continue;
      } else if (c == separator) {
        return true;
      } else {
        if (c == '}') {
          return false;
        }
        reportUnexpected(c);
      }
    }
  }
  
  
}
