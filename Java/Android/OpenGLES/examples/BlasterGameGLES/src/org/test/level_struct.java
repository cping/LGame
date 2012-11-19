package org.test;

public final class level_struct
{
	public int y_scroll;
	public int y;
	public int frame;
	public int high_score;
	public int high_score_difficulty;
	public int high_score_reached;
	public int tile;
	public int bg_tile;
	public String name;
	public int mid_tile;
	public int mid_alpha;
	public int grid_y;
	public int[][] grid;
	public int stars;
	public int[] star_score;
	public int[] star_reward;
	public int locked;
	public String music;
	public int won;
	public int pause;
	public int waves;
	public float wave_total;
	public int button;

	public level_struct clone()
	{
		level_struct varCopy = new level_struct();

		varCopy.y_scroll = this.y_scroll;
		varCopy.y = this.y;
		varCopy.frame = this.frame;
		varCopy.high_score = this.high_score;
		varCopy.high_score_difficulty = this.high_score_difficulty;
		varCopy.high_score_reached = this.high_score_reached;
		varCopy.tile = this.tile;
		varCopy.bg_tile = this.bg_tile;
		varCopy.name = this.name;
		varCopy.mid_tile = this.mid_tile;
		varCopy.mid_alpha = this.mid_alpha;
		varCopy.grid_y = this.grid_y;
		varCopy.grid = this.grid;
		varCopy.stars = this.stars;
		varCopy.star_score = this.star_score;
		varCopy.star_reward = this.star_reward;
		varCopy.locked = this.locked;
		varCopy.music = this.music;
		varCopy.won = this.won;
		varCopy.pause = this.pause;
		varCopy.waves = this.waves;
		varCopy.wave_total = this.wave_total;
		varCopy.button = this.button;

		return varCopy;
	}
}