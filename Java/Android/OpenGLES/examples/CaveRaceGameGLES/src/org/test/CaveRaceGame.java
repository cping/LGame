package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.LSystem;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.input.LInputFactory.Key;
import loon.core.input.LInputFactory.Touch;
import loon.core.timer.GameTime;

public class CaveRaceGame 
{
	private LTexture backgroundTexture;
	private java.util.ArrayList<Bomb> bombs = new java.util.ArrayList<Bomb>();
	private LTexture bombTexture;
	private LTexture desertTexture;
	private java.util.ArrayList<Enemy> enemys = new java.util.ArrayList<Enemy>();
	private LTexture enemyTexture;
	private int fade = 0xff;
	private LTexture forestTexture;
	private int frame;
	private LTexture gamepadTexture;
	private GameStatus gameStatus = GameStatus.values()[0];
	private LTexture introScreenTexture;
	private boolean isLicenseChecked;
	private boolean isTrial;
	private LTexture lavaTexture;
	private String[] levelname = new String[] {"Intro", "Forest 1", "Forest 2", "Forest 3", "Forest 4", "Forest 5", "Desert 1", "Desert 2", "Desert 3", "Desert 4", "Desert 5", "Winter 1", "Winter 2", "Winter 3", "Winter 4", "Winter 5", "Lava 1", "Lava 2", "Lava 3", "Lava 4", "Lava 5", "Lava 6", "Lava 7", "Lava 8", "End"};
	private Map map = new Map(0x19, 13);
	private Player player = new Player();
	private LTexture playerTexture;
	private java.util.Random random = new java.util.Random();
	private boolean showGameOver;
	private boolean showStart;
	private LTexture statusBarTexture;
	private LTexture statusToolsTexture;
	private LTexture stoneTexture;
	private LTexture textTexture;
	private LTexture tresureTexture;
	private LTexture winterTexture;

	public CaveRaceGame()
	{

	}

	private void Blit(SpriteBatch batch,int x, int y, LTexture texture)
	{
		batch.draw(texture,x, y);
	}

	private void BlitSprite(SpriteBatch batch,int x, int y, LTexture texture, int index, int pixels)
	{
		int num = texture.getWidth() / pixels;
		int num2 = (index % num) * pixels;
		int num3 = (index > 0) ? ((index / num) * pixels) : 0;
		batch.draw(texture,  x,  y, num2, num3, pixels, pixels);
	}

	private void BlitSprite(SpriteBatch batch,int x, int y, LTexture texture, int index, int pixels, float scale)
	{
		int num = texture.getWidth() / pixels;
		int num2 = (index % num) * pixels;
		int num3 = (index > 0) ? ((index / num) * pixels) : 0;
		batch.draw(texture,  x, y, num2, num3, pixels, pixels, LColor.white,0f, 0f,0f, scale,scale,SpriteEffects.None );
	}

	private void BlitText(SpriteBatch batch,int index)
	{
		batch.draw(this.textTexture,272f, 176f, 0, index * 0x40, 0x109, 0x40, new LColor(this.fade, this.fade, this.fade, this.fade));
	}

	private void BlitText(SpriteBatch batch,int index, int x, int y)
	{
		batch.draw(this.textTexture, x, y, 0, index * 0x40, 0x109, 0x40);
	}

	private boolean box(int x, int y, int x1, int y1, int x2, int y2)
	{
		return (((x >= x1) && (x <= x2)) && ((y >= y1) && (y <= y2)));
	}

