package org.test.act;

import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.LTransition;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.action.sprite.painting.DrawableScreen;
import loon.canvas.LColor;
import loon.canvas.LColorPool;
import loon.events.GameKey;
import loon.events.GameTouch;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.LSTRDictionary;
import loon.utils.MathUtils;
import loon.utils.timer.GameTime;

public class MainGame extends DrawableScreen {

	private LColorPool colors = new LColorPool();

	private ammo_struct[] ammo;

	private int begin_frames;
	private bg_main bg_1;
	private block_struct[][] block;
	private button_struct[] button;

	private float_struct[] floating;

	private int frame;

	public int high_score;

	private int level = 1;
	private int level_frame_count;
	private int level_frame_end;
	private int level_kills;
	private int level_targets;
	private int map_h = 15;
	private int map_w = 30;
	private int max_ammo = 20;
	private int max_buttons = 10;
	private int max_floats = 20;
	private int max_hits = 3;
	private int max_shots = 0x23;
	private int max_smoke = 100;
	private int max_targets = 0x19;

	private me_struct me = new me_struct();

	public int paused;
	private int pressed;

	private int released;
	private shot_struct[] shot;
	private smoke_struct[] smoke;

	private target_struct[] target;
	private tile_struct[] tile;
	private int tile_cols = 10;
	private int tile_rows = 10;
	private int tile_size = 0x20;
	private LTexture tiles;
	private int view_h = 480;
	private int view_tile_h = 15;
	private int view_tile_w = 0x19;
	private int view_w = 800;
	private int zone_offset;
	private int zone_tx;
	private float zone_x;

	public MainGame() {

	}

	public final void add_float(int sx, int sy, String stext, LColor scolor) {
		for (int i = 0; i < this.max_floats; i++) {
			if (this.floating[i].active != 1) {
				this.floating[i].active = 1;
				this.floating[i].fx = sx;
				this.floating[i].fy = sy;
				this.floating[i].text = stext;
				this.floating[i].dur = 60;
				this.floating[i].alpha = 0xff;
				this.floating[i].color = scolor;
				return;
			}
		}
	}

	public final void add_smoke(int stile, int alpha, int sx, int sy,
			float speed_x, float speed_y) {
		for (int i = 0; i < this.max_smoke; i++) {
			if (this.smoke[i].active != 1) {
				this.smoke[i].active = 1;
				this.smoke[i].fx = this.smoke[i].x = sx;
				this.smoke[i].fy = this.smoke[i].y = sy;
				this.smoke[i].speed_x = speed_x;
				this.smoke[i].speed_y = speed_y;
				this.smoke[i].alpha = alpha;
				this.smoke[i].fading = 1;
				this.smoke[i].dur = 30;
				this.smoke[i].tile = stile;
				this.smoke[i].rot = MathUtils.nextInt(0x167);
				return;
			}
		}
	}

	public final int column_top(int cx) {
		int num2 = 0;
		for (int i = 0; i < (this.map_h - 1); i++) {
			num2 = i;
			if (this.block[cx][i + 1].active == 1) {
				break;
			}
		}
		if (this.block[cx][this.map_h - 1].active == 0) {
			num2 = this.map_h - 1;
		}
		return num2;
	}

	public final void create_explosion(int ex, int ey, int spread, int dirt,
			int fog, int fire, int boom) {
		int num;
		for (num = 0; num < fog; num++) {
			this.add_smoke(
					1,
					0xff,
					(ex - spread) + MathUtils.nextInt(spread * 2),
					ey,
					(-this.me.speed_x * 2f)
							+ MathUtils.nextInt(((int) this.me.speed_x) * 2),
					(float) -MathUtils.nextInt(5));
		}
		for (num = 0; num < fire; num++) {
			this.add_smoke(0, 0x9b, ex, ey,
					(-this.me.speed_x - 2f) + MathUtils.nextInt(5),
					(float) -MathUtils.nextInt(7));
		}
		for (num = 0; num < dirt; num++) {
			this.add_smoke(
					4,
					0xff,
					(ex - (spread / 4)) + MathUtils.nextInt((spread / 4) * 2),
					ey,
					(-this.me.speed_x * 2f)
							+ MathUtils.nextInt(((int) this.me.speed_x) * 2),
					(float) -MathUtils.nextInt(5));
		}
		for (num = 0; num < boom; num++) {
			this.add_smoke(
					3,
					0xff,
					ex,
					ey,
					(-this.me.speed_x * 2f)
							+ MathUtils.nextInt(((int) this.me.speed_x) * 2),
					(float) -MathUtils.nextInt(7));
		}
	}

	public final int damage(int x, int y, int radius, int power, int source) {
		RectBox rectangle = new RectBox(x - radius, y - (radius / 2),
				radius * 2, radius);
		RectBox rectangle2 = new RectBox(0, 0, this.tile_size, this.tile_size);
		int num2 = 0;
		int num3 = -1;
		int num5 = 0;
		for (int i = 0; i < this.max_targets; i++) {
			if ((this.target[i].active > 0) && (source == this.max_targets)) {
				rectangle2.x = this.target[i].x - (this.target[i].w / 2);
				rectangle2.y = this.target[i].y - (this.target[i].h / 2);
				rectangle2.width = this.target[i].w;
				rectangle2.height = this.target[i].h;
				if (rectangle.intersects(rectangle2)) {
					this.target[i].hp -= power;
					this.target[i].lift = 3f;
					this.create_explosion(x, y, 5, 2, 0, 0, 0);
					num2 = 1;
					if (this.target[i].hp < 1) {
						this.me.kills++;
						this.add_smoke(this.target[i].tile, 0xff,
								this.target[i].x, this.target[i].y,
								(float) this.target[i].speed_x,
								(float) (2 + -MathUtils.nextInt(5)));
						if (this.target[i].explodes == 0) {
							this.create_explosion(this.target[i].x,
									this.target[i].y, this.target[i].w / 2, 0,
									5, 10, 7);
						} else {
							num3 = i;
							this.create_explosion(this.target[i].x,
									this.target[i].y, this.target[i].explodes,
									10, 10, 20, 3);
						}
						this.target[i].active = 0;
						int num4 = 100;
						if (this.target[i].air == 1) {
							num4 = 0x7d;
						}
						if (this.target[i].explodes > 0) {
							num4 = 50;
						}
						if (this.target[i].speed_x == -((int) this.me.speed_x)) {
							num4 = 0x4b;
						}
						if (this.me.streak == 0) {
							this.add_float(this.target[i].x, this.target[i].y,
									"" + num4, LColor.orange);
						} else {
							this.add_float(this.target[i].x, this.target[i].y
									+ num5,
									num4 + " x " + (this.me.streak + 1),
									LColor.orange);
						}
						num4 *= this.me.streak + 1;
						if (source == this.max_targets) {
							this.me.score += num4;
							if (this.high_score < this.me.score) {
								this.high_score = this.me.score;
								this.save_game();
							}
						}
						num5 -= 40;
					}
				}
			}
		}
		this.me.hits += num2;
		this.me.streak += num2;
		if ((num2 == 0) && (source == this.max_targets)) {
			this.me.streak = 0;
		}
		return num3;
	}

