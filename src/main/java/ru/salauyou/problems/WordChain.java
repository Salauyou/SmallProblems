package ru.salauyou.problems;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordChain {


  static final BiFunction<String, Integer, String> ROTATE_STRING 
      = (s, r) -> s.substring(s.length() - r, s.length()) + s.substring(0, s.length() - r);

  static final UnaryOperator<String> CUT_LAST_CHAR
      = s -> s.substring(0, s.length() - 1);
  
  
      
  static Graph<String> readWords(Path path, int size) {
    final Graph<String> g = new Graph<>();
    List<String> words = null;
    try (Stream<String> lines 
          = Files.newBufferedReader(path, StandardCharsets.UTF_8).lines()) {
      words = lines.distinct()
          .filter(w -> w.length() == size)
          .collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
      return g;
    }
    for (int rot = 0; rot < size; rot++) {
      int r = rot;
      Collections.sort(words, Comparator.comparing(w -> ROTATE_STRING.apply(w, r)));
      int j = 0;
      String w1 = words.get(j);
      String rw1 = ROTATE_STRING.andThen(CUT_LAST_CHAR).apply(w1, rot);
      for (int i = 1; i < words.size(); i++) {
        String w2 = words.get(i);
        String rw2 = ROTATE_STRING.andThen(CUT_LAST_CHAR).apply(w2, rot);
        if (rw1.equals(rw2)) {
          for (int k = j; k < i; k++) {
            g.addEdge(words.get(k), w2);
          }
        } else {
          j = i;
          w1 = w2;
          rw1 = rw2;
        }
      }
    }
    if (g.nodes().size() > words.size()) {
      throw new AssertionError();
    } 
    return g;
  }  
}
