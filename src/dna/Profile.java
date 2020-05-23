package dna;

import java.util.Random;

public class Profile {

	static int number; //for testing purposes
	String name;
	float priority; //the algorithm priority to satisfy the preference
	int[] Preference; //the preference for a post TODO make a 2D array to keep more then one preference
	int[] post; //first cell for what post, second for time
	
	public Profile(String name, float priority, int[] preference) {
		number++;
		this.name = name;
		this.priority = priority;
		Preference = preference;
	}
	
	public Profile() {
		number++;
		Random r = new Random();
		this.name = String.valueOf(number);
		this.priority = r.nextFloat();
		int[] temp = {r.nextInt(5), r.nextInt(5), r.nextInt(5)};
		Preference = temp;
	}
}
