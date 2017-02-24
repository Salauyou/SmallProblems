package ru.salauyou.yamlparser.impl;

import ru.salauyou.yamlparser.ParserException.Reason;

public class BlockObjectParser extends ObjectParser {
  
  final BlockObjectParser parent;
  final int intendation;
  
  public BlockObjectParser(ProcessorImpl processor, 
      BlockObjectParser parent, int intendation) {
    super(processor);
    this.parent = parent;
    this.intendation = intendation;
  }
  
  
  @Override
  public void go() {
    newline: for (;;) {
      
      // get intendation of current line
      int i = processor.countSpaces(true);
      
      // should it be skipped?
      int c;
      if ((c = processor.nextChar()) < 0) {
        return;
      } else {
        processor.returnChars(1);
        if (c == '\n' || c == '#') {
          processor.skipLine();
          continue newline;
        }
      }
      
      // decide who will parse it
      if (i > intendation) {
        processor.returnChars(i);
        new BlockObjectParser(processor, this, i).go();
        continue newline;
      } else if (i < intendation) {
        if (checkIntendation(i)) {
          processor.returnChars(i);
          return;
        } else {
          processor.acceptParserError(
              Reason.WRONG_INTENDATION);
          processor.skipLine();
          continue newline;
        }
      }

      // read key
      String key = null;
      while (key == null) {
        c = processor.nextChar();
        if (c < 0) {
          return;
        } else if (c == '\n') {
          continue newline;
        } else if (c == '#') {
          processor.skipLine();
          continue newline;
        } else if (c == '{' || c == '}' || c == ':' || c == ',') {
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

      // read separator
      processor.skipSpaces();
      c = processor.nextChar();
      if (c != ':') {
        if (c < 0) {
          reportMissing(':');
          return;
        } else if (c == '\n') {
          reportMissing(':');
          continue newline;
        } else {
          reportUnexpected(c);
          processor.skipLine();
          continue newline;
        }
      }

      // only after separator is found
      // send key to processor
      processor.acceptKey(this, key);

      // read value
      String value = null;
      boolean found = false;
      while (!found) {
        processor.skipSpaces();
        c = processor.nextChar();
        if (c < 0) {
          return;
        } else if (c == '\n') {
          continue newline;
        } else if (c == '#') {
          processor.skipLine();
          continue newline;
        } else if (c == '}' || c == ':') {
          reportUnexpected(c);
        } else if (c == '{') {
          found = true;
          new FoldedObjectParser(processor).go();
        } else if (c == '\'') {
          value = new PlainParsers.SingleQuoted(processor).parse();
          found = true;
        } else if (c == '"') {
          value = new PlainParsers.SingleQuoted(processor).parse();
          found = true;
        } else {
          processor.returnChars(1);
          value = new PlainParsers.Scalar(processor).parse();
          found = value != null;
        }
      }

      // send value if needed
      if (value != null) {
        processor.acceptValue(this, value);
      }

      // read rest of the line
      processor.skipSpaces();
      c = processor.nextChar();
      if (c < 0) {
        return;
      } else if (c == '#') {
        processor.skipLine();
      } else if (c != '\n') {
        reportUnexpected(c);
        processor.skipLine();
      }
    }
  }
  
  
  boolean checkIntendation(int i) {
    return i == intendation || 
        (parent != null && parent.checkIntendation(i));
  }
  
}