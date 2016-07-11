package ru.iitdgroup.lingutil.collect;

import java.util.Collections;
import java.util.Objects;


public class SimpleTrie<V> {

  V value;
  CharMap<SimpleTrie<V>> next;
 
  
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
    SimpleTrie<V> t;
    char c = key.charAt(start);
    if (next == null) {
      t = new SimpleTrie<>();
      next = new CharMapImpl.SingleCharMap<>(c, t);
    } else {
      t = next.get(c);
      if (t == null) {
        t = new SimpleTrie<>();
        next = next.put(c, t);
      }
    }
    t.add(key, start + 1, value);
  }


  V find(String key, int start) {
    if (start == key.length()) {
      return this.value;
    }
    if (next == null) {
      return null;
    }
    SimpleTrie<V> t = next.get(key.charAt(start));
    if (t == null) {
      return null;
    } else {
      return t.find(key, start + 1);
    }
  }
  
  
  public SimpleTrie<V> trieFor(char c) {
    return next == null ? null : next.get(c);
  }
  
  
  public SimpleTrie<V> trieFor(String prefix) {
    Objects.requireNonNull(prefix);
    if (prefix.isEmpty()) {
      return this;
    }
    SimpleTrie<V> t = this;
    for (int i = 0; i < prefix.length(); i++) {
      if (t == null || t.next == null) {
        return null;
      } else {
        t = t.next.get(prefix.charAt(i));
      }
    }
    return t;
  }
  
  
  public Iterable<CharEntry<SimpleTrie<V>>> tries() {
    return next == null ? Collections.emptyList() : next;
  }
}
