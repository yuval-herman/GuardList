package GuardListApp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;

import dna.Population;
import dna.Profile;
import dna.Schedule;

public class ConsoleController {
	Population basePopulation;
	Schedule baseSchedule;
	

	
	public ConsoleController(Population basePopulation, Schedule baseSchedule) {
		this.basePopulation = basePopulation;
		this.baseSchedule = baseSchedule;
	}
	
	public ConsoleController() {
	}
	
	public void chooseOperation() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("choose operation:/n");

		System.out.println("1. calculate schedule from current data");
		System.out.println("2. add profile");
		System.out.println("3. edit profile");
		System.out.println("4. edit locations");
		System.out.print("->");
		
		switch (scanner.nextInt()) {
		case 1:
return;
			break;
		case 2:
			Profile[] array = new Profile[baseSchedule.getProfiles().length + 1];
			System.arraycopy(array, 0, baseSchedule.getProfiles(), 0, baseSchedule.getProfiles().length);
			array[array.length] = new Profile();

			break;
		case 3:

			break;
		case 4:

			break;

		default:
			break;
		}
	}
	
	public void loadPopulation(String file) {
		ObjectInputStream in;
		Population pop = null;
		try {
			in = new ObjectInputStream(new FileInputStream(file));
			pop = (Population) in.readObject();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		this.basePopulation = pop;
	}
	
	public void loadSchedule(String file) {
		ObjectInputStream in;
		Schedule schedule = null;
		try {
			in = new ObjectInputStream(new FileInputStream(file));
			schedule = (Schedule) in.readObject();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		this.baseSchedule = schedule;
	}
}
