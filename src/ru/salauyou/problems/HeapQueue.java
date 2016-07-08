package ru.salauyou.problems;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HeapQueue<E extends Comparable<? super E>> extends AbstractQueue<E> {

  final List<E> heap = new ArrayList<>();

  @Override
  public boolean offer(E e) {
    heap.add(e);
    int p = heap.size() - 1;
    E upper;
    for (int i = (p - 1) / 2; i >= 0; i = (i - 1) / 2) {
      if ((upper = heap.get(i)).compareTo(e) <= 0) {
        break;
      }
      heap.set(p, upper);
      heap.set(p = i, e);
    }
    return true;
  }

  
  @Override
  public E poll() {
    int s = heap.size() - 1;
    if (s < 0) {
      return null;
    } else if (s == 0) {
      return heap.remove(0);
    }
    E res = heap.get(0);
    E upper = heap.remove(s);
    heap.set(0, upper);
    int p = 0;
    E e1, e2;
    for (int i = 1; i < s; i = i * 2 + 1) {
      e1 = heap.get(i);
      if (i < (s - 1) && (e2 = heap.get(i + 1)).compareTo(e1) < 0) {
        e1 = e2;
        i++;
      }
      if (upper.compareTo(e1) <= 0) {
        break;
      }
      heap.set(p, e1);
      heap.set(p = i, e1 = upper);
    }
    return res;
  }

  
  @Override
  public E peek() {
    return heap.isEmpty() ? null : heap.get(0);
  }

  
  @Override
  public Iterator<E> iterator() {
    return new Iterator<E>() {
      final Iterator<E> it = heap.iterator();

      @Override
      public boolean hasNext() {
        return it.hasNext();
      }

      @Override
      public E next() {
        return it.next();
      }
      
      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  
  @Override
  public int size() {
    return heap.size();
  }

}
