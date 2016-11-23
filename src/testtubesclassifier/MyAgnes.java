/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testtubesclassifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import weka.clusterers.AbstractClusterer;
import weka.core.Attribute;
import weka.core.EuclideanDistance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class MyAgnes extends AbstractClusterer{
    private int numberCluster;
    private Instances finalCluster;
    private Node root = null;
    private DisjoinSetUnion dsu = null;
    private Node[] ori = null;
    
    private class Item implements Comparable<Item> {
        double dist;
        int index_i;
        int index_j;
        
        Item(double dist, int index_i, int index_j) {
            this.dist = dist;
            this.index_i = index_i;
            this.index_j = index_j;
        }
        
        @Override
        public int compareTo(Item other) {
            double d = this.dist - other.dist;
            if (d < 0) {
                return -1;
            } else if (d > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }
    
    public MyAgnes(int numCluster){
        numberCluster = numCluster;
    }
    
    @Override
    public void buildClusterer(Instances instances) throws Exception {
        //initalization
        finalCluster = instances;
        
        getCapabilities().testWithFail(instances);
        
        //replace missing value
        finalCluster.deleteWithMissingClass();
        
        //adding the cluster value while not convergen using means of all cluster
        FastVector values = new FastVector();
        for (int j = 0; j < numberCluster; j++) {
            values.addElement(String.valueOf(j));
        }
        finalCluster.insertAttributeAt(new Attribute("Cluster", values), finalCluster.numAttributes());
        
        List<Item> edges = new ArrayList<>();
        EuclideanDistance euclidDistance = new EuclideanDistance();
        euclidDistance.setInstances(finalCluster);
        for (int i = 0; i < finalCluster.numInstances(); i++) {
            for (int j = i + 1; j < finalCluster.numInstances(); j++) {
                double d = euclidDistance.distance(finalCluster.instance(i), finalCluster.instance(j));
                edges.add(new Item(d, i, j));
            }
        }
        Collections.sort(edges);
        
        Node[] nodes = new Node[finalCluster.numInstances()];
        ori = new Node[finalCluster.numInstances()];
        for (int i = 0; i < finalCluster.numInstances(); i++) {
            nodes[i] = new Node(true, finalCluster.instance(i).toString());
            ori[i] = nodes[i];
        }
        
        dsu = new DisjoinSetUnion(finalCluster.numInstances());
        int clusterNow = finalCluster.numInstances();
        for (Item item : edges) {
            //System.out.println(item.dist + " " + item.index_i + " " + item.index_j);
            if (dsu.find(item.index_i) != dsu.find(item.index_j)) {
                Node a = nodes[dsu.find(item.index_i)];
                Node b = nodes[dsu.find(item.index_j)];
                dsu.merge(item.index_i, item.index_j);
                Node dad = new Node(false, "");
                dad.addChild(a);
                dad.addChild(b);
                nodes[dsu.find(item.index_i)] = dad;
                //System.out.println("merge " + item.index_i + " " + item.index_j + " " + dsu.getSize(item.index_i));
                clusterNow--;
            }
            if (clusterNow <= numberCluster) break;
        }
        
        boolean[] done = new boolean[finalCluster.numInstances()];
        this.root = new Node(false, "");
        for (int i = 0; i < finalCluster.numInstances(); i++) {
            int root = dsu.find(i);
            if (!done[root]) {
                done[root] = true;
                this.root.addChild(nodes[root]);
            }
        }
    }

    @Override
    public int numberOfClusters() throws Exception {
        return numberCluster;
    }

    public void printClusterResult(){
        boolean[] done = new boolean[finalCluster.numInstances()];
        int[] ptCluster = new int[finalCluster.numInstances()];
        int pt = 0;
        for (int i = 0; i < finalCluster.numInstances(); i++) {
            int root = dsu.find(i);
            if (!done[root]) {
                done[root] = true;
                ptCluster[root] = pt++;
            }
            finalCluster.instance(i).setValue(finalCluster.numAttributes()-1, String.valueOf(ptCluster[root]));
        }
        for (int i = 0; i < finalCluster.numInstances(); i++) {
            ori[i].setCaption(finalCluster.instance(i).toString());
        }
        
        List<Integer> at = new ArrayList<>();
        root.printRoot();
        
        done = new boolean[finalCluster.numInstances()];
        pt = 0;
        for (int i = 0; i < finalCluster.numInstances(); i++) {
            int root = dsu.find(i);
            if (!done[root]) {
                ptCluster[root] = pt;
                done[root] = true;
                int size = dsu.getSize(i);
                double percent = (double) size * 100 / finalCluster.numInstances();
                System.out.println(String.format("Cluster %d: %.2f%% (%d/%d)", pt++, percent, size, finalCluster.numInstances()));
            }
        }
    }
    
}
