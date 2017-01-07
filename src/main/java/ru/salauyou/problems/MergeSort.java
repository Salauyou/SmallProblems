package ru.salauyou.problems;

import java.util.LinkedList;
import java.util.Queue;

public class MergeSort {

  public static <E extends Comparable<? super E>> void sort(E[] input) {
    sort(input, 0, input.length);
  }
  
  
  public static <E extends Comparable<? super E>> 
          void bottomUpSort(E[] input) {
    int len = input.length;
    int bits = 32 - Integer.numberOfLeadingZeros(len);
    for (int b = 0; b < bits; b++) {
      for (int p = 0;; p += 2) {
        int start = p << b;
        if (start >= len) {
          break;
        }
        merge(input, start, 
            Math.min(len, (p + 2) << b), 
            Math.min(len, (p + 1) << b));
      }
    }
  }
  
  
  
  
  static <E extends Comparable<? super E>> 
          void sort(E[] input, int start, int end) {
    if (end - start <= 2) {
      if (end - start == 2) {
        E v1 = input[start];
        E v2 = input[start + 1];
        if (v1.compareTo(v2) > 0) {
          input[start] = v2;
          input[start + 1] = v1;
        }
      }
      return;
    }
    int mid = (end + start) / 2;
    sort(input, start, mid);
    sort(input, mid, end);
    if (input[mid].compareTo(input[mid + 1]) < 0) {
      merge(input, start, end, mid);
    }
  }
  
  
  
  
  
  static <E extends Comparable<? super E>> 
          void merge(E[] input, int start, int end, int mid) {
    if (end - start == 2) {
      E v1 = input[start];
      E v2 = input[start + 1];
      if (v1.compareTo(v2) > 0) {
        input[start] = v2;
        input[start + 1] = v1;
      }
      return;
    }
    final Queue<E> temp = new LinkedList<>();
    int p1 = start;
    int p2 = mid;
    while (p1 < mid && p2 < end) {
      if (input[p1].compareTo(input[p2]) <= 0) {
        temp.add(input[p1]);
        p1++;
      } else {
        temp.add(input[p2]);
        p2++;
      }
    }
    if (p2 == end) {
      for (; p1 < mid; p1++) {
        temp.add(input[p1]);
      }
    }
    E e;
    for (int i = start; (e = temp.poll()) != null; i++) {
      input[i] = e;
    }
  }
  
  
  
}
