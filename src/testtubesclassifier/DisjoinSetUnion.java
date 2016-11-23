/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testtubesclassifier;

/**
 *
 * @author Luqman A. Siswanto
 */
public class DisjoinSetUnion {
    private int[] par = null;
    private int[] size = null;
    
    public DisjoinSetUnion(int n) {
        par = new int[n];
        size = new int[n];
        for (int i = 0; i < n; i++) {
            par[i] = i;
            size[i] = 1;
        }
    }
    
    public int find(int u) {
        return par[u] = (par[u] == u? u : find(par[u]));
    }
    
    public void merge(int u, int v) {
        size[find(v)] += size[find(u)];
        par[find(u)] = find(v);
    }
    
    public int getSize(int u) {
        return size[find(u)];
    }
    
}