	private void CheckBombHit()
	{
		for (Bomb bomb : this.bombs)
		{
			if (bomb.time == 1)
			{
				for (int i = 1; i <= bomb.power; i++)
				{
					int num2 = bomb.x / 0x20;
					int num3 = bomb.y / 0x20;
					int num4 = num3 - i;
					int num5 = num3 + i;
					int num6 = num2 - i;
					int num7 = num2 + i;
					if (num7 >= this.map.width)
					{
						num7 = this.map.width - 1;
					}
					if (num6 < 0)
					{
						num6 = 0;
					}
					if (num4 < 0)
					{
						num4 = 0;
					}
					if (num5 >= this.map.height)
					{
						num5 = this.map.height - 1;
					}
					if (this.map.stone[num2][num3] < 9)
					{
						this.map.stone[num2][num3] = 0;
					}
					if (this.map.stone[num7][num3] < 9)
					{
						this.map.stone[num7][num3] = 0;
					}
					if (this.map.stone[num6][num3] < 9)
					{
						this.map.stone[num6][num3] = 0;
					}
					if (this.map.stone[num2][num4] < 9)
					{
						this.map.stone[num2][num4] = 0;
					}
					if (this.map.stone[num2][num5] < 9)
					{
						this.map.stone[num2][num5] = 0;
					}
					if (this.map.treasure[num2][num3] > 0)
					{
						this.map.treasure[num2][num3] = 0;
					}
					if (this.map.treasure[num7][num3] > 0)
					{
						this.map.treasure[num7][num3] = 0;
					}
					if (this.map.treasure[num6][num3] > 0)
					{
						this.map.treasure[num6][num3] = 0;
					}
					if (this.map.treasure[num2][num4] > 0)
					{
						this.map.treasure[num2][num4] = 0;
					}
					if (this.map.treasure[num2][num5] > 0)
					{
						this.map.treasure[num2][num5] = 0;
					}
					if (this.map.bomb[num7][num3] > 0)
					{
						for (int k = 0; k < this.bombs.size(); k++)
						{
							if (((this.bombs.get(k).x / 0x20) == num7) && ((this.bombs.get(k).y / 0x20) == num3))
							{
								this.bombs.get(k).time = 1;
							}
						}
					}
					if (this.map.bomb[num6][num3] > 0)
					{
						for (int m = 0; m < this.bombs.size(); m++)
						{
							if (((this.bombs.get(m).x / 0x20) == num6) && ((this.bombs.get(m).y / 0x20) == num3))
							{
								this.bombs.get(m).time = 1;
							}
						}
					}
					if (this.map.bomb[num2][num4] > 0)
					{
						for (int n = 0; n < this.bombs.size(); n++)
						{
							if (((this.bombs.get(n).x / 0x20) == num2) && ((this.bombs.get(n).y / 0x20) == num4))
							{
								this.bombs.get(n).time = 1;
							}
						}
					}
					if (this.map.bomb[num2][num5] > 0)
					{
						for (int num11 = 0; num11 < this.bombs.size(); num11++)
						{
							if (((this.bombs.get(num11).x / 0x20) == num2) && ((this.bombs.get(num11).y / 0x20) == num5))
							{
								this.bombs.get(num11).time = 1;
							}
						}
					}
					java.util.ArrayList<Integer> list = new java.util.ArrayList<Integer>();
					for (int j = 0; j < this.enemys.size(); j++)
					{
						if (this.box(this.enemys.get(j).x, this.enemys.get(j).y, (num7 * 0x20) - 0x10, bomb.y - 0x10, (num7 * 0x20) + 0x10, bomb.y + 0x10))
						{
							
							list.add(j);
							this.player.points += 0x4b;
						}
						if (this.box(this.enemys.get(j).x, this.enemys.get(j).y, (num6 * 0x20) - 0x10, bomb.y - 0x10, (num6 * 0x20) + 0x10, bomb.y + 0x10))
						{
							
							list.add(j);
							this.player.points += 0x4b;
						}
						if (this.box(this.enemys.get(j).x, this.enemys.get(j).y, bomb.x - 0x10, (num4 * 0x20) - 0x10, bomb.x + 0x10, (num4 * 0x20) + 0x10))
						{
							
							list.add(j);
							this.player.points += 0x4b;
						}
						if (this.box(this.enemys.get(j).x, this.enemys.get(j).y, bomb.x - 0x10, (num5 * 0x20) - 0x10, bomb.x + 0x10, (num5 * 0x20) + 0x10))
						{
							
							list.add(j);
							this.player.points += 0x4b;
						}
					}
					for (int num13 : list)
					{
						try
						{
							this.enemys.remove(num13);
							continue;
						}
						catch (RuntimeException e)
						{
							continue;
						}
					}
					if (this.box(this.player.x, this.player.y, (num7 * 0x20) - 0x10, bomb.y - 0x10, (num7 * 0x20) + 0x10, bomb.y + 0x10))
					{
						this.player.energy = 0;
					}
					if (this.box(this.player.x, this.player.y, (num6 * 0x20) - 0x10, bomb.y - 0x10, (num6 * 0x20) + 0x10, bomb.y + 0x10))
					{
						this.player.energy = 0;
					}
					if (this.box(this.player.x, this.player.y, bomb.x - 0x10, (num4 * 0x20) - 0x10, bomb.x + 0x10, (num4 * 0x20) + 0x10))
					{
						this.player.energy = 0;
					}
					if (this.box(this.player.x, this.player.y, bomb.x - 0x10, (num5 * 0x20) - 0x10, bomb.x + 0x10, (num5 * 0x20) + 0x10))
					{
						this.player.energy = 0;
					}
					if (this.box(this.player.x, this.player.y, bomb.x - 0x10, bomb.y - 0x10, bomb.x + 0x10, bomb.y + 0x10))
					{
						this.player.energy = 0;
					}
				}
			}
		}
	}

