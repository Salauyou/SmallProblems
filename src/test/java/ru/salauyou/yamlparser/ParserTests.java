package ru.salauyou.yamlparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import ru.salauyou.yamlparser.impl.YamlDocumentProcessor;


public class ParserTests {

  @Test
  public void parseSimpleKeyValue() {
    Object result = doParse("  key: value");
    assertEquals(ImmutableMap.of("key", "value"), result);
    
    result = doParse("  key: three word value  #");
    assertEquals(ImmutableMap.of("key", "three word value"), result);
    
    result =  doParse("Complicated#  key  :  More complicated#   value   #  and comment 'as well'");
    assertEquals(ImmutableMap.of("Complicated#  key", "More complicated#   value"), result);
  }
  
  
  @Test
  public void parseWithSingleQuotes() {
    Object result = doParse("' key': '  value'  ");
    assertEquals(ImmutableMap.of(" key", "  value"), result);
    
    result = doParse("'key  ': 'value  '");
    assertEquals(ImmutableMap.of("key  ", "value  "), result);
    
    result = doParse("'key':'value # with hashes #'   # real comment ###");
    assertEquals(ImmutableMap.of("key", "value # with hashes #"), result);
    
    result = doParse("   key here  :  '  Key''s  value here '  ");
    assertEquals(ImmutableMap.of("key here", "  Key's  value here "), result);
  }
  
  
  @Test
  public void parseObject() {
    Object result = doParse(
        " key1 : {key2 : { key 3: value 3, key 4 :value 4  }, key5 : value5 } ");
    Map<String, ?> expected = ImmutableMap.of(
        "key1", ImmutableMap.of(
            "key2", ImmutableMap.of(
              "key 3", "value 3", 
              "key 4", "value 4"), 
            "key5", "value5"));
    assertEquals(expected, result);
  }
  
  
  static final String MIXED_INPUT = Joiner.on('\n').join(
      ImmutableList.of(
          " key1:                                  # first key       ",
          "   key2 : {key3: value3, key4:          # messy =(        ",
          "     value4, key5                       # see next line   ",
          "     : {key6 : value6  }, key7: value7} # another folded  ",
          "           ### just a line comment ###                    ",
          "                                                          ",
          "   key8 :                               # nested block    ",
          "           ## one more line comment ##                    ", 
          "                                                          ",
          "      key9 : value9                                       ",
          "      key10 : value10                   # nothing special ",
          " key11 : value11                        # back 2 levels   "));
  
  static final Map<String, ?> MIXED_EXPECTED = ImmutableMap.of(
      "key1", ImmutableMap.of(
          "key2", ImmutableMap.of(
              "key3", "value3",
              "key4", "value4",
              "key5", ImmutableMap.of(
                  "key6", "value6"),
              "key7", "value7"),
          "key8", ImmutableMap.of(
              "key9", "value9",
              "key10", "value10")),
      "key11", "value11");
  
  @Test
  public void parseMixedObject() {
    Object result = doParse(MIXED_INPUT);
    assertEquals(MIXED_EXPECTED, result);
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
        "key : } value",
        "key { : value",
        "key : { key2 : value2, key3: value3",
        "key1, key2 :value",
        "key : value1 : value2  ",
        "key { value } : value",
        "key:{ key2 :value2 #, key3: value3 } ",
        "{ key1 : value1} : value2");
    
    for (String line : malformed) {
      try {
        Object result = doParse(line);
        fail(String.format("'%s': %s", line, result));
      } catch (Exception e) {
        System.err.println(e.getMessage());  // this is okay
      }
    }
  }
  
  
  static Object doParse(String input) {
    ObjectHandler<Map<String, ?>> h = new MapObjectHandler(false);
    YamlDocumentProcessor p = new YamlDocumentProcessor(h);
    p.parse(input);
    return h.getResult();
  }
  
  
}
