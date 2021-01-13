package fr.dauphine.ja.student.pandemiage.gameengine;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Set;
import java.util.Stack;
import fr.dauphine.ja.pandemiage.common.AiInterface;
import fr.dauphine.ja.pandemiage.common.AiLoader;
import fr.dauphine.ja.pandemiage.common.DefeatReason;
import fr.dauphine.ja.pandemiage.common.Disease;
import fr.dauphine.ja.pandemiage.common.GameStatus;
import fr.dauphine.ja.pandemiage.common.PlayerCardInterface;
import fr.dauphine.ja.pandemiage.common.PlayerInterface;
import fr.dauphine.ja.student.pandemiage.ai.ThreadAI;
import fr.dauphine.ja.student.pandemiage.common.DiseaseState;
import fr.dauphine.ja.student.pandemiage.common.GameOverException;
import fr.dauphine.ja.student.pandemiage.game.Board;
import fr.dauphine.ja.student.pandemiage.game.CityContainer;
import fr.dauphine.ja.student.pandemiage.game.Player;
import fr.dauphine.ja.student.pandemiage.game.cards.CityCard;
import fr.dauphine.ja.student.pandemiage.game.cards.EpidemicCard;
import fr.dauphine.ja.student.pandemiage.game.cards.InfectionCard;
import fr.dauphine.ja.student.pandemiage.game.cards.PlayerCard;
import fr.dauphine.ja.student.pandemiage.markers.DiseaseCube;
import fr.dauphine.ja.student.pandemiage.ui.Cli;
import fr.dauphine.ja.student.pandemiage.ui.Gui;
import fr.dauphine.ja.student.pandemiage.ui.renderers.ConsoleRenderer;
import fr.dauphine.ja.student.pandemiage.ui.renderers.Renderer;
import fr.dauphine.ja.student.pandemiage.ui.renderers.Renderers;
import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.pgm.util.io.graphml.GraphMLReader;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Empty GameEngine implementing GameInterface
 *
 */
public class GameEngine extends GameEngineForSimulation {

	private final String aiJar;
	private final String cityGraphFilename;

	public GameEngine(String aiJar, String cityGraphFilename, int turnDuration, long seed, int difficulty, int nPlayers,
			int handSize, boolean isGui, boolean isConsole, Gui gui) {
		super(aiJar, cityGraphFilename, turnDuration, seed, difficulty, nPlayers, handSize, isGui, isConsole, gui);
		this.aiJar = aiJar;
		this.cityGraphFilename = cityGraphFilename;
		ArrayList<Renderer> rendererList = new ArrayList<>();
		rendererList.add(new ConsoleRenderer(this));
		this.renderer = new Renderers(this, rendererList);
	}

	// Do not change!
	private void setDefeated(String msg, DefeatReason dr) {
		gameStatus = GameStatus.DEFEATED;
		System.err.println("Player(s) have been defeated: " + msg);
		System.err.println("Result: " + gameStatus);
		System.err.println("Reason: " + dr);
		printGameStats();
		System.exit(2);
	}

	// Do not change!
	private void setVictorious() {
		gameStatus = GameStatus.VICTORIOUS;
		System.err.println("Player(s) have won.");
		System.err.println("Result: " + gameStatus);
		printGameStats();
		System.exit(0);
	}

	// Do not change!
	private void printGameStats() {
		Map<Disease, Integer> blocks = new HashMap<>();
		for (String city : allCityNames()) {
			for (Disease d : Disease.values()) {
				blocks.put(d, blocks.getOrDefault(d, 0) + infectionLevel(city, d));
			}
		}
		System.err.println(blocks);
		System.err.println("Infection-rate:" + infectionRate());
		for (Disease d : Disease.values()) {
			System.err.println("Cured-" + d + ":" + isCured(d));
		}
		System.err.println("Nb-outbreaks:" + getNbOutbreaks());
		System.err.println("Nb-player-cards-left:" + getNbPlayerCardsLeft());
	}

	public void setDefeated2(String msg, DefeatReason defeatReason) {
		renderer.setDefeated(msg, defeatReason);
		setDefeated(msg, defeatReason);
	}

	public void setVictorious2() {
		renderer.setVictorious();
		setVictorious();
	}

