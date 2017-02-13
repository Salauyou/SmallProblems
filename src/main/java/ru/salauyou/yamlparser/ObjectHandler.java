package ru.salauyou.yamlparser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Event-based object handler
 */
public interface ObjectHandler {
  
  interface KeyHandle {
    @Nullable String key();
    @Nullable KeyHandle parent();
  }
  
  interface ObjectHandle {};
  
  
  void openObject(@Nonnull ObjectHandle objectHandle, 
      @Nonnull KeyHandle firstKey);
  
  void closeObject(@Nonnull ObjectHandle objectHandle);
  
  void openKey(@Nonnull KeyHandle key);
  
  void acceptValue(@Nonnull KeyHandle key, 
      @Nullable Object value);
  
  void closeKey(@Nonnull KeyHandle key);
  
  Object getResult();
 
}
