package GuardListApp;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import dna.Population;
import dna.Profile;

public class App {
	
	static int popSize = 200;
	static double mutChance = 0.001;
	
	public static void main(String[] args) {
		/*String data = new String();
		try {
			FileReader config = new FileReader("testConfigFile.txt");
			
			int i; 
		    while ((i=config.read()) != -1) {
		    	switch ((char) i) {
				case '=':
					switch (data) {
					case value:
						
						break;

					default:
						break;
					}
					break;

				case '[':
					
					break;
					
				case ']':
					
					break;
					
				default:
					data+=((char) i);
					break;
				}
		  	}
		    config.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		int[] range = new int[] {1, 10}; //first cell for number of stations, second for number of people
		//generate population
		Profile[] schedule = new Profile[range[1]];
		for(int i = 0; i<schedule.length;i++) {
			schedule[i] = new Profile();
		}
		
		Population population = new Population(range); //instantiating like this is for testing
														 //purposes and makes for random profiles
		population.generatePopulation(popSize, schedule);
		//main loop
		int i=0;
		while (i<1000) {
//			System.out.println("generation->" + i);
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
		System.out.println("eval:");
		population.evaluate();
//		System.out.println(Arrays.deepToString(schedule));
		population.printFitness();
	}
}
