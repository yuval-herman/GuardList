package dna;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;


public class Population implements Serializable{//Comparator<Dna>,

	/**
	 * 
	 */
	private static final long serialVersionUID = 8857517385334250613L;
	private ArrayList<Dna> population;
	private int range[];

	public Population(ArrayList<Dna> population,int range[]) {
		this.setPopulation(population);
		this.setRange(range);
	}
	
	public Population(int range[]) {
		this.setPopulation(new ArrayList<Dna>());
		this.setRange(range);
	}

	public Population() {
		// TODO Auto-generated constructor stub
	}

	public static Population loadState(String file) {
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
		return pop;
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
	
	public int[] getRange() {
		return range;
	}

	public void setRange(int[] range) {
		this.range = range;
	}

	public ArrayList<Dna> getPopulation() {
		return population;
	}

	public void setPopulation(ArrayList<Dna> population) {
		this.population = population;
	}
	
	public void generatePopulation(int popSize, Schedule schedule) {
		if(range == null) range = schedule.getRange();
		population = new ArrayList<Dna>(); //new population
		Random r = new Random();
		for(int i = 0; i < popSize; i++) { //deep copy profiles
			Profile[] temp = new Profile[schedule.getProfiles().length];
			/*
			List<Integer> psTimeArr = new ArrayList<Integer>();
			List<Integer> pslocArr = new ArrayList<Integer>();
			
			for (int k = 1; k<=range[1]; k++) {
				psTimeArr.add(k);
			}
			Collections.shuffle(psTimeArr);
			
			for (int k = 1; k<=range[0]; k++) {
				pslocArr.add(k);
			}
			Collections.shuffle(pslocArr);*/

			for (int j = 0 ; j<temp.length; j++) {
				temp[j] = schedule.getProfiles()[j].duplicate();
				int location = r.nextInt(range.length);
				int post = r.nextInt(range[location]);
				temp[j].setPost(new int[] {location, post});
			}

			population.add(new Dna(0, new Schedule(temp, range)));
		}
	}
	
	public void calculateFitness() {
		for (Dna dna : population) {
			dna.calculateFitness();
		}
	}
	
	public void prettyPrintDna(Dna dna, int timeScale, Date beginDate) {
//		System.out.println("\n\nfitness: "+dna.getFitness());
//		System.out.println("--------------------------");
		Profile[] tempProfiles = dna.getGenome().getProfiles();
		Arrays.sort(tempProfiles);
		for (Profile profile : tempProfiles) {
			Date displayTimePost = new
					Date((beginDate.getTime()+ 3600000*(timeScale/range[profile.getPost()[0]]*
							profile.getPost()[1])));
			Date displayTimePreference = new
					Date((beginDate.getTime()+ 3600000*(timeScale/range[profile.getPreference()[0]]*
							profile.getPreference()[1])));
//			System.out.print(profile.getName()+"    |    prefer: ");
//			System.out.print(profile.getPreference()[0]+":"+profile.getPreference()[1]+"->"+displayTimePreference.getHours()+":"+displayTimePreference.getMinutes()+"    |    post: ");
//			System.out.println(profile.getPost()[0]+":"+profile.getPost()[1]+"->"+displayTimePost.getHours()+":"+displayTimePost.getMinutes());
		}
		int algPosts = 0;
		int algLocs = 0;
		int algPostLocs = 0;
		for (int i = 0; i < tempProfiles.length; i++) {
			if (tempProfiles[i].getPreference()[0] == tempProfiles[i].getPost()[0]) {
				algPosts++;
			}
			if (tempProfiles[i].getPreference()[1] == tempProfiles[i].getPost()[1]) {
				algLocs++;
			}if (tempProfiles[i].getPreference()[0] == tempProfiles[i].getPost()[0] && tempProfiles[i].getPreference()[1] == tempProfiles[i].getPost()[1]) {
				algPostLocs++;
			}
		}
//		System.out.println("\naligned posts: "+algPosts);
//		System.out.println("aligned locations: "+algLocs);
//		System.out.println("aligned posts and locations: "+algPostLocs);
	}
	
	/*public Dna[] crossover() {
		sortByFitness();
		
		ArrayList<Dna> newpop = new ArrayList<Dna>();
		for (int i = 0; i < population.size(); i++) {
			newpop.add(population.get();
		}
		/*for(int i = population.size()-1; i >= population.size()/2; i--) {
			for (int j = 0; j <= population.get(i).fitness; j+=2) {
				newpop.add(population.get(i).duplicate());
			}
		}
		return newpop.toArray(new Dna[newpop.size()]);
	}*/
	
    private int randomNumberBias(int max, int min, double probabilityPower)
    {
    	Random r = new Random();
        double randomDouble = r.nextDouble();

        return (int) Math.floor(max + (min + 1 - max) * (Math.pow(randomDouble, probabilityPower))); //Math.floor(min + (max + 1 - min) * (Math.pow(randomDouble, probabilityPower)));
    }
	
	public void newGeneration() {
		ArrayList<Dna> newpop = new ArrayList<Dna>();
		for(int i =0; i<population.size(); i++) {
			Dna tempDna = population.get(randomNumberBias(population.size()-1, 0, 2))
					.crossover(population.get(randomNumberBias(population.size()-1, 0, 2)));
			tempDna.calculateFitness();
			newpop.add(tempDna);
		}
		population=newpop;
	}
	
	public void mutation(double mutChance) {
		for (int i = 0; i < population.size(); i++) {
			population.get(i).getGenome().mutate(mutChance, range); 
		}
	}
	
	public void printFitness() {
		ArrayList<Double> fitnesArr = new ArrayList<Double>();
		for (Dna dna : population) {
			fitnesArr.add(dna.fitness);
		}
		fitnesArr.sort(null);
		for(int i = 0; i < fitnesArr.size() / 2; i++)
		{
			double temp = fitnesArr.get(i);
			fitnesArr.set(i, fitnesArr.get(fitnesArr.size() - i - 1));
			fitnesArr.set(fitnesArr.size() - i - 1, temp);
		}
//		System.out.println(fitnesArr);
		for(int i=0; i<=100;i++) {
			
		}
	}

	public void printEvaluate() {
		int counter = 0;
		for (Dna dna : population) {
			if(dna.evaluate()) {
//				System.out.println(dna);
				counter++;
			}
		}
//		System.out.println(counter + " possible schedules");
	}
	
	public Dna[] evaluate() {
		ArrayList<Dna> dnaArray = new ArrayList<Dna>();
		for (Dna dna : population) {
			if(dna.evaluate()) {
				dnaArray.add(dna);
			}
		}
		return dnaArray.toArray(new Dna[0]);
	}
	
	public void devaluate() {
		for (Dna dna : population) {
			if(!dna.evaluate()) {
//				System.out.println(dna);
			}
		}
	}
	
	public void evaluate(ArrayList<Dna> pop) {
		for (Dna dna : pop) {
			if(dna.evaluate()) {
//				System.out.println(dna);
			}
		}
	}
	
	public boolean checkError(ArrayList<Dna> pop) {
		for (Dna dna : pop) {
			if(!dna.evaluate()) {
//				System.out.println(dna);
				return false;
			}
		}
		return true;
	}
	
	public boolean checkError() {
		for (Dna dna : population) {
			if(!dna.evaluate()) {
//				System.out.println(dna);
				return false;
			}
		}
		return true;
	}
	
	public void sortByFitness() {
		Dna[] temp = population.toArray(new Dna[population.size()]);
		Arrays.sort(temp);
		population.clear();
		population=new ArrayList<Dna>(Arrays.asList(temp));
	}
	
	@Override
	public String toString() {
		return "Population [\npopulation=" + population + "\n]";
	}

	public void saveSchedule(String filename, Dna eval) {
		try {
		      File myObj = new File(filename);
		      if (myObj.createNewFile()) {
//		        System.out.println("File created: " + myObj.getName());
		      } else {
//		        System.out.println(filename+" file already exists. Overwriting...");
		      }
		    } catch (IOException e) {
//		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		try {
		      FileWriter myWriter = new FileWriter(filename);
		      myWriter.write(eval.toString());
		      myWriter.close();
//		      System.out.println("Successfully wrote to the file.");
		    } catch (IOException e) {
//		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	}
}
