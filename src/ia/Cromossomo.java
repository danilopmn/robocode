package ia;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Cromossomo {

	final float MAX_BULLET_POWER = 3;
	final int MAX_VELOCITY = 8;
	final float MAX_DEGREE = 30;
	final int MAX_SIZE = 8;
	final int DIRECTIONS = 8;
	Pair[][][] genes;
	private Random random;
	public Cromossomo(){
		random = new Random();
		this.genes = new Pair[MAX_VELOCITY+1][DIRECTIONS][MAX_SIZE+1];
	}
	
	public Cromossomo crossOver(Cromossomo x){
		Cromossomo y = new Cromossomo();
		y.genes = genes.clone();
		int velocityLimit = random.nextInt(MAX_VELOCITY)+1;
		int directionsLimit = random.nextInt(DIRECTIONS-1)+1;
		int sizeLimit = random.nextInt(MAX_SIZE)+1;
		for(int i = 0; i < velocityLimit; i++){
			for(int h = 0; h < directionsLimit; h++){
				for(int z = 0; z < sizeLimit; z++){
					y.genes[i][h][z] = x.genes[i][h][z];
				}
			}
		}
		return y;
	}
	
	public Cromossomo mutacao(){
		Cromossomo x = new Cromossomo();
		x.genes = this.genes.clone();
		for(int i = 0; i <= MAX_VELOCITY; i++){
			for(int h = 0; h < DIRECTIONS; h++){
				for(int z = 0; z <= MAX_SIZE; z++){
					if(random.nextDouble() < 0.01){
						double newFirst = x.genes[i][h][z].first+random.nextDouble()-random.nextDouble();
						newFirst = Math.max(newFirst,0);
						newFirst = Math.min(newFirst,MAX_BULLET_POWER);
						double newSecond = x.genes[i][h][z].second+random.nextDouble()-random.nextDouble();
						newSecond = Math.max(newSecond,0);
						newSecond = Math.min(newSecond,MAX_DEGREE);
						x.genes[i][h][z] = new Pair(newFirst,newSecond);
					}
				}
			}
		}
		return x;
	}
	
	public void randomPopulate(){
		Random random = new Random();
		for(int i = 0; i <= MAX_VELOCITY; i++){
			for(int h = 0; h < DIRECTIONS; h++){
				for(int z = 0; z <= MAX_SIZE; z++){
					this.genes[i][h][z] = new Pair(random.nextDouble()+random.nextInt(3),random.nextDouble()+random.nextInt(61)-30);
				}
			}
		}
	}
	
	public String toString(){
		String saida = "";
		for(int i = 0; i <= MAX_VELOCITY; i++){
			for(int h = 0; h < DIRECTIONS; h++){
				for(int z = 0; z <= MAX_SIZE; z++){
					saida += "this.slot["+i+"]["+h+"]["+z+"] = new Pair("+this.genes[i][h][z].first+","+this.genes[i][h][z].second+");\n";
					
				}
			}
		}
		return saida;
	}

	class Pair{
		double first;
		double second;
		public Pair(double a , double b){
			this.first = a;
			this.second = b;
		}
		
		public String toString(){
			String saida =  String.valueOf(first) + " " + String.valueOf(second);
			return saida;
		}
	}
	
}
