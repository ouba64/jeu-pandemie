package fr.dauphine.ja.student.pandemiage.ai;

import java.util.List;
import java.util.Random;

import fr.dauphine.ja.student.pandemiage.ai.actions.Action;
import fr.dauphine.ja.student.pandemiage.common.ActionId;

/**
 * A pseudo-AI that randomly chooses its action
 * 
 * @author Ouba
 *
 */
public class RandomAi extends ThreadAI {
	/*
	 * enum AiAction { PLAY_TURN, DISCARD } AiAction action;
	 */

//	private int maxHandSize;
//	private int nbEpidemicCards;

	public RandomAi() {
		super();
	}


	/**
	 * Discard at random
	 */
	@Override
	public List<Integer> chooseBestDiscardAction(List<List<Integer>> discardActions) {
		int pos  = random.nextInt(discardActions.size());
		return discardActions.get(pos);
	}



	public int nextInt(int lowerBound, int upperBound) {
		int randomNumber = random.nextInt(upperBound - lowerBound) + lowerBound;
		return randomNumber;
	}

	public static Integer[] chooseAction(List<Action>[] actionss, Random random) {
		List<Action> actions;
		int actionId;
		int i;
		// choose discover a cure in priority
		if(actionss[ActionId.DISCOVER_A_CURE.ordinal()].size()>0) {
			return new Integer[] { ActionId.DISCOVER_A_CURE.ordinal(), 0 };
		}
		// no discover a cure, randomly choose an action
		do {
			// choose an action type
			actionId = random.nextInt(ActionId.values().length);
		}
		while (actionss[actionId].size() == 0);
		actions = actionss[actionId];
		// choose an action of the previously chosen type
		i = random.nextInt(actions.size());
		return new Integer[] { actionId, i };
	}

	@Override
	public Integer[] chooseBestAction(List<Action>[] actionss) {
		return chooseAction(actionss, random);
	}

	@Override
	public SimulationAi duplicate() {
		return null;
	}

}
