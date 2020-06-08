package GuardListApp;

import dna.Dna;
import dna.Population;
import dna.Profile;
import dna.Schedule;

public class App {
	
	static int popSize = 400;
	static double mutChance = 0.02;
	
	public static void main(String[] args) {
		ConsoleController consoleController = new ConsoleController();
		consoleController.loadSchedule("schedule.ser");
		//generate population
		Profile[] profiles = new Profile[]{new Profile("1", 0.1f, new int[] {1,1}, null),
				new Profile("2", 0.2f, new int[] {0,4}, null),
				new Profile("3", 0.3f, new int[] {0,3}, null),
				new Profile("4", 0.4f, new int[] {1,4}, null),
				new Profile("5", 0.5f, new int[] {1,2}, null),
				new Profile("6", 0.6f, new int[] {0,4}, null),
				new Profile("7", 0.7f, new int[] {1,1}, null),
				new Profile("8", 0.8f, new int[] {1,0}, null),
				new Profile("9", 0.9f, new int[] {1,0}, null),
				new Profile("10", 0.99f, new int[] {1,4}, null),
				};
		
		Population population = new Population(); //instantiating like this is for testing
														 //purposes and makes for random profiles
		
		population.generatePopulation(popSize, consoleController.baseSchedule);
		population.getPopulation().get(0).getGenome().saveState("schedule.ser");
		//main loop
//		Population population = Population.loadState("population.ser");
		
		int i=0;
		
		while (population.evaluate()<popSize/2.2) {//(i<10000) {
			System.out.println("generation->" + i);
			//calculate fitness
			population.sortByFitness();
			System.out.println("highest fitness="+population.getPopulation().get(199).getFitness());
			System.out.println("number of possible schedules=" + population.evaluate());
			//crossover+new generation
			//Dna[] temp = population.crossover();
			population.newGeneration();//population.crossover());
			//mutation+hyper mutation
			population.sortByFitness();
			if (population.getPopulation().get((int) (popSize/2)).getFitness() == population.getPopulation().get(popSize-1).getFitness()) {
				population.mutation(mutChance*2);
			} else {
				population.mutation(mutChance);
			}
			
			i++;
		}

		population.calculateFitness();
		
//		System.out.println(population);
		population.sortByFitness();
		System.out.println("eval:");
		population.printEvaluate();
//		System.out.println(Arrays.deepToString(schedule));
		population.printFitness();
	}
}
