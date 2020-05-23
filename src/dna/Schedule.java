package dna;

import java.util.ArrayList;

public class Schedule {

	ArrayList<Profile> profiles;

	public Schedule(ArrayList<Profile> profiles) {
		this.profiles = profiles;
	}
	
	public Schedule() {
		this.profiles = new ArrayList<Profile>();
	}
}
