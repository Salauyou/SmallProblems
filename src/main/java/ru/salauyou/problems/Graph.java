package ru.salauyou.problems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Simple implementation of undirected graph, 
 * represented by its edges
 */
public class Graph<N> {
  
  final Map<N, Set<N>> edges = new HashMap<>();
  int edgeCount = 0;
  
  
  public Set<N> nodes() {
    return Collections.unmodifiableSet(edges.keySet());
  }
  
  /**
   * Returns all nodes adjusted to a given node
   */
  public Set<N> adjacentTo(N node) {
    Set<N> adj = edges.get(node);
    return adj == null ? Collections.emptySet() 
        : new HashSet<>(edges.get(node));
  }
  
  public boolean adjacent(N a, N b) {
    Set<N> adj = edges.get(a);
    return adj != null && adj.contains(b);
  }
  
  public int edges() {
    return this.edgeCount;
  }
  
  
  public boolean addEdge(N a, N b) {
    if (adjacent(a, b)) {
      return false;
    }
    edges.computeIfAbsent(a, k -> new HashSet<>()).add(b);
    edges.computeIfAbsent(b, k -> new HashSet<>()).add(a);
    edgeCount++;
    return true;
  }
  
  
  public List<N> bfsPath(N from, N to) {
    Objects.requireNonNull(from);
    Objects.requireNonNull(to);
    if (!edges.containsKey(from) || !edges.containsKey(to)) {
      return null;
    }
    if (Objects.equals(from, to)) {
      return Collections.singletonList(from);
    }
    final Queue<N> q = new LinkedList<>();
    final Map<N, N> visited = new HashMap<>();
    q.offer(to);
    visited.put(to, to);
    N a;
    while ((a = q.poll()) != null) {
      for (N b : edges.get(a)) {
        if (!visited.containsKey(b)) {
          visited.put(b, a);
          q.offer(b);
        }
        if (Objects.equals(b, from)) {
          List<N> path = new ArrayList<>();
          path.add(from);
          while (!Objects.equals(b, to)) {
            a = visited.get(b);
            path.add(b = a);
          }
          return path;
        }
      }
    }
    return null;
  }
  
  
  public Graph<N> bfsSpanningTree(N root) {
    Objects.requireNonNull(root);
    if (!edges.containsKey(root)) {
      throw new IllegalArgumentException();
    }
    final Graph<N> st = new Graph<>();
    final Queue<N> q = new LinkedList<>();
    final Set<N> visited = new HashSet<>();
    q.offer(root);
    N a;
    while ((a = q.poll()) != null) {
      for (N b : edges.get(a)) {
        if (!visited.contains(b)) {
          st.addEdge(a, b);
          visited.add(b);
          q.offer(b);
        }
      }
    }
    return st;
  }
  
  
  public Graph<N> bfsSpanningTree() {
    if (edges.isEmpty()) {
      return new Graph<>();
    }
    N any = edges.keySet().iterator().next();
    return bfsSpanningTree(any);
  }
 
  
  @Override
  public String toString() {
    if (edges.isEmpty()) {
      return "";
    }
    boolean comparableKeys = true;
    final List<N> nodes = new ArrayList<>();
    for (N node : edges.keySet()) {
      comparableKeys &= node instanceof Comparable;
      nodes.add(node);
    }
    @SuppressWarnings("unchecked")
    final Comparator<N> cmp = comparableKeys 
        ? (a, b) -> ((Comparable<N>) a).compareTo(b)
        : (a, b) -> 0;
    if (comparableKeys) {
      Collections.sort(nodes, cmp);
    }
    final StringBuilder sb = new StringBuilder();
    for (N node : nodes) {
      sb.append(node).append(" — ");
      List<N> ns = edges.get(node).stream()
           .sorted(cmp)
           .collect(Collectors.toList());
      sb.append(ns).append('\n');
    }
    return sb.toString();
  }
  
} 
