/**
 * The main Driver method used to find the best Schema with a genetic
 * algorithm
 * @author Lou Brand
 */

import java.util.ArrayList;
import java.io.*;

public class Driver{
	public static void main (String[] args){
        ArrayList<IOTuple> inputOutputTuples;
        
        try{
            InputStream file = new FileInputStream("pybelSmiles.ser");
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream (buffer);
            inputOutputTuples = (ArrayList<IOTuple>)input.readObject();

            int GEN_EPOCHS = 5;
            int EPOCHS = 50;
			int POP_SIZE = 50;
            
            FindSchema fs = new FindSchema(inputOutputTuples, GEN_EPOCHS, EPOCHS, POP_SIZE);
        }
        catch(ClassNotFoundException ex){System.out.println("The class wasn't found!");}
        catch(IOException ex){System.out.println("There was an error!");}
	}
}