	private void CheckEnemyHit()
	{
		for (Enemy enemy : this.enemys)
		{
			if ((enemy.x == this.player.x) && (enemy.y == this.player.y))
			{
				this.player.energy -= 2;
				if (this.player.energy < 0)
				{
					this.player.energy = 0;
				}
			}
		}
	}

	private void CheckLevelComplete()
	{
		if (this.enemys.size() <= 0)
		{
			this.player.levelId++;
			if (this.isTrial && (this.player.levelId > 4))
			{
				this.player.levelId = 4;
				this.showGameOver = true;
				return;
			}
			if (this.player.levelId > (this.levelname.length - 1))
			{
				this.player.levelId = this.levelname.length - 1;
				this.showGameOver = true;
				return;
			}
			this.player.points += 100;
			this.GameStart(this.player.levelId);
		}
		if (this.player.energy <= 0)
		{
			this.fade -= 50;
			if (this.fade <= 0)
			{
				this.fade = 0xff;
				this.player.points -= 50;
				this.player.lives--;
				if (this.player.points < 0)
				{
					this.player.points = 0;
				}
				this.GameStart(this.player.levelId);
			}
		}
		if (this.player.lives <= 0)
		{
			this.showGameOver = true;
			this.showStart = false;
			this.gameStatus = GameStatus.Menu;
		}
	}

	private void CheckLicense()
	{
		if (!this.isLicenseChecked)
		{
			this.isTrial = false;
			this.isLicenseChecked = true;
		}
	}

	private void CheckTreasure()
	{
		if (this.map.treasure[this.player.x / 0x20][this.player.y / 0x20] > 0)
		{
			
			this.map.treasure[this.player.x / 0x20][this.player.y / 0x20] = 0;
			this.player.points += 50;
		}
		if (this.map.stone[this.player.x / 0x20][this.player.y / 0x20] == 1)
		{
			
			this.map.stone[this.player.x / 0x20][this.player.y / 0x20] = 0;
			if (++this.player.power > 4)
			{
				this.player.power = 4;
			}
			this.player.points += 50;
		}
		if (this.map.stone[this.player.x / 0x20][this.player.y / 0x20] == 2)
		{
			
			this.map.stone[this.player.x / 0x20][this.player.y / 0x20] = 0;
			if (++this.player.bombs > 4)
			{
				this.player.bombs = 4;
			}
			this.player.points += 50;
		}
		if (this.map.stone[this.player.x / 0x20][this.player.y / 0x20] == 3)
		{
			
			this.map.stone[this.player.x / 0x20][this.player.y / 0x20] = 0;
			this.player.energy = 9;
			this.player.points += 50;
		}
		if (this.map.stone[this.player.x / 0x20][this.player.y / 0x20] == 4)
		{
			
			this.map.stone[this.player.x / 0x20][this.player.y / 0x20] = 0;
			if (++this.player.lives > 4)
			{
				this.player.lives = 4;
			}
			this.player.points += 50;
		}
	}

	public void Draw(SpriteBatch batch,GameTime gameTime)
	{
		switch (this.gameStatus)
		{
			case Menu:
				this.DrawMenu(batch);
				break;

			case Start:
				this.player = new Player();
				this.GameStart(this.player.levelId);
				break;

			case Resume:
				this.GameResume();
				break;

			case Playing:
				this.DrawPlaying(batch);
				break;
		}
		//super.Draw(gameTime);
	}

