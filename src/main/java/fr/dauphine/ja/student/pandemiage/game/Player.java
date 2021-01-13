package fr.dauphine.ja.student.pandemiage.game;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Stack;

import fr.dauphine.ja.pandemiage.common.DefeatReason;
import fr.dauphine.ja.pandemiage.common.Disease;
import fr.dauphine.ja.pandemiage.common.PlayerCardInterface;
import fr.dauphine.ja.pandemiage.common.PlayerInterface;
import fr.dauphine.ja.pandemiage.common.UnauthorizedActionException;
import fr.dauphine.ja.student.pandemiage.ai.SimulationAi;
import fr.dauphine.ja.student.pandemiage.common.DiseaseState;
import fr.dauphine.ja.student.pandemiage.common.GameOverException;
import fr.dauphine.ja.student.pandemiage.game.cards.CityCard;
import fr.dauphine.ja.student.pandemiage.game.cards.EpidemicCard;
import fr.dauphine.ja.student.pandemiage.game.cards.InfectionCard;
import fr.dauphine.ja.student.pandemiage.game.cards.PlayerCard;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngine;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngineForSimulation;
import fr.dauphine.ja.student.pandemiage.markers.DiseaseCube;
import fr.dauphine.ja.student.pandemiage.markers.Marker;

/**
 * This class represents a Player.
 * 
 * @author Ouba
 *
 */
public class Player implements PlayerInterface, Marker {
	List<PlayerCardInterface> hand;
	GameEngineForSimulation ge;
	CityCard myLocation;
	String name;

	/**
	 * Temporary indices of cards of same color (discover a cure)
	 */
	List<Integer> indices;
	public Player(GameEngine ge) {
		super();
		this.ge = ge;
		hand = new Stack<>();
	}

	private void movePlayerFromTo(String from, String to) {
		// current city
		CityContainer cityContainer = ge.getBoard().getCityContainers().get(from);
		cityContainer.getPlayers().remove(this);
		// new city
		cityContainer = ge.getBoard().getCityContainers().get(to);
		cityContainer.getPlayers().add(this);
		myLocation = cityContainer.getCityCard();
		// render the move on appropriate media
		ge.getRenderer().movePlayerFromTo(from, to);
	}

	@Override
	public void moveTo(String cityName) throws UnauthorizedActionException {
		// TODO check then throw exception if not ok
		// current city
		movePlayerFromTo(playerLocation(), cityName);
	}

	@Override
	public void flyTo(String cityName) throws UnauthorizedActionException {
		movePlayerFromTo(playerLocation(), cityName);
		CityCard cityCard = ge.getNameToCityCards().get(cityName);
		// discard destination card
		hand.remove(cityCard);
		ge.getRenderer().discardCard(cityCard);
	}

	@Override
	public void flyToCharter(String cityName) throws UnauthorizedActionException {
		CityCard playerLocation = ge.getNameToCityCards().get(playerLocation());
		movePlayerFromTo(playerLocation(), cityName);

		// discard player location card
		hand.remove(playerLocation);
		ge.getRenderer().discardCard(playerLocation);
	}

	@Override
	public void skipTurn() {
	}

	@Override
	public void treatDisease(Disease disease) throws UnauthorizedActionException {
		Board board = ge.getBoard();
		CityContainer cityContainer = ge.getBoard().getCityContainers().get(playerLocation());
		DiseaseState diseaseState = ge.getBoard().getDiscoveredCureIndicators()[disease.ordinal()];
		// disease is active
		if (diseaseState == DiseaseState.ACTIVE) {
			board.moveDiseaseCubeFromCityToSupply(cityContainer, disease, 1);
		}
		// disease is cured, it might be eradicated
		else if (diseaseState == DiseaseState.CURED) {
			// move all the diseases in current player position to the disease supply
			int nb = cityContainer.getDiseaseCubes()[disease.ordinal()].size();
			board.moveDiseaseCubeFromCityToSupply(cityContainer, disease, nb);
			// check if this disease has just been eradicated. For this, browse all cities
			// and check to see if there no more disease left
			boolean thereAreCubesRemaining = false;
			for (CityContainer cc : ge.getBoard().getCityContainers().values()) {
				if (cc.getCityCard().getDisease() == disease) {
					if (cc.getDiseaseCubes(disease).size() > 0) {
						thereAreCubesRemaining = true;
						break;
					}
				}
			}
			if (!thereAreCubesRemaining) {
				setDiseaseState(disease, DiseaseState.ERADICATED);
			}
		}
	}

	public void setDiseaseState(Disease disease, DiseaseState diseaseState) {
		ge.getBoard().getDiscoveredCureIndicators()[disease.ordinal()] = diseaseState;
		ge.getRenderer().setDiseaseState(disease, diseaseState);
	}

