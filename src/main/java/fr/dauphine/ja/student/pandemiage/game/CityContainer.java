package fr.dauphine.ja.student.pandemiage.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.dauphine.ja.pandemiage.common.Disease;
import fr.dauphine.ja.student.pandemiage.game.cards.CityCard;
import fr.dauphine.ja.student.pandemiage.markers.DiseaseCube;
import fr.dauphine.ja.student.pandemiage.markers.ResearchStation;

/**
 * The CityContainer contains markers (player, diseases, etc.) for a city on the
 * board.
 * 
 * @author Ouba
 *
 */
public class CityContainer {
	CityCard cityCard;
	List<Player> players;
	List<DiseaseCube>[] diseaseCubes;
	List<ResearchStation> researchStations;

	public CityContainer() {
		players = new ArrayList<>();
		diseaseCubes = new ArrayList[Disease.values().length];
		for(int i=0; i<Disease.values().length; i++) {
			diseaseCubes[i] = new ArrayList<>();
		}
	}



	public List<Player> getPlayers() {
		return players;
	}



	public List<DiseaseCube>[] getDiseaseCubes() {
		return diseaseCubes;
	}



	public List<ResearchStation> getResearchStations() {
		return researchStations;
	}

	/**
	 * Place 1 cube of disease in a city
	 * @param disease
	 * @param diseaseCube
	 */
	public void addDiseaseCube(Disease disease, DiseaseCube diseaseCube) {
		diseaseCubes[disease.ordinal()].add(diseaseCube);
	}
	
	/**
	 * Remove 1 cube of disease from a city
	 * @param disease
	 * @return
	 */
	public DiseaseCube removeDiseaseCube(Disease disease) {
		DiseaseCube diseaseCube = diseaseCubes[disease.ordinal()].remove(diseaseCubes[disease.ordinal()].size()-1);
		return diseaseCube;
	}

	public List<DiseaseCube> getDiseaseCubes(Disease disease){
		return diseaseCubes[disease.ordinal()];
	}
	
	public CityCard getCityCard() {
		return cityCard;
	}

	public void setCityCard(CityCard cityCard) {
		this.cityCard = cityCard;
	}

	@Override
	public String toString() {
		return "CityContainer [cityCard=" + cityCard + ", players=" + players + ", diseaseCubes="
				+ Arrays.toString(diseaseCubes) + "]";
	}
	
	

	public void setPlayers(List<Player> players) {
		this.players = players;
	}



	public void setDiseaseCubes(List<DiseaseCube>[] diseaseCubes) {
		this.diseaseCubes = diseaseCubes;
	}



	@Override
	protected Object clone()  {
		CityContainer cityContainer = new CityContainer();
		/*CityCard cityCard;
		List<Player> players;
		List<DiseaseCube>[] diseaseCubes;
		List<ResearchStation> researchStations;*/
		cityContainer.setCityCard(cityCard);
		cityContainer.setPlayers((List<Player>) ((ArrayList<Player>)players).clone());
		List<DiseaseCube>[] diseaseCubes = new ArrayList[this.diseaseCubes.length];
		cityContainer.setDiseaseCubes(diseaseCubes);
		List<DiseaseCube> ds;
		// 
		for (int i = 0; i < this.diseaseCubes.length; i++) {
			List<DiseaseCube> dcs = this.diseaseCubes[i];
			ds = (List<DiseaseCube>) ((ArrayList<DiseaseCube>)dcs).clone();
			diseaseCubes[i] = ds;
		}

		return cityContainer;
	}
}
