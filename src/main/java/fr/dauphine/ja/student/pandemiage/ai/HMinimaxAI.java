package fr.dauphine.ja.student.pandemiage.ai;

public class HMinimaxAI extends SimulationAi {

	@Override
	public SimulationAi duplicate() {
		HMinimaxAI ai = new HMinimaxAI();
		ai.setGe(this.ge);
		return ai;
	}

}
