package fr.dauphine.ja.student.pandemiage.game;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import fr.dauphine.ja.pandemiage.common.DefeatReason;
import fr.dauphine.ja.pandemiage.common.Disease;
import fr.dauphine.ja.pandemiage.common.PlayerCardInterface;
import fr.dauphine.ja.pandemiage.common.PlayerInterface;
import fr.dauphine.ja.student.pandemiage.common.DiseaseState;
import fr.dauphine.ja.student.pandemiage.common.GameOverException;
import fr.dauphine.ja.student.pandemiage.game.cards.EpidemicCard;
import fr.dauphine.ja.student.pandemiage.game.cards.InfectionCard;
import fr.dauphine.ja.student.pandemiage.game.cards.PlayerCard;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngine;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngineForSimulation;
import fr.dauphine.ja.student.pandemiage.markers.DiseaseCube;

public class Board {
	GameEngineForSimulation ge;
	int[] infectionRateTrack;
	int infectionRatePosition;
	int nbOutbreaks;

	DiseaseState[] discoveredCureIndicators;

	/**
	 * A Deque allows operation at both end of a collection. We choose it here
	 * because the collection of infection card will be accessed from its both end.
	 * from top: during setup, we should initially infect 9 cities, the infection
	 * cards are draw from the top of the deck. During city infection, after the
	 * player has executed 4 action and after he has drawn the top 2 cards. from
	 * bottom: to realize an infection , the infection cards are drawn from the
	 * bottom of the deck.
	 */
	Deque<InfectionCard> infectionDeck;
	Stack<InfectionCard> infectionDiscardPile;

	Stack<PlayerCard> playerDeck;
	/**
	 * When a player card is drawn, this is the place where is temporary displayed before it goes to the player hand
	 */
	Stack<PlayerCard> temp;
	Stack<PlayerCard> playerDiscardPile;

	Stack<EpidemicCard> epidemicCards;

	/**
	 * For each Disease a list of available cubes (i.e. those that are not on the
	 * board yet)
	 */
	Map<Disease, Stack<DiseaseCube>> diseaseCubes;
	/**
	 * [City name] --> CityContainer
	 */
	Map<String, CityContainer> cityContainers;

	public Board(GameEngine ge) {
		infectionDeck = new ArrayDeque<>();
		infectionDiscardPile = new Stack<>();

		playerDeck = new Stack<>();
		temp = new Stack<>();
		playerDiscardPile = new Stack<>();

		epidemicCards = new Stack<>();
		discoveredCureIndicators = new DiseaseState[Disease.values().length];
		this.ge = ge;
		cityContainers = new HashMap<>();
	}

	public int getInfectionRatePosition() {
		return infectionRatePosition;
	}

	public void setInfectionRatePosition(int infectionRatePosition) {
		this.infectionRatePosition = infectionRatePosition;
	}

	public int getNbOutbreaks() {
		return nbOutbreaks;
	}

	public void setNbOutbreaks(int outbreakPosition) {
		this.nbOutbreaks = outbreakPosition;
	}

	public DiseaseState[] getDiscoveredCureIndicators() {
		return discoveredCureIndicators;
	}

	public void setDiscoveredCureIndicators(DiseaseState[] discoveredCureIndicators) {
		this.discoveredCureIndicators = discoveredCureIndicators;
	}

	public void setInfectionDiscardPile(Stack<InfectionCard> infectionDiscardPile) {
		this.infectionDiscardPile = infectionDiscardPile;
	}

	public Stack<PlayerCard> getPlayerDeck() {
		return playerDeck;
	}

	public Stack<PlayerCard> getPlayerDiscardPile() {
		return playerDiscardPile;
	}

	public int[] getInfectionRateTrack() {
		return infectionRateTrack;
	}

	public void setInfectionRateTrack(int[] infectionRateTrack) {
		this.infectionRateTrack = infectionRateTrack;
	}

	public Deque<InfectionCard> getInfectionDeck() {
		return infectionDeck;
	}

	public void setInfectionDeck(Deque<InfectionCard> infectionDeck) {
		this.infectionDeck = infectionDeck;
	}

	public Stack<InfectionCard> getInfectionDiscardPile() {
		return infectionDiscardPile;
	}

	public Map<String, CityContainer> getCityContainers() {
		return cityContainers;
	}

	public Stack<EpidemicCard> getEpidemicCards() {
		return epidemicCards;
	}

