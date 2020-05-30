package dna;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class Population {

	private ArrayList<Dna> population;
	private int range[];

	public Population(ArrayList<Dna> population,int range[]) {
		this.setPopulation(population);
		this.setRange(range);
	}
	
	public Population(int range[]) {
		this.setPopulation(new ArrayList<Dna>());
		this.setRange(range);
	}

	public int[] getRange() {
		return range;
	}

	public void setRange(int[] range) {
		this.range = range;
	}

	public ArrayList<Dna> getPopulation() {
		return population;
	}

	public void setPopulation(ArrayList<Dna> population) {
		this.population = population;
	}
	
	public void generatePopulation(int popSize, Profile[] profiles) {
		population = new ArrayList<Dna>(); //new population
		Random r = new Random();
		
		for(int i = 0; i < popSize; i++) { //deep copy profiles
			Profile[] temp = new Profile[profiles.length];
			List<Integer> psArr = new ArrayList<Integer>();
			for (int k = 1; k<=range[1]; k++) psArr.add(k);
			Collections.shuffle(psArr);
			for (int j = 0 ; j<temp.length; j++) {
				temp[j] = profiles[j].duplicate();
				temp[j].setPost(new int[] {1, psArr.get(j)});
			}
			
			population.add(new Dna(0, new Schedule(temp)));
		}
	}
	
	public void calculateFitness() {
		for (Dna dna : population) {
			dna.calculateFitness(range);
		}
	}
	
	public Dna[] crossover() {
		ArrayList<Dna> newpop = new ArrayList<Dna>();
		
		for(int i = 0; i < population.size(); i++) {
//			System.out.println(population.get(i).fitness);
			for (int j = 0; j <= population.get(i).fitness; j++) {
				newpop.add(population.get(i).duplicate());
			}
		}
		return newpop.toArray(new Dna[newpop.size()]);
	}
	
	public void newGeneration(Dna[] temppop) {
		ArrayList<Dna> newpop = new ArrayList<Dna>();
		Random r = new Random();
		for(int i =0; i<population.size(); i++) {
			newpop.add(temppop[r.nextInt(temppop.length)].crossover(temppop[r.nextInt(temppop.length)]));
		}
		population=newpop;
	}
	
	public void mutation(double mutChance) {
		for (int i = 0; i < population.size(); i++) {
			population.get(i).genome.mutate(mutChance); 
		}
	}
	
	public void printFitness() {
		for (Dna dna : population) {
			System.out.println(dna.fitness);
		}
	}

	public void evaluate() {
		for (Dna dna : population) {
			if(dna.evaluate()) {
				System.out.println(dna);
			}
		}
	}
	
	public void devaluate() {
		for (Dna dna : population) {
			if(!dna.evaluate()) {
				System.out.println(dna);
			}
		}
	}
	
	public void evaluate(ArrayList<Dna> pop) {
		for (Dna dna : pop) {
			if(dna.evaluate()) {
				System.out.println(dna);
			}
		}
	}
	
	public boolean checkError(ArrayList<Dna> pop) {
		for (Dna dna : pop) {
			if(!dna.evaluate()) {
				System.out.println(dna);
				return false;
			}
		}
		return true;
	}
	
	public boolean checkError() {
		for (Dna dna : population) {
			if(!dna.evaluate()) {
				System.out.println(dna);
				return false;
			}
		}
		return true;
	}
	
	public boolean checkError(Profile[] original) {
		for (Dna dna : population) {
			if(!dna.evaluate(original)) {
				System.out.println(dna);
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "Population [\npopulation=" + population + "\n]";
	}
}
