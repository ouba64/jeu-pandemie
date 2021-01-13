package fr.dauphine.ja.student.pandemiage.ai.actions;

import java.util.List;

import fr.dauphine.ja.pandemiage.common.Disease;
import fr.dauphine.ja.pandemiage.common.UnauthorizedActionException;
import fr.dauphine.ja.student.pandemiage.game.CityContainer;
import fr.dauphine.ja.student.pandemiage.game.Player;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngineForSimulation;
import fr.dauphine.ja.student.pandemiage.markers.DiseaseCube;

public class TreatDiseaseAction extends Action {
	List<DiseaseCube> diseaseCubes;
	Disease disease;

	public TreatDiseaseAction(GameEngineForSimulation ge, Player p, List<DiseaseCube> diseaseCubes, Disease disease) {
		super(ge, p);
		this.diseaseCubes = diseaseCubes;
		this.disease = disease;
	}

	@Override
	public void execute() throws UnauthorizedActionException {
		DiseaseCube diseaseCube = diseaseCubes.get(0);
		p.treatDisease(diseaseCube.getDisease());
	}
	
	

	public List<DiseaseCube> getDiseaseCubes() {
		return diseaseCubes;
	}

	public void setDiseaseCubes(List<DiseaseCube> diseaseCubes) {
		this.diseaseCubes = diseaseCubes;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	@Override
	public String toString() {
		return "Treat " + diseaseCubes.get(0).getDisease().name() + " disease";
	}

	@Override
	public Action getNewAction() {
		return new TreatDiseaseAction(null, null, null, disease);
	
	}
	
	@Override
	public Action duplicate(GameEngineForSimulation geNew) {
		TreatDiseaseAction action = new TreatDiseaseAction(geNew, null, null, disease);
		action.setP((Player) geNew.getPlayers().get(geNew.getCurrentPlayer()));
		CityContainer cityContainer = geNew.getBoard().getCityContainers().get(p.playerLocation());
		action.setDiseaseCubes(cityContainer.getDiseaseCubes(disease));
		this.ge= geNew;
		return action;
	}
}
