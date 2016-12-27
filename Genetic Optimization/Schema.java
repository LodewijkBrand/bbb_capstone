/**
 * An object that represents a Schema object for a NeuralNetwork. The 
 * Schema object holds all the structural information for a NeuralNetwork.
 * We are trying to optimize the Schema with a genetic algorithm.
 * @author Lou Brand
 */

import java.util.Random;

public class Schema{
    private int numInputs;
    private int numOutputs;
    private double learningRate;
    private double momentum;
    private double hidden;
    
    /**
     * Constructs a Schema object and randomly assigns the parameters
     */
    public Schema(){
        randomStructure();
    }
    
    /**
     * Randomly assign the parameters for this Schema
     */
    public void randomStructure(){
        learningRate = Math.random() * 5;   // [0, 5]
        momentum = Math.random();       // [0, 1]
        hidden = Math.random();         // [0, 1]
        //threshold = Math.random();
    }
    
    /**
     * Gets the learning rate associated with this Schema
     * @return learningRate The learning rate of a NeuralNetwork
     */
    public double getLearningRate(){
        return learningRate;
    }
    
    /**
     * Sets the learning rate associated with this Schema
     * @param newLearningRate The new learning rate
     */
    public void setLearningRate(double newLearningRate){
        learningRate = newLearningRate;
    }
    
    /**
     * Gets the momentum associated with this Schema
     * @return momentum The momentum of a NeuralNetwork
     */
    public double getMomentum(){
        return momentum;
    }
    
    /**
     * Sets the momentum associated with this Schema
     * @param newMomentum The new momentum
     */
    public void setMomentum(double newMomentum){
        momentum = newMomentum;
    }
    
    /**
     * Gets the ratio of hidden nodes to input nodes associated with this Schema
     * @return hidden The ratio of hidden nodes to input nodes
     */
    public double getHiddenRatio(){
        return hidden;
    }
    
    /**
     * Sets the hidden node to input node ratio
     * @param newHidden The new ratio of hidden nodes to input nodes
     */
    public void setHiddenRatio(double newHidden){
        hidden = newHidden;
    }
    
    /**
     * Slightly mutate each parameter contained in this Schema
     */
    public void mutate(){
        double maxPercentChange = .01; // 1%
		double mutationProbability = .05;
        
        if (Math.random() < mutationProbability){
			double change = Math.random()*2.0 - 1; //[-1, 1]
			setLearningRate(learningRate + change * maxPercentChange * learningRate);
		}
        
        if (Math.random() < mutationProbability){
			double change = Math.random()*2.0 - 1; //[-1, 1]
			setMomentum(momentum + change * maxPercentChange * momentum);
		}
        
        if (Math.random() < mutationProbability){
			double change = Math.random()*2.0 - 1; //[-1, 1]
			setHiddenRatio(hidden + change * maxPercentChange * hidden);
		}
    }
}
