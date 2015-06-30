package ia;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import ia.Cromossomo.Pair;

public class Natureza {

	static String PATH = "./robots/ia/";
	static String JAVA = ".java";
	
	private int tamanho;
	private int fracaoReducao;
	private Random random;
	
	public Natureza(int quantidadeDeSeres, int fracaoSelecaoNatural){
		tamanho = quantidadeDeSeres;
		this.fracaoReducao = fracaoSelecaoNatural;
		random = new Random();
	}
	
	public ArrayList<Cromossomo> criarPopulacaoInicial(){
		ArrayList<Cromossomo> populacao = new ArrayList<Cromossomo>();
		for(int y = 0; y <= Cromossomo.MAX_BULLET_POWER; y++){
			for( int f = -5; f <= 5; f++){
				Cromossomo x = new Cromossomo();
				x.randomPopulate();
				for(int i = 0; i < Cromossomo.VELOCITY; i++){
					for(int h = 0; h < Cromossomo.DIRECTIONS; h++){
						for(int z = 0; z < Cromossomo.DISTANCE; z++){
							x.genes[i][h][z].first = y;
							x.genes[i][h][z].second = f*5;
						}
					}
				}
				populacao.add(x);
			}
		}
		for(int i = 0; tamanho > populacao.size(); i++){
			Cromossomo x = new Cromossomo();
			x.randomPopulate();
			populacao.add(x);
		}
		return populacao;
	}
	
	private Cromossomo selecaoNatural(HashMap<Cromossomo,Double> cromossomoScores){
		List<Double> scores = new ArrayList<Double>(cromossomoScores.values());
		Collections.sort(scores);
		double melhor = scores.get(scores.size()-1);
		Cromossomo sobrevivente = null;
		for (Map.Entry<Cromossomo,Double> entry : cromossomoScores.entrySet()) {
		    Cromossomo key = entry.getKey();
		    if(sobrevivente == null) sobrevivente = key;
		    double value = entry.getValue();
		    if( value > melhor - 0.0001) sobrevivente = key;
		}
		return sobrevivente;
	}
	
