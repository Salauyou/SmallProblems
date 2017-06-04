package ru.salauyou.builder.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;


public class TreeHelperTest {

  Multimap<Character, Character> gr;
  Multimap<String, String> tree;
  
  Function<String, String> treeParentExtractor 
    = new Function<String, String>() {
      @Override
      public String apply(String input) {
        for (Map.Entry<String, String> e : tree.entries()) {
          if (input.equals(e.getValue())) {
            return e.getKey();
          }
        }
        return null;
      }
  };
  
  
  @Before
  public void prepare() {
    
    // A--> B--> C
    //  \    \-------\
    //   \    \--> D--> E
    //    \-----/
 
    gr = HashMultimap.create();
    gr.putAll('A', Arrays.asList('B', 'D'));
    gr.putAll('B', Arrays.asList('C', 'D', 'E'));
    gr.put('D', 'E');
    
    // fruit--> common--> apple
    //      \         \--> pear
    //       \         \--> apricot
    //        \--> tropical--> banana
    //                     \--> pineapple
    //                      \--> passionfruit
    // nut--> peanut
    //    \--> hazelnut
    //     \--> cashew
    
    tree = HashMultimap.create();
    tree.putAll("fruit", Arrays.asList("common", "tropical"));
    tree.putAll("common", Arrays.asList("apple", "pear", "apricot"));
    tree.putAll("tropical", Arrays.asList("banana", "pineapple", "passionfruit"));
    tree.putAll("nut", Arrays.asList("peanut", "hazelnut", "cashew"));
  }
  
  
  @Test
  public void testTopologicalSort() {
   
    List<Character> sorted = TreeHelper.topologicalSort(gr);
    assertEquals(5, sorted.size());
    assertTrue(sorted.indexOf('A') < sorted.indexOf('B'));
    assertTrue(sorted.indexOf('A') < sorted.indexOf('C'));
    assertTrue(sorted.indexOf('A') < sorted.indexOf('D'));
    assertTrue(sorted.indexOf('A') < sorted.indexOf('E'));
    assertTrue(sorted.indexOf('B') < sorted.indexOf('E'));
    assertTrue(sorted.indexOf('B') < sorted.indexOf('D'));
    assertTrue(sorted.indexOf('D') < sorted.indexOf('E'));
  }


  @Test(expected = IllegalArgumentException.class)
  public void testTopologicalSortCyclic() {
    Multimap<Character, Character> gr = HashMultimap.create();
    
    // A <--> B    
    //    \--> C
    gr.putAll('A', Arrays.asList('B', 'C'));
    gr.put('B', 'A');
    TreeHelper.topologicalSort(gr);
  }

  
  @Test
  public void testCollectDescendants() {
    Collection<String> items;
    Set<String> result;
    
    items = Arrays.asList("apple", "common", "fruit", "pear");
    result = TreeHelper.collectDescendants(items, treeParentExtractor, "fruit");
    assertEquals(ImmutableSet.of("common", "apple", "pear"), result);
    
    result = TreeHelper.collectDescendants(items, treeParentExtractor, "common");
    assertEquals(ImmutableSet.of("apple", "pear"), result);
    
    result = TreeHelper.collectDescendants(items, treeParentExtractor, "apple");
    assertTrue(result.isEmpty());
    
    result = TreeHelper.collectDescendants(items, treeParentExtractor, "nut");
    assertTrue(result.isEmpty());  // no "nuts" among items
    
  }
  
  
}
