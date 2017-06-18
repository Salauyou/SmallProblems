package ru.salauyou.builder.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;


public class LineTokenzr {

  
   
  /**
   * Extracts a key-value pair from the input string using 
   * colon separator ({@code :}), or returns {@code null}
   * if input is {@code null}, or blank, or comment-only.
   * If input contains only one token, value is considered 
   * {@code null}
   * 
   * @throws ParseException if there is >2 tokens in the 
   *    string, or key is {@code null}, or format is wrong
   */
  public static Pair<String, String> keyValue(String s) 
      throws ParseException {
    
    if (s == null) {
      return null;
    }
    List<String> tokens = tokenize(s, ':', 2);
    if (tokens.get(0) == null) {
      if (tokens.size() == 1) {
        return null;
      } else {
        wrongFormat("Key is null", 0);
      }
    }
    return Pair.of(tokens.get(0), tokens.get(1));
  }
  
  
  /**
   * Splits a string into list of tokens by a given separator 
   * char, with respect of quoted tokens and trailing comments. 
   * Size of result is always (n + 1), where n is number of
   * occurrences of separator char in the string (except quoted 
   * and commented). Unquoted tokens are trimmed, empty and 
   * blank unquoted tokens are considered {@code null} (thus, 
   * tokenizing empty or blank string will produce a list 
   * containing one {@code null} element).
   * <p>
   * Trailing comments (characters following {@code //}, or 
   * {@code #} after a whitespace, or {@code #} at the beginning 
   * of the string) are skipped, unless they are quoted.
   * <p>
   * 'Single-quoted' tokens are parsed literally, except 
   * {@code ''} sequence which is parsed as {@code '} character. 
   * "Double-quoted" tokens are Java-unescaped after read.
   * <p>
   * The {@code limit} parameter, if > 0, determines maximum
   * number of tokens the string may contain: when extra 
   * separator is found, method will throw {@code ParseException}. 
   * If {@code limit <= 0}, all tokens will be read.
   */
  public static List<String> tokenize(
      String s, char separator, int limit) 
      throws ParseException {
    
    boolean limited = limit > 0;
    List<String> result = new ArrayList<>();
    int i = 0;

    for (;;) {
      String token = null;
      i = skipSpacesAndComments(s, i);
      
      // read token if chars left
      if (i < s.length()) {
        StringBuilder sb = new StringBuilder();
        char c = s.charAt(i);
        if (c == '\'') {
          i = readSingleQuoted(s, i + 1, sb);
          token = sb.toString();
        } else if (c == '"') {
          i = readDoubleQuoted(s, i + 1, sb);
          token = StringEscapeUtils.unescapeJava(sb.toString());
        } else {
          i = readPlain(s, i, separator, sb);
          token = StringUtils.stripToNull(sb.toString());
        }
      }

      // append to result
      result.add(token);

      // expect EOL or separator after token
      i = skipSpacesAndComments(s, i);
      if (i >= s.length()) {
        return result;
      }
      if (s.charAt(i) != separator) {
        wrongFormat("Extra characters found after token", i);
      }
      // check limit
      if (limited && result.size() == limit) {
        wrongFormat("Number of tokens exceed limit " + limit, i);
      }
      i++; // consume separator
    }
  }


  static int skipSpacesAndComments(String s, int i) {
    while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
      ++i;
    }
    if (i < s.length()) {
      char c = s.charAt(i);
      if (c == '#' || (i + 1 < s.length()
          && c == '/' && s.charAt(i + 1) == '/')) {
        return s.length();
      }
    }
    return i;
  }


  // start = first character following a quote
  static int readSingleQuoted(String s, int start, StringBuilder sb) 
      throws ParseException {
    
    int i = start;
    boolean quoteMet = false;
    for (; i < s.length(); ++i) {
      char c = s.charAt(i);
      if (c == '\'') {
        if (quoteMet) {
          sb.append(c);
        }
        quoteMet = !quoteMet;
      } else if (quoteMet) {
        return i;
      } else {
        sb.append(c);
      }
    }
    if (quoteMet) {
      return i;
    } else {
      return wrongFormat("Closing ' is missing", start - 1);
    }
  }


  // start = first char following a quote
  static int readDoubleQuoted(String s, int start, StringBuilder sb) 
      throws ParseException {
    
    int i = start;
    for (; i < s.length(); ++i) {
      char c = s.charAt(i);
      if (c == '"') {
        // check if this quote is escaped
        int k = i;
        while (s.charAt(--k) == '\\');
        if ((i - k) % 2 == 1) {
          return i + 1;
        }
      }
      sb.append(c);
    }
    return wrongFormat("Closing \" is missing", start - 1);
  }


  static int readPlain(String s, int i, 
      char separator, StringBuilder sb) {
    
    char p = '\0';  // preceeding char
    for (; i < s.length(); ++i) {
      char c = s.charAt(i);
      if ((c == '#' && Character.isWhitespace(p))
          || c == separator) {
        return i;
      } else if (c == '/' && p == '/') {
        sb.deleteCharAt(sb.length() - 1);
        return i - 1;
      } else {
        sb.append(c);
      }
      p = c;
    }
    return i;
  }


  static int wrongFormat(String message, int i) 
      throws ParseException {
    throw new ParseException(message, i);
  }
}