	/**
	 * This loop works with thread ai
	 */
	public void loop() {
		// Load Ai from Jar file
		System.out.println("Loading AI Jar file " + aiJar);
		aiInterface = AiLoader.loadAi(aiJar);
		ThreadAI aiRunnable;
		Thread threadAi;
		Thread mainThread = Thread.currentThread();
		if (aiInterface instanceof ThreadAI) {
			aiRunnable = (ThreadAI) aiInterface;
			aiRunnable.setGameEngineThread(mainThread);
			// load cities
			createPlayersAndOtherStuff();
			setup();
			threadAi = new Thread(aiRunnable);
			threadAi.start();
			try {
				Thread.sleep(1);
			}
			catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			PlayerInterface p;
			boolean timeIsUp;
			boolean additionalTimeIsUp;
			int turn = 1;
			try {
				while (gameStatus == GameStatus.ONGOING) {
					p = getNextPlayer();
					renderer.stateTurnNb(turn++, p);
					timeIsUp = true;
					// we're calling playTurn here in the Thread of main, so even if the Thread of
					// the AI is sleeping,
					// we still can modify the members of the corresponding object.
					aiRunnable.playTurn(this, p);
					try {
						Thread.sleep(turnDuration * 1000);
					}
					catch (InterruptedException e) {
						// this is the place where we can find out that the game is won
						if (gameStatus == GameStatus.VICTORIOUS) {
							setVictorious2();
							break;
						}
						if(gameStatus==GameStatus.DEFEATED) {
							setDefeated2(goe.getMessage(), goe.getDefeatReason());
							break;
						}
						timeIsUp = doNextActionAfterAiIsDoneOnTime((Player) p);
					}
					// time is up, from now on, the player has 1 sec to play his move
					if (timeIsUp) {
						additionalTimeIsUp = true;
						threadAi.interrupt();
						// give the player one additional second
						try {
							threadAi.join(1000);
						}
						catch (InterruptedException e) {
							additionalTimeIsUp = doNextActionAfterAiIsDoneOnTime((Player) p);
						}
						if (additionalTimeIsUp) {
							setDefeated2("Player " + p + " was not able to play his turn on time",
									DefeatReason.TOO_MUCH_TIME);
						}
					}
				}
			}
			catch (GameOverException goe) {
				setDefeated2(goe.getMessage(), goe.getDefeatReason());
			}
		}
		else {
			setDefeated2("Game not implemented for AIs that are not Threads", DefeatReason.UNKN);
		}
	}

	private boolean doNextActionAfterAiIsDoneOnTime(Player p) throws GameOverException {
		// ai Thread has finished its task (thinking then playing the best move) on
		// time, go on with the next activity
		boolean timeIsUp = false;
		Player player = (Player) p;
		// 3. Infect cities.
		player.infectCities();
		return timeIsUp;
	}

