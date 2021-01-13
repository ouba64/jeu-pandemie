package fr.dauphine.ja.student.pandemiage.gameengine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import fr.dauphine.ja.pandemiage.common.AiInterface;
import fr.dauphine.ja.pandemiage.common.DefeatReason;
import fr.dauphine.ja.pandemiage.common.Disease;
import fr.dauphine.ja.pandemiage.common.GameInterface;
import fr.dauphine.ja.pandemiage.common.GameStatus;
import fr.dauphine.ja.pandemiage.common.PlayerCardInterface;
import fr.dauphine.ja.pandemiage.common.PlayerInterface;
import fr.dauphine.ja.student.pandemiage.ai.SimulationAi;
import fr.dauphine.ja.student.pandemiage.ai.actions.Action;
import fr.dauphine.ja.student.pandemiage.ai.actions.GetOutException;
import fr.dauphine.ja.student.pandemiage.common.DiseaseState;
import fr.dauphine.ja.student.pandemiage.common.GameCutException;
import fr.dauphine.ja.student.pandemiage.common.GameOverException;
import fr.dauphine.ja.student.pandemiage.common.GameWonException;
import fr.dauphine.ja.student.pandemiage.game.Board;
import fr.dauphine.ja.student.pandemiage.game.Player;
import fr.dauphine.ja.student.pandemiage.game.PlayerForSimulation;
import fr.dauphine.ja.student.pandemiage.game.cards.CityCard;
import fr.dauphine.ja.student.pandemiage.ui.Cli;
import fr.dauphine.ja.student.pandemiage.ui.Gui;
import fr.dauphine.ja.student.pandemiage.ui.renderers.ConsoleRenderer;
import fr.dauphine.ja.student.pandemiage.ui.renderers.Renderers;

/**
 * Empty GameEngine implementing GameInterface
 *
 */
public class GameEngineForSimulation implements GameInterface {

	/**
	 * The number of cubes per color
	 */
	public static final int NB_CUBES_BY_DESEASE = 24;
	public static final int NB_EPIDEMIC_CARDS = 6;
	/**
	 * When we reach this number of outbreak, the game is over
	 */
	public static final int FATAL_OUTBREAK_NUMBER = 8;
	protected final String aiJar;
	protected final String cityGraphFilename;
	protected GameStatus gameStatus;
	int turnDuration;
	public static Object waitLock = new Object();

	protected List<PlayerInterface> players;
	AiInterface aiInterface;
	int currentPlayer;
	Board board;

	List<String> cities;
	Map<String, CityCard> nameToCityCards;
	Map<String, List<String>> neighbourss;

	protected Random random;
	protected long seed;
	int difficulty;
	int nPlayers = 1;
	int maxHandSize;
	int initialPlayerDeckSize;
	
	/**
	 * This is used to pass info between threads.
	 */
	GameOverException goe;
	GameWonException gwe;

	fr.dauphine.ja.student.pandemiage.ui.renderers.Renderer renderer;
	
	/**
	 * Rank importance: 1st has coefficient 4, 2nd, 3, etc.
	 * 
	 */
	static double[] rankImportance = new double[] {4, 3, 2, 1};
	
	/**
	 * The importance of each component in the computation of a state value.
	 * 
	 * 	// 0) outbreaks
		// 1-4) disease cube left
		// 5) player deck size
		// 6) nb of diseases cured
		// 7) nb of diseases eradicated
		// 8) nb of cards of a disease. Bigger ratio => nearer to get 5 card of the same disease => discover cure
		// 9) do not discard a lot of card => player deck won't shrink fast. Correlated with "player deck size"?

		// 10) fly to cities that are on the brink of outbreak
	 */
	//								       0  1  2  3  4  5  6  7  8  9
	//static double[] coeffs = new double[] {9, 3, 3, 3, 3, 1, 7, 7, 5, 2};  <--- 2 victories
	static double[] coeffs = new double[] {9, 3, 3, 3, 3, 1, 8, 7, 5, 2};
	static double totalCoeff;
	static {
		for(double coeff : coeffs) {
			totalCoeff+=coeff;
		}
	}
	
	/**
	 * This is the return of hMonteCarloMinimax(s). hMonteCarloMinimax is like EXPECTIMINIMAX (See Artificial Intelligence
	 * 3rd ed, P178) but instead of computing the expected value of CHANCE with all chance rolls, we do a monte carlo
	 * experiment and thus compute the expected value with a selection of chance rolls.
	 * The preffix h stands for heuristic, which means we cut the search at a depth d.
	 */
	double hMonteCarloMinimax;
	/**
	 * When we're using monte carlo method, the number of experiment to conduct.
	 * 1 is for debuggage, change that !
	 */
	int monteCarloN = 1;
	

	/**
	 * Current node depth
	 */
	int depth = 0;
	
