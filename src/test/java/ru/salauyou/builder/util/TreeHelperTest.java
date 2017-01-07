package ru.salauyou.builder.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;


public class TreeHelperTest {

  @Test
  public void testTopologicalSort() {
    Multimap<Character, Character> gr = HashMultimap.create();
    gr.putAll('A', Arrays.asList('B', 'D'));
    gr.putAll('B', Arrays.asList('C', 'D', 'E'));
    gr.put('D', 'E');

    List<Character> sorted = TreeHelper.topologicalSort(gr);
    assertEquals(5, sorted.size());
    assertTrue(sorted.indexOf('A') < sorted.indexOf('B'));
    assertTrue(sorted.indexOf('A') < sorted.indexOf('C'));
    assertTrue(sorted.indexOf('A') < sorted.indexOf('D'));
    assertTrue(sorted.indexOf('A') < sorted.indexOf('E'));
    assertTrue(sorted.indexOf('B') < sorted.indexOf('E'));
    assertTrue(sorted.indexOf('B') < sorted.indexOf('D'));
    assertTrue(sorted.indexOf('D') < sorted.indexOf('E'));
  }


  @Test(expected = IllegalArgumentException.class)
  public void testTopologicalSortCyclic() {
    Multimap<Character, Character> gr = HashMultimap.create();
    gr.putAll('A', Arrays.asList('B', 'C'));
    gr.put('B', 'A');
    TreeHelper.topologicalSort(gr);
  }

}
