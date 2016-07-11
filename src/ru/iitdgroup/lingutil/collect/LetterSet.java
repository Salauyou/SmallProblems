package ru.iitdgroup.lingutil.collect;

import java.util.AbstractList;
import java.util.Iterator;

/**
 * Effective bit-based set of chars within range [0-9a-zA-Z]
 * 
 * @author Salauyou
 * @Immutable
 */
public final class LetterSet implements Iterable<Character> {

    private final long bits;
    private String chars = null;
    
    public static final LetterSet EMPTY = new LetterSet(null);
    
    
    private LetterSet(char[] chars) {
        long b = 0;
        if (chars != null && chars.length > 0) {
            for (char c : chars) 
                b |= 1L << bitFor(c, false);
        }
        this.bits = b;
    }
    
    
    private LetterSet(char c) {
        this.bits = 1L << bitFor(c, false);
    }
    
    
    /**
     * Creates a set containing given chars
     */
    public static LetterSet of(CharSequence chars) {
        if (chars == null || chars.length() == 0)
            return EMPTY;
        return new LetterSet(chars.toString().toCharArray());
    }
    
    
    /**
     * Creates a set containing one given char
     */
    public static LetterSet of(char c) {
        return new LetterSet(c);
    }
    
    
    /**
     * Returns how much chars in the set
     */
    public int size() {
        return cardinality(this.bits);
    }
    
    
    /**
     * Test if `this` and `another` have one or more 
     * common chars
     */
    public boolean intersect(LetterSet another) {
        return (this.bits & another.bits) != 0;
    }
    
    
    /**
     * Tests if this set contains given char
     */
    public boolean contains(char c) {
        int bit = bitFor(c, true);
        return bit < 0 ? false : (bits & 1L << bit) != 0;
    }
    
    
    /**
     * Returns a string composed of all chars that this set
     * contains, repeating each once, in undefined order
     */
    public String chars() {
        if (chars == null)
            chars = chars(bits);
        return chars;
    }

    
    @Override
    public int hashCode() {
        return Long.hashCode(bits);
    }
    
    
    @Override
    public boolean equals(Object o) {
        if (o == null || (o instanceof LetterSet))
            return false;
        return this.bits == ((LetterSet)o).bits;
    } 
    
    
    @Override
    public String toString() {
        return "[" + chars() + "]";
    }
    
    
    @Override
    public Iterator<Character> iterator() {
        return new AbstractList<Character>() {
            String ch = chars();            
            @Override public Character get(int i) { return ch.charAt(i); }
            @Override public int size()           { return ch.length(); }            
        }.iterator();
    }
    
    
    
    // ------- functionality shared across LetterSet and LetterMap ------ //
    
    static String chars(long bits) {
        if (bits == 0)
            return "";
        StringBuilder sb = new StringBuilder();
        char s = '0';
        int shift = 0;
        long b = bits;
        for (int i = 0; b != 0; b >>>= 1, i++) {
            switch (i) {
            case LOWERCASE_SHIFT:
                shift = i;
                s = 'a';
                break;
            case UPPERCASE_SHIFT:
                shift = i;
                s = 'A';
            }
            if ((b & 1) == 1)
                sb.append((char)(i - shift + s));
        }
        return sb.toString();
    }
    
    
    static int cardinality(long bits) {
        if (bits == 0)
            return 0;
        if ((bits & -bits) == bits)  // bits == 2^x
            return 1;
        long b = bits;
        int  s = 0;
        while (b != 0) {
            s += b & 1;
            b >>>= 1;
        }
        return s;
    }
    
    
    static final int LOWERCASE_SHIFT = 10;
    static final int UPPERCASE_SHIFT = 36;
    
    static int bitFor(char c, boolean silent) {
        if (c >= '0' && c <= '9')
            return c - '0';
        else if (c >= 'a' && c <= 'z')
            return c - 'a' + LOWERCASE_SHIFT;
        else if (c >= 'A' && c <= 'Z')
            return c - 'A' + UPPERCASE_SHIFT;
        else if (silent)
            return -1;
        else 
            throw new IllegalArgumentException("Out of [0-9a-zA-Z] range");
    }


    

    
}
