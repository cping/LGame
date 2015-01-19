package loon;

public enum GameType {

	UNKOWN("Unkown Game"),
	ACT("Action Game"),
	STG("Shooting Game"),
	FTG("Fighting Game"),
	AVG("Adventure Game"),
	SLG("Simulation Game"),
	RPG("Role-playing game"),
	STRATEGY("Strategy Game"),
	SPORT("Sports Game"),
	RACING("Racing Game"),
	CASUAL("Casual Game"),
	MUSIC("Music Game"),
	MMOG("Multiplayer Online Game");
	
	private String name;

	GameType(String name) {
		this.name = name;
	}

	public static GameType getEnum(int idx) {
		if (idx < values().length) {
			return values()[idx];
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		return name;
	}
}
