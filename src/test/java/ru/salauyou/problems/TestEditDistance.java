package ru.salauyou.problems;

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import ru.iitdgroup.lingutil.collect.SimpleTrie;
import ru.iitdgroup.lingutil.search.EditDistance;

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
  
  
  @Test
  public void testDictionarySearch() {
    List<String> entries = Arrays.asList("one", "once", "two", "three", 
        "onehundred", "twothousand", "pi", "pizza");
    
    Map<String, String> dictMap = new HashMap<>();
    SimpleTrie<String> dictTrie = new SimpleTrie<String>();
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
    final SimpleTrie<String> trieDict = new SimpleTrie<>();
    final Map<String, String> mapDict = new HashMap<>();
    final AtomicInteger c = new AtomicInteger();
    DictionaryReader.readWords(Paths.get("words355.txt"), w -> { 
        trieDict.put(w, w); 
        mapDict.put(w, w); 
        c.incrementAndGet(); });
    System.out.format("%s words read\n\n", c.get());
    
    final int limit = 2;
    final List<String> words 
        = Arrays.asList("dummy", "parti", "neede", "frygally", "frutsration");
    
    final int repeats = 5;
    for (int i = 0; i < repeats; i++) {
      for (String needle : words) {
        List<String> matches;
        matches = EditDistance.findMatches(mapDict, "dummy" + i, 2);
        matches = EditDistance.findMatches(trieDict, "dummy" + i, 2); // warm-up

        long ts;
        System.gc();
        ts = System.nanoTime();
        matches = EditDistance.findMatches(trieDict, needle, limit);
        Collections.sort(matches);
        ts = System.nanoTime() - ts;
        if (i == repeats - 1) {
          System.out.format("Trie: %6d µs, matches: %s\n", 
              TimeUnit.NANOSECONDS.toMicros(ts), matches);
        }
        
        System.gc();
        ts = System.nanoTime();
        matches = EditDistance.findMatches(mapDict, needle, limit);
        Collections.sort(matches);
        ts = System.nanoTime() - ts;
        if (i == repeats - 1) {
          System.out.format("Map:  %6d µs, matches: %s\n\n", 
              TimeUnit.NANOSECONDS.toMicros(ts), matches);
        }
      }
    }
  }
  
  
}
