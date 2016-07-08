package ru.salauyou.problems;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TestWordChain {

  static final Path WORDS_FILE = Paths.get("words6.txt");
  
  Graph<String> gr;
  
  
  @Before
  public void buildWordGraph() {
    gr = WordChain.readWords(WORDS_FILE, 6);
  }
  
  @Ignore
  @Test
  public void testBuildGraph() {
    System.out.println(gr);
  }
  
  
  @Test
  public void testFindPath() {
    assertNotNull(gr.bfsPath("defect", "reveal"));
  }
  
  
  @Ignore
  @Test
  public void testFindBfsPath() {
    Random rnd = new Random();
    Set<String> es = gr.nodes();
    for (String e1 : es) {
      for (String e2 : es) {
        if (Objects.equals(e1, e2)) {
          continue;
        }
        List<String> path = gr.bfsPath(e1, e2);
        if (path != null && rnd.nextFloat() < 0.01) {
          System.out.println(gr.bfsPath(e1, e2));
        }
      }
    }
  }
  
  
  @Test
  public void testBstSpanningTree() {
    Graph<String> st = gr.bfsSpanningTree("deject"); 
    List<String> p1 = gr.bfsPath("defect", "reveal");
    List<String> p2 = st.bfsPath("defect", "reveal");
    assertNotNull(p1);
    assertNotNull(p2);
    System.out.println(p1);
    System.out.println(p2);
    assertTrue(st.edges() <= gr.edges());
  }
  
}