	public final void draw_tile(SpriteBatch batch, int t, int x, int y,
			float srot, float talpha) {
		batch.draw(this.tiles, x, y, this.tile[t].w, this.tile[t].h,
				this.tile[t].x + 1, this.tile[t].y + 1, this.tile[t].w - 2,
				this.tile[t].h - 2, colors.getColor((int) talpha, (int) talpha,
						(int) talpha, (int) talpha), MathUtils.toDegrees(srot),
				(float) (this.tile[t].w / 2), (float) (this.tile[t].h / 2),
				SpriteEffects.None);
	}

	public final void gen_map() {
		int num;
		int num2;
		int num3;
		int num4;
		this.zone_x = 0f;
		for (num3 = this.map_h - 1; num3 > (this.map_h - 5); num3--) {
			num2 = 0;
			while (num2 < this.map_w) {
				this.block[num2][num3].tile[0] = 30;
				num = 0;
				while (num < this.max_hits) {
					this.block[num2][num3].tile[num] = this.block[num2][num3].tile[0];
					num++;
				}
				this.block[num2][num3].hits = 1;
				this.block[num2][num3].active = 0;
				num4 = 0;
				if (num3 == (this.map_h - 1)) {
					num4 = 1;
				} else {
					if (this.block[num2][num3 + 1].active == 1) {
						num4 = 1;
					}
					if (((num2 == 1) || (num2 == 2))
							&& (this.block[0][num3].active == 0)) {
						num4 = 0;
					}
				}
				if (num4 == 1) {
					this.block[num2][num3].active = 1;
				}
				num2++;
			}
		}
		for (num3 = 0; num3 < this.map_h; num3++) {
			for (num2 = 0; num2 < this.map_w; num2++) {
				if (this.block[num2][num3].active == 1) {
					num4 = 0;
					num = 0x1f + MathUtils.nextInt(4);
					if (num3 == 0) {
						num4 = 1;
					} else if (this.block[num2][num3 - 1].active == 0) {
						num4 = 1;
					}
					if (num4 == 1) {
						this.block[num2][num3].tile[0] = num;
					}
				}
			}
		}
		this.me.position.x = this.me.x;
		this.me.position.y = this.me.y;
		this.level_targets = 2 + this.level;
		if (this.level_targets >= this.max_targets) {
			this.level_targets = this.max_targets - 1;
		}
		this.level_kills = this.level * 5;
		this.level_frame_end = 0xe10;
		this.level_frame_count = 0;
	}

	public final float get_angle(float x1, float y1, float x2, float y2) {
		float num = x1 - x2;
		float num2 = y1 - y2;
		float num3 = 0f;
		num3 = (((float) Math.atan2((double) (y2 - y1), (double) (x2 - x1))) * 180f) / 3.14159f;
		if ((num < 0f) && (num2 > 0f)) {
			num3 = 90f - -num3;
		} else if ((num < 0f) && (num2 < 0f)) {
			num3 = 90f + num3;
		} else if ((num > 0f) && (num2 < 0f)) {
			num3 = 90f + num3;
		} else if ((num > 0f) && (num2 > 0f)) {
			num3 = 270f + (180f - -num3);
		}
		if (num3 < 0f) {
			num3 = 360f + num3;
		}
		if (num3 > 359f) {
			num3 -= 360f;
		}
		return num3;
	}

	public final void get_input() {
		this.me.trot = 0f;
	}

	public final void init_vars() {
		int num;
		int num2;

		int index = 0;

		for (num2 = 0; num2 < this.tile_cols; num2++) {
			num = 0;
			while (num < this.tile_rows) {
				this.tile[index].w = this.tile[index].h = this.tile_size;
				this.tile[index].x = num * this.tile[index].w;
				this.tile[index].y = num2 * this.tile[index].h;
				index++;
				num++;
			}
		}
		index = 5;
		this.tile[index].w = 0x40;
		index = 0x47;
		this.tile[index].w = 0x40;
		index = 8;
		this.tile[index].w = this.tile[index].h = 0x40;
		index = 0x1c;
		this.tile[index].w = this.tile[index].h = 0x40;
		index = 0x3f;
		this.tile[index].w = this.tile[index].h = 0x40;
		index = 0x41;
		this.tile[index].w = this.tile[index].h = 0x40;
		index = 0x43;
		this.tile[index].w = this.tile[index].h = 0x40;
		for (num2 = 0; num2 < this.map_h; num2++) {
			for (num = 0; num < this.map_w; num++) {
				this.block[num][num2].tile = new int[this.max_hits];
			}
		}
		for (index = 0; index < this.max_ammo; index++) {
			this.ammo[index].tile = 80;
			this.ammo[index].burst = 1;
			this.ammo[index].bounces = 5;
			this.ammo[index].size = this.tile_size;
			this.ammo[index].velocity = 6f;
			this.ammo[index].drag = 0.01f;
			this.ammo[index].drop = 0.2f;
			this.ammo[index].smoke = 1 + index;
			this.ammo[index].wait = 30;
			this.ammo[index].accel = 0f;
		}
		index = 0;
		this.ammo[index].radius = 80;
		this.ammo[index].power = 150;
		this.ammo[index].size = 0x11;
		this.ammo[index].tile = this.ammo[index].tile;
		this.ammo[index].velocity = 1f;
		this.ammo[index].drag = 0.01f;
		this.ammo[index].drop = 0.2f;
		this.ammo[index].smoke = 2;
		this.ammo[index].burst = 1;
		this.ammo[index].wait = 0x19;
		index = 1;
		this.ammo[index].radius = 5;
		this.ammo[index].power = 0x23;
		this.ammo[index].size = 7;
		this.ammo[index].tile++;
		this.ammo[index].velocity = 12f;
		this.ammo[index].drag = 0.001f;
		this.ammo[index].drop = 0.1f;
		this.ammo[index].smoke = 0;
		this.ammo[index].burst = 3;
		this.ammo[index].wait = 15;
		index = 2;
		this.ammo[index].radius = 0x37;
		this.ammo[index].power = 100;
		this.ammo[index].size = 7;
		this.ammo[index].tile += 2;
		this.ammo[index].velocity = 3f;
		this.ammo[index].drag = 0.0001f;
		this.ammo[index].drop = 0.01f;
		this.ammo[index].smoke = 1;
		this.ammo[index].burst = 1;
		this.ammo[index].wait = 15;
		this.ammo[index].accel = 0.1f;
		index = 3;
		this.ammo[index].radius = 3;
		this.ammo[index].power = 20;
		this.ammo[index].size = 5;
		this.ammo[index].tile += 3;
		this.ammo[index].velocity = 8f;
		this.ammo[index].drag = 0.001f;
		this.ammo[index].drop = 0.01f;
		this.ammo[index].smoke = 0;
		this.ammo[index].burst = 2;
		this.ammo[index].wait = 20;
		index = 4;
		this.ammo[index].radius = 5;
		this.ammo[index].power = 100;
		this.ammo[index].size = 10;
		this.ammo[index].tile += 4;
		this.ammo[index].velocity = 6f;
		this.ammo[index].drag = 0.001f;
		this.ammo[index].drop = 0.001f;
		this.ammo[index].smoke = this.ammo[index].tile;
		this.ammo[index].burst = 1;
		this.ammo[index].wait = 20;
	}

