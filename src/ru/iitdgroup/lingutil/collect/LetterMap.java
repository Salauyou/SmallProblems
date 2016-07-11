package ru.iitdgroup.lingutil.collect;

import java.util.Map.Entry;


/**
 * Effective bit-based map which keys are chars within range [0-9a-zA-Z]
 * 
 * @author Salauyou
 * @param <V> value type
 */
public class LetterMap<V> {

    static final int WIDTH = 64;
    private final Object[] values = new Object[WIDTH];
    private boolean immutable = false;
    
    
    /**
     * Returns how much keys are associated with non-null values
     */
    public int size() {
        int s = 0;
        for (int i = 0; i < WIDTH; i++) 
            s += values[i] == null ? 0 : 1;
        return s;
    }
    
    
    /**
     * Returns value mapped to a given char key; or null
     * if there is no association, or key char is out of allowed
     * range, or it is mapped to null
     */
    public V get(char c) {
        int p = LetterSet.bitFor(c, true);
        @SuppressWarnings("unchecked")
        V res = p < 0 ? null : (V) values[p];
        return res;
    }
    
    
    /**
     * Returns if there is non-null value mapped 
     * to given key char
     */
    public boolean containsKey(char c) {
        int p = LetterSet.bitFor(c, true);
        return p < 0 ? false : values[p] != null;
    }
    
    
    /**
     * Maps given value to given key char, replacing any
     * former mapping
     */
    public LetterMap<V> put(char c, V value) {
        checkMutability();
        values[LetterSet.bitFor(c, false)] = value;
        return this;
    }
    
    
    /**
     * Performs multiple mapping, replacing any former mappings
     */
    @SafeVarargs
    public final LetterMap<V> putAll(Entry<Character, ? extends V>... entries) {
        for (Entry<Character, ? extends V> e : entries)
            put(e.getKey(), e.getValue());
        return this;
    }
    
    
    /**
     * Removes mapping to given key char
     */
    public LetterMap<V> remove(char c) {
        checkMutability();
        int p = LetterSet.bitFor(c, true);
        if (p >= 0)
            values[p] = null;
        return this;
    }
    
    
    /**
     * Makes `this` letter map immutable, so it will
     * throw `UnsupportedOperationException` on any
     * modification operation calls
     */
    public LetterMap<V> makeImmutable() {
        immutable = true;
        return this;
    }
    
    
    
    // private stuff
    
    private void checkMutability() {
        if (immutable)
            throw new UnsupportedOperationException("This LetterMap is immutable");
    }
    
}
