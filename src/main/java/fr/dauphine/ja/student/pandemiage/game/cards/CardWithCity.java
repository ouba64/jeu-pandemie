package fr.dauphine.ja.student.pandemiage.game.cards;

import fr.dauphine.ja.pandemiage.common.Disease;
import fr.dauphine.ja.pandemiage.common.PlayerCardInterface;

public abstract class CardWithCity implements Card, PlayerCardInterface {
	Disease disease;
	String cityName;

	@Override
	public String getCityName() {
		return cityName;
	}

	@Override
	public Disease getDisease() {
		return disease;
	}
}