	public final void load_game() {

	}

	public final void move_floats() {
		for (int i = 0; i < this.max_floats; i++) {
			if (this.floating[i].active == 1) {
				this.floating[i].fy--;
				this.floating[i].dur--;
				this.floating[i].alpha -= 7;
				if ((this.floating[i].alpha <= 0)
						|| (this.floating[i].dur <= 0)) {
					this.floating[i].active = 0;
				}
			}
		}
	}

	public final void move_player() {
		float num9;
		RectBox rectangle2 = new RectBox(0, 0, this.tile_size, this.tile_size);
		if (this.me.shot_wait > 0) {
			this.me.shot_wait--;
		}
		this.me.shaking--;
		if (this.me.shaking < 1) {
			this.me.shaking = 0;
		}
		if (this.me.tspeed_x > this.me.speed_x) {
			this.me.speed_x += 0.5f;
		}
		if (this.me.tspeed_x < this.me.speed_x) {
			this.me.speed_x -= 0.5f;
		}
		if (this.me.trot == 0f) {
			num9 = 0.075f;
		} else {
			num9 = 0.25f;
		}
		if (this.me.trot > this.me.rot) {
			this.me.rot += num9;
		}
		if (this.me.trot < this.me.rot) {
			this.me.rot -= num9;
		}
		if ((this.me.rot > this.me.trot) && (this.me.trot > 0f)) {
			this.me.rot = this.me.trot;
		}
		if ((this.me.rot < this.me.trot) && (this.me.trot < 0f)) {
			this.me.rot = this.me.trot;
		}
		if (((this.me.rot < num9) && (this.me.rot > -num9))
				&& (this.me.trot == 0f)) {
			this.me.rot = this.me.trot;
		}
		this.zone_x += this.me.speed_x;
		if (this.zone_x >= (this.map_w << 5)) {
			this.zone_x -= this.map_w << 5;
		}
		this.zone_tx = (int) (this.zone_x / ((float) this.tile_size));
		this.zone_offset = ((int) this.zone_x) - (this.zone_tx << 5);
		this.begin_frames--;
		if (this.begin_frames < 1) {
			if ((this.level <= 1) && (this.me.active == 0)) {
				this.me.score = 0;
			}
			this.me.active = 1;
		}
		if (this.me.active == 1) {
			if (this.me.hp < 50) {
				if ((this.frame % 8) == 0) {
					this.add_smoke(1, 0xff, this.me.x - (this.me.w / 2),
							this.me.y, -this.me.speed_x * 2f, -1f);
				}
				if ((this.me.hp < 0x19) && ((this.frame % 8) == 4)) {
					this.add_smoke(0, 0xff, this.me.x - (this.me.w / 2),
							this.me.y, -this.me.speed_x * 2f, 0f);
				}
			}
			int num4 = this.me.x / this.tile_size;
			int num5 = this.me.y / this.tile_size;
			this.me.tile_x = num4;
			this.me.tile_y = num5;
			RectBox rectangle = new RectBox(this.me.x - (this.me.w / 2),
					this.me.y - (this.me.h / 2), this.me.w, this.me.h);
			for (int i = 0; i < this.max_targets; i++) {
				if (this.target[i].active > 0) {
					rectangle2.x = this.target[i].x - (this.target[i].w / 2);
					rectangle2.y = this.target[i].y - (this.target[i].h / 2);
					rectangle2.width = this.target[i].w;
					rectangle2.height = this.target[i].h;
					if (rectangle.intersects(rectangle2)) {
						int index = this.damage(this.target[i].x,
								this.target[i].y, 10, 200, this.max_targets);
						if (index >= 0) {
							this.damage(this.target[index].x,
									this.target[index].y,
									this.target[index].explodes, 150,
									this.max_targets);
						}
						this.me.hp -= 20;
						this.me.shaking = 20;
						this.create_explosion(this.me.x, this.me.y, 40, 0, 4,
								4, 4);
						this.create_explosion(this.target[i].x,
								this.target[i].y, 40, 0, 4, 4, 4);
					}
				}
			}
			rectangle = new RectBox((this.me.x + ((int) this.zone_x))
					- (this.me.w / 2), this.me.y - (this.me.h / 2), this.me.w,
					this.me.h);
			for (int j = num5 - 2; j <= (num5 + 2); j++) {
				for (int k = (this.zone_tx + num4) - 2; k <= ((this.zone_tx + num4) + 2); k++) {
					if ((j >= 0) && (j < this.map_h)) {
						int num7 = k;
						int num8 = j;
						if (num7 >= this.map_w) {
							num7 -= this.map_w;
						}
						if (num7 < 0) {
							num7 = this.map_w + num7;
						}
						if (this.block[num7][num8].active == 1) {
							rectangle2.x = k << 5;
							rectangle2.y = num8 << 5;
							if (rectangle.intersects(rectangle2)) {
								this.me.hp -= MathUtils.nextInt(2);
								if (this.me.hp < 0) {
									this.me.hp = 0;
								}
								this.me.shaking = 20;
							}
						}
					}
				}
			}
		}
	}

