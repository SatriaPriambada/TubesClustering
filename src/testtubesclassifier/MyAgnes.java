/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testtubesclassifier;

import weka.clusterers.AbstractClusterer;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

/**
 *
 * @author Satria
 */
public class MyAgnes extends AbstractClusterer{
    private int numberCluster;
    private Instances finalCluster;
    private Node DendogramRoot = null;
    
    public MyAgnes(int numCluster){
        numberCluster = numCluster;
    }
    
    @Override
    public void buildClusterer(Instances i) throws Exception {
        //initalization
        finalCluster = i ;
        
        getCapabilities().testWithFail(i);
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

        //count the distance matrix
        
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
