package dna;

import java.util.Random;

public class Profile {

	private static int number; //for testing purposes
	private String name;
	private float priority; //the algorithm priority to satisfy the preference
	private int[] Preference; //the preference for a post TODO make a 2D array to keep more then one preference
	private int[] post; //first cell for what post, second for time
	
	public Profile(String name, float priority, int[] preference, int[] post) {
		number++;
		this.setName(name);
		this.setPriority(priority);
		this.setPreference(preference);
		this.setPost(post);
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
		return Preference;
	}

	public void setPreference(int[] preference) {
		Preference = preference;
	}

	public Profile() {
		number++;
		Random r = new Random();
		this.setName(String.valueOf(number));
		this.priority = r.nextFloat();
		int[] temp = {r.nextInt(5), r.nextInt(5), r.nextInt(5)};
		Preference = temp;
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
}
