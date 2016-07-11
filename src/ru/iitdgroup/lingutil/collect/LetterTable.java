package ru.iitdgroup.lingutil.collect;

/**
 * Bit-based 2D map where keys are chars within range [0-9a-zA-Z]
 * 
 * @author Salauyou
 * @param <V>
 */
public class LetterTable<V> {

    boolean immutable = false;
    
    LetterMap<LetterMap<V>> table = new LetterMap<>();
    
    
    public LetterTable<V> put(char f, char s, V value) {
        checkMutability();
        LetterMap<V> row = table.get(f);
        if (row == null) {
            row = new LetterMap<>();
            table.put(f, row);
        }
        row.put(s, value);
        return this;
    }
    
    
    public LetterTable<V> put(CharSequence c, V value) {
        if (c.length() != 2)
            throw new IllegalArgumentException();
        return put(c.charAt(0), c.charAt(1), value);
    }
    
    
    public V get(char f, char s) {
        LetterMap<V> row = table.get(f);
        return row == null ? null : row.get(s);
    }
    
    
    public LetterTable<V> makeImmutable() {
        immutable = true;
        return this;
    }
    
    
    // private stuff
    
    private void checkMutability() {
        if (immutable)
            throw new UnsupportedOperationException("This LetterMap is immutable");
    }
    
}
