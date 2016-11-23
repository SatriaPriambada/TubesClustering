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
    private Node DendogramRoot = null;
    
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
        DendogramRoot = new Node(null);
        DendogramRoot.setId("root");
        
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
        
        DisjoinSetUnion dsu = new DisjoinSetUnion(finalCluster.numInstances());
        int clusterNow = finalCluster.numInstances();
        for (Item item : edges) {
            //System.out.println(item.dist + " " + item.index_i + " " + item.index_j);
            if (dsu.find(item.index_i) != dsu.find(item.index_j)) {
                dsu.merge(item.index_i, item.index_j);
                clusterNow--;
            }
            if (clusterNow <= numberCluster) break;
        }
        
        boolean[] done = new boolean[finalCluster.numInstances()];
        int[] ptCluster = new int[finalCluster.numInstances()];
        int pt = 0;
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
        for (int i = 0; i < finalCluster.numInstances(); i++) {
            int root = dsu.find(i);
            finalCluster.instance(i).setValue(finalCluster.numAttributes()-1, String.valueOf(ptCluster[root]));
        }
    }

    @Override
    public int numberOfClusters() throws Exception {
        return numberCluster;
    }

    public void printClusterResult(){
        for (Instance i : finalCluster){
            System.out.println(i.toString());
        }
        System.out.println("Dendogram :");
        printTree(DendogramRoot, "|____");
    }
    
     
    private static Node addChild(Node parent, String id) {
        Node node = new Node(parent);
        node.setId(id);
        parent.getChildren().add(node);
        return node;
    }

    private static void printTree(Node node, String appender) {
        System.out.println(appender + node.getId());
        for (Node each : node.getChildren()) {
            printTree(each, appender + appender);
        }
    }
}
