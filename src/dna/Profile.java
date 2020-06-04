package dna;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Profile {

	private static int number; //for testing purposes
	private String name;
	private float priority; //the algorithm priority to satisfy the preference, lower=better
	private int[] preference; //the preference for a post TODO make a 2D array to keep more then one preference
	private int[] post; //first cell for what post, second for time
	private double fitness;
	
	public Profile(String name, float priority, int[] preference, int[] post) {
		number++;
		this.setName(name);
		this.setPriority(priority);
		this.setPreference(preference);
		this.setPost(post);
	}
	
	@Override
	public String toString() {
		return "Profile [\nname=" + name + ", priority=" + priority + ", preference=" + Arrays.toString(preference)
		+ ", post=" + Arrays.toString(post) + ", fitness=" + fitness + "\n]";
	}

	public static int getNumber() {
		return number;
	}

	public static void setNumber(int number) {
		Profile.number = number;
	}

	public float getPriority() {
		return priority;
	}

	public void setPriority(float priority) {
		this.priority = priority;
	}

	public int[] getPreference() {
		return preference;
	}

	public void setPreference(int[] preference) {
		this.preference = preference;
	}

	public Profile() {
		number++;
		Random r = new Random();
		this.setName(String.valueOf(number));
		do { //just making sure i done get a zero
			this.priority = r.nextFloat();
		} while (this.priority==0f);
		preference = new int[]{1, r.nextInt(10)};
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int[] getPost() {
		return post;
	}

	public void setPost(int[] post) {
		this.post = post;
	}

//	public void mutate(double mutChance) {
//		Random r = new Random();
//		if(r.nextDouble() < mutChance) {
//			setPost(new int[]{post[0], r.nextInt(5)});
//		}
//	}

	public Profile duplicate() {
		return new Profile(name, priority, preference, post);
	}

	public int calculateFitness(int range[]) {
		int stationDiff = Math.abs(preference[0]-post[0])+1;
		int timeDiff = Math.abs(preference[1]-post[1])+1;
		fitness = (100*priority/timeDiff)-(100/range[1]);
//		fitness = timeDiff;
		return (int) (fitness*10);
	}

	public void switchWith(Profile profile) {
		int[] oldPost = post;
		setPost(profile.getPost());
		profile.setPost(oldPost);
	}
}
