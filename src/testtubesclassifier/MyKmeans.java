/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testtubesclassifier;

import java.util.Random;
import weka.classifiers.AbstractClassifier;
import weka.clusterers.AbstractClusterer;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

/**
 *
 * @author Satria
 */
public class MyKmeans extends AbstractClusterer{
    private int numberCluster;
    private Instances finalCluster;
    
    public MyKmeans(int numCluster){
        numberCluster = numCluster;
    }
    @Override
    public void buildClusterer(Instances i) throws Exception {
        //initalization
        finalCluster = i ;
        int numInstance = finalCluster.numInstances();
        
        getCapabilities().testWithFail(i);
                
        //replace missing value
        //finalCluster.deleteWithMissingClass();
        
        
//      //pick random initialization cluster
        FastVector values = new FastVector();
        Random randomGenerator = new Random();
        randomGenerator.setSeed(100);
        
        int[] listCluster = new int[numberCluster];
        int[] prevCluster = new int[numInstance];
        for (int j = 0; j < numberCluster; j++) {
            values.addElement(String.valueOf(j));
            int initCenter = randomGenerator.nextInt(numInstance);
            listCluster[j] = initCenter;
            System.out.println("Centroid " + j + " is " + initCenter + finalCluster.instance(j));
        }
        
        finalCluster.insertAttributeAt(new Attribute("Cluster", values), finalCluster.numAttributes());
        
        //adding the cluster value while not convergen using means of all cluster
        
        
        for (int instIdx = 0; instIdx < finalCluster.numInstances(); instIdx++){
            double smallestDist = 999999;
            int tempClusterIdx = 0;
            for (int centroidCounter = 0; centroidCounter < numberCluster; centroidCounter++) {
                
                double tempDist = countDistance(finalCluster.instance(instIdx), finalCluster.instance(listCluster[centroidCounter]));
                
                if (tempDist < smallestDist){
                    smallestDist = tempDist;
                    tempClusterIdx = centroidCounter;
                }
            }
            System.out.println("instance " + instIdx + " tempCluster " + tempClusterIdx);
            finalCluster.instance(instIdx).setValue(finalCluster.numAttributes()-1, String.valueOf(tempClusterIdx));
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
    }
    
    public double countDistance(Instance inst1, Instance inst2) throws Exception {
        double result = 0;
        EuclideanDistance DistFunction = new EuclideanDistance();
        DistFunction.setInstances(finalCluster);
        for (int i = 0; i < inst1.numAttributes() - 1; i++) {
            if (inst1.attribute(i).type() == 0){
                //System.out.print("attribut " + i + " numeric ");
                
                //System.out.println(inst1);
                //System.out.println(inst2);
                result = result + DistFunction.distance(inst1, inst2);
            } else {
                //System.out.print("attribut " + i + " nominal ");
                if (inst1.value(i) == inst2.value(i)){
                    //System.out.println(inst1.value(i));
                    //System.out.println(inst2.value(i));
                } else {
                    result = result + 1;
                }
            }
        }
        System.out.println();
        System.out.println("result " + result);
        return result;
    }
    
}
