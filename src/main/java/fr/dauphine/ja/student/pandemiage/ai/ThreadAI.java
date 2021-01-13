package fr.dauphine.ja.student.pandemiage.ai;

import java.util.List;
import java.util.Random;

import fr.dauphine.ja.pandemiage.common.AiInterface;
import fr.dauphine.ja.pandemiage.common.GameInterface;
import fr.dauphine.ja.pandemiage.common.PlayerInterface;
import fr.dauphine.ja.pandemiage.common.UnauthorizedActionException;
import fr.dauphine.ja.student.pandemiage.ai.actions.Action;
import fr.dauphine.ja.student.pandemiage.common.GameCutException;
import fr.dauphine.ja.student.pandemiage.common.GameOverException;
import fr.dauphine.ja.student.pandemiage.common.GameWonException;
import fr.dauphine.ja.student.pandemiage.game.Player;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngine;

/**
 * An Ai engine that is represented by a Thread
 * 
 * @author Ouba
 *
 */
public abstract class ThreadAI extends SimulationAi implements AiInterface, Runnable {


	protected Integer go = null;
	Thread gameEngineThread;
	public ThreadAI() {
		random = new Random(SEED);
	}

	@Override
	public void run() {

		try {
			while (true) {
				synchronized (GameEngine.waitLock) {
					while (go == null) {
						try {
	
								GameEngine.waitLock.wait();
							}
				
						catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					playTurnInternal(ge, p);			
					// we're done 4 actions and discard,
					// and game is not over (win or defeat), tell game engine thread, wake it up.
					gameEngineThread.interrupt();
					go = null;
				}
			}
		}
		catch(GameOverException | GameWonException gw) {
			// game is over, exit this thread and tell gameEngine thread.
			gameEngineThread.interrupt();
		}
	}


	@Override
	public void playTurn(GameInterface g, PlayerInterface p) {
		synchronized (GameEngine.waitLock) {
			go = 1;
			// this.g = g;
			this.ge = (GameEngine) g;
			this.p = p;
			// wake up ai and so that it can execute its job (look for best action)
			GameEngine.waitLock.notifyAll();
		}
	}

	/**
	 * Each player turn is divided into 3 parts: 1. Do 4 actions. 2. Draw 2 Player
	 * cards. 3. Infect cities.
	 * 
	 * @author Ouba
	 * @throws GameOverException
	 * @throws GameWonException 
	 */
	public void playTurnInternal(GameInterface g, PlayerInterface p) throws GameOverException, GameWonException {
		int i;
		List<Action>[] actionss;
		Action action;
		// play 4 actions
		// You may do up to 4 actions each turn.
		// Select any combination of the actions listed below. You may do the same
		// action
		// several times, each time counting as 1 action. Your role’s special abilities
		// may change
		// how an action is done. Some actions involve discarding a card from your hand;
		// all these
		// discards go to the Player Discard Pile.
		ge.getRenderer().stateDo4Actions();
		for (i = 0; i < 4; i++) {
			this.i = i;
			ge.getRenderer().stateActionNb(i, p.playerLocation());
			actionss = getActions();
			Integer[] coord;
			// the ai thread has been interrupted, time is up, it got to hurry up now!
			// That's why from now on, it got to use the very fast random strategy to
			// choose its actions.
			if (Thread.currentThread().isInterrupted()) {
				coord = RandomAi.chooseAction(actionss, random);
			}
			// time is left, go on with the time consuming ai strategy to choose actions
			else {
				coord = chooseBestAction(actionss);
			}
			ge.getRenderer().stateChosenAction(actionss, coord);
			action = actionss[coord[0]].get(coord[1]);
			try {
				action.execute();
			}
			catch (UnauthorizedActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}
		Player player = (Player) p;
		// 2. Draw 2 Player cards.
		player.draw2PlayerCards();
	}

	
	

	public Integer[] chooseBestAction(List<Action>[] actionss) {
		Integer[] chosen = null;
		try {
			chosen = chooseBestAction(actionss, i, null, null, null);
		}
		catch (GameCutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return chosen;
	}
	@Override
	public List<Integer> chooseBestDiscardAction(List<List<Integer>> discardActions) {
		List<Integer> bestDiscardAction = null;
		try {
			bestDiscardAction = chooseBestDiscardAction(discardActions, j);
		}
		catch (GameCutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bestDiscardAction;
	}

	public Thread getGameEngineThread() {
		return gameEngineThread;
	}

	public void setGameEngineThread(Thread mainThread) {
		this.gameEngineThread = mainThread;
	}

}
