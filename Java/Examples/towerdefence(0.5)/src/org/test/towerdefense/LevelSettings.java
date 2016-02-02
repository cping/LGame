package org.test.towerdefense;

import java.util.ArrayList;
import java.util.HashMap;

import loon.font.LFont;
import loon.geom.Vector2f;

public class LevelSettings {

	public LevelSettings(MainGame game, int level) {

		this.setTowerBlockingGridCells(new java.util.ArrayList<Vector2f>());
		this.setLevelSpecificOccupiedGridCells(new java.util.ArrayList<Vector2f>());
		HashMap<Vector2f, String> textAndRelativePosition = new HashMap<Vector2f, String>();
		LFont font = LFont.getFont(10);
		switch (level) {
		case 1:
			this.setStartPoint(new Vector2f(0, 9));
			this.setEndPoint(new Vector2f(0x11, 9));
			this.setBackgroundTextureFile("assets/background.png");
			this.setBackgroundWithGridTextureFile("assets/background_grid.png");
			return;

		case 2: {
			this.setStartPoint(new Vector2f(0, 15));
			this.setEndPoint(new Vector2f(0x11, 15));
			this.setBackgroundTextureFile("assets/background2.png");
			this.setBackgroundWithGridTextureFile("assets/background2_grid.png");
			this.getLevelSpecificOccupiedGridCells().add(new Vector2f(9, 4));
			this.getLevelSpecificOccupiedGridCells().add(new Vector2f(6, 11));
			textAndRelativePosition.put(new Vector2f(71f, 33f),
					LanguageResources.getLakeHeader().toUpperCase());
			int num = 0x2e;
			for (String str : LanguageResources.getLakeInfo().split("[$]", -1)) {
				textAndRelativePosition
						.put(new Vector2f(28f, (float) num), str);
				num += 14;
			}
			this.setInfoSpriteWithText(new SpriteWithText(game,
					"assets/speechbubble.png", 0x2ee0, new Vector2f(96f, 138f),
					textAndRelativePosition, font));
			return;
		}
		case 3: {
			this.setStartPoint(new Vector2f(0, 15));
			this.setEndPoint(new Vector2f(0x11, 4));
			this.setBackgroundTextureFile("assets/background3.png");
			this.setBackgroundWithGridTextureFile("assets/background3_grid.png");
			this.getLevelSpecificOccupiedGridCells().add(new Vector2f(3, 9));
			this.getLevelSpecificOccupiedGridCells().add(new Vector2f(7, 15));
			this.getTowerBlockingGridCells().add(new Vector2f(7, 7));
			this.getTowerBlockingGridCells().add(new Vector2f(7, 8));
			this.getTowerBlockingGridCells().add(new Vector2f(3, 13));
			this.getTowerBlockingGridCells().add(new Vector2f(3, 14));
			this.getTowerBlockingGridCells().add(new Vector2f(4, 13));
			this.getTowerBlockingGridCells().add(new Vector2f(4, 14));
			textAndRelativePosition.put(
					new Vector2f(103f - (font.stringWidth(LanguageResources
							.getBlocking().toUpperCase()) / 2f), 16f),
					LanguageResources.getMudHeader().toUpperCase());
			int num2 = 30;
			for (String str2 : LanguageResources.getMudInfo().split("[$]", -1)) {
				textAndRelativePosition.put(new Vector2f(34f, (float) num2),
						str2);
				num2 += 14;
			}
			this.setInfoSpriteWithText(new SpriteWithText(game,
					"assets/speechbubble2.png", 0x2ee0, new Vector2f(59f, 54f),
					textAndRelativePosition, font));
			return;
		}
		}
	}

	private String privateBackgroundTextureFile;

	public final String getBackgroundTextureFile() {
		return privateBackgroundTextureFile;
	}

	public final void setBackgroundTextureFile(String value) {
		privateBackgroundTextureFile = value;
	}

	private String privateBackgroundWithGridTextureFile;

	public final String getBackgroundWithGridTextureFile() {
		return privateBackgroundWithGridTextureFile;
	}

	public final void setBackgroundWithGridTextureFile(String value) {
		privateBackgroundWithGridTextureFile = value;
	}

	private Vector2f privateEndPoint;

	public final Vector2f getEndPoint() {
		return privateEndPoint;
	}

	public final void setEndPoint(Vector2f value) {
		privateEndPoint = value;
	}

	private SpriteWithText privateInfoSpriteWithText;

	public final SpriteWithText getInfoSpriteWithText() {
		return privateInfoSpriteWithText;
	}

	public final void setInfoSpriteWithText(SpriteWithText value) {
		privateInfoSpriteWithText = value;
	}

	private java.util.ArrayList<Vector2f> privateLevelSpecificOccupiedGridCells = new ArrayList<Vector2f>();;

	public final java.util.ArrayList<Vector2f> getLevelSpecificOccupiedGridCells() {
		return privateLevelSpecificOccupiedGridCells;
	}

	public final void setLevelSpecificOccupiedGridCells(
			java.util.ArrayList<Vector2f> value) {
		privateLevelSpecificOccupiedGridCells = value;
	}

	private Vector2f privateStartPoint;

	public final Vector2f getStartPoint() {
		return privateStartPoint;
	}

	public final void setStartPoint(Vector2f value) {
		privateStartPoint = value;
	}

	private java.util.ArrayList<Vector2f> privateTowerBlockingGridCells = new ArrayList<Vector2f>();;

	public final java.util.ArrayList<Vector2f> getTowerBlockingGridCells() {
		return privateTowerBlockingGridCells;
	}

	public final void setTowerBlockingGridCells(
			java.util.ArrayList<Vector2f> value) {
		privateTowerBlockingGridCells = value;
	}
}