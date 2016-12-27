/**
 * Class that serializes the colleciton of neural networks used in the
 * online application
 * @author Lou Brand
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.*;

public class SerializePredictor{
    private ArrayList<IOTuple> trainingData;
    private ArrayList<NeuralNetwork> myVoters;
    private int epochs;
    private final double TOLERANCE = .5;
    private final int NUM_VOTERS = 21;
    
    /**
     * Constructor takes in a training set and a number of training cycles
     * @param data The training set (in this case all our compounds)
     * @param numEpochs Number of training (backpropagation) cycles
     */
    public SerializePredictor(ArrayList<IOTuple> data, int numEpochs){
        epochs = numEpochs;
        trainingData = data;
        
        Collections.shuffle(trainingData); //Randomize the order of our data
        createVoters();
        serialize();
    }
    
    /**
     * Creates a collection of neural networks that will "vote" on whether
     * they think a compound penetrates the blood-brain barrier
     */
    private void createVoters(){
        myVoters = new ArrayList<NeuralNetwork>();
        double stupification = 1/NUM_VOTERS; //We want to train each Neural Network with a handicap, this may have an effect on how well they work together as a team
        
        for (int i = 0; i < NUM_VOTERS; i++){
            NeuralNetwork currentNN = new NeuralNetwork();
            
            currentNN.initialize(trainingData.get(0));
            
            List<IOTuple> leftTrainSet = trainingData.subList(0, (int)(i*trainingData.size()*stupification));
            //Small subset of the data removed for stupification
            List<IOTuple> rightTrainSet = trainingData.subList((int)((i+1)*trainingData.size()*stupification), trainingData.size());
            
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
            
            myVoters.add(currentNN);
        }
    }
    
    /**
     * Save the collection of neural networks in a serialized object
     */
    private void serialize(){
        try{
            String serial = "BBBPredictor.ser";
            FileOutputStream fileOut = new FileOutputStream(serial);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(myVoters);
            out.close();
            fileOut.close();
            System.out.println("Serialized data is saved in " + serial);
        }catch(IOException i){i.printStackTrace();}
    }
}
