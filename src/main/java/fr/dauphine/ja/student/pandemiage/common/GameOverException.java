package fr.dauphine.ja.student.pandemiage.common;

import fr.dauphine.ja.pandemiage.common.DefeatReason;
import fr.dauphine.ja.student.pandemiage.gameengine.GameEngineForSimulation;

public class GameOverException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DefeatReason defeatReason;

	public GameOverException(DefeatReason defeatReason, String message, GameEngineForSimulation ge) {
		super(message);
		this.defeatReason = defeatReason;
		ge.setGoe(this);
	}

	public DefeatReason getDefeatReason() {
		return defeatReason;
	}

	public void setDefeatReason(DefeatReason defeatReason) {
		this.defeatReason = defeatReason;
	}

}
