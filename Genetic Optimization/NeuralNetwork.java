/**
 * This object represents a Neural Network. This version aims to create
 * an optimal Schema configuration. 
 * @author Lou Brand
 * @author Matthew Dickinson
 */

import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

public class NeuralNetwork implements Comparable<NeuralNetwork>{
    private InputLayer myInput;
    private HiddenLayer myHidden;
    private OutputLayer myOutput;
    private ArrayList<IOTuple> myTrainingSet;
    private Schema mySchema;
    private final double TOLERANCE = .5;

    /**
     * Initializes a NeuralNetwork with a Schema object that will be
     * optimized with a genetic algorithm
     */
    public NeuralNetwork(ArrayList<IOTuple> trainingSet, Schema structure){
        myTrainingSet = trainingSet;
        mySchema = structure;
        initialize();
    }

    /**
     * Adds a new Input and Output object into the NeuralNetwork for training and testing purposes
     * Remember: This keeps the integrity of the edges!
     * @param newInput The next Input
     * @param newOutput The next Output
     */
    public void newIO(IOTuple newIO){
        Input newInput = newIO.getInput();
        Output newOutput = newIO.getOutput();

        ArrayList<Neuron> inputNeurons = myInput.getNeurons();
        for (int i = 0; i < inputNeurons.size()-1; i++){		// The minus-one is to not change the BIAS!!!!!!!!
            Neuron current = inputNeurons.get(i);
            double newValue = newInput.getInputs().get(i);
            current.setValue(newValue);
        }
        myOutput.setExpectedOutputs(newOutput);
    }


    /**
     * Initializes the NeuralNetwork's Synapses between each Layer based on the Schema
     */
    private void initialize(){
        Input initialInput = myTrainingSet.get(0).getInput();
        Output initialOutput = myTrainingSet.get(0).getOutput();

        myInput = new InputLayer(initialInput);
        myHidden = new HiddenLayer((int) (initialInput.size() * mySchema.getHiddenRatio()));
        myOutput = new OutputLayer(initialOutput);
        
        for (Neuron inputNeuron : myInput.getNeurons()){
            for (Neuron hiddenNeuron : myHidden.getNeurons()){
                Synapse inputToHidden = new Synapse(inputNeuron, hiddenNeuron);
            }
        }
        for (Neuron hiddenNeuron : myHidden.getNeurons()){
            for (Neuron outputNeuron : myOutput.getNeurons()){
                Synapse hiddenToOutput = new Synapse(hiddenNeuron, outputNeuron);
            }
        }
    }

    /**
     * Feeds the InputLayer through the NeuralNetwork and calculates the OutputLayer
     */
    public void feedForward(){
        for (Neuron hidden : myHidden.getNeurons()){
            ArrayList<Synapse> edges = hidden.getInputSynapses();
            double currentSum = 0.0;
            for (Synapse current : edges){
                currentSum += current.getWeight() * current.getOrigin().getValue();
            }
            hidden.setValue(sigmoid(currentSum));
        }
        for (Neuron output : myOutput.getNeurons()){
            ArrayList<Synapse> edges = output.getInputSynapses();
            double currentSum = 0.0;
            for (Synapse current : edges){
                currentSum += current.getWeight() * current.getOrigin().getValue();
            }
            output.setValue(sigmoid(currentSum));
        }
    }

