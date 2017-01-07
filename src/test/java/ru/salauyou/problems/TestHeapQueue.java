package ru.salauyou.problems;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

public class TestHeapQueue {

  
  @Test
  public void testOffer() {
    Queue<Integer> q = new HeapQueue<>();
    for (Integer i : Arrays.asList(3, 1, 0, 4, 5, 2)) {
      q.offer(i);
    }
    assertEquals(6, q.size());
  }
  
  
  @Test
  public void testPoll() {
    Queue<Integer> q = new HeapQueue<>();
    for (Integer i : Arrays.asList(9, 0, 1, 3, 4, 5, 2, 7, 8, 6)) {
      q.offer(i);
    }
    Integer i;
    List<Integer> list = new ArrayList<>();
    while ((i = q.poll()) != null) {
      list.add(i);
    }
    assertEquals(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), list);
  }
  
  

  @Test
  public void testQueueSort() {
    List<Integer> sorted 
        = IntStream.range(0, 1000_000).boxed().collect(Collectors.toList());
    List<Integer> shuffled = new ArrayList<>(sorted);
    Collections.shuffle(shuffled);
    Queue<Integer> q = new HeapQueue<>();
    q.addAll(shuffled);
    List<Integer> res = new ArrayList<>();
    Integer i;
    while ((i = q.poll()) != null) {
      res.add(i);
    }
    assertEquals(sorted, res);
  }

}
