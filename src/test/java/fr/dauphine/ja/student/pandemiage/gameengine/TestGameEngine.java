package fr.dauphine.ja.student.pandemiage.gameengine;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class TestGameEngine {

	@Test
	public void testNeighbours() {
		String cityGraphFilename = "pandemic.graphml";
		boolean isGui = false;
		boolean isConsole = false;
		GameEngine ge = new GameEngine(null, cityGraphFilename, 0, 0, 0, 0, 0, isGui, isConsole, null);
		ge.createPlayersAndOtherStuff();
		List<String> londonNeighbours = ge.neighbours("London");
		assertTrue(londonNeighbours.size() == 4 && londonNeighbours.contains("Paris")
				&& londonNeighbours.contains("New York") && londonNeighbours.contains("Madrid")
				&& londonNeighbours.contains("Essen") && !londonNeighbours.contains("Milan"));
	}
}
