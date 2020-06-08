package GuardListApp;

import dna.Population;
import dna.Schedule;

public class ConsoleController {
	Population basePopulation;
	Schedule baseSchedule;
	public ConsoleController(Population basePopulation, Schedule baseSchedule) {
		this.basePopulation = basePopulation;
		this.baseSchedule = baseSchedule;
	}
	public ConsoleController() {
		baseSchedule = new Schedule();
		basePopulation = new Population();
	}
	
	
}
