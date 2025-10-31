package org.test.rtsgame;

import java.util.ArrayList;

public class Levels {

	public static class LevelDetails {
		public ArrayList<Integer> ArmyNumber;
		public int GameMode;

	}

	public static LevelDetails Load(int index) {
		LevelDetails lev = new LevelDetails();
		lev.ArmyNumber = new ArrayList<Integer>();
		// ArmyNumber保存了敌人数量，添加的index与兵种对应
		switch (index) {
		case 0:
			lev.ArmyNumber.add(1);
			break;
		case 1:
			lev.ArmyNumber.add(4);
			break;
		case 2:
			lev.ArmyNumber.add(2);
			lev.ArmyNumber.add(4);
			break;
		case 3:
			lev.ArmyNumber.add(4);
			lev.ArmyNumber.add(4);
			break;
		case 4:
			lev.ArmyNumber.add(8);
			break;
		case 5:
			lev.ArmyNumber.add(2);
			lev.ArmyNumber.add(0);
			lev.ArmyNumber.add(2);
			break;
		case 6:
			lev.ArmyNumber.add(0);
			lev.ArmyNumber.add(4);
			lev.ArmyNumber.add(4);
			break;
		case 7:
			lev.ArmyNumber.add(0);
			lev.ArmyNumber.add(0);
			lev.ArmyNumber.add(6);
			break;
		case 8:
			lev.ArmyNumber.add(10);
			lev.ArmyNumber.add(0);
			lev.ArmyNumber.add(0);
			break;
		case 9:
			lev.ArmyNumber.add(5);
			lev.ArmyNumber.add(5);
			lev.ArmyNumber.add(0);
			break;
		case 10:
			lev.ArmyNumber.add(0);
			lev.ArmyNumber.add(6);
			lev.ArmyNumber.add(0);
			break;
		case 11:
			lev.ArmyNumber.add(3);
			lev.ArmyNumber.add(3);
			lev.ArmyNumber.add(3);
			break;
		case 12:
			lev.ArmyNumber.add(0);
			lev.ArmyNumber.add(4);
			lev.ArmyNumber.add(8);
			break;
		case 13:
			lev.ArmyNumber.add(0);
			lev.ArmyNumber.add(6);
			lev.ArmyNumber.add(6);
			break;
		case 14:
			lev.ArmyNumber.add(4);
			lev.ArmyNumber.add(4);
			lev.ArmyNumber.add(4);
			break;
		default:
			lev.ArmyNumber.add(1);
			break;
		}
		if (index < 5) {
			lev.GameMode = 0;
		} else if (index < 10) {
			lev.GameMode = 1;
		} else {
			lev.GameMode = 2;
		}
		return lev;
	}

}
