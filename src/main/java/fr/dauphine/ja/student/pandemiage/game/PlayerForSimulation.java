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
import fr.dauphine.ja.student.pandemiage.ai.actions.GetOutException;
import fr.dauphine.ja.student.pandemiage.common.DiseaseState;
import fr.dauphine.ja.student.pandemiage.common.GameCutException;
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
public class PlayerForSimulation extends Player {


	public PlayerForSimulation(GameEngine ge) {
		super(ge);
		this.ge = ge;
		hand = new Stack<>();
	}


	/**
	 * After doing 4 actions, draw the top 2 cards together from the Player Deck.
	 * If an action is given, go directly to the discard instruction, discard everything before.
	 * 
	 * @author Ouba
	 * @param discardAction 
	 * @param jStart 
	 * @param gameEngine
	 * @throws GameOverException
	 * @throws GameCutException 
	 * @throws GetOutException 
	 */
	public void draw2PlayerCards(Integer jStart, List<Integer> discardAction) throws GameOverException, GameCutException, GetOutException {
		boolean isActionGiven = discardAction!=null;
		ge.getRenderer().draw2PlayerCards();
		PlayerCard playerCard = null;
		Board board = ge.getBoard();
		InfectionCard infectionCard;
		boolean isEpidemicCard;
		if(board.getPlayerDeck().size()<2) {
			GameOverException gameOverException = new GameOverException(DefeatReason.NO_MORE_PLAYER_CARDS,
					"No more player cards", ge);
			throw gameOverException;
		}
		jStart = (jStart == null)? 0 : jStart;
		for (int j = jStart; j < 2; j++) {
			if(!isActionGiven) {
				playerCard = board.movePlayerCardFromDeckToTemp();
			}
			// If your draws include any Epidemic cards, immediately do the following
			// steps in order:
			// 1) Increase
			// 2) Infect
			// 3) Intensify
			isEpidemicCard = playerCard instanceof EpidemicCard;
			if (isEpidemicCard) {
				// if action is given, don't do this part
				if(!isActionGiven) {
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
					//	Reshuffle just the cards in the Infection Discard Pile and
					//	place them on top of the Infection Deck.
					board.shuffle(ge.getBoard().getInfectionDiscardPile());
					board.moveInfectionCardsFromDiscardPileToInfectionDeck(ge.getBoard().getInfectionDiscardPile().size());
					// After resolving any Epidemic cards, remove them from the game.	
					//-
					// this state is the consequence of the execution of 4th action:
					// cut-off depth is reached
					// or terminal node is reached, end of simulation
					/*if(ge.cutoff()) {
						throw new GameCutException();
					}	*/				
				}
			}
			// Otherwise, add it to your hand (it is a CityCard)
			if(!isEpidemicCard) {
				// if action is given don't do this step as it has already be done
				if(!isActionGiven) {
					board.movePlayerCardFromTempToHand(hand, (PlayerCardInterface) playerCard);
				}
				// we reach the coordinate of the given action, execute it, it is the starting node of the simulation
				((SimulationAi) ge.getAiInterface()).discard(ge, this, ge.getMaxHandSize(), -1, j, discardAction);
			}
			// after jStart and its corresponding action has been executed, for next j there is no given action
			discardAction = null;
			isActionGiven = false;
		}
	}	
}