    /**
    * The back-propagation algorithm. This is how the NeuralNetwork learns!
    * x = Input Value
    * w1 = Synapse #1
    * w2 = Synapse #2
    * d = Desired Output
    * z = Calculated Output (via Neural Net)
    * P = Performance Function = -1/2 * (d - z) ^ 2
    * We want to maximize P. By changing the weights w1 and w2.
    * Follow below:
    * Change in w* = learningRate * dP/dw*
    * Schematic: x * w1 = p1 --> sigmoid(sum) = y * w2 = p2 --> sigmoid(sum) = z --> -1/2 * (d - z) ^ 2 = P
    * Then: dP/dw2 = dP/dz * dz/dw2 = (d - z) * dz/dp2 * dp2/dw2 = (d - z) * dz/dp2 * y
    * And : dP/dw1 = dP/dz * dp/dw1 = (d - z) * dz/dp2 * dp2/dw1 = (d - z) * dz/dp2 * dp2/dy * dy/dw1 = (d - z) * dz/dp2 * w2 * dy/dp1 * x
    * At the end: dP/dw2 = (d - z) * z * (1 - z) * y
    * 			   dP/dw1 = (d - z) * z * (1 - z) * w2 * y * (1 - y) * x
    */
    public void backProp(){
        double LEARNING_RATE = mySchema.getLearningRate();
        double MOMENTUM = mySchema.getMomentum();
        
        for (int i = 0; i < myOutput.size(); i++){
            Neuron outputNeuron = myOutput.get(i);
            double z = outputNeuron.getValue();			//Calculated Output
            double d = myOutput.getExpectedOutput(i);	//Desired Output
            for (Synapse w2Synapse : outputNeuron.getInputSynapses()){
                double w2 = w2Synapse.getWeight();	//Second tier Synapse weight
                double y = w2Synapse.getOrigin().getValue();	//Hidden Neuron value
                double dPdw2 = (d - z) * z * (1.0 - z) * y;
                for (Synapse w1Synapse : w2Synapse.getOrigin().getInputSynapses()){
                    double w1 = w1Synapse.getWeight();			//First tier Synapse weight
                    double x = w1Synapse.getOrigin().getValue();	//Input Neuron value
                    double dPdw1 = (d - z) * z * (1.0 - z) * y * w2 * (1.0 - y) * x;
                    w1Synapse.setWeight(w1 + dPdw1 * LEARNING_RATE + w1Synapse.getMomentum() * MOMENTUM);
                    w1Synapse.setMomentum(dPdw1);
                }
                w2Synapse.setWeight(w2 + dPdw2 * LEARNING_RATE + w2Synapse.getMomentum() * MOMENTUM);
                w2Synapse.setMomentum(dPdw2);
            }
        }
    }

    /**
     * The mathematically "convenient" sigmoid function. 
     * @return Self explanitory
     */
    private double sigmoid(double sum){
        return 1.0 / (1.0 + Math.exp(-sum));
    }

    /**
     * Gets the expected output of this NeuralNetwork given a specific Input and Output pair
     * @return The expected output
     */
    public double getExpectedOutput(){
        return myOutput.getExpectedOutput(0);
    }

    /**
     * Gets the calculated value of this NeuralNetwork after an Input has been fed-forward
     */
    public double getCalculatedValue(){
        return myOutput.get(0).getValue();
    }
    
    /**
     * Gets the HiddenLayer associated with this NeuralNetwork
     */
    public HiddenLayer getHiddenLayer(){
        return myHidden;
    }
    
    /**
     * Gets the InputLayer associated with this NeuralNetwork
     * @return myInput The InputLayer
     */
    public InputLayer getInputLayer(){
        return myInput;
    }
    
    /**
     * Gets the list of Synapses associated with this NeuralNetwork
     * @return The edges (Synapes) connecting each node in the network
     */
    public ArrayList<Synapse> getEdges(){
        ArrayList<Synapse> edges = new ArrayList<Synapse>();
        ArrayList<Neuron> neurons = new ArrayList<Neuron>();
        neurons.addAll(myInput.getNeurons());
        neurons.addAll(myHidden.getNeurons());
        
        for (Neuron current : neurons){
            edges.addAll(current.getOutputSynapses());
        }

        return edges;
    }
    
