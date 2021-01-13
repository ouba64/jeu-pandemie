package fr.dauphine.ja.student.pandemiage.game.cards;

public class InfectionCard extends CardWithCity {

	public InfectionCard(CityCard cityCard) {
		super();
		this.disease = cityCard.getDisease();
		this.cityName = cityCard.getCityName();
	}

	@Override
	public String toString() {
		return "InfectionCard["  + cityName +  "][" + disease.name() + "]";
	}
	
	
}