	public ArrayList<Cromossomo> proximaGeracao(HashMap<Cromossomo,Double> cromossomoScores){
		ArrayList<Cromossomo> pais = new ArrayList<Cromossomo>(cromossomoScores.keySet());
		ArrayList<Cromossomo> novaGeracao = new ArrayList<Cromossomo>();
		for( int i = 0; i < tamanho; i++){
			HashMap<Cromossomo,Double> naBrigaPraVida = new HashMap<Cromossomo,Double>();
			for( int h = 0 ; h < fracaoReducao; h++){
				int index = random.nextInt(pais.size());
				Cromossomo paiGuerreiro = pais.get(index);
				Double scorePaiGuerreiro = cromossomoScores.get(paiGuerreiro);
				naBrigaPraVida.put(paiGuerreiro, scorePaiGuerreiro);
			}
			Cromossomo sobrevivente = selecaoNatural(naBrigaPraVida);
			novaGeracao.add(new Cromossomo(sobrevivente));
		}
		for(int i = 0, h = novaGeracao.size()-1; i<h; i++, h--){
			novaGeracao.get(i).crossOver(novaGeracao.get(h));
			novaGeracao.get(i).mutacao();
			novaGeracao.get(h).mutacao();
		}
		return novaGeracao;
	}

	
	public String gerarMutante(Cromossomo x) {
		String nomeDoNovoRobo = "Wolverine";
		File f = new File(PATH + nomeDoNovoRobo + JAVA);
		try {
			f.delete();
			f.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			FileWriter fw = new FileWriter(f);
			PrintWriter escritor = new PrintWriter(fw);
			String codigoDoRobo = criarCodigoDoRobo(x);
			escritor.println(codigoDoRobo);
			escritor.flush();
			escritor.close();
			compileIt(nomeDoNovoRobo + JAVA);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "ia."+nomeDoNovoRobo+"*";
	}

	private static boolean compileIt(String arquivo) {

		String file = PATH + arquivo;
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		int compilationResult = compiler.run(null, null, null, file);
		if (compilationResult == 0) {
			return true;
		} else {
			return false;
		}

	}

	private String criarCodigoDoRobo(Cromossomo x) {
		String novoRoboCodigo = "package ia;\n" + 
				"\n" + 
				"\n" + 
				"import static robocode.util.Utils.normalRelativeAngleDegrees;\n" + 
				"\n" + 
				"import java.awt.Color;\n" + 
				"import java.awt.geom.Point2D;\n" + 
				"import java.io.BufferedReader;\n" + 
				"import java.io.File;\n" + 
				"import java.io.FileInputStream;\n" + 
				"import java.io.FileReader;\n" + 
				"import java.io.InputStream;\n" + 
				"import java.io.InputStreamReader;\n" + 
				"import java.util.Random;\n" + 
				"import java.util.Scanner;\n" + 
				"\n" + 
				"import ia.Cromossomo.Pair;\n" + 
				"import robocode.AdvancedRobot;\n" + 
				"import robocode.HitByBulletEvent;\n" + 
				"import robocode.HitRobotEvent;\n" + 
				"import robocode.HitWallEvent;\n" + 
				"import robocode.ScannedRobotEvent;\n" + 
				"\n" + 
				"public class Wolverine extends AdvancedRobot {\n" + 
				"	Random random;\n" + 
				"	int moveParaFrente;\n" + 
				"	int dist = 50; // distance to move when we're hit\n" + 
				"	final float MAX_BULLET_POWER = 3;\n" + 
				"	final int Q_VELOCITY = 2;\n" + 
				"	final float MAX_DEGREE = 30;\n" + 
				"	final int Q_DISTANCE = 9;\n" + 
				"	final int DIRECTIONS = 2;\n" + 
				"	double enemyX;\n" + 
				"	double enemyY;\n" + 
				"	double lastEnemyX;\n" + 
				"	int scanDirection;\n" + 
				"	double lastEnemyY;\n" + 
				"	double enemyAbsoluteHeading;\n" + 
				"	Pair[][][] slot;\n" + 
				"	\n" + 
				"	public Wolverine() {\n" + 
				"		this.slot = new Pair[Q_VELOCITY][DIRECTIONS][Q_DISTANCE];\n" + 
				"	    fillSlot();\n" + 
				"		this.random = new Random();\n" + 
				"		this.moveParaFrente = 1;\n" + 
				"		this.scanDirection = 1;\n" + 
				"	}\n" + 
				"\n" + 
				"	/**\n" + 
				"	 * run:  Fire's main run function\n" + 
				"	 */\n" + 
				"	public void run() {\n" + 
				"		// Set colors\n" + 
				"		setBodyColor(Color.pink);\n" + 
				"		setGunColor(Color.pink);\n" + 
				"		setRadarColor(Color.pink);\n" + 
				"		setScanColor(Color.pink);\n" + 
				"		setBulletColor(Color.pink);\n" + 
				"		setAdjustGunForRobotTurn(true);\n" + 
				"		setAdjustRadarForGunTurn(true);\n" + 
				"		setAdjustRadarForRobotTurn(true);\n" + 
				"		// Spin the gun around slowly... forever\n" + 
				"		while (true) {\n" + 
				"			setTurnRadarRight(360 * scanDirection);\n" + 
				"			moveAleatorio();\n" + 
				"			execute();\n" + 
				"		}\n" + 
				"	}\n" + 
				"	\n" + 
				"	public void onHitWall(HitWallEvent e) {\n" + 
				"		this.moveParaFrente = (this.moveParaFrente + 1)%2; \n" + 
				"	}\n" + 
				"\n" + 
				"	void moveAleatorio(){\n" + 
				"		if(random.nextInt(5) == 1){\n" + 
				"			int distancia  = random.nextInt(100);\n" + 
				"			if(moveParaFrente == 1)\n" + 
				"				setAhead(distancia);\n" + 
				"			else \n" + 
				"				setBack(distancia);\n" + 
				"			int graus = random.nextInt(60) - 30;\n" + 
				"			setTurnRight(graus);\n" + 
				"		}\n" + 
				"	}\n" + 
				"	\n" + 
				"	private boolean andandoDireita(ScannedRobotEvent e) {\n" + 
				"		double Ax = (enemyX-lastEnemyX);\n" + 
				"		double Ay = (enemyY-lastEnemyY);\n" + 
				"		double Bx = (enemyX-getX());\n" + 
				"		double By = (enemyX-getY());\n" + 
				"		return Ax*By - Ay*Bx >= 0;\n" + 
				"	}\n" + 
				"	\n" + 
				"	private int distanceCategory(double distance){\n" + 
				"		int category = (int) distance/8;\n" + 
				"		category = Math.min(category,8);\n" + 
				"		return category;\n" + 
				"	}\n" + 
				"	\n" + 
				"	private int velocityCategory(double velocity){\n" + 
				"		return velocity > 0.1 ? 1 : 0;\n" + 
				"	}\n" + 
				"	/**\n" + 
				"	 * onScannedRobot:  Fire!\n" + 
				"	 */\n" + 
				"	public void onScannedRobot(ScannedRobotEvent e) {\n" + 
				"		update(e);		\n" + 
				"		\n" + 
				"		boolean direita = andandoDireita(e);\n" + 
				"		int distanceCat = distanceCategory(e.getDistance());\n" + 
				"		int pos = direita ? 1 : 0;\n" + 
				"		int velocity = velocityCategory(e.getVelocity());\n" + 
				"		Pair gene = slot[velocity][pos][distanceCat];\n" + 
				"		double turnGunAmt = normalRelativeAngleDegrees(gene.second + e.getBearing() + getHeading() - getGunHeading());\n" + 
				"		setTurnGunRight(turnGunAmt);\n" + 
				"		if(getGunHeat() == 0 && turnGunAmt < 5 && gene.first >= 0.1){\n" + 
				"			fire(gene.first);\n" + 
				"		}\n" + 
				"		\n" + 
				"		scanDirection *= -1;\n" + 
				"		setTurnRadarRight(360 * scanDirection);\n" + 
				"	}\n" + 
				"\n" + 
				"	/**\n" + 
				"	 * onHitByBullet:  Turn perpendicular to the bullet, and move a bit.\n" + 
				"	 */\n" + 
				"	public void onHitByBullet(HitByBulletEvent e) {\n" + 
				"		\n" + 
				"	}\n" + 
				"	\n" + 
				"	private void update(ScannedRobotEvent e) {\n" + 
				"	    double absBearingDeg = (getHeading() + e.getBearing());\n" + 
				"	    if (absBearingDeg < 0) {\n" + 
				"	      absBearingDeg += 360;\n" + 
				"	    }\n" + 
				"	    lastEnemyX = enemyX;\n" + 
				"	    lastEnemyY = enemyY;\n" + 
				"	    enemyX = getX() + Math.sin(Math.toRadians(absBearingDeg)) * e.getDistance();\n" + 
				"	    enemyY = getY() + Math.cos(Math.toRadians(absBearingDeg)) * e.getDistance();\n" + 
				"	    if(e.getVelocity() < 0.1){\n" + 
				"	    	enemyAbsoluteHeading = absoluteBearing(lastEnemyX,lastEnemyY,enemyX,enemyY);\n" + 
				"	    } else {\n" + 
				"	    	enemyAbsoluteHeading = e.getHeading();\n" + 
				"	    }\n" + 
				"	}\n" + 
				"	\n" + 
				"	double absoluteBearing(double x1, double y1, double x2, double y2) {\n" + 
				"		double xo = x2-x1;\n" + 
				"		double yo = y2-y1;\n" + 
				"		double hyp = Point2D.distance(x1, y1, x2, y2);\n" + 
				"		double arcSin = Math.toDegrees(Math.asin(xo / hyp));\n" + 
				"		double bearing = 0;\n" + 
				"\n" + 
				"		if (xo > 0 && yo > 0) { // both pos: lower-Left\n" + 
				"			bearing = arcSin;\n" + 
				"		} else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right\n" + 
				"			bearing = 360 + arcSin; // arcsin is negative here, actuall 360 - ang\n" + 
				"		} else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left\n" + 
				"			bearing = 180 - arcSin;\n" + 
				"		} else if (xo < 0 && yo < 0) { // both neg: upper-right\n" + 
				"			bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang\n" + 
				"		}\n" + 
				"\n" + 
				"		return bearing;\n" + 
				"	}\n" + 
				"	\n" + 
				"	public class Pair{\n" + 
				"		double first;\n" + 
				"		double second;\n" + 
				"		public Pair(double a , double b){\n" + 
				"			this.first = a;\n" + 
				"			this.second = b;\n" + 
				"		}\n" + 
				"		\n" + 
				"		public String toString(){\n" + 
				"			String saida =  String.valueOf(first) + \" \" + String.valueOf(second);\n" + 
				"			return saida;\n" + 
				"		}\n" + 
				"	}\n" + 
				"	\n" + 
				"	private void fillSlot(){";
		novoRoboCodigo += x.toString();
		novoRoboCodigo += "}}";
		return novoRoboCodigo;
	}

}