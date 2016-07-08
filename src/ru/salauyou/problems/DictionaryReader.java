package ru.salauyou.problems;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class DictionaryReader {

  
  
    try (BufferedReader r = Files.newBufferedReader(WORDS, StandardCharsets.UTF_8)) {
      r.lines().forEach(consumer::accept);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  
}
