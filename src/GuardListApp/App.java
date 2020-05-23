package GuardListApp;

import dna.Population;
import dna.Profile;

public class App {
	
	static int popSize = 250;
	static double mutChance = 0.0085;
	
	public static void main(String[] args) {
		//generate population
		Population population = new Population(); //instantiating like this is for testing
														 //purposes and makes for random profiles
		Profile[] profiles = new Profile[5];
		for(int i = 0; i<profiles.length;i++) {
			profiles[i] = new Profile();
		}
		population.generatePopulation(popSize, profiles);
		population.getPopulation().get(0).getProfiles()[0].setPost(new int[] {1, 2});
	}

}
