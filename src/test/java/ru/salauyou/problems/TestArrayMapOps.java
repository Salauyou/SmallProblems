package ru.salauyou.problems;

import org.junit.Test;

/**
 * <p>Created on 2019-10-22
 *
 * @author Aliaksandr Salauyou (sbt-solovev-an@mail.ca.sbrf.ru)
 */
public class TestArrayMapOps {

  public String[] movePortion(String[] arr, int left, int right, int pos) {
    int shift = 0;
    String[] res = new String[arr.length];
    for (int i = 0; i < arr.length; ++i) {
      if (left != right && pos != left && pos != right) {
        if (i + shift == left) {
          shift = right - i;
        } else if (i + shift == right) {
          shift = pos - i;
        } else if (i + shift == pos) {
          shift = left - i;
        }
      }
      res[i] = arr[i + shift];
    }
    return res;
  }


  @Test
  public void testMovePortion() {
    String[] arr = { "0", "1", "2", "3", "4", "5", "6" };
    String[] result;

    // to start, end
    result = movePortion(arr, 1, 6, 0);
    result = movePortion(arr, 2, 4, 7);

    // head, tail in middle
    result = movePortion(arr, 0, 3, 5);
    result = movePortion(arr, 4, 7, 1);

    // moving in middle
    result = movePortion(arr, 1, 4, 6);
    result = movePortion(arr, 3, 7, 1);

    // unchanged
    result = movePortion(arr, 3, 3, 6);
    result = movePortion(arr, 1, 3, 1);
    result = movePortion(arr, 1, 3, 3);
  }


}
