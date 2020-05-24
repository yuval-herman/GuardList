package GuardListApp;

import dna.Dna;
import dna.Population;
import dna.Profile;

public class App {
	
	static int popSize = 250;
	static double mutChance = 0.0085;
	
	public static void main(String[] args) {
		//generate population
		Population population = new Population(); //instantiating like this is for testing
														 //purposes and makes for random profiles
		Dna[] profiles = new Dna[5];
		for(int i = 0; i<profiles.length;i++) {
			profiles[i] = new Dna(0, new Profile());
		}
		population.generatePopulation(popSize, profiles);
		
		//main loop
		while (true) {
			//calculate fitness
			
		}
	}

}
