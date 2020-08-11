package GuardListApp;

import dna.Profile;
import dna.Schedule;

public class ProfileData {
	private Profile[] connnectedProfiles;
	private Schedule schedule;
	private boolean isAdmin;
	public ProfileData(Profile[] connnectedProfiles, Schedule schedule, boolean isAdmin) {
		this.connnectedProfiles = connnectedProfiles;
		this.schedule = schedule;
		this.isAdmin = isAdmin;
	}
	public Profile[] getConnnectedProfiles() {
		return connnectedProfiles;
	}
	public void setConnnectedProfiles(Profile[] connnectedProfiles) {
		this.connnectedProfiles = connnectedProfiles;
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

}