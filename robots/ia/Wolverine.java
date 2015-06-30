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
	    if(e.getVelocity() < 0.1){
	    	enemyAbsoluteHeading = absoluteBearing(lastEnemyX,lastEnemyY,enemyX,enemyY);
	    } else {
	    	enemyAbsoluteHeading = e.getHeading();
	    }
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
	
	private void fillSlot(){this.slot[0][0][0] = new Pair(2.6695810925808043,0.0);
this.slot[0][0][1] = new Pair(1.2484280623000013,0.4680936845942564);
this.slot[0][0][2] = new Pair(1.0189276063764832,21.12806706187906);
this.slot[0][0][3] = new Pair(2.9213938276446823,15.880639559546998);
this.slot[0][0][4] = new Pair(1.0260293398730491,0.06624280870067611);
this.slot[0][0][5] = new Pair(1.2260272052344474,0.0);
this.slot[0][0][6] = new Pair(1.422751192457472,0.0);
this.slot[0][0][7] = new Pair(1.9962679855128465,0.0);
this.slot[0][0][8] = new Pair(1.0210365491519977,0.004090669727267016);
this.slot[0][1][0] = new Pair(0.8738449718208164,0.0);
this.slot[0][1][1] = new Pair(3.0,25.93159348580298);
this.slot[0][1][2] = new Pair(0.15309800551985586,13.51039790665573);
this.slot[0][1][3] = new Pair(1.2973903996194833,0.369972889788025);
this.slot[0][1][4] = new Pair(2.2055683003165694,1.0490172926018775);
this.slot[0][1][5] = new Pair(1.2747531725857995,0.0);
this.slot[0][1][6] = new Pair(0.8308916512933247,26.160213104822812);
this.slot[0][1][7] = new Pair(0.06771897447606934,0.0);
this.slot[0][1][8] = new Pair(0.7693120360944459,1.4237017600635475);
this.slot[1][0][0] = new Pair(2.27245489344635,18.69218694838215);
this.slot[1][0][1] = new Pair(2.2883229954715203,15.49407187041761);
this.slot[1][0][2] = new Pair(0.30561094919034415,17.417137464907324);
this.slot[1][0][3] = new Pair(0.35894658960016645,-10.19367194969469);
this.slot[1][0][4] = new Pair(0.0,0.7732663888340546);
this.slot[1][0][5] = new Pair(1.0373431568121336,16.731528258480314);
this.slot[1][0][6] = new Pair(1.7868052489942263,16.694778168415464);
this.slot[1][0][7] = new Pair(1.3807848635674582,0.0);
this.slot[1][0][8] = new Pair(1.9639169067248141,28.210943080422812);
this.slot[1][1][0] = new Pair(2.8605509909209204,0.0);
this.slot[1][1][1] = new Pair(0.7111942644325626,5.3933202901833415);
this.slot[1][1][2] = new Pair(1.5263360939139807,12.429354276582323);
this.slot[1][1][3] = new Pair(2.8202754165095953,7.629995263961135);
this.slot[1][1][4] = new Pair(2.7836207308046967,-1.6324878370138727);
this.slot[1][1][5] = new Pair(2.055055011391649,4.6103266087781325);
this.slot[1][1][6] = new Pair(0.9347168127145444,11.253540572857101);
this.slot[1][1][7] = new Pair(0.015795724056035798,14.986264272784055);
this.slot[1][1][8] = new Pair(2.764536929683822,21.98101965889552);
}}
