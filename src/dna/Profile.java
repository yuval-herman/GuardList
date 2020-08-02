package dna;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public class Profile implements Serializable, Comparable<Profile>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7581073385039458589L;
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

	public Profile(String name, Float priority, int[] preference) {
		this(name, priority, preference, null);
	}

	@Override
	public String toString() {
		return name + ", " + priority + ", " + Arrays.toString(preference)
		+ ", " + Arrays.toString(post) + "\n";
	}
	
	public String hebToString() {
		return "שם: " + name + ", עמדה ושעה: " + Arrays.toString(post) + "\n";
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

	public void mutate(int[] range) {
		Random r = new Random();
		if (r.nextFloat() > 0.5) {
			int location = r.nextInt(range.length);
			int post = r.nextInt(range[location]);
			setPost(new int[] {location, post});
		} else {
			setPost(new int[] {(int) (getPost()[0]*(r.nextFloat()*(r.nextFloat()+1))), (int) (getPost()[1]*(r.nextFloat()*(r.nextFloat()+1)))});
		}

	}

	public Profile duplicate() {
		return new Profile(name, priority, preference, post);
	}

	public double calculateFitness(int range[]) {
		int stationDiff = Math.abs(preference[0]-post[0])+1;
		int timeDiff = Math.abs(preference[1]-post[1])+1;
		fitness = (500*priority/stationDiff)-(100/range.length-1);
		fitness += (100*priority/timeDiff);//TODO see what we do with that -(100/range[1]);
		return (fitness);
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public void switchWith(Profile profile) {
		int[] oldPost = post;
		setPost(profile.getPost());
		profile.setPost(oldPost);
	}

	public void edit(String name, float priority, int[] preference, int[] post) {
		this.setName(name);
		this.setPriority(priority);
		this.setPreference(preference);
		this.setPost(post);
	}

	@Override
	public int compareTo(Profile arg0) {
		return (post[0]-arg0.getPost()[0])+(post[1]-arg0.getPost()[1]);
	}
}
