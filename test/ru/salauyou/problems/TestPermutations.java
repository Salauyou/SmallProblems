package ru.salauyou.problems;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class TestPermutations {

  @Test
  public void testApplyPermutation() {
    Object[] set; 
    int[] perm; 
    
    // (5 2 1)(4 3)
    set = new Character[]{ 'A', 'B', 'C', 'D', 'E' };
    perm = new int[]{ 4, 0, 3, 2, 1 };
    Permutations.applyPermutation(set, perm);
    assertArrayEquals(new Character[]{ 'B', 'E', 'D', 'C', 'A' }, set);
  
    // (1)
    set = new Character[]{ 'A', 'B', 'C' };
    perm = new int[]{ 0, 1, 2 };
    Permutations.applyPermutation(set, perm);
    assertArrayEquals(new Character[]{ 'A', 'B', 'C' }, set);
  
    // (5 4 3 2 1)
    set = new Character[]{ 'A', 'B', 'C', 'D', 'E' };
    perm = new int[]{ 4, 3, 2, 1, 0 };
    Permutations.applyPermutation(set, perm);
    assertArrayEquals(new Character[]{ 'E', 'D', 'C', 'B', 'A' }, set);
    
    // (2 3)(4 1)(6 5)
    set = new Character[]{ 'A', 'B', 'C', 'D', 'E', 'F' };
    perm = new int[]{ 3, 2, 1, 0, 5, 4 };
    Permutations.applyPermutation(set, perm);
    assertArrayEquals(new Character[]{ 'D', 'C', 'B', 'A', 'F', 'E' }, set);
  }
  
  
}
