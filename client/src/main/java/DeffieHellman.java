import java.util.Random;

public class DeffieHellman {
    
    //computes the first calculation step of the Deffiel Hellamns algorithm.
    //g^x mod n 
    //g and n are prime numbers that are public to the chat. 
    //x is a random number specific for the client calling the method. 
    public static int firstComputation(int g, int n, int x){
        int firstCalculation=(int)(Math.pow((double)g, (double)x)%n);
        return firstCalculation;
    }

    //Computes the secret value for the specific client, wich must end up beeing
    //the same secret value as the other client's. 
    //strangersvalue is the othe client's firstComputation value. 
    public static int computeSecretValue(int strangersValue, int n, int x){
        int secretValue=(int)(Math.pow((double)strangersValue, (double)x))%n;
        return secretValue; 
    }


}
