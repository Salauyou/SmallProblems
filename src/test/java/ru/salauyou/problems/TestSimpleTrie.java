package ru.salauyou.problems;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.Before;
import org.junit.Test;

import ru.iitdgroup.lingutil.collect.CharEntry;
import ru.iitdgroup.lingutil.collect.SimpleTrie;


public class TestSimpleTrie {

  @Test
  public void testTrieCreate() {
    SimpleTrie<Integer> t = new SimpleTrie<>();
    assertNotNull(t);
  }
  
  
  SimpleTrie<Integer> t;
  
  
  @Before
  public void initTrie() {
    t = new SimpleTrie<>();
    t.put("", 0);
    t.put("ONE", 1);
    t.put("TWO", 2);
    t.put("THREE", 3);
    t.put("ONEHUNDRED", 100);
    t.put("TWOTHOUSAND", 2000);
  }
  
  
  @Test
  public void testPutGet() {
    assertEquals((Integer) 0, t.get(""));
    assertEquals((Integer) 1, t.get("ONE"));
    assertEquals((Integer) 2, t.get("TWO"));
    assertEquals((Integer) 3, t.get("THREE"));
    assertEquals((Integer) 100, t.get("ONEHUNDRED"));
    assertEquals((Integer) 2000, t.get("TWOTHOUSAND"));
    
    assertNull(t.get("O"));
    assertNull(t.get("ON"));
    assertNull(t.get("ONETWO"));
    assertNull(t.get("ONEHUNDREDS"));
  }
  
  
  @Test
  public void testTrieFor() {
    assertNotNull(t.trieFor('O'));
    assertNotNull(t.trieFor("ON"));
    assertNotNull(t.trieFor("ONE"));
    assertNotNull(t.trieFor("ONEH"));
    assertNotNull(t.trieFor("ONEHUNDRED"));
    
    assertNull(t.trieFor("ONEHUNDREDS"));
    assertNull(t.trieFor("ONETWO"));
    
    SimpleTrie<Integer> sub = t.trieFor("ON");
    assertEquals((Integer) 1, sub.get("E"));
    assertEquals((Integer) 100, sub.get("EHUNDRED"));
    assertNull(sub.get("ONE"));
    assertNull(sub.get("ENHUND"));
    assertNull(sub.get("EHUNDREDS"));
    
    sub = t.trieFor("ONE");
    assertEquals((Integer) 1, sub.get(""));
    
    sub = t.trieFor("ONEHUNDRE");
    assertNull(sub.get(""));
    assertEquals((Integer) 100, sub.get("D"));
    sub = sub.trieFor("D");
    assertEquals((Integer) 100, sub.get(""));
    assertNull(sub.get("S"));
  }
  
  
  @Test
  public void testTries() {
    Iterable<CharEntry<SimpleTrie<Integer>>> it = t.tries();
    assertEquals(Arrays.asList('O', 'T'), mapAndCollect(it, CharEntry::getChar));
    for (CharEntry<SimpleTrie<Integer>> e : t.tries()) {
      switch (e.getChar()) {
      case 'O' : 
        assertEquals((Integer) 1, e.getValue().get("NE"));
        assertEquals((Integer) 100, e.getValue().get("NEHUNDRED"));
        assertEquals(Arrays.asList('N'), 
            mapAndCollect(e.getValue().tries(), CharEntry::getChar));
        break;
      case 'T':
        assertEquals((Integer) 2, e.getValue().get("WO"));
        assertEquals((Integer) 3, e.getValue().get("HREE"));
        assertEquals((Integer) 2000, e.getValue().get("WOTHOUSAND"));
        assertEquals(Arrays.asList('H', 'W'), 
            mapAndCollect(e.getValue().tries(), CharEntry::getChar));
        break;
      default:
        break;
      }
    }
  }
  
  
  static <E, R> List<R> mapAndCollect(Iterable<E> it, Function<E, R> mapper) {
    return StreamSupport.stream(it.spliterator(), false)
        .map(mapper)
        .sorted()
        .collect(Collectors.toList());
  }
  
  
}
