package dna;

import java.util.ArrayList;
import java.util.Arrays;

public class Population {

	private ArrayList<Schedule> population;

	public Population(ArrayList<Schedule> population) {
		this.setPopulation(population);
	}
	
	public Population() {
		this.setPopulation(new ArrayList<Schedule>());
	}

	public ArrayList<Schedule> getPopulation() {
		return population;
	}

	public void setPopulation(ArrayList<Schedule> population) {
		this.population = population;
	}
	
	public void generatePopulation(int popSize, Dna[] profiles) {
		population = new ArrayList<Schedule>();
		
		for(int i = 0; i < popSize; i++) { //deep copy profiles TODO either improve or make it a function🤦
			Profile[] temp = new Profile[profiles.length];
			for (int j = 0 ; j<temp.length; j++) {
				temp[j] = new Profile(profiles[j].genome.getName(), profiles[j].genome.getPriority(),
						profiles[j].genome.getPreference(), profiles[j].genome.getPost());
			}
			population.add(new Schedule(temp));
		}
	}
	
	public void calculateFitness() {
		
	}
}
