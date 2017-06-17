package ru.salauyou.builder.util;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;


public class LineTokenzrTest {

  @Test
  public void testPlain() throws ParseException {
    Pair<String, String> exp = Pair.of("one", "two");
    assertEquals(exp, LineTokenzr.keyValue("one:two"));
    assertEquals(exp, LineTokenzr.keyValue("one:   two  "));
    assertEquals(exp, LineTokenzr.keyValue("  one  :two"));

    exp = Pair.of("1 one", "two 2");
    assertEquals(exp, LineTokenzr.keyValue("1 one:two 2"));
    assertEquals(exp, LineTokenzr.keyValue("1 one:  two 2  "));
    assertEquals(exp, LineTokenzr.keyValue("   1 one   :two 2"));

    // + comments
    assertEquals(exp, LineTokenzr.keyValue(" 1 one:two 2//  three //four"));
    assertEquals(exp, LineTokenzr.keyValue("1 one: two 2   //three four"));
    assertEquals(exp, LineTokenzr.keyValue("1 one :two 2   #three four"));
    assertEquals(exp, LineTokenzr.keyValue("  1 one  : two 2 ## three # four "));
  }


  @Test
  public void testQuoted() throws ParseException {
    Pair<String, String> exp = Pair.of("one 1 ", " 2 two");
    assertEquals(exp, LineTokenzr.keyValue("'one 1 ':' 2 two'"));
    assertEquals(exp, LineTokenzr.keyValue("  'one 1 ':  ' 2 two'"));
    assertEquals(exp, LineTokenzr.keyValue("'one 1 '  :' 2 two'  "));
    assertEquals(exp, LineTokenzr.keyValue("\"one 1 \":\" 2 two\""));
    
    // + comments
    assertEquals(exp, LineTokenzr.keyValue("'one 1 ':' 2 two' #three four"));
    assertEquals(exp, LineTokenzr.keyValue("  'one 1 ':  ' 2 two'// 345"));
    assertEquals(exp, LineTokenzr.keyValue("'one 1 '  :' 2 two'  //four five"));

    // quoting special characters
    exp = Pair.of("one: two ", " three #four //five");
    assertEquals(exp, LineTokenzr.keyValue("'one: two ':' three #four //five'"));
    assertEquals(exp, LineTokenzr.keyValue("  'one: two ':  ' three #four //five'// six"));
    assertEquals(exp, LineTokenzr.keyValue("'one: two '  :' three #four //five' #six: seven"));

    // quoting quotes
    exp = Pair.of("one'two", "three\"four");
    assertEquals(exp, LineTokenzr.keyValue("'one''two':'three\"four'"));
    assertEquals(exp, LineTokenzr.keyValue(" \"one'two\": \"three\\\"four\""));
    assertEquals(exp, LineTokenzr.keyValue("\"one'two\" : 'three\"four' "));
  }


  @Test
  public void testEmptyAndNull() throws ParseException {
    Pair<String, String> exp = Pair.of("", "");
    assertEquals(exp, LineTokenzr.keyValue("'':''"));
    assertEquals(exp, LineTokenzr.keyValue(" '':  ''"));
    assertEquals(exp, LineTokenzr.keyValue("''  :''  "));
    assertEquals(exp, LineTokenzr.keyValue("\"\":\"\""));
    assertEquals(exp, LineTokenzr.keyValue("  \"\"  :  \"\""));

    // + comments
    assertEquals(exp, LineTokenzr.keyValue(" '': ''//not empty"));
    assertEquals(exp, LineTokenzr.keyValue(" \"\":''# some text"));
    assertEquals(exp, LineTokenzr.keyValue(" ''  :\"\"  #some text"));
    
    exp = Pair.of("one 1", null);
    
  }

}
