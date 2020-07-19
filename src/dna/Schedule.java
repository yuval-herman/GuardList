package dna;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Schedule implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3557971571091428047L;
	private Profile[] profiles;
	private int range[];

	public void addProfile(Profile newProfile) {
		Profile[] profiles2 = new Profile[profiles.length + 1];
		System.arraycopy(profiles, 0, profiles2, 0, profiles.length);
		profiles2[profiles.length] = newProfile;
		setProfiles(profiles2);
	}
	
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
		return "Schedule [\nprofiles=\n" + Arrays.toString(profiles) + ", range=" + Arrays.toString(range) + "\n]";
	}
	
	public static Schedule fromString(String scheduleString) {
		int[] range = null;
		ArrayList<Profile> profiles = new ArrayList<Profile>();
		scheduleString=scheduleString.replaceAll("\\s","");
		String[] data = scheduleString.split("\\[|\\]|,");
		for(int i = 0; i < data.length; i++){
			switch (data[i]) {
			case "Profile":
				profiles.add(new Profile(
						data[i+1].split("=")[1],
						Float.valueOf(data[i+2].split("=")[1]),
						new int[]{Integer.valueOf(data[i+4]),Integer.valueOf(data[i+5])},
						null
						));
				break;

			case "range=":
				range=new int[] {Integer.valueOf(data[i+1]), Integer.valueOf(data[i+2])};
				break;

			default:
				break;
			}
		}
		return new Schedule(profiles.toArray(new Profile[0]), range);
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
		int[] tempRange = new int[range.length];
		for (int i = 0; i < tempRange.length; i++) {
			for (int j = 0; j < profiles.length; j++) {
				if (profiles[j].getPost()[0]==i) {
					tempRange[i]++;
				}
			}
		}
		return tempRange;
	}
	
	private boolean locationFull() {
		for (int i = 0; i < range.length; i++) {
			for (int j = 0; j < range[i]; j++) {
				int fullLoc=0;
				for (int j2 = 0; j2 < profiles.length; j2++) {
					if (fullLoc==range[i]) break;

					if (profiles[j2].getPost()[0]==i && profiles[j2].getPost()[1]==fullLoc) {
						fullLoc++;
						j2=-1;
					}
				}
				if (fullLoc!=range[i]) return false;
			}
		}
		return true;
	}

	public double calculateFitness() { //TODO needs to make sure there are enough people in all the locations
		double tempFitness = 0;
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
			tempFitness/=Math.abs(tempRange[i]-range[i])+1;
		}
		return tempFitness;
	}

	public boolean evaluate() {
		return hasDuplicates()==0 && Arrays.equals(range, LocationSum()) && locationFull();
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

	public Profile findByName(String name) {
		for (Profile profile : profiles) {
			if (profile.getName().equals(name)) {
				return profile;
			}
		}
		return null;
	}

	public void editProfile(Profile newpProfile, int profileNumber) {
		// TODO Auto-generated method stub
		
	}

	public String hebtoString() {
		String retString = "שבצ\"ק:\n";
		for (int i = 0; i < profiles.length; i++) {
			retString += profiles[i].hebToString();
		}
		return retString;
	}
}
