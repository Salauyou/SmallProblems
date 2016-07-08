package ru.salauyou.problems;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.Test;

import ru.salauyou.problems.LinkedListQuickSort.LList;

public class TestLinkedListQuickSort {

  @Test
  public void testLList() {
    LList<Integer> list = LList.of(1, 2, 3, 4, 5);
    assertEquals(Arrays.asList(1, 2, 3, 4, 5), list.toList());
  }
  
  
  @Test
  public void testQuickSort() {
    LList<Integer> list = LList.of(2, 3, 1, 5, 6, 9, 0, 7, 8, 4);
    list.quickSort();
    assertEquals(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), list.toList());
    
    Random rnd = new Random();
    IntStream.generate(() -> rnd.nextInt(1000))
        .limit(1000)
        .forEach(list::add);
    list.quickSort();
    int pred = -1;
    for (int cur : list) {
      assertTrue(cur >= pred);
      pred = cur;
    }
  }
  
  
}
