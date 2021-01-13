package fr.dauphine.ja.student.pandemiage.ui.renderers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import fr.dauphine.ja.pandemiage.common.DefeatReason;
import fr.dauphine.ja.pandemiage.common.Disease;
import fr.dauphine.ja.pandemiage.common.PlayerCardInterface;
import fr.dauphine.ja.pandemiage.common.PlayerInterface;
import fr.dauphine.ja.student.pandemiage.ai.actions.Action;
import fr.dauphine.ja.student.pandemiage.common.ActionId;
import fr.dauphine.ja.student.pandemiage.common.DiseaseState;
import fr.dauphine.ja.student.pandemiage.common.GameOverException;
import fr.dauphine.ja.student.pandemiage.game.CityContainer;
import fr.dauphine.ja.student.pandemiage.game.cards.CityCard;
import fr.dauphine.ja.student.pandemiage.game.cards.PlayerCard;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngineForSimulation;
import fr.dauphine.ja.student.pandemiage.markers.DiseaseCube;

public class ConsoleRenderer extends  AbstractRenderer {
	int curLevel;

	static String pattern = "___";
	

	public ConsoleRenderer(GameEngineForSimulation gameEngineForSimulation) {
		super(gameEngineForSimulation);
		this.ge = gameEngineForSimulation;
	}

	private String createTab(int level) {
		String res = "";
		for(int i=0; i<level; i++) {
			res+= "    ";
		}
		return res;
	}
	
	private void showLine(int level, String text) {
		this.curLevel = level;
		System.out.println(createTab(level)+ text);	
	}
	
	private String getLine(int size, String pattern) {
		String line ="";

		for(int i=0; i<size; i++) {
			line+=pattern;
		}
		return line;
	}
	
	private void showLineDontStoreLevel(int level, String text) {
		System.out.println(createTab(level)+ text);	
	}
	
