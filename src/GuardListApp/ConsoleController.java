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
		this.basePopulation = new Population();
		this.baseSchedule = new Schedule();
	}
	
	public void chooseOperation() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("choose operation:\n");

		System.out.println("1. calculate schedule from current data");
		System.out.println("2. add profile");
		System.out.println("3. edit profile");
		System.out.println("4. edit locations");
		System.out.print("->");
		
		switch (Integer.valueOf(scanner.nextLine())) {
		case 1:
			basePopulation.generatePopulation(400, baseSchedule);
			return;
		case 2:
			System.out.print("\n\nwrite new name: ");
			String name = scanner.nextLine();

			System.out.print("\n\nwrite the priority: ");
			float priority = Float.valueOf(scanner.nextLine());
			
			int[] preference = new int[2];
			System.out.print("\n\nwrite location preference: ");
			preference[0] = Integer.valueOf(scanner.nextLine());
			System.out.print("\nwrite time preference: ");
			preference[1] = Integer.valueOf(scanner.nextLine());
			
			baseSchedule.addProfile(new Profile(name, priority, preference, null));//TODO get user input
			basePopulation.generatePopulation(basePopulation.getPopulation().size(), baseSchedule);
			break;
		case 3:
			System.out.print("\n\nwrite name of profile: ");
			Profile tempProfile = baseSchedule.findByName(scanner.nextLine());
			
			if (tempProfile == null) {
				System.out.println("no profile by that name was found.");
				break;
			}
			
			System.out.print("\n\nwrite new name: ");
			String name1 = scanner.nextLine();

			System.out.print("\n\nwrite the priority: ");
			float priority1 = scanner.nextFloat();
			
			int[] preference1 = new int[2];
			System.out.print("\n\nwrite location preference: ");
			preference1[0] = scanner.nextInt();
			System.out.print("\nwrite time preference: ");
			preference1[1] = scanner.nextInt();
			
			tempProfile.edit(name1, priority1, preference1, null);//TODO get user input
			basePopulation.generatePopulation(basePopulation.getPopulation().size(), baseSchedule);
			break;
		case 4:
			System.out.print("\n\nwrite number of locations and then number of hours: ");
			
			baseSchedule.setRange(new int[] {scanner.nextInt(), scanner.nextInt()});
			basePopulation.generatePopulation(basePopulation.getPopulation().size(), baseSchedule);
			break;

		default:
			return;
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
