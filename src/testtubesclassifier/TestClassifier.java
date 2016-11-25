/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testtubesclassifier;

import java.io.File;
import java.util.Random;
import java.util.Scanner;
import weka.classifiers.Evaluation;
import static weka.clusterers.AbstractClusterer.runClusterer;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.core.converters.ConverterUtils.DataSource;

/**
 *
 * @author Satria
 */
public class TestClassifier {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Enter Type (myKMeans/myAgnes)");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        
        System.out.println("Enter number of cluster");
        int numCluster = Integer.parseInt(sc.nextLine());
        
        int correctPrediction = 0;
        double accuracy = 0.0f;
        
        long startTime = System.currentTimeMillis();

        // Training
        DataSource dt = new DataSource("sip.arff");
        Instances trainDataset = dt.getDataSet();
        trainDataset.setClassIndex(trainDataset.numAttributes()-1);

        // Testing
        DataSource dtest = new DataSource("weather.nominal.test.arff");
        Instances testDataset = dtest.getDataSet();
        testDataset.setClassIndex(testDataset.numAttributes()-1);
        
        String[] options = new String[40];
        options[0] = "-t dt";
        if (input.equals("myKMeans")){
            MyKmeans classifierKMeans = new MyKmeans(numCluster);
            classifierKMeans.buildClusterer(trainDataset);
            classifierKMeans.printClusterResult();
        } else if (input.equals("myAgnes")){
            MyAgnes classifierAgnes = new MyAgnes(numCluster);
            classifierAgnes.buildClusterer(trainDataset);
            classifierAgnes.printClusterResult();
        }
        
        long stopTime = System.currentTimeMillis();
        System.out.println("Elapsed time was " + (stopTime - startTime) + " miliseconds.");
    }
}
