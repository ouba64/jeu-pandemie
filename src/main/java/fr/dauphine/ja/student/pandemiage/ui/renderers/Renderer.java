package fr.dauphine.ja.student.pandemiage.ui.renderers;

import java.util.List;

import fr.dauphine.ja.pandemiage.common.DefeatReason;
import fr.dauphine.ja.pandemiage.common.Disease;
import fr.dauphine.ja.pandemiage.common.PlayerCardInterface;
import fr.dauphine.ja.pandemiage.common.PlayerInterface;
import fr.dauphine.ja.student.pandemiage.ai.actions.Action;
import fr.dauphine.ja.student.pandemiage.common.DiseaseState;
import fr.dauphine.ja.student.pandemiage.common.GameOverException;
import fr.dauphine.ja.student.pandemiage.game.CityContainer;
import fr.dauphine.ja.student.pandemiage.game.cards.CityCard;
import fr.dauphine.ja.student.pandemiage.game.cards.PlayerCard;
import fr.dauphine.ja.student.pandemiage.markers.DiseaseCube;

/**
 * A Renderer knows how to show the game while it is going on. It renders on a
 * medium, the states and actions of the game. There are basically 2 types of
 * media where the information about the game are displayed: Console and Javafx
 * Gui. For instance, when the player executes the action, "fly from Paris to
 * London", the console renderer shows a text stating : "Player 1 goes from
 * Paris to London". while the gui renderer on the hand, makes an animation of
 * the pawn representing the player thats translate from Paris location to
 * London's.
 * 
 * @author Nassim
 *
 */
public interface Renderer {

	void movePlayerFromTo(String playerLocation, String cityName);

	void moveDiseaseCubeFromSupplyToCity(DiseaseCube diseaseCube, CityContainer cityContainer, Disease disease);

	void moveDiseaseCubeFromCityToSupply(DiseaseCube diseaseCube, CityContainer cityContainer, Disease disease);

	void setDiseaseState(Disease disease, DiseaseState diseaseState);

	void movePlayerCardsFromHandToDiscardPile(PlayerInterface playerInterface, int i);

	/**
	 * Render this action : Move the infection rate marker forward 1 space on the
	 * Infection Rate Track.
	 */
	void stateIncrease();

	/**
	 * Shuffle cards
	 * 
	 * @param list
	 *            the list of cards after they have bein shuffled
	 */
	void shuffle(List<?> list);

	void moveInfectionCardsFromDiscardPileToInfectionDeck();

	void movePlayerCardFromDeckToTemp(PlayerCard playerCard);

	void increaseOutbreaks(String cityName);

	void getActions(List<Action>[] actionss);

	void stateChosenAction(List<Action>[] actionss, Integer[] coord);

	void stateDefeated(GameOverException goe);

	void stateTurnNb(int i, PlayerInterface p);

	void stateDo4Actions();

	void stateActionNb(int i, String cityName);

	void draw2PlayerCards();

	void stateInfectCities();

	void setDefeated(String msg, DefeatReason defeatReason);

	void setVictorious();

	void movePlayerCardFromTempToHand(List<PlayerCardInterface> hand, PlayerCardInterface playerCard);

	/**
	 * Render the discard of a player card
	 * @param p The player
	 * @param pos The position in hand of the card to discard
	 * @param discarded The discarded card
	 */
	void discard(PlayerInterface p, int pos, PlayerCardInterface discarded);

	void createPlayersAndOtherStuff();

	void setup();

	/**
	 * When some moves are executed, we should discard either the start or destination of the move
	 * @param cityCard
	 */
	void discardCard(CityCard cityCard);

	void stateInfect();

	void stateExperimentStarts(int experimentId);

	void stateExperimentEnds(String string);

}
