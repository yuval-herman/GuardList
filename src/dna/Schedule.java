package dna;

import java.util.ArrayList;

public class Schedule {

	private Profile[] profiles;

	public Schedule(Profile[] profiles) {
		this.setProfiles(profiles);
	}
	
	public Schedule(int num) { //TODO make profile array
		this.setProfiles(profiles);
	}

	public Profile[] getProfiles() {
		return profiles;
	}

	public void setProfiles(Profile[] profiles) {
		this.profiles = profiles;
	}

	public void mutate(double mutChance) {
		for (int i = 0; i < profiles.length; i++) {
			profiles[i].mutate(mutChance);
		}
	}

	public Schedule duplicate() {
		Profile[] newProfiles = new Profile[profiles.length];
		for (int i = 0; i < profiles.length; i++) {
			newProfiles[i] = profiles[i].duplicate();
		}
		Schedule dupli = new Schedule(newProfiles);
		return dupli;
	}
}
