package ru.salauyou.yamlparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;



public class ParserTests {

  @Test
  public void parseSimpleKeyValue() {
    Object result = doParse("key: value");
    assertEquals(ImmutableMap.of("key", "value"), result);
    
    result = doParse("key: three word value  #");
    assertEquals(ImmutableMap.of("key", "three word value"), result);
    
    result =  doParse("  Complicated#  key  :  More complicated#   value   #  and comment 'as well'");
    assertEquals(ImmutableMap.of("Complicated#  key", "More complicated#   value"), result);
  }
  
  
  @Test
  public void parseWithSingleQuotes() {
    Object result = doParse("' key': '  value'  ");
    assertEquals(ImmutableMap.of(" key", "  value"), result);
    
    result = doParse("'key  ': 'value  '");
    assertEquals(ImmutableMap.of("key  ", "value  "), result);
    
    result = doParse("  'key':'value # with hashes #'   # real comment ###");
    assertEquals(ImmutableMap.of("key", "value # with hashes #"), result);
    
    result = doParse("key here  :  '  Key''s  value here '  ");
    assertEquals(ImmutableMap.of("key here", "  Key's  value here "), result);
  }
  
  
  @Test
  public void parseObject() {
    Object result = doParse(
        " key1 : {key2 : { key 3: value 3, key 4 :value 4  }, key5 : value5 }");
    Map<String, ?> expected = ImmutableMap.of(
        "key1", ImmutableMap.of(
            "key2", ImmutableMap.of(
              "key 3", "value 3", 
              "key 4", "value 4"), 
            "key5", "value5"));
    assertEquals(expected, result);
  }
  
  
  @Test
  public void testMalformed() {
    List<String> malformed = ImmutableList.of(
        "key:: value",
        "key: value { 123 }  ",
        "key : {value, another value}",
        "{ key }: value",
        "key : value1, value2 ",
        "key: value1, { value2 }",
        " key : } value",
        "key { : value",
        "key : { key2 : value2, key3: value3",
        "key1, key2 :value",
        "key : value1 : value2  ",
        "  key { value } : value",
        "key:{ key2 :value2 #, key3: value3 } ",
        " { key1 : value1} : value2");
    
    for (String line : malformed) {
      try {
        Object result = doParse(line);
        fail(String.format("'%s': %s", line, result));
      } catch (IllegalStateException e) {
        // okay
      }
    }
  }
  
  
  static Object doParse(String input) {
    ObjectHandler h = new MapObjectHandler();
    LineProcessor p = new LineProcessor(h);
    p.parseString(input);
    return h.getResult();
  }
  
  
}
