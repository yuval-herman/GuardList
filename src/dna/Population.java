package dna;

import java.util.ArrayList;

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
	
	public void generatePopulation(int popSize, Profile[] profiles) {
		
	}
}
