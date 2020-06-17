package GuardListApp;

import java.util.Arrays;
import java.util.Date;

import dna.Population;
import dna.Profile;
import dna.Schedule;

public class App {
	
	static int popSize = 1000;
	static double mutChance = 0.03;
	
	public static void main(String[] args) {
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
				new Profile("11", 0.91f, new int[] {1,4}, null),
		};

		//consoleController.chooseOperation();
		int[] range = new int[] {5, 6};
		int timeScale = 12;
		
		Date beginDate = new Date();
		
		/*
		 * for (Profile profile : profiles) { Date displayTime = new
		 * Date((beginDate.getTime()+ 3600000*(timeScale/range[profile.getPost()[0]]*
		 * profile.getPost()[1])));
		 * System.out.println(Arrays.toString(profile.getPreference())+"	"
		 * +displayTime); }
		 */
		
		Population population = new Population(range);//.basePopulation; //instantiating like this is for testing
		//purposes and makes for random profiles
		population.generatePopulation(popSize, new Schedule(profiles, null));
		population.getPopulation().get(0).getGenome().saveState("schedule.ser");
		//main loop
//		Population population = Population.loadState("population.ser");
		
		int i=0;
		int high = 0;
		
		while /*(population.evaluate()<popSize/2.2) {*/(i<100) {
			System.out.println("generation->" + i);
			//calculate fitness
			population.sortByFitness();
			high=population.getPopulation().get(199).getFitness()>high?population.getPopulation().get(199).getFitness():high;
			System.out.println("highest fitness="+population.getPopulation().get(199).getFitness());
			System.out.println("number of possible schedules=" + population.evaluate());
			//crossover+new generation
			//Dna[] temp = population.crossover();
			population.newGeneration();//population.crossover());
			//mutation+hyper mutation
			population.sortByFitness();
			if (population.getPopulation().get((int) (popSize/1.7)).getFitness() == population.getPopulation().get(popSize-1).getFitness()) {
				population.mutation(mutChance*3);
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
		population.prettyPrintDna(population.getPopulation().get(popSize-1), timeScale, beginDate);
		population.printFitness();
		System.out.println("highest ever: "+high);
		System.out.println("generations: "+ i);
	}
}
