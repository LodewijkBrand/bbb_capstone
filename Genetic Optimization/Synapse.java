/**
 * An object that represents a Synapse. A Synapse is used to connect two Neurons together.
 * @author Lou Brand
 */

import java.util.Random;

public class Synapse{
    private double weight;
    private Neuron origin, destination;
    private double momentum;

    /**
    * When a Synapse object is created it needs two Neuron objects it is connecting. 
    * @param from The Neuron this edge is coming from
    * @param to The Neuron this edge is going to
    */
    public Synapse(Neuron from, Neuron to){
        origin = from;
        destination = to;
        from.addOutput(this);
        to.addInput(this);
        setRandomWeight();
        momentum = 0;
    }

    /**
    * Gives this Synapse a random value between [-1, 1]
    * This parameter could theoretically be varied via our genetic algorithm
    */
    private void setRandomWeight(){
        Random rand = new Random();
        weight = rand.nextDouble() * 2.0 - 1.0;
    }

    /**
    * Gives this Synapse a new weight.
    * @param newWeight The new weight for this Synapse
    */
    public void setWeight(double newWeight){
        weight = newWeight;
    }

    /**
    * Gets the weight of this Synapse
    */
    public double getWeight(){
        return weight;
    }

    /**
    * Gets the Neuron where this Synapse originates from
    * @return origin The Neuron this Synapse is coming from
    */
    public Neuron getOrigin(){
        return origin;
    }

    /**
    * Gets the Neuron where this Synapse is going to
    * @return destination The Neuron this Synapse is going to
    */
    public Neuron getDestination(){
        return destination;
    }
    
        
    public double getMomentum(){
        return momentum;
    }
    
    public void setMomentum(double newMomentum){
        momentum = newMomentum;
    }
}