	@Override
	public void movePlayerFromTo(String playerLocation, String cityName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moveDiseaseCubeFromSupplyToCity(DiseaseCube diseaseCube, CityContainer cityContainer, Disease disease) {
		showLineDontStoreLevel(curLevel+1, diseaseCube + " is placed on " + cityContainer.getCityCard().getCityName());
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
		showLine(4, "Increase : " + getInfectionRateString());
	}
	
	@Override
	public void stateInfect() {
		showLine(4, "Infect (draw an infection card and put 3 disease cubes on the corresponding city");
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
		showLine(3, "Drawn card : " + playerCard);
	}

	@Override
	public void increaseOutbreaks(String cityName) {
		showLineDontStoreLevel(curLevel+1, "* An outbreak occured in " + cityName);
	}

	@Override
	public void getActions(List<Action>[] actionss) {
		int i=0;
		for(List<Action> actions : actionss) {
			showLine(5, ActionId.values()[i++].name());
			for(Action action : actions) {
				showLine(6, action.toString());
			}
		}
	}

	@Override
	public void stateChosenAction(List<Action>[] actionss, Integer[] coord) {
		showLine(4, "Chosen action is : <" + actionss[coord[0]].get(coord[1])+">");
	}
	
	@Override
	public void discardCard(CityCard cityCard) {
		showLine(5, cityCard + " is discarded");
	}

	@Override
	public void stateDefeated(GameOverException goe) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateTurnNb(int i, PlayerInterface p) {
		
		showLine(1, getLine(60, pattern));
		showLine(1, i + "th turn, " + p );
	}
	
	private String treat(List<?> list) {
		String s = list.toString();
		s = s.substring(1, s.length()-1);
		return s;
	}
	
	private String getDiscoveredCureIndicatorsToString() {
		DiseaseState[] states = ge.getBoard().getDiscoveredCureIndicators();
		ArrayList<String> s = new ArrayList<String>(4);
		for(Disease disease : Disease.values()) {
			s.add (disease + "(" +  states[disease.ordinal()].toString().toLowerCase()+")");
		}
		return treat(s);
	}
	
	private String getDiseaseCubesNumbersToString() {
		Map<Disease, Stack<DiseaseCube>> diseaseCubes = ge.getBoard().getDiseaseCubes();
		ArrayList<String> s = new ArrayList<String>(4);
		for(Disease disease : Disease.values()) {
			s.add (disease + "(" +  Integer.valueOf(diseaseCubes.get(disease).size()).toString().toLowerCase()+")");
		}
		return treat(s);
	}
	
	private void showCurrentState(int level) {
		showLine(level, "Location        : " + ge.getPlayers().get(ge.getCurrentPlayer()).playerLocation());
		showLine(level, "Hand("+ge.getPlayers().get(ge.getCurrentPlayer()).playerHand().size()+")   :" + treat(ge.getPlayers().get(ge.getCurrentPlayer()).playerHand()));
		showLine(level, "Player Deck size: " + ge.getBoard().getPlayerDeck().size());
		showLine(level, "Diseases state  : " + getDiscoveredCureIndicatorsToString());
		showLine(level, "Disease cubes   : " + getDiseaseCubesNumbersToString());
		showLine(level, "Disease c on board  : " + getDiseaseCubeOnBoard());
		showLine(level, "Number of outbreaks : " + ge.getBoard().getNbOutbreaks());
		showLine(level,  getInfectionRateString());
		
	}

	private String getDiseaseCubeOnBoard() {
		String res = "";
		List<String> cs  = ge.allCityNames();
		CityContainer cityContainer;
		String cityContainerS = "";
		boolean isEmpty ;
		ArrayList<String> parts;
		for(String cityName : cs) {
			cityContainer = ge.getBoard().getCityContainers().get(cityName);
			cityContainerS = "[" + (cityName) + " ";
			isEmpty = true;
			for(Disease disease : Disease.values()) {
				List<DiseaseCube> diseases = cityContainer.getDiseaseCubes(disease);
				cityContainerS += ","+  ((disease == Disease.BLACK) ?  disease.name().charAt(0)+"k" : disease.name().charAt(0)) + "("+diseases.size()+")";
				if(diseases.size()>0) {
					isEmpty = false;
				}
			}
			cityContainerS +=  "], ";
			if(!isEmpty) {
				res += cityContainerS;
			}
		}
		
		return res;
	}
	
	private String getInfectionRateString() {
		return "Infection rate  : " + ge.infectionRate() 
		+ " (the marker is on " + (ge.getBoard().getInfectionRatePosition()+1)+ "th position)";
	}
	
	@Override
	public void stateActionNb(int i, String cityName) {
		showLine(3, "Action " + (i+1));
		//showLine(4, getLine(20));
		showLine(4, "Current state");
		showCurrentState(5);
		showLine(4, "Possible choices: ");
	}
	


	@Override
	public void stateDo4Actions() {
		showLine(2, getLine(30, pattern));
		showLine(2, "1) Do 4 actions");
	}

	@Override
	public void draw2PlayerCards() {
		showLine(2, getLine(30, pattern));
		showLine(2, "2) Draw 2 player cards");
	}

	@Override
	public void stateInfectCities() {
		showLine(2, getLine(30, pattern));
		showLine(2, "3) Infect cities (draw infectionRate infection cards and place 1 cube per card");
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
	public void movePlayerCardFromTempToHand(List<PlayerCardInterface> hand, PlayerCardInterface playerCard) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void discard(PlayerInterface p, int pos, PlayerCardInterface discarded) {
		showLine(3, "Discarded card : " + discarded);
	}

	@Override
	public void createPlayersAndOtherStuff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setup() {
		// TODO Auto-generated method stub
		
	}
	


	@Override
	public void stateExperimentStarts(int experimentId) {
		String pattern = "==";
		String line = getLine(1, pattern) + " Experiment " + experimentId + " starts " + getLine(20, pattern); 
		showLine(ge.getDepth(), line);
	}

	@Override
	public void stateExperimentEnds(String experimentId) {
		String pattern = "==";
		String line = getLine(1, pattern) + " Experiment " + experimentId + " ends   " +getLine(20, pattern); 
		showLine(ge.getDepth(), line);
	}
}
