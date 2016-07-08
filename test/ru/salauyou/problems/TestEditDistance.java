package ru.salauyou.problems;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Ignore;
import org.junit.Test;

public class TestEditDistance {

  @Test
  @Ignore
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
  
  
  @Test
  @Ignore
  public void testDictionarySearch() {
    List<String> entries = Arrays.asList("one", "once", "two", "three", 
        "onehundred", "twothousand", "pi", "pizza");
    
    Map<String, String> dictMap = new HashMap<>();
    Trie<String> dictTrie = new Trie<String>();
    entries.forEach(s -> { dictMap.put(s, s); dictTrie.put(s, s); });
    
    List<String> matches = EditDistance.findMatches(dictMap, "one", 1);
    Collections.sort(matches);
    assertEquals(Arrays.asList("once", "one"), matches);
        
    matches = EditDistance.findMatches(dictTrie, "one", 1);
    Collections.sort(matches);
    assertEquals(Arrays.asList("once", "one"), matches);
  }
  
  
  @Test
  public void testLargeDictionary() {
    final Trie<String> trieDict = new Trie<>();
    final Map<String, String> mapDict = new HashMap<>();
    final AtomicInteger c = new AtomicInteger();
    DictionaryReader.readWords(w -> { 
        trieDict.put(w, w); 
        mapDict.put(w, w); 
        c.incrementAndGet(); });
    System.out.format("%s words read\n\n", c.get());
    
    final int limit = 2;
    final List<String> words 
        = Arrays.asList("parti", "neede", "frygally", "frutsration");
    for (String needle : words) {
      List<String> matches;
      matches = EditDistance.findMatches(mapDict, "dummy", 2);
      matches = EditDistance.findMatches(trieDict, "dummy", 2); // warm-up
      
      long ts;
      ts = System.nanoTime();
      matches = EditDistance.findMatches(trieDict, needle, limit);
      Collections.sort(matches);
      ts = System.nanoTime() - ts;
      System.out.format("Trie: %6d µs, matches: %s\n", 
          TimeUnit.NANOSECONDS.toMicros(ts), matches);
      
      ts = System.nanoTime();
      matches = EditDistance.findMatches(mapDict, needle, limit);
      Collections.sort(matches);
      ts = System.nanoTime() - ts;
      System.out.format("Map:  %6d µs, matches: %s\n\n", 
          TimeUnit.NANOSECONDS.toMicros(ts), matches);
    }
  }
  
  
}
