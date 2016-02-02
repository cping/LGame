package org.test.towerdefense;

import loon.action.sprite.SpriteBatch;
import loon.canvas.LColor;
import loon.font.LFont;
import loon.geom.Vector2f;
import loon.utils.MathUtils;

public class Utils {
	public static Vector2f ConvertToGridPoint(Vector2f positionCoordinates) {
		return new Vector2f(((int) ((positionCoordinates.x - -20f) / 20f)) - 1,
				((int) ((positionCoordinates.y - 40f) / 20f)) - 1);
	}

	public static Vector2f ConvertToPositionCoordinates(Vector2f gridPoint) {
		return new Vector2f((float) ((gridPoint.x * 20) + -20),
				(float) ((gridPoint.y * 20) + 40));
	}

	public static void DrawLevelText(SpriteBatch spriteBatch, LFont font,
			String text, boolean locked, Vector2f position) {
		LColor white = LColor.white;
		if (locked) {
			white = LColor.gray;
			DrawStringAlignCenter(spriteBatch, font,
					LanguageResources.getLocked(), position.add(0f, 14f),
					LColor.red);
		} else {
			DrawStringAlignCenter(spriteBatch, font,
					LanguageResources.getUnlocked(), position.add(0f, 14f),
					new LColor(0f, 1f, 0f, 1f));
		}
		DrawStringAlignCenter(spriteBatch, font, text.toUpperCase(), position,
				white);
	}

	private static Vector2f pos = new Vector2f();

	public static void DrawStringAlignCenter(SpriteBatch spriteBatch,
			LFont font, String text, float x, float y, LColor color) {
		pos.set(x - (font.stringWidth(text) / 2f), y);
		spriteBatch.drawString(font, text, pos, color);
	}

	public static void DrawStringAlignCenter(SpriteBatch spriteBatch,
			LFont font, String text, Vector2f position, LColor color) {
		spriteBatch.drawString(font, text,
				new Vector2f(position.x - (font.stringWidth(text) / 2f),
						position.y), color);
	}

	public static void DrawStringAlignCenter(SpriteBatch spriteBatch,
			LFont font, String text, Vector2f position, LColor color,
			float scale) {
		spriteBatch.drawString(font, text,
				new Vector2f(position.x - (font.stringWidth(text) / 2f),
						position.y), color, 0f, new Vector2f(0f, 0f), scale);
	}

	public static void DrawStringAlignLeft(SpriteBatch spriteBatch, LFont font,
			String text, float x, float y, LColor color) {
		pos.set(x, y);
		spriteBatch.drawString(font, text, pos, color);
	}

	public static void DrawStringAlignLeft(SpriteBatch spriteBatch, LFont font,
			String text, Vector2f position, LColor color) {
		spriteBatch.drawString(font, text,
				new Vector2f(position.x, position.y), color);
	}

	public static void DrawStringAlignRight(SpriteBatch spriteBatch,
			LFont font, String text, Vector2f position, LColor color) {
		spriteBatch.drawString(font, text,
				new Vector2f(position.x - font.stringWidth(text), position.y),
				color);
	}

	public static void DrawStringAlignRight(SpriteBatch spriteBatch,
			LFont font, String text, float x, float y, LColor color) {
		pos.set(x - font.stringWidth(text), y);
		spriteBatch.drawString(font, text, pos, color);
	}

	public static float GetAngle(Vector2f v1) {
		v1.nor();
		return (float) Math.atan2((double) v1.y, (double) v1.x);
	}

	public static Vector2f GetDirection(Vector2f v1, Vector2f v2) {
		Vector2f vector = v2.sub(v1);
		vector.normalize();
		return vector;
	}

	public static float GetDistance(Vector2f v1, Vector2f v2) {
		float num = v1.x - v2.x;
		float num2 = v1.y - v2.y;
		return (float) Math.sqrt((double) ((num * num) + (num2 * num2)));
	}

	public static int GetTextureOffsetY(float angleInRadians, int spriteHeight) {
		float num = MathUtils.wrapAngle(angleInRadians + 1.570796f);
		if ((num >= -0.3926991f) && (num <= 0.3926991f)) {
			return 0;
		}
		if ((num >= -1.963495f) && (num <= -1.178097f)) {
			return (6 * spriteHeight);
		}
		if ((num <= -2.748894f) || (num >= 2.748894f)) {
			return (4 * spriteHeight);
		}
		if ((num >= 1.178097f) && (num <= 1.963495f)) {
			return (2 * spriteHeight);
		}
		if ((num >= -2.748894f) && (num <= -1.963495f)) {
			return (5 * spriteHeight);
		}
		if ((num >= 0.3926991f) && (num <= 1.178097f)) {
			return spriteHeight;
		}
		if ((num >= 1.963495f) && (num <= 2.748894f)) {
			return (3 * spriteHeight);
		}
		return (7 * spriteHeight);
	}

}