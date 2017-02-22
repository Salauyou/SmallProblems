package ru.salauyou.yamlparser.impl;

import ru.salauyou.yamlparser.ParserException.Reason;

public class BlockedObjectParser extends ObjectParser {
  
  final BlockedObjectParser parent;
  final int intendation;
  
  public BlockedObjectParser(ProcessorImpl processor, 
      BlockedObjectParser parent, int intendation) {
    super(processor);
    this.parent = parent;
    this.intendation = intendation;
  }
  
  
  @Override
  public void go() {
    for (;;) {
      newline: {
      
        // get intendation and decide
        // who will parse current line
        int i = countSpaces(true);
        if (i > intendation) {
          processor.returnChars(i);
          new BlockedObjectParser(processor, this, i).go();
          continue newline;
        } else if (i < intendation) {
          if (checkIntendation(i)) {
            processor.returnChars(i);
            return;
          } else {
            processor.acceptParserError(Reason.WRONG_INTENDATION);
            processor.skipLine();
            continue newline;
          }
        }

        // read key
        String key = null;
        int c;
        do {
          processor.nextChar();
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
        } while (key == null);

        // read separator
        skipSpaces();
        c = processor.nextChar();
        if (c != ':') {
          if (c < 0) {
            processor.acceptParserError(Reason.UNEXPECTED_EOL);
            return;
          } else if (c == '\n') {
            processor.acceptParserError(Reason.UNEXPECTED_EOL);
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
        boolean folderFound = false;
        do {
          skipSpaces();
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
            folderFound = true;
            new FoldedObjectParser(processor).go();
          } else if (c == '\'') {
            value = new PlainParsers.SingleQuoted(processor).parse();
          } else if (c == '"') {
            value = new PlainParsers.SingleQuoted(processor).parse();
          } else {
            processor.returnChars(1);
            value = new PlainParsers.Scalar(processor).parse();
          }
        } while (value == null || !folderFound);

        // send value if needed
        if (value != null) {
          processor.acceptValue(this, value);
        }

        // read rest of the line
        skipSpaces();
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
  }
  
  
  boolean checkIntendation(int i) {
    return i == intendation || 
        (parent != null && parent.checkIntendation(i));
  }
  

}
