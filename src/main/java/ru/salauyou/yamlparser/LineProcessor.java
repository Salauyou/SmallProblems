package ru.salauyou.yamlparser;

import java.util.Deque;
import java.util.Queue;

import com.google.common.collect.Queues;

import ru.salauyou.yamlparser.parsers.KeyValueParser;

public class LineProcessor implements Processor {

  Deque<ItemParser> parsers = Queues.newArrayDeque();
  Deque<ItemParser> resultAcceptors = Queues.newArrayDeque();
  Queue<Character> returnedChars = Queues.newArrayDeque();
  
  Object result;
  Character current;
  
  
  public Object parseString(String input) {
    if (!input.endsWith("\n")) {
      input += ItemParser.BR;
    }
    parsers.add(new KeyValueParser(false));
    for (int i = 0; i < input.length(); ++i) {
      processChar(current = input.charAt(i));
      while ((current = returnedChars.poll()) != null) {
        processChar(current);
      }
    }
    if (!parsers.isEmpty()) {
      throw new IllegalStateException(
          "Unexpected end of line");
    }
    return result;
  }

  
  void processChar(char c) {
    ItemParser parser = parsers.peekLast();
    if (parser != null) {
      ItemParser res = parser.acceptChar(this, c);
      if (res == null) {
        parsers.pollLast();
        resultAcceptors.pollLast();
      } else if (res != parser) {
        parsers.add(res);
        resultAcceptors.add(parser);
      }
    } else if (current != ItemParser.BR) {
      ItemParser.throwUnexpected(c);
    }
  }
  

  @Override
  public void returnChars(CharSequence chars) {
    for (int i = 0; i < chars.length(); ++i) {
      returnedChars.offer(chars.charAt(i));
    }
  }
  
  
  @Override
  public void returnChar() {
    returnedChars.offer(current);
  }


  @Override
  public void acceptResult(Object result) {
    ItemParser acceptor = resultAcceptors.peekLast();
    if (acceptor != null) {
      acceptor.acceptResult(result);
    } else {
      this.result = result;
    }
  }

  
}
