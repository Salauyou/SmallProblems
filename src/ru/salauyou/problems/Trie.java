package ru.salauyou.problems;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;


public class Trie<V> {

  final Node<V> root;
  
  Trie() {
    this(new Node<>());
  }
  
  Trie(Node<V> root) {
    this.root = root;
  }
  
  
  public V put(String key, V v) {
    Objects.requireNonNull(key);
    Objects.requireNonNull(v);
    V old = root.getValue(key, 0);
    root.attachKey(key, 0, v);
    return old;
  }
 
  public V get(String key) {
    return key == null ? null : root.getValue(key, 0);
  } 
  
  public boolean containsKey(String key) {
    return key == null ? false 
        : (root.getValue(key, 0) != null);
  }
  
  
  public Trie<V> trieFor(char c) {
    Node<V> n = root.next == null ? null : root.next.get(c);
    return n == null ? null : new Trie<>(n);
  }
  
  
  public Trie<V> trieFor(String prefix) {
    Objects.requireNonNull(prefix);
    if (prefix.isEmpty()) {
      return this;
    }
    Node<V> n = root;
    for (int i = 0; i < prefix.length(); i++) {
      if (n == null || n.next == null) {
        return null;
      } else {
        n = n.next.get(prefix.charAt(i));
      }
    }
    return n == null ? null : new Trie<>(n);
  }
  
  
  
  public Iterable<CharEntry<Trie<V>>> tries() {
    return () -> {
      if (root.next == null) {
        return Collections.emptyIterator();
      }
      return new Iterator<CharEntry<Trie<V>>>() {
        final Iterator<Entry<Character, Node<V>>> it 
            = root.next.entrySet().iterator();
        
        @Override
        public boolean hasNext() {
          return it.hasNext();
        }

        @Override
        public CharEntry<Trie<V>> next() {
          Entry<Character, Node<V>> next = it.next();
          return new CharEntry<>(next.getKey(), 
              new Trie<V>(next.getValue()));
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
  
  
  
  static class Node<V> {
    
    V value;
    Map<Character, Node<V>> next;
    
    void attachKey(String key, int start, V value) {
      if (start == key.length()) {
        this.value = value;
        return;
      }
      if (next == null) {
        next = new HashMap<>();
      }
      Node<V> n = next.computeIfAbsent(key.charAt(start), k -> new Node<>());
      n.attachKey(key, start + 1, value);
    }
    
    
    V getValue(String key, int start) {
      if (start == key.length()) {
        return this.value;
      }
      if (next == null) {
        return null;
      }
      Node<V> n = next.get(key.charAt(start));
      if (n == null) {
        return null;
      } else {
        return n.getValue(key, start + 1);
      }
    }
    
  }
  
}
