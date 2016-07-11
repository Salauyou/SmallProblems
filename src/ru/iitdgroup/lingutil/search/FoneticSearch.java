package ru.iitdgroup.lingutil.search;

import static ru.iitdgroup.lingutil.collect.LetterSet.of;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import ru.iitdgroup.lingutil.match.ScoredMatch;
import ru.iitdgroup.lingutil.collect.LetterMap;
import ru.iitdgroup.lingutil.collect.LetterSet;
import ru.iitdgroup.lingutil.collect.LetterTable;


/**
 * Implementation of original algorithm to search for
 * "phonetically-equivalent" occurrences of a pattern in a given
 * text.
 * <p>The main differences from common Levenstein-like approach
 * are that:
 * <ul>
 * <li>in addition to one-char phonetically equivalent substitutions 
 * (like "K" <-> "C"), also are handled cases when 2-char digraph 
 * is substituted by phonetically equivalent char or vice versa 
 * ("PH" <-> "F"), or one digraph is substituted by anotner 
 * ("ZZ" <-> "TS"). Different kinds of substitutions have 
 * different costs;
 * <li>only single non-phonetic char edits 
 * (insertions/deletions/replacements) are allowed between 
 * literally or phonetically equivalent matching pieces. 
 * Thus, "P<b>V</b>I<b>K</b>ZZA" will match "PITS<b>N</b>A" with 
 * price of (3 gap costs + 1 digraph cost), but "PI<b>VK</b>ZZA" 
 * won't match "PITSA" nor even "PIZZA" regardless of cost limit.
 * This restriction is made by assumption that the vast majority 
 * of misprints are one-char edits and transpositions.
 * </ul>
 * 
 * @author Salauyou
 */
public class FoneticSearch {

    double substCost   = 0.5;
    double digraphCost = 0.5;
    double gapCost     = 1.0;
    double replaceCost = 2.0;
    
    final static LetterMap<LetterSet> SUBS 
        = new LetterMap<LetterSet>()
              .put('A', of('O'))
              .put('B', of('P'))
              .put('C', of("KQ"))
              .put('D', of('T'))
              .put('E', of('I'))
              .put('F', of('P'))
              .put('G', of("HJ"))
              .put('H', of('G'))
              .put('I', of("JY"))
              .put('J', of("GIY"))
              .put('K', of("CQ"))
              .put('P', of('F'))
              .put('Q', of("KC"))
              .put('S', of('Z'))
              .put('T', of('D'))
              .put('U', of("AO"))
              .put('V', of('W'))
              .put('W', of("VU"))
              .put('Y', of("IJ"))
              .put('Z', of('S'))
              .put('0', of('O'))
              .put('1', of("IL"))
              .put('2', of('T'))
              .put('3', of('E'))
              .put('5', of('S'))
              .put('6', of('B'))
              .put('8', of('B'))
              .put('9', of('D'))
              .makeImmutable();
    
    final static LetterTable<LetterSet> DIGRAPHS 
        = new LetterTable<LetterSet>()
              .put("CH", of("C4"))
              .put("CK", of("KC"))
              .put("EA", of('I'))
              .put("EE", of("IY"))
              .put("ER", of('R'))
              .put("EU", of('E'))
              .put("DJ", of('J'))
              .put("DZ", of('J'))
              .put("FF", of("FVW"))
              .put("GH", of('G'))
              .put("GG", of('G'))
              .put("IE", of('E'))
              .put("IO", of("OE"))
              .put("IU", of('U'))
              .put("JA", of('A'))
              .put("JE", of('E'))
              .put("JO", of('E'))
              .put("KH", of("KHC"))
              .put("KN", of('N'))
              .put("KS", of('X'))
              .put("KW", of("KQ"))
              .put("LL", of('L'))
              .put("MM", of('M'))
              .put("NG", of('N'))
              .put("NN", of('N'))
              .put("OO", of('U'))
              .put("OU", of("OU"))
              .put("PH", of('F'))
              .put("PP", of('P'))
              .put("QU", of("QKC"))
              .put("RR", of('R'))
              .put("SC", of('C'))
              .put("SH", of('S'))
              .put("SG", of('S'))
              .put("SS", of('S'))
              .put("TC", of('C'))
              .put("TH", of("TZS"))
              .put("TO", of('2'))
              .put("TS", of('C'))
              .put("TT", of('T'))
              .put("TW", of("T2"))
              .put("TU", of('2'))
              .put("TZ", of('C'))
              .put("WH", of('W'))
              .put("YA", of('A'))
              .put("YE", of('E'))
              .put("YO", of("EO"))
              .put("YU", of('U'))
              .put("ZH", of("GJ"))
              .put("ZZ", of("CZ"))
              .makeImmutable();

    
    