	@Override
	public void discoverCure(List<PlayerCardInterface> cardNames) throws UnauthorizedActionException {
		//ArrayList<Integer> indices;
		//indices = new ArrayList<>(5);
		boolean fiveFound = false;

		int i;
		int n = cardNames.size();
		if (n < 5) {
			throw new UnauthorizedActionException();
		}
		Disease curedDisease = cardNames.get(0).getDisease();
		Board board = ge.getBoard();
		/*// is there 5 cards of the same color? Collect their position in the hand
		for (Disease disease : Disease.values()) {
			fiveFound = false;
			curedDisease = disease;
			// for each player card in hand
			for (i = 0; i < n; i++) {
				if (hand.get(i).getDisease() == disease) {
					indices.add(i);
					if (indices.size() == 5) {
						fiveFound = true;
						break;
					}
				}
			}
			if(fiveFound) {
				break;
			}
			else {
				indices.clear();
			}
		}*/
		// discard them

			for (i = 0; i < indices.size(); i++) {
				board.movePlayerCardsFromHandToDiscardPile(this, indices.get(i) - i);
			}
		
		setDiseaseState(curedDisease, DiseaseState.CURED);
	}

	@Override
	public String playerLocation() {
		return myLocation.getCityName();
	}

	@Override
	public List<PlayerCardInterface> playerHand() {
		return hand;
	}

	/**
	 * After doing 4 actions, draw the top 2 cards together from the Player Deck.
	 * 
	 * @author Ouba
	 * @param gameEngine
	 * @throws GameOverException
	 */
	public void draw2PlayerCards() throws GameOverException {
		ge.getRenderer().draw2PlayerCards();
		PlayerCard playerCard;
		Board board = ge.getBoard();
		InfectionCard infectionCard;
		if (board.getPlayerDeck().size() < 2) {
			GameOverException gameOverException = new GameOverException(DefeatReason.NO_MORE_PLAYER_CARDS,
					"No more player cards", ge);
			throw gameOverException;
		}
		for (int j = 0; j < 2; j++) {
			playerCard = board.movePlayerCardFromDeckToTemp();
			// store the coordinate of the discard action
			((SimulationAi)ge.getAiInterface()).setJ(j);
			((SimulationAi)ge.getAiInterface()).setPlayerCard(playerCard);
			// If your draws include any Epidemic cards, immediately do the following
			// steps in order:
			// 1) Increase
			// 2) Infect
			// 3) Intensify
			if (playerCard instanceof EpidemicCard) {
				// 1) Increase
				increase();
				// 2) Infect:
				// Draw the bottom card from the Infection Deck. Unless its
				// disease color has been eradicated, put 3 disease cubes of that color on
				// the named city. If the city already has cubes of this color, do not add
				// 3 cubes to it. Instead, add just enough cubes so that it has 3 cubes of
				// this color and then an outbreak of this disease occurs in the city (see
				// Outbreaks below). Discard this card to the Infection Discard Pile.
				ge.getRenderer().stateInfect();
				infectionCard = board.drawInfectionCardFromBottom();
				infectCity(infectionCard, 3);
				// 3) Intensify
				// Reshuffle just the cards in the Infection Discard Pile and
				// place them on top of the Infection Deck.
				board.shuffle(ge.getBoard().getInfectionDiscardPile());
				board.moveInfectionCardsFromDiscardPileToInfectionDeck(ge.getBoard().getInfectionDiscardPile().size());
				// After resolving any Epidemic cards, remove them from the game.
			}
			// Otherwise, add it to your hand (it is a CityCard)
			else {
				board.movePlayerCardFromTempToHand(hand, (PlayerCardInterface) playerCard);
				ge.getAiInterface().discard(ge, this, ge.getMaxHandSize(), -1);
			}
		}

	}

	/**
	 * Move the infection rate marker forward 1 space on the Infection Rate Track.
	 */
	protected void increase() {
		int infectionRatePosition = ge.getBoard().getInfectionRatePosition();
		if (infectionRatePosition < ge.getBoard().getInfectionRateTrack().length - 1) {
			infectionRatePosition++;
			ge.getBoard().setInfectionRatePosition(infectionRatePosition);
		}
		ge.getRenderer().stateIncrease();
	}

	/**
	 * Flip over as many Infection cards from the top of the Infection Deck as the
	 * current infection rate. This number is below the space of the Infection Rate
	 * Track that has the infection rate marker. Flip these cards over one at a
	 * time, infecting the city named on each card.
	 * 
	 * @author Ouba
	 * @param gameEngine
	 */
	public void infectCities() throws GameOverException {
		ge.getRenderer().stateInfectCities();
		Deque<InfectionCard> infectionDeck = ge.getBoard().getInfectionDeck();
		Stack<InfectionCard> infectionDiscardPile = ge.getBoard().getInfectionDiscardPile();
		InfectionCard infectionCard;
		int infectionRatePosition = ge.getBoard().getInfectionRatePosition();
		int infectionRate = ge.getBoard().getInfectionRateTrack()[infectionRatePosition];
		for (int i = 0; i < infectionRate; i++) {
			infectionCard = infectionDeck.pop();
			infectionDiscardPile.push(infectionCard);
			infectCity(infectionCard, 1);
		}

	}

