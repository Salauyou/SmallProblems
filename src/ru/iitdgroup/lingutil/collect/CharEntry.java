package ru.iitdgroup.lingutil.collect;

public class CharEntry<E> {

  final char c;
  final E value;

  CharEntry(char c, E value) {
    this.c = c;
    this.value = value;
  }

  public char getChar() {
    return c;
  }

  public E getValue() {
    return value;
  }
}