	public final void move_shots() {
		float num14 = 0f;
		RectBox rectangle2 = new RectBox(0, 0, this.tile_size, this.tile_size);
		for (int i = 0; i < this.max_shots; i++) {
			int num9;
			float num11;
			if (this.shot[i].active != 1) {
				continue;
			}
			if (this.shot[i].wait > 0) {
				this.shot[i].wait--;
				continue;
			}
			if (((this.shot[i].x > (this.view_w + this.tile_size)) || (this.shot[i].x < -this.tile_size))
					|| ((this.shot[i].y > this.view_h) || (this.shot[i].y < -this.tile_size))) {
				if (this.shot[i].source == this.max_targets) {
					this.me.shots++;
					this.me.accuracy = (int) ((((float) this.me.hits) / ((float) this.me.shots)) * 100f);
					this.me.streak = 0;
				}
				this.shot[i].active = 0;
				continue;
			}
			if (this.ammo[this.shot[i].ammo].smoke > 0) {
				if ((this.frame % 3) == 0) {
					this.add_smoke(this.ammo[this.shot[i].ammo].smoke, 150,
							this.shot[i].x, this.shot[i].y, 0f, 0f);
				}
				if (((this.frame % 5) == 0)
						&& (this.ammo[this.shot[i].ammo].smoke == 1)) {
					this.add_smoke(0, 150, this.shot[i].x, this.shot[i].y, 0f,
							0f);
				}
			}
			this.shot[i].frames++;
			float num10 = num11 = 0f;
			if ((this.shot[i].angle >= 0f) && (this.shot[i].angle < 90f)) {
				num11 = (89f - this.shot[i].angle) / 89f;
				num10 = 1f - num11;
				num11 = -num11;
			} else if ((this.shot[i].angle >= 90f)
					&& (this.shot[i].angle < 180f)) {
				num10 = (89f - (this.shot[i].angle - 90f)) / 89f;
				num11 = 1f - num10;
			} else if ((this.shot[i].angle >= 180f)
					&& (this.shot[i].angle < 270f)) {
				num11 = (89f - (this.shot[i].angle - 180f)) / 89f;
				num10 = 1f - num11;
				num10 = -num10;
			} else if ((this.shot[i].angle >= 270f)
					&& (this.shot[i].angle < 360f)) {
				num10 = (89f - (this.shot[i].angle - 270f)) / 89f;
				num11 = 1f - num10;
				num11 = -num11;
				num10 = -num10;
			}
			if (this.ammo[this.shot[i].ammo].accel != 0f) {
				this.shot[i].accel += this.ammo[this.shot[i].ammo].accel
						* (this.shot[i].frames / 4);
			}
			num14 = this.ammo[this.shot[i].ammo].velocity + this.shot[i].accel;
			if (((num11 * num14) <= 7.5)
					&& (this.ammo[this.shot[i].ammo].drop != 0f)) {
				this.shot[i].lift += this.ammo[this.shot[i].ammo].drop;
			}
			if (((this.shot[i].drag + (num10 * this.ammo[this.shot[i].ammo].velocity)) + this.shot[i].drag) < 0.5f) {
				this.shot[i].drag += 0.025f;
			}
			float num12 = -this.shot[i].drag + (num10 * num14);
			float num13 = this.shot[i].lift + (num11 * num14);
			if ((num12 != 0f) || (num13 != 0f)) {
				this.shot[i].rot = this.get_angle(this.shot[i].fx,
						this.shot[i].fy, this.shot[i].fx + (num12 * 10f),
						this.shot[i].fy + (num13 * 10f));
				this.shot[i].rot = MathUtils.toRadians(this.shot[i].rot);
			}
			this.shot[i].fx += num12;
			this.shot[i].fy += num13;
			this.shot[i].x = (int) this.shot[i].fx;
			this.shot[i].y = (int) this.shot[i].fy;
			this.shot[i].tile_x = this.shot[i].x / this.tile_size;
			this.shot[i].tile_y = this.shot[i].y / this.tile_size;
			int num3 = this.shot[i].x / this.tile_size;
			int num4 = this.shot[i].y / this.tile_size;
			this.shot[i].tile_x = num3;
			this.shot[i].tile_y = num4;
			RectBox rectangle = new RectBox(this.shot[i].x
					- (this.ammo[this.shot[i].ammo].size / 2), this.shot[i].y
					- (this.ammo[this.shot[i].ammo].size / 2),
					this.ammo[this.shot[i].ammo].size,
					this.ammo[this.shot[i].ammo].size);
			if ((this.shot[i].source < this.max_targets)
					&& (this.shot[i].source >= 0)) {
				rectangle2.x = this.me.x - (this.me.w / 2);
				rectangle2.y = this.me.y - (this.me.h / 2);
				rectangle2.width = this.me.w;
				rectangle2.height = this.me.h;
				if ((rectangle.intersects(rectangle2) && (this.me.active == 1))
						&& (this.me.active == 1)) {
					this.create_explosion(this.shot[i].x, this.shot[i].y,
							this.ammo[this.shot[i].ammo].radius, 0,
							this.ammo[this.shot[i].ammo].power / 7,
							(this.ammo[this.shot[i].ammo].power - 10) / 10, 0);
					this.me.hp -= this.ammo[this.shot[i].ammo].power / 4;
					if (this.me.hp < 0) {
						this.me.hp = 0;
					}
					this.me.shaking = 20;
					this.shot[i].active = 0;
					if (this.me.hp <= 0) {
						this.create_explosion(this.me.x, this.me.y, this.me.w,
								10, 5, 5, 5);
						this.me.active = 0;
						this.level = 1;
						this.reset_data();
						this.gen_map();
						this.begin_frames = 250;
						return;
					}
				}
			} else {
				for (int k = 0; k < this.max_targets; k++) {
					if (this.target[k].active > 0) {
						rectangle2.x = this.target[k].x
								- (this.target[k].w / 2);
						rectangle2.y = this.target[k].y
								- (this.target[k].h / 2);
						rectangle2.width = this.target[k].w;
						rectangle2.height = this.target[k].h;
						if (rectangle.intersects(rectangle2)) {
							num9 = this.damage(this.shot[i].x, this.shot[i].y,
									this.ammo[this.shot[i].ammo].radius,
									this.ammo[this.shot[i].ammo].power,
									this.shot[i].source);
							if (num9 >= 0) {
								this.damage(this.target[num9].x,
										this.target[num9].y,
										this.target[num9].explodes, 150,
										this.max_targets);
							}
							this.create_explosion(
									this.shot[i].x,
									this.shot[i].y,
									this.ammo[this.shot[i].ammo].radius,
									0,
									this.ammo[this.shot[i].ammo].power / 7,
									(this.ammo[this.shot[i].ammo].power - 10) / 10,
									0);
							this.shot[i].active = 0;
							if (this.shot[i].source == this.max_targets) {
								this.me.shots++;
								this.me.accuracy = (int) ((((float) this.me.hits) / ((float) this.me.shots)) * 100f);
							}
							break;
						}
					}
				}
			}
			rectangle.setBounds((this.shot[i].x + ((int) this.zone_x))
					- (this.ammo[this.shot[i].ammo].size / 2), this.shot[i].y
					- (this.ammo[this.shot[i].ammo].size / 2),
					this.ammo[this.shot[i].ammo].size,
					this.ammo[this.shot[i].ammo].size);
			for (int j = num4 - 2; j <= (num4 + 2); j++) {
				for (int m = (this.zone_tx + num3) - 2; m <= ((this.zone_tx + num3) + 2); m++) {
					if (((j >= 0) && (j < this.map_h))
							&& (this.shot[i].active == 1)) {
						int num7 = m;
						int num8 = j;
						if (num7 >= this.map_w) {
							num7 -= this.map_w;
						}
						if (num7 < 0) {
							num7 = this.map_w + num7;
						}
						if (this.block[num7][num8].active == 1) {
							rectangle2.x = m << 5;
							rectangle2.y = num8 << 5;
							if (rectangle.intersects(rectangle2)) {
								num9 = this.damage(this.shot[i].x,
										this.shot[i].y,
										this.ammo[this.shot[i].ammo].radius,
										this.ammo[this.shot[i].ammo].power,
										this.shot[i].source);
								if (num9 >= 0) {
									this.damage(this.target[num9].x,
											this.target[num9].y,
											this.target[num9].explodes, 150,
											this.max_targets);
								}
								this.create_explosion(
										this.shot[i].x,
										this.shot[i].y,
										this.ammo[this.shot[i].ammo].radius,
										this.ammo[this.shot[i].ammo].power / 5,
										this.ammo[this.shot[i].ammo].power / 7,
										(this.ammo[this.shot[i].ammo].power - 0x23) / 10,
										0);
								this.shot[i].active = 0;
								if (this.shot[i].source == this.max_targets) {
									this.me.shots++;
									this.me.accuracy = (int) ((((float) this.me.hits) / ((float) this.me.shots)) * 100f);
								}
								break;
							}
						}
					}
				}
				if (this.shot[i].active == 0) {
					break;
				}
			}
		}
	}