	/**
	 * Cut-off depth
	 */
	int depthMax = 2;
	
	public static int experimentId;

	/*
	 * public GameEngine(String cityGraphFilename, String aiJar) {
	 * this.cityGraphFilename = cityGraphFilename; this.aiJar = aiJar;
	 * this.gameStatus = GameStatus.ONGOING;
	 * 
	 * random = new Random(seed); renderer = new VectorRenderer(); }
	 */
	
	public GameEngineForSimulation() {
		cityGraphFilename = null;
		aiJar = null;
	}

	public GameEngineForSimulation(String aiJar, String cityGraphFilename, int turnDuration, long seed, int difficulty, int nPlayers,
			int handSize, boolean isGui, boolean isConsole, Gui gui) {
		super();
		this.aiJar = aiJar;
		this.cityGraphFilename = cityGraphFilename;
		this.turnDuration = turnDuration;
		this.seed = seed;
		this.difficulty = difficulty;
		this.nPlayers = nPlayers;
		this.maxHandSize = handSize;
		random = new Random(seed);
		List<fr.dauphine.ja.student.pandemiage.ui.renderers.Renderer> renderers = new ArrayList<>();

		if(isConsole) {
			//renderers.add(new ConsoleRenderer(this));
		}
		renderer = new Renderers(this, renderers);
	}
	
	/**
	 * The depth of this node is the depth of the parent + 1
	 * @return
	 */
	public GameEngineForSimulation duplicateState() {
		GameEngineForSimulation gefs = new GameEngineForSimulation();
		gefs.setGameStatus(gameStatus);
		List<PlayerInterface> playersfs = new ArrayList<>();
		gefs.setPlayers(playersfs);
		PlayerForSimulation playerfs ;
		Player player;
		for(PlayerInterface p : this.players) {
			player =(Player) p;
			playerfs = player.duplicate();
			playerfs.setGe(gefs);
			playersfs.add(playerfs);
		}
		SimulationAi aiInterface = ((SimulationAi)this.aiInterface).duplicate();
		aiInterface.setGe(gefs);
		gefs.setAiInterface(aiInterface);
		gefs.setCurrentPlayer(currentPlayer);
		
		aiInterface.setP(playersfs.get(gefs.getCurrentPlayer()));
		
        Board boardf = board.duplicate();
		gefs.setBoard(boardf);
		boardf.setGe(gefs);
		
		gefs.setCities(cities);
		gefs.setNameToCityCards(nameToCityCards);
		gefs.setNeighbourss(neighbourss);
		/**
		 * 	int difficulty;
			int nPlayers = 1;
			int handSize;
		 */
		gefs.setDifficulty(difficulty);
		gefs.setnPlayers(nPlayers);
		gefs.setMaxHandSize(maxHandSize);
		/*
		int monteCarloN;
		int depth ;
		int depthMax;
		*/
		gefs.setMonteCarloN(monteCarloN);
		gefs.setDepth(depth+1);
		gefs.setMaxHandSize(maxHandSize);
		
		gefs.setInitialPlayerDeckSize(initialPlayerDeckSize);
		gefs.setRandom(random);
		

		List<fr.dauphine.ja.student.pandemiage.ui.renderers.Renderer> rendererList = new ArrayList<>();
		// TODO for debugging purpose, comment in production
		//rendererList.add(new ConsoleRenderer(this));		
		Renderers renderers = new Renderers(this, rendererList);
		gefs.setRenderer(renderers);
		return gefs;
	}

	// Do not change!
	private void setDefeated(String msg, DefeatReason dr) {
		gameStatus = GameStatus.DEFEATED;
		SimulationAi.showLine(depth, gameStatus + "," + dr);
	}

