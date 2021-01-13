package fr.dauphine.ja.student.pandemiage.ai.actions;

import fr.dauphine.ja.pandemiage.common.UnauthorizedActionException;
import fr.dauphine.ja.student.pandemiage.common.GameWonException;
import fr.dauphine.ja.student.pandemiage.game.Player;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngineForSimulation;

public abstract class Action {
	GameEngineForSimulation ge;
	Player p;

	public Action(GameEngineForSimulation ge, Player p) {
		super();
		this.ge = ge;
		this.p = p;
	}

	public abstract void execute() throws UnauthorizedActionException, GameWonException;

	

	public GameEngineForSimulation getGe() {
		return ge;
	}

	public void setGe(GameEngineForSimulation ge) {
		this.ge = ge;
	}

	public Player getP() {
		return p;
	}

	public void setP(Player p) {
		this.p = p;
	}

	public Action duplicate(GameEngineForSimulation geNew) {
		Action action = getNewAction();
		action.setP((Player) geNew.getPlayers().get(geNew.getCurrentPlayer()));
		action.setGe(geNew);
		return action;
	}

	public abstract Action getNewAction() ;
}