	private void DrawBombs(SpriteBatch batch)
	{
		for (Bomb bomb : this.bombs)
		{
			if (bomb.time > 1)
			{
				this.BlitSprite(batch,bomb.x, bomb.y, this.bombTexture, 1, 0x20);
				continue;
			}
			if (this.frame < 2)
			{
				this.BlitSprite(batch,bomb.x, bomb.y, this.bombTexture, 2, 0x20);
				for (int j = 1; j <= bomb.power; j++)
				{
					this.BlitSprite(batch,bomb.x - (j * 0x20), bomb.y, this.bombTexture, 4, 0x20);
					this.BlitSprite(batch,bomb.x + (j * 0x20), bomb.y, this.bombTexture, 4, 0x20);
					this.BlitSprite(batch,bomb.x, bomb.y - (j * 0x20), this.bombTexture, 3, 0x20);
					this.BlitSprite(batch,bomb.x, bomb.y + (j * 0x20), this.bombTexture, 3, 0x20);
				}
				continue;
			}
			if (this.frame < 5)
			{
				this.BlitSprite(batch,bomb.x, bomb.y, this.bombTexture, 5, 0x20);
				for (int k = 1; k <= bomb.power; k++)
				{
					this.BlitSprite(batch,bomb.x - (k * 0x20), bomb.y, this.bombTexture, 7, 0x20);
					this.BlitSprite(batch,bomb.x + (k * 0x20), bomb.y, this.bombTexture, 7, 0x20);
					this.BlitSprite(batch,bomb.x, bomb.y - (k * 0x20), this.bombTexture, 6, 0x20);
					this.BlitSprite(batch,bomb.x, bomb.y + (k * 0x20), this.bombTexture, 6, 0x20);
				}
				continue;
			}
			if (this.frame < 8)
			{
				this.BlitSprite(batch,bomb.x, bomb.y, this.bombTexture, 8, 0x20);
				for (int m = 1; m <= bomb.power; m++)
				{
					this.BlitSprite(batch,bomb.x - (m * 0x20), bomb.y, this.bombTexture, 10, 0x20);
					this.BlitSprite(batch,bomb.x + (m * 0x20), bomb.y, this.bombTexture, 10, 0x20);
					this.BlitSprite(batch,bomb.x, bomb.y - (m * 0x20), this.bombTexture, 9, 0x20);
					this.BlitSprite(batch,bomb.x, bomb.y + (m * 0x20), this.bombTexture, 9, 0x20);
				}
				continue;
			}
			if (this.frame < 12)
			{
				this.BlitSprite(batch,bomb.x, bomb.y, this.bombTexture, 5, 0x20);
				for (int n = 1; n <= bomb.power; n++)
				{
					this.BlitSprite(batch,bomb.x - (n * 0x20), bomb.y, this.bombTexture, 7, 0x20);
					this.BlitSprite(batch,bomb.x + (n * 0x20), bomb.y, this.bombTexture, 7, 0x20);
					this.BlitSprite(batch,bomb.x, bomb.y - (n * 0x20), this.bombTexture, 6, 0x20);
					this.BlitSprite(batch,bomb.x, bomb.y + (n * 0x20), this.bombTexture, 6, 0x20);
				}
				continue;
			}
			this.BlitSprite(batch,bomb.x, bomb.y, this.bombTexture, 2, 0x20);
			for (int i = 1; i <= bomb.power; i++)
			{
				this.BlitSprite(batch,bomb.x - (i * 0x20), bomb.y, this.bombTexture, 4, 0x20);
				this.BlitSprite(batch,bomb.x + (i * 0x20), bomb.y, this.bombTexture, 4, 0x20);
				this.BlitSprite(batch,bomb.x, bomb.y - (i * 0x20), this.bombTexture, 3, 0x20);
				this.BlitSprite(batch,bomb.x, bomb.y + (i * 0x20), this.bombTexture, 3, 0x20);
			}
		}
	}

	private void DrawEnemys(SpriteBatch batch)
	{
		for (Enemy enemy : this.enemys)
		{
			enemy.x += enemy.xmov;
			enemy.y += enemy.ymov;
			this.BlitSprite(batch,enemy.x, enemy.y, this.enemyTexture, enemy.i, 0x20);
		}
	}

	private void DrawGamePad(SpriteBatch batch)
	{
		float scale = 1.5f;
		this.BlitSprite(batch,0x250, 0x110, this.gamepadTexture, 0, 0x80, scale);
		this.BlitSprite(batch,0x10, 0x150, this.gamepadTexture, 1, 0x80);
	}

	private void DrawMap(SpriteBatch batch,LTexture texture, byte[][] items, boolean first)
	{
		for (int i = 0; i < this.map.height; i++)
		{
			for (int j = 0; j < this.map.width; j++)
			{
				if ((items[j][i] != 0) || first)
				{
					this.BlitSprite(batch,j * 0x20, i * 0x20, texture, items[j][i], 0x20);
				}
			}
		}
	}

