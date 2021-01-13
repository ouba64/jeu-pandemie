package fr.dauphine.ja.student.pandemiage.markers;

import fr.dauphine.ja.pandemiage.common.Disease;

/**
 * Disease cube that is placed on the board.
 * 
 * @author Yousra
 *
 */
public class DiseaseCube implements Marker {
	Disease disease;

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	@Override
	public String toString() {
		return "diseaseCube[" + disease.name() + "]";
	}

}
