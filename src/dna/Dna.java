package dna;
import java.util.Arrays;
import java.util.Random;

public class Dna {

	int fitness;
	Profile genome;
	
	public Dna(int fitness, Profile genome) {
		this.fitness = fitness;
		this.genome = genome;
	}
	/*
	public Dna crossover(Dna mate) {
		char[] n=new char[genome.length]; 
        
        // Copy the array a into n 
        System.arraycopy(Arrays.copyOfRange(genome, 0, genome.length/2), 0, n, 0, genome.length/2); 
        // Copy the array b into n 
        System.arraycopy(Arrays.copyOfRange(mate.genome, mate.genome.length/2, mate.genome.length), 0, n, genome.length/2, mate.genome.length/2); 
        return new Dna(0, n);
	}
	
	public void mutation(double mutChance) {
		Random r = new Random();
		for(int i = 0; i<genome.length;i++) {
		    String alphabet = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz .,?:1234567890!@#$%^&*()+=-/";
		    if(r.nextFloat()<mutChance) {
		    	genome[i] = alphabet.charAt(r.nextInt(alphabet.length()));
		    }
		}
	}
	
	@Override
	public String toString() {
		return "dna [fitness=" + fitness + ", genome=" + Arrays.toString(genome) + "]";
	}

	public void calcFitness(String str) {
		char[] ans = str.toCharArray();
		int fit=0;
		for(int i=0;i<str.length();i++) {
			if(genome[i]==ans[i]) {
				fit++;
			}
		}
		
		fitness=fit;
	}
	
	public double calcNormalizedFitness() {
		return fitness != 0 ? fitness / (genome.length/100.0) / 100 : 0;
	}
	
	public boolean evaluate() {
		return fitness == genome.length;
	}
	
	public char[] rndCharArr(int len) {
		Random r = new Random();

		char[] rndArr = new char[len];
	    String alphabet = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz .,?:1234567890!@#$%^&*()+=-/";
	    for (int i = 0; i < len; i++) {
	        rndArr[i] = alphabet.charAt(r.nextInt(alphabet.length()));
	    } // prints 50 random characters from alphabet
		return rndArr;
	}
	
	public String getStringGenome() {
		return String.copyValueOf(genome);
	}
*/	
}