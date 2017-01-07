package ru.salauyou.problems;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

public class TestMergeSort {
  
  @Test
  public void testMerge() {
    Integer[] a = { 1, 2, 3, 5, 0, 4, 6, 7 };
    MergeSort.merge(a, 0, a.length, a.length / 2);
    assertArrayEquals(new Integer[]{ 0, 1, 2, 3, 4, 5, 6, 7 }, a);
    
    a = new Integer[]{ 3, 4, 5, 0, 1, 2 };
    MergeSort.merge(a, 0, a.length, a.length / 2);
    assertArrayEquals(new Integer[]{ 0, 1, 2, 3, 4, 5 }, a);
  }
  
  
  @Test
  public void testSort() {
    Integer[] a = { 3, 1, 0, 5, 2, 4 };
    MergeSort.sort(a);
    assertArrayEquals(new Integer[]{ 0, 1, 2, 3, 4, 5 }, a);
  }
  
  
  @Test
  public void testBottomUpSort() {
    Integer[] a = { 3, 1, 0, 5, 4, 2 };
    MergeSort.bottomUpSort(a);
    assertArrayEquals(new Integer[]{ 0, 1, 2, 3, 4, 5 }, a);
  }
  
  
  @Test
  public void testBottomUpSortLarge() {
    int size = 1000_000;
    Integer[] a = shuffled(size);
    MergeSort.bottomUpSort(a);
    assertArrayEquals(sorted(size), a);
  }
  
  
  @Test
  public void testSortLarge() {
    int size = 1000_000;
    Integer[] a = shuffled(size);
    MergeSort.sort(a);
    assertArrayEquals(sorted(size), a);
  }
  
  
  @Test
  public void testJavaSort() {
    int size = 1000_000;
    Integer[] a = shuffled(size);
    Arrays.sort(a);
    assertArrayEquals(sorted(size), a);
  }
  
  
  static Integer[] sorted(int size) {
    List<Integer> list = IntStream.range(0, size).boxed().collect(Collectors.toList());
    return list.toArray(new Integer[0]);
  }
  
  
  static Integer[] shuffled(int size) {
    List<Integer> list = IntStream.range(0, size).boxed().collect(Collectors.toList());
    Collections.shuffle(list);
    return list.toArray(new Integer[0]);
  }
  
  
}
