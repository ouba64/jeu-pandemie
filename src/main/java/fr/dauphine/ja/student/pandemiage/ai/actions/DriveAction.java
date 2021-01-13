package fr.dauphine.ja.student.pandemiage.ai.actions;

import fr.dauphine.ja.pandemiage.common.UnauthorizedActionException;
import fr.dauphine.ja.student.pandemiage.game.Player;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngine;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngineForSimulation;

public class DriveAction extends MoveAction {

	public DriveAction(GameEngineForSimulation ge, Player p, String destination) {
		super(ge, p, destination);
	}

	@Override
	public void execute() throws UnauthorizedActionException {
		p.moveTo(destination);
	}

	@Override
	public String toString() {
		return super.toString() + " by driving";
	}
	
	@Override
	public Action getNewAction() {
		return new DriveAction(null, null, destination);
	}
}
