package fr.dauphine.ja.student.pandemiage.game.cards;

import fr.dauphine.ja.pandemiage.common.Disease;

public class CityCard extends CardWithCity implements PlayerCard {
	int r;
	int g;
	int b;
	float x;
	float y;
	float size;
	double eigencentrality;
	int degree;
	
	
	public static int counter = 1;
	public CityCard() {
		//System.out.println("===============================" + counter++);
	}

	public void setLabel(String label) {
		this.cityName = label;
	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}

	public int getG() {
		return g;
	}

	public void setG(int g) {
		this.g = g;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public double getEigencentrality() {
		return eigencentrality;
	}

	public void setEigencentrality(double eigencentrality) {
		this.eigencentrality = eigencentrality;
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}


	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	@Override
	public String toString() {
		return "cityCard[" + cityName + "-" + disease.name()+ "]";
	}
}
