package GuardListApp;

import java.util.Arrays;

import dna.Dna;
import dna.Population;
import dna.Profile;

public class App {
	
	static int popSize = 200;
	static double mutChance = 0.01;
	
	public static void main(String[] args) {
		int[] range = new int[] {1, 5}; //first cell for number of stations, second for number of people
		//generate population
		Profile[] schedule = new Profile[5];
		for(int i = 0; i<schedule.length;i++) {
			schedule[i] = new Profile();
		}
		
		Population population = new Population(range); //instantiating like this is for testing
														 //purposes and makes for random profiles
		population.generatePopulation(popSize, schedule);
		//main loop
		int i=0;
		while (i<1000) {
			System.out.println("generation->" + i);
			//calculate fitness
			population.calculateFitness();
			//crossover+new generation
//			Dna[] temp = population.crossover();
			population.newGeneration(population.crossover());
			//mutation
			population.mutation(mutChance);
			
			i++;
		}

		population.calculateFitness();
		
//		System.out.println(population);
		population.evaluate();
		System.out.println(Arrays.deepToString(schedule));
//		population.printFitness();
	}
}
