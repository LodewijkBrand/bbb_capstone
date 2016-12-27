/**
 * Application level Blood-Brain Barrier predictor
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

public class Predictor{
    private ArrayList<NeuralNetwork> myVoters;
    private ArrayList<Double> inputs;
    
    /**
     * Given a list of input parameters predict whether or not a compound
     * crosses the blood-brain barrier
     * @param args Input parameters calculated from Pybel in Cinfony
     */
    public static void main(String[] args){
        Predictor myPredictor = new Predictor(args);
    }
    
    /**
     * Constructor determines whether or not a compounds crosses the blood-
     * brain barrier
     * @param args Input parameters calculated from Pybel in Cinfony
     */
    public Predictor(String[] args){
        try{
            InputStream file = new FileInputStream("BBBPredictor.ser");
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream (buffer);
            myVoters = (ArrayList<NeuralNetwork>)input.readObject();
        }
        catch(ClassNotFoundException ex){System.out.println("The class wasn't found!");}
        catch(IOException ex){System.out.println("There was an error!");}
                
        IOTuple smilesInput = createIO(args);
        
        if (predict(smilesInput)){
            System.out.println("yes");
        } else{
            System.out.println("no");
        }
    }
    
    /**
     * Creates an IOTuple from a set of inputs
     * @param inputs The input parameters
     * @return currentIO The parameters of the compound
     */
    private IOTuple createIO(String[] inputs){
        Input currentInput = processInput(inputs);
        Output currentOutput = new Output(null);
        IOTuple currentIO = new IOTuple(currentInput, currentOutput);
        
        return currentIO;
    }
    
    /**
     * Process the set of input parameters correctly
       @param The input parameters calculated in Pybel
     * @return currentInput Correctly normalized input
     */
    private Input processInput(String[] smilesInputs){
        inputs = new ArrayList<Double>();
        
        for (String input : smilesInputs){
            inputs.add(Double.parseDouble(input));
        }
        
        Input currentInput = new Input(inputs);
        normalizeInput(currentInput);
        
        return currentInput;
    }
    
    /**
     * Normalizes the Input based on our dataset's maximum and minimum values
     * @param currentInput The input (not normalized) parameters
     */
    private void normalizeInput(Input currentInput){
        ArrayList<Double> inputMax = new ArrayList<Double>();
        ArrayList<Double> inputMin = new ArrayList<Double>();
        String delimiter = " ";
        String lineIn;
        
        try{
            BufferedReader br = new BufferedReader(new FileReader("inputMaxMin.txt"));
            while((lineIn = br.readLine()) != null){
                String[] splitMaxMin = lineIn.split(delimiter);
                inputMin.add(Double.parseDouble(splitMaxMin[0]));
                inputMax.add(Double.parseDouble(splitMaxMin[1]));
            }
        } catch (FileNotFoundException e){e.printStackTrace();}
        catch (IOException e){e.printStackTrace();}
        
        for (int i = 0; i < inputMax.size(); i++){ 
            currentInput.normalizeValue(inputMin.get(i), inputMax.get(i), i);
        }
    }
    
    /**
     * Predicts whether or not a compound enters the brain using a
     * collection of pre-trained neural networks (Majority vote)
     * @return True = Yes, False = No
     */
    private boolean predict(IOTuple query){
        int yes = 0; 
        int no = 0;
        
        for (NeuralNetwork currentNN : myVoters){
            currentNN.newIO(query);
            currentNN.feedForward();
            double calculated = currentNN.getCalculatedValue();
            
            if (calculated >= .5){
                yes++;
            } else{
                no++;
            }
        }
        
        if (yes > no){
            return true;
        } else{
            return false;
        }
    }
}
