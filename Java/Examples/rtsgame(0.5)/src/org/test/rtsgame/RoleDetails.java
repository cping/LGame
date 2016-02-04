package org.test.rtsgame;

import java.util.HashMap;

public class RoleDetails {
	public float Damage;
	public float Health;
	public float MoveSpeed;
	public float Range;
	public float ReloadTime;
	public String Summary;

	private static HashMap<RoleRank, RoleDetails> armys = new HashMap<RoleRank, RoleDetails>(
			10);

	static {

		RoleDetails archer = new RoleDetails();

		archer.Damage = 0.1f;
		archer.Health = 1;
		archer.ReloadTime = 0.5f;
		archer.Range = 135;
		archer.MoveSpeed = 1;

		RoleDetails bazooka = new RoleDetails();

		bazooka.Damage = 0.5f;
		bazooka.Health = 2;
		bazooka.ReloadTime = 2f;
		bazooka.Range = 195;
		bazooka.MoveSpeed = 0.25f;

		RoleDetails ninja = new RoleDetails();

		ninja.Damage = 0.1f;
		ninja.Health = 1f;
		ninja.ReloadTime = 0.25f;
		ninja.Range = 75;
		ninja.MoveSpeed = 1f;

		armys.put(RoleRank.archer, archer);
		armys.put(RoleRank.bazooka, bazooka);
		armys.put(RoleRank.bazooka, ninja);
	}

	public static RoleDetails Load(RoleRank name) {
		return armys.get(name);
	}
}
