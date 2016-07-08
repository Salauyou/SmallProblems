package ru.salauyou.problems;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestEditDistance {

  @Test
  public void testEditDistance() {
    assertEquals(0, EditDistance.editDistance("ABC", "ABC", 5));
    assertEquals(0, EditDistance.editDistance("", "", 5));
    assertEquals(1, EditDistance.editDistance("", "A", 2));
    assertEquals(1, EditDistance.editDistance("", "a", 2));
    
    assertEquals(3, EditDistance.editDistance("abc", "def", 3));
    assertEquals(-1, EditDistance.editDistance("abc", "def", 2));
    
    assertEquals(2, EditDistance.editDistance("PIZZA", "PITSA", 2));
    assertEquals(1, EditDistance.editDistance("PIZZA", "PIZA", 2));
    assertEquals(1, EditDistance.editDistance("PIZZA", "PEZZA", 2));
  }
  
  
}
