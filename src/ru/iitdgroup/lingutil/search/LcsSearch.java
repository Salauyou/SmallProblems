package ru.iitdgroup.lingutil.search;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import ru.iitdgroup.lingutil.match.ScoredMatch;


/**
 * Search based on gapped LCS
 * 
 * @author Salauyou
 */
public class LcsSearch {
    
    
    public static Map<Character, int[]> toPositionCharMap(CharSequence input) {
        Map<Character, int[]> result = new HashMap<>();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (result.containsKey(c))
                continue;
            int[] p = new int[input.length()];
            result.put(c, p);
            int pred = Integer.MAX_VALUE;
            for (int k = input.length() - 1; k >= 0; k--) {
                p[k] = pred;
                if (input.charAt(k) == c)
                    pred = k;
            }
        }
        return result;
    }
    
    
    
    /**
     * Searches fuzzilly based on LCS
     * 
     * @param input         input string (where to search)
     * @param word          substring to search for
     * @param maxWidth      max width of resulting substring
     * @param minLcs        min number of chars in common subsequence btw `word` and resulting substring
     */
    public static List<ScoredMatch> findLcsOccurrences(CharSequence input, CharSequence word, int maxWidth, int minLcs, int maxGap) {
        return findLcsOccurrences(input.charAt(0), toPositionCharMap(input), input.length(), word, maxWidth, minLcs, maxGap);
    }
    
    
    
    /**
     * Searches fuzzilly based on LCS
     * 
     * @param firstChar     first char of input
     * @param nextPositions map char -> next position array as as obtained by `toPositionCharMap(input)`
     * @param inputLength   input length
     * @param query         query string
     * @param maxWidth      max width of resulting substring
     * @param minLcs        min lcs size (number of chars in common subsequence btw `word` and resulting substring)
     * @param maxGap        max gap size between common chunks (0 means full match)
     * @return              matches, where score is number of common chars
     */
    public static List<ScoredMatch> findLcsOccurrences(char firstChar, Map<Character, int[]> nextPositions, 
                                                       int inputLength, CharSequence query, int maxWidth, int minLcs, int maxGap) {
        PositionHolder found = new PositionHolder();
        
        // Working matrix t[i][j][z]
        // i    - position in word 
        // j    - position in input 
        // z[0] - number of stored lcs paths 
        // z[k] - best width of path of length k
        int[][][] t = new int[query.length()][][]; 
        
        // next char position matrix
        int[][] nc = new int[query.length()][];
        for (int i = 0; i < query.length(); i++) {
            nc[i] = nextPositions.get(query.charAt(i));
            t[i] = new int[inputLength][];    
        }
        
        for (int i = 0; i < query.length(); i++) {
            if (nc[i] == null)
                continue;

            for (int j = query.charAt(i) == firstChar ? 0 : nc[i][0]; 
                 j < inputLength; j = nc[i][j]) {
                
                int[] from = t[i][j];                           // node [i,j]
                int maxLen = 0;                                 // longest path succeded to append from node [i,j]
                int rm = Integer.MAX_VALUE;                     // rightmost position so far

                // find closest common chars
                // lower than i, but not lower than max gap,
                // and righter than j, but not righter than gap
                for (int k = i + 1; k < Math.min(i + maxGap + 1, query.length()); k++) {
                    if (nc[k] == null)
                        continue;
                    int p = nc[k][j];                           // position of common char right to j at row i
                    if (p >= rm) 
                        continue;
                    int w = p - j;                              // edge width
                    if (w >= maxWidth || w > maxGap + 1) 
                        continue;

                    int[] to = t[k][p];
                    if (to == null) {
                        to = new int[query.length() + 1];
                        t[k][p] = to;
                        Arrays.fill(t[k][p], Integer.MAX_VALUE);
                        to[0] = 1;
                    }
                    if (to[1] > w)                              // path of size 1
                        to[1] = w;

                    if (from != null) {                         // try to append paths from node [i,j]
                        int z = 0;
                        for (z = 1; z <= from[0]; z++) {
                            int nw = from[z] + w;               // new width
                            if (nw >= maxWidth) 
                                break;
                            if (to[z + 1] >= nw) {
                                to[z + 1] = nw;
                                maxLen = z;
                            } else break;
                        }
                        to[0] = Math.max(to[0], z);
                    }
                    rm = p;
                    if (rm - j == 1) 
                        break;
                }
                
                // if cannot continue longest path stored in node [i,j]
                // and it has enough size, offer it to result set
                if (from != null && maxLen < from[0] && from[0] >= minLcs - 1) 
                    found.offer(j - from[from[0]], j + 1, (double) from[0]);
            }
        }
        return found.toList();
    }
    
    
    
    private static class PositionHolder {
        
        SortedMap<Integer, ScoredMatch> byStart = new TreeMap<>();
        Map<Integer, ScoredMatch> byEnd = new HashMap<>();
        
        
        void offer(int start, int end, double score) {
            ScoredMatch bs = byStart.get(start);
            ScoredMatch be = byEnd.get(end);
            boolean add = false;
            if (bs != null) {
                // if new lcs is of bigger size or it has the same size but is shorter
                if (bs.score < score || (bs.score == score && end < bs.end)) {
                    byStart.remove(bs.start);
                    byEnd.remove(bs.end);
                    add = true;
                } 
            }
            if (be != null) {
                if (be.score < score || (be.score == score && start > be.start)) {
                    byStart.remove(be.start);
                    byEnd.remove(be.end);
                    add = true;
                }
            }
            // if previous pair is removed (add = true),
            // or there is no pair at given positions
            if ((bs == null && be == null) || add) {
                ScoredMatch p = new ScoredMatch(start, end, score);
                byStart.put(start, p);
                byEnd.put(end, p);
            }
        } 
        
        
        List<ScoredMatch> toList() {
            return byStart.values().stream().collect(Collectors.toList());
        }
        
    }    
    
}
