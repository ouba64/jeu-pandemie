package fr.dauphine.ja.student.pandemiage.common;

/**
 * We choose an Exception to state a victory because this mechanism suits our need: it exit the execution of the
 * application and brings us right were we want.
 * It is better than a return that will bring us to the callee of the function.
 * And it is better than the exit instruction which exits us from the application.
 *
 */
public class GameWonException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GameWonException(String message) {
		super(message);

	}



}
