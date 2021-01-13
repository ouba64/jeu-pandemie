package fr.dauphine.ja.student.pandemiage.ai.actions;

import fr.dauphine.ja.pandemiage.common.UnauthorizedActionException;
import fr.dauphine.ja.student.pandemiage.game.Player;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngineForSimulation;

public class MoveAction extends Action {
	String destination;

	/*protected void movePlayerFromTo() {
		String from = p.playerLocation();
		// current city
		CityContainer cityContainer = ge.getBoard().getCityContainers().get(from);
		cityContainer.getPlayers().remove(p);
		// new city
		cityContainer = ge.getBoard().getCityContainers().get(destination);
		cityContainer.getPlayers().add(p);
		CityCard myLocation = cityContainer.getCityCard();
		p.setMyLocation(myLocation);
		// render the move on appropriate media
		ge.getRenderer().movePlayerFromTo(from, destination);
	}*/

	public MoveAction(GameEngineForSimulation ge, Player p, String destination) {
		super(ge, p);
		this.destination = destination;
	}

	@Override
	public void execute() throws UnauthorizedActionException {

	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}
	
	@Override
	public String toString() {
		return "Go to " + destination;
	}

	@Override
	public Action getNewAction() {
		return new MoveAction(null, null, destination);
	}

}
