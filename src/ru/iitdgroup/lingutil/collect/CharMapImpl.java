package ru.iitdgroup.lingutil.collect;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @author Salauyou
 * @param <V>
 *          value type
 */
class CharMapImpl {

  private CharMapImpl() {}

  static final class MultiCharMap<V> extends CharMap<V> {

    static final int WIDTH = 64;

    @SuppressWarnings("unchecked")
    final Cme<V>[] table = new Cme[WIDTH];
    long existing = 0;
    int size = 0;
    Cme<V>[] cachedEntries;

    // instantiation within package only
    MultiCharMap() {}

    public int size() {
      return size;
    }

    public V get(char c) {
      Cme<V> e = table[bitFor(c)];
      while (e != null && e.c != c)
        e = e.next;
      return e == null ? null : e.v;
    }

    public boolean containsKey(char c) {
      Cme<V> e = table[bitFor(c)];
      while (e != null && e.c != c)
        e = e.next;
      return e != null;
    }

    public CharMap<V> put(char c, V value) {
      checkMutability();
      int p = bitFor(c);
      existing |= 1 << p;
      Cme<V> e = table[p];
      if (e == null) {
        table[p] = new Cme<>(this, c, value);
        cachedEntries = null;
        size++;
        return this;
      }
      while (e.c != c && e.next != null)
        e = e.next;
      if (e.c == c)
        e.v = value;
      else {
        e.next = new Cme<>(this, c, value);
        cachedEntries = null;
        size++;
      }
      return this;
    }

    @Override
    public CharMap<V> remove(char c) {
      checkMutability();
      // TODO: implement
      throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<CharEntry<V>> iterator() {
      if (cachedEntries == null) {
        cachedEntries = new Cme[size];
        int p = 0;
        for (Cme<V> e : table) {
          while (e != null) {
            cachedEntries[p++] = e;
            e = e.next;
          }
        }
      }

      return new Iterator<CharEntry<V>>() {
        int i = 0;

        @Override
        public boolean hasNext() {
          return i < cachedEntries.length;
        }

        @Override
        public CharEntry<V> next() {
          if (i >= cachedEntries.length) {
            throw new NoSuchElementException();
          }
          Cme<V> cme = cachedEntries[i++];
          return new CharEntry<>(cme.c, cme.v);
        }
      };
    }

    
    static int bitFor(char c) {
      return c & WIDTH - 1;
    }

    // -------- char map entry ---------- //

    static class Cme<V>  {
      Cme<V> next = null;
      final char c;
      final CharMap<V> m;
      V v;

      Cme(CharMap<V> m, char c, V value) {
        this.c = c;
        this.v = value;
        this.m = m;
      }
    }
  }

  
  
  // ----------- empty char maps --------- //

  @SuppressWarnings("rawtypes")
  final static CharMap MUTABLE_EMPTY = new CharMap() {

    @Override
    public Object get(char c) {
      return null;
    }

    @Override
    public boolean containsKey(char c) {
      return false;
    }

    @Override
    public int size() {
      return 0;
    }

    @Override
    public CharMap remove(char c) {
      return this;
    }

    @Override
    public CharMap makeImmutable() {
      return IMMUTABLE_EMPTY;
    }

    @Override
    public Iterator iterator() {
      return EMPTY_ITERATOR;
    }

    @Override
    public CharMap put(char c, Object value) {
      Objects.requireNonNull(value);
      return new SingleCharMap<>(c, value);
    }

  };

  
  @SuppressWarnings("rawtypes")
  final static CharMap IMMUTABLE_EMPTY = new CharMap() {

    @Override
    public Object get(char c) {
      return null;
    }

    @Override
    public boolean containsKey(char c) {
      return false;
    }

    @Override
    public int size() {
      return 0;
    }

    @Override
    public Iterator iterator() {
      return EMPTY_ITERATOR;
    }

    @Override
    public CharMap put(char c, Object value) {
      Objects.requireNonNull(value);
      checkMutability(); // this will always throw
      return this;
    }

    @Override
    public CharMap remove(char c) {
      checkMutability(); // this will always throw
      return this;
    }

  }.makeImmutable();

  
  @SuppressWarnings("rawtypes")
  final static Iterator EMPTY_ITERATOR = new Iterator() {
    @Override
    public boolean hasNext() {
      return false;
    }

    @Override
    public Object next() {
      throw new NoSuchElementException();
    }
  };

  
  
  // ------------ single-key char map ----------- //

  final static class SingleCharMap<V> extends CharMap<V> {

    final char c;
    V v;

    // instaitiation within package only
    SingleCharMap(char c, V v) {
      this.c = c;
      this.v = v;
    }

    @Override
    public V get(char c) {
      return this.c == c ? v : null;
    }

    @Override
    public boolean containsKey(char c) {
      return this.c == c;
    }

    @Override
    public int size() {
      return 1;
    }

    @Override
    public CharMap<V> put(char c, V value) {
      Objects.requireNonNull(value);
      checkMutability();
      if (this.c == c) {
        this.v = value;
        return this;
      } else {
        return new MultiCharMap<V>().put(this.c, this.v).put(c, value);
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public CharMap<V> remove(char c) {
      checkMutability();
      return c == this.c ? MUTABLE_EMPTY : this;
    }

    @Override
    public Iterator<CharEntry<V>> iterator() {
      return new Iterator<CharEntry<V>>() {
        boolean used = false;

        @Override
        public boolean hasNext() {
          return !used;
        }

        @Override
        public CharEntry<V> next() {
          if (used) {
            throw new NoSuchElementException();
          }
          used = true;
          return new CharEntry<>(c, v);
        }
      };
    }
  }

}
