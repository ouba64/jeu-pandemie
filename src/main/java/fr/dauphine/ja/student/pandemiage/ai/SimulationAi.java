package fr.dauphine.ja.student.pandemiage.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.dauphine.ja.pandemiage.common.AiInterface;
import fr.dauphine.ja.pandemiage.common.Disease;
import fr.dauphine.ja.pandemiage.common.GameInterface;
import fr.dauphine.ja.pandemiage.common.PlayerCardInterface;
import fr.dauphine.ja.pandemiage.common.PlayerInterface;
import fr.dauphine.ja.pandemiage.common.UnauthorizedActionException;
import fr.dauphine.ja.student.pandemiage.ai.actions.Action;
import fr.dauphine.ja.student.pandemiage.ai.actions.CharterFlightAction;
import fr.dauphine.ja.student.pandemiage.ai.actions.DirectFlightAction;
import fr.dauphine.ja.student.pandemiage.ai.actions.DiscoverACureAction;
import fr.dauphine.ja.student.pandemiage.ai.actions.DriveAction;
import fr.dauphine.ja.student.pandemiage.ai.actions.GetOutException;
import fr.dauphine.ja.student.pandemiage.ai.actions.PassAction;
import fr.dauphine.ja.student.pandemiage.ai.actions.TreatDiseaseAction;
import fr.dauphine.ja.student.pandemiage.common.ActionId;
import fr.dauphine.ja.student.pandemiage.common.GameCutException;
import fr.dauphine.ja.student.pandemiage.common.GameOverException;
import fr.dauphine.ja.student.pandemiage.common.GameWonException;
import fr.dauphine.ja.student.pandemiage.game.CityContainer;
import fr.dauphine.ja.student.pandemiage.game.Player;
import fr.dauphine.ja.student.pandemiage.game.PlayerForSimulation;
import fr.dauphine.ja.student.pandemiage.game.cards.PlayerCard;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngineForSimulation;
import fr.dauphine.ja.student.pandemiage.markers.DiseaseCube;

/**
 * An Ai engine that is represented by a Thread
 * 
 * @author Ouba
 *
 */
public abstract class SimulationAi implements AiInterface {
	GameEngineForSimulation ge;
	protected PlayerInterface p;
	// protected GameInterface g;

	protected static final long SEED = 1;
	private static final boolean DEBUG_SIMULATIONS = false;
	// protected Integer go = null;

	/**
	 * This random will be used by the different AI when time is up to choose their
	 * action in a very fast way. Indeed, it is better to choose a poor action but
	 * still be alive, rather than to wait until time is up and loose the game.
	 */
	protected Random random;

	protected Action passAction = new PassAction(null, null);

	/**
	 * i-th action
	 */
	int i;
	
	/**
	 * j-th drawn player card
	 */
	int j;
	/**
	 * The playerCard that corresponds to previous j
	 */
	PlayerCard playerCard;
	
	
	public SimulationAi() {
		random = new Random(SEED);
	}

	public void playTurn(GameInterface g, PlayerInterface p, Integer i, Action action, Integer j,
			List<Integer> discardAction) throws GameWonException, GameCutException, GameOverException, GetOutException {
		playTurnInternal(g, p, i, action, j, discardAction);
	}

