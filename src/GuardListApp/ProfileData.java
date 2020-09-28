package GuardListApp;

import dna.Profile;
import dna.Schedule;

public class ProfileData {
	private ProfileData[] connnectedProfiles;
	private Profile profile;
	private Schedule schedule;
	private boolean isAdmin;
	
	public ProfileData(ProfileData[] connnectedProfiles, Profile profile, Schedule schedule, boolean isAdmin) {
		this.connnectedProfiles = connnectedProfiles;
		this.profile = profile;
		this.schedule = schedule;
		this.isAdmin = isAdmin;
	}
	public ProfileData[] getConnnectedProfiles() {
		return connnectedProfiles;
	}
	public void setConnnectedProfiles(ProfileData[] connnectedProfiles) {
		this.connnectedProfiles = connnectedProfiles;
	}
	public Profile getProfile() {
		return profile;
	}
	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	public Schedule getSchedule() {
		return schedule;
	}
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
	public boolean isAdmin() {
		return isAdmin;
	}
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	public void addConnnectedProfile(ProfileData profileData) {
		ProfileData[] connnectedProfiles2 = new ProfileData[connnectedProfiles.length + 1];
		System.arraycopy(connnectedProfiles, 0, connnectedProfiles2, 0, connnectedProfiles.length);
		connnectedProfiles2[connnectedProfiles.length] = profileData;
		this.connnectedProfiles = connnectedProfiles2;
	}

}