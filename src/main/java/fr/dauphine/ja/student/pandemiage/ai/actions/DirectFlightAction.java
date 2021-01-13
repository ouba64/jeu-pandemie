package fr.dauphine.ja.student.pandemiage.ai.actions;

import fr.dauphine.ja.pandemiage.common.UnauthorizedActionException;
import fr.dauphine.ja.student.pandemiage.game.Player;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngineForSimulation;

public class DirectFlightAction extends MoveAction {

	public DirectFlightAction(GameEngineForSimulation ge, Player p, String destination) {
		super(ge, p, destination);
	}

	@Override
	public void execute() throws UnauthorizedActionException {
		p.flyTo(destination);
	}

	@Override
	public String toString() {

		return super.toString() + " by direct flight";
	}
	
	@Override
	public Action getNewAction() {
		return new DirectFlightAction(null, null, destination);
	}
}
