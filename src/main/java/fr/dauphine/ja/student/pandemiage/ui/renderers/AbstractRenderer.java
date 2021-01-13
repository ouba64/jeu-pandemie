package fr.dauphine.ja.student.pandemiage.ui.renderers;

import java.util.List;

import fr.dauphine.ja.pandemiage.common.Disease;
import fr.dauphine.ja.pandemiage.common.PlayerInterface;
import fr.dauphine.ja.student.pandemiage.ai.actions.Action;
import fr.dauphine.ja.student.pandemiage.common.DiseaseState;
import fr.dauphine.ja.student.pandemiage.common.GameOverException;
import fr.dauphine.ja.student.pandemiage.game.CityContainer;
import fr.dauphine.ja.student.pandemiage.game.cards.PlayerCard;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngine;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngineForSimulation;
import fr.dauphine.ja.student.pandemiage.markers.DiseaseCube;

public abstract class AbstractRenderer implements Renderer {
	GameEngineForSimulation ge;
	
	
	
	public AbstractRenderer(GameEngineForSimulation gameEngineForSimulation) {
		super();
		this.ge = gameEngineForSimulation;
	}



	public GameEngineForSimulation getGe() {
		return ge;
	}



	public void setGe(GameEngineForSimulation ge) {
		this.ge = ge;
	}



	
	
}
