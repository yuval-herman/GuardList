package GuardListApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Scanner;

import dna.Dna;
import dna.Population;
import dna.Profile;
import dna.Schedule;

public class App {
	
	static int popSize = 300;
	static double mutChance = 0.03;
	
	public static void main(String[] args) {
		//generate population
//		Profile[] profiles = new Profile[]{new Profile("1", 0.1f, new int[] {1,1}, null),
//				new Profile("2", 0.2f, new int[] {0,4}, null),
//				new Profile("3", 0.3f, new int[] {0,3}, null),
//				new Profile("4", 0.4f, new int[] {1,4}, null),
//				new Profile("5", 0.5f, new int[] {1,2}, null),
//				new Profile("6", 0.6f, new int[] {0,4}, null),
//				new Profile("7", 0.7f, new int[] {1,1}, null),
//				new Profile("8", 0.8f, new int[] {1,0}, null),
//				new Profile("9", 0.9f, new int[] {1,0}, null),
//				new Profile("10", 0.99f, new int[] {1,4}, null),
//				new Profile("11", 0.91f, new int[] {1,4}, null),
//		};

		int[] range = new int[] {5, 6};
		int timeScale = 12;

		Date beginDate = new Date();
		String str = null;
		byte[] encoded = null;
		
		try {
			encoded = Files.readAllBytes(Paths.get(args[0]));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		str = new String(encoded, Charset.defaultCharset());
		
		Population population = new Population(range);
		population.generatePopulation(popSize, Schedule.fromString(str));

		//main loop
	
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
			
			if (args.length>1) {
				if (i<Integer.valueOf(args[1])) {
					expresion=true;
				} else {
					expresion=false;
				}
			} else if (i<1000||population.evaluate().length<1) {
				expresion=true;
			} else {
				expresion=false;
			}
			
			i++;
		} while (expresion);

		population.calculateFitness();
		
		population.sortByFitness();
		System.out.println("eval:");
		population.printEvaluate();
		Dna[] eval = population.evaluate();
		population.prettyPrintDna(eval[eval.length-1], timeScale, beginDate);
		System.out.println();
		System.out.println("generations: "+ i);
	}
}
