package dna;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public class Schedule implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3557971571091428047L;
	private Profile[] profiles;
	private int range[];

	public int[] getRange() {
		return range;
	}

	public void setRange(int[] range) {
		this.range = range;
	}

	public Schedule(Profile[] profiles, int range[]) {
		this.setProfiles(profiles);
		this.setRange(range);
	}

	public Schedule() {
		// TODO Auto-generated constructor stub
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

	public void mutate(double mutChance, int[] range) {
		Random r = new Random();
		for (int i = 0; i < profiles.length; i++) {
			if(r.nextDouble() < mutChance) {
				profiles[i].mutate(range);;
			}
		}
	}
	
	public Schedule duplicate() {
		Profile[] newProfiles = new Profile[profiles.length];
		for (int i = 0; i < profiles.length; i++) {
			newProfiles[i] = profiles[i].duplicate();
		}
		Schedule dupli = new Schedule(newProfiles, range);
		return dupli;
	}

	public int hasDuplicates() {
		int duplicates=0;
		for (int j=0;j<profiles.length;j++)
		  for (int k=j+1;k<profiles.length;k++)
		    if (k!=j && Arrays.equals(profiles[k].getPost() ,profiles[j].getPost()))
		      duplicates++;
		return duplicates;
	}
	
	private int[] LocationSum() {
		int[] tempRange = new int[range[0]];
		for (int i = 0; i < tempRange.length; i++) {
			for (int j = 0; j < profiles.length; j++) {
				if (profiles[j].getPost()[0]==i) {
					tempRange[i]++;
				}
			}
		}
		return tempRange;
	}

	public int calculateFitness() { //TODO needs to make sure there are enough people in all the locations
		int tempFitness = 0;
		for (int i = 0; i < profiles.length; i++) {
			tempFitness+=profiles[i].calculateFitness(range);
		}
		tempFitness/=profiles.length;
		int duplicates = hasDuplicates();
		if (duplicates>0) {
			tempFitness/=duplicates+1;
		}
		int[] tempRange = LocationSum();
		for (int i = 0; i < tempRange.length; i++) {
			tempFitness/=Math.abs(tempRange[i]-range[1])+1;
		}
		return tempFitness;
	}

	public boolean evaluate() {
		boolean fullPosts = true;
		for (int num : LocationSum()) {
			if (num!=range[1]) fullPosts=false;
		}
		return hasDuplicates()==0 && fullPosts;
	}

	public void saveState(String file) {
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(this);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
