package ia;


import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Scanner;

import ia.Cromossomo.Pair;
import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

public class Wolverine extends AdvancedRobot {
	Random random;
	int moveParaFrente;
	int dist = 50; // distance to move when we're hit
	final float MAX_BULLET_POWER = 3;
	final int Q_VELOCITY = 2;
	final float MAX_DEGREE = 30;
	final int Q_DISTANCE = 9;
	final int DIRECTIONS = 2;
	double enemyX;
	double enemyY;
	double lastEnemyX;
	int scanDirection;
	double lastEnemyY;
	double enemyAbsoluteHeading;
	Pair[][][] slot;
	
	public Wolverine() {
		this.slot = new Pair[Q_VELOCITY][DIRECTIONS][Q_DISTANCE];
	    fillSlot();
		this.random = new Random();
		this.moveParaFrente = 1;
		this.scanDirection = 1;
	}

	/**
	 * run:  Fire's main run function
	 */
	public void run() {
		// Set colors
		setBodyColor(Color.pink);
		setGunColor(Color.pink);
		setRadarColor(Color.pink);
		setScanColor(Color.pink);
		setBulletColor(Color.pink);
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);
		// Spin the gun around slowly... forever
		while (true) {
			setTurnRadarRight(360 * scanDirection);
			moveAleatorio();
			execute();
		}
	}
	
	public void onHitWall(HitWallEvent e) {
		this.moveParaFrente = (this.moveParaFrente + 1)%2; 
	}

	void moveAleatorio(){
		if(random.nextInt(5) == 1){
			int distancia  = random.nextInt(100);
			if(moveParaFrente == 1)
				setAhead(distancia);
			else 
				setBack(distancia);
			int graus = random.nextInt(60) - 30;
			setTurnRight(graus);
		}
	}
	
	private boolean andandoDireita(ScannedRobotEvent e) {
		double Ax = (enemyX-lastEnemyX);
		double Ay = (enemyY-lastEnemyY);
		double Bx = (enemyX-getX());
		double By = (enemyX-getY());
		return Ax*By - Ay*Bx >= 0;
	}
	
	private int distanceCategory(double distance){
		int category = (int) distance/8;
		category = Math.min(category,8);
		return category;
	}
	
	private int velocityCategory(double velocity){
		return velocity > 0.1 ? 1 : 0;
	}
	/**
	 * onScannedRobot:  Fire!
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		update(e);		
		
		boolean direita = andandoDireita(e);
		int distanceCat = distanceCategory(e.getDistance());
		int pos = direita ? 1 : 0;
		int velocity = velocityCategory(e.getVelocity());
		Pair gene = slot[velocity][pos][distanceCat];
		double turnGunAmt = normalRelativeAngleDegrees(gene.second + e.getBearing() + getHeading() - getGunHeading());
		setTurnGunRight(turnGunAmt);
		if(getGunHeat() == 0 && turnGunAmt < 5 && gene.first >= 0.1){
			fire(gene.first);
		}
		
		scanDirection *= -1;
		setTurnRadarRight(360 * scanDirection);
	}

	/**
	 * onHitByBullet:  Turn perpendicular to the bullet, and move a bit.
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		
	}
	
	private void update(ScannedRobotEvent e) {
	    double absBearingDeg = (getHeading() + e.getBearing());
	    if (absBearingDeg < 0) {
	      absBearingDeg += 360;
	    }
	    lastEnemyX = enemyX;
	    lastEnemyY = enemyY;
	    enemyX = getX() + Math.sin(Math.toRadians(absBearingDeg)) * e.getDistance();
	    enemyY = getY() + Math.cos(Math.toRadians(absBearingDeg)) * e.getDistance();
	    enemyAbsoluteHeading = absoluteBearing(lastEnemyX,lastEnemyY,enemyX,enemyY);
	    
	}
	
	double absoluteBearing(double x1, double y1, double x2, double y2) {
		double xo = x2-x1;
		double yo = y2-y1;
		double hyp = Point2D.distance(x1, y1, x2, y2);
		double arcSin = Math.toDegrees(Math.asin(xo / hyp));
		double bearing = 0;

		if (xo > 0 && yo > 0) { // both pos: lower-Left
			bearing = arcSin;
		} else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
			bearing = 360 + arcSin; // arcsin is negative here, actuall 360 - ang
		} else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
			bearing = 180 - arcSin;
		} else if (xo < 0 && yo < 0) { // both neg: upper-right
			bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang
		}

		return bearing;
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
	
	private void fillSlot(){this.slot[0][0][0] = new Pair(2.120772062244663,22.47026231935029);
this.slot[0][0][1] = new Pair(0.7847853301432529,0.0);
this.slot[0][0][2] = new Pair(1.458203489632354,0.0);
this.slot[0][0][3] = new Pair(0.2355477621024259,-13.56262701711643);
this.slot[0][0][4] = new Pair(2.7995215450400694,25.51903892429295);
this.slot[0][0][5] = new Pair(0.8456228415835622,-0.3043250713043655);
this.slot[0][0][6] = new Pair(1.158906295627165,0.0);
this.slot[0][0][7] = new Pair(2.0,15.0);
this.slot[0][0][8] = new Pair(1.9525126710571112,17.084919566890825);
this.slot[0][1][0] = new Pair(3.0,26.163553544681733);
this.slot[0][1][1] = new Pair(2.32350002247753,21.554500652588796);
this.slot[0][1][2] = new Pair(2.9399095476134716,25.095369303381844);
this.slot[0][1][3] = new Pair(1.8806281741511093,19.6809989590718);
this.slot[0][1][4] = new Pair(3.0,25.738828862816725);
this.slot[0][1][5] = new Pair(2.0,20.0);
this.slot[0][1][6] = new Pair(1.6653253520395208,20.999067599601936);
this.slot[0][1][7] = new Pair(1.8929094824639066,20.974633019887595);
this.slot[0][1][8] = new Pair(1.142101465762745,22.548842469936222);
this.slot[1][0][0] = new Pair(2.19636314306977,19.76643787322123);
this.slot[1][0][1] = new Pair(3.0,10.0);
this.slot[1][0][2] = new Pair(0.0,0.0);
this.slot[1][0][3] = new Pair(2.735017038600521,19.683465007055805);
this.slot[1][0][4] = new Pair(2.0,15.0);
this.slot[1][0][5] = new Pair(1.8331808874279707,19.663540359726614);
this.slot[1][0][6] = new Pair(2.0,20.0);
this.slot[1][0][7] = new Pair(2.0,20.0);
this.slot[1][0][8] = new Pair(2.0,20.0);
this.slot[1][1][0] = new Pair(3.0,21.12828730446941);
this.slot[1][1][1] = new Pair(3.0,19.25421444737082);
this.slot[1][1][2] = new Pair(2.8329643526589794,20.09690234561);
this.slot[1][1][3] = new Pair(3.0,20.0);
this.slot[1][1][4] = new Pair(3.0,19.54818102644323);
this.slot[1][1][5] = new Pair(2.5737750576667358,20.662152700390862);
this.slot[1][1][6] = new Pair(3.0,20.0);
this.slot[1][1][7] = new Pair(3.0,19.22342102507016);
this.slot[1][1][8] = new Pair(3.0,21.911837882020038);
}}
