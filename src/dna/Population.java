package dna;

import java.util.ArrayList;
import java.util.Arrays;

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
		
		for(int i = 0; i < popSize; i++) { //deep copy profiles TODO either improve or make it a function🤦
			Profile[] temp = new Profile[profiles.length];
			for (int j = 0 ; j<temp.length; j++) {
				temp[j] = new Profile(profiles[j].getName(), profiles[j].getPriority(),
						profiles[j].getPreference(), profiles[j].getPost());
			}
			population.add(new Dna(0, new Schedule(temp)));
		}
	}
	
	public void calculateFitness() {
		
	}
}
