package GuardListApp;

import dna.Dna;
import dna.Population;
import dna.Profile;
import dna.Schedule;

public class ScheduleGenerator {

	public Schedule generateSchedule(Profile[] profiles, int[] range) {
		return new Schedule(profiles, range);
	}

	public Schedule ScheduleFromString(String str) {
		//name,priority, preference
		String[] dataLines = str.split("\\r?\\n");
		Profile[] profiles = new Profile[dataLines.length-1];
		for (int i = 0; i < dataLines.length-1; i++) {
			String[] data = dataLines[i].split(",");
			String[] preference = data[2].split(":");

			profiles[i] = new Profile(data[0],
					Float.valueOf(data[1]),
					new int[]{Integer.valueOf(preference[0]),Integer.valueOf(preference[1])});
		}
		
		String[] rangeStr = dataLines[dataLines.length-1].split(":");
		int[] range = new int[rangeStr.length];
		for (int i = 0; i < rangeStr.length; i++) {
			range[i]=Integer.valueOf(rangeStr[i]);
		}
		return new Schedule(profiles, range);
	}

	public Dna calculateBestSchedule(Schedule schedule) {
		int popSize = 300;
		double mutChance = 0.03;
		Population population = new Population(schedule.getRange());
		population.generatePopulation(popSize, schedule);

		int i=0;
		boolean expresion = true;
		do {
			System.out.println("generation->" + i);
			//calculate fitness
			population.sortByFitness();
			System.out.println("highest fitness="+population.getPopulation().get(popSize-1).getFitness());
			System.out.println("number of possible schedules=" + population.evaluate().length);
			//crossover+new generation
			population.newGeneration();//population.crossover());
			//mutation+hyper mutation
			population.sortByFitness();
			if (population.getPopulation().get((int) (popSize/1.7)).getFitness() == population.getPopulation().get(popSize-1).getFitness()) {
				population.mutation(mutChance*3);
			} else {
				population.mutation(mutChance);
			}
			i++;
		} while (i<3000);

		population.calculateFitness();

		population.sortByFitness();
		System.out.println("eval:");
		population.printEvaluate();
		Dna[] eval = population.evaluate();
		System.out.println();
		System.out.println("generations: "+ i);
		return eval[eval.length-1];
	}
}
