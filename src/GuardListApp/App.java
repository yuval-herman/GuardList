package GuardListApp;

import dna.Dna;
import dna.Population;
import dna.Profile;
import dna.Schedule;

public class App {
	
	static int popSize = 200;
	static double mutChance = 0.001;
	
	public static void main(String[] args) {
		
		//generate population
		Population population = new Population(); //instantiating like this is for testing
														 //purposes and makes for random profiles
		Profile[] schedule = new Profile[5];
		for(int i = 0; i<schedule.length;i++) {
			schedule[i] = new Profile();
		}
		population.generatePopulation(popSize, schedule);
		
		//calculate fitness
		population.calculateFitness();

		//population.printFitness();
		//main loop
		int i=0;
		while (i<1000) {
			//calculate fitness
			population.calculateFitness();
			//crossover+new generation
			population.newGeneration(population.crossover());
			//mutation
			population.mutation(mutChance);
			
			i++;
		}

		population.calculateFitness();
		population.printFitness();
	}
}
