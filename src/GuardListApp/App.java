package GuardListApp;

import dna.Dna;
import dna.Population;
import dna.Profile;
import dna.Schedule;

public class App {
	
	static int popSize = 250;
	static double mutChance = 0.0085;
	
	public static void main(String[] args) {
		//generate population
		Population population = new Population(); //instantiating like this is for testing
														 //purposes and makes for random profiles
		Profile[] schedule = new Profile[5];
		for(int i = 0; i<schedule.length;i++) {
			schedule[i] = new Profile();
		}
		population.generatePopulation(popSize, schedule);
		
		//main loop
		while (true) {
			//calculate fitness
			
		}
	}

}
