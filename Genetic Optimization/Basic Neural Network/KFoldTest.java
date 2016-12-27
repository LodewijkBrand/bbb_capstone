/**
 * Class used for testing the effectiveness of a neural network using a
 * K-Fold Test
 * @author Lou Brand
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class KFoldTest{
    private ArrayList<IOTuple> myData;
    private int epochs;
    private final double TOLERANCE = .5; //95% accuracy
    private final int NUM_FOLDS = 25;
    private final int NUM_DATAPOINTS = 1;
    
    /**
     * Creates a KFoldTest object with a 
     * @param data The dataset of interest
     * @param numEpochs Number of training (backpropagation) cycles
     */
    public KFoldTest(ArrayList<IOTuple> data, int numEpochs){
        epochs = numEpochs;
        myData = data; 
        
        for (int i = 0; i < NUM_DATAPOINTS; i++){
            Collections.shuffle(myData);
            kFold();
        }
    }
    
    /**
     * Trains a neural network with different subsets of our dataset and
     * tests the accuracy
     */
    private void kFold(){
        int numInTestSet = myData.size()/NUM_FOLDS;
        double avgPercentRight = 0.0;
        
        for (int i = 0; i < NUM_FOLDS; i++){
            NeuralNetwork currentNN = new NeuralNetwork();
            
            currentNN.initialize(myData.get(0)); //Initialize the NeuralNetwork with a piece of data. (This will be changed later)
            
            List<IOTuple> leftTrainSet = myData.subList(0, i*numInTestSet);
            List<IOTuple> testSet = myData.subList(i*numInTestSet, (i+1)*numInTestSet);
            List<IOTuple> rightTrainSet = myData.subList((i+1)*numInTestSet, myData.size());
            
            ArrayList<IOTuple> trainingSet = new ArrayList<IOTuple>();
            trainingSet.addAll(leftTrainSet);
            trainingSet.addAll(rightTrainSet);
            
            for (int j = 0; j < epochs; j++){
                //System.out.println("Epoch: " + j);
                for (IOTuple trainingIO : trainingSet){
                    currentNN.newIO(trainingIO);
                    currentNN.feedForward();
                    currentNN.backProp();
                }
            }
            
            double sumSquaredError = 0.0;
            int numRight = 0;
            int falsePositive = 0;
            int falseNegative = 0;
            int numWrong = 0;
            
            for (IOTuple testingIO : testSet){
                currentNN.newIO(testingIO);
                currentNN.feedForward();
                double calculated = currentNN.getCalculatedValue(); 
                double expected = currentNN.getExpectedOutput();
                System.out.println("Expected: " + expected + " Calculated: " + calculated);
                
                double error = Math.abs(calculated - expected);
                if (error < TOLERANCE){
                    numRight++;
                } else {
                    numWrong++;
                    if (expected == 1.0){
                        falseNegative++;
                    }
                    if (expected == 0.0){
                        falsePositive++;
                    }
                }
            }
            
            double percentRight = 100.0 * (double)numRight/(double)testSet.size();
            
            //System.out.println("Fold " + (i+1) + ", has a mean SSE of: " + sumSquaredError/(double)testSet.size());
            System.out.println("The NeuralNetwork is right " + percentRight + "% of the time.");
            System.out.println("The NeuralNetwork was wrong " + numWrong + " times");
            System.out.println("False Positives: " + falsePositive);
            System.out.println("False Negatives: " + falseNegative);
            
            avgPercentRight += percentRight;
        }
        
        System.out.println("The NeuralNetwork was correct on average " + avgPercentRight/(double)NUM_FOLDS + "% of the time.");
        System.out.println("Learning Rate: 0.7");
        System.out.println("Momentum: 0.5");
        System.out.println("Hidden Ratio: 0.5");
        System.out.println("Tolerance: \u00B1" + TOLERANCE);
    }
}
