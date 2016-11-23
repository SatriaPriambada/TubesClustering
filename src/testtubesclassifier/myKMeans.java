/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testtubesclassifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import weka.classifiers.AbstractClassifier;
import weka.clusterers.AbstractClusterer;
import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.Capabilities;
import weka.core.DenseInstance;
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
public class myKMeans extends AbstractClusterer{
    private int numberCluster;
    private Instances finalCluster;
    int[] prevInstCluster;
    
    public myKMeans(int numCluster){
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
        
        FastVector values = new FastVector();
        Random randomGenerator = new Random();
        randomGenerator.setSeed(100);
        
        prevInstCluster = new int[numInstance];
        Instance[] seedCluster = new Instance[numberCluster];
        Instances[] centroidInstances = new Instances[numberCluster];
        //initate all prev cluster with not define (-999)
        for (int j = 0; j < numInstance; j++) {
            prevInstCluster[j] = -999;
        }
        ArrayList<Attribute> attrInfo = new ArrayList();
        for (int j = 0; j < finalCluster.numAttributes() - 1; j++) {
            attrInfo.add(finalCluster.attribute(j));
        }
        
//      //pick random initialization cluster
        for (int j = 0; j < numberCluster; j++) {
            values.addElement(String.valueOf(j));
            int initCenter = randomGenerator.nextInt(numInstance);
            seedCluster[j] = finalCluster.instance(initCenter);
            centroidInstances[j] = new Instances(String.valueOf(j), attrInfo ,numInstance);
            System.out.println("Centroid " + j + " is " + initCenter + finalCluster.instance(initCenter));
        }
        
        finalCluster.insertAttributeAt(new Attribute("Cluster", values), finalCluster.numAttributes());
        
        //adding the cluster value while not convergen using means of all cluster
        boolean convergent = false;
        
        while(!convergent){
            int convergentCounter = 0;
            
            for (int instIdx = 0; instIdx < numInstance; instIdx++){
                double smallestDist = 999999;
                int tempClusterIdx = 0;
                for (int centroidCounter = 0; centroidCounter < numberCluster; centroidCounter++) {

                    double tempDist = countDistance(finalCluster.instance(instIdx), seedCluster[centroidCounter]);
                            //finalCluster.instance(listCluster[centroidCounter]));

                    if (tempDist < smallestDist){
                        smallestDist = tempDist;
                        tempClusterIdx = centroidCounter;
                    }
                }
                //System.out.println("instance " + instIdx + " tempCluster " + tempClusterIdx);
                finalCluster.instance(instIdx).setValue(finalCluster.numAttributes()-1, String.valueOf(tempClusterIdx));
                //check wether there it is the first assignment or not
                if (prevInstCluster[instIdx] == -999){
                    prevInstCluster[instIdx] = tempClusterIdx;
                    centroidInstances[tempClusterIdx].add(finalCluster.instance(instIdx));
                } else {
                    //check wether there is different cluster if yes then continue looping if all cluster is equal then stop loop
                    if (prevInstCluster[instIdx] == tempClusterIdx){
                        convergentCounter++;
                    } else {
                        prevInstCluster[instIdx] = tempClusterIdx;
                        centroidInstances[tempClusterIdx].add(finalCluster.instance(instIdx));
                    }
                }
            }
            for (int j = 0; j < centroidInstances.length; j++) {
                seedCluster[j] = updateCentroid(centroidInstances[j]);
            }
            
            if (convergentCounter == numInstance){
                // all clustroid is the same then it is convergent
                convergent = true;
            }
        }
    }

    @Override
    public int numberOfClusters() throws Exception {
        return numberCluster;
    }

    public void printClusterResult(){
        int[] total = new int[numberCluster];
        for (int j = 0; j < numberCluster; j++) {
            total[j] = 0;
        }
        int index = 0;
        for (Instance i : finalCluster){
            //System.out.println(i.toString());
            for (int j = 0; j < numberCluster; j++) {
                //System.out.println(prevInstCluster[index] + " vs " + j);
                if (prevInstCluster[index] == j){
                    int value = total[j];
                    total[j] = ++value;
                    System.out.println(j + " total " + total[j] + "value" + value);
                }
            }
            index++;
        }
        
        for (int j = 0; j < numberCluster; j++) {
            System.out.println("Cluster " + j + ":" + total[j]);
        }
        
    }
    
    public Instance updateCentroid(Instances centroidCluster){
        double[] newMean = new double[centroidCluster.numAttributes()];
        for (int i = 0; i < centroidCluster.numAttributes()-2; i++) {
            newMean[i] = centroidCluster.meanOrMode(i);
        }
        Instance returnInstance = new DenseInstance(centroidCluster.numAttributes());
        for (int i = 0; i < newMean.length; i++) {
            returnInstance.setValue(centroidCluster.attribute(i), newMean[i]);
        }
        System.out.println("New centroid " + returnInstance);
        return returnInstance;
    }
    
    public double countDistance(Instance inst1, Instance inst2) throws Exception {
        double result = 0;
        EuclideanDistance DistFunction = new EuclideanDistance();
        DistFunction.setInstances(finalCluster);
        return DistFunction.distance(inst1, inst2);
    }
    
}