	/**
	 * Infect the city of the infection card passed as argument. Unless its disease
	 * color has been eradicated, put 3 disease cubes of that color on the named
	 * city. If the city already has cubes of this color, do not add 3 cubes to it.
	 * Instead, add just enough cubes so that it has 3 cubes of this color and then
	 * an outbreak of this disease occurs in the city
	 * 
	 * 
	 * @param infectionCard
	 * @param nCubesOfInfection
	 *            Depending of the type of the infection, the number of cubes to add
	 *            to a city differs. When we're dealing with the infection caused by
	 *            drawing an epidemic card, we should add 3 cubes. When we're
	 *            dealing with the infection that is part of the 3rd activity in the
	 *            turn of a player, we should add 1 cube.
	 * @throws GameOverException
	 */
	public void infectCity(InfectionCard infectionCard, int nCubesOfInfection) throws GameOverException {
		boolean isEradicated = ge.getBoard().getDiscoveredCureIndicators()[infectionCard.getDisease()
				.ordinal()] == DiseaseState.ERADICATED;
		if (!isEradicated) {
			Stack<String> frontier = new Stack<>();
			LinkedHashSet<String> outbreakCities = new LinkedHashSet<>();
			frontier.add(infectionCard.getCityName());
			infectCity(infectionCard.getDisease(), frontier, outbreakCities, nCubesOfInfection);
		}
	}

	private void infectCity(Disease disease, Stack<String> frontier, LinkedHashSet<String> outbreakCities,
			int nCubesOfInfection) throws GameOverException {
		Board board = ge.getBoard();
		String cityName;
		List<DiseaseCube> cityDiseaseCubes;
		int excedent;
		int nbToAdd;
		boolean isFirstCity = true;
		int nCubes;
		CityContainer cityContainer;
		while (frontier.size() > 0) {
			// pop next city from frontier
			cityName = frontier.pop();
			cityContainer = board.getCityContainers().get(cityName);
			cityDiseaseCubes = cityContainer.getDiseaseCubes()[disease.ordinal()];
			nCubes = isFirstCity ? nCubesOfInfection : 1;
			isFirstCity = isFirstCity ? false : isFirstCity;
			excedent = cityDiseaseCubes.size() + nCubes - 3;
			// how many cubes should we add
			nbToAdd = Math.min(nCubes, 3 - cityDiseaseCubes.size());
			// complete to 3
			board.moveDiseaseCubeFromSupplyToCity(cityContainer, disease, nbToAdd);
			// there is an outbreak
			if (excedent > 0) {
				outbreakCities.add(cityName);
				handleOutbreak(cityName, disease, frontier, outbreakCities);
			}
		}
	}

	private void handleOutbreak(String cityName, Disease disease, Stack<String> frontier,
			LinkedHashSet<String> outbreakCities) throws GameOverException {
		List<String> neighbours = ge.neighbours(cityName);
		int outbreakMarker = ge.getBoard().increaseOutbreaks(cityName);
		// game over!
		if (outbreakMarker == GameEngine.FATAL_OUTBREAK_NUMBER) {
			GameOverException gameOverException = new GameOverException(DefeatReason.TOO_MANY_OUTBREAKS,
					"Fatal number of outbreaks is reached", ge);
			throw gameOverException;
		}
		for (String neighbour : neighbours) {
			if (!outbreakCities.contains(neighbour)) {
				frontier.add(neighbour);
			}
		}
	}
	
	

	public List<PlayerCardInterface> getHand() {
		return hand;
	}

	public void setHand(List<PlayerCardInterface> hand) {
		this.hand = hand;
	}

	public CityCard getMyLocation() {
		return myLocation;
	}

	public void setMyLocation(CityCard myLocation) {
		this.myLocation = myLocation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public PlayerForSimulation duplicate() {
		PlayerForSimulation playerfs = new PlayerForSimulation(null);
		List<PlayerCardInterface> hand = new ArrayList<>();
		for (PlayerCardInterface pc : this.hand) {
			hand.add(pc);
		}
		playerfs.setHand(hand);
		// GameEngine ge;
		playerfs.setMyLocation(this.myLocation);
		playerfs.setName(this.name);

		return playerfs;
	}

	public GameEngineForSimulation getGe() {
		return ge;
	}

	public void setGe(GameEngineForSimulation ge) {
		this.ge = ge;
	}

	public List<Integer> getIndices() {
		return indices;
	}

	public void setIndices(List<Integer> indices) {
		this.indices = indices;
	}
	
	
}
