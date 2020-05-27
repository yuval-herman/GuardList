package dna;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;


public class Population {

	private ArrayList<Dna> population;

	public Population(ArrayList<Dna> population) {
		this.setPopulation(population);
	}
	
	public Population() {
		this.setPopulation(new ArrayList<Dna>());
	}

	public ArrayList<Dna> getPopulation() {
		return population;
	}

	public void setPopulation(ArrayList<Dna> population) {
		this.population = population;
	}
	
	public void generatePopulation(int popSize, Profile[] profiles) {
		population = new ArrayList<Dna>();
		Random r = new Random();
		
		for(int i = 0; i < popSize; i++) { //deep copy profiles TODO either improve or make it a function🤦
			Profile[] temp = new Profile[profiles.length];
			for (int j = 0 ; j<temp.length; j++) {
				temp[j] = new Profile(profiles[j].getName(), profiles[j].getPriority(),
						profiles[j].getPreference(), new int[]{1, r.nextInt(5)});
			}
			population.add(new Dna(0, new Schedule(temp)));
		}
	}
	
	public void calculateFitness() {
		for (Dna dna : population) {
			dna.calculateFitness();
		}
	}
	
	public Dna[] crossover() {
		ArrayList<Dna> newpop = new ArrayList<Dna>();
		for(int i = 0; i < population.size(); i++) {
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
}
