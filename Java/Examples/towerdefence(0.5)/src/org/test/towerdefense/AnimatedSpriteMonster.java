package org.test.towerdefense;

import loon.geom.Vector2f;
import loon.utils.reply.ObjRef;

public class AnimatedSpriteMonster {

	public static java.util.ArrayList<AnimatedSprite> GetAllAnimatedSpriteMonsters(MainGame game) {

		java.util.ArrayList<AnimatedSprite> list = new java.util.ArrayList<AnimatedSprite>();
		int num = 8;
		int num2 = 0xe8;
		int spriteWidth = 80;
		int spriteHeight = 80;

		ObjRef<Integer> numValue = new ObjRef<Integer>(0);

		AnimatedSprite item = new AnimatedSprite(game, GetTextureFile(MonsterType.Peasant, "png/", numValue),
				new Vector2f((float) num, 8f), 6, numValue.get(), spriteWidth, spriteHeight, 1f);
		item.setAnimationSpeedRatio(3);
		list.add(item);
		numValue.set(0);

		AnimatedSprite sprite2 = new AnimatedSprite(game, GetTextureFile(MonsterType.Peon, "png/", numValue),
				new Vector2f(num2, 72f), 6, numValue.get(), spriteWidth, spriteHeight, 1f);
		sprite2.setAnimationSpeedRatio(3);
		list.add(sprite2);
		numValue.set(0);

		AnimatedSprite sprite3 = new AnimatedSprite(game, GetTextureFile(MonsterType.Berserker, "png/", numValue),
				new Vector2f((float) num, 132f), 6, numValue.get(), spriteWidth, spriteHeight, 1f);
		sprite3.setAnimationSpeedRatio(3);
		list.add(sprite3);
		numValue.set(0);

		AnimatedSprite sprite4 = new AnimatedSprite(game, GetTextureFile(MonsterType.Chicken, "png/", numValue),
				new Vector2f((float) num2, 200f), 6, numValue.get(), spriteWidth, spriteHeight, 1f);
		sprite4.setAnimationSpeedRatio(3);
		list.add(sprite4);
		numValue.set(0);

		AnimatedSprite sprite5 = new AnimatedSprite(game, GetTextureFile(MonsterType.Doctor, "png/", numValue),
				new Vector2f((float) num, 258f), 6, numValue.get(), spriteWidth, spriteHeight, 1f);
		sprite5.setAnimationSpeedRatio(3);
		list.add(sprite5);
		numValue.set(0);

		AnimatedSprite sprite6 = new AnimatedSprite(game, GetTextureFile(MonsterType.Chieftain, "png/", numValue),
				new Vector2f((float) num2, 322f), 6, numValue.get(), spriteWidth, spriteHeight, 1f);
		sprite6.setAnimationSpeedRatio(3);
		list.add(sprite6);
		numValue.set(0);

		return list;
	}

	public static AnimatedSprite GetAnimatedSpriteMonsterForMonsterToolbar(MainGame game,
			Vector2f monsterToolbarDrawPosition, MonsterType monsterType) {
		ObjRef<Integer> tempRef_num = new ObjRef<Integer>(0);
		AnimatedSprite tempVar = new AnimatedSprite(game, GetTextureFile(monsterType, "png/", tempRef_num),
				monsterToolbarDrawPosition.add(-2f, -34f), 6, tempRef_num.get(), 80, 80, 1f);
		return tempVar;
	}

	public static AnimatedSprite GetSmallAnimatedSpriteMonster(MainGame game, MonsterType monsterType) {
		ObjRef<Integer> tempRef_num = new ObjRef<Integer>(0);
		AnimatedSprite tempVar = new AnimatedSprite(game, GetTextureFile(monsterType, "", tempRef_num),
				new Vector2f(200f, -4f), 12, tempRef_num.get(), 80, 80, 0.5f);
		return tempVar;
	}

	private static String GetTextureFile(MonsterType monsterType, String subDir, ObjRef<Integer> spriteCount) {
		String str = "";
		spriteCount.set(0);
		switch (monsterType) {
		case Peasant:
			str = "monsterinfo1";
			spriteCount.set(0x10);
			break;

		case Peon:
			str = "monsterinfo2";
			spriteCount.set(15);
			break;

		case Berserker:
			str = "monsterinfo3";
			spriteCount.set(0x15);
			break;

		case Chicken:
			str = "monsterinfo4";
			spriteCount.set(0x22);
			break;

		case Doctor:
			str = "monsterinfo5";
			spriteCount.set(0x19);
			break;

		case Chieftain:
			str = "monsterinfo6";
			spriteCount.set(0x1b);
			break;
		}
		return ("assets/" + subDir + str + ".png");
	}
}