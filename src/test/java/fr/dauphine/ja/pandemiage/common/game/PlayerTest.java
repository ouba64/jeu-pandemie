package fr.dauphine.ja.pandemiage.common.game;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import fr.dauphine.ja.pandemiage.common.Disease;
import fr.dauphine.ja.student.pandemiage.common.GameOverException;
import fr.dauphine.ja.student.pandemiage.game.Board;
import fr.dauphine.ja.student.pandemiage.game.CityContainer;
import fr.dauphine.ja.student.pandemiage.game.Player;
import fr.dauphine.ja.student.pandemiage.game.cards.InfectionCard;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngine;

public class PlayerTest {

	GameEngine ge;

	@Before
	public void initialize() {
		String cityGraphFilename = "pandemic.graphml";
		boolean isGui = false;
		boolean isConsole = false;
		ge = new GameEngine(null, cityGraphFilename, 0, 0, 0, 1, 9, isGui, isConsole, null);
		ge.createPlayersAndOtherStuff();
	}


	@Test
	public void testInfectCities() {
		// at start Paris has 2 blue diseases and London 3.
		Disease disease = Disease.BLUE;
		Board board = ge.getBoard();
		CityContainer cityContainer ;
		try {
			cityContainer = ge.getBoard().getCityContainers().get("Paris");
			board.moveDiseaseCubeFromSupplyToCity(cityContainer, disease, 2);
			cityContainer = ge.getBoard().getCityContainers().get("London");
			board.moveDiseaseCubeFromSupplyToCity(cityContainer, disease, 3);
		}
		catch (GameOverException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// when the player drew 2 cards and he got an epidemic card of Paris, we should infect Paris (put
		// 3 desease cubes). If everything goes all right, we should have :
		//
		// Paris : 3
		// Essen : 2 (Paris gives 1, London gives 1)
		// Milan : 1
		// Algiers : 1
		// Madrid : 2 (Paris gives 1, London gives 1)
		// London : 3
		// New York : 1
		InfectionCard ic = null;
		for (InfectionCard infectionCard : ge.getBoard().getInfectionDeck()) {
			if (infectionCard.getCityName().equals("Paris")) {
				ic = infectionCard;
				break;
			}
		}
		try {
			((Player) ge.getPlayers().get(0)).infectCity(ic, 3);
		}
		catch (GameOverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(board.getCityContainers().get("Paris").getDiseaseCubes()[disease.ordinal()].size() == 3
				&& board.getCityContainers().get("Essen").getDiseaseCubes()[disease.ordinal()].size() == 2
				&& board.getCityContainers().get("Milan").getDiseaseCubes()[disease.ordinal()].size() == 1
				&& board.getCityContainers().get("Algiers").getDiseaseCubes()[disease.ordinal()].size() == 1
				&& board.getCityContainers().get("Madrid").getDiseaseCubes()[disease.ordinal()].size() == 2
				&& board.getCityContainers().get("London").getDiseaseCubes()[disease.ordinal()].size() == 3
				&& board.getCityContainers().get("New York").getDiseaseCubes()[disease.ordinal()].size() == 1);
	}

}