	public final void move_smoke() {
		for (int i = 0; i < this.max_smoke; i++) {
			if (this.smoke[i].active == 1) {
				this.smoke[i].fx += this.smoke[i].speed_x;
				this.smoke[i].fy += this.smoke[i].speed_y;
				this.smoke[i].x = (int) this.smoke[i].fx;
				this.smoke[i].y = (int) this.smoke[i].fy;
				this.smoke[i].rot += 0.25f;
				this.smoke[i].dur--;
				if (this.smoke[i].fading == 1) {
					this.smoke[i].alpha -= 10f;
				}
				if (this.smoke[i].alpha < 1f) {
					this.smoke[i].alpha = 0f;
				}
				if (this.smoke[i].dur <= 0) {
					this.smoke[i].active = 0;
				}
			}
		}
	}

	public final void move_targets() {
		for (int i = 0; i < this.max_targets; i++) {
			int num7;
			if (this.target[i].active == 1) {
				if (((MathUtils.nextInt(150) == 0) && (this.me.active == 1))
						&& (this.target[i].explodes == 0)) {
					int index = 3;
					if (this.target[i].tile == 0x47) {
						index = 2;
					}
					if (this.target[i].tile == 0x41) {
						index = 4;
					}
					num7 = MathUtils.nextInt(0x29);
					for (int j = 0; j < this.ammo[index].burst; j++) {
						float sangle = (this.get_angle(
								(float) this.target[i].x,
								(float) this.target[i].y, (float) this.me.x,
								(float) this.me.y) - 20f)
								+ num7;
						if (sangle < 0f) {
							sangle = 360f + sangle;
						}
						if (sangle > 359f) {
							sangle -= 360f;
						}
						int num4 = this.shoot(index, sangle, this.target[i].x,
								this.target[i].y, j * 5);
						if (num4 >= 0) {
							this.shot[num4].source = i;
						}
					}
				}
				this.target[i].fx += this.target[i].speed_x;
				this.target[i].fy += this.target[i].speed_y;
				if (this.target[i].lift < 0f) {
					this.target[i].lift = 0f;
				}
				this.target[i].fy -= this.target[i].lift;
				this.target[i].lift -= 0.2f;
				this.target[i].tile_x = ((int) this.target[i].fx)
						/ this.tile_size;
				this.target[i].tile_y = ((int) this.target[i].fy)
						/ this.tile_size;
				int num2 = 1;
				if (((this.target[i].tile_x >= 0) && (this.target[i].tile_x < this.map_w))
						&& ((this.target[i].air != 1) && (this.target[i].fy <= ((((this.tile_size * (this.map_h - 4)) - (this.target[i].h / 2)) + 1) + 5)))) {
					this.target[i].fy += 1.75f;
					num2 = 0;
				}
				if ((((this.target[i].explodes == 0) && (this.target[i].air != 1)) && ((num2 == 1) && (this.target[i].tile_x >= 0)))
						&& ((this.target[i].tile_x < this.map_w) && (this.target[i].speed_x != -this.me.tspeed_x))) {
					this.target[i].rot = 0f;
					if (MathUtils.nextInt(180) == 0) {
						this.target[i].lift = 3f;
						if (this.target[i].speed_x > -this.me.tspeed_x) {
							this.target[i].rot = MathUtils.toRadians(350f);
						} else {
							this.target[i].rot = MathUtils.toRadians(10f);
						}
					}
				}
				this.target[i].x = (int) this.target[i].fx;
				this.target[i].y = (int) this.target[i].fy;
				if ((this.target[i].speed_x < 0)
						&& (this.target[i].x < -this.target[i].w)) {
					this.target[i].active = 0;
				}
				if ((this.target[i].speed_x > 0)
						&& (this.target[i].x > (this.view_w + this.target[i].w))) {
					this.target[i].active = 0;
				}
				if ((this.target[i].speed_y < 0)
						&& (this.target[i].y < -this.target[i].h)) {
					this.target[i].active = 0;
				}
				if ((this.target[i].speed_y > 0)
						&& (this.target[i].y > (this.view_h + this.target[i].h))) {
					this.target[i].active = 0;
				}
				if ((this.frame % 5) == 0) {
					if (this.target[i].air == 1) {
						this.add_smoke(1, 0xff, this.target[i].x,
								this.target[i].y, 0f, -1.5f);
					}
					if ((this.target[i].air == 0)
							&& (this.target[i].speed_x != -((int) this.me.tspeed_x))) {
						if (this.target[i].speed_x < -((int) this.me.tspeed_x)) {
							this.add_smoke(4, 0xff, this.target[i].x
									+ (this.target[i].w / 2), this.target[i].y
									+ (this.target[i].h / 2),
									(float) -MathUtils.nextInt(3),
									(float) -MathUtils.nextInt(2));
						} else {
							this.add_smoke(4, 0xff, this.target[i].x
									- (this.target[i].w / 2), this.target[i].y
									+ (this.target[i].h / 2),
									(float) -MathUtils.nextInt(3),
									(float) -MathUtils.nextInt(2));
						}
					}
				}
				continue;
			}
			if ((this.me.active == 1) && (i < this.level_targets)) {
				this.target[i].active = 1;
				this.target[i].hp = 50;
				this.target[i].rot = 0f;
				this.target[i].air = 0;
				this.target[i].explodes = 0;
				int num3 = MathUtils.nextInt(3);
				this.target[i].tile = 0x43;
				this.target[i].w = this.tile[this.target[i].tile].w;
				this.target[i].h = this.tile[this.target[i].tile].h;
				switch (num3) {
				case 1:
					this.target[i].hp = 100;
					this.target[i].tile = 0x3f;
					this.target[i].w = this.tile[this.target[i].tile].w;
					this.target[i].h = this.tile[this.target[i].tile].h;
					break;

				case 2:
					this.target[i].hp = 0x4b;
					this.target[i].tile = 0x41;
					this.target[i].w = this.tile[this.target[i].tile].w;
					this.target[i].h = this.tile[this.target[i].tile].h;
					this.target[i].air = 1;
					break;
				}
				if (MathUtils.nextInt(2) == 0) {

					this.target[i].x = (-MathUtils.nextInt(1, 20) * this.tile_size)
							+ (this.target[i].w / 2);

					this.target[i].speed_x = MathUtils.nextInt(1,
							((int) this.me.tspeed_x) + 1);
				} else {
					this.target[i].speed_x = -((int) this.me.tspeed_x)
							- MathUtils.nextInt(((int) this.me.tspeed_x) + 1);
					if (this.target[i].speed_x == -((int) this.me.tspeed_x)) {
						this.target[i].tile = 0x47;
						this.target[i].hp = 100;
						this.target[i].w = this.tile[this.target[i].tile].w;
						this.target[i].h = this.tile[this.target[i].tile].h;
						this.target[i].air = 0;
					}
					this.target[i].x = (this.view_w + (MathUtils.nextInt(1, 20) * this.tile_size))
							+ (this.target[i].w / 2);
				}
				if (MathUtils.nextInt(10) == 0) {
					int num8 = 0;
					for (num7 = 0; num7 < this.max_targets; num7++) {
						if ((this.target[num7].active == 1)
								&& (this.target[num7].explodes > 0)) {
							num8 = 1;
							break;
						}
					}
					if (num8 == 0) {
						this.target[i].air = 0;
						this.target[i].tile = 70;
						this.target[i].w = this.tile[this.target[i].tile].w;
						this.target[i].h = this.tile[this.target[i].tile].h;
						this.target[i].explodes = 200;
						this.target[i].hp = 1;
						this.target[i].speed_x = -((int) this.me.tspeed_x);
						this.target[i].speed_y = 0;
					}
				}
				this.target[i].y = (this.view_h - (this.tile_size * 4))
						- (this.target[i].h / 2);
				if (this.target[i].air == 1) {
					this.target[i].y = MathUtils.nextInt(50, this.view_h
							- (this.tile_size * 6));
				}
				this.target[i].fx = this.target[i].x;
				this.target[i].fy = this.target[i].y;
			}
		}
	}

