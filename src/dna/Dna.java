package dna;
import java.util.Arrays;
import java.util.Random;

public class Dna {

	float fitness;
	Schedule genome;
	
	public Dna(float fitness, Schedule genome) {
		this.fitness = fitness;
		this.genome = genome;
	}
	
	public void calculateFitness() {
		fitness=0;
		for (Profile profile : genome.getProfiles()) {
			fitness += (Math.abs(profile.getPost()[1] - profile.getPreference()[1])+1) / profile.getPriority();
		}
	}
	
	public Dna crossover(Dna mate) {
		Random r = new Random();
		Profile[] newProfileArr = new Profile[genome.getProfiles().length];
		for (int i = 0; i < genome.getProfiles().length; i++) {
			newProfileArr[i] = r.nextFloat() < 0.5 ? genome.getProfiles()[i].duplicate() : mate.genome.getProfiles()[i].duplicate();
		}
		return new Dna(0, new Schedule(newProfileArr));
	}
	
	public Dna duplicate() {
		return new Dna(fitness, genome.duplicate());
	}
	/*
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