	// Do not change!
	private void setVictorious() {
		gameStatus = GameStatus.VICTORIOUS;
		SimulationAi.showLine(depth, gameStatus+" ");
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

	public void loop(Integer i, Action action, Integer j, List<Integer> discardAction) 
			throws GameWonException, GameCutException, GameOverException {
		// Load Ai from Jar file
		experimentId++;
		renderer.stateExperimentStarts(experimentId);
		SimulationAi ai;

		ai = (SimulationAi) aiInterface;
		PlayerInterface p;
		int turn = 1;
		// each loop call executes the given action until it reach the next action, upon which it exits (by catching GetOutException)
		try {
			while (gameStatus == GameStatus.ONGOING) {
				p = getNextPlayer();
				renderer.stateTurnNb(turn++, p);
				ai.playTurn(this, p, i, action, j, discardAction);
				Player player = (Player) p;
				// 3. Infect cities.
				player.infectCities();
				i = 0;
				action = null;
				j = null;
				discardAction = null;
			}
		}
		catch(GetOutException e) {
			
		}

		// game ends
		renderer.stateExperimentEnds(experimentId + "  (" + hMonteCarloMinimax + ")");
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

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
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

	public double gethMonteCarloMinimax() {
		return hMonteCarloMinimax;
	}

	public void sethMonteCarloMinimax(double hMonteCarloMinimax) {
		this.hMonteCarloMinimax = hMonteCarloMinimax;
	}

	public int getMonteCarloN() {
		return monteCarloN;
	}

	public void setMonteCarloN(int monteCarloN) {
		this.monteCarloN = monteCarloN;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}


	public int getDepthMax() {
		return depthMax;
	}

	public void setDepthMax(int depthMax) {
		this.depthMax = depthMax;
	}
	
	
	
	
	public Map<String, List<String>> getNeighbourss() {
		return neighbourss;
	}

	public void setNeighbourss(Map<String, List<String>> neighbourss) {
		this.neighbourss = neighbourss;
	}


	
	
	public GameOverException getGoe() {
		return goe;
	}

	public void setGoe(GameOverException goe) {
		this.goe = goe;
	}

	public GameWonException getGwe() {
		return gwe;
	}

	public void setGwe(GameWonException gwe) {
		this.gwe = gwe;
	}

	public int getInitialPlayerDeckSize() {
		return initialPlayerDeckSize;
	}

	public void setInitialPlayerDeckSize(int initialPlayerDeckSize) {
		this.initialPlayerDeckSize = initialPlayerDeckSize;
	}
	
	public boolean cutoff() {
		return depth>=depthMax;
	}

	/**
	 * Set a value of hMonteCarloMinimax.
	 * 
	 */
	public void computeUtility() {
		double[] v =new double[coeffs.length];
		// 0) outbreaks
		v[0] = 1-((double)getNbOutbreaks()/FATAL_OUTBREAK_NUMBER);
		if(v[0]==0) {
			hMonteCarloMinimax = 0;
			return;
		}
		// 1-4) disease cube left
		for(Disease disease : Disease.values()) {
			v[1+disease.ordinal()] = (double)board.getDiseaseCubes().get(disease).size()/NB_CUBES_BY_DESEASE; 
			if(v[1+disease.ordinal()]==0) {
				hMonteCarloMinimax = 0;
				return;
			}
		}
		// 5) player deck size
		v[5] = (double)board.getPlayerDeck().size()/initialPlayerDeckSize;
		if(v[5]==0) {
			hMonteCarloMinimax = 0;
			return;
		}
		// 6) nb of diseases cured
		for(DiseaseState diseaseState : board.getDiscoveredCureIndicators()) {
			v[6] +=  (diseaseState == DiseaseState.CURED ? 1 : 0);
		}
		v[6] = v[6]/4;
		// 7) nb of diseases eradicated
		for(DiseaseState diseaseState : board.getDiscoveredCureIndicators()) {
			v[7] +=  (diseaseState == DiseaseState.ERADICATED ? 1 : 0);
		}
		v[7] = v[7]/4;
		// 8) nb of cards of a disease. Bigger ratio => nearer to get 5 card of the same disease => discover cure
		// try to gather the disease that are still active
		ArrayList<Double> ratios  = new ArrayList<>(4);
		for(int i=0; i<4; i++) {
			ratios.add((double) 0);
		}
		int diseasePos;
		List<PlayerCardInterface> hand = players.get(currentPlayer).playerHand();
		int handSize = hand.size();
		for(PlayerCardInterface playerCard : hand) {
			diseasePos = playerCard.getDisease().ordinal();
			ratios.set(diseasePos, ratios.get(diseasePos)+1);
		}
		DiseaseState[] diseaseStates = board.getDiscoveredCureIndicators();
		int active;
		// normalize
		for(int i=0; i<4; i++) {
			active = diseaseStates[i]==DiseaseState.ACTIVE ? 1 : 0;
			ratios.set(i, (active * ratios.get(i))/handSize);
		}
		Collections.sort(ratios);
		int j;
		double sum = 0;
		double sumCoeffs = 0;
		for(int i=ratios.size()-1; i>=0; i--) {
			j=3-i;
			sum+=(rankImportance[j] * ratios.get(i));
			sumCoeffs += rankImportance[j];
		}
		v[8] = sum/sumCoeffs;
		
		// 9) do not discard a lot of card => player deck won't shrink fast. Correlated with "player deck size"?
		v[9] = (double)handSize/(Cli.DEFAULT_HAND_SIZE+1);
		
		// 10) fly to cities that are on the brink of outbreak
		
		// compute score of action
		double score = 0;
		for(int i = 0; i<coeffs.length; i++) {
			score += (coeffs[i] * v[i]);
		}
		score = score/totalCoeff;
		hMonteCarloMinimax = score;
	}

	@Override
	public String toString() {
		return "ge" + depth;
	}
}