	public final void reset_data() {
		int index = 0;
		for (int i = 0; i < this.map_h; i++) {
			for (int j = 0; j < this.map_w; j++) {
				this.block[j][i].active = 0;
				index++;
			}
		}
		for (index = 0; index < this.max_buttons; index++) {
			this.button[index].active = 0;
			this.button[index].ammo = -1;
			this.button[index].select = 0;
			this.button[index].icon = -1;
		}
		for (index = 0; index < this.max_targets; index++) {
			this.target[index].active = 0;
			this.target[index].tile = 70;
		}
		for (index = 0; index < this.max_shots; index++) {
			this.shot[index].active = 0;
		}
		for (index = 0; index < this.max_ammo; index++) {
			if (index <= 1) {
				this.ammo[index].active = 1;
			} else {
				this.ammo[index].active = 0;
			}
		}
		this.me.x = this.view_w / 2;
		this.me.y = this.view_h / 2;
		this.me.w = this.tile_size * 2;
		this.me.h = this.tile_size;
		this.me.ammo = 0;
		this.me.angle = 45f;
		this.me.tile = 5;
		this.me.tspeed_x = this.me.speed_x = 4f;
		this.me.shots = 0;
		this.me.kills = 0;
		this.me.hits = 0;
		this.me.accuracy = 0;
		this.me.streak = 0;
		this.me.shaking = 0;
		this.me.hp = 100;
		this.begin_frames = 150;
		index = 0;
		this.button[index].active = 1;
		this.button[index].ammo = 0;
		this.button[index].x = 10;
		this.button[index].y = 10;
		this.button[index].w = 0x40;
		this.button[index].h = 0x40;
		this.button[index].tile = 0x1c;
		index = 1;
		this.button[index].active = 1;
		this.button[index].ammo = 1;
		this.button[index].x = 0x54;
		this.button[index].y = 10;
		this.button[index].w = 0x40;
		this.button[index].h = 0x40;
		this.button[index].tile = 0x1c;
		index = 2;
		this.button[index].active = 1;
		this.button[index].ammo = 2;
		this.button[index].x = 0x9e;
		this.button[index].y = 10;
		this.button[index].w = 0x40;
		this.button[index].h = 0x40;
		this.button[index].tile = 0x1c;
		index = this.max_buttons - 1;
		this.button[index].active = 1;
		this.button[index].ammo = -1;
		this.button[index].icon = 7;
		this.button[index].x = (this.view_w - 10) - 0x40;
		this.button[index].y = 10;
		this.button[index].w = 0x40;
		this.button[index].h = 0x40;
		this.button[index].tile = 0x1c;
		index = 2;
		this.button[index].select = 1;
		this.me.ammo = this.button[index].ammo;
	}

	public final void save_game() {

	}

	public final int shoot(int sammo, float sangle, int sx, int sy, int wait) {
		for (int i = 0; i < this.max_shots; i++) {
			if (this.shot[i].active != 1) {
				if (wait == 0) {

				}
				this.shot[i].active = 1;
				this.shot[i].fx = this.shot[i].x = sx;
				this.shot[i].fy = this.shot[i].y = sy;
				this.shot[i].mx = this.shot[i].my = 1;
				this.shot[i].ammo = sammo;
				this.shot[i].angle = this.shot[i].start_angle = sangle;
				this.shot[i].bounces = this.ammo[sammo].bounces;
				this.shot[i].speed = this.ammo[sammo].velocity;
				this.shot[i].lift = 0f;
				this.shot[i].drag = 0f;
				this.shot[i].wait = wait;
				this.shot[i].start_x = 0;
				this.shot[i].start_y = 0;
				this.shot[i].frames = 0;
				this.shot[i].source = -1;
				this.shot[i].accel = 0f;
				return i;
			}
		}
		return -1;
	}

	public final void update_level() {
		if ((this.me.active == -2) && (this.begin_frames <= 150)) {
			this.me.active = -3;
			this.level++;
			this.reset_data();
			this.gen_map();
		}
		if ((this.me.hp <= 0) && (this.me.active == 1)) {

			this.create_explosion(this.me.x, this.me.y, this.me.w, 10, 5, 5, 5);
			this.me.active = 0;
			this.level = 1;
			this.reset_data();
			this.gen_map();
			this.begin_frames = 250;
		} else if ((this.level_frame_count >= this.level_frame_end)
				&& (this.me.active == 1)) {

			this.me.active = -1;
			this.level = 1;
			this.reset_data();
			this.gen_map();
			this.begin_frames = 250;
		} else if ((this.me.kills >= this.level_kills) && (this.me.active == 1)) {

			this.me.active = -2;
			this.begin_frames = 480;
		} else if (this.me.active == 1) {
			this.level_frame_count++;
		}
	}

	private LColorPool pools = new LColorPool();

	private int lastOffset;

