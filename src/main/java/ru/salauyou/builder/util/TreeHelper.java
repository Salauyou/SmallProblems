package ru.salauyou.builder.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;


public final class TreeHelper {

  /**
   * Returns a list of topologycally sorted vertices 
   * of a given graph. Graph is presented as multimap 
   * where each entry represents an edge directed 
   * from key to value
   * 
   * @throws IllegalArgumentException
   *    if graph is not a DAG
   */
  public static <E> List<E> topologicalSort(
      Multimap<E, E> graph) {

    // initialize
    // edges directed in reverse (i. e. value -> key)
    Multimap<E, E> m = HashMultimap.create();
    // d(e) - current outcoming degree of vertex e
    Map<E, Integer> d = Maps.newHashMap();

    for (E e : graph.keySet()) {
      Collection<? extends E> vs = graph.get(e);
      d.put(e, vs.size());
      for (E v : vs) {
        if (!d.containsKey(v)) {
          d.put(v, 0);
        }
        m.put(v, e);
      }
    }
    // vertices with d = 0
    Queue<E> c = Lists.newLinkedList();
    for (Map.Entry<E, Integer> e : d.entrySet()) {
      if (e.getValue() == 0) {
        c.add(e.getKey());
      }
    }

    // process:
    // take a vertex with d = 0, add it into 
    // list and decrease d of vertices connected
    // through incoming egdes (like we remove 
    // incoming edges); repeat while such 
    // vertices exist
    List<E> r = Lists.newArrayList();
    E e;
    while ((e = c.poll()) != null) {
      d.remove(e);
      r.add(e);
      for (E v : m.get(e)) {
        int deg = d.get(v);
        if (deg == 1) {
          c.add(v);
          d.remove(v);
        } else {
          d.put(v, deg - 1);
        }
      }
    }
    // check if all vertices encountered
    if (!d.isEmpty()) {
      throw new IllegalArgumentException(
          "Given graph is not a DAG");
    } else {
      Collections.reverse(r);
      return r;
    }
  }


  private TreeHelper() {}
  
}
