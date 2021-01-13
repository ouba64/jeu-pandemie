package fr.dauphine.ja.student.pandemiage.ui.renderers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import fr.dauphine.ja.pandemiage.common.DefeatReason;
import fr.dauphine.ja.pandemiage.common.Disease;
import fr.dauphine.ja.pandemiage.common.PlayerCardInterface;
import fr.dauphine.ja.pandemiage.common.PlayerInterface;
import fr.dauphine.ja.student.pandemiage.ai.actions.Action;
import fr.dauphine.ja.student.pandemiage.common.DiseaseState;
import fr.dauphine.ja.student.pandemiage.common.GameOverException;
import fr.dauphine.ja.student.pandemiage.game.CityContainer;
import fr.dauphine.ja.student.pandemiage.game.Player;
import fr.dauphine.ja.student.pandemiage.game.cards.CityCard;
import fr.dauphine.ja.student.pandemiage.game.cards.PlayerCard;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngineForSimulation;
import fr.dauphine.ja.student.pandemiage.markers.DiseaseCube;
import fr.dauphine.ja.student.pandemiage.ui.Gui;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class JavafxRenderer extends AbstractRenderer {
	Gui gui;
	/**
	 * Mapping that maps board elements (decks, players, pawns, etc.) to its graphical representation
	 */
	Map<Object, Node> mapping;
	public JavafxRenderer(GameEngineForSimulation gameEngineForSimulation, Gui gui) {
		super(gameEngineForSimulation);
		this.gui = gui;
		mapping = new HashMap<>();
	}

	@Override
	public void stateDo4Actions() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw2PlayerCards() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateInfectCities() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDefeated(String msg, DefeatReason defeatReason) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setVictorious() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void movePlayerFromTo(String playerLocation, String cityName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moveDiseaseCubeFromSupplyToCity(DiseaseCube diseaseCube, CityContainer cityContainer, Disease disease) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moveDiseaseCubeFromCityToSupply(DiseaseCube diseaseCube, CityContainer cityContainer, Disease disease) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDiseaseState(Disease disease, DiseaseState diseaseState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void movePlayerCardsFromHandToDiscardPile(PlayerInterface playerInterface, int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateIncrease() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shuffle(List<?> list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moveInfectionCardsFromDiscardPileToInfectionDeck() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void movePlayerCardFromDeckToTemp(PlayerCard playerCard) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void increaseOutbreaks(String cityName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getActions(List<Action>[] actionss) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateChosenAction(List<Action>[] actionss, Integer[] coord) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateDefeated(GameOverException goe) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateTurnNb(int i, PlayerInterface p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateActionNb(int i, String cityName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void movePlayerCardFromTempToHand(List<PlayerCardInterface> hand, PlayerCardInterface playerCard) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void discard(PlayerInterface p, int pos, PlayerCardInterface discarded) {
		// TODO Auto-generated method stub
		
	}
	
	public static void position(Node node, int x, int y) {
		double height = node.getBoundsInParent().getHeight();
		double width = node.getBoundsInParent().getWidth();
		node.setTranslateX(x-950+(width/2));
		node.setTranslateY(y-450+(height/2));
	}
	
	public static void move(Node node, int toX, int toY) {
        TranslateTransition translate = 
        new TranslateTransition(Duration.millis(750)); 
        int toX_ = toX - 950;
        int toY_ = toY - 500;
        translate.setToX(toX_); 
        translate.setToY(toY_); 
        translate.setNode(node);
        translate.play();
	}

	@Override
	public void createPlayersAndOtherStuff() {
		Runnable runnable = new Runnable() {
          @Override
          public void run() {
      		
      		//
      		// create players (only 1 in this implementation)
      		/*for (int i = 0; i < nPlayers; i++) {
      			Player p = new Player(this);
      			p.setName("Player " + (i + 1));
      			players = new ArrayList<>();
      			players.add(p);
      		}
      		this.board = new Board(this);*/
      		Player p;
      		// p graphical component
      		Node gPlayer;
      		for (int i = 0; i < ge.getnPlayers(); i++) {
      			p = (Player) ge.getPlayers().get(i);
      			gPlayer = gui.createPlayer();
      			gui.getgBoard().getChildren().add(gPlayer);
      			position(gPlayer,1800, 770 );


      			mapping.put(p, gPlayer);
      		}
      	
      	
      		//
      		// create player deck : only city card at this stage
      		Rectangle gCityCard;
      		List<CityCard> cityCards = new ArrayList<>( ge.getNameToCityCards().values());
      		for (CityCard cityCard : cityCards) {
      			gCityCard = new Rectangle(138, 196);
      			gui.getgBoard().getChildren().add(gCityCard);
      			position(gCityCard, 1429, 527);
      			gCityCard.setFill(Color.STEELBLUE);

      			mapping.put(cityCard, gCityCard);
      		}
      		
      		//
      		// create disease cube
      	/*	Map<Disease, Stack<DiseaseCube>> diseaseCubes = new HashMap<>();

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
      		}*/
      		
      		
      			/*
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
      		// set the disease states
      		for (int i = 0; i < board.getDiscoveredCureIndicators().length; i++) {
      			board.getDiscoveredCureIndicators()[i] = DiseaseState.ACTIVE;
      		}
      		//
      		// create infection rate mark
      		int[] infectionRateTrack = new int[] { 2, 2, 2, 3, 3, 4, 4 };
      		board.setInfectionRateTrack(infectionRateTrack);
      		*/
          }
      };
		Platform.runLater(runnable);
	}

	@Override
	public void setup() {
		List<PlayerInterface> p = ge.getPlayers();
		// move pawn
		Node gc = mapping.get(p.get(0));
		move(gc, 275, 379);
	}

	@Override
	public void discardCard(CityCard cityCard) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateInfect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateExperimentStarts(int experimentId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateExperimentEnds(String experimentId) {
		// TODO Auto-generated method stub
		
	}

}