    public FoneticSearch setSubstitutionCost(double cost) {
        this.substCost = cost;
        return this;
    }
    
    
    public FoneticSearch setDigraphCost(double cost) {
        this.digraphCost = cost;
        return this;
    }
    
    
    public FoneticSearch setGapCost(double cost) {
        this.gapCost = cost;
        return this;
    }
    
    
    public FoneticSearch setReplaceCost(double cost) {
        this.replaceCost = cost;
        return this;
    }
    
    
    
    /**
     * Use this if many words need to be searched in one input,
     * in conjunction with {@link FoneticSearch#findOccurrences(CharSequence, 
     * LetterSet[], CharSequence, double)} 
     */
    public static LetterSet[] extractDigraphs(CharSequence text) {
        LetterSet[] ds = new LetterSet[text.length()];
        for (int j = 1; j < text.length(); j++) 
            ds[j] = DIGRAPHS.get(text.charAt(j - 1), text.charAt(j));
        return ds;
    }
    
    
    
    /**
     * Searches for "phonetically-equivalent" occurrences of `pattern` in `text`, 
     * allowing phonetic substitutions, one-char gaps in either `pattern` or `text`
     * and one-char replacements
     */
    public List<ScoredMatch> findOccurrences(CharSequence text, CharSequence pattern, double maxCost) {
        return findOccurrences(text, extractDigraphs(text), pattern, maxCost);
    }
    
    
    
    /**
     * Searches for "phonetically-equivalent" occurrences of `pattern` in `text`, 
     * allowing phonetic substitutions, one-char gaps in either `text` 
     * or `pattern` and one-char replacements.
     * <p>
     * This method is preferred over 
     * {@link FoneticSearch#findOccurrences(CharSequence, CharSequence, double)} 
     * when many words need to be searched in one input, e. g.:
     * <blockquote><pre>
     * String text = ...;
     * List&lt;String&gt; words = ...;
     * List&lt;ScoredMatch&gt; matches = new ArrayList&lt;&gt;();
     * LetterSet[] ds = FoneticSearch.extractDigraphs(text);
     * for (String w : words)
     *     matches.addAll(FoneticSearch.findOccurrences(text, ds, w, 2.5));
     * </pre></blockquote>
     */
    public List<ScoredMatch> findOccurrences(CharSequence text, LetterSet[] textDigraphs, 
                                             CharSequence pattern, double maxCost) {
        int len = text.length();
        
        double[] t1 = new double[len + 2];  // submatch costs
        double[] t0 = initialT(len);        
        int[]    s1 = new int[len + 2];     // starting positions
        int[]    s0 = initialS(len);
        
        BitSet   r0 = initialR(len);        // set of positions marked to visit
        int[]    v  = initialV(len);        // positions to visit at next rows
        int[]    v0 = initialV(len);   
       
        int nv  = len;                      // count of positions to visit
        int nv0 = len;
        int nv1 = 0;
        
        boolean f0 = false;                 // found anything?
        boolean f1 = true;
        
        for (int i = 0; i < pattern.length(); i++) {
            double[]  t  = new double[len + 2];
            int[]     s  = new int[len + 2];
            int[]     v1 = new int[len];           
            BitSet    r1 = new BitSet(len);
            
            char      a  = pattern.charAt(i);
            LetterSet la = i == 0 ? null : DIGRAPHS.get(pattern.charAt(i - 1), a);
            LetterSet sa = SUBS.get(a);       
            
            for (int k = 0; k < nv; k++) {
                int    j    = v[k];
                int    ss   = -1;
                double cost = -1.0;        
                char   b    = text.charAt(j);
                double cc   = Double.MAX_VALUE;
                
                if (a == b)
                    cost = 0;
                else if ((sa != null && sa.contains(b)) 
                      || (SUBS.containsKey(b) && SUBS.get(b).contains(a)))
                    cost = substCost;
                                
                // find best match/substitution/gap/replace
                if (cost >= 0) {
                    if (t0[j + 1] > 0) {
                        ss = s0[j + 1];
                        cc = t0[j + 1] + cost;
                    }
                    if (t0[j] > 0 && t0[j] + cost + gapCost < cc) {
                        ss = s0[j];
                        cc = t0[j] + cost + gapCost;
                    }
                    if (t1[j + 1] > 0 && t1[j + 1] + cost + gapCost < cc) {
                        ss = s1[j + 1];
                        cc = t1[j + 1] + cost + gapCost;
                    }
                    if (t1[j] > 0 && t1[j] + cost + replaceCost < cc) {
                        ss = s1[j];
                        cc = t1[j] + cost + replaceCost;
                    }
                }
                // try digraphs
                LetterSet lb = textDigraphs[j];
                if (la != null && la.contains(b)) {
                    if (t1[j + 1] > 0 && t1[j + 1] + digraphCost < cc) {
                        cc = t1[j + 1] + digraphCost;
                        ss = s1[j + 1];
                    }
                    if (t1[j] > 0 && t1[j] + digraphCost + gapCost < cc) {
                        cc = t1[j] + digraphCost + gapCost;
                        ss = s1[j];
                    }
                }
                if (lb != null && lb.contains(a)) {
                    if (t0[j] > 0 && t0[j] + digraphCost < cc) {
                        cc = t0[j] + digraphCost;
                        ss = s0[j];
                    }
                    if (t1[j] > 0 && t1[j] + digraphCost + gapCost < cc) {
                        cc = t1[j] + digraphCost + gapCost;
                        ss = s1[j];
                    }
                }                
                if (la != null && lb != null && t1[j] > 0 && lb.intersect(la) 
                    && t1[j] + digraphCost < cc) {
                    cc = t1[j] + digraphCost;
                    ss = s1[j];
                }
                
                // set cost and start position for i, j-substring
                if (ss >= 0 && cc - 1.0 <= maxCost) {
                    t[j + 2] = cc;
                    s[j + 2] = ss;
                    f0 = true;
                    // mark positions for visit
                    for (int p = j + 1; p <= j + 2 && p < len; p++) {
                        if (!r0.get(p)) {
                            r0.set(p);
                            v0[nv0++] = p;
                        }
                        if (!r1.get(p)) {
                            r1.set(p);
                            v1[nv1++] = p;
                        }
                    }
                }
            }
            // nothing found on current and previous rows
            if (!f0 && !f1)
                return Collections.emptyList();
           
            f1 = f0;
            f0 = false;
            s1 = s0;
            s0 = s;
            t1 = t0;
            t0 = t;
            
            v   = v0;
            v0  = v1;
            r0  = r1;
            nv  = nv0;
            nv0 = nv1;
            nv1 = 0;
        }    
        
        // collect matches
        List<ScoredMatch> res = new ArrayList<>(); 
        BitSet st = new BitSet(len);
        for (int j = text.length() + 1; j > 0; j--) {
            if (t0[j] != 0 && !st.get(s0[j])) {
                st.set(s0[j]);
                res.add(new ScoredMatch(s0[j], j - 1, t0[j] - 1));
            }
            if (t1[j] != 0 && !st.get(s1[j])) {
                if (t1[j] - 1 + gapCost <= maxCost) {
                    st.set(s1[j]);
                    res.add(new ScoredMatch(s1[j], j - 1, t1[j] - 1 + gapCost));
                }
            }
        }
        res.sort((m1, m2) -> m1.start - m2.start);
        return res;
    }
    
  
    
    
    