	private void DrawMenu(SpriteBatch batch)
	{
		this.Blit(batch,0, 0, this.introScreenTexture);
		this.DrawText(batch);
		if (this.isTrial)
		{
			this.BlitText(batch,3, 120, 400);
		}
	}

	private void DrawPlayer(SpriteBatch batch)
	{
		if (this.player.energy < 1)
		{
			this.player.id = 5;
		}
		else
		{
			this.player.x += this.player.xmov;
			this.player.y += this.player.ymov;
		}
		BlitSprite(batch,this.player.x, this.player.y, this.playerTexture, this.player.id, 0x20);
	}

	private void DrawPlaying(SpriteBatch batch)
	{
		this.DrawMap(batch,this.backgroundTexture, this.map.background, true);
		this.DrawMap(batch,this.tresureTexture, this.map.treasure, false);
		this.DrawMap(batch,this.stoneTexture, this.map.stone, false);
		this.DrawPlayer(batch);
		this.DrawEnemys(batch);
		this.DrawBombs(batch);
		this.CheckBombHit();
		this.CheckEnemyHit();
		this.DrawStatusBar(batch);
		this.DrawGamePad(batch);
		this.DrawText(batch);
		if (++this.frame > 7)
		{
			this.frame = 0;
			this.CheckTreasure();
			this.CheckLevelComplete();
			this.MoveEnemys();
			this.UpdateBombs();
		}
	}

	private void DrawStatusBar(SpriteBatch batch)
	{
		this.Blit(batch,0, 0x1a0, this.statusBarTexture);
		for (int i = 0; i < this.player.lives; i++)
		{
			this.BlitSprite(batch,(i * 0x1c) + 180, 0x19f, this.statusToolsTexture, 0, 0x20);
		}
		for (int j = 0; j < this.player.energy; j++)
		{
			this.BlitSprite(batch,(j * 14) + 170, 0x1bf, this.statusToolsTexture, 1, 0x20);
		}
		for (int k = 0; k < this.player.power; k++)
		{
			this.BlitSprite(batch,(k * 0x10) + 0x20c, 0x1a0, this.statusToolsTexture, 2, 0x20);
		}
		for (int m = 0; m < this.player.bombs; m++)
		{
			this.BlitSprite(batch,(m * 0x1b) + 520, 0x1c0, this.statusToolsTexture, 3, 0x20);
		}
	}

	private void DrawText(SpriteBatch batch)
	{
		if (this.showStart)
		{
			this.BlitText(batch,0);
			this.fade -= 2;
			if (this.fade <= 0)
			{
				this.fade = 0xff;
				this.showStart = false;
			}
		}
		if (this.showGameOver)
		{
			this.BlitText(batch,2);
			this.fade -= 3;
			if (this.fade <= 0)
			{
				this.fade = 0xff;
				this.showGameOver = false;
				this.gameStatus = GameStatus.Menu;
			}
		}
	}

	private void GameResume()
	{
		this.LoadGameSate();
		this.GameStart(this.player.levelId);
	}

	private void GameStart(int levelId)
	{
		this.frame = 0;
		this.fade = 0xff;
		this.showStart = true;
		int points = this.player.points;
		int lives = this.player.lives;
		this.map.Load(this.levelname[levelId]);
		this.player = this.map.GetPlayer();
		this.enemys = this.map.GetEnemys();
		this.bombs.clear();
		this.player.points = points;
		this.player.lives = lives;
		this.player.levelId = levelId;
		this.backgroundTexture = this.forestTexture;
		if (this.levelname[levelId].startsWith("Desert"))
		{
			this.backgroundTexture = this.desertTexture;
		}
		if (this.levelname[levelId].startsWith("Winter"))
		{
			this.backgroundTexture = this.winterTexture;
		}
		if (this.levelname[levelId].startsWith("Forest"))
		{
			this.backgroundTexture = this.forestTexture;
		}
		if (this.levelname[levelId].startsWith("Lava"))
		{
			this.backgroundTexture = this.lavaTexture;
		}
		this.gameStatus = GameStatus.Playing;
	}



