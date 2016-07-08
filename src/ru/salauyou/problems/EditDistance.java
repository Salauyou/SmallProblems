package ru.salauyou.problems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EditDistance {

  public static <T> List<T> findMatches(Trie<T> dictionary, 
          String pattern, int maxDistance) {
    return Collections.emptyList();
  }
  
  
  public static <T> List<T> findMatches(Map<String, T> dictionary,
          String pattern, int maxDistance) {
    final List<T> matches = new ArrayList<>();
    for (Entry<String, T> e : dictionary.entrySet()) {
      String text = e.getKey();
      if (editDistance(pattern, text, maxDistance) >= 0) {
        matches.add(e.getValue());
      }
    }
    return matches;
  }
  
  
  static int editDistance(String s1, String s2, int limit) {
    int len = s1.length();
    int[] r = new int[len + 1];
    int[] t = new int[len + 1];
    for (int i = 0; i <= len; i++) {
      r[i] = i;
    }
    for (int i = 0; i < s2.length(); i++) {
      char ci = s2.charAt(i);
      t[0] = i + 1;
      int min = t[0];
      for (int j = 0; j < len; j++) {
        char cj = s1.charAt(j);
        int d = min(r[j] + signal(ci != cj), r[j+1] + 1, t[j] + 1);
        if (d < min) {
          min = d;
        }
        t[j + 1] = d;
      }
      if (min > limit) {
        return -1;
      }
      int[] temp = r;
      r = t;
      t = temp;
    }
    return r[len] > limit ? -1 : r[len];
  }
  
  
  
  static int min(int a, int b, int c) {
    return Math.min(Math.min(a, b), c);
  }
  
  static int signal(boolean b) {
    return b ? 1 : 0;
  }
  
  
}