    static final int      INITIAL_LEN = 4096;
    static final double[] INITIAL_T = new double[INITIAL_LEN + 2];
    static final int[]    INITIAL_S = new int[INITIAL_LEN + 2];
    static final BitSet   INITIAL_R = new BitSet(INITIAL_LEN);
    static final int[]    INITIAL_V = new int[INITIAL_LEN];

    static {
        Arrays.fill(INITIAL_T, 1.0);
        for (int j = 2; j < INITIAL_S.length; j++)
            INITIAL_S[j] = j - 1;
        for (int j = 0; j < INITIAL_V.length; j++) {
            INITIAL_V[j] = j;
            INITIAL_R.set(j);
        }
    }
    
    
    static double[] initialT(int size) {
        if (size <= INITIAL_LEN)
            return INITIAL_T;
        else {
            double[] t = new double[size + 2];
            Arrays.fill(t, 1.0);
            return t;
        }
    }
    
    
    static int[] initialS(int size) {
        if (size <= INITIAL_LEN)
            return INITIAL_S;
        else {
            int[] s = new int[size + 2];
            for (int j = 2; j < s.length; j++)
                s[j] = j - 1;
            return s;
        }
    }
    
    
    static int[] initialV(int size) {
        if (size <= INITIAL_LEN)
            return INITIAL_V;
        else {
            int[] v = new int[size];
            for (int j = 0; j < size; j++)
                v[j] = j;
            return v;
        }
    }
    
    
    static BitSet initialR(int size) {
        if (size <= INITIAL_LEN)
            return INITIAL_R;
        BitSet r = new BitSet(size);
        for (int j = 0; j < size; j++)
            r.set(j);
        return r;
    }
    
}