	@Override
	public void draw(SpriteBatch batch) {
		if(!isOnLoadComplete()){
			return;
		}
		batch.setUseAscent(true);
		int num3;
		int tile;
		int x;
		int y;
		String str;
		SpriteEffects none = SpriteEffects.None;
		this.bg_1.Draw();

		if (lastOffset != zone_offset) {
			this.tiles.glBegin();
			for (int i = 0; i < this.view_tile_h; i++) {
				for (int j = 0; j <= this.view_tile_w; j++) {
					int num5 = i;
					int num4 = j + this.zone_tx;
					if (num4 >= this.map_w) {
						num4 -= this.map_w;
					}
					if ((this.block[num4][num5].active == 1)
							&& (this.block[num4][num5].hits > 0)) {
						tile = this.block[num4][num5].tile[this.block[num4][num5].hits - 1];
						x = (this.tile_size * j) - this.zone_offset;
						y = this.tile_size * i;
						this.tiles.draw(x, y, this.tile[tile].w,
								this.tile[tile].h, this.tile[tile].x,
								this.tile[tile].y, this.tile[tile].w
										+ this.tile[tile].x, this.tile[tile].h
										+ this.tile[tile].y);
					}
				}
			}
			this.tiles.glEnd();
			this.tiles.saveBatchCache();
		} else {
			this.tiles.postLastBatchCache();
		}

		lastOffset = zone_offset;

		for (num3 = 0; num3 < this.max_smoke; num3++) {
			if (this.smoke[num3].active == 1) {
				this.draw_tile(batch, this.smoke[num3].tile,
						this.smoke[num3].x, this.smoke[num3].y,
						this.smoke[num3].rot, this.smoke[num3].alpha);
			}
		}
		for (num3 = 0; num3 < this.max_targets; num3++) {
			tile = this.target[num3].tile;
			x = this.target[num3].x;
			y = this.target[num3].y;
			if (this.target[num3].speed_x <= -this.me.tspeed_x) {
				none = SpriteEffects.FlipHorizontally;
			} else {
				none = SpriteEffects.None;
			}
			if ((this.target[num3].active == 1) && (this.target[num3].hp > 0)) {
				batch.draw(this.tiles, x, y, this.tile[tile].w,
						this.tile[tile].h, this.tile[tile].x + 1,
						this.tile[tile].y + 1, this.tile[tile].w - 2,
						this.tile[tile].h - 2, LColor.white,
						MathUtils.toDegrees(this.target[num3].rot),
						(float) (this.tile[tile].w / 2),
						(float) (this.tile[tile].h / 2), none);
			}
		}
		for (num3 = 0; num3 < this.max_shots; num3++) {
			if ((this.shot[num3].active == 1) && (this.shot[num3].wait < 1)) {
				this.draw_tile(batch, this.ammo[this.shot[num3].ammo].tile,
						this.shot[num3].x, this.shot[num3].y,
						this.shot[num3].rot, 255f);
			}
		}
		if (this.me.active == 1) {
			tile = this.me.tile;
			x = this.me.x;
			y = this.me.y;
			if (this.me.shaking > 0) {
				int maxValue = this.me.shaking / 2;
				x = (x - (maxValue / 2)) + MathUtils.nextInt(maxValue);
				y = (y - (maxValue / 2)) + MathUtils.nextInt(maxValue);
				batch.draw(this.tiles, x, y, this.tile[tile].w,
						this.tile[tile].h, this.tile[tile].x + 1,
						this.tile[tile].y + 1, this.tile[tile].w - 2,
						this.tile[tile].h - 2, new LColor(200, 200, 200),
						MathUtils.toDegrees(this.me.rot),
						(float) (this.tile[tile].w / 2),
						(float) (this.tile[tile].h / 2), SpriteEffects.None);
			} else {
				batch.draw(this.tiles, x, y, this.tile[tile].w,
						this.tile[tile].h, this.tile[tile].x + 1,
						this.tile[tile].y + 1, this.tile[tile].w - 2,
						this.tile[tile].h - 2, LColor.white,
						MathUtils.toDegrees(this.me.rot),
						(float) (this.tile[tile].w / 2),
						(float) (this.tile[tile].h / 2), SpriteEffects.None);
			}
		}
		if (this.me.active != 0) {
			for (num3 = 0; num3 < 5; num3++) {
				if ((this.me.hp >= ((num3 + 1) * 20)) || (num3 == 0)) {
					this.draw_tile(batch, 20, 20 + (num3 * 15), 0x56, 0f, 255f);
				} else {
					this.draw_tile(batch, 0x15, 20 + (num3 * 15), 0x56, 0f,
							255f);
				}
			}
		}
		for (num3 = 0; num3 < this.max_buttons; num3++) {
			if (this.button[num3].active == 1) {
				tile = this.button[num3].tile;
				if (this.button[num3].select == 1) {
					tile = 8;
				}
				x = this.button[num3].x;
				y = this.button[num3].y;
				batch.draw(this.tiles, x, y, this.tile[tile].w,
						this.tile[tile].h, this.tile[tile].x,
						this.tile[tile].y, this.tile[tile].w,
						this.tile[tile].h, LColor.white);
				if (this.button[num3].ammo >= 0) {
					this.draw_tile(batch,
							this.ammo[this.button[num3].ammo].tile,
							this.button[num3].x + (this.button[num3].w / 2),
							this.button[num3].y + (this.button[num3].h / 2),
							MathUtils.toRadians(135f), 255f);
				} else if (this.button[num3].icon >= 0) {
					this.draw_tile(batch, this.button[num3].icon,
							this.button[num3].x + (this.button[num3].w / 2),
							this.button[num3].y + (this.button[num3].h / 2),
							0f, 255f);
				}
			}
		}
		if (this.begin_frames > 0) {
			if ((this.begin_frames >= 150) && (this.me.active == 0)) {
				str = "GAME START";
			} else if ((this.begin_frames >= 150) && (this.me.active == -1)) {
				str = "GAME START! (OUT OF TIME)";
			} else if ((this.begin_frames >= 150) && (this.me.active == -2)) {
				str = "LEVEL " + this.level + " COMPLETE!";
			} else if (this.begin_frames > 90) {
				str = "Get Ready for Level " + this.level + "..";
			} else {
				str = "" + ((this.begin_frames / 30) + 1);
			}

			Vector2f origin = new Vector2f(
					batch.getFont().stringWidth(str) / 2f);
			batch.drawString(str, this.view_w / 2, this.view_h / 4, origin,
					pools.getColor(0xff, 0xff, 0));

			if ((this.begin_frames > 150) && (this.me.active == -2)) {

				y = (this.view_h / 2) - 0x4b;
				x = (this.view_w / 2) - 0x73;
				str = "Targets Destroyed: " + this.me.kills;
				batch.drawString(str, x, y);
				str = "Time to Complete: " + (this.level_frame_count / 30)
						+ "s";
				y += 40;
				batch.drawString(str, x, y);
				str = "Shots: " + this.me.shots;
				y += 40;
				batch.drawString(str, x, y);
				str = "Hits: " + this.me.hits;
				y += 40;
				batch.drawString(str, x, y);
				str = "Accuracy: " + this.me.accuracy + "%";
				y += 40;
				batch.drawString(str, x, y);
			}
		}

		for (num3 = 0; num3 < this.max_floats; num3++) {
			if (this.floating[num3].active == 1) {
				batch.drawString(this.floating[num3].text,
						this.floating[num3].fx, this.floating[num3].fy, pools
								.getColor(this.floating[num3].color.r
										- this.floating[num3].alpha,
										this.floating[num3].color.g
												- this.floating[num3].alpha,
										this.floating[num3].color.b
												- this.floating[num3].alpha,
										this.floating[num3].alpha));
			}
		}

		if (this.me.active != 0) {

			if ((this.me.score >= this.high_score) && (this.high_score > 0)) {
				batch.drawString("" + this.me.score, 20f, 120f, LColor.green);
				batch.drawString("New High Score!", 230f, 66f, LColor.green);
			} else {
				batch.drawString("" + this.me.score, 20f, 120f, LColor.white);
				batch.drawString("High Score: " + this.high_score, 230f, 66f,
						LColor.white);
			}
			if (this.me.streak > 0) {
				str = "x " + (this.me.streak + 1);
				batch.drawString(str, 20f, 170f, LColor.orange);
			}
		}
	}

