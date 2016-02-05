package org.test;


public class CLevel
{
	public String backGround;
	public String filename;
	public int highscore;
	public int[][] levelArray;
	public boolean locked;
	public int maxWave;
	public int maxWaves;
	public boolean saved;
	public CWaypoints waypoints;

	public CLevel(CWaypoints waypoints, int[][] levelArray, String backGround, int maxWaves, String filename)
	{
		this.waypoints = waypoints;
		this.levelArray = levelArray;
		this.backGround = backGround;
		this.maxWaves = maxWaves;
		this.locked = true;
		this.saved = false;
		this.filename = filename;
	}
}