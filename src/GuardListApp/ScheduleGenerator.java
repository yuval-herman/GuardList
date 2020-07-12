package GuardListApp;

import dna.Profile;
import dna.Schedule;

public class ScheduleGenerator {

	public Schedule generateSchedule(Profile[] profiles, int[] range) {
		return new Schedule(profiles, range);
	}
	
	public Schedule generateSchedule(String str, int[] range) {
		//name,priority, preference
		String[] dataLines = str.split("\\r?\\n");
		Profile[] profiles = new Profile[dataLines.length];
		for (int i = 0; i < dataLines.length; i++) {
			String[] data = dataLines[i].split(",");
			String[] preference = data[2].split(":");

			profiles[i] = new Profile(data[0],
					Float.valueOf(data[1]),
					new int[]{Integer.valueOf(preference[0]),Integer.valueOf(preference[1])});
		}
		return new Schedule(profiles, range);
	}
}
