package ia;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import robocode.BattleResults;
import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.BattleErrorEvent;
import robocode.control.events.BattleFinishedEvent;
import robocode.control.events.BattleMessageEvent;
import robocode.control.events.BattlePausedEvent;
import robocode.control.events.BattleResumedEvent;
import robocode.control.events.BattleStartedEvent;
import robocode.control.events.IBattleListener;
import robocode.control.events.RoundEndedEvent;
import robocode.control.events.RoundStartedEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.events.TurnStartedEvent;

public class TesteDeBatalha {
	RobocodeEngine engine;
	BattleSpecification battleSpec;
	Natureza natureza;
	private MyBattleListener battleListener;
	
	public TesteDeBatalha(){
		this.engine = new RobocodeEngine(new File("./")); // Run from
		engine.setVisible(false);
		natureza = new Natureza(200,10);
		battleListener = new MyBattleListener();
		engine.addBattleListener(battleListener);
	}
	
	public void simularPopulacao(){
		File f = new File("TesteDeBatalha.log");
		File f2 = new File("MelhorRobo.log");
		try {
			f.delete();
			f.createNewFile();
			f2.delete();
			f2.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
		FileWriter fw = new FileWriter(f);
		PrintWriter escritor = new PrintWriter(fw);
		FileWriter fw2 = new FileWriter(f);
		PrintWriter escritor2 = new PrintWriter(fw2);
			
		ArrayList<Cromossomo> populacao = natureza.criarPopulacaoInicial();
		Cromossomo melhor = null;
		double melhorScore = -1;
		for(int i = 0; i < 20; i++){
			HashMap<Cromossomo,Double> scores = new HashMap<Cromossomo,Double>();
			int cont = 0;
			for(Cromossomo cromossomo: populacao){
				if(melhor == null) melhor = cromossomo;
				double score = batalhar(cromossomo,5);
				if(score > melhorScore) melhor = cromossomo;
				String log = "" + i + " "+ (cont++) + " " + score;
				escritor.println(log);
				escritor.flush();
				System.out.println(log);
				scores.put(cromossomo, new Double(score));
			}
			populacao = natureza.proximaGeracao(scores);
		}
		escritor2.println(melhor.toString());
		escritor2.flush();
		escritor.close();
		escritor2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public double batalhar(Cromossomo x, int numRounds) {
		String nomeRobo = natureza.gerarMutante(x);
		int numberOfRounds = numRounds;
		BattlefieldSpecification battlefield = new BattlefieldSpecification(
				800, 600); 
		RobotSpecification[] roboInimigo = engine
				.getLocalRepository(nomeRobo+", sample.Walls");
		battleSpec = new BattleSpecification(numberOfRounds, battlefield,
				roboInimigo);
		engine.runBattle(battleSpec, true /* wait till the battle is over */);
		return battleListener.lastScore;
	}
	
	private class MyBattleListener implements IBattleListener {

		double lastScore;
		
		@Override
		public void onBattleCompleted(BattleCompletedEvent event) {
			BattleResults[] results = event.getIndexedResults();
			lastScore = results[0].getScore();
		}

		@Override
		public void onBattleError(BattleErrorEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onBattleFinished(BattleFinishedEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onBattleMessage(BattleMessageEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onBattlePaused(BattlePausedEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onBattleResumed(BattleResumedEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onBattleStarted(BattleStartedEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onRoundEnded(RoundEndedEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onRoundStarted(RoundStartedEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTurnEnded(TurnEndedEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTurnStarted(TurnStartedEvent event) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static void main(String[] args){
		TesteDeBatalha testeDeBatalha = new TesteDeBatalha();
		testeDeBatalha.simularPopulacao();
	}	
}
