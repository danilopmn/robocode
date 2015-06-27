package ia;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Cromossomo {

	protected static final float MAX_BULLET_POWER = 3;
	protected static final int VELOCITY = 2;
	protected static final float MAX_DEGREE = 30;
	protected static final int DISTANCE = 9;
	protected static final int DIRECTIONS = 2;
	protected Pair[][][] genes;
	private Random random;
	
	public Cromossomo(){
		random = new Random();
		this.genes = new Pair[VELOCITY][DIRECTIONS][DISTANCE];
	}
	
	public Cromossomo(Cromossomo x){
		this();
		for(int i = 0; i < VELOCITY; i++){
			for(int h = 0; h < DIRECTIONS; h++){
				for(int z = 0; z < DISTANCE; z++){
					this.genes[i][h][z] = x.genes[i][h][z];
				}
			}
		}
	}
	
	public void crossOver(Cromossomo x){
		Cromossomo y = new Cromossomo(this);
		int velocity = random.nextInt(VELOCITY);
		int direction = random.nextInt(DIRECTIONS);
		int sizeLimit = random.nextInt(DISTANCE)+1;
		for(int z = 0; z < sizeLimit; z++){
			this.genes[velocity][direction][z] = x.genes[velocity][direction][z];
			x.genes[velocity][direction][z] = y.genes[velocity][direction][z];
		}
	}
	
	public void mutacao(){
		for(int i = 0; i < VELOCITY; i++){
			for(int h = 0; h < DIRECTIONS; h++){
				for(int z = 0; z < DISTANCE; z++){
					if(random.nextDouble() < 0.05){
						double newFirst = genes[i][h][z].first+random.nextDouble()-random.nextDouble();
						newFirst = Math.max(newFirst,0);
						newFirst = Math.min(newFirst,MAX_BULLET_POWER);
						double newSecond = genes[i][h][z].second+3*random.nextDouble()-3*random.nextDouble();
						newSecond = Math.max(newSecond,0);
						newSecond = Math.min(newSecond,MAX_DEGREE);
						genes[i][h][z] = new Pair(newFirst,newSecond);
					}
				}
			}
		}
	}
	
	public void randomPopulate(){
		Random random = new Random();
		for(int i = 0; i < VELOCITY; i++){
			for(int h = 0; h < DIRECTIONS; h++){
				for(int z = 0; z < DISTANCE; z++){
					this.genes[i][h][z] = new Pair(random.nextDouble()+random.nextInt(3),random.nextDouble()+random.nextInt(61)-30);
				}
			}
		}
	}
	
	public String toString(){
		String saida = "";
		for(int i = 0; i < VELOCITY; i++){
			for(int h = 0; h < DIRECTIONS; h++){
				for(int z = 0; z < DISTANCE; z++){
					saida += "this.slot["+i+"]["+h+"]["+z+"] = new Pair("+this.genes[i][h][z].first+","+this.genes[i][h][z].second+");\n";
					
				}
			}
		}
		return saida;
	}

	public class Pair{
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
