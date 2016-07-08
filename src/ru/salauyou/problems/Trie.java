package ru.salauyou.problems;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;


public class Trie<V> {

  V value;
  Map<Character, Trie<V>> next;
 
  
  public V put(String key, V v) {
    Objects.requireNonNull(key);
    Objects.requireNonNull(v);
    V old = find(key, 0);
    add(key, 0, v);
    return old;
  }
 
  
  public V get(String key) {
    return key == null ? null 
        : (key.isEmpty() ? value : find(key, 0));
  } 
  
  
  public boolean containsKey(String key) {
    return key == null ? false : (find(key, 0) != null);
  }
  
  
  void add(String key, int start, V value) {
    if (start == key.length()) {
      this.value = value;
      return;
    }
    if (next == null) {
      next = new HashMap<>();
    }
    Trie<V> t = next.computeIfAbsent(key.charAt(start), k -> new Trie<>());
    t.add(key, start + 1, value);
  }


  V find(String key, int start) {
    if (start == key.length()) {
      return this.value;
    }
    if (next == null) {
      return null;
    }
    Trie<V> t = next.get(key.charAt(start));
    if (t == null) {
      return null;
    } else {
      return t.find(key, start + 1);
    }
  }
  
  
  public Trie<V> trieFor(char c) {
    return next == null ? null : next.get(c);
  }
  
  
  public Trie<V> trieFor(String prefix) {
    Objects.requireNonNull(prefix);
    if (prefix.isEmpty()) {
      return this;
    }
    Trie<V> t = this;
    for (int i = 0; i < prefix.length(); i++) {
      if (t == null || t.next == null) {
        return null;
      } else {
        t = t.next.get(prefix.charAt(i));
      }
    }
    return t;
  }
  
  
  public Iterable<CharEntry<Trie<V>>> tries() {
    return () -> {
      if (next == null) {
        return Collections.emptyIterator();
      }
      return new Iterator<CharEntry<Trie<V>>>() {
        final Iterator<Entry<Character, Trie<V>>> it 
            = next.entrySet().iterator();
        
        @Override
        public boolean hasNext() {
          return it.hasNext();
        }

        @Override
        public CharEntry<Trie<V>> next() {
          Entry<Character, Trie<V>> next = it.next();
          return new CharEntry<>(next.getKey(), next.getValue());
        }        
      };
    };
  }
  
  
  
  
  public static class CharEntry<E> {
    
    final char c;
    final E value;
    
    CharEntry(char c, E value) {
      this.c = c;
      this.value = value;
    }
    
    public char getChar() {
      return c;
    }
    
    public E getValue() {
      return value;
    }
  }

    

  
}
