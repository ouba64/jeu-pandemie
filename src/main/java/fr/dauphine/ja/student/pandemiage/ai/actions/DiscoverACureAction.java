package fr.dauphine.ja.student.pandemiage.ai.actions;

import java.awt.List;
import java.util.ArrayList;

import fr.dauphine.ja.pandemiage.common.Disease;
import fr.dauphine.ja.pandemiage.common.GameStatus;
import fr.dauphine.ja.pandemiage.common.PlayerCardInterface;
import fr.dauphine.ja.pandemiage.common.UnauthorizedActionException;
import fr.dauphine.ja.student.pandemiage.common.DiseaseState;
import fr.dauphine.ja.student.pandemiage.common.GameWonException;
import fr.dauphine.ja.student.pandemiage.game.Player;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngineForSimulation;

public class DiscoverACureAction extends Action {
	/**
	 * The positions of the 5 cards in hand
	 */
	ArrayList<Integer> indices;
	Disease curedDisease;


	public DiscoverACureAction(GameEngineForSimulation ge, Player p, ArrayList<Integer> indices, Disease curedDisease,
			Thread mainThread) {
		super(ge, p);
		this.indices = indices;
		this.curedDisease = curedDisease;
	}

	public ArrayList<Integer> getIndices() {
		return indices;
	}

	public void setIndices(ArrayList<Integer> indices) {
		this.indices = indices;
	}

	@Override
	public void execute() throws UnauthorizedActionException, GameWonException {
		java.util.List<PlayerCardInterface> cardsOfSameDisease = new ArrayList<>();
		for(Integer i : indices) {
			cardsOfSameDisease.add(p.getHand().get(i));
		}
		((Player)p).setIndices(indices);
		p.discoverCure(cardsOfSameDisease);
		// do we win the game?
		boolean gameIsWon = true;
		for (DiseaseState diseaseState : ge.getBoard().getDiscoveredCureIndicators()) {
			if (diseaseState == DiseaseState.ACTIVE) {
				gameIsWon = false;
				break;
			}
		}
		// we should get out the experiment as soon as one of the 4 actions yields a
		// victory
		if (gameIsWon) {
			ge.setGameStatus(GameStatus.VICTORIOUS);
			throw new GameWonException("*******      Victory !     ***********");
		}
	}

	@Override
	public String toString() {
		return "Discover the cure for " + curedDisease.name().toLowerCase();
	}

	@Override
	public Action getNewAction() {
		return new DiscoverACureAction(null, null, indices, curedDisease, null);
	}

}
