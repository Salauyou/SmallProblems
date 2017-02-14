package ru.salauyou.yamlparser;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;

public interface Processor {
  
  void setObjectHandler(@Nonnull ObjectHandler handler);
  
  void parse(@Nonnull InputStream input) throws IOException;
  
  void parse(@Nonnull String input);
  
  void skipObject();
  
  void skipAll();
  
}
