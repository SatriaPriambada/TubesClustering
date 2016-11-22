/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testtubesclassifier;

import weka.classifiers.AbstractClassifier;
import weka.clusterers.AbstractClusterer;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

/**
 *
 * @author Satria
 */
public class myKMeans extends AbstractClusterer{
    private int numberCluster;
    private Instances finalCluster;
    
    public myKMeans(int numCluster){
        numberCluster = numCluster;
    }
    @Override
    public void buildClusterer(Instances i) throws Exception {
        //initalization
        finalCluster = i ;
        
        getCapabilities().testWithFail(i);
        
        //replace missing value
        Filter replaceMisVal = new ReplaceMissingValues();
        finalCluster = Filter.useFilter(i, replaceMisVal);
        
        //pick random initialization cluster
        
        //adding the cluster value while not convergen using means of all cluster
        FastVector values = new FastVector();
        //values.addElement("A");
        
        finalCluster.insertAttributeAt(new Attribute("Cluster", values), finalCluster.numAttributes());
    
    }

    @Override
    public int numberOfClusters() throws Exception {
        return numberCluster;
    }

    public void printClusterResult(){
        for (Instance i : finalCluster){
            System.out.println(i.toString());
        }
    }
    
}
