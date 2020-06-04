package dna;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Dna implements Comparator<Dna> {

	int fitness;
	Schedule genome;
	boolean eval=false;
	
	public Dna(int fitness, Schedule genome) {
		this.fitness = fitness;
		this.genome = genome;
	}
	
	@Override
	public String toString() {
		return "Dna [\nfitness=" + fitness + ", genome=" + genome + "\n]";
	}

	public void calculateFitness(int range[]) {
		fitness = genome.calculateFitness(range);
	}
	
	private Profile[] sortProfiles() {
		Profile[] tempProfiles = genome.getProfiles().clone();
		Arrays.sort(tempProfiles, new Comparator<Profile>() {
			@Override
			public int compare(Profile o1, Profile o2) {
			return o1.getPost()[1]-o2.getPost()[1];
			}});
		return tempProfiles;
		}
	
	public Dna crossover(Dna mate) { //TODO somehow fix this mess
		Random r = new Random();
		Profile[] newProfileArr = new Profile[genome.getProfiles().length];
		
		for (int i = 0; i < genome.getProfiles().length; i++) {
			
			if(r.nextFloat() < 0.5f) {// && !contain(newProfileArr ,genome.getProfiles()[i])) {
				newProfileArr[i] = genome.getProfiles()[i].duplicate();
			} else {
			//	if(!contain(newProfileArr ,mate.genome.getProfiles()[i])) {
					newProfileArr[i] = mate.genome.getProfiles()[i].duplicate();
				//}
			}
			
		}
		return new Dna(0, new Schedule(findMissingPost(newProfileArr)));
//		return mate;
	}
	
	private Profile[] findMissingPost(Profile[] newProfileArr) {
		int in = 0;
		int ino =0;
		for (int i = 0; i < newProfileArr.length; i++) {
			if(!contain(newProfileArr, genome.getProfiles()[i])) {
				ino++;
				for (int j = 0; j < newProfileArr.length; j++) {
					if (newProfileArr[j] == null) {
						newProfileArr[j] = genome.getProfiles()[j];
						newProfileArr[j].setPost(genome.getProfiles()[i].getPost());
						in++;
						break;
					}
				}
			}
		}
		if (newProfileArr[9] ==  null) {
			System.out.println(ino);
			System.out.println(in);
			System.out.println();
		}
		return newProfileArr;
	}
	
	private boolean contain(Profile[] newProfileArr, Profile profile) {
		for (int i = 0; i < newProfileArr.length; i++) {
			try {
				if (Arrays.equals(newProfileArr[i].getPost(), profile.getPost())) { 
				return true; 
				}
			} catch (Exception e) {
				continue;
			}
		}
		return false;
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

	public boolean evaluate() {
		return genome.hasDuplicates()>0;
	}

	public boolean evaluate(Profile[] original) {
		for (int i = 0; i< genome.getProfiles().length ; i++) {
			if (!Arrays.equals(genome.getProfiles()[i].getPreference(), original[i].getPreference())) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int compare(Dna arg0, Dna arg1) {
		return (int) (arg0.fitness-arg1.fitness);
	}
}