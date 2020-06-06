package dna;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class Population implements  Serializable{//Comparator<Dna>,

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

	public static Population loadState(String file) {
	    ObjectInputStream in;
	    Population pop = null;
		try {
			in = new ObjectInputStream(new FileInputStream(file));
			pop = (Population) in.readObject();
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
	
	public void generatePopulation(int popSize, Profile[] profiles) {
		population = new ArrayList<Dna>(); //new population
		for(int i = 0; i < popSize; i++) { //deep copy profiles
			Profile[] temp = new Profile[profiles.length];
			List<Integer> psArr = new ArrayList<Integer>();
			for (int k = 1; k<=range[1]; k++) psArr.add(k);
			Collections.shuffle(psArr);
			for (int j = 0 ; j<temp.length; j++) {
				temp[j] = profiles[j].duplicate();
				temp[j].setPost(new int[] {1, psArr.get(j)});
			}
			
			population.add(new Dna(0, new Schedule(temp)));
		}
	}
	
	public void calculateFitness() {
		for (Dna dna : population) {
			dna.calculateFitness(range);
		}
	}
	
	public Dna[] crossover() {
		sortByFitness();
		
		ArrayList<Dna> newpop = new ArrayList<Dna>();
		for (int i = 0; i < population.size(); i++) {
			newpop.add(population.get(GetRandomNumberLowBias(population.size()-1, 0, 2)).duplicate());
		}
		/*for(int i = population.size()-1; i >= population.size()/2; i--) {
			for (int j = 0; j <= population.get(i).fitness; j+=2) {
				newpop.add(population.get(i).duplicate());
			}
		}*/
		return newpop.toArray(new Dna[newpop.size()]);
	}
	
    private int GetRandomNumberLowBias(int max, int min, double probabilityPower)
    {
    	Random r = new Random();
        double randomDouble = r.nextDouble();

        return (int) Math.floor(max + (min + 1 - max) * (Math.pow(randomDouble, probabilityPower))); //Math.floor(min + (max + 1 - min) * (Math.pow(randomDouble, probabilityPower)));
    }
	
	public void newGeneration(Dna[] temppop) {
		ArrayList<Dna> newpop = new ArrayList<Dna>();
		Random r = new Random();
		for(int i =0; i<population.size(); i++) {
			newpop.add(temppop[r.nextInt(temppop.length)].crossover(temppop[r.nextInt(temppop.length)]));
		}
		population=newpop;
	}
	
	public void mutation(double mutChance) {
		for (int i = 0; i < population.size(); i++) {
			population.get(i).genome.mutate(mutChance); 
		}
	}
	
	public void printFitness() {
		ArrayList<Integer> fitnesArr = new ArrayList<Integer>();
		for (Dna dna : population) {
			fitnesArr.add(dna.fitness);
		}
		fitnesArr.sort(null);
		for(int i = 0; i < fitnesArr.size() / 2; i++)
		{
			int temp = fitnesArr.get(i);
			fitnesArr.set(i, fitnesArr.get(fitnesArr.size() - i - 1));
			fitnesArr.set(fitnesArr.size() - i - 1, temp);
		}
		System.out.println(fitnesArr);
		for(int i=0; i<=100;i++) {
			
		}
	}

	public void evaluate() {
		int counter = 0;
		for (Dna dna : population) {
			if(dna.evaluate()) {
				System.out.println(dna);
				counter++;
			}
		}
		System.out.println(counter + " possible schedules");
	}
	
	public void devaluate() {
		for (Dna dna : population) {
			if(!dna.evaluate()) {
				System.out.println(dna);
			}
		}
	}
	
	public void evaluate(ArrayList<Dna> pop) {
		for (Dna dna : pop) {
			if(dna.evaluate()) {
				System.out.println(dna);
			}
		}
	}
	
	public boolean checkError(ArrayList<Dna> pop) {
		for (Dna dna : pop) {
			if(!dna.evaluate()) {
				System.out.println(dna);
				return false;
			}
		}
		return true;
	}
	
	public boolean checkError() {
		for (Dna dna : population) {
			if(!dna.evaluate()) {
				System.out.println(dna);
				return false;
			}
		}
		return true;
	}
	
	public boolean checkError(Profile[] original) {
		for (Dna dna : population) {
			if(!dna.evaluate(original)) {
				System.out.println(dna);
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
}
