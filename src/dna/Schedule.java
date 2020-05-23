package dna;

import java.util.ArrayList;

public class Schedule {

	private Profile[] profiles;

	public Schedule(Profile[] profiles) {
		this.setProfiles(profiles);
	}

	public Profile[] getProfiles() {
		return profiles;
	}

	public void setProfiles(Profile[] profiles) {
		this.profiles = profiles;
	}
}