	public void setEpidemicCards(Stack<EpidemicCard> epidemicCards) {
		this.epidemicCards = epidemicCards;
	}

	/**
	 * Add DiseaseCube to a city on the board.
	 * 
	 * @param cityName
	 * @param disease
	 * @param diseaseCube
	 */
	public void addDiseaseCube(String cityName, Disease disease, DiseaseCube diseaseCube) {
		CityContainer container = cityContainers.get(cityName);
		container.addDiseaseCube(disease, diseaseCube);
	}

	public void setPlayerDeck(Stack<PlayerCard> playerDeck) {
		this.playerDeck = playerDeck;
	}

	public Map<Disease, Stack<DiseaseCube>> getDiseaseCubes() {
		return diseaseCubes;
	}

	public void setDiseaseCubes(Map<Disease, Stack<DiseaseCube>> diseaseCubes) {
		this.diseaseCubes = diseaseCubes;
	}

	public GameEngineForSimulation getGe() {
		return ge;
	}

	public void setGe(GameEngineForSimulation gefs) {
		this.ge = gefs;
	}

	/**
	 * Get a Disease cube from the supply.
	 * 
	 * @param disease
	 * @return
	 * @throws GameOverException
	 */
	private DiseaseCube popDiseaseCube(Disease disease) throws GameOverException {
		DiseaseCube diseaseCube = null;
		if(diseaseCubes.get(disease).size()>0) {
			diseaseCube = diseaseCubes.get(disease).pop();
		}
		else {
			GameOverException gameOverException = new GameOverException(DefeatReason.NO_MORE_BLOCKS, "No more block",
					 ge);
			throw gameOverException;
		}
		return diseaseCube;
	}

	public PlayerCard movePlayerCardFromDeckToTemp() throws GameOverException {
		PlayerCard playerCard;
		if (playerDeck.size() > 0) {
			playerCard = playerDeck.pop();
			ge.getRenderer().movePlayerCardFromDeckToTemp(playerCard);
		}
		else {
			GameOverException gameOverException = new GameOverException(DefeatReason.NO_MORE_PLAYER_CARDS,
					"No more player cards", ge);
			throw gameOverException;
		}
		return playerCard;
	}
	
	public void movePlayerCardFromTempToHand(List<PlayerCardInterface>hand, PlayerCardInterface playerCard) {
		hand.add((PlayerCardInterface) playerCard);
		ge.getRenderer().movePlayerCardFromTempToHand(hand, playerCard);
	}

	public void pushPlayerCard(PlayerCard playerCard) {
		playerDiscardPile.push(playerCard);
	}

	/**
	 * Add a disease cube to the supply.
	 * 
	 * @param disease
	 * @param diseaseCube
	 */
	private void pushDiseaseCubeToSupply(Disease disease, DiseaseCube diseaseCube) {
		diseaseCubes.get(disease).push(diseaseCube);
	}
	


	public void moveDiseaseCubeFromSupplyToCity(CityContainer cityContainer, Disease disease, int nb)
			throws GameOverException {
		DiseaseCube diseaseCube;
		for (int i = 0; i < nb; i++) {
			diseaseCube = popDiseaseCube(disease);
			cityContainer.getDiseaseCubes(disease).add(diseaseCube);
			ge.getRenderer().moveDiseaseCubeFromSupplyToCity(diseaseCube, cityContainer, disease);
		}
	}

	public void moveDiseaseCubeFromCityToSupply(CityContainer cityContainer, Disease disease, int nb) {
		DiseaseCube diseaseCube;
		int n;
		for (int i = 0; i < nb; i++) {
			n= cityContainer.getDiseaseCubes(disease).size();
			diseaseCube = cityContainer.getDiseaseCubes(disease).remove(n-1);
			pushDiseaseCubeToSupply(disease, diseaseCube);
			ge.getRenderer().moveDiseaseCubeFromCityToSupply(diseaseCube, cityContainer, disease);
		}
	}
	
	public void movePlayerCardsFromHandToDiscardPile(PlayerInterface playerInterface , int pos) {
		Board board = ge.getBoard();
		List<PlayerCardInterface> hand = playerInterface.playerHand();
		PlayerCardInterface removed = hand.remove(pos);
		board.getPlayerDiscardPile().push((PlayerCard) removed);
		ge.getRenderer().movePlayerCardsFromHandToDiscardPile(playerInterface, pos);
	}
	