    /**
     * Defines the crossover between this NeuralNetwork and another
     * NeuralNetwork
     * @param partner The other parent
     * @return children The children produced from crossover
     */
    public ArrayList<NeuralNetwork> crossover(NeuralNetwork partner){
        /* Another Crossover Option
        String myStruct = mySchema.getStructure();
        String partnerStruct = partner.getSchema().getStructure();
        
        Random rand = new Random();
        
        String daughterStruct = "";
        String sonStruct = "";
        
        int pivot = rand.nextInt(myStruct.length() - 1) + 1;
        
        for (int i = 0; i < pivot; i++){
            sonStruct = sonStruct + myStruct.charAt(i);
            daughterStruct = daughterStruct + partnerStruct.charAt(i);
        }
        
        for (int j = pivot; j < myStruct.length(); j++){
            sonStruct = sonStruct + partnerStruct.charAt(j);
            daughterStruct = daughterStruct + myStruct.charAt(j);
        }
        
        Schema sonSchema = new Schema(myTrainingSet.get(0));
        sonSchema.setStructure(sonStruct);
        Schema daughterSchema = new Schema(myTrainingSet.get(0));
        daughterSchema.setStructure(daughterStruct);*/
        
        double myLR = mySchema.getLearningRate();
        double partnerLR = partner.getSchema().getLearningRate();
        double myM = mySchema.getMomentum();
        double partnerM = partner.getSchema().getMomentum();
        double myHR = mySchema.getHiddenRatio();
        double partnerHR = partner.getSchema().getHiddenRatio();
        
        double pivot = Math.random();
        
        Schema sonSchema = new Schema();
        Schema daughterSchema = new Schema();
        
        sonSchema.setLearningRate(myLR * pivot + partnerLR * (1.0 - pivot));
        daughterSchema.setLearningRate(myLR * (1.0 - pivot) + partnerLR * pivot);
        sonSchema.setMomentum(myM * pivot + partnerM * (1.0 - pivot));
        daughterSchema.setMomentum(myM * (1.0 - pivot) + partnerM * pivot);
        sonSchema.setHiddenRatio(myHR * pivot + partnerHR * (1.0 - pivot));
        daughterSchema.setHiddenRatio(myHR * (1.0 - pivot) + partnerHR * pivot);
        
        sonSchema.mutate();
        daughterSchema.mutate();
        
        NeuralNetwork son = new NeuralNetwork(myTrainingSet, sonSchema);
        NeuralNetwork daughter = new NeuralNetwork(myTrainingSet, daughterSchema);
        
        ArrayList<NeuralNetwork> children = new ArrayList<NeuralNetwork>();
        children.add(son);
        children.add(daughter);
        
        return children;
    }
    
    /**
     * Compares the fitness of two NeuralNetworks
     * @param other The other NeuralNetwork in the comparison
     * @return Who's fittest?
     */
    public int compareTo(NeuralNetwork other){
        Integer myFitness = this.getFitness();
        Integer yourFitness = other.getFitness();
        
        return myFitness.compareTo(yourFitness);
    }
    
    /**
     * Gets the fitness associated with this NeuralNetwork. Fitness is 
     * correlated to how well the NeuralNetwork performs on it's training
     * set.
     * @return numRight The number of correct predictions
     */
    public int getFitness(){
        //Collections.shuffle(myTrainingSet);
        int numRight = 0;
        
        for (IOTuple currentIO : myTrainingSet){
            this.newIO(currentIO);
            this.feedForward();
            double calculated = this.getCalculatedValue();
            double expected = this.getExpectedOutput();
            double error = Math.abs(calculated - expected);
            
            if (error < TOLERANCE){
                numRight++;
            }
        }

        return numRight;
        
        /**Potential Fitness Metric (Specificity)*/
        /*int truePositive = 0;
        int trueNegative = 0;
        int falsePositive = 0;
        int falseNegative = 0;
        int numWrong = 0;
        
        for (IOTuple currentIO : myTrainingSet){
            this.newIO(currentIO);
            this.feedForward();
            double calculated = this.getCalculatedValue();
            double expected = this.getExpectedOutput();

            double error = Math.abs(calculated - expected);
            
            if (error < TOLERANCE){
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

        return (int)((double)trueNegative/(double)(trueNegative + falsePositive))*1000;*/
    }
    
    /**
     * Gets the Schema associated with this NeuralNetwork
     * @return mySchema This NeuralNetwork's Schema
     */
    public Schema getSchema(){
        return mySchema;
    }
    
    /**
     * Assigns a new training set to this NeuralNetwork
     * @param newTrainingSet The new training set
     */
    public void assignTrainingSet(ArrayList<IOTuple> newTrainingSet){
        myTrainingSet = newTrainingSet;
    }
}
