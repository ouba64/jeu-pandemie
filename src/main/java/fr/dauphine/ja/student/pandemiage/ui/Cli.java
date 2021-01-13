package fr.dauphine.ja.student.pandemiage.ui;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import fr.dauphine.ja.student.pandemiage.gameengine.GameEngine;

public class Cli {
	public static final String DEFAULT_AIJAR = "./target/dauphine-pandemic-inys-ai.jar";
	public static final String DEFAULT_CITYGRAPH_FILE = "pandemic.graphml";
	public static final int DEFAULT_TURN_DURATION = 1; // in seconds
	public static final int DEFAULT_DIFFICULTY = 0; // Normal
	public static final int DEFAULT_HAND_SIZE = 9;

	/**
	 * Nombre de cartes joueurs initial : pour un joueur 5 cartes.
	 */
	public static final int NOMBRE_DE_CARTES_JOUEURS_INITIAL = 5;
	
	/**
	 * Nombre de joueurs pour notre implémentation
	 */
	public static final int NOMBRE_DE_JOUEURS = 1;
	
	
	public static void main(String[] args) {
		String aijar = DEFAULT_AIJAR;
		String cityGraphFile = DEFAULT_CITYGRAPH_FILE;
		int difficulty = DEFAULT_DIFFICULTY;
		int turnDuration = DEFAULT_TURN_DURATION;
		int handSize = DEFAULT_HAND_SIZE;
		int seed = 1;
		int nPlayers = 1;
		
		boolean isGui = false;
		boolean isConsole = true;

		Options options = new Options();
		CommandLineParser parser = new DefaultParser();

		options.addOption("a", "aijar", true, "use <FILE> as player Ai.");
		options.addOption("d", "difficulty", true, "Difficulty level. 0 (Introduction), 1 (Normal) or 3 (Heroic).");
		options.addOption("c", "citygraph", true, "City graph filename.");
		options.addOption("t", "turnduration", true, "Number of seconds allowed to play a turn.");
		options.addOption("s", "handsize", true, "Maximum size of a player hand.");
		options.addOption("h", "help", false, "Display this help");

		options.addOption("gui", "gui", true, "If this option is given, then the gui will be shown");
		options.addOption("con", "console", true, "If this option is given, then the game will be shown on console");
		try {
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption("a")) {
				aijar = cmd.getOptionValue("a");
			}

			if (cmd.hasOption("c")) {
				cityGraphFile = cmd.getOptionValue("c");
			}

			if (cmd.hasOption("d")) {
				difficulty = Integer.parseInt(cmd.getOptionValue("d"));
			}

			if (cmd.hasOption("t")) {
				turnDuration = Integer.parseInt(cmd.getOptionValue("t"));
			}
			if (cmd.hasOption("s")) {
				handSize = Integer.parseInt(cmd.getOptionValue("s"));
			}
			if (cmd.hasOption("h")) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("pandemiage", options);
				System.exit(0);
			}

			/* ... */

			if (cmd.hasOption("gui")) {
				isGui = Boolean.parseBoolean(cmd.getOptionValue("gui"));
			}
			
			if (cmd.hasOption("con")) {
				isConsole = Boolean.parseBoolean(cmd.getOptionValue("con"));
			}



		}
		catch (ParseException e) {
			System.err.println("Error: invalid command line format.");
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("pandemiage", options);
			System.exit(1);
		}
		turnDuration = 1;
		turnDuration = 1000000;
		GameEngine g = new GameEngine(aijar, cityGraphFile, turnDuration, seed, difficulty, nPlayers, handSize, isGui, isConsole, null);
		/* ... */
		g.loop();
	}
}