	/**
	 * Give the actions that are available in the current state.
	 * 
	 * @return A list of actions per type of action. We do things this way so that
	 *         each type of action will be equally likely to be picked up in
	 *         RandomAi
	 */
	public List<Action>[] getActions() {
		@SuppressWarnings("unchecked")
		List<Action>[] actionss = new ArrayList[ActionId.values().length];
		List<Action> actions;
		int n = p.playerHand().size();
		PlayerCardInterface playerCard;
		PlayerCardInterface myLocationCityCard;
		List<PlayerCardInterface> hand = p.playerHand();
		String playerLocation = p.playerLocation();
		int i;
		Action action;
		for (ActionId actionId : ActionId.values()) {
			// actionId = actionIds[random.nextInt(actionIds.length)];
			actions = new ArrayList<>();
			actionss[actionId.ordinal()] = actions;
			switch (actionId) {

			// Discard the City card that matches the city you are in to move to any city
			case CHARTER_FLIGHT:
				CharterFlightAction charterFlightAction;
				int j;
				// i are player cards
				for (i = 0; i < n; i++) {
					playerCard = p.playerHand().get(i);
					if (playerCard.getCityName().equals(p.playerLocation())) {
						int k = ge.allCityNames().size();
						// every city in the board is a potential action
						for (j = 0; j < k; j++) {
							// don't fly to the city one is in
							if (j == i) {
								continue;
							}
							charterFlightAction = new CharterFlightAction(ge, (Player) p, ge.allCityNames().get(j));
							actions.add(charterFlightAction);
						}

						break;

					}
				}
				break;
			// Discard a City card to move to the city named on the card.
			case DIRECT_FLIGHT:
				// the destination should be different than the city where the player is
				// currently, so look for the card representing
				// the city where the player is, so that we can discard it while looking for a
				// destination
				myLocationCityCard = null;
				for (i = 0; i < n; i++) {
					myLocationCityCard = p.playerHand().get(i);
					if (myLocationCityCard.getCityName().equals(p.playerLocation())) {
						break;
					}
				}

				for (j = 0; j < p.playerHand().size(); j++) {
					if (j == i) {
						continue;
					}
					action = new DirectFlightAction(ge, (Player) p, p.playerHand().get(j).getCityName());
					actions.add(action);
				}

				break;
			case DISCOVER_A_CURE:
				ArrayList<Integer> indices;
				indices = new ArrayList<>(5);
				boolean fiveFound = false;
				Disease curedDisease = null;

				// is there 5 cards of the same color? Collect their position in the hand
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
				}
				if (fiveFound) {
					action = new DiscoverACureAction(ge, (Player) p, indices, curedDisease, null);
					actions.add(action);
				}
				break;
			case DRIVE:
				List<String> neighbours = ge.neighbours(playerLocation);
				for (String neighbour : neighbours) {
					action = new DriveAction(ge, (Player) p, neighbour);
					actions.add(action);
				}
				break;
			case TREAT_DISEASE:
				// we can treat any disease color that is on the city
				CityContainer cityContainer = ge.getBoard().getCityContainers().get(playerLocation);
				TreatDiseaseAction treatDiseaseAction;
				// find the color that have cubes
				for (List<DiseaseCube> diseaseCubes : cityContainer.getDiseaseCubes()) {
					if (diseaseCubes.size() > 0) {
						treatDiseaseAction = new TreatDiseaseAction(ge, (Player) p, diseaseCubes, diseaseCubes.get(0).getDisease());
						actions.add(treatDiseaseAction);
					}
				}

				break;
			case PASS:
				actions.add(passAction);
			default:
				break;
			}
		}
		ge.getRenderer().getActions(actionss);
		return actionss;
	}

	/**
	 * Each player turn is divided into 3 parts: 1. Do 4 actions. 2. Draw 2 Player
	 * cards. 3. Infect cities. In simulation, after the loop method is called, the
	 * simulation should skip the execution of all the instructions before the
	 * starting node of the simulation
	 * 
	 * (i, action) are together <br>
	 * (j, List<Integer> discardAction) are together <br>
	 * The 2 pairs cannot both be non-null, either one is
	 * 
	 * @author Ouba
	 * @throws GameOverException
	 * @throws GameWonException
	 * @throws GameCutException 
	 * @throws GetOutException 
	 */
	public void playTurnInternal(GameInterface g, PlayerInterface p, Integer i, Action action, Integer j,
			List<Integer> discardAction) throws GameOverException, GameWonException, GameCutException, GetOutException {
		boolean isActionGiven = action != null;
		List<Action>[] actionss;
		// skip 4 actions if i is not given
		if (i != null) {
			// play 4 actions
			// You may do up to 4 actions each turn.
			// Select any combination of the actions listed below. You may do the same
			// action
			// several times, each time counting as 1 action. Your role’s special abilities
			// may change
			// how an action is done. Some actions involve discarding a card from your hand;
			// all these
			// discards go to the Player Discard Pile.
			ge.getRenderer().stateDo4Actions();
			for (int k = i; k < 4; k++) {
				// we reach the coordinate of the candidate action, this is the starting point
				// of the simulation
				if (isActionGiven && k == i) {
					try {
						// execute given action then go on with execution until one reaches the next action
						action.execute();
					}
					catch (UnauthorizedActionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// we are in the simulation
				else {
					ge.getRenderer().stateActionNb(i, p.playerLocation());
					actionss = getActions();
					Integer[] coord;
					coord = chooseBestAction(actionss, k, null, null, null);
					ge.getRenderer().stateChosenAction(actionss, coord);
					action = actionss[coord[0]].get(coord[1]);
					throw new GetOutException();					
				}
			}
		}

		PlayerForSimulation player = (PlayerForSimulation) p;
		// 2. Draw 2 Player cards.
		player.draw2PlayerCards(j, discardAction);
	}

	/**
	 * The AI should choose an action (one of the 4 actions it should do during its
	 * turn) from the list of all actions. We are beyond the starting node of the
	 * simulation. At the begining of the simulation, i, j are null.
	 * 
	 * @param i
	 *            : i-th action (out of 4)
	 * @param j
	 *            : null
	 * @param givenDiscardAction
	 *            : null
	 * @param actionss
	 * @return
	 * @throws GameCutException 
	 */
	public Integer[] chooseBestAction(List<Action>[] actionss, Integer i, Action givenAction, Integer j,
			List<Integer> givenDiscardAction) throws GameCutException {
		GameEngineForSimulation geNew;
		@SuppressWarnings("unused")
		Action bestAction = null;
		double sum;
		double hMonteCarloMinimax;
		double bestHMonteCarloMinimax;
		// for each action, do a simulation to get its corresponding H-Minimax
		bestHMonteCarloMinimax = -1;
		List<Action> actions;
		Action action;
		Action actionNew;
		int actBest = 0;
		int acBest = 0;
		// the consequence of an action executed is right before the subsequent action execution
		if(ge.cutoff()) {	
			throw new GameCutException();
		}
		
		// for each type of action (move, flight, etc.)
		for (int act = 0; act < ActionId.values().length; act++) {
			actions = actionss[act];
			// for each action of the type
			for (int ac = 0; ac < actions.size(); ac++) {
				sum = 0;
				action = actions.get(ac);
				// monte carlo method: do n experiments and compute the average utility value
				for (int k = 0; k < ge.getMonteCarloN(); k++) {
					// duplicate current state and continue the search with the duplicata and give
					// the candidate action. This execution will continue the game until it reaches an exception
					geNew = ge.duplicateState();
					actionNew = action.duplicate(geNew);
					showLine(geNew.getDepth(), "a"+i + "=" + actionNew);
					try {
						// after actionNew is executed, it will eventually end up with either a victory, either a defeat, either depth limit
						geNew.loop(i, actionNew, null, null);
					}
					catch (GameOverException goe) {
						//setDefeated2(goe.getMessage(), goe.getDefeatReason());
						geNew.computeUtility();
						SimulationAi.showLine(geNew.getDepth(), goe.getDefeatReason().name()+"("+geNew.gethMonteCarloMinimax()+")");
					}
					catch (GameWonException e) {
						//setVictorious2();
						geNew.computeUtility();
						SimulationAi.showLine(geNew.getDepth(), "x victory");
					}
					catch (GameCutException e) {
						geNew.computeUtility();
						SimulationAi.showLine(geNew.getDepth(), "x cut("+geNew.gethMonteCarloMinimax()+")");
					}
					hMonteCarloMinimax = geNew.gethMonteCarloMinimax();
					sum += hMonteCarloMinimax;
				}
				hMonteCarloMinimax = sum / ge.getMonteCarloN();
				if (hMonteCarloMinimax > bestHMonteCarloMinimax) {
					bestHMonteCarloMinimax = hMonteCarloMinimax;
					bestAction = action;
					actBest = act;
					acBest = ac;
				}
			}
		}
		ge.sethMonteCarloMinimax(bestHMonteCarloMinimax);
		showLine(ge.getDepth(), "MAX=" + ge.gethMonteCarloMinimax());
		return new Integer[] { actBest, acBest };
	}
	
	public static String createTab(int level) {
		String res = "";
		for(int i=0; i<level; i++) {
			res+= "    ";
		}
		return res;
	}
	public static void showLine(int level, String text) {
		if(DEBUG_SIMULATIONS) {
			System.err.println(createTab(level)+ (level) + ") " +text);	
		}
	}

	public List<Integer> chooseBestDiscardAction(List<List<Integer>> discardActions, Integer j) throws GameCutException {
		GameEngineForSimulation geNew = null;
		List<Integer> bestDiscardAction = null;
		double sum;
		double hMonteCarloMinimax;
		double bestHMonteCarloMinimax;
		// for each action, do a simulation to get its corresponding H-Minimax
		bestHMonteCarloMinimax = -1;
		
		// the consequence of an action executed is right before the subsequent action execution
		if(ge.cutoff()) {	
			throw new GameCutException();
		}
		
		for (List<Integer> discardAction : discardActions) {
			sum = 0;
			// monte carlo method: do n experiments and compute the average utility value
			for (int k = 0; k < ge.getMonteCarloN(); k++) {
				// duplicate current state and continue the search with the duplicata and give
				// the candidate action
				geNew = ge.duplicateState();
				showLine(geNew.getDepth(), "d" + j +"="+ discardAction);
				try {
					geNew.loop(null, null, j, discardAction);
				}
				catch (GameOverException goe) {
					//setDefeated2(goe.getMessage(), goe.getDefeatReason());
					geNew.computeUtility();
					SimulationAi.showLine(geNew.getDepth(), geNew.getGameStatus()+"("+geNew.gethMonteCarloMinimax()+")");
				}
				catch (GameWonException e) {
					//setVictorious2();
					geNew.computeUtility();
					SimulationAi.showLine(geNew.getDepth(), "x victory");
				}
				catch (GameCutException e) {
					geNew.computeUtility();
					SimulationAi.showLine(geNew.getDepth(), "x cut("+geNew.gethMonteCarloMinimax()+")");
				}
				hMonteCarloMinimax = geNew.gethMonteCarloMinimax();
				sum += hMonteCarloMinimax;
			}
			hMonteCarloMinimax = sum / ge.getMonteCarloN();
			if (hMonteCarloMinimax > bestHMonteCarloMinimax) {
				bestHMonteCarloMinimax = hMonteCarloMinimax;
				bestDiscardAction = discardAction;
			}
		}
		return bestDiscardAction;
	}

	public List<PlayerCardInterface> discard(GameInterface g, PlayerInterface p, int maxHandSize, int nbEpidemicCards,
			Integer j, List<Integer> givenDiscardAction) throws GameCutException, GetOutException {
		boolean isActionGiven = givenDiscardAction != null;
		List<PlayerCardInterface> discardeds = new ArrayList<>();
		// action is given (this is one action candidate and this is the starting node
		// of the simulation)
		// and we reach its coordinate
		if (isActionGiven) {
			discardeds = executeDiscardAction(g, p, maxHandSize, nbEpidemicCards, givenDiscardAction, discardeds);
		}
		// we are in the simulation at a node beyond the initial candidate action's node
		else {
			int handSize = p.playerHand().size();
			int nToDiscard = handSize - maxHandSize;
			GameEngineForSimulation geNew = null;
			List<Integer> bestDiscardAction = null;
			if (nToDiscard > 0) {
				// discard action is represented by the list of indices to remove from hand
				List<List<Integer>> discardActions = getDiscardActions(nToDiscard);
				bestDiscardAction = chooseBestDiscardAction(discardActions, j);
				throw new GetOutException();
				// get best action among the previous one and execute it
				//discardeds = executeDiscardAction(geNew, p, maxHandSize, nbEpidemicCards, bestDiscardAction,
				//		discardeds);
			}
		}
		return discardeds;
	}

	@Override
	public List<PlayerCardInterface> discard(GameInterface g, PlayerInterface p, int maxHandSize, int nbEpidemicCards) {
		int handSize = p.playerHand().size();
		int nToDiscard = handSize - maxHandSize;
		List<Integer> bestDiscardAction = null;
		List<PlayerCardInterface> discardeds = null;
		if (nToDiscard > 0) {
			discardeds = new ArrayList<>();
			// discard action is represented by the list of indices to remove from hand
			List<List<Integer>> discardActions = getDiscardActions(nToDiscard);
			bestDiscardAction = chooseBestDiscardAction(discardActions);
			// get best action among the previous one and execute it
			discardeds = executeDiscardAction(ge, p, maxHandSize, nbEpidemicCards, bestDiscardAction, discardeds);
		}
		return discardeds;
	}

	public List<List<Integer>> getDiscardActions(int nToDiscard) {
		// what are the distinct tuples of size nToDiscard cards that we can take out of
		// the hand
		int handSize = ge.getMaxHandSize();
		ArrayList<Integer> tuple;
		ArrayList<List<Integer>> actions = new ArrayList<>();
		for (int i = 0; i < handSize; i++) {
			tuple = new ArrayList<>(1);
			tuple.add(i);
			actions.add(tuple);
		}

		// nDiscard cannot be bigger than: we draw 1 card, we check hand size limit
		return actions;
	}

	private List<PlayerCardInterface> executeDiscardAction(GameInterface g, PlayerInterface p2, int maxHandSize,
			int nbEpidemicCards, List<Integer> givenDiscardAction, List<PlayerCardInterface> discardeds) {
		int k = 0;
		PlayerCardInterface discarded;
		for (Integer pos : givenDiscardAction) {
			discarded = ge.getBoard().discard(p, pos - k);
			discardeds.add(discarded);
			k++;
		}
		return discardeds;
	}


	public PlayerInterface getP() {
		return p;
	}

	public void setP(PlayerInterface p) {
		this.p = p;
	}

	public GameEngineForSimulation getGe() {
		return ge;
	}

	public void setGe(GameEngineForSimulation ge) {
		this.ge = ge;
	}

	@Override
	public void playTurn(GameInterface g, PlayerInterface p) {
	}

	// public abstract List<Integer> chooseBestDiscardAction(List<List<Integer>>
	// discardActions) ;

	public List<Integer> chooseBestDiscardAction(List<List<Integer>> discardActions) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
	public int getJ() {
		return j;
	}

	public void setJ(int j) {
		this.j = j;
	}

	public PlayerCard getPlayerCard() {
		return playerCard;
	}

	public void setPlayerCard(PlayerCard playerCard) {
		this.playerCard = playerCard;
	}
	
	

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public abstract SimulationAi duplicate();

}
