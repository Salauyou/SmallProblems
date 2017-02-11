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
    LineProcessor p = new LineProcessor();
    Object result = p.parseString("key: value");
    assertEquals(ImmutableMap.of("key", "value"), result);
    
    p = new LineProcessor();
    result = p.parseString("key: three word value  #");
    assertEquals(ImmutableMap.of("key", "three word value"), result);
    
    p = new LineProcessor();
    result = p.parseString("  Complicated#  key  :  More complicated#   value   #  and comment 'as well'");
    assertEquals(ImmutableMap.of("Complicated#  key", "More complicated#   value"), result);
  }
  
  
  @Test
  public void parseWithSingleQuotes() {
    LineProcessor p = new LineProcessor();
    Object result = p.parseString("' key': '  value'  ");
    assertEquals(ImmutableMap.of(" key", "  value"), result);
    
    p = new LineProcessor();
    result = p.parseString("'key  ': 'value  '");
    assertEquals(ImmutableMap.of("key  ", "value  "), result);
    
    p = new LineProcessor();
    result = p.parseString("  'key':'value # with hashes #'   # real comment ###");
    assertEquals(ImmutableMap.of("key", "value # with hashes #"), result);
    
    p = new LineProcessor();
    result = p.parseString("key here  :  '  Key''s  value here '  ");
    assertEquals(ImmutableMap.of("key here", "  Key's  value here "), result);
  }
  
  
  @Test
  public void parseObject() {
    LineProcessor p = new LineProcessor();
    Object result = p.parseString(
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
        Object result = new LineProcessor().parseString(line);
        fail(String.format("'%s': %s", line, result));
      } catch (IllegalStateException e) {
        // okay
      }
    }
  }
  
  
}
