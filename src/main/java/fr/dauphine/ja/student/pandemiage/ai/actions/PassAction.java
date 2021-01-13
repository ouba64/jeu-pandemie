package fr.dauphine.ja.student.pandemiage.ai.actions;

import fr.dauphine.ja.pandemiage.common.UnauthorizedActionException;
import fr.dauphine.ja.student.pandemiage.game.Player;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngine;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngineForSimulation;

public class PassAction extends Action {

	public PassAction(GameEngineForSimulation ge, Player p) {
		super(ge, p);
	}

	@Override
	public void execute() throws UnauthorizedActionException {
	}

	@Override
	public String toString() {
		return "Pass";
	}

	@Override
	public Action getNewAction() {
		return this;
	}
	
	
}
