import java.util.Random;

public class DeffieHellman {
    private static Random random=new Random();
    
    //input arr must be an oredered array in ascending order. 
    public static int[] get2RandomNumsFromArray(int[] arr){
        int randomA=random.nextInt(arr.length);
        int randomB=random.nextInt(arr.length);
        while(randomB==randomA){
            randomB=random.nextInt(arr.length);
        }
        int n=arr[randomA];
        int g=arr[randomB];
        int[] result={g, n};
        return result;
    } 
    
}
