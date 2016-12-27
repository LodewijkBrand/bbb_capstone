/**
 * Class used to represent a Population of NeuralNetworks for a genetic
 * algorithm
 * @author Lou Brand
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Population{
    private int popSize;
    private ArrayList<NeuralNetwork> myPopulation;
    private ArrayList<IOTuple> popTrainingSet;
    private final int EVOLVE_EPOCHS = 100;
    
    /**
     * Initializes a Population given a size and a training set
     * @param size The number of NeuralNetworks in the Population
     * @param trainingSet The set of IOTuples that will train each
     * NeuralNetwork in the Population
     */
    public Population(int size, ArrayList<IOTuple> trainingSet){
        popSize = size;
        myPopulation = new ArrayList<NeuralNetwork>();
        popTrainingSet = trainingSet;
        
        for (int i = 0; i < popSize; i++){
            Schema currentStruct = new Schema();
            myPopulation.add(new NeuralNetwork(popTrainingSet, currentStruct));
        }
    }
    
    /**
	 * Selects a new parent given a probability array
	 * @param probability The probabiblity array
	 */
	public NeuralNetwork findParent(double[] probability){
		Random rand = new Random();
		double x = Math.random()*(probability[probability.length-1]);
		boolean found = false;
		int lowIndex = 0;
		int highIndex = probability.length-1;
		int pos = (lowIndex+highIndex)/2;
		
		while (!found && pos != 0){
			if (x < probability[pos] && x >= probability[pos-1]){
				found = true;
			} else if (x >= probability[pos]){
				if (lowIndex != pos) {lowIndex = pos;}
				else {lowIndex = highIndex;}
				pos = (lowIndex + highIndex)/2;
			} else if (x < probability[pos-1]){
				if (highIndex != pos) {highIndex = pos;}
				else {highIndex = lowIndex;}
				pos = (lowIndex + highIndex)/2;
			} else {System.out.println("SOMETHING WRONG");}
		}
		
		return myPopulation.get(pos);
	}
    
    /**
     * Produces the next generation of NeuralNetworks
     */
    public void evolve(){
        ArrayList<NeuralNetwork> nextGen = new ArrayList<NeuralNetwork>();
                            
        for (int j = 0; j < EVOLVE_EPOCHS; j++){
            for (NeuralNetwork currentNN : myPopulation){
                for (IOTuple trainingIO : popTrainingSet){
                    currentNN.newIO(trainingIO);
                    currentNN.feedForward();
                    currentNN.backProp();
                }
            }
        }
        
        double[] scaledProbability = scaledFitness();
        
        //Top scaling of the top two individuals
        NeuralNetwork top = myPopulation.get(myPopulation.size() - 1);
        NeuralNetwork secondPlace = myPopulation.get(myPopulation.size() - 2);
        Schema topSchema = top.getSchema();
        Schema secondSchema = secondPlace.getSchema();
        
        nextGen.add(new NeuralNetwork(popTrainingSet, topSchema));
        nextGen.add(new NeuralNetwork(popTrainingSet, secondSchema));
        
        for (int i = 0; i < popSize/2 - 1; i++){
            NeuralNetwork mother = findParent(scaledProbability);
            NeuralNetwork father = findParent(scaledProbability);
            for (NeuralNetwork child : mother.crossover(father)){
                nextGen.add(child);
            }
        }
        
        myPopulation = nextGen;
    }
    
    /**
     * Returns an array of weighted rankings that correspond to the sorted
     * array of NeuralNetwork. This array will be used to determine the
     * parent NeuralNetworks for the next generation
     * @return probability Array of rankings (corresponds to myPopulation)
     */
    public double[] scaledFitness(){
        Collections.sort(myPopulation);
        
        System.out.println("MOST FIT: " + myPopulation.get(myPopulation.size()-1).getFitness());
        System.out.println("LEAST FIT: " + myPopulation.get(0).getFitness());
        
        double[] probability = new double[myPopulation.size()];
        double cumulativeFitness = 0;
		
		for (int rank = 0; rank < myPopulation.size(); rank++){
            cumulativeFitness += Math.pow(rank, 1.1);
			probability[rank] = cumulativeFitness;
		}
        
		return probability;
    }
    
    /**
     * Returns the NeuralNetworks that make up the current population
     * @return myPopulation The current population of NeuralNetworks
     */
    public ArrayList<NeuralNetwork> getIndividuals(){
        return myPopulation;
    }
    
    /**
     * Assigns a new training set to each NeuralNetwork in our Population
     * @param newTrainingSet The new training set
     */
    public void newTrainingSet(ArrayList<IOTuple> newTrainingSet){
        for (NeuralNetwork current : myPopulation){
            current.assignTrainingSet(newTrainingSet);
        }
    }
    
    /**
     * Shuffles the training set
     */
    public void shuffleData(){
        Collections.shuffle(popTrainingSet);
    }
    
    /**
     * Initializes the edges in each NeuralNetwork in our Population to
     * a random number between -1.0 and 1.0
     */
    public void initializeEdges(){
        int numEdges = myPopulation.get(0).getEdges().size();
        Random rand = new Random();
        for (int i = 0; i < numEdges; i++){
            double weight = rand.nextDouble() * 2.0 - 1.0;
            for (NeuralNetwork current : myPopulation){
                current.getEdges().get(i).setWeight(weight);
            }
        }
    }
}
