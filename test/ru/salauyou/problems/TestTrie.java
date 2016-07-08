package ru.salauyou.problems;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class TestTrie {

  @Test
  public void testTrieCreate() {
    Trie<Integer> t = new Trie<>();
    assertNotNull(t);
  }
  
  
  Trie<Integer> t;
  
  
  @Before
  public void initTrie() {
    t = new Trie<>();
    t.put("ONE", 1);
    t.put("TWO", 2);
    t.put("THREE", 3);
    t.put("ONEHUNDRED", 100);
    t.put("TWOTHOUSAND", 2000);
  }
  
  
  @Test
  public void testPutGet() {
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
    
    Trie<Integer> sub = t.trieFor("ON");
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
  
  
  
}
