package GuardListApp;

import dna.Dna;
import dna.Population;
import dna.Profile;

public class App {
	
	static int popSize = 200;
	static double mutChance = 0.01;
	
	public static void main(String[] args) {
		int[] range = new int[] {1, 10}; //first cell for number of stations, second for number of people
		//generate population
		Profile[] schedule = new Profile[]{new Profile("1", 0.1f, new int[] {1,1}, null),
				new Profile("2", 0.2f, new int[] {1,2}, null),
				new Profile("3", 0.3f, new int[] {1,3}, null),
				new Profile("4", 0.4f, new int[] {1,4}, null),
				new Profile("5", 0.5f, new int[] {1,5}, null),
				new Profile("6", 0.6f, new int[] {1,6}, null),
				new Profile("7", 0.7f, new int[] {1,7}, null),
				new Profile("8", 0.8f, new int[] {1,8}, null),
				new Profile("9", 0.9f, new int[] {1,9}, null),
				new Profile("10", 1f, new int[] {1,1}, null),
				};
		
		Population population = new Population(range); //instantiating like this is for testing
														 //purposes and makes for random profiles
		population.generatePopulation(popSize, schedule);
		population.saveState("population.ser");
		//main loop
//		Population population = Population.loadState("population.ser");
		
		int i=0;
		while (i<10000) {
			System.out.println("generation->" + i);
			//calculate fitness
			population.calculateFitness();
			//crossover+new generation
			Dna[] temp = population.crossover();
			population.newGeneration(temp);//population.crossover());
			//mutation
			population.mutation(mutChance);
			
			i++;
		}

		population.calculateFitness();
		
//		System.out.println(population);
		population.sortByFitness();
		System.out.println("eval:");
		population.evaluate();
//		System.out.println(Arrays.deepToString(schedule));
		population.printFitness();
	}
}
