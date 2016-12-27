/**
 * Class that tests the effectiveness of the voting neural network
 * architecture
 * @author Lou Brand
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SurveyTest{
    private ArrayList<IOTuple> allData;
    private List<IOTuple> testingData;
    private List<IOTuple> trainingData;
    private ArrayList<NeuralNetwork> myVoters;
    private int epochs;
    private final double TOLERANCE = .5;
    private int NUM_VOTERS;
    
    /**
     * Creates an object to test the effectiveness of a neural network
     * voting architecture
     * @param data The data we will be testing on
     * @param numEpochs the number of training (backpropagation) cycles
     */
    public SurveyTest(ArrayList<IOTuple> data, int numEpochs){
        epochs = numEpochs;
        allData = data;
        test();
        //testVotersAndWrite();
        //testEpochsAndWrite();
    }

    /**
     * Basic test of the voting architecture
     */
    private void test(){
        NUM_VOTERS = 25;
        double percentSetAside = 1/(double)NUM_VOTERS; //When NUM_VOTERS = 20, 5% of data is set aside
        int setAside = (int)(percentSetAside * allData.size());
        
        for (int i = 0; i < 100; i++){
            Collections.shuffle(allData); //Randomize the order of our data
            testingData = allData.subList(0, setAside); //Set aside 5% of the data for testing purposes
            trainingData = allData.subList(setAside, allData.size());
            createVoters(epochs);
            survey();
        }
    }

    /**
     * Tests the effect of the number of voters on the voting architecture
     */
    private void testVotersAndWrite(){
        try{
            File myFile = new File("SurveyTest");
            myFile.createNewFile();
            FileWriter myFW = new FileWriter(myFile);
            BufferedWriter myBW = new BufferedWriter(myFW);
            for (int k = 5; k < 40; k++){
                NUM_VOTERS = k;
                double percentSetAside = 1/(double)NUM_VOTERS; //When NUM_VOTERS = 20, 5% of data is set aside
                int setAside = (int)(percentSetAside * allData.size());
                
                for (int i = 0; i < 100; i++){
                    Collections.shuffle(allData); //Randomize the order of our data
                    testingData = allData.subList(0, setAside); //Set aside 5% of the data for testing purposes
                    trainingData = allData.subList(setAside, allData.size());
                    createVoters(epochs);
                    double accuracy = survey();
                    myBW.write("" + NUM_VOTERS + "\t" + accuracy);
                    myBW.newLine();
                }
            }
            myBW.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    
    /**
     * Tests the effect of the number of training (backpropagaion) cycles
     * on the voting architecture
     */
    private void testEpochsAndWrite(){
        try{
            File myFile = new File("SurveyTest");
            myFile.createNewFile();
            FileWriter myFW = new FileWriter(myFile);
            BufferedWriter myBW = new BufferedWriter(myFW);
            
            NUM_VOTERS = 9; //Arbitrarily chosen
            double percentSetAside = 1/(double)NUM_VOTERS;
            int setAside = (int)(percentSetAside * allData.size());
            
            for (int k = 1; k < 600; k = 2*k){
                for (int i = 0; i < 1; i++){
                    Collections.shuffle(allData); //Randomize the order of our data
                    testingData = allData.subList(0, setAside); //Set aside 5% of the data for testing purposes
                    trainingData = allData.subList(setAside, allData.size());
                    createVoters(k);
                    double accuracy = survey();
                    myBW.write("" + k + "\t" + accuracy);
                    myBW.newLine();
                }
            }
            myBW.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Creates the collection of neural networks to be used for voting
     * @param epo Number of training cycles for each neural network
     */
    private void createVoters(int epo){
        epochs = epo;
        myVoters = new ArrayList<NeuralNetwork>();
        double stupification = 1/NUM_VOTERS; //We want to train each Neural Network with a handicap, this may have an effect on how well they work together as a team
        
        for (int i = 0; i < NUM_VOTERS; i++){
            NeuralNetwork currentNN = new NeuralNetwork();
            
            currentNN.initialize(allData.get(0));
            
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
     * Tests how well the voting architecture works
     * @return percentRight The percentage of right predictions from our
     * neural network voting architecture
     */
    private double survey(){
        int numRight = 0;
        int numWrong = 0;
        
        int truePositive = 0;
        int falsePositive = 0;
        int trueNegative = 0;
        int falseNegative = 0;
        
        for (IOTuple testingIO : testingData){
            int yes = 0; //Yes this compound passes through the BBB
            int no = 0; //No, this compound doesn't pass through the BBB
            for (NeuralNetwork currentNN : myVoters){
                currentNN.newIO(testingIO);
                currentNN.feedForward();
                double calculated = currentNN.getCalculatedValue();
                if (calculated >= .5){
                    yes++;
                } else {
                    no++;
                }
            }
            
            double expected = testingIO.getOutput().getOutputs().get(0);
            
            if (yes > no){
                if (expected == 1.0){
                    numRight++;
                    truePositive++;
                } else {
                    numWrong++;
                    falsePositive++;
                }
            } else{
                if (expected == 0.0){
                    numRight++;
                    trueNegative++;
                } else {
                    numWrong++;
                    falseNegative++;
                }
            }
        }
        
        System.out.println("We got " + numRight + " out of " + testingData.size());
	    double percentRight = (double)numRight/(double)testingData.size()*100;
        System.out.println(percentRight + "% Right");
        System.out.println("Num Right: " + numRight);
        
        // Sensitivity and Specificity Datapoints
        /*System.out.println("Positive Predictive Value: " + (double)truePositive/(double)(truePositive + falsePositive));
        System.out.println("Negative Predictive Value: " + (double)trueNegative/(double)(trueNegative + falseNegative));
        System.out.println("Sensitivity (to going through BBB): " + (double)truePositive/(double)(truePositive + falseNegative));
        System.out.println("Specificity (to not going through BBB): " + (double)trueNegative/(double)(trueNegative + falsePositive));*/
        //System.out.println((double)truePositive/(double)(truePositive + falsePositive) + " " +(double)trueNegative/(double)(trueNegative + falseNegative) + " " + (double)truePositive/(double)(truePositive + falseNegative) + " " + (double)trueNegative/(double)(trueNegative + falsePositive));
	    
        return percentRight;
    }
}
