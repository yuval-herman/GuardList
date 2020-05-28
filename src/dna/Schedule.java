package dna;

import java.util.Arrays;
import java.util.Random;

public class Schedule {

	private Profile[] profiles;

	public Schedule(Profile[] profiles) {
		this.setProfiles(profiles);
	}

	@Override
	public String toString() {
		return "Schedule [\nprofiles=" + Arrays.toString(profiles) + "\n]";
	}

	public Profile[] getProfiles() {
		return profiles;
	}

	public void setProfiles(Profile[] profiles) {
		this.profiles = profiles;
	}

	public void mutate(double mutChance) {
		Random r = new Random();
		for (int i = 0; i < profiles.length; i++) {
			if(r.nextDouble() < mutChance) {
				profiles[i].switchWith(profiles[r.nextInt(5)]);
			}
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

	public boolean evaluate() {
		boolean duplicates=false;
		for (int j=0;j<profiles.length;j++)
		  for (int k=j+1;k<profiles.length;k++)
		    if (k!=j && Arrays.equals(profiles[k].getPost() ,profiles[j].getPost()))
		      duplicates=true;
		return !duplicates;
	}

	public int calculateFitness(int range[]) {
		int tempFitness = 0;
		for (int i = 0; i < profiles.length; i++) {
			tempFitness+=profiles[i].calculateFitness(range);
		}
		tempFitness/=profiles.length;
		return tempFitness;
	}
}
