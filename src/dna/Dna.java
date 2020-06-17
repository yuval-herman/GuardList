package dna;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public class Dna implements Serializable, Comparable<Dna>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4163955073606402973L;
	int fitness;
	private Schedule genome;
	public int getFitness() {
		return fitness;
	}

	public void setFitness(int fitness) {
		this.fitness = fitness;
	}

	public Schedule getGenome() {
		return genome;
	}

	public void setGenome(Schedule genome) {
		this.genome = genome;
	}

	public boolean isEval() {
		return eval;
	}

	public void setEval(boolean eval) {
		this.eval = eval;
	}

	boolean eval=false;
	
	public Dna(int fitness, Schedule genome) {
		this.fitness = fitness;
		this.genome = genome;
	}
	
	@Override
	public String toString() {
		return "Dna [\nfitness=" + fitness + ", genome=" + genome + "\n]";
	}

	public void calculateFitness() {
		fitness = genome.calculateFitness();
	}
	
	public Dna crossover(Dna mate) {
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
		return new Dna(0, new Schedule(newProfileArr, genome.getRange()));
//		return mate;
	}
	
	/*private Profile[] findMissingPost(Profile[] newProfileArr) {
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
	}*/
	
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

	public boolean evaluate() {
		return genome.evaluate();
	}

	@Override
	public int compareTo(Dna o) {
		return fitness-o.fitness;
	}
}