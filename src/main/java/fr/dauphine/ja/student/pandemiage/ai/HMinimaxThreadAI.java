package fr.dauphine.ja.student.pandemiage.ai;

/**
 * 
 * 
 * @author Ouba
 *
 */
public class HMinimaxThreadAI extends ThreadAI {
	/*
	 * enum AiAction { PLAY_TURN, DISCARD } AiAction action;
	 */

	private int maxHandSize;
	private int nbEpidemicCards;

	public HMinimaxThreadAI() {
		super();
	}

	@Override
	public SimulationAi duplicate() {
		HMinimaxAI ai = new HMinimaxAI();
		return ai;
	}

}
