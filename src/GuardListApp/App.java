package GuardListApp;

import dna.Population;

public class App {
	
	static int popSize = 250;
	static double mutChance = 0.0085;
	
	public static void main(String[] args) {
		//generate population
		Population population = new Population(); //instantiating like this is for testing
														 //purposes and makes for random profiles
		population.generatePopulation(popSize, null);
	}

}
