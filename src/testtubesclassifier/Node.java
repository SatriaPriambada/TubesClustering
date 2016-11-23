/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testtubesclassifier;

import java.util.ArrayList;
import java.util.List;
 
public class Node {
    private boolean isLeaf;
    private final List<Node> children = new ArrayList<>();
    private String caption;
    
    public Node() {}
    
    public Node(boolean isLeaf, String caption) {
        this.isLeaf = isLeaf;
        this.caption = caption;
    }
    
    public String getCaption() {
        return this.caption;
    }
    
    public void setCaption(String caption) {
        this.caption = caption;
    }
    
    public void addChild(Node child) {
        children.add(child);
    }
    
    public void printRoot() {
        List<Integer> at = new ArrayList<>();
        at.add(0);
        for (int i = 0; i < this.children.size(); i++) {
            this.children.get(i).print(cloneList(at), false, i + 1 == this.children.size());
        }
    }
    
    public void print(List<Integer> at, boolean first, boolean last) {
        int pol = (at.isEmpty()? 0 : at.get(at.size() - 1) + 1);
        if (!first) {
            int j = 0;
            for (int i = 0; i < pol; i++) {
                if (i == at.get(j)) {
                    System.out.print("|");
                    j++;
                } else {
                    System.out.print(" ");
                }
            }
        }
        if (last && !at.isEmpty()) {
            at.remove((int) at.size() - 1);
        }
        System.out.print("___");
        pol += 2;
        if (this.isLeaf) {
            System.out.println(" " + caption);
            return;
        }
        at.add(pol);
        for (int i = 0; i < this.children.size(); i++) {
            this.children.get(i).print(cloneList(at), i == 0, i + 1 == this.children.size());
        }
    }
    
    public List<Integer> cloneList(List<Integer> list) {
        List<Integer> ret = new ArrayList<>();
        for (Integer i : list) {
            ret.add(i);
        }
        return ret;
    }
    
}