	@Override
	public void loadContent() {
		this.bg_1 = new bg_main();
		this.tile = new tile_struct[this.tile_rows * this.tile_cols];
		for (int i = 0; i < tile.length; i++) {
			tile[i] = new tile_struct();
		}
		this.floating = new float_struct[this.max_floats];
		for (int i = 0; i < floating.length; i++) {
			floating[i] = new float_struct();
		}
		this.block = new block_struct[this.map_w][this.map_h];
		for (int j = 0; j < map_h; j++) {
			for (int i = 0; i < map_w; i++) {
				block[i][j] = new block_struct();
			}
		}
		this.me = new me_struct();
		this.target = new target_struct[this.max_targets];
		for (int i = 0; i < target.length; i++) {
			target[i] = new target_struct();
		}
		this.shot = new shot_struct[this.max_shots];
		for (int i = 0; i < shot.length; i++) {
			shot[i] = new shot_struct();
		}
		this.smoke = new smoke_struct[this.max_smoke];
		for (int i = 0; i < smoke.length; i++) {
			smoke[i] = new smoke_struct();
		}
		this.ammo = new ammo_struct[this.max_ammo];
		for (int i = 0; i < ammo.length; i++) {
			ammo[i] = new ammo_struct();
		}
		this.button = new button_struct[this.max_buttons];
		for (int i = 0; i < button.length; i++) {
			button[i] = new button_struct();
		}
		this.init_vars();
		this.load_game();
		this.reset_data();
		this.gen_map();
		this.me.angle = 90f;
		this.me.speed_y = 0f;
		this.me.active = 0;
		this.me.score = 0;

		this.bg_1.Initialize("assets/bg_1.png", LSystem.viewSize.getWidth(), -1);
		this.tiles = LTextures.loadTexture("assets/tiles.png");

		// 缓存需要的字符串
		LSTRDictionary
				.bind(getFont(),
						"﻿pacor.ts;ublifnmkge_{	dvyzw()C=h}SBV2LTx[]D0<+,IPW/1*U-3>GERFKM5948!67&\"N|%j:@OHAXY");
	}

	@Override
	public void unloadContent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pressed(GameTouch e) {

		this.pressed = 1;

	}

	@Override
	public void released(GameTouch e) {

		this.released = 1;
		if ((this.pressed == 1) && (this.released == 1)) {
			int num2;
			int num3 = 0;
			int index = 0;
			while (index < this.max_buttons) {
				if ((((this.button[index].active == 1) && ((e.x()) >= this.button[index].x)) && (((e
						.x()) < (this.button[index].x + this.button[index].w)) && ((e
						.y()) >= this.button[index].y)))
						&& ((e.y()) < (this.button[index].y + this.button[index].h))) {
					num3 = 1;
					this.pressed = this.released = 0;
					if (this.button[index].ammo >= 0) {
						this.me.ammo = this.button[index].ammo;
						num2 = 0;
						while (num2 < this.max_buttons) {
							this.button[num2].select = 0;
							num2++;
						}
						this.button[index].select = 1;
					}
					if (index == (this.max_buttons - 1)) {
						if (this.paused == 1) {
							this.paused = 0;
							this.button[index].icon = 7;
						} else {
							this.paused = 1;
							this.button[index].icon = 0x11;
						}
					}
				}
				index++;
			}
			if (((num3 != 1) && (this.begin_frames > 150))
					&& (this.begin_frames < 0x1b3)) {
				this.begin_frames = 150;
			}
			if ((((this.paused == 0) && (this.me.shot_wait < 1)) && ((this.me.active == 1) && (this.pressed == 1)))
					&& (this.released == 1)) {
				this.add_smoke(10, 0x9b, e.x(), e.y(), 0f, 0f);
				float sangle = this.get_angle((float) this.me.x,
						(float) this.me.y, e.x(), e.y());
				for (index = 0; index < this.ammo[this.me.ammo].burst; index++) {
					num2 = this.shoot(this.me.ammo, sangle, this.me.x,
							this.me.y, index * 5);
					if (num2 >= 0) {
						this.shot[num2].start_x = this.me.x;
						this.shot[num2].start_y = this.me.y;
						this.shot[num2].source = this.max_targets;
					}
				}
				this.me.shot_wait += this.ammo[this.me.ammo].wait;
			}
		}
		this.pressed = this.released = 0;

	}

	@Override
	public void drag(GameTouch e) {
		if ((this.paused == 0) && (this.me.active == 1)) {
			float posX = me.position.x + getTouchDX();
			float posY = me.position.y + getTouchDY();
			if (LSystem.viewSize.contains(posX, posY)) {
				this.me.position.set(posX, posY);
				this.me.x = this.me.position.x();
				this.me.y = this.me.position.y();
				this.me.trot = MathUtils.toRadians(45f * (e.x() / 5f));
				if (((this.me.position.x < -1f) || (this.me.position.x > 1f))
						|| ((this.me.position.y < -1f) || (this.me.position.y > 1f))) {
					this.pressed = 0;
					this.released = 0;
				}
				if (this.me.x < this.me.w) {
					this.me.x = this.me.w;
				}
				if (this.me.x > (((this.view_w - 1) - this.me.w) - this.me.w)) {
					this.me.x = ((this.view_w - 1) - this.me.w) - this.me.w;
				}
				if (this.me.y < this.me.h) {
					this.me.y = this.me.h;
				}
				if (this.me.y > ((this.view_h - 1) - this.me.h)) {
					this.me.y = (this.view_h - 1) - this.me.h;
				}
				this.me.position.x = this.me.x;
				this.me.position.y = this.me.y;
			}
		}
	}

	@Override
	public void move(GameTouch e) {
	}

	public LTransition onTransition() {
		return LTransition.newArc();
	}

	@Override
	public void pressed(GameKey e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void released(GameKey e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(GameTime gameTime) {
		if(!isOnLoadComplete()){
			return;
		}
		this.bg_1.Update(this.me.speed_x);
		this.get_input();
		if (this.paused == 0) {
			this.move_player();
			this.move_targets();
			this.move_shots();
			this.move_smoke();
			this.move_floats();
			this.update_level();
			this.frame++;
			if (this.frame >= 30) {
				this.frame = 0;
			}
		}

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

}