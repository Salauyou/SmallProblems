package ru.salauyou.problems;

import java.util.Arrays;

public class Permutations {

  
  public static <E> void applyPermutation(E[] source, int[] permutation) {
    E buf = null;
    int len = source.length;
    int[] p = Arrays.copyOf(permutation, len); 
    for(;;) {
      int i = 0;
      for(; i < len && p[i] < 0; i++);  // next cycle
      if (i >= len)
        return;
      buf = source[i];
      while (p[i] >= 0) {
        E b = source[p[i]];
        source[p[i]] = buf;
        buf = b;
        int pred = i;
        i = p[i];
        p[pred] = -1;
      }
    }
  }
  
  
}
