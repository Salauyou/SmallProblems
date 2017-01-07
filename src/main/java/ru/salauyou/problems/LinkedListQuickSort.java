package ru.salauyou.problems;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LinkedListQuickSort {

  
  public static class LList<E extends Comparable<? super E>> 
            implements Iterable<E> {
    
    final Node<E> head = new Node<>(null);
    Node<E> tail = null;
    
    public void add(E value) {
      final Node<E> n = new Node<>(value);
      updateTail();
      n.attach(tail);
      tail = n;
    }
    
    void updateTail() {
      if (tail == null) {
        tail = head;
      }
      while (tail.next != null) {
        tail = tail.next;
      }
    }
   
    public void quickSort() {
      quickSortPart(this.head, null);
    }
    
    
    /**
     * Sort sublist between nodes `from` and `to`, 
     * exclusively
     */
    void quickSortPart(Node<E> from, Node<E> to) {
      // empty sublist
      if (from.next == to) {
        return;
      }
      // 1 node
      if (from.next != null && from.next.next == to) {
        return;
      }
      // 2 and more
      final Node<E> p = from.next;  // pivot node
      final E v = p.value;          // pivot value
      Node<E> n = p.next;           // current node
      Node<E> pred = p;             // pred node
      while (n != to) {
        if (n.value.compareTo(v) < 0) {
          n.detach(pred);
          n.attach(from);
          n = pred;
        }
        pred = n;
        n = n.next;
      }
      quickSortPart(from, p);
      quickSortPart(p, to);
    }
    
    
    
    @SafeVarargs
    public static <T extends Comparable<? super T>> 
            LList<T> of(T... values) {
      final LList<T> list = new LList<>();
      for (T v : values) {
        list.add(v);
      }
      return list;
    }
    
    
    @Override
    public String toString() {
      final Iterator<E> it = iterator();
      if (!it.hasNext()) {
        return "[]";
      }
      final StringBuilder sb = new StringBuilder("[");
      for (;;) {
        sb.append(it.next());
        if (!it.hasNext()) {
          return sb.append(']').toString();
        } else {
          sb.append(',').append(' ');
        }
      }
    }

    
    public List<E> toList() {
      return StreamSupport.stream(spliterator(), false)
          .collect(Collectors.toList());
    }
    
    
    @Override
    public Iterator<E> iterator() {
      return new Iterator<E>() {
        Node<E> cur = head;
        Node<E> pred = null;
        
        @Override
        public boolean hasNext() {
          return cur.next != null;
        }

        @Override
        public E next() {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          E v = cur.next.value;
          pred = cur;
          cur = cur.next;
          return v;
        }
        
        @Override
        public void remove() {
          cur.detach(pred);
        }
      };
    }
  }
  
  
  static class Node<E> {

    Node<E> next;
    final E value;
    
    Node(E value) {
      this.value = value;
    }  
    
    void attach(Node<E> to) {
      this.next = to.next;
      to.next = this;
    }
    
    void detach(Node<E> from) {
      from.next = this.next;
      this.next = null;
    }
    
    @Override
    public String toString() {
      return String.format("[%s]%s", 
          this.value, 
          next == null ? "" : (" → " + next.value));
    }
  }
  
}
