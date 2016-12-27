/**
 * Object used to find the optimal Schema for a NeuralNetwork using a
 * genetic algorithm
 * @author Lou Brand
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FindSchema{
    private ArrayList<IOTuple> myData;
    private int epochs, genEpochs, populationSize;
    
    private final double TOLERANCE = .5;
    private final int NUM_FOLDS = 25;
    private final int NUM_DATAPOINTS = 1;
    
    /**
     * Constructor starts the process of finding the optimal NeuralNetwork
     * Schema
     * @param data All the Input/Output pairs
     * @param geneticEpochs How many epochs the genetic algorithm will run
     * @param numEpochs How backpropagation cycles each NeuralNetwork will
     * be trained
     */
    public FindSchema(ArrayList<IOTuple> data, int geneticEpochs, int numEpochs, int popSize){
        genEpochs = geneticEpochs;
        epochs = numEpochs;
		populationSize = popSize;
        myData = data; 
        
        Collections.shuffle(myData);
        findBestSchema();   
    }
    
    /**
     * Uses a genetic algorithm to determine the best Schema
     * @return (Hopefully) the optimal Schema
     */
    public Schema bestSchema(){
        Collections.shuffle(myData);
        Population myPop = new Population(populationSize, myData);

        for (int j = 0; j < genEpochs; j++){
            System.out.println("Epoch: " + j);
            myPop.evolve();
        }
        
        Collections.sort(myPop.getIndividuals());
        NeuralNetwork best = myPop.getIndividuals().get(myPop.getIndividuals().size()-1);
        
        return best.getSchema();
    }
    
    /**
     * Prints out the Schema parameters to be put in our normal NeuralNetwork
     * algorithm (Majority voting architecture)
     */
    private void findBestSchema(){
        int numInTestSet = myData.size()/NUM_FOLDS;
        double avgPercentRight = 0.0;
        
        Schema bestStruct = bestSchema();
        
        System.out.println("Learning Rate: " + bestStruct.getLearningRate());
        System.out.println("Momentum: " + bestStruct.getMomentum());
        System.out.println("Hidden Ratio: " + bestStruct.getHiddenRatio());
        System.out.println("Tolerance: \u00B1" + TOLERANCE);
    }
}
