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

	public int hasDuplicates() {//TODO change return to not have the not sign and change every function using this method to accommodate
		int duplicates=0;
		for (int j=0;j<profiles.length;j++)
		  for (int k=j+1;k<profiles.length;k++)
		    if (k!=j && Arrays.equals(profiles[k].getPost() ,profiles[j].getPost()))
		      duplicates++;
		return duplicates;
	}

	public int calculateFitness(int range[]) {
		int tempFitness = 0;
		for (int i = 0; i < profiles.length; i++) {
			tempFitness+=profiles[i].calculateFitness(range);
		}
		tempFitness/=profiles.length;
		int duplicates = hasDuplicates();
		if (duplicates>0) {
			tempFitness/=duplicates+1;
		}
		return tempFitness;
	}
}
