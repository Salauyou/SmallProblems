package ru.salauyou.builder.impl;

import java.lang.reflect.Method;
import java.util.List;

import com.google.common.collect.Lists;

import ru.salauyou.builder.api.Builder;


public class BuilderProcessor {

  List<BuilderImpl> builders;


  /**
   * Creates a builder processor, accepting 
   * provided singleton builders
   */
  public BuilderProcessor(Object[] builders) {

  }


  /**
   * Executes on given target object, catching 
   * and collecting exceptions throwed by 
   * separate builders
   */
  public List<Exception> executeOn(Object target) {
    List<Exception> result = Lists.newArrayList();
    for (BuilderImpl b : builders) {
      if (!b.applicableTo(target)) {
        continue;
      }
      try {
        b.method.invoke(b.builder, target);
      } catch (Exception e) {
        result.add(e);
        if (b.stopOnException) {
          break;
        }
      }
    }
    return result;
  }


  static class BuilderImpl {
    final Object builder;
    final Method method;
    final boolean stopOnException;
    final Class<?>[] appliedTo;

    BuilderImpl(Object builder, Method method,
        boolean stopOnException, Class<?>[] appliedTo) {
      this.builder = builder;
      this.method = method;
      this.stopOnException = stopOnException;
      this.appliedTo = appliedTo;
    }

    boolean applicableTo(Object target) {
      Class<?> targetClass = target.getClass();
      for (Class<?> cl : appliedTo) {
        if (cl.isAssignableFrom(targetClass)) {
          return true;
        }
      }
      return false;
    }

    static BuilderImpl from(Object builder) {
      Class<?> builderClass = builder.getClass();
      Builder b = builderClass.getAnnotation(Builder.class);
      if (b == null) {
        return null;
      }

      Method[] ms = builderClass.getMethods();
      Method builderMethod = null;
      for (Method m : ms) {
        if (m.getName().equals("build")
            && m.getParameterCount() == 1) {
          if (builderMethod != null) {
            throw new IllegalArgumentException(
                "More than one one-arg 'build' "
                    + "method found in class "
                    + builderClass);
          }
          builderMethod = m;
        }
      }
      if (builderMethod == null) {
        throw new IllegalArgumentException(
            "No one-arg 'build' method found in class "
                + builderClass);
      }
      return new BuilderImpl(
          builder, builderMethod,
          b.stopOnException(), b.appliedTo());
    }
  }

}
