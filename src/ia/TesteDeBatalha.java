package ia;

import java.io.File;
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
		natureza = new Natureza(1000,10);
		battleListener = new MyBattleListener();
		engine.addBattleListener(battleListener);
	}
	
	public void simularPopulacao(){
		ArrayList<Cromossomo> populacao = natureza.criarPopulacaoInicial();
		for(int i = 0; i < 100; i++){
			HashMap<Cromossomo,Double> scores = new HashMap<Cromossomo,Double>();
			for(Cromossomo cromossomo: populacao){
				double score = batalhar(cromossomo,5);
				scores.put(cromossomo, new Double(score));
			}
			populacao = natureza.proximaGeracao(scores);
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
			System.out.println(lastScore);
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
