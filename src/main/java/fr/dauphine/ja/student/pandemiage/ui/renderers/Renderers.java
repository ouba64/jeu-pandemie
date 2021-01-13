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
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngine;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngineForSimulation;
import fr.dauphine.ja.student.pandemiage.markers.DiseaseCube;

/**
 * Group of Renderers
 *
 */
public class Renderers implements Renderer {
	GameEngineForSimulation ge;

	List<Renderer> renderers;

	public Renderers(GameEngineForSimulation gameEngineForSimulation, List<Renderer> renderers) {
		super();
		this.ge = gameEngineForSimulation;
		this.renderers = renderers;
	}

	

	public GameEngineForSimulation getGe() {
		return ge;
	}



	public void setGe(GameEngineForSimulation ge) {
		this.ge = ge;
	}



	@Override
	public void movePlayerFromTo(String playerLocation, String cityName) {
		for (Renderer renderer : renderers) {
			renderer.movePlayerFromTo(playerLocation, cityName);
		}
	}

	@Override
	public void moveDiseaseCubeFromSupplyToCity(DiseaseCube diseaseCube, CityContainer cityContainer, Disease disease) {
		for (Renderer renderer : renderers) {
			renderer.moveDiseaseCubeFromSupplyToCity(diseaseCube, cityContainer, disease);
		}
	}

	@Override
	public void moveDiseaseCubeFromCityToSupply(DiseaseCube diseaseCube, CityContainer cityContainer, Disease disease) {
		for (Renderer renderer : renderers) {
			renderer.moveDiseaseCubeFromCityToSupply(diseaseCube, cityContainer, disease);
		}
	}

	@Override
	public void setDiseaseState(Disease disease, DiseaseState diseaseState) {
		for (Renderer renderer : renderers) {
			renderer.setDiseaseState(disease, diseaseState);
		}
	}

	@Override
	public void movePlayerCardsFromHandToDiscardPile(PlayerInterface playerInterface, int i) {
		for (Renderer renderer : renderers) {
			renderer.movePlayerCardsFromHandToDiscardPile(playerInterface, i);
		}
	}

	@Override
	public void stateIncrease() {
		for (Renderer renderer : renderers) {
			renderer.stateIncrease();
		}
	}

	@Override
	public void shuffle(List<?> list) {
		for (Renderer renderer : renderers) {
			renderer.shuffle(list);
		}
	}

	@Override
	public void moveInfectionCardsFromDiscardPileToInfectionDeck() {
		for (Renderer renderer : renderers) {
			renderer.moveInfectionCardsFromDiscardPileToInfectionDeck();
		}
	}

	@Override
	public void movePlayerCardFromDeckToTemp(PlayerCard playerCard) {
		for (Renderer renderer : renderers) {
			renderer.movePlayerCardFromDeckToTemp(playerCard);
		}
	}

	@Override
	public void increaseOutbreaks(String cityName) {
		for (Renderer renderer : renderers) {
			renderer.increaseOutbreaks(cityName);
		}
	}

	@Override
	public void getActions(List<Action>[] actionss) {
		for (Renderer renderer : renderers) {
			renderer.getActions(actionss);
		}
	}

	@Override
	public void stateChosenAction(List<Action>[] actionss, Integer[] coord) {
		for (Renderer renderer : renderers) {
			renderer.stateChosenAction(actionss, coord);
		}
	}

	@Override
	public void stateDefeated(GameOverException goe) {
		for (Renderer renderer : renderers) {
			renderer.stateDefeated(goe);
		}
	}

	@Override
	public void stateTurnNb(int i, PlayerInterface p) {
		for (Renderer renderer : renderers) {
			renderer.stateTurnNb(i, p);
		}
	}

	@Override
	public void stateActionNb(int i, String cityName) {
		for (Renderer renderer : renderers) {
			renderer.stateActionNb(i, cityName);
		}
	}

	@Override
	public void stateDo4Actions() {
		for (Renderer renderer : renderers) {
			renderer.stateDo4Actions();
		}
	}

	@Override
	public void draw2PlayerCards() {
		for (Renderer renderer : renderers) {
			renderer.draw2PlayerCards();
		}
	}

	@Override
	public void stateInfectCities() {
		for (Renderer renderer : renderers) {
			renderer.stateInfectCities();
		}
	}

	@Override
	public void setDefeated(String msg, DefeatReason defeatReason) {
		for (Renderer renderer : renderers) {
			renderer.setDefeated(msg, defeatReason);
		}
	}

	@Override
	public void setVictorious() {
		for (Renderer renderer : renderers) {
			renderer.setVictorious();
		}
	}

	@Override
	public void movePlayerCardFromTempToHand(List<PlayerCardInterface> hand, PlayerCardInterface playerCard) {
		for (Renderer renderer : renderers) {
			renderer.movePlayerCardFromTempToHand(hand, playerCard);
		}
	}

	@Override
	public void discard(PlayerInterface p, int pos, PlayerCardInterface discarded) {
		for (Renderer renderer : renderers) {
			renderer.discard(p, pos, discarded);
		}
	}

	@Override
	public void createPlayersAndOtherStuff() {
		for (Renderer renderer : renderers) {
			renderer.createPlayersAndOtherStuff();
		}
	}

	@Override
	public void setup() {
		for (Renderer renderer : renderers) {
			renderer.setup();
		}
	}

	@Override
	public void discardCard(CityCard cityCard) {
		for (Renderer renderer : renderers) {
			renderer.discardCard(cityCard);
		}
	}

	@Override
	public void stateInfect() {
		for (Renderer renderer : renderers) {
			renderer.stateInfect();
		}
	}



	@Override
	public void stateExperimentStarts(int experimentId) {
		for (Renderer renderer : renderers) {
			renderer.stateExperimentStarts(experimentId);
		}
	}



	@Override
	public void stateExperimentEnds(String experimentId) {
		for (Renderer renderer : renderers) {
			renderer.stateExperimentEnds(experimentId);
		}
	}
}
