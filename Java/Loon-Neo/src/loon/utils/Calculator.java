package loon.utils;

public class Calculator {

	private final static int ADD = 0, SUBTRACT = 1, MULTIPLY = 2, DIVIDE = 3;

	private float currentTotal;

	public Calculator() {
		this(0);
	}

	public Calculator(float number) {
		this.currentTotal = number;
	}

	public Calculator add(String number) {
		return convertToDouble(number, ADD);
	}

	public Calculator sub(String number) {
		return convertToDouble(number, SUBTRACT);
	}

	public Calculator mul(String number) {
		return convertToDouble(number, MULTIPLY);
	}

	public Calculator div(String number) {
		return convertToDouble(number, DIVIDE);
	}

	private Calculator convertToDouble(String number, int operator) {
		float dblNumber = Float.valueOf(number);
		switch (operator) {
		case ADD:
			return add(dblNumber);
		case SUBTRACT:
			return sub(dblNumber);
		case MULTIPLY:
			return mul(dblNumber);
		case DIVIDE:
			return div(dblNumber);
		default:
			break;
		}
		return this;
	}

	public Calculator add(float number) {
		currentTotal += number % 1.0 == 0 ? (int) number : number;
		return this;
	}

	public Calculator sub(float number) {
		currentTotal -= number % 1.0 == 0 ? (int) number : number;
		return this;
	}

	public Calculator mul(float number) {
		currentTotal *= number % 1.0 == 0 ? (int) number : number;
		return this;
	}

	public Calculator div(float number) {
		currentTotal /= number % 1.0 == 0 ? (int) number : number;
		return this;
	}

	public int getInt() {
		return (int) currentTotal;
	}

	public float getFloat() {
		return currentTotal;
	}

	@Override
	public String toString() {
		return currentTotal % 1f == 0 ? Integer.toString(getInt()) : String.valueOf(currentTotal);
	}

	public Calculator equal(String number) {
		currentTotal = Float.valueOf(number);
		return this;
	}

}
