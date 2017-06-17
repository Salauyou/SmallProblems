package ru.salauyou.builder.util;

import java.text.ParseException;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;


public class LineTokenzr {

  
   
  public static Pair<String, String> keyValue(String s) 
      throws ParseException {
    List<String> tokens = tokenize(s, ':');
    if (tokens.isEmpty() || tokens.get(0) == null) {
      wrongFormat("Key is null", 0);
    } else if (tokens.size() < 2) {
      wrongFormat("Key-value separator : is missing", 0);
    } else if (tokens.size() > 2) {
      wrongFormat("More than one : separator found", 0);
    }
    return Pair.of(tokens.get(0), tokens.get(1));
  }
  
  
  /**
   * Splits a string into list of tokens by a given separator 
   * char, with respect of quoted tokens and trailing comments. 
   * Size of result is always (n + 1), where n is number of
   * occurrences of separator char in a string, except quoted 
   * and commented. Unquoted tokens are trimmed; empty or 
   * whitespace-only unquoted tokens are considered {@code null}. 
   * (Thus, tokenizing empty or blank string will result in list 
   * containing one {@code null} element.)
   * <p>
   * Trailing comments (followed by {@code //}, or {@code #} 
   * after whitespace, or {@code #} at the beginning of a line) 
   * are skipped, unless they are quoted.
   * <p>
   * Quotation style follows Yaml rules. Single-quoted ({@code '})
   * tokens are parsed literally, the only escape sequence 
   * is {@code ''} encoding single-quote character. Double-quoted 
   * ({@code "}) tokens are Java-unescaped after read.
   */
  public static List<String> tokenize(String s, char separator) 
      throws ParseException {
    
    List<String> result = Lists.newArrayList();
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

      // expect separator or EOL after token
      i = skipSpacesAndComments(s, i);
      if (i >= s.length()) {
        return result;
      }
      if (s.charAt(i++) != separator) {
        wrongFormat("Separator " + separator 
            + " not found after token", i);
      }
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


  static int readSingleQuoted(String s, int i, StringBuilder sb) 
      throws ParseException {
    
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
      return wrongFormat("Closing ' is missing", i);
    }
  }


  static int readDoubleQuoted(String s, int i, StringBuilder sb) 
      throws ParseException {
    
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
    return wrongFormat("Closing \" is missing", i);
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