	/**
	 * Load cities from graphml file.
	 * 
	 * @author Ouba
	 * @return
	 */
	private List<CityCard> loadCities() {
		Set<CityCard> cityCards = new HashSet<>();
		CityCard cityCard;
		CityCard nextCity;

		Graph graph = new TinkerGraph();
		GraphMLReader reader = new GraphMLReader(graph);
		Map<Vertex, CityCard> vertexToCity = new HashMap<>();
		cities = new ArrayList<>();
		nameToCityCards = new HashMap<>();
		neighbourss = new HashMap<>();
		Map<String, HashSet<String>> neighbourss = new HashMap<>();
		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream(cityGraphFilename);
			// InputStream is = new BufferedInputStream(new FileInputStream(url.getPath()));
			reader.inputGraph(is);
			Iterable<Vertex> vertices = graph.getVertices();
			Iterator<Vertex> verticesIterator = vertices.iterator();
			while (verticesIterator.hasNext()) {
				// get next Vertex
				Vertex vertex = verticesIterator.next();
				cityCard = getVertexCity(vertexToCity, vertex, neighbourss);
				Vertex nextVertex = null;
				cities.add(cityCard.getCityName());
				cityCards.add(cityCard);
				Iterable<Edge> edges = vertex.getOutEdges();
				Iterable<?>[] edgess = new Iterable[] { vertex.getOutEdges(), vertex.getInEdges() };
				// i=0 : take descendants, i=1 : take ascendants
				for (int i = 0; i < 2; i++) {
					edges = (Iterable<Edge>) edgess[i];
					Iterator<Edge> edgesIterator = edges.iterator();
					// iterate over all the edges entering Vertex
					while (edgesIterator.hasNext()) {
						Edge edge = edgesIterator.next();
						if (i == 0) {
							nextVertex = edge.getInVertex();
						}
						else {
							nextVertex = edge.getOutVertex();
						}
						nextCity = getVertexCity(vertexToCity, nextVertex, neighbourss);
						neighbourss.get(cityCard.getCityName()).add(nextCity.getCityName());
						neighbourss.get(nextCity.getCityName()).add(cityCard.getCityName());
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		ArrayList<String> list;
		// transform neighbors from set data structure to list data structure
		for (Entry<String, HashSet<String>> entry : neighbourss.entrySet()) {
			list = new ArrayList<>();
			this.neighbourss.put(entry.getKey(), list);
			for (String neighbour : entry.getValue()) {
				list.add(neighbour);
			}
		}
		Collections.sort(cities);
		return new ArrayList<>(cityCards);
	}

	/**
	 * Create all the objects of the Game: player, board, cards, etc.
	 */
	public void createPlayersAndOtherStuff() {
		//
		// create players (only 1 in this implementation)
		for (int i = 0; i < nPlayers; i++) {
			Player p = new Player(this);
			p.setName("Player " + (i + 1));
			players = new ArrayList<>();
			players.add(p);
		}
		this.board = new Board(this);
		//
		// create player deck : only city card at this stage
		List<CityCard> cityCards = loadCities();
		for (CityCard cityCard : cityCards) {
			board.getPlayerDeck().push(cityCard);
		}
		//
		// create city containers
		for (CityCard cityCard : cityCards) {
			CityContainer cityContainer = new CityContainer();
			cityContainer.setCityCard(cityCard);
			board.getCityContainers().put(cityCard.getCityName(), cityContainer);
		}

		//
		// create infection cards
		ArrayDeque<InfectionCard> infectionDeck = new ArrayDeque<>();
		board.setInfectionDeck(infectionDeck);
		InfectionCard infectionCard;
		for (CityCard cityCard : cityCards) {
			infectionCard = new InfectionCard(cityCard);
			board.getInfectionDeck().add(infectionCard);
		}
		//
		// create epidemic cards
		EpidemicCard epidemicCard;
		Stack<EpidemicCard> epidemicCards = new Stack<EpidemicCard>();
		board.setEpidemicCards(epidemicCards);
		for (int i = 0; i < NB_EPIDEMIC_CARDS; i++) {
			epidemicCard = new EpidemicCard();
			epidemicCards.add(epidemicCard);
		}
		//
		// create disease cube
		Map<Disease, Stack<DiseaseCube>> diseaseCubes = new HashMap<>();
		board.setDiseaseCubes(diseaseCubes);
		Stack<DiseaseCube> cubes;
		DiseaseCube diseaseCube;
		for (Disease disease : Disease.values()) {
			cubes = new Stack<>();
			diseaseCubes.put(disease, cubes);
			for (int i = 0; i < NB_CUBES_BY_DESEASE; i++) {
				diseaseCube = new DiseaseCube();
				diseaseCube.setDisease(disease);
				cubes.add(diseaseCube);
			}
		}
		//
		// set the disease states
		for (int i = 0; i < board.getDiscoveredCureIndicators().length; i++) {
			board.getDiscoveredCureIndicators()[i] = DiseaseState.ACTIVE;
		}
		//
		// create infection rate mark
		int[] infectionRateTrack = new int[] { 2, 2, 2, 3, 3, 4, 4 };
		board.setInfectionRateTrack(infectionRateTrack);

		renderer.createPlayersAndOtherStuff();
	}

	/**
	 * 1) Set out the board and pieces <br>
	 * 2) Place outbreaks and cure markers <br>
	 * 3) Place infection rate marker and infect 9 cities <br>
	 * 4) Give each player cards and a pawn <br>
	 * 5) Prepare the Player Deck<br>
	 */
	public void setup() {
		// 1) Set out the board and pieces
		// 2) Place outbreaks and cure markers
		// 3) Place infection rate marker and infect 9 cities
		board.setInfectionRatePosition(0);
		Deque<InfectionCard> infectionDeck = board.getInfectionDeck();
		Stack<InfectionCard> infectionDiscardPile = board.getInfectionDiscardPile();
		List<InfectionCard> temp = new ArrayList<>(infectionDeck);
		Collections.shuffle(temp, random);
		infectionDeck = new ArrayDeque<>(temp);
		board.setInfectionDeck(infectionDeck);

		InfectionCard infectionCard;
		Disease disease;
		CityContainer cityContainer;
		// - nbd = nb of diseases to put in each city
		for (int nbd = 3; nbd > 0; nbd--) {
			// flip over 3 infection cards.
			for (int j = 0; j < 3; j++) {
				infectionCard = infectionDeck.pop();
				infectionDiscardPile.push(infectionCard);
				disease = infectionCard.getDisease();
				cityContainer = board.getCityContainers().get(infectionCard.getCityName());
				try {
					board.moveDiseaseCubeFromSupplyToCity(cityContainer, disease, nbd);
				}
				catch (GameOverException e) {
					e.printStackTrace();
				}
				// put nbd disease cubes
				/*
				 * for (int i = 0; i < nbd; i++) { try { diseaseCube =
				 * board.popDiseaseCube(disease); } catch (GameOverException e) { }
				 * board.addDiseaseCube(infectionCard.getCityName(), disease, diseaseCube); }
				 */
			}
		}
		// 4) Give each player cards and a pawn
		Collections.shuffle(board.getPlayerDeck());
		PlayerInterface p;
		for (int i = 0; i < Cli.NOMBRE_DE_JOUEURS; i++) {
			p = players.get(i);
			// Place the matching
			// colored pawns in Atlanta.
			((Player) p).setMyLocation(nameToCityCards.get("Atlanta"));
			for (int j = 0; j < Cli.NOMBRE_DE_CARTES_JOUEURS_INITIAL; j++) {
				// for now, there is no epidemic card, so we're sure that the player deck
				// contains only PlayerCardInterface
				p.playerHand().add((PlayerCardInterface) board.getPlayerDeck().pop());
			}
		}
		currentPlayer = 0;
		// 5) Prepare the Player Deck
		int nEpidemicCards = 0;
		if (difficulty == 0) {
			nEpidemicCards = 4;
		}
		else if (difficulty == 1) {
			nEpidemicCards = 5;
		}
		else if (difficulty == 2) {
			nEpidemicCards = 6;
		}
		List<List<PlayerCard>> parts = new ArrayList<>(nEpidemicCards);
		for (int i = 0; i < nEpidemicCards; i++) {
			parts.add(null);
		}
		int totalSize = board.getPlayerDeck().size();
		int partSize = totalSize / nEpidemicCards;
		int from;
		int to;
		Stack<PlayerCard> playerDeckWithEpidemicCards = new Stack<>();
		// create the parts
		for (int i = 0; i < nEpidemicCards; i++) {
			from = i * partSize;
			// for last part, the last element is total size
			to = i == nEpidemicCards - 1 ? totalSize : (i + 1) * partSize;
			parts.set(i, new ArrayList<PlayerCard>(board.getPlayerDeck().subList(from, to)));
			parts.get(i).add(board.getEpidemicCards().pop());
			Collections.shuffle(parts.get(i));
			playerDeckWithEpidemicCards.addAll(parts.get(i));
		}
		board.setPlayerDeck(playerDeckWithEpidemicCards);
		initialPlayerDeckSize = playerDeckWithEpidemicCards.size();
		gameStatus = GameStatus.ONGOING;

		renderer.setup();
	}

	/**
	 * Get the next player
	 * 
	 * @return
	 */
	private PlayerInterface getNextPlayer() {
		PlayerInterface p = players.get((currentPlayer + 1) % nPlayers);
		return p;
	}

	@Override
	public List<String> allCityNames() {
		return cities;
	}

	/**
	 * @author Ouba
	 */
	@Override
	public List<String> neighbours(String cityName) {
		return neighbourss.get(cityName);
	}

	@Override
	public int infectionLevel(String cityName, Disease d) {
		int infectionLevel = board.getCityContainers().get(cityName).getDiseaseCubes()[d.ordinal()].size();
		return infectionLevel;
	}

	@Override
	public boolean isCured(Disease d) {
		DiseaseState i = board.getDiscoveredCureIndicators()[d.ordinal()];
		return i == DiseaseState.CURED;
	}

	@Override
	public int infectionRate() {
		return board.getInfectionRateTrack()[board.getInfectionRatePosition()];
	}

	@Override
	public GameStatus gameStatus() {
		return gameStatus;
	}

	@Override
	public int turnDuration() {
		return turnDuration;
	}

	@Override
	public boolean isEradicated(Disease d) {
		DiseaseState i = board.getDiscoveredCureIndicators()[d.ordinal()];
		return i == DiseaseState.ERADICATED;
	}

	@Override
	public int getNbOutbreaks() {
		return board.getNbOutbreaks();
	}

	@Override
	public int getNbPlayerCardsLeft() {
		return board.getPlayerDeck().size();
	}


	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	private CityCard getVertexCity(Map<Vertex, CityCard> vertexToCity, Vertex vertex,
			Map<String, HashSet<String>> neighbourss) {
		CityCard cityCard;
		cityCard = vertexToCity.get(vertex);
		// each CityCard will be created only once in this part of code
		if (cityCard == null) {
			cityCard = new CityCard();
			vertexToCity.put(vertex, cityCard);
			String label;
			Disease disease = null;
			int r;
			int g;
			int b;
			float x;
			float y;
			float size;
			double eigencentrality;
			int degree;
			label = (String) vertex.getProperty("label");
			r = (int) vertex.getProperty("r");
			g = (int) vertex.getProperty("g");
			b = (int) vertex.getProperty("b");
			if (r == 107 && g == 112 && b == 184) {
				disease = Disease.BLUE;
			}
			else if (r == 153 && g == 153 && b == 153) {
				disease = Disease.BLACK;
			}
			else if (r == 153 && g == 18 && b == 21) {
				disease = Disease.RED;
			}
			else if (r == 242 && g == 255 && b == 0) {
				disease = Disease.YELLOW;
			}

			x = (float) vertex.getProperty("x");
			y = (float) vertex.getProperty("y");
			size = (float) vertex.getProperty("size");
			eigencentrality = Double.valueOf((String) vertex.getProperty("Eigenvector Centrality"));
			degree = Integer.parseInt((String) vertex.getProperty("Degree"));

			cityCard.setLabel(label);
			cityCard.setDisease(disease);
			cityCard.setR(r);
			cityCard.setG(g);
			cityCard.setB(b);
			cityCard.setX(x);
			cityCard.setY(y);
			cityCard.setSize(size);
			cityCard.setEigencentrality(eigencentrality);
			cityCard.setDegree(degree);

			HashSet<String> neighbours = neighbourss.get(cityCard.getCityName());
			if (neighbours == null) {
				neighbours = new HashSet<>();
			}
			neighbourss.put(cityCard.getCityName(), neighbours);
			nameToCityCards.put(cityCard.getCityName(), cityCard);
		}
		return cityCard;
	}

	public GameStatus getGameStatus() {
		return gameStatus;
	}

	public void setGameStatus(GameStatus gameStatus) {
		this.gameStatus = gameStatus;
	}

	public int getTurnDuration() {
		return turnDuration;
	}

	public void setTurnDuration(int turnDuration) {
		this.turnDuration = turnDuration;
	}

	public List<PlayerInterface> getPlayers() {
		return players;
	}

	public void setPlayers(List<PlayerInterface> players) {
		this.players = players;
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(int currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public List<String> getCities() {
		return cities;
	}

	public void setCities(List<String> cities) {
		this.cities = cities;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public int getnPlayers() {
		return nPlayers;
	}

	public void setnPlayers(int nPlayers) {
		this.nPlayers = nPlayers;
	}

	public int getMaxHandSize() {
		return maxHandSize;
	}

	public void setMaxHandSize(int handSize) {
		this.maxHandSize = handSize;
	}

	public String getAiJar() {
		return aiJar;
	}

	public String getCityGraphFilename() {
		return cityGraphFilename;
	}

	public Map<String, CityCard> getNameToCityCards() {
		return nameToCityCards;
	}

	public void setNameToCityCards(Map<String, CityCard> nameToCityCards) {
		this.nameToCityCards = nameToCityCards;
	}

	public fr.dauphine.ja.student.pandemiage.ui.renderers.Renderer getRenderer() {
		return renderer;
	}

	public void setRenderer(fr.dauphine.ja.student.pandemiage.ui.renderers.Renderer renderer) {
		this.renderer = renderer;
	}

	public AiInterface getAiInterface() {
		return aiInterface;
	}

	public void setAiInterface(AiInterface aiInterface) {
		this.aiInterface = aiInterface;
	}

	@Override
	public List<String> getDiscardedInfectionCards() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, List<String>> getNeighbourss() {
		return neighbourss;
	}

	public void setNeighbourss(Map<String, List<String>> neighbourss) {
		this.neighbourss = neighbourss;
	}

}
