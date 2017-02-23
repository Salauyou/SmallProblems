package ru.salauyou.yamlparser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Processor {
  
  void setObjectHandler(@Nonnull ObjectHandler<?> handler);
  
  void parse(@Nonnull File input, 
      @Nullable Charset charset) throws IOException;
  
  void parse(@Nonnull String input);
  
  void skipObject();
  
  void skipAll();
  
}
