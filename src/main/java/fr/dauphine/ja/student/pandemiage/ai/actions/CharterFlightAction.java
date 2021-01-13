package fr.dauphine.ja.student.pandemiage.ai.actions;

import fr.dauphine.ja.pandemiage.common.UnauthorizedActionException;
import fr.dauphine.ja.student.pandemiage.game.Player;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngine;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngineForSimulation;

public class CharterFlightAction extends MoveAction {


	public CharterFlightAction(GameEngineForSimulation ge, Player p, String destination) {
		super(ge, p, destination);
	}

	@Override
	public void execute() throws UnauthorizedActionException {
		p.flyToCharter(destination);
	}

	@Override
	public String toString() {
		return super.toString()+ " by charter";
	}

	@Override
	public Action getNewAction() {
		return new CharterFlightAction(null, null, destination);
	}
}