	public void drawPlayerCard(PlayerInterface playerInterface) {
		Board board = ge.getBoard();
		List<PlayerCardInterface> hand = playerInterface.playerHand();
	}
	

	public InfectionCard drawInfectionCardFromBottom() {
		InfectionCard infectionCard = infectionDeck.removeFirst();
		return infectionCard;
	}
	
	public void moveInfectionCardsFromDiscardPileToInfectionDeck(int nb) {

		for(int i=0; i<nb; i++) {
			moveInfectionCardsFromDiscardPileToInfectionDeck();
		}
	}

	private void moveInfectionCardsFromDiscardPileToInfectionDeck() {
		InfectionCard infectionCard;
		infectionCard = infectionDiscardPile.pop();
		infectionDeck.push(infectionCard);
		ge.getRenderer().moveInfectionCardsFromDiscardPileToInfectionDeck();
	}
	
	public void shuffle(List<?> list) {
		Collections.shuffle(list, ge.getRandom());
		ge.getRenderer().shuffle(list);
	}

	public int increaseOutbreaks(String cityName) {
		nbOutbreaks++;
		ge.getRenderer().increaseOutbreaks(cityName);
		return nbOutbreaks;
	}

	/**
	 * Discard a player card, ie, move it from hand to discard pile
	 * @param p
	 * @param pos
	 * @return 
	 */
	public PlayerCardInterface discard(PlayerInterface p, int pos) {
		PlayerCardInterface discarded = p.playerHand().remove(pos);
		playerDiscardPile.push((PlayerCard) discarded);
		ge.getRenderer().discard(p, pos, discarded);
		return discarded;
	}
	
	
	
	public void setCityContainers(Map<String, CityContainer> cityContainers) {
		this.cityContainers = cityContainers;
	}

	public Stack<PlayerCard> getTemp() {
		return temp;
	}

	public void setTemp(Stack<PlayerCard> temp) {
		this.temp = temp;
	}

	public void setPlayerDiscardPile(Stack<PlayerCard> playerDiscardPile) {
		this.playerDiscardPile = playerDiscardPile;
	}

	public Board duplicate()  {
		Board board = new Board(null);
		/*GameEngine ge;
		int[] infectionRateTrack;
		int infectionRatePosition;
		int nbOutbreaks;

		DiseaseState[] discoveredCureIndicators;
		Deque<InfectionCard> infectionDeck;
		Stack<InfectionCard> infectionDiscardPile;

		Stack<PlayerCard> playerDeck;
		Stack<PlayerCard> temp;
		Stack<PlayerCard> playerDiscardPile;

		Stack<EpidemicCard> epidemicCards;
		Map<Disease, Stack<DiseaseCube>> diseaseCubes;
		Map<String, CityContainer> cityContainers;*/
		
		
		board.setInfectionRateTrack(infectionRateTrack);
		board.setInfectionRatePosition(infectionRatePosition);
		
		board.setNbOutbreaks(nbOutbreaks);
		board.setDiscoveredCureIndicators(discoveredCureIndicators.clone());
		board.setInfectionDeck(((ArrayDeque) infectionDeck).clone());
		board.setInfectionDiscardPile((Stack<InfectionCard>) infectionDiscardPile.clone());
		board.setPlayerDeck((Stack<PlayerCard>) playerDeck.clone());
		board.setTemp((Stack<PlayerCard>) temp.clone());
		board.setPlayerDiscardPile((Stack<PlayerCard>) playerDiscardPile.clone());
		board.setEpidemicCards((Stack<EpidemicCard>) epidemicCards.clone());
		//board.setDiseaseCubes((Map<Disease, Stack<DiseaseCube>>) ((HashMap<Disease, Stack<DiseaseCube>>) diseaseCubes).clone());
		Map<Disease, Stack<DiseaseCube>> diseaseCubes = new HashMap<>();
		board.setDiseaseCubes(diseaseCubes);
		for(Entry<Disease, Stack<DiseaseCube>> entry : this.diseaseCubes.entrySet()) {
			diseaseCubes.put(entry.getKey(), (Stack<DiseaseCube>) entry.getValue().clone());
		}

		Map<String, CityContainer> cityContainers = new HashMap<>();
		board.setCityContainers(cityContainers);
		for(Entry<String, CityContainer> entry : this.cityContainers.entrySet()) {
			cityContainers.put(entry.getKey(), (CityContainer) entry.getValue().clone());
		}
		return board;
	}

}
