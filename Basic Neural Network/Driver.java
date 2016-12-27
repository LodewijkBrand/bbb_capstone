/**
 * The main Driver method of the Neural Network
 * Can be used for testing purposes or creating a serialized NeuralNetwork
 * for the application.
 * @author Lou Brand
 */

import java.util.ArrayList;
import java.io.*;

public class Driver{
    ArrayList<IOTuple> inputOutputTuples;
    private final int EPOCHS = 200; //Determine the number of epochs to train Neural Network
    
	public static void main (String[] args){
        Driver myDriver = new Driver("pybelSmiles.ser");
        //myDriver.kFoldTest();
        myDriver.surveyTest();
        //myDriver.serializePredictor();
	}
    
    /**
     * Reads in the serialized IOTuples 
     */
    public Driver(String ioFilename){
        try{
            InputStream file = new FileInputStream(ioFilename);
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream (buffer);
            inputOutputTuples = (ArrayList<IOTuple>)input.readObject();
        }
        catch(ClassNotFoundException ex){System.out.println("The class wasn't found!");}
        catch(IOException ex){System.out.println("There was an error!");}
    }
    
    /**
     * Create a KFoldTest object and performs a K-Fold Test with IOTuples
     *  read in
     */
    private void kFoldTest(){
        KFoldTest myTest = new KFoldTest(inputOutputTuples, EPOCHS);
    }
    
    /**
     * Creates a collection of NeuralNetwork objects and tests performance 
     * with a majority vote
     */
    private void surveyTest(){
        SurveyTest myTest = new SurveyTest(inputOutputTuples, EPOCHS);
    }
    
    /**
     * Serializes the collection of NeuralNetwork objects for the online
     * application
     */
    private void serializePredictor(){
        SerializePredictor myPredictor = new SerializePredictor(inputOutputTuples, EPOCHS);
    }
}

//Testing Materials

/*IOTuple initialTuple = inputOutputTuples.get(0);

NeuralNetwork myNeuralNetwork = new NeuralNetwork();
myNeuralNetwork.initialize(initialTuple);

for (int k = 0; k < EPOCHS; k++){
    for (int i = 0; i < inputOutputTuples.size() - 20; i++){
        myNeuralNetwork.newIO(inputOutputTuples.get(i));
        myNeuralNetwork.feedForward();
        myNeuralNetwork.backProp();
    }
}

for (int j = inputOutputTuples.size()-20; j < inputOutputTuples.size(); j++){
    myNeuralNetwork.newIO(inputOutputTuples.get(j));
    double expected = myNeuralNetwork.getExpectedOutput();
    myNeuralNetwork.feedForward();
    System.out.println("Calculated: " + myNeuralNetwork.getCalculatedValue() + "\t Expected: " + expected);
}*/