	protected void LoadContent()
	{
		this.enemyTexture = LTextures.loadTexture("assets/Sprites/Enemy");
		this.stoneTexture = LTextures.loadTexture("assets/Sprites/Stone");
		this.playerTexture = LTextures.loadTexture("assets/Sprites/Player");
		this.tresureTexture = LTextures.loadTexture("assets/Sprites/Treasure");
		this.bombTexture = LTextures.loadTexture("assets/Sprites/Bomb");
		this.gamepadTexture = LTextures.loadTexture("assets/Sprites/Gamepad");
		this.desertTexture = LTextures.loadTexture("assets/Sprites/Desert");
		this.forestTexture = LTextures.loadTexture("assets/Sprites/Forest");
		this.winterTexture = LTextures.loadTexture("assets/Sprites/Winter");
		this.lavaTexture = LTextures.loadTexture("assets/Sprites/Lava");
		this.statusBarTexture = LTextures.loadTexture("assets/Interface/statusBar");
		this.statusToolsTexture = LTextures.loadTexture("assets/Interface/statusTools");
		this.introScreenTexture = LTextures.loadTexture("assets/Interface/IntroScreen");
		this.textTexture = LTextures.loadTexture("assets/Interface/Text");
	}

	private void LoadGameSate()
	{
		this.isLicenseChecked = false;
	}

	private void MoveEnemys()
	{
		for (Enemy enemy : this.enemys)
		{
			int num = this.random.nextInt() % 4;
			enemy.xmov = 0;
			enemy.ymov = 0;
			switch (num)
			{
				case 0:
				{
					if ((((enemy.y / 0x20) < (this.map.height - 1)) && (this.map.background[enemy.x / 0x20][(enemy.y / 0x20) + 1] < 0x19)) && ((this.map.stone[enemy.x / 0x20][(enemy.y / 0x20) + 1] < 5) && (this.map.bomb[enemy.x / 0x20][(enemy.y / 0x20) + 1] == 0)))
					{
						enemy.ymov = 4;
					}
					continue;
				}
				case 1:
				{
					if ((((enemy.y / 0x20) > 0) && (this.map.background[enemy.x / 0x20][(enemy.y / 0x20) - 1] < 0x19)) && ((this.map.stone[enemy.x / 0x20][(enemy.y / 0x20) - 1] < 5) && (this.map.bomb[enemy.x / 0x20][(enemy.y / 0x20) - 1] == 0)))
					{
						enemy.ymov = -4;
					}
					continue;
				}
				case 2:
				{
					if ((((enemy.x / 0x20) < (this.map.width - 1)) && (this.map.background[(enemy.x / 0x20) + 1][enemy.y / 0x20] < 0x19)) && ((this.map.stone[(enemy.x / 0x20) + 1][enemy.y / 0x20] < 5) && (this.map.bomb[(enemy.x / 0x20) + 1][enemy.y / 0x20] == 0)))
					{
						enemy.xmov = 4;
					}
					continue;
				}
				case 3:
				{
					if ((((enemy.x / 0x20) > 0) && (this.map.background[(enemy.x / 0x20) - 1][enemy.y / 0x20] < 0x19)) && ((this.map.stone[(enemy.x / 0x20) - 1][enemy.y / 0x20] < 5) && (this.map.bomb[(enemy.x / 0x20) - 1][enemy.y / 0x20] == 0)))
					{
						enemy.xmov = -4;
					}
					continue;
				}
			}
		}
	}

	
	public void Update(GameTime gameTime)
	{
		GameStatus gameStatus = this.gameStatus;
		if (gameStatus == GameStatus.Menu)
		{
			this.UpdateMenu();
			this.CheckLicense();
		}
		else if (Key.isKeyPressed(Key.BACK))
		{
			LSystem.exit();
		}
	}

	private void UpdateBombs()
	{
		for (int i = 0; i < this.bombs.size(); i++)
		{
			Bomb local1 = this.bombs.get(i);
			local1.time--;
			if (this.bombs.get(i).time == 1)
			{
			
			}
			if (this.bombs.get(i).time <= 0)
			{
				this.map.bomb[this.bombs.get(i).x / 0x20][this.bombs.get(i).y / 0x20] = 0;
				this.bombs.remove(i);
				this.player.bombs++;
			}
		}
	}

	private void UpdateMenu()
	{
		if (Touch.isDown())
		{
			if (this.box(Touch.x(), Touch.y(), 0x23, 0x6a, 0xd3, 0x9b))
			{
				this.gameStatus = GameStatus.Start;
			}
			if (this.box(Touch.x(), Touch.y(), 0x2d, 170, 0xf3, 0xdb))
			{
				this.gameStatus = GameStatus.Resume;
			}

		}
	}

	private enum GameStatus
	{
		Menu,
		Start,
		Resume,
		Playing;

	}
}