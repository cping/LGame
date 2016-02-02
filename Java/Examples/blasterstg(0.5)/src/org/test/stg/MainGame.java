package org.test.stg;

import loon.BaseIO;
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.LTransition;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.action.sprite.painting.DrawableScreen;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.canvas.LColorPool;
import loon.event.GameKey;
import loon.event.GameTouch;
import loon.event.LTouchCollection;
import loon.event.LTouchLocation;
import loon.event.SysInputFactory;
import loon.event.SysKey;
import loon.event.SysTouch;
import loon.font.LFont;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.CollectionUtils;
import loon.utils.LIterator;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.timer.GameTime;

public class MainGame extends DrawableScreen {

	private ai_struct[] ai;
	public final int AI_8 = 4;
	public final int AI_CIRCLE = 7;
	public final int AI_FALL = 8;
	public final int AI_FOLLOW = 6;
	public final int AI_LINES_X = 2;
	public final int AI_LINES_Y = 3;
	public final int AI_RANDOM = 0;
	public final int AI_TRIANGLE = 5;
	public final int AI_ZIGZAG = 1;
	public int ais = 10;
	private ammo_struct[] ammo;
	public final int AMMO_BALL = 0;
	public final int AMMO_BOMB = 7;
	public final int AMMO_BURST = 6;
	public final int AMMO_ELECTRIC = 8;
	public final int AMMO_EXPLODE = 11;
	public final int AMMO_EXPLODE_MISSILE = 10;
	public final int AMMO_FIRE = 5;
	public final int AMMO_LASER = 2;
	public final int AMMO_MISSILE = 9;
	public final int AMMO_NPC_BALL = 15;
	public final int AMMO_NPC_BURST = 0x10;
	public final int AMMO_NPC_FIRE = 13;
	public final int AMMO_NPC_ROCKET = 12;
	public final int AMMO_NPC_SPRAY = 0x11;
	public final int AMMO_NPC_WIDE = 14;
	public final int AMMO_ROCKET = 1;
	public final int AMMO_SPREAD = 3;
	public int ammo_start = 0;
	public final int AMMO_WIDE = 4;
	public int ammos = 20;
	public int ani_dur = 6;
	public int ani_frame = 0;

	private shot_random random = new shot_random();

	public final int BTN_BEGIN = 5;
	public final int BTN_EXIT = 1;
	public final int BTN_HELP = 11;
	public final int BTN_LEVEL = 3;
	public final int BTN_MUSIC = 8;
	public final int BTN_NEXTLEVEL = 9;
	public final int BTN_NONE = 0;
	public final int BTN_PAUSE = 2;
	public final int BTN_RESUME = 4;
	public final int BTN_RETURN = 6;
	public final int BTN_REVIEW = 10;
	public final int BTN_SETTINGS = 12;
	public final int BTN_SOUND = 7;
	private button_struct[] button;
	public int buttons = 20;
	public int click = 0;
	public Vector2f click_down = new Vector2f();
	public int click_dur;
	public Vector2f click_last = new Vector2f();
	public Vector2f click_move = new Vector2f();
	public Vector2f click_origin = new Vector2f();
	public int click_x;
	public int click_y;
	public final int DIFF_EASY = 0;
	public final int DIFF_HARD = 2;
	public final int DIFF_NORMAL = 1;
	public final int dur_intro = 150;
	public final int EFF_EXHAUST = 4;
	public final int EFF_FLASH = 0;
	public final int EFF_PIECES = 2;
	public final int EFF_SMOKE = 1;
	public final int EFF_SPARKLE = 3;
	public int FADE_IN = 1;
	public int FADE_IN_OUT = 3;
	public int FADE_NONE = 0;
	public int FADE_OUT = 2;
	private float_number_struct[] float_number;
	public int float_numbers = 50;
	public int fn_accuracy;
	public int fn_score;
	public int fn_streak;
	private LFont font;
	public int frame = 0;

	public int got_weapon;

	public final int GRID_H = 0x19;
	public final int GRID_SIZE = 32;
	public final int GRID_W = 15;
	public int group_shots = 0x2d;
	public int held_down;
	public int high_score;
	public int hold = 0;
	public int hold_mode = -1;

	public int last_trail = 0;
	private level_struct[] level;
	public int level_scroll;
	public int levels = 7;
	public int lv_marathon = 6;
	private me_struct me;
	public int mode;
	public int mode_fade = 0;
	public int mode_frame = 0;
	public final int MODE_GAMEOVER = 7;
	public final int MODE_HELP = 9;
	public final int MODE_INTRO = 2;
	public final int MODE_LEVEL = 6;
	public final int MODE_MAIN = 3;
	public final int MODE_NONE = 0;
	public final int MODE_PAUSE = 4;
	public final int MODE_PLAY = 1;
	public final int MODE_SETTINGS = 10;
	public final int MODE_STARTUP = 5;
	public final int MODE_WIN = 8;

	public int music_off;
	public int next_save;
	private npc_struct[] npc;
	public final int NPC_ARMORED = 3;
	public final int NPC_BLOCK = 4;
	public final int NPC_BOOST = 5;
	public final int NPC_BUZZER = 1;
	public int npc_count;
	public final int NPC_DRILLER = 8;
	public final int NPC_NONE = 0;
	public final int NPC_SPEEDER = 6;
	public final int NPC_SPINNER = 9;
	public final int NPC_TANK = 7;
	public final int NPC_ZOOMER = 2;
	public int npcs = 50;
	public int[] number_tile;
	private object_struct[] obj;
	public int objects = 200;
	public int pause_frame;
	public int paused_fade;
	public int paused_hold;
	public int paused_hold_mode;
	public int paused_mode;
	public final int PLAY_H = 0x2d1;
	public final int PLAY_W = 480;
	public final int PLAY_X = 0;
	public final int PLAY_Y = 0x4f;
	public int prog_frame;

	public int return_mode;
	private reward_struct[] reward;
	public int rewards = 20;
	private shot_struct[] shot;
	public int[][] shot_group;
	public int shots = 0x4b;
	public boolean show_ad = true;

	public final int SND_AMMO_BALL = 14;
	public final int SND_AMMO_BURST = 10;
	public final int SND_AMMO_ELECTRIC = 0x12;
	public final int SND_AMMO_FIRE = 15;
	public final int SND_AMMO_LASER = 9;
	public final int SND_AMMO_MINE = 13;
	public final int SND_AMMO_MISSILE = 12;
	public final int SND_AMMO_ROCKET = 11;
	public final int SND_AMMO_SPREAD = 0x10;
	public final int SND_AMMO_WIDE = 0x11;
	public final int SND_CLICK = 2;
	public final int SND_COMBO = 20;
	public final int SND_EXPLODE_ME = 8;
	public final int SND_EXPLODE_MEDIUM = 5;
	public final int SND_EXPLODE_NPC = 4;
	public final int SND_EXPLODE_SMALL = 6;
	public final int SND_HIGH_SCORE = 7;
	public final int SND_HURT_ME = 0;
	public final int SND_HURT_NPC = 1;
	public final int SND_POWER_UP = 3;
	public final int SND_SHIELD_HIT = 0x13;
	public int sound_off;
	public int sounds = 30;

	private LTexture[] texture;
	public final int TEXTURE_BG = 5;
	public final int TEXTURE_CLOUD = 4;
	public final int TEXTURE_GAMEOVER = 6;
	public final int TEXTURE_INTRO = 2;
	public final int TEXTURE_NUMBERS = 1;
	public final int TEXTURE_OVERLAY = 3;
	public final int TEXTURE_STARS = 8;
	public final int TEXTURE_TILES = 0;
	public final int TEXTURE_WIN = 7;
	public final int textures = 9;
	private tile_struct[] tile;
	public int tiles;
	public final int TRAIL_COLOR = 2;
	public final int TRAIL_ELECTRIC = 3;
	public final int TRAIL_SMOKE = 1;
	public int ui_charge_x = 0x2d;
	public int ui_charge_y = 0x2f3;

	public float wave_total;
	public int waves;
	public int win_bonus;
	public String win_bonus_text;
	public int win_bonuses;
	public int win_end_score;
	public int win_new_reward;
	public int win_reward;
	private LColorPool Pool = new LColorPool();

	public MainGame() {
		SysInputFactory.startTouchCollection();
		this.font = LFont.getFont(25);
		this.load_tiles();
		this.load_ais();
		this.init_data();
		this.load_ammo();
		this.change_mode(5);
		this.load_data();

	}

	public void add_ammo(int a, int num) {
		int index = a;
		this.ammo[index].shots = num;
		if (this.ammo[index].shot == null) {
			this.ammo[index].shot = new shot_struct[this.ammo[index].shots];
		}
		for (int i = 0; i < num; i++) {
			if (this.ammo[index].shot[i] == null) {
				this.ammo[index].shot[i] = new shot_struct();
			}
			this.ammo[index].shot[i].obj = new object_struct();
			this.ammo[index].shot[i].obj.loc = new loc_struct();
			this.ammo[index].shot[i].obj.dest = new loc_struct();
			this.ammo[index].reload = 8;
			this.ammo[index].shots_counted = 1f;
			this.ammo[index].shot[i].target_npc = -1;
			this.ammo[index].shot[i].damage = 1f;
			this.ammo[index].shot[i].expend = 1;
			this.ammo[index].shot[i].obj.loc.color = LColor.white;
			this.ammo[index].shot[i].obj.loc.alpha = 0xff;
			this.ammo[index].shot[i].obj.loc.scale = 1f;
			this.ammo[index].shot[i].obj.loc.speed = 10f;
			this.ammo[index].shot[i].obj.loc.turn_speed = 20f;
			this.ammo[index].shot[i].obj.loc.lock_rot = 1;
			this.ammo[index].shot[i].obj.loc.dur = -1f;
			this.ammo[index].shot[i].obj.loc.show_rot = 1;
			this.ammo[index].shot[i].obj.loc.fw = this.ammo[index].shot[i].obj.loc.w = this.tile[this.ammo[index].shot[i].obj.tile].w;
			this.ammo[index].shot[i].obj.loc.fh = this.ammo[index].shot[i].obj.loc.h = this.tile[this.ammo[index].shot[i].obj.tile].h;
			this.ammo[index].shot[i].obj.dest = this.shot[index].obj.loc
					.cpy();
		}
	}

	public int add_button(int x, int y, int type, int layers, int tile_base) {
		int num3 = -1;
		for (int i = 0; i < this.buttons; i++) {
			if (this.button[i].active != 1) {
				num3 = i;
				this.button[i] = new button_struct();
				this.button[i].loc = new loc_struct();
				this.button[i].dest = new loc_struct();
				this.button[i].active = 1;
				this.button[i].type = type;
				this.button[i].loc.color = LColor.newWhite();
				this.button[i].loc.fx = this.button[i].loc.x = x;
				this.button[i].loc.fy = this.button[i].loc.y = y;
				this.button[i].loc.alpha = 0xff;
				this.button[i].loc.dur = -1f;
				this.button[i].loc.scale = 1f;
				this.button[i].loc.speed = 5f;
				this.button[i].loc.slow_dist = 150f;
				this.button[i].dest = this.button[i].loc.cpy();
				if ((layers == 0) && (tile_base == 0)) {
					layers = 2;
				}
				if (layers == 0) {
					layers = 1;
				}
				this.button[i].tiles = layers;
				this.button[i].tile = new int[this.button[i].tiles];
				this.button[i].tile_color = new LColor[this.button[i].tiles];
				int index = 0;
				while (index < layers) {
					this.button[i].tile_color[index] = LColor.white;
					index++;
				}
				if (tile_base == 0) {
					index = 0;
					this.button[i].tile[index] = 20;
					this.button[i].tile_color[index] = LColor.orange;
					index = 1;
					this.button[i].tile[index] = 0x15;
					this.button[i].tile_color[index] = LColor.lightBlue;
				} else {
					index = 0;
					this.button[i].tile[index] = tile_base;
					this.button[i].tile_color[index] = LColor.white;
				}
				this.button[i].loc.sw = this.button[i].loc.fw = this.button[i].loc.w = this.tile[this.button[i].tile[0]].w;
				this.button[i].loc.sh = this.button[i].loc.fh = this.button[i].loc.h = this.tile[this.button[i].tile[0]].h;
				return num3;
			}
		}
		return num3;
	}

	public void add_effect(int x, int y, float angle, float speed, int dur,
			float scale, LColor col, int alpha, int type) {
		int num;
		int num2;
		int num4;
		int num5;
		int num6;
		Vector2f vector = new Vector2f();
		if (type == 0) {
			vector = this.get_location(x, y, angle, speed * dur);
			for (num2 = 0; num2 <= 1; num2++) {
				num = 0x1b;
				num5 = this.tile[num].w / 2;
				num6 = this.tile[num].h / 2;
				num4 = this.add_object(x, y, num);
				if (num4 >= 0) {
					this.obj[num4].loc.dur = dur;
					this.obj[num4].loc.alpha_hold = ((int) this.obj[num4].loc.dur) - 5;
					this.obj[num4].loc.alpha = alpha;
					this.obj[num4].dest.fx = vector.x;
					this.obj[num4].dest.fy = vector.y;
					this.obj[num4].loc.rot = angle;
					if (!(speed == 0f)) {
						this.obj[num4].loc.lock_rot = 1;
					}
					this.obj[num4].loc.scale = scale * 0.25f;
					this.obj[num4].dest.alpha = 0;
					this.obj[num4].loc.speed = speed;
					this.obj[num4].cx = num5;
					this.obj[num4].cy = num6;
					this.obj[num4].loc.spin = random.Next(0, 360);
					this.obj[num4].loc.spin_speed = -20 + random.Next(0, 0x29);
					switch (num2) {
					case 0:
						this.obj[num4].loc.color = col;
						this.obj[num4].dest.scale = scale * 2f;
						break;

					case 1:
						this.obj[num4].loc.color = LColor.white;
						this.obj[num4].dest.scale = scale;
						break;
					}
				}
			}
		} else if (type == 4) {
			vector = this.get_location(x, y, angle, speed * dur);
			num = 80;
			num5 = this.tile[num].w / 2;
			num6 = this.tile[num].h / 2;
			num4 = this.add_object(x, y, num);
			if (num4 >= 0) {
				this.obj[num4].loc.dur = dur;
				this.obj[num4].loc.alpha_hold = ((int) this.obj[num4].loc.dur) / 2;
				this.obj[num4].loc.alpha = alpha;
				this.obj[num4].dest.fx = vector.x;
				this.obj[num4].dest.fy = vector.y;
				this.obj[num4].loc.scale = scale * 0.5f;
				this.obj[num4].dest.alpha = 0;
				this.obj[num4].loc.speed = speed;
				this.obj[num4].cx = num5;
				this.obj[num4].cy = num6;
				this.obj[num4].loc.spin = random.Next(0, 360);
				this.obj[num4].loc.spin_speed = -20 + random.Next(0, 0x29);
				this.obj[num4].loc.color = col;
				this.obj[num4].dest.scale = scale * 1f;
			}
		} else if (type == 3) {
			vector = this.get_location(x, y, angle, speed * dur);
			for (num2 = 0; num2 <= 1; num2++) {
				num = 0x1b;
				num5 = this.tile[num].w / 2;
				num6 = this.tile[num].h / 2;
				num4 = this.add_object(x, y, num);
				if (num4 >= 0) {
					this.obj[num4].loc.dur = dur;
					this.obj[num4].loc.scale_hold = ((int) this.obj[num4].loc.dur) - 5;
					this.obj[num4].loc.alpha = alpha;
					this.obj[num4].dest.alpha = 0xff;
					this.obj[num4].dest.scale = 0f;
					this.obj[num4].dest.fx = vector.x;
					this.obj[num4].dest.fy = vector.y;
					this.obj[num4].loc.lock_rot = 0;
					this.obj[num4].loc.speed = speed;
					this.obj[num4].loc.rot = angle;
					this.obj[num4].loc.lock_rot = 1;
					this.obj[num4].dest.scale = scale * 0.25f;
					this.obj[num4].cx = num5;
					this.obj[num4].cy = num6;
					this.obj[num4].loc.spin = random.Next(0, 360);
					this.obj[num4].loc.spin_speed = -20 + random.Next(0, 0x29);
					switch (num2) {
					case 0:
						this.obj[num4].loc.color = col;
						break;

					case 1:
						this.obj[num4].loc.scale = scale * 0.25f;
						this.obj[num4].loc.color = LColor.white;
						break;
					}
				}
			}
		} else {
			int num7;
			float num10;
			if (type == 1) {
				num7 = 3;
				num2 = 0;
				num10 = 0f;
				while (num2 < num7) {
					num10 = (angle - 60f) + random.Next(0, 0x79);
					vector = this.get_location(x, y, num10, random.Next(
							(int) (30f * scale), (int) (120f * scale)));
					num = 0x1b;
					num5 = this.tile[num].w / 2;
					num6 = this.tile[num].h / 2;
					num4 = this.add_object(x, y, num);
					if (num4 >= 0) {
						this.obj[num4].loc.speed = random.Next(
								(int) (2f * scale), (int) (5f * scale));
						this.obj[num4].loc.dur = dur;
						this.obj[num4].loc.alpha_hold = ((int) this.obj[num4].loc.dur) / 2;
						this.obj[num4].loc.alpha = alpha;
						this.obj[num4].dest.fx = vector.x;
						this.obj[num4].dest.fy = vector.y;
						this.obj[num4].loc.scale = scale * 0.5f;
						this.obj[num4].dest.alpha = 0;
						this.obj[num4].cx = num5;
						this.obj[num4].cy = num6;
						this.obj[num4].loc.spin = random.Next(0, 360);
						this.obj[num4].loc.spin_speed = -20
								+ random.Next(0, 0x29);
						this.obj[num4].loc.color = col;
						this.obj[num4].dest.scale = scale * 1.5f;
					}
					num2++;
				}
			} else if (type == 2) {
				num7 = 6;
				num2 = 0;
				num10 = 0f;
				while (num2 < num7) {
					num10 = (angle - 60f) + random.Next(0, 0x79);
					vector = this.get_location(x, y, num10, random.Next(
							(int) (130f * scale), (int) (230f * scale)));
					num = 0x22;
					num5 = this.tile[num].w / 2;
					num6 = this.tile[num].h / 2;
					num4 = this.add_object(x, y, num);
					if (num4 >= 0) {
						this.obj[num4].loc.speed = random.Next(
								(int) (4f * scale), (int) (10f * scale));
						this.obj[num4].loc.dur = dur;
						this.obj[num4].loc.scale_hold = 5;
						this.obj[num4].loc.alpha = alpha;
						this.obj[num4].dest.fx = vector.x;
						this.obj[num4].dest.fy = vector.y;
						this.obj[num4].loc.scale = (random.Next(
								(int) (3f * scale), (int) (10f * scale))) / 10f;
						this.obj[num4].dest.scale = 0f;
						this.obj[num4].cx = num5;
						this.obj[num4].cy = num6;
						this.obj[num4].loc.spin = random.Next(0, 360);
						this.obj[num4].loc.spin_speed = -20
								+ random.Next(0, 0x29);
						this.obj[num4].loc.color = col;
					}
					num2++;
				}
			}
		}
	}

	public int add_float_number(int x, int y, LColor color, int center,
			float scale, float dur, int num) {
		int num3 = -1;
		if (num >= 0) {
			for (int i = 0; i < this.float_numbers; i++) {
				if (this.float_number[i].active != 1) {
					num3 = i;
					this.float_number[i] = new float_number_struct();
					this.float_number[i].loc = new loc_struct();
					this.float_number[i].dest = new loc_struct();
					this.float_number[i].active = 1;
					this.float_number[i].center = center;
					this.float_number[i].count = 0;
					this.float_number[i].number = num;
					this.float_number[i].num_tile = new int[20];
					this.float_number[i].loc.color = color;
					this.float_number[i].loc.fx = this.float_number[i].loc.x = x;
					this.float_number[i].loc.fy = this.float_number[i].loc.y = y;
					this.float_number[i].loc.alpha = 0xff;
					this.float_number[i].loc.dur = dur;
					this.float_number[i].loc.scale = scale;
					this.float_number[i].loc.speed = 5f;
					this.float_number[i].loc.slow_dist = 150f;
					this.float_number[i].dest = this.float_number[i].loc
							.cpy();
					this.format_number(i, num);
					return num3;
				}
			}
		}
		return num3;
	}

	public int add_npc(int x, int y, int npc_type, float npc_mod, int ai_type,
			int ai_start, int h, int t1, int t2) {
		int num2 = -1;
		for (int i = 0; i < this.npcs; i++) {
			if (this.npc[i].active != 1) {
				num2 = i;
				this.npc[i] = new npc_struct();
				this.npc[i].active = 1;
				this.npc[i].ai_speed = 1f;
				this.npc[i].ai_dir = 1;
				this.npc[i].ai_npc = -1;
				this.npc[i].ai = ai_type;
				this.npc[i].ai_way = ai_start;
				this.npc[i].obj.loc.fx = this.npc[i].obj.loc.x = x;
				this.npc[i].obj.loc.fy = this.npc[i].obj.loc.y = y;
				this.npc[i].obj.active = 1;
				this.npc[i].type = npc_type;
				this.npc[i].mod = npc_mod;
				this.npc[i].obj.loc.hold = h;
				this.npc[i].kill_time = 90;
				this.set_npc(i, this.npc[i].type, this.npc[i].mod, t1, t2);
				this.goto_way(i, this.npc[i].ai_way);
				this.npc[i].obj.loc.rot = this.npc[i].obj.dest.rot;
				return num2;
			}
		}
		return num2;
	}

	public int add_object(int x, int y, int t) {
		int num2 = -1;
		for (int i = 0; i < this.objects; i++) {
			if (this.obj[i].active != 1) {
				num2 = i;
				this.obj[i] = new object_struct();
				this.obj[i].loc = new loc_struct();
				this.obj[i].dest = new loc_struct();
				this.obj[i].active = 1;
				this.obj[i].tile = t;
				this.obj[i].loc.fx = this.obj[i].loc.x = x;
				this.obj[i].loc.fy = this.obj[i].loc.y = y;
				this.obj[i].loc.color = LColor.white;
				this.obj[i].loc.alpha = 0xff;
				this.obj[i].loc.scale = 1f;
				this.obj[i].loc.speed = 5f;
				this.obj[i].loc.dur = -1f;
				this.obj[i].loc.slow_dist = 150f;
				this.obj[i].dest = this.obj[i].loc.cpy();
				return num2;
			}
		}
		return num2;
	}

	public int add_shot(int x, int y, float angle, float scale, int type,
			int type_num, int by_player) {
		int num3 = -1;
		for (int i = 0; i < this.shots; i++) {
			if (this.shot[i].active != 1) {
				num3 = i;
				this.shot[i] = new shot_struct();
				this.shot[i].obj = new object_struct();
				this.shot[i].obj.loc = new loc_struct();
				this.shot[i].obj.dest = new loc_struct();
				this.shot[i] = this.ammo[type].shot[type_num].cpy();
				this.shot[i].active = 1;
				this.shot[i].by_player = by_player;
				this.shot[i].target_npc = -1;
				this.shot[i].last_npc = -1;
				this.shot[i].obj.loc.rot = angle
						+ this.ammo[type].shot[type_num].obj.loc.rot;
				this.shot[i].obj.loc.scale = scale;
				this.shot[i].obj.loc.sw = this.shot[i].obj.loc.fw * scale;
				this.shot[i].obj.loc.sh = this.shot[i].obj.loc.fh * scale;
				this.shot[i].obj.loc.fx = this.shot[i].obj.loc.x = x;
				this.shot[i].obj.loc.fy = this.shot[i].obj.loc.y = y;
				this.shot[i].obj.dest = this.shot[i].obj.loc.cpy();
				this.shot[i].obj.dest.speed = this.ammo[type].shot[type_num].obj.dest.speed;
				return num3;
			}
		}
		return num3;
	}

	public void change_mode(int cmode) {
		int num;
		if (cmode == 4) {
			this.paused_hold_mode = this.hold_mode;
			this.paused_mode = this.mode;
			this.paused_hold = this.hold;
			this.paused_fade = this.mode_fade;
		}
		this.return_mode = this.mode;
		this.mode = cmode;
		this.hold = 0;
		this.hold_mode = 0;
		this.mode_fade = this.FADE_NONE;
		this.mode_frame = 0;
		this.show_ad = true;
		for (num = 0; num < this.buttons; num++) {
			this.button[num].active = 0;
		}
		if (this.mode == 5) {
			this.show_ad = false;
			this.hold = 15;
			this.hold_mode = 2;
		} else if (this.mode == 2) {
			this.show_ad = false;
			this.hold = 150;
			this.hold_mode = 3;
			this.mode_fade = this.FADE_IN_OUT;
		} else {
			int num2;
			if (this.mode == 10) {
				this.show_ad = false;
				this.mode_fade = this.FADE_IN;
				this.add_button(-25, 550, 6, 1, 0xd1);
				num2 = this.add_button(10, 130, 7, 1, 200);
				if (num2 >= 0) {
					if (this.sound_off == 1) {
						this.button[num2].val = 0;
						this.button[num2].tile[0] = 0xca;
					} else {
						this.button[num2].val = 1;
					}
				}
				num2 = this.add_button(10, 0x131, 8, 1, 0xc9);
				if (num2 >= 0) {
					if (this.music_off == 1) {
						this.button[num2].val = 0;
						this.button[num2].tile[0] = 0xcb;
					} else {
						this.button[num2].val = 1;
					}
				}
			} else if (this.mode == 9) {
				this.show_ad = false;
				this.mode_fade = this.FADE_IN;
				this.add_button(-25, 660, 6, 1, 0xd1);
				this.add_button(300, 0x2d5, 10, 1, 220);
			} else if (this.mode == 4) {
				this.add_button(-25, 100, 4, 1, 0xd0);
				num2 = this.add_button(-25, 350, 3, 1, 0xcf);
				if (num2 >= 0) {
					this.button[num2].val = this.me.level;
				}
				this.add_button(-25, 450, 6, 1, 0xd1);
				this.add_button(-25, 550, 1, 1, 0xd3);
				num2 = this.add_button(70, 0xe1, 7, 1, 200);
				if (num2 >= 0) {
					if (this.sound_off == 1) {
						this.button[num2].val = 0;
						this.button[num2].tile[0] = 0xca;
					} else {
						this.button[num2].val = 1;
					}
				}
				num2 = this.add_button(0x9b, 0xe1, 8, 1, 0xc9);
				if (num2 >= 0) {
					if (this.music_off == 1) {
						this.button[num2].val = 0;
						this.button[num2].tile[0] = 0xcb;
					} else {
						this.button[num2].val = 1;
					}
				}
			} else if (this.mode == 7) {
				for (num = 0; num < this.float_numbers; num++) {
					this.float_number[num].active = 0;
				}
				this.start_music("music_gameover");
				this.mode_fade = this.FADE_IN;
				num2 = this.add_button(-25, 0xef, 3, 1, 0xcf);
				if (num2 >= 0) {
					this.button[num2].val = this.me.level;
				}
				this.add_button(-25, 0x27f, 6, 1, 0xd1);
				this.add_button(-25, 0x2cf, 1, 1, 0xd3);
				this.add_button(300, 160, 10, 1, 220);
			} else if (this.mode != 8) {
				if (this.mode == 6) {
					for (num = 0; num < this.float_numbers; num++) {
						this.float_number[num].active = 0;
					}
					num2 = this.add_button(20, 20, 3, 2, 150);
					if (num2 >= 0) {
						if (this.me.level == this.lv_marathon) {
							this.button[num2].tile[1] = 0xc7;
							this.button[num2].tile[0] = 0xc7;
						} else {
							this.button[num2].tile[1] = 150;
							this.button[num2].tile[0] = this.level[this.me.level].button;
						}
						this.button[num2].locked = 1;
						this.button[num2].val = this.me.level;
						this.button[num2].loc.scale = 0f;
					}
					this.show_ad = false;
					this.mode_fade = this.FADE_IN;
					num2 = this.add_button(240 - (this.tile[0xd8].w / 2), 210,
							5, 1, 0xd8);
					if (num2 >= 0) {
						this.button[num2].val = 0;
					}
					num2 = this.add_button(240 - (this.tile[0xd9].w / 2),
							0x13b, 5, 1, 0xd9);
					if (num2 >= 0) {
						this.button[num2].val = 1;
					}
					num2 = this.add_button(240 - (this.tile[0xda].w / 2), 420,
							5, 1, 0xda);
					if (num2 >= 0) {
						this.button[num2].val = 2;
					}
					this.add_button(-50, 750, 6, 1, 0xd1);
				} else if (this.mode == 1) {
					if (this.return_mode == 4) {
						this.hold = this.paused_hold;
						this.hold_mode = this.paused_hold_mode;
						this.mode_fade = this.FADE_NONE;
					} else {
						this.start_level(this.me.level);
						this.mode_fade = this.FADE_IN;
						this.hold = 120;
						this.start_music(this.level[this.me.level].music);
					}
				} else if (this.mode == 3) {
					for (num = 0; num < this.float_numbers; num++) {
						this.float_number[num].active = 0;
					}
					this.set_rewards();
					this.start_music("music_main");
					this.show_ad = false;
					int num6 = 0;
					int num7 = 0x69;
					int num4 = 0;
					int num5 = 0;
					for (num = 0; num < this.levels; num++) {
						if (num == this.lv_marathon) {
							num6 += 160;
							num7 += 70;
						}
						num2 = this.add_button((num4 * 160) + num6,
								(num5 * 160) + num7, 3, 2, 150);
						if (num2 >= 0) {
							this.button[num2].val = num;
							if (num == this.lv_marathon) {
								this.button[num2].tile[0] = 0xc7;
								this.button[num2].tile[1] = 0xc7;
							} else {
								this.button[num2].tile[1] = 150;
								if (this.level[num].locked == 0) {
									this.button[num2].tile[0] = this.level[num].button;
								} else {
									this.button[num2].tile[0] = 0x95;
									this.button[num2].locked = 1;
								}
							}
							this.button[num2].loc.scale = 0f;
						}
						num4++;
						if (num4 >= 3) {
							num4 = 0;
							num5++;
						}
					}
					this.add_button(-25, 0x2d1, 11, 1, 210);
					this.add_button((480 - this.tile[0xe1].w) + 0x19, 0x2d1,
							12, 1, 0xe1);
				}
			} else {
				int num3;
				LColor lime;
				String obj2;
				this.start_music("music_win");
				this.mode_fade = this.FADE_IN;
				if ((this.me.level < (this.levels - 1))
						&& ((num2 = this.add_button(0xeb, 0x27f, 9, 1, 0xd4)) >= 0)) {
					this.button[num2].val = this.me.level + 1;
				}
				num2 = this.add_button(0xeb, 0x2cf, 3, 1, 0xcf);
				if (num2 >= 0) {
					this.button[num2].val = this.me.level;
				}
				this.add_button(-50, 0x27f, 6, 1, 0xd1);
				this.add_button(-50, 0x2cf, 1, 1, 0xd3);
				for (num = 0; num < this.float_numbers; num++) {
					this.float_number[num].active = 0;
				}
				this.win_end_score = this.me.score;
				this.win_bonus = this.win_bonuses = 0;
				this.win_bonus_text = "";
				if (this.me.accuracy >= 100f) {
					num3 = (int) (this.win_end_score * 0.1f);
					num3 += (int) (num3 * this.me.rewards.score_increase);
					obj2 = this.win_bonus_text;
					this.win_bonus_text = StringUtils.concat(obj2, "+ ", num3, " for ",
							(int) this.me.accuracy, "% Accuracy\n");
					this.win_bonus += num3;
					this.win_bonuses++;
				} else if (this.me.accuracy >= 90f) {
					num3 = (int) (this.win_end_score * 0.5f);
					num3 += (int) (num3 * this.me.rewards.score_increase);
					obj2 = this.win_bonus_text;
					this.win_bonus_text = StringUtils.concat(obj2, "+ ", num3, " for ",
							(int) this.me.accuracy, "% Accuracy\n");
					this.win_bonus += num3;
					this.win_bonuses++;
				} else if (this.me.accuracy >= 75f) {
					num3 = (int) (this.win_end_score * 0.02f);
					num3 += (int) (num3 * this.me.rewards.score_increase);
					obj2 = this.win_bonus_text;
					this.win_bonus_text = StringUtils.concat(obj2, "+ ", num3, " for ",
							this.me.accuracy, "% Accuracy\n");
					this.win_bonus += num3;
					this.win_bonuses++;
				}
				if (this.me.life >= this.me.life_max) {
					num3 = (int) (this.win_end_score * 0.1f);
					num3 += (int) (num3 * this.me.rewards.score_increase);
					obj2 = this.win_bonus_text;
					this.win_bonus_text = StringUtils.concat(obj2, "+ ", num3,
							" for Life left (3)\n");
					this.win_bonus += num3;
					this.win_bonuses++;
				} else if (this.me.life >= 2f) {
					num3 = (int) (this.win_end_score * 0.03f);
					num3 += (int) (num3 * this.me.rewards.score_increase);
					obj2 = this.win_bonus_text;
					this.win_bonus_text = StringUtils.concat(obj2, "+ ", num3,
							" for Life left (2)\n");
					this.win_bonus += num3;
					this.win_bonuses++;
				}
				this.me.score += this.win_bonus;
				if (this.me.score >= this.level[this.me.level].high_score) {
					lime = LColor.lime;
				} else {
					lime = LColor.yellow;
				}
				this.fn_score = this.add_float_number(0x11c, 0x1df, lime, 0,
						0.75f, -1f, this.me.score);
				this.win_new_reward = 0;
				num3 = -1;
				if (this.me.score >= this.level[this.me.level].high_score) {
					for (num = 0; num < this.level[this.me.level].stars; num++) {
						if (((this.level[this.me.level].star_reward[num] < 0) || (this.level[this.me.level].high_score < this.level[this.me.level].star_score[num]))
								&& ((this.level[this.me.level].star_reward[num] >= 0) && (this.me.score >= this.level[this.me.level].star_score[num]))) {
							this.win_new_reward = 1;
							break;
						}
					}
				}
				if (this.me.score >= this.level[this.me.level].high_score) {
					this.level[this.me.level].high_score = this.me.score;
					if (this.me.difficulty < 1f) {
						this.level[this.me.level].high_score_difficulty = 0;
					} else if (this.me.difficulty == 1f) {
						this.level[this.me.level].high_score_difficulty = 1;
					} else if (this.me.difficulty > 1f) {
						this.level[this.me.level].high_score_difficulty = 2;
					}
				}
				if (this.me.level < (this.levels - 1)) {
					this.level[this.me.level + 1].locked = 0;
				}
				this.save_data();
			}
		}
	}

	public void do_button(int btype, int bval) {

		if (btype == 4) {
			this.mode_fade = this.FADE_NONE;
			this.hold_mode = 1;
			this.hold = 1;
		} else if (btype == 12) {
			this.hold = 20;
			this.mode_fade = this.FADE_OUT;
			this.hold_mode = 10;
		} else if (btype == 11) {
			this.hold = 20;
			this.mode_fade = this.FADE_OUT;
			this.hold_mode = 9;
		} else if (btype == 10) {

		} else if (btype == 9) {
			this.me.level = bval;
			if (bval >= this.levels) {
				this.me.level = this.levels - 1;
			}
			this.hold = 1;
			this.mode_fade = this.FADE_OUT;
			this.hold_mode = 6;
		} else {
			int num;
			if (btype == 8) {
				this.music_off = bval;

				if (this.music_off == 1) {

				} else {

				}
				num = 0;
				while (num < this.buttons) {
					if ((this.button[num].type == 8)
							&& (this.button[num].active == 1)) {
						if (this.music_off == 0) {
							this.button[num].val = 1;
							this.button[num].tile[0] = 0xc9;
						} else {
							this.button[num].val = 0;
							this.button[num].tile[0] = 0xcb;
						}
					}
					num++;
				}
				this.save_data();
			} else if (btype == 7) {
				this.sound_off = bval;
				for (num = 0; num < this.buttons; num++) {
					if ((this.button[num].type == 7)
							&& (this.button[num].active == 1)) {
						if (this.sound_off == 0) {
							this.button[num].val = 1;
							this.button[num].tile[0] = 200;
						} else {
							this.button[num].val = 0;
							this.button[num].tile[0] = 0xca;
						}
					}
				}
				this.save_data();
			} else if (btype == 6) {
				this.hold_mode = 3;
				this.hold = 20;
				this.mode_fade = this.FADE_OUT;
			} else if (btype == 3) {
				if ((this.level[bval].locked == 0)
						|| (bval == this.lv_marathon)) {
					this.me.level = bval;
					this.hold = 20;
					this.mode_fade = this.FADE_OUT;
					this.hold_mode = 6;
				}
			} else if (btype == 5) {
				this.hold_mode = 1;
				this.hold = 20;
				this.mode_fade = this.FADE_OUT;
				if (bval == 0) {
					this.me.difficulty = 0.75f;
				} else if (bval == 1) {
					this.me.difficulty = 1f;
				} else if (bval == 2) {
					this.me.difficulty = 1.35f;
				}
			} else if (btype == 1) {
				LSystem.exit();
			}
		}

	}

	public void end_npc(int n) {
		int num;
		for (num = 0; num < this.shots; num++) {
			if (this.shot[num].target_npc == n) {
				this.shot[num].target_npc = -1;
				this.shot[num].obj.loc.lock_rot = 1;
			}
		}
		num = 0;
		while (num < this.npcs) {
			if ((this.npc[num].active == 1) && (this.npc[num].ai_npc == n)) {
				this.npc[num].ai_npc = -1;
				this.npc[num].ai = 6;
			}
			num++;
		}
		if (this.npc[n].shielding >= 0) {
			if (this.npc[this.npc[n].shielding].shield_count == 1) {
				this.npc[this.npc[n].shielding].shield = 0f;
			}
			this.npc[this.npc[n].shielding].shield_count--;
		}
		int num4 = 0;
		if (((this.me.ammo == 0) && (this.waves > 2)) && (this.got_weapon == 0)) {
			num4 = 1;
		}
		if (((this.npc[n].boost_ammo == -1) && (((random.Next(0, 100) < (5 + ((int) (this.me.rewards.drop_rate * 10f)))) || (this.npc[n].boss == 1)) || (num4 == 1)))
				&& ((num = this.add_npc(random.Next(0x21, getWidth() - 32),
						-700, 5, 1f, 8, 0, 0, 0, 0)) >= 0)) {
			int num3 = random.Next(0, 100);
			if ((num3 < 0x4b) || (num4 == 1)) {
				int num5;
				this.got_weapon = 1;
				do {
					num5 = 1;
					this.npc[num].boost_ammo = random.Next(1, 10);
					if ((this.npc[num].boost_ammo == 2) && (this.me.level < 1)) {
						num5 = 0;
					}
					if ((this.npc[num].boost_ammo == 5) && (this.me.level < 2)) {
						num5 = 0;
					}
					if ((this.npc[num].boost_ammo == 9) && (this.me.level < 3)) {
						num5 = 0;
					}
					if ((this.npc[num].boost_ammo == 7) && (this.me.level < 3)) {
						num5 = 0;
					}
					if ((this.npc[num].boost_ammo == 8) && (this.me.level < 4)) {
						num5 = 0;
					}
				} while (num5 == 0);
				this.npc[num].boost_color = LColor.lightBlue;
				this.npc[num].boost_tile = this.ammo[this.npc[num].boost_ammo].tile;
				this.npc[num].boost_tile_color = this.ammo[this.npc[num].boost_ammo].shot[0].obj.loc.color;
				this.npc[num].boost_label = 90;
			} else if (num3 < 0x55) {
				this.npc[num].boost_color = LColor.lime;
				this.npc[num].boost_power_ball = 1;
				this.npc[num].boost_tile = 0x38;
				this.npc[num].boost_tile_color = LColor.lime;
				this.npc[num].boost_label = 0x59;
			} else {
				this.npc[num].boost_color = LColor.lightCoral;
				this.npc[num].boost_life = 1;
				this.npc[num].boost_tile = 0x44;
				this.npc[num].boost_tile_color = LColor.white;
			}
			this.npc[num].obj.loc.color = LColor.white;
		}
		this.npc[n].active = 0;
	}

	public final void end_shot(int s) {
		this.shot[s].active = 0;
		if (this.shot[s].by_player == 1) {
			if (this.me.shot_count > 0f) {
				this.me.accuracy = (this.me.hits / this.me.shot_count) * 100f;
				this.format_number(this.fn_accuracy, (int) this.me.accuracy);
			}
			for (int i = 0; i < this.shots; i++) {
				for (int j = 0; j < this.group_shots; j++) {
					if (this.shot_group[i][j] == s) {
						this.shot_group[i][j] = -1;
					}
				}
			}
		}
	}

	public void format_number(int fn, int num) {
		float num11 = 0f;
		int h = 0;
		if ((fn >= 0) && (this.float_number[fn].active == 1)) {
			this.float_number[fn].number = num;
			this.float_number[fn].count = 0;
			String str = "" + this.float_number[fn].number;
			if ((str.length() >= 1) && (num >= 0)) {
				int num2;
				int num4;
				int num5;
				for (num2 = str.length() - 1; num2 >= 0; num2--) {
					num4 = Integer.parseInt(str.substring(num2, num2 + 1));

					num5 = this.number_tile[num4];
					num11 += this.tile[num5].w;
					if (this.tile[num5].h > h) {
						h = this.tile[num5].h;
					}
					this.float_number[fn].count++;
				}
				this.float_number[fn].loc.h = h;
				num11 += this.tile[10].w * ((str.length() - 1) / 3);
				this.float_number[fn].count += (str.length() - 1) / 3;

				this.float_number[fn].loc.fw = num11;
				this.float_number[fn].loc.fh = h;
				this.float_number[fn].loc.w = (int) num11;
				int startIndex = str.length() - 1;
				int num3 = 0;
				for (num2 = (str.length() - 1) + ((str.length() - 1) / 3); num2 >= 0; num2--) {
					if (((num3 % 3) == 0) && (num3 > 0)) {
						num5 = 10;
						num3 = 0;
					} else {
						num4 = Integer.parseInt(str.substring(startIndex,
								startIndex + 1));
						num5 = this.number_tile[num4];
						startIndex--;
						num3++;
					}
					this.float_number[fn].num_tile[(this.float_number[fn].count - 1)
							- num2] = num5;
				}
				this.float_number[fn].sw = this.float_number[fn].loc.fw
						* this.float_number[fn].loc.scale;
				this.float_number[fn].sh = this.float_number[fn].loc.fh
						* this.float_number[fn].loc.scale;
			}
		}
	}

	public void game_loop() {
		int num2;
		int num3;
		int x;
		int y;
		int num8;
		int num9;
		int num10;
		int num11;
		float num18;
		RectBox rectangle;
		RectBox rectangle2;
		int[] numArray = new int[this.group_shots];
		Vector2f vector = new Vector2f();

		/*
		 * if ((this.music_off == 1)) { this.do_button(8, 1); }
		 */

		this.npc_count = 0;
		for (num2 = this.buttons - 1; num2 >= 0; num2--) {
			if (this.button[num2].active == 1) {
				if (this.button[num2].loc.dur == 0f) {
					this.button[num2].active = 0;
				} else {
					this.button[num2].loc = this.update_loc(
							this.button[num2].loc, this.button[num2].dest);
					if (this.button[num2].clicked > 0) {
						this.button[num2].clicked--;
						if (this.button[num2].clicked == 0) {
							this.do_button(this.button[num2].type,
									this.button[num2].val);
							break;
						}
					}
					if ((((this.click == 1) && (this.button[num2].locked == 0)) && ((this.click_x >= this.button[num2].loc.fx) && (this.click_x < (this.button[num2].loc.fx + this.button[num2].loc.sw))))
							&& ((this.click_y >= this.button[num2].loc.fy) && (this.click_y < (this.button[num2].loc.fy + this.button[num2].loc.sh)))) {
						this.click = -1;
						if ((this.button[num2].clicked == 0)
								&& (this.button[num2].loc.dur == -1f)) {
							this.button[num2].clicked = 10;
							if (this.tile[this.button[num2].tile[this.button[num2].tiles - 1]].clicked < 1) {
								this.button[num2].loc.scale = 1.25f;
							}
							break;
						}
					}
				}
			}
		}
		if ((this.mode == 2) && ((this.click > 0) && (this.hold > 30))) {
			this.hold = 30;
		}
		if (this.mode != 1) {
			this.last_trail--;
			if (this.last_trail < 0) {
				this.last_trail = 3;
			}
			this.click_move.x = this.click_move.y = 0f;
			return;
		}
		this.level[this.me.level].y += this.level[this.me.level].y_scroll;
		this.me.last_shot++;
		if ((this.fn_score >= 0) && (this.me.score_display != this.me.score)) {
			if ((this.me.score - this.me.score_display) > 0x3e8) {
				this.me.score_display += 0x3e8;
			} else if ((this.me.score - this.me.score_display) > 100) {
				this.me.score_display += 100;
			} else if ((this.me.score - this.me.score_display) > 10) {
				this.me.score_display += 10;
			} else if ((this.me.score - this.me.score_display) > 2) {
				this.me.score_display++;
			} else {
				this.me.score_display = this.me.score;
			}
			this.float_number[this.fn_score].loc.scale = 1f;
			this.format_number(this.fn_score, this.me.score_display);
		}
		num2 = 0;
		while (num2 < this.float_numbers) {
			if (this.float_number[num2].active == 1) {
				if (this.float_number[num2].loc.dur == 0f) {
					this.float_number[num2].active = 0;
				} else {
					this.float_number[num2].loc = this.update_loc(
							this.float_number[num2].loc,
							this.float_number[num2].dest);
					this.float_number[num2].sw = this.float_number[num2].loc.fw
							* this.float_number[num2].loc.scale;
					this.float_number[num2].sh = this.float_number[num2].loc.fh
							* this.float_number[num2].loc.scale;
				}
			}
			num2++;
		}
		for (num2 = 0; num2 < this.objects; num2++) {
			if (this.obj[num2].active == 1) {
				if (this.obj[num2].loc.dur == 0f) {
					this.obj[num2].active = 0;
				} else {
					this.obj[num2].loc = this.update_loc(this.obj[num2].loc,
							this.obj[num2].dest);
				}
			}
		}
		for (num2 = 0; num2 < this.shots; num2++) {
			if (this.shot[num2].active == 1) {
				if ((this.me.dead > 0) && (this.shot[num2].by_player == 1)) {
					this.shot[num2].obj.loc.dur = 0f;
				}
				if ((((this.shot[num2].obj.loc.x < (-this.shot[num2].obj.loc.sw / 2f)) || (this.shot[num2].obj.loc.x >= (480f + (this.shot[num2].obj.loc.sw / 2f)))) || (this.shot[num2].obj.loc.y < 0))
						|| (this.shot[num2].obj.loc.y >= (721f + (this.shot[num2].obj.loc.sh / 2f)))) {
					this.shot[num2].obj.loc.dur = 0f;
					if (this.me.bomb == num2) {
						this.me.bomb = -1;
					}
				}
				if (this.shot[num2].obj.loc.dur == 0f) {
					if (((this.shot[num2].last_npc == -1) && (this.shot[num2].by_player == 1))
							&& (this.shot[num2].bomb != -2)) {
						if (this.me.streak != 0) {
							this.me.streak = 0;
							this.format_number(this.fn_streak, this.me.streak);
						}
						this.me.combo = 0;
						this.me.combo_group = -1;
					}
					this.end_shot(num2);
				} else {
					if (this.shot[num2].trail > 0) {
						if (this.shot[num2].trail == 2) {
							num8 = 1;
						} else if (this.shot[num2].trail == 3) {
							num8 = 1;
						} else {
							num8 = 2;
						}
						if ((this.mode_frame % num8) == 0) {
							vector = this.get_location(
									(float) this.shot[num2].obj.loc.x,
									(float) this.shot[num2].obj.loc.y,
									this.shot[num2].obj.loc.rot - 180f, 0f);
							if (this.shot[num2].trail == 2) {
								num8 = 0x38;
							} else if (this.shot[num2].trail == 3) {
								num8 = 0x51;
							} else {
								num8 = 0x1b;
							}
							num3 = this.add_object((int) vector.x,
									(int) vector.y, num8);
							if (num3 >= 0) {
								this.obj[num3].loc.alpha = this.shot[num2].obj.loc.alpha / 4;
								if (this.shot[num2].trail == 3) {
									this.obj[num3].loc.alpha = random.Next(100,
											0xff);
								}
								if (this.shot[num2].trail == 1) {
									this.obj[num3].loc.alpha = this.shot[num2].obj.loc.alpha / 2;
								}
								this.obj[num3].dest.alpha = 0;
								if (this.shot[num2].trail == 3) {
									this.obj[num3].dest.alpha = random.Next(50,
											100);
								}
								this.obj[num3].loc.scale = this.obj[num3].dest.scale = this.shot[num2].obj.loc.scale;
								if (this.shot[num2].trail == 3) {
									this.obj[num3].dest.scale = 0f;
								}
								this.obj[num3].loc.dur = 10f;
								if (this.shot[num2].trail == 2) {
									this.obj[num3].loc.dur = 5f;
								}
								this.obj[num3].cx = this.tile[this.obj[num3].tile].w / 2;
								this.obj[num3].cy = this.tile[this.obj[num3].tile].h / 2;
								this.obj[num3].loc.spin = random.Next(0, 360);
								this.obj[num3].loc.spin_speed = -10
										+ random.Next(0, 0x15);
								if (this.shot[num2].trail == 2) {
									this.obj[num3].loc.color = this.shot[num2].obj.loc.color;
								} else if (this.shot[num2].trail == 3) {
									this.obj[num3].loc.color = LColor.white;
								} else {
									this.obj[num3].loc.color = LColor.white;
								}
							}
						}
					}
					if ((this.shot[num2].target_npc >= 0)
							&& (this.npc[this.shot[num2].target_npc].active == 1)) {
						this.shot[num2].obj.loc.lock_rot = 0;
						this.shot[num2].obj.dest.fx = this.npc[this.shot[num2].target_npc].obj.loc.fx;
						this.shot[num2].obj.dest.fy = this.npc[this.shot[num2].target_npc].obj.loc.fy;
					}
					this.shot[num2].obj.loc = this.update_loc(
							this.shot[num2].obj.loc, this.shot[num2].obj.dest);
					rectangle = new RectBox(this.shot[num2].obj.loc.x
							- (((int) this.shot[num2].obj.loc.sw) / 2),
							this.shot[num2].obj.loc.y
									- (((int) this.shot[num2].obj.loc.sh) / 2),
							(int) this.shot[num2].obj.loc.sw,
							(int) this.shot[num2].obj.loc.sh);
					if (this.shot[num2].by_player == 1) {
						num3 = 0;
						while (num3 < this.npcs) {
							if (this.npc[num3].active == 1) {
								rectangle2 = new RectBox(
										this.npc[num3].obj.loc.x
												- ((int) (this.npc[num3].obj.loc.sw / 2f)),
										this.npc[num3].obj.loc.y
												- ((int) (this.npc[num3].obj.loc.sh / 2f)),
										(int) this.npc[num3].obj.loc.sw,
										(int) this.npc[num3].obj.loc.sh);
								if (rectangle2.intersects(rectangle)) {
									if (this.shot[num2].last_npc != num3) {
										if (this.npc[num3].shield > 0f) {
											num11 = 0;
											while (num11 < this.group_shots) {
												if ((this.shot_group[num2][num11] >= 0)
														&& (this.shot_group[num2][num11] != num2)) {
													break;
												}
												if ((num11 == (this.group_shots - 1))
														&& (this.shot[num2].last_npc == -1)) {
													this.me.shot_count--;
												}
												num11++;
											}
										} else {
											this.me.hits++;
											if (this.me.shot_count > 0f) {
												this.me.accuracy = (this.me.hits / this.me.shot_count) * 100f;
												this.format_number(
														this.fn_accuracy,
														(int) this.me.accuracy);
											}
										}
									}
									num11 = 0;
									while (num11 < this.group_shots) {
										if (this.shot_group[num2][num11] >= 0) {
											this.shot[this.shot_group[num2][num11]].last_npc = num3;
										}
										num11++;
									}
									num11 = 0;
									if ((this.npc[num3].open_min != 0f)
											&& (this.npc[num3].open_max != 0f)) {
										float num20;
										float num21;
										num11 = 1;
										float num22 = this.shot[num2].obj.loc.rot
												- this.npc[num3].obj.loc.rot;
										if (num22 >= 360f) {
											num22 -= 360f;
										}
										if (num22 < 0f) {
											num22 += 360f;
										}
										if (this.npc[num3].obj.loc.show_rot == 0) {
											num20 = this.npc[num3].open_min;
											num21 = this.npc[num3].open_max;
										} else {
											num20 = this.npc[num3].open_min;
											num21 = this.npc[num3].open_max;
											if (num20 >= 360f) {
												num20 -= 360f;
											}
											if (num20 < 0f) {
												num20 += 360f;
											}
											if (num21 >= 360f) {
												num21 -= 360f;
											}
											if (num21 < 0f) {
												num21 += 360f;
											}
										}
										if (num20 > num21) {
											if ((num22 >= num20)
													|| (num22 <= num21)) {
												num11 = 0;
											}
										} else if ((num22 >= num20)
												&& (num22 <= num21)) {
											num11 = 0;
										}
									}
									int num16 = 1;
									if (this.npc[num3].shield > 0f) {
										num11 = 1;
									}
									if (num11 == 0) {
										num16 = 0;
										if ((this.npc_hurt(num3,
												this.shot[num2].damage,
												this.shot[num2].obj.loc.rot) > 0)
												&& (this.me.dead == 0)) {
											if (this.npc[num3].boost_tile > 0) {
												this.play_sound(3);
												x = y = 0;
												if (this.npc[num3].boost_ammo >= 0) {
													x = 0x23;
													y = 0x2b0;
													this.me.ammo = this.npc[num3].boost_ammo;
												}
												if (this.npc[num3].boost_life > 0) {
													this.me.life += this.npc[num3].boost_life;
													if (this.me.life > this.me.life_max) {
														this.me.life = this.me.life_max;
													}
													x = 80;
													y = 0x2b0;
												}
												if (this.npc[num3].boost_power_ball > 0) {
													this.me.power_ball += this.npc[num3].boost_power_ball;
													if (this.me.power_ball > 2) {
														this.me.power_ball = 2;
													}
													x = this.me.obj.loc.x;
													y = this.me.obj.loc.y;
												}
												vector = this
														.get_location(
																(float) this.npc[num3].obj.loc.x,
																(float) this.npc[num3].obj.loc.y,
																0f, 0f);
												num8 = this
														.add_object(
																(int) vector.x,
																(int) vector.y,
																this.npc[num3].boost_tile);
												if (num8 >= 0) {
													this.obj[num8].dest.fx = x;
													this.obj[num8].dest.fy = y;
													this.obj[num8].loc.speed = 30f;
													this.obj[num8].loc.alpha_hold = 30;
													this.obj[num8].loc.alpha = 200;
													this.obj[num8].dest.alpha = 0;
													this.obj[num8].loc.scale = 2.5f;
													this.obj[num8].dest.scale = 0.4f;
													this.obj[num8].loc.color = this.npc[num3].boost_tile_color;
													this.obj[num8].loc.dur = 30f;
													this.obj[num8].cx = this.tile[this.npc[num3].boost_tile].w / 2;
													this.obj[num8].cy = this.tile[this.npc[num3].boost_tile].h / 2;
												}
											} else {
												int num = this.npc[num3].points
														+ ((int) (this.npc[num3].points * this.me.rewards.score_increase));
												int num13 = num;
												if (num13 > 0) {
													this.me.streak++;
													this.format_number(
															this.fn_streak,
															this.me.streak);
													if (this.me.streak > 1) {
														num8 = (int) ((num * 0.1f) * (this.me.streak - 1));
														num13 += num8;
														num8 = this
																.add_float_number(
																		this.npc[num3].obj.loc.x,
																		this.npc[num3].obj.loc.y,
																		LColor.orange,
																		1,
																		0.25f,
																		45f,
																		num8);
														if (num8 >= 0) {
															this.float_number[num8].dest.fy += -40f;
															this.float_number[num8].dest.scale = 0.5f;
															this.float_number[num8].dest.alpha = 0;
															this.float_number[num8].loc.speed = 10f;
														}
													}
													for (num11 = 0; num11 < this.group_shots; num11++) {
														if (this.shot_group[num2][num11] >= 0) {
															this.shot[this.shot_group[num2][num11]].kills++;
														}
													}
													if (this.shot[num2].kills > 1) {
														num8 = num13
																* this.shot[num2].kills;
														num8 -= num13;
														num13 += num8;
														num8 = this
																.add_float_number(
																		this.npc[num3].obj.loc.x,
																		this.npc[num3].obj.loc.y,
																		LColor.lime,
																		1,
																		0.25f,
																		65f,
																		num8);
														if (num8 >= 0) {
															this.float_number[num8].dest.fy += 50f;
															this.float_number[num8].dest.scale = 1f;
															this.float_number[num8].dest.alpha = 0;
															this.float_number[num8].loc.speed = 10f;
														}
													}
													num8 = this
															.add_float_number(
																	this.npc[num3].obj.loc.x,
																	this.npc[num3].obj.loc.y,
																	LColor.yellow,
																	1, 0.25f,
																	45f, num);
													if (num8 >= 0) {
														this.float_number[num8].dest.fy += 0f;
														this.float_number[num8].dest.scale = 1f;
														this.float_number[num8].dest.alpha = 0;
														this.float_number[num8].loc.speed = 0f;
													}
													this.me.score += num13;
													if ((this.me.score > 0)
															&& (this.me.score >= this.level[this.me.level].high_score)) {
														if ((this.level[this.me.level].high_score_reached == 0)
																&& (this.level[this.me.level].high_score > 0)) {
															num8 = 0;
															while (num8 < 15) {
																this.add_effect(
																		240,
																		360,
																		(float) random
																				.Next(0,
																						360),
																		(float) random
																				.Next(1,
																						3),
																		random.Next(
																				30,
																				60),
																		1f,
																		LColor.lime,
																		random.Next(
																				0x4b,
																				0xff),
																		3);
																this.add_effect(
																		240,
																		360,
																		(float) random
																				.Next(0,
																						360),
																		(float) random
																				.Next(2,
																						5),
																		random.Next(
																				30,
																				60),
																		1f,
																		LColor.silver,
																		MathUtils
																				.nextInt(
																						0x4b,
																						0xff),
																		3);
																num8++;
															}
															num8 = this
																	.add_object(
																			240,
																			360,
																			0x56);
															if (num8 >= 0) {
																this.obj[num8].cx = this.tile[0x56].w / 2;
																this.obj[num8].cy = this.tile[0x56].h / 2;
																this.obj[num8].loc.scale = 0f;
																this.obj[num8].dest.scale = 2f;
																this.obj[num8].loc.alpha_hold = 70;
																this.obj[num8].dest.alpha = 0;
																this.obj[num8].loc.dur = 90f;
																this.obj[num8].loc.hold = 5;
															}
														}
														this.float_number[this.fn_score].loc.color = LColor.lime;
														this.level[this.me.level].high_score_reached = 1;
													}
												}
											}
										}
										this.add_effect(
												this.npc[num3].obj.loc.x,
												this.npc[num3].obj.loc.y, 0f,
												0f, 15, 0.5f,
												this.shot[num2].obj.loc.color,
												0xff, 0);
									} else {
										vector = this
												.get_location(
														(float) this.npc[num3].obj.loc.x,
														(float) this.npc[num3].obj.loc.y,
														this.shot[num2].obj.loc.rot - 180f,
														this.npc[num3].obj.loc.sh / 3f);
										this.add_effect(
												(int) vector.x,
												(int) vector.y,
												(this.shot[num2].obj.loc.rot - 200f)
														+ random.Next(0, 0x29),
												0f, 5, 0.5f, LColor.white,
												0xff, 0);
										this.add_effect(
												(int) vector.x,
												(int) vector.y,
												(this.shot[num2].obj.loc.rot - 200f)
														+ random.Next(0, 0x29),
												8f, 15, 0.65f,
												this.shot[num2].obj.loc.color,
												0xff, 0);
										this.add_effect(
												(int) vector.x,
												(int) vector.y,
												(this.shot[num2].obj.loc.rot - 200f)
														+ random.Next(0, 0x29),
												5f, 15, 0.5f,
												this.shot[num2].obj.loc.color,
												0xff, 0);
										this.add_effect(
												(int) vector.x,
												(int) vector.y,
												this.shot[num2].obj.loc.rot - 180f,
												7f, 15, 0.5f, LColor.white,
												0xff, 0);
										this.play_sound(0x13);
										this.shot[num2].expend = 1;
									}
									if (this.shot[num2].bomb > 0) {
										this.play_sound(this.ammo[this.shot[num2].bomb_ammo].sound);
										num10 = 0;
										while (num10 < this.group_shots) {
											numArray[num10] = -1;
											num10++;
										}
										num8 = 0;
										while (num8 < this.ammo[this.shot[num2].bomb_ammo].shots) {
											vector = this
													.get_location(
															(float) this.npc[num3].obj.loc.x,
															(float) this.npc[num3].obj.loc.y,
															this.shot[num2].obj.loc.rot - 180f,
															(this.npc[num3].obj.loc.sh / 2f)
																	+ (this.shot[num2].obj.loc.sh * num16));
											num10 = this
													.add_shot(
															(int) vector.x,
															(int) vector.y,
															this.shot[num2].obj.loc.rot,
															this.ammo[this.shot[num2].bomb_ammo].shot[num8].obj.loc.scale,
															this.shot[num2].bomb_ammo,
															num8, 1);
											if (num10 >= 0) {
												if (num8 < this.group_shots) {
													numArray[num8] = num10;
												}
												this.shot[num10].bomb = -2;
												this.shot[num10].kills = this.shot[num2].kills;
											}
											num8++;
										}
										num10 = 0;
										while (num10 < this.group_shots) {
											num8 = 0;
											while (num8 < this.group_shots) {
												if (numArray[num10] >= 0) {
													this.shot_group[numArray[num10]][num8] = numArray[num8];
												}
												num8++;
											}
											num10++;
										}
										this.me.bomb = -1;
										this.end_shot(num2);
										for (num10 = 0; num10 < this.shots; num10++) {
											if ((this.shot[num10].active == 1)
													&& (this.shot[num10].bomb == 1)) {
												this.me.bomb = num10;
												break;
											}
										}
										break;
									}
									if ((this.shot[num2].expend == 1)
											|| (this.npc[num3].solid == 1)) {
										this.end_shot(num2);
										break;
									}
								}
							}
							num3++;
						}
					} else {
						rectangle2 = new RectBox(this.me.obj.loc.x - 0x17,
								this.me.obj.loc.y - 0x17, 0x2e, 0x2e);
						if (rectangle2.intersects(rectangle)) {
							if (this.me.shake < 1) {
								this.player_hurt(1, this.shot[num2].obj.loc.rot);
							}
							this.add_effect(this.me.obj.loc.x,
									this.me.obj.loc.y, 0f, 0f, 15, 0.5f,
									this.shot[num2].obj.loc.color, 0xff, 0);
							this.end_shot(num2);
						}
					}
				}
			}
		}
		this.level[this.me.level].pause = 0;
		num2 = 0;
		while (num2 < this.npcs) {
			if (this.npc[num2].active == 1) {
				if (this.npc[num2].boost_tile < 1) {
					this.npc_count++;
				}
				if (this.npc[num2].pause == 1) {
					this.level[this.me.level].pause = 1;
				}
				if (this.npc[num2].obj.loc.dur == 0f) {
					this.npc[num2].active = 0;
					this.end_npc(num2);
				} else {
					if (this.npc[num2].shake > 0) {
						this.npc[num2].shake--;
					}
					if ((this.npc[num2].boost_tile > 0)
							&& ((this.level[this.me.level].frame % 2) == 0)) {
						num8 = 300 + random.Next(0, 0x79);
						vector = this.get_location(this.npc[num2].obj.loc.fx,
								this.npc[num2].obj.loc.fy, (float) num8, 32f);
						this.add_effect((int) vector.x, (int) vector.y, 180f,
								(float) random.Next(0, 2), 30,
								((float) random.Next(5, 0x11)) / 10f,
								this.npc[num2].boost_color,
								random.Next(100, 0x100), 3);
					}
					if (((this.npc[num2].life < this.npc[num2].life_max) && (this.npc[num2].life <= (this.npc[num2].life_max / 3f)))
							&& ((this.level[this.me.level].frame % 3) == 1)) {
						num8 = (((int) this.npc[num2].obj.loc.rot) - 0xb9)
								+ random.Next(0, 11);
						vector = this
								.get_location(
										this.npc[num2].obj.loc.fx,
										this.npc[num2].obj.loc.fy,
										(float) num8,
										(float) (this.tile[this.npc[num2].obj.tile].h / 2));
						this.add_effect((int) vector.x, (int) vector.y, 180f,
								0f, 15, ((float) random.Next(5, 0x11)) / 10f,
								LColor.white, random.Next(100, 0x100), 4);
					}
					if ((this.npc[num2].trail > 0)
							&& ((this.level[this.me.level].frame % 3) == 0)) {
						num8 = (((int) this.npc[num2].obj.loc.rot) - 0xb9)
								+ random.Next(0, 11);
						vector = this
								.get_location(
										this.npc[num2].obj.loc.fx,
										this.npc[num2].obj.loc.fy,
										(float) num8,
										(float) (this.tile[this.npc[num2].obj.tile].h / 2));
						this.add_effect((int) vector.x, (int) vector.y, 180f,
								0f, 10, ((float) random.Next(2, 8)) / 10f,
								LColor.white, random.Next(100, 0xaf), 1);
					}
					if (((((this.npc[num2].obj.loc.x > 0) && (this.npc[num2].obj.loc.x < 480)) && (this.npc[num2].obj.loc.y > 0)) && (this.npc[num2].obj.loc.y < 0x2d1))
							&& ((this.npc[num2].obj.loc.hold == 0) && (this.npc[num2].next_shot > 0))) {
						this.npc[num2].next_shot--;
					}
					if (this.npc[num2].ai == 8) {
						this.npc[num2].obj.dest.fy = 1221f;
						if (this.npc[num2].obj.loc.dest_reached == 1) {
							this.npc[num2].active = 0;
							this.end_npc(num2);
						}
					} else if ((this.npc[num2].ai == 7)
							&& (this.npc[num2].ai_circle_entered == 1)) {
						x = 240;
						y = 360;
						if ((this.npc[num2].ai_npc >= 0)
								&& (this.npc[this.npc[num2].ai_npc].active == 1)) {
							x = (int) this.npc[this.npc[num2].ai_npc].obj.loc.fx;
							y = (int) this.npc[this.npc[num2].ai_npc].obj.loc.fy;
						}
						this.npc[num2].ai_circle_angle += this.npc[num2].obj.loc.speed / 3f;
						if (this.npc[num2].ai_circle_angle < 0f) {
							this.npc[num2].ai_circle_angle += 360f;
						}
						if (this.npc[num2].ai_circle_angle >= 360f) {
							this.npc[num2].ai_circle_angle -= 360f;
						}
						vector = this.get_location((float) x, (float) y,
								this.npc[num2].ai_circle_angle,
								this.npc[num2].ai_circle_distance);
						if (this.npc[num2].obj.loc.show_rot == 1) {
							this.npc[num2].obj.loc.rot = this.get_angle(
									this.npc[num2].obj.loc.fx,
									this.npc[num2].obj.loc.fy, vector.x,
									vector.y);
						}
						this.npc[num2].obj.dest.fx = this.npc[num2].obj.loc.fx = vector.x;
						this.npc[num2].obj.dest.fy = this.npc[num2].obj.loc.fy = vector.y;
					} else if (this.npc[num2].ai == 6) {
						this.npc[num2].obj.dest.fx = this.me.obj.loc.fx;
						this.npc[num2].obj.dest.fy = this.me.obj.loc.fy;
					} else if (this.npc[num2].obj.loc.dest_reached == 1) {
						if (this.npc[num2].ai == 7) {
							this.npc[num2].ai_circle_entered = 1;
						} else if (this.npc[num2].ai == 0) {
							this.npc[num2].obj.dest.fx = random.Next(0, 480);
							this.npc[num2].obj.dest.fy = random.Next(0, 0x2d1);
						} else {
							this.npc[num2].ai_way += this.npc[num2].ai_dir;
							if (this.npc[num2].ai_way >= this.ai[this.npc[num2].ai].ways) {
								this.npc[num2].ai_way = 0;
							}
							if (this.npc[num2].ai_way < 0) {
								this.npc[num2].ai_way = this.ai[this.npc[num2].ai].ways - 1;
							}
							this.goto_way(num2, this.npc[num2].ai_way);
						}
					}
					this.npc[num2].obj.loc = this.update_loc(
							this.npc[num2].obj.loc, this.npc[num2].obj.dest);
					if ((this.npc[num2].next_shot == 0)
							&& (this.npc[num2].ammo != -1)) {
						this.npc[num2].next_shot = random.Next(
								this.npc[num2].shoot_min,
								this.npc[num2].shoot_max);
						num18 = this.get_angle(this.npc[num2].obj.loc.fx,
								this.npc[num2].obj.loc.fy, this.me.obj.loc.fx,
								this.me.obj.loc.fy);
						if (this.npc[num2].accuracy < 1f) {
							num18 = (num18 - ((180f * (1f - this.npc[num2].accuracy)) / 2f))
									+ MathUtils
											.nextInt(
													0,
													((int) (180f * (1f - this.npc[num2].accuracy))) + 1);
						}
						for (num8 = 0; num8 < this.ammo[this.npc[num2].ammo].shots; num8++) {
							vector = this
									.get_location(
											(float) this.npc[num2].obj.loc.x,
											(float) this.npc[num2].obj.loc.y,
											num18
													+ this.ammo[this.npc[num2].ammo].shot[num8].obj.loc.fx,
											40f);
							num3 = this
									.add_shot(
											(int) vector.x,
											(int) vector.y,
											num18,
											this.ammo[this.npc[num2].ammo].shot[num8].obj.loc.scale,
											this.npc[num2].ammo, num8, 0);
							if (num3 >= 0) {
								this.shot[num3].obj.loc.speed *= this.npc[num2].mod;
								this.shot[num3].obj.dest.speed *= this.npc[num2].mod;
								if (this.shot[num3].obj.loc.speed < 1f) {
									this.shot[num3].obj.loc.speed = 1f;
								}
								if ((this.shot[num3].obj.loc.speed <= (this.npc[num2].obj.loc.speed + 1f))
										&& (this.shot[num3].obj.dest.speed <= (this.npc[num2].obj.loc.speed + 1f))) {
									this.shot[num3].obj.loc.speed = this.npc[num2].obj.loc.speed + 2f;
								}
							}
							num3 = this.add_object((int) vector.x,
									(int) vector.y, 0x1b);
							if (num3 >= 0) {
								this.obj[num3].loc.alpha = 0xff;
								this.obj[num3].loc.scale = 1f;
								this.obj[num3].dest.scale = 0f;
								this.obj[num3].loc.dur = 10f;
								this.obj[num3].loc.alpha = 200;
								this.obj[num3].dest.alpha = 0;
								this.obj[num3].cx = this.tile[this.obj[num3].tile].w / 2;
								this.obj[num3].cy = this.tile[this.obj[num3].tile].h / 2;
								this.obj[num3].loc.color = Pool.getColor(0xff,
										200, 0);
								this.obj[num3].loc.spin_speed = -20
										+ random.Next(0, 0x29);
							}
							num3 = this.add_object((int) vector.x,
									(int) vector.y, 0x1b);
							if (num3 >= 0) {
								this.obj[num3].loc.alpha = 0xff;
								this.obj[num3].loc.scale = 0.5f;
								this.obj[num3].dest.scale = 0f;
								this.obj[num3].loc.dur = 5f;
								this.obj[num3].loc.alpha = 200;
								this.obj[num3].dest.alpha = 0;
								this.obj[num3].cx = this.tile[this.obj[num3].tile].w / 2;
								this.obj[num3].cy = this.tile[this.obj[num3].tile].h / 2;
								this.obj[num3].loc.color = Pool.getColor(0xff,
										0xff, 0xff);
								this.obj[num3].loc.spin_speed = -20
										+ random.Next(0, 0x29);
							}
						}
					}
					rectangle = new RectBox(this.me.obj.loc.x
							- (this.me.obj.loc.w / 2), this.me.obj.loc.y
							- (this.me.obj.loc.h / 2), this.me.obj.loc.w,
							this.me.obj.loc.h);
					rectangle2 = new RectBox(this.npc[num2].obj.loc.x
							- ((int) (this.npc[num2].obj.loc.sw / 2f)),
							this.npc[num2].obj.loc.y
									- ((int) (this.npc[num2].obj.loc.sh / 2f)),
							(int) this.npc[num2].obj.loc.sw,
							(int) this.npc[num2].obj.loc.sh);
					if ((rectangle2.intersects(rectangle) && (this.me.dead == 0))
							&& ((this.me.shake < 1) || (this.npc[num2].boost_tile > 0))) {
						if (this.npc[num2].boost_tile < 1) {
							this.player_hurt(1, this.npc[num2].obj.loc.rot);
						}
						if ((((this.npc[num2].shield < 1f) && (this.npc[num2].solid != 1)) && (this
								.npc_hurt(num2, 1f, this.npc[num2].obj.loc.rot) > 0))
								&& (this.npc[num2].boost_tile > 0)) {
							this.play_sound(3);
							if (this.npc[num2].boost_ammo >= 0) {
								this.me.ammo = this.npc[num2].boost_ammo;
							} else if (this.npc[num2].boost_life > 0) {
								this.me.life += this.npc[num2].boost_life;
								if (this.me.life > this.me.life_max) {
									this.me.life = this.me.life_max;
								}
							} else if (this.npc[num2].boost_power_ball > 0) {
								this.me.power_ball += this.npc[num2].boost_power_ball;
								if (this.me.power_ball > 2) {
									this.me.power_ball = 2;
								}
							}
						}
					}
				}
			}
			num2++;
		}
		if ((this.hold >= 1) || (this.me.dead != 0)) {
			this.held_down = 0;
			this.me.obj.loc = this
					.update_loc(this.me.obj.loc, this.me.obj.dest);
			this.me.shield_rot += 12f;
			if (this.me.shake > 0) {
				this.me.shake--;
			}
			if (this.last_trail == 0) {
				vector = this.get_location((float) this.me.obj.loc.x,
						(float) this.me.obj.loc.y, this.me.obj.loc.rot - 180f,
						40f);
				num3 = this.add_object((int) vector.x, (int) vector.y, 0x1b);
				if (num3 >= 0) {
					this.obj[num3].dest.fx += 0f;
					num9 = 150;
					if (this.level[this.me.level].y_scroll < 0) {
						num9 = -num9;
					}
					this.obj[num3].dest.fy += num9;
					this.obj[num3].loc.alpha = random.Next(0x7d, 0x100);
					this.obj[num3].loc.scale = ((float) random.Next(4, 8)) / 10f;
					this.obj[num3].dest.scale = 0f;
					this.obj[num3].loc.speed = (MathUtils
							.abs((int) (this.level[this.me.level].y_scroll / 2)) - 1)
							+ random.Next(0, 2);
					this.obj[num3].loc.dur = 15f;
					this.obj[num3].cx = this.tile[this.obj[num3].tile].w / 2;
					this.obj[num3].cy = this.tile[this.obj[num3].tile].h / 2;
					this.obj[num3].loc.spin_speed = -10 + random.Next(0, 0x15);
					this.obj[num3].loc.color = Pool.getColor(150, 150, 0xff);
				}
			}
		}
		if (this.click == 1) {
			this.held_down = 0;
			this.click_last.x = this.click_x;
			this.click_last.y = this.click_y;
			if (((((this.click_x >= (this.ui_charge_x - 50)) && (this.click_x < (this.ui_charge_x + 50))) && (this.click_y >= (this.ui_charge_y - 50))) && (this.click_y < (this.ui_charge_y + 50)))
					&& ((this.held_down > 0) || (this.me.shield > 0))) {
				num8 = 30;
				num9 = 0;
				num10 = 0;
				while (num10 < this.me.shield) {
					for (num2 = 0; num2 < 360; num2 += num8) {
						num18 = num2 + (num10 * (num8 / this.me.shield));
						vector = this.get_location((float) this.me.obj.loc.x,
								(float) this.me.obj.loc.y, num18,
								(float) getHeight());
						num9++;
					}
					num10++;
				}
				this.click = -1;
				this.held_down = 0;
				this.me.shield = 0;
			}
		}
		if (this.click == 3) {
			if ((((this.click_x >= (this.ui_charge_x - 50)) && (this.click_x < (this.ui_charge_x + 50))) && (this.click_y >= (this.ui_charge_y - 50)))
					&& (this.click_y < (this.ui_charge_y + 50))) {
				this.held_down++;
				if (this.held_down > 0x2d) {
					this.me.shield = 1;
				}
				if (this.held_down > 0x69) {
					this.me.shield = 2;
				}
				if (this.held_down > 0xa5) {
					this.me.shield = 3;
				}
			} else {
				this.held_down = 0;
				if ((MathUtils.abs((float) (this.click_x - this.click_last.x)) > 3f)
						|| (MathUtils
								.abs((float) (this.click_y - this.click_last.y)) > 3f)) {
					this.me.obj.loc.rot = this.me.obj.dest.rot = this
							.get_angle(this.click_last.x, this.click_last.y,
									(float) this.click_x, (float) this.click_y);
					this.click_last.x = this.click_origin.x;
					this.click_last.y = this.click_origin.y;
				}
				this.me.obj.loc.fx = this.me.obj.dest.fx = this.me.obj.loc.fx
						+ (this.click_x - ((int) this.click_origin.x));
				this.me.obj.loc.fy = this.me.obj.dest.fy = this.me.obj.loc.fy
						+ (this.click_y - ((int) this.click_origin.y));
				num8 = 0x10;
				if (this.me.obj.loc.fx <= num8) {
					this.me.obj.loc.fx = 1 + num8;
				}
				if (this.me.obj.loc.fx >= (0x1df - num8)) {
					this.me.obj.loc.fx = 0x1de - num8;
				}
				if (this.me.obj.loc.fy <= num8) {
					this.me.obj.loc.fy = 1 + num8;
				}
				if (this.me.obj.loc.fy >= (720 - num8)) {
					this.me.obj.loc.fy = 0x2cf - num8;
				}
				if (this.me.obj.dest.fx <= num8) {
					this.me.obj.dest.fx = 1 + num8;
				}
				if (this.me.obj.dest.fx >= (0x1df - num8)) {
					this.me.obj.dest.fx = 0x1de - num8;
				}
				if (this.me.obj.dest.fy <= num8) {
					this.me.obj.dest.fy = 1 + num8;
				}
				if (this.me.obj.dest.fy >= (720 - num8)) {
					this.me.obj.dest.fy = 0x2cf - num8;
				}
				this.click_origin.x = this.click_x;
				this.click_origin.y = this.click_y;
				if (this.last_trail > 1) {
					this.last_trail--;
				}
			}
		} else if (this.level[this.me.level].y_scroll < 0) {
			this.me.obj.dest.rot = 180f;
		} else {
			this.me.obj.dest.rot = 0f;
		}
		if (this.click != 2) {
			this.manage_level();
			this.level[this.me.level].frame++;
			if (this.pause_frame > 0) {
				this.pause_frame--;
			}
			if ((this.level[this.me.level].pause == 0)
					&& (this.pause_frame == 0)) {
				this.prog_frame++;
			}
			if (this.me.immune > 0) {
				this.me.immune--;
				if ((this.level[this.me.level].frame % 2) == 0) {
					num8 = random.Next(0, 360);
					vector = this.get_location((float) this.me.obj.loc.x,
							(float) this.me.obj.loc.y, (float) num8, 40f);
					this.add_effect((int) vector.x, (int) vector.y,
							(float) num8, (float) random.Next(8, 10),
							random.Next(5, 15), 1f, this.random_color(),
							random.Next(0x4b, 0xff), 3);
				}
			}
			if (((this.held_down % 4) == 0)
					&& ((this.me.shield > 0) || ((this.held_down > 0) && (this.click == 3)))) {
				if (this.click != 3) {
					vector = this.get_location((float) this.ui_charge_x,
							(float) this.ui_charge_y, 0f, 0f);
				} else {
					vector = this.get_location((float) this.ui_charge_x,
							(float) this.ui_charge_y,
							(float) (-45 + random.Next(0, 0x5b)),
							(float) random.Next(0x7d, 250));
				}
				num3 = this.add_object((int) vector.x, (int) vector.y, 0x1b);
				if (num3 >= 0) {
					this.obj[num3].dest.fx = this.ui_charge_x;
					this.obj[num3].dest.fy = this.ui_charge_y;
					this.obj[num3].loc.alpha = random.Next(150, 0xe1);
					this.obj[num3].dest.alpha = 0xff;
					this.obj[num3].loc.scale = ((float) random.Next(4, 8)) / 10f;
					this.obj[num3].dest.scale = 2f;
					this.obj[num3].loc.speed = random.Next(5, 9);
					this.obj[num3].loc.dur = 90f;
					if (this.click != 3) {
						this.obj[num3].loc.dur = 20f;
					}
					this.obj[num3].cx = this.tile[this.obj[num3].tile].w / 2;
					this.obj[num3].cy = this.tile[this.obj[num3].tile].h / 2;
					if ((num3 % 2) == 0) {
						this.obj[num3].loc.color = LColor.red;
					} else {
						this.obj[num3].loc.color = Pool.getColor(0xff, 0x9b,
								0x9b);
					}
					this.obj[num3].loc.spin_speed = -20 + random.Next(0, 0x29);
				}
			}

			this.me.obj.loc = this
					.update_loc(this.me.obj.loc, this.me.obj.dest);
			this.me.shield_rot += 12f;
			if (this.me.shake > 0) {
				this.me.shake--;
			}
			if (this.last_trail == 0) {
				vector = this.get_location((float) this.me.obj.loc.x,
						(float) this.me.obj.loc.y, this.me.obj.loc.rot - 180f,
						40f);
				num3 = this.add_object((int) vector.x, (int) vector.y, 0x1b);
				if (num3 >= 0) {
					this.obj[num3].dest.fx += 0f;
					num9 = 150;
					if (this.level[this.me.level].y_scroll < 0) {
						num9 = -num9;
					}
					this.obj[num3].dest.fy += num9;
					this.obj[num3].loc.alpha = random.Next(0x7d, 0x100);
					this.obj[num3].loc.scale = ((float) random.Next(4, 8)) / 10f;
					this.obj[num3].dest.scale = 0f;
					this.obj[num3].loc.speed = (MathUtils
							.abs((int) (this.level[this.me.level].y_scroll / 2)) - 1)
							+ random.Next(0, 2);
					this.obj[num3].loc.dur = 15f;
					this.obj[num3].cx = this.tile[this.obj[num3].tile].w / 2;
					this.obj[num3].cy = this.tile[this.obj[num3].tile].h / 2;
					this.obj[num3].loc.spin_speed = -10 + random.Next(0, 0x15);
					this.obj[num3].loc.color = Pool.getColor(150, 150, 0xff);
				}
			}
			return;
		}
		if ((this.click_dur >= 3)
				&& ((this.click_dur >= 15) || ((MathUtils
						.abs((float) (this.click_x - this.click_down.x)) >= 25f) || (MathUtils
						.abs((float) (this.click_y - this.click_down.y)) >= 25f)))) {

			this.held_down = 0;
			this.click_dur = 0;
		}
		num11 = -1;
		rectangle = new RectBox(this.click_x, this.click_y, 1, 1);
		for (num3 = 0; num3 < this.npcs; num3++) {
			if (this.npc[num3].active == 1) {
				rectangle2 = new RectBox(
						(this.npc[num3].obj.loc.x - ((int) (this.npc[num3].obj.loc.sw / 2f))) - 0x12,
						(this.npc[num3].obj.loc.y - ((int) (this.npc[num3].obj.loc.sh / 2f))) - 0x12,
						(int) (this.npc[num3].obj.loc.sw + 36f),
						(int) (this.npc[num3].obj.loc.sh + 36f));
				if (rectangle2.intersects(rectangle)) {
					num11 = num3;
				}
			}
		}
		num3 = 0;
		while (num3 < this.group_shots) {
			numArray[num3] = -1;
			num3++;
		}
		int index = 0;
		for (int i = -1; i < this.me.power_ball; i++) {
			if (this.me.bomb >= 0) {
				for (num10 = 0; num10 < this.shots; num10++) {
					if ((this.shot[num10].bomb == 1)
							&& (this.shot[num10].active == 1)) {
						if (this.shot[num10].bomb_ammo >= 0) {
							this.play_sound(this.ammo[this.shot[num10].bomb_ammo].sound);
						}
						num8 = 0;
						while (num8 < this.ammo[this.shot[num10].bomb_ammo].shots) {
							vector = this
									.get_location(
											(float) this.shot[num10].obj.loc.x,
											(float) this.shot[num10].obj.loc.y,
											this.shot[num10].obj.loc.rot
													+ this.ammo[this.shot[num10].bomb_ammo].shot[num8].obj.loc.fx,
											0f);
							if (((num3 = this
									.add_shot(
											(int) vector.x,
											(int) vector.y,
											this.shot[num10].obj.loc.rot,
											this.ammo[this.shot[num10].bomb_ammo].shot[num8].obj.loc.scale,
											this.shot[num10].bomb_ammo, num8, 1)) >= 0)
									&& (index < this.group_shots)) {
								numArray[index] = num3;
							}
							num8++;
							index++;
						}
						this.shot[num10].active = 0;
					}
				}
				this.me.bomb = -1;
				i = this.me.power_ball;
			} else {
				if ((this.me.last_shot < this.ammo[this.me.ammo].reload)
						&& (i == -1)) {
					num3 = 0;
					while (num3 < this.group_shots) {
						num8 = 0;
						while (num8 < this.group_shots) {
							if (numArray[num3] >= 0) {
								this.shot_group[numArray[num3]][num8] = numArray[num8];
							}
							num8++;
						}
						num3++;
					}
				}
				this.me.obj.loc.rot = this.me.obj.dest.rot = this.get_angle(
						this.me.obj.loc.fx, this.me.obj.loc.fy,
						(float) this.click_x, (float) this.click_y);
				this.me.last_shot = 0;
				this.me.shot_count++;
				this.play_sound(this.ammo[this.me.ammo].sound);
				num8 = 0;
				while (num8 < this.ammo[this.me.ammo].shots) {
					if (i == 0) {
						vector = this
								.get_location(
										(float) this.me.obj.loc.x,
										(float) this.me.obj.loc.y,
										(this.me.obj.loc.rot + this.ammo[this.me.ammo].shot[num8].obj.loc.fx) - 90f,
										60f);
						x = (int) vector.x;
						y = (int) vector.y;
					} else if (i == 1) {
						vector = this
								.get_location(
										(float) this.me.obj.loc.x,
										(float) this.me.obj.loc.y,
										(this.me.obj.loc.rot + this.ammo[this.me.ammo].shot[num8].obj.loc.fx) + 90f,
										60f);
						x = (int) vector.x;
						y = (int) vector.y;
					} else {
						x = this.me.obj.loc.x;
						y = this.me.obj.loc.y;
					}
					float angle = this.get_angle(this.me.obj.loc.fx,
							this.me.obj.loc.fy, (float) this.click_x,
							(float) this.click_y);
					vector = this.get_location((float) x, (float) y, angle
							+ this.ammo[this.me.ammo].shot[num8].obj.loc.fx,
							40f);
					num3 = this.add_shot((int) vector.x, (int) vector.y, angle,
							this.ammo[this.me.ammo].shot[num8].obj.loc.scale,
							this.me.ammo, num8, 1);
					if (num3 >= 0) {
						this.shot[num3].obj.loc.speed += this.shot[num3].obj.loc.speed
								* this.me.rewards.weapon_speed;
						this.shot[num3].damage += this.shot[num3].damage
								* this.me.rewards.weapon_power;
						if (this.ammo[this.me.ammo].shot[num8].target_npc == 1) {
							this.shot[num3].target_npc = num11;
						}
						if ((this.ammo[this.me.ammo].shot[num8].bomb == 1)
								&& (i == (this.me.power_ball - 1))) {
							this.me.bomb = num3;
						} else if (i == -1) {
							this.me.bomb = -1;
						}
						if (index < this.group_shots) {
							numArray[index] = num3;
						}
					}
					num3 = this
							.add_object((int) vector.x, (int) vector.y, 0x1b);
					if (num3 >= 0) {
						this.obj[num3].loc.alpha = 0xff;
						this.obj[num3].loc.scale = 1f;
						this.obj[num3].dest.scale = 0f;
						this.obj[num3].loc.dur = 10f;
						this.obj[num3].loc.alpha = 200;
						this.obj[num3].dest.alpha = 0;
						this.obj[num3].cx = this.tile[this.obj[num3].tile].w / 2;
						this.obj[num3].cy = this.tile[this.obj[num3].tile].h / 2;
						this.obj[num3].loc.color = Pool.getColor(0xff, 200, 0);
						this.obj[num3].loc.spin_speed = -20
								+ random.Next(0, 0x29);
					}
					num3 = this
							.add_object((int) vector.x, (int) vector.y, 0x1b);
					if (num3 >= 0) {
						this.obj[num3].loc.alpha = 0xff;
						this.obj[num3].loc.scale = 0.5f;
						this.obj[num3].dest.scale = 0f;
						this.obj[num3].loc.dur = 5f;
						this.obj[num3].loc.alpha = 200;
						this.obj[num3].dest.alpha = 0;
						this.obj[num3].cx = this.tile[this.obj[num3].tile].w / 2;
						this.obj[num3].cy = this.tile[this.obj[num3].tile].h / 2;
						this.obj[num3].loc.color = Pool.getColor(0xff, 0xff,
								0xff);
						this.obj[num3].loc.spin_speed = -20
								+ random.Next(0, 0x29);
					}
					num8++;
					index++;
				}
			}
		}

		num3 = 0;
		while (num3 < this.group_shots) {
			num8 = 0;
			while (num8 < this.group_shots) {
				if (numArray[num3] >= 0) {
					this.shot_group[numArray[num3]][num8] = numArray[num8];
				}
				num8++;
			}
			num3++;
		}

		this.held_down = 0;
		this.click_dur = 0;

	}

	public float get_angle(float x1, float y1, float x2, float y2) {
		float num2 = x2 - x1;
		float num3 = y2 - y1;
		float num4 = 0f;
		if (num2 == 0f) {
			if (num2 == 0f) {
				num4 = 0f;
			} else if (num3 > 0.0) {
				num4 = 1.5707963267948966f;
			} else {
				num4 = 4.71238898038469f;
			}
		} else if (num3 == 0f) {
			if (num2 > 0f) {
				num4 = 0f;
			} else {
				num4 = 3.1415926535897931f;
			}
		} else if (num2 < 0f) {
			num4 = MathUtils.atan(num3 / num2) + 3.1415926535897931f;
		} else if (num3 < 0f) {
			num4 = MathUtils.atan(num3 / num2) + 6.2831853071795862f;
		} else {
			num4 = MathUtils.atan(num3 / num2);
		}
		num4 = (num4 * 180f) / 3.1415926535897931f;
		num4 += 90f;
		if (num4 < 0f) {
			num4 = 360f + num4;
		}
		if (num4 >= 360f) {
			num4 -= 360f;
		}
		return num4;
	}

	public void get_input() {

		if (SysTouch.isDown()) {
			this.click_move.set(SysTouch.getLocation());
		}
		if (this.click != -1) {
			this.click = 0;
		}
		LTouchCollection state = SysInputFactory.getTouchState();
		for (LIterator<LTouchLocation> it = state.listIterator();it.hasNext();) {
			LTouchLocation location = it.next();
			if (location.isDrag() && (this.click != -1)) {
				this.click_dur++;
				this.click_x = (int) location.getPosition().x;
				this.click_y = (int) location.getPosition().y;
				this.click = 3;

			} else if ((location.isDown()) && (this.click != -1)) {

				this.click_dur = 0;
				this.click_x = (int) location.getPosition().x;
				this.click_y = (int) location.getPosition().y;
				this.click_down.x = location.getPosition().x;
				this.click_down.y = location.getPosition().y;
				this.click_origin.x = location.getPosition().x;
				this.click_origin.y = location.getPosition().y;
				this.click = 1;
			} else if (location.isUp()) {

				if ((MathUtils
						.abs((location.getPosition().x - this.click_origin.x)) < 10f)
						&& (MathUtils
								.abs((location.getPosition().y - this.click_origin.y)) < 2f)) {
					this.click_x = (int) location.getPosition().x;
					this.click_y = (int) location.getPosition().y;
					if (this.click != -1) {
						this.click = 2;
					} else {
						this.click = 0;
					}
				} else {
					this.click = 0;
				}
			}
		}
	}

	public Vector2f get_location(float x, float y, float angle, float distance) {
		Vector2f vector = new Vector2f();
		float degrees = angle;
		if (degrees < 0f) {
			degrees += 360f;
		}
		if (degrees > 359f) {
			degrees -= 360f;
		}
		degrees -= 90f;
		if (degrees < 0f) {
			degrees += 360f;
		}
		if (degrees > 359f) {
			degrees -= 360f;
		}
		vector.x = x
				+ ((MathUtils.cos(MathUtils.toRadians(degrees)) * distance));
		vector.y = y
				+ ((MathUtils.sin(MathUtils.toRadians(degrees)) * distance));
		return vector;
	}

	public void goto_way(int n, int w) {
		Vector2f vector = new Vector2f();
		if ((n >= 0) && (n < this.npcs)) {
			int ai = this.npc[n].ai;
			if ((ai >= 0) && (ai < this.ais)) {
				this.npc[n].obj.loc.speed = 3f;
				if (ai == 0) {
					this.npc[n].obj.dest.fx = random.Next(50, 430);
					this.npc[n].obj.dest.fy = random.Next(50, 0x28b);
					this.npc[n].obj.loc.speed = random.Next(2, 4)
							* this.npc[n].ai_speed;
				} else if (ai == 7) {
					this.npc[n].ai_circle_distance = 200f;
					this.npc[n].ai_circle_entered = 0;
					this.npc[n].ai_circle_angle = this.npc[n].ai_way;
					vector = this.get_location(240f, 360f,
							this.npc[n].ai_circle_angle,
							this.npc[n].ai_circle_distance);
					this.npc[n].obj.dest.fx = vector.x;
					this.npc[n].obj.dest.fy = vector.y;
					this.npc[n].obj.loc.speed *= this.npc[n].ai_speed;
				} else if (ai == 8) {
					this.npc[n].obj.dest.fy = 1221f;
					this.npc[n].obj.dest.rot = 180f;
					this.npc[n].obj.loc.speed = 3f;
				} else if (ai == 6) {
					this.npc[n].obj.dest.fx = this.me.obj.loc.fx;
					this.npc[n].obj.dest.fy = this.me.obj.loc.fy;
					this.npc[n].obj.loc.speed = 2f * this.npc[n].ai_speed;
				} else if (this.ai[ai].ways > 0) {
					int index = w;
					if (index >= this.ai[ai].ways) {
						index = 0;
					}
					if (index < 0) {
						index = this.ai[ai].ways - 1;
					}
					this.npc[n].obj.dest.fx = this.ai[ai].x_way[index];
					this.npc[n].obj.dest.fy = this.ai[ai].y_way[index];
					this.npc[n].obj.loc.turn_speed = this.ai[ai].turn_speed[index]
							* this.npc[n].ai_speed;
					this.npc[n].obj.loc.speed = this.ai[ai].speed[index]
							* this.npc[n].ai_speed;
				}
				if (this.npc[n].obj.loc.lock_rot != 1) {
					this.npc[n].obj.dest.rot = this.get_angle(
							this.npc[n].obj.loc.fx, this.npc[n].obj.loc.fy,
							this.npc[n].obj.dest.fx, this.npc[n].obj.dest.fy);
				}
			}
		}
	}

	public void init_data() {
		int num;
		if (texture == null) {
			this.texture = new LTexture[9];
		}
		if (me == null) {
			this.me = new me_struct();
		}
		if (me.rewards == null) {
			this.me.rewards = new reward_struct();
		}
		if (float_number == null) {
			this.float_number = new float_number_struct[this.float_numbers];
		}
		for (int i = 0; i < float_number.length; i++) {
			if (float_number[i] == null) {
				float_number[i] = new float_number_struct();
			}
		}
		if (obj == null) {
			this.obj = new object_struct[this.objects];
		}
		for (int i = 0; i < obj.length; i++) {
			if (obj[i] == null) {
				obj[i] = new object_struct();
			}
		}
		if (button == null) {
			this.button = new button_struct[this.buttons];
		}
		for (int i = 0; i < button.length; i++) {
			if (button[i] == null) {
				button[i] = new button_struct();
			}
		}
		if (shot == null) {
			this.shot = new shot_struct[this.shots];
		}
		for (int i = 0; i < shot.length; i++) {
			if (shot[i] == null) {
				shot[i] = new shot_struct();
			}
		}
		if (shot_group == null) {
			this.shot_group = new int[this.shots][this.group_shots];
		}
		if (npc == null) {
			this.npc = new npc_struct[this.npcs];
		}
		for (int i = 0; i < npc.length; i++) {
			if (npc[i] == null) {
				npc[i] = new npc_struct();
			}
		}
		if (this.me.obj == null) {
			this.me.obj = new object_struct();
		}
		if (this.me.obj.loc == null) {
			this.me.obj.loc = new loc_struct();
		}
		if (this.me.obj.dest == null) {
			this.me.obj.dest = new loc_struct();
		}
		this.me.obj.loc.turn_speed = 5f;
		this.me.obj.loc.speed = 3f;
		this.me.obj.loc.show_rot = 1;
		this.me.obj.loc.scale = 1f;
		this.me.obj.loc.alpha = 0xff;
		this.me.obj.loc.dur = -1f;
		this.me.obj.tile = 0x37;
		this.me.difficulty = 1f;
		this.me.obj.loc.fw = this.me.obj.loc.w = this.tile[this.me.obj.tile].w;
		this.me.obj.loc.fh = this.me.obj.loc.h = this.tile[this.me.obj.tile].h;
		this.me.obj.cx = this.tile[this.me.obj.tile].w / 2;
		this.me.obj.cy = this.tile[this.me.obj.tile].h / 2;
		this.me.obj.dest = this.me.obj.loc.cpy();
		if (level == null) {
			this.level = new level_struct[this.levels];
		}
		for (int i = 0; i < level.length; i++) {
			if (level[i] == null) {
				level[i] = new level_struct();
			}
		}
		for (num = 0; num < this.levels; num++) {
			this.level[num].locked = 1;
			if (num == 0) {
				this.level[num].locked = 0;
			}
			this.level[num].stars = 4;
			this.level[num].star_reward = new int[this.level[num].stars];
			this.level[num].star_score = new int[this.level[num].stars];
			this.level[num].star_score[0] = 0x88b8 + (num * 0x4e20);
			this.level[num].star_score[1] = 0x186a0 + (num * 0x9c40);
			this.level[num].star_score[2] = 0x249f0 + (num * 0xea60);
			this.level[num].star_score[3] = 0x30d40 + (num * 0x13880);
			this.level[num].star_reward[0] = -1;
			this.level[num].star_reward[1] = -1;
			this.level[num].star_reward[2] = -1;
			this.level[num].star_reward[3] = -1;
		}
		num = this.lv_marathon;
		this.level[num].star_score[0] = 0xc350;
		this.level[num].star_score[1] = 0x186a0;
		this.level[num].star_score[2] = 0x30d40;
		this.level[num].star_score[3] = 0x7a120;
		this.level[0].button = 0x97;
		this.level[1].button = 0x98;
		this.level[2].button = 0x99;
		this.level[3].button = 0x9a;
		this.level[4].button = 0x9b;
		this.level[5].button = 0x9c;
		this.rewards = 30;
		if (reward == null) {
			this.reward = new reward_struct[this.rewards];
		}
		for (int i = 0; i < reward.length; i++) {
			if (reward[i] == null) {
				reward[i] = new reward_struct();
			}
		}
		num = 0;
		this.reward[num].text = "Increase your weapon speed by 10%.";
		this.reward[num].weapon_speed = 0.1f;
		this.level[0].star_reward[0] = num;
		num = 1;
		this.reward[num].text = "Increase your weapon speed by 20%.";
		this.reward[num].weapon_speed = 0.2f;
		this.level[0].star_reward[1] = num;
		num = 2;
		this.reward[num].text = "Increase your weapon speed by 30%.";
		this.reward[num].weapon_speed = 0.3f;
		this.level[0].star_reward[2] = num;
		num = 3;
		this.reward[num].text = "Increase your weapon speed by 50%.";
		this.reward[num].weapon_speed = 0.5f;
		this.level[0].star_reward[3] = num;
		num = 4;
		this.reward[num].text = "Increase your weapon power by 10%.";
		this.reward[num].weapon_power = 0.1f;
		this.level[1].star_reward[0] = num;
		num = 5;
		this.reward[num].text = "Increase your weapon power by 20%.";
		this.reward[num].weapon_power = 0.2f;
		this.level[1].star_reward[1] = num;
		num = 6;
		this.reward[num].text = "Increase your weapon power by 30%.";
		this.reward[num].weapon_power = 0.3f;
		this.level[1].star_reward[2] = num;
		num = 7;
		this.reward[num].text = "Increase your weapon power by 50%.";
		this.reward[num].weapon_power = 0.5f;
		this.level[1].star_reward[3] = num;
		this.level[2].star_reward[0] = -1;
		this.level[2].star_reward[2] = -1;
		num = 8;
		this.reward[num].text = "Increase your total life by 1 heart.";
		this.reward[num].life = 1f;
		this.level[2].star_reward[1] = num;
		num = 9;
		this.reward[num].text = "Increase your total life by 2 hearts.";
		this.reward[num].life = 2f;
		this.level[2].star_reward[3] = num;
		num = 12;
		this.reward[num].text = "Decrease all damage taken by 10%.";
		this.reward[num].mitigate = 0.1f;
		this.level[3].star_reward[0] = num;
		num = 13;
		this.reward[num].text = "Decrease all damage taken by 20%.";
		this.reward[num].mitigate = 0.2f;
		this.level[3].star_reward[1] = num;
		num = 14;
		this.reward[num].text = "Decrease all damage taken by 30%.";
		this.reward[num].mitigate = 0.3f;
		this.level[3].star_reward[2] = num;
		num = 15;
		this.reward[num].text = "Decrease all damage taken by 50%.";
		this.reward[num].mitigate = 0.5f;
		this.level[3].star_reward[3] = num;
		this.level[4].star_reward[0] = -1;
		num = 0x11;
		this.reward[num].text = "Increase power-up frequency by 1%.";
		this.reward[num].drop_rate = 0.04f;
		this.level[4].star_reward[1] = num;
		num = 0x12;
		this.reward[num].text = "Increase power-up frequency by 2%.";
		this.reward[num].drop_rate = 0.06f;
		this.level[4].star_reward[2] = num;
		num = 0x13;
		this.reward[num].text = "Increase power-up frequency by 3%.";
		this.reward[num].drop_rate = 0.1f;
		this.level[4].star_reward[3] = num;
		num = 20;
		this.reward[num].text = "Increase all points earned by 1%.";
		this.reward[num].score_increase = 0.01f;
		this.level[5].star_reward[0] = num;
		num = 0x15;
		this.reward[num].text = "Increase all points earned by 2%.";
		this.reward[num].score_increase = 0.02f;
		this.level[5].star_reward[1] = num;
		num = 0x16;
		this.reward[num].text = "Increase all points earned by 3%.";
		this.reward[num].score_increase = 0.03f;
		this.level[5].star_reward[2] = num;
		num = 0x17;
		this.reward[num].text = "Increase all points earned by 5%.";
		this.reward[num].score_increase = 0.05f;
		this.level[5].star_reward[3] = num;
	}

	public void load_ais() {
		int num2;
		int num3;
		float num9 = 2f;
		float num10 = 12f;
		if (ai == null) {
			this.ai = new ai_struct[this.ais];
		}
		for (int i = 0; i < ai.length; i++) {
			if (ai[i] == null) {
				ai[i] = new ai_struct();
			}
		}
		int index = 7;
		index = 6;
		index = 8;
		index = 0;
		index = 4;
		this.ai[index].ways = 4;
		this.ai[index].x_way = new int[this.ai[index].ways];
		this.ai[index].y_way = new int[this.ai[index].ways];
		this.ai[index].turn_speed = new float[this.ai[index].ways];
		this.ai[index].speed = new float[this.ai[index].ways];
		this.ai[index].hold = new int[this.ai[index].ways];
		int num6 = 50;
		for (num2 = 0; num2 < this.ai[index].ways; num2++) {
			this.ai[index].speed[num2] = num9;
			this.ai[index].turn_speed[num2] = num10;
			this.ai[index].hold[num2] = 0;
		}
		num2 = 0;
		this.ai[index].x_way[num2] = num6;
		this.ai[index].y_way[num2] = num6;
		num2 = 1;
		this.ai[index].x_way[num2] = 480 - num6;
		this.ai[index].y_way[num2] = num6;
		num2 = 2;
		this.ai[index].x_way[num2] = num6;
		this.ai[index].y_way[num2] = 0x26b;
		num2 = 3;
		this.ai[index].x_way[num2] = 480 - num6;
		this.ai[index].y_way[num2] = 0x26b;
		index = 5;
		this.ai[index].ways = 11;
		this.ai[index].x_way = new int[this.ai[index].ways];
		this.ai[index].y_way = new int[this.ai[index].ways];
		this.ai[index].turn_speed = new float[this.ai[index].ways];
		this.ai[index].speed = new float[this.ai[index].ways];
		this.ai[index].hold = new int[this.ai[index].ways];
		num6 = 50;
		for (num2 = 0; num2 < this.ai[index].ways; num2++) {
			this.ai[index].speed[num2] = num9;
			this.ai[index].turn_speed[num2] = num10;
			this.ai[index].hold[num2] = 0;
		}
		num2 = 0;
		this.ai[index].x_way[num2] = getWidth() / 2;
		this.ai[index].y_way[num2] = num6;
		int num4 = 500;
		int num5 = 10;
		for (num3 = 0; num3 < num5; num3++) {
			num2++;
			this.ai[index].x_way[num2] = num6
					+ (num3 * ((getWidth() - (num6 * 2)) / num5));
			this.ai[index].y_way[num2] = num4;
		}
		index = 3;
		this.ai[index].ways = 8;
		this.ai[index].x_way = new int[this.ai[index].ways];
		this.ai[index].y_way = new int[this.ai[index].ways];
		this.ai[index].turn_speed = new float[this.ai[index].ways];
		this.ai[index].speed = new float[this.ai[index].ways];
		this.ai[index].hold = new int[this.ai[index].ways];
		num6 = 32;
		num3 = 0;
		num2 = 0;
		while (num3 < 4) {
			num4 = 0;
			while (num4 < 2) {
				this.ai[index].x_way[num2] = ((num3 * 100) + num6) + 0x16;
				if (((num4 == 0) && ((num3 % 2) == 0))
						|| ((num4 == 1) && ((num3 % 2) == 1))) {
					this.ai[index].y_way[num2] = num6 + 0x16;
				} else {
					this.ai[index].y_way[num2] = (0x2bb - num6) - 100;
				}
				this.ai[index].speed[num2] = num9;
				this.ai[index].turn_speed[num2] = num10;
				this.ai[index].hold[num2] = 0;
				num4++;
				num2++;
			}
			num3++;
		}
		index = 2;
		this.ai[index].ways = 8;
		this.ai[index].x_way = new int[this.ai[index].ways];
		this.ai[index].y_way = new int[this.ai[index].ways];
		this.ai[index].turn_speed = new float[this.ai[index].ways];
		this.ai[index].speed = new float[this.ai[index].ways];
		this.ai[index].hold = new int[this.ai[index].ways];
		num4 = 0;
		num2 = 0;
		while (num4 < (this.ai[index].ways / 2)) {
			num3 = 0;
			while (num3 < 2) {
				this.ai[index].y_way[num2] = ((num4 * 100) + 32) + 0x16;
				if (((num3 == 0) && ((num4 % 2) == 0))
						|| ((num3 == 1) && ((num4 % 2) == 1))) {
					this.ai[index].x_way[num2] = 0x36;
				} else {
					this.ai[index].x_way[num2] = (getWidth() - 0x16) - 32;
				}
				this.ai[index].speed[num2] = num9;
				this.ai[index].turn_speed[num2] = num10;
				this.ai[index].hold[num2] = 0;
				num3++;
				num2++;
			}
			num4++;
		}
		index = 1;
		this.ai[index].ways = 4;
		this.ai[index].x_way = new int[this.ai[index].ways];
		this.ai[index].y_way = new int[this.ai[index].ways];
		this.ai[index].turn_speed = new float[this.ai[index].ways];
		this.ai[index].speed = new float[this.ai[index].ways];
		this.ai[index].hold = new int[this.ai[index].ways];
		num4 = 0;
		for (num2 = 0; num4 < this.ai[index].ways; num2++) {
			this.ai[index].y_way[num2] = ((num4 * 0xaf) + 32) + 0x16;
			if ((num4 % 2) == 0) {
				this.ai[index].x_way[num2] = 0x36;
			} else {
				this.ai[index].x_way[num2] = (getWidth() - 0x16) - 32;
			}
			this.ai[index].speed[num2] = num9;
			this.ai[index].turn_speed[num2] = num10;
			this.ai[index].hold[num2] = 0;
			num4++;
		}
	}

	public void load_ammo() {
		this.ammo = new ammo_struct[this.ammos];
		for (int i = 0; i < ammos; i++) {
			if (ammo[i] == null) {
				ammo[i] = new ammo_struct();
			}
		}
		int a = 1;
		this.add_ammo(a, 2);
		this.ammo[a].name = "Rocket";
		this.ammo[a].tile = 0x39;
		this.ammo[a].reload = 13;
		this.ammo[a].sound = 11;
		int index = 0;
		this.ammo[a].shot[index].obj.tile = 0x39;
		this.ammo[a].shot[index].obj.cx = this.tile[this.ammo[a].shot[index].obj.tile].w / 2;
		this.ammo[a].shot[index].obj.cy = this.tile[this.ammo[a].shot[index].obj.tile].h / 2;
		this.ammo[a].shot[index].obj.loc.fx = 0f;
		this.ammo[a].shot[index].obj.loc.fy = 0f;
		this.ammo[a].shot[index].obj.loc.rot = 0f;
		this.ammo[a].shot[index].obj.loc.lock_rot = 1;
		this.ammo[a].shot[index].obj.loc.turn_speed = 12f;
		this.ammo[a].shot[index].obj.loc.speed = 3f;
		this.ammo[a].shot[index].obj.loc.speed_up = 0.3f;
		this.ammo[a].shot[index].obj.dest.speed = 15f;
		this.ammo[a].shot[index].trail = 1;
		this.ammo[a].shot[index].obj.loc.fx = 25f;
		this.ammo[a].shot[index].target_npc = 1;
		index = 1;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.fx = -25f;
		this.ammo[a].shot[index].obj.loc.hold = 8;
		a = 0;
		this.add_ammo(a, 1);
		this.ammo[a].name = "Ball";
		this.ammo[a].tile = 0x38;
		this.ammo[a].reload = 7;
		this.ammo[a].sound = 14;
		index = 0;
		this.ammo[a].shot[index].damage = 1f;
		this.ammo[a].shot[index].obj.tile = 0x38;
		this.ammo[a].shot[index].obj.loc.scale = 0.8f;
		this.ammo[a].shot[index].obj.cx = this.tile[this.ammo[a].shot[index].obj.tile].w / 2;
		this.ammo[a].shot[index].obj.cy = this.tile[this.ammo[a].shot[index].obj.tile].h / 2;
		this.ammo[a].shot[index].obj.loc.turn_speed = 12f;
		this.ammo[a].shot[index].obj.loc.speed = 14f;
		this.ammo[a].shot[index].obj.loc.rot = this.me.obj.loc.rot;
		this.ammo[a].shot[index].obj.loc.lock_rot = 1;
		this.ammo[a].shot[index].obj.loc.color = LColor.blue;
		this.ammo[a].shot[index].trail = 2;
		a = 6;
		this.add_ammo(a, 3);
		this.ammo[a].name = "Burst";
		this.ammo[a].tile = 0x49;
		this.ammo[a].reload = 12;
		this.ammo[a].sound = 10;
		index = 0;
		this.ammo[a].shot[index].obj.tile = 0x3a;
		this.ammo[a].shot[index].obj.loc.scale = 1.5f;
		this.ammo[a].shot[index].obj.cx = this.tile[this.ammo[a].shot[index].obj.tile].w / 2;
		this.ammo[a].shot[index].obj.cy = this.tile[this.ammo[a].shot[index].obj.tile].h / 2;
		this.ammo[a].shot[index].obj.loc.turn_speed = 14f;
		this.ammo[a].shot[index].obj.loc.speed = 14f;
		this.ammo[a].shot[index].obj.loc.rot = this.me.obj.loc.rot;
		this.ammo[a].shot[index].obj.loc.lock_rot = 1;
		this.ammo[a].shot[index].obj.loc.color = LColor.yellow;
		index = 1;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.tile = 0x3a;
		this.ammo[a].shot[index].obj.loc.hold = 3;
		index = 2;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.tile = 0x3a;
		this.ammo[a].shot[index].obj.loc.hold = 6;
		a = 4;
		this.add_ammo(a, 3);
		this.ammo[a].name = "Wide";
		this.ammo[a].tile = 0x48;
		this.ammo[a].reload = 10;
		this.ammo[a].sound = 0x11;
		index = 0;
		this.ammo[a].shot[index].obj.tile = 0x3a;
		this.ammo[a].shot[index].obj.cx = this.tile[this.ammo[a].shot[index].obj.tile].w / 2;
		this.ammo[a].shot[index].obj.cy = this.tile[this.ammo[a].shot[index].obj.tile].h / 2;
		this.ammo[a].shot[index].obj.loc.turn_speed = 11f;
		this.ammo[a].shot[index].obj.loc.speed = 15f;
		this.ammo[a].shot[index].obj.loc.rot = this.me.obj.loc.rot;
		this.ammo[a].shot[index].obj.loc.lock_rot = 1;
		this.ammo[a].shot[index].obj.loc.color = LColor.lightGreen;
		index = 1;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy().cpy();
		this.ammo[a].shot[index].obj.loc.fx = -45f;
		index = 2;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy().cpy();
		this.ammo[a].shot[index].obj.loc.fx = 45f;
		a = 2;
		this.add_ammo(a, 5);
		this.ammo[a].name = "Laser";
		this.ammo[a].tile = 0x4a;
		this.ammo[a].reload = 10;
		this.ammo[a].sound = 9;
		index = 0;
		this.ammo[a].shot[index].expend = 0;
		this.ammo[a].shot[index].damage = 0.34f;
		this.ammo[a].shot[index].obj.loc.alpha = 0xff;
		this.ammo[a].shot[index].obj.tile = 0x3b;
		this.ammo[a].shot[index].obj.cx = this.tile[this.ammo[a].shot[index].obj.tile].w / 2;
		this.ammo[a].shot[index].obj.cy = this.tile[this.ammo[a].shot[index].obj.tile].h / 2;
		this.ammo[a].shot[index].obj.loc.turn_speed = 12f;
		this.ammo[a].shot[index].obj.loc.speed = 13f;
		this.ammo[a].shot[index].obj.loc.rot = this.me.obj.loc.rot;
		this.ammo[a].shot[index].obj.loc.lock_rot = 1;
		this.ammo[a].shot[index].obj.loc.color = LColor.white;
		index = 1;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy().cpy();
		this.ammo[a].shot[index].obj.loc.hold = 1;
		this.ammo[a].shot[index].obj.loc.alpha = 200;
		index = 2;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy().cpy();
		this.ammo[a].shot[index].obj.loc.hold = 2;
		this.ammo[a].shot[index].obj.loc.alpha = 0xaf;
		index = 3;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy().cpy();
		this.ammo[a].shot[index].obj.loc.hold = 3;
		this.ammo[a].shot[index].obj.loc.alpha = 150;
		index = 4;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy().cpy();
		this.ammo[a].shot[index].obj.loc.hold = 4;
		this.ammo[a].shot[index].obj.loc.alpha = 0x7d;
		a = 3;
		this.add_ammo(a, 3);
		this.ammo[a].name = "Spread";
		this.ammo[a].tile = 0x4b;
		this.ammo[a].reload = 12;
		this.ammo[a].sound = 0x10;
		index = 0;
		this.ammo[a].shot[index].obj.tile = 0x38;
		this.ammo[a].shot[index].damage = 1f;
		this.ammo[a].shot[index].obj.loc.scale = 0.8f;
		this.ammo[a].shot[index].obj.cx = this.tile[this.ammo[a].shot[index].obj.tile].w / 2;
		this.ammo[a].shot[index].obj.cy = this.tile[this.ammo[a].shot[index].obj.tile].h / 2;
		this.ammo[a].shot[index].obj.loc.turn_speed = 12f;
		this.ammo[a].shot[index].obj.loc.speed = 14f;
		this.ammo[a].shot[index].obj.loc.rot = this.me.obj.loc.rot;
		this.ammo[a].shot[index].obj.loc.lock_rot = 1;
		this.ammo[a].shot[index].trail = 2;
		this.ammo[a].shot[index].obj.loc.color = LColor.orange;
		index = 1;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.fx = -30f;
		this.ammo[a].shot[index].obj.loc.rot = -20f;
		index = 2;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.fx = 30f;
		this.ammo[a].shot[index].obj.loc.rot = 20f;
		a = 5;
		this.add_ammo(a, 5);
		this.ammo[a].name = "Fire";
		this.ammo[a].tile = 0x3e;
		this.ammo[a].reload = 12;
		this.ammo[a].sound = 15;
		index = 0;
		this.ammo[a].shot[index].expend = 1;
		this.ammo[a].shot[index].obj.loc.dur = 30f;
		this.ammo[a].shot[index].obj.tile = 0x3e;
		this.ammo[a].shot[index].damage = 1f;
		this.ammo[a].shot[index].obj.loc.scale = 1f;
		this.ammo[a].shot[index].obj.cx = this.tile[this.ammo[a].shot[index].obj.tile].w / 2;
		this.ammo[a].shot[index].obj.cy = this.tile[this.ammo[a].shot[index].obj.tile].h / 2;
		this.ammo[a].shot[index].obj.loc.turn_speed = 12f;
		this.ammo[a].shot[index].obj.loc.speed = 6f;
		this.ammo[a].shot[index].obj.loc.rot = this.me.obj.loc.rot;
		this.ammo[a].shot[index].obj.loc.lock_rot = 1;
		this.ammo[a].shot[index].obj.loc.color = LColor.white;
		this.ammo[a].shot[index].obj.loc.alpha = 0xff;
		this.ammo[a].shot[index].obj.dest.alpha = 100;
		this.ammo[a].shot[index].trail = 1;
		index = 1;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.fx = -15f;
		this.ammo[a].shot[index].obj.loc.rot = -10f;
		index = 2;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.fx = -30f;
		this.ammo[a].shot[index].obj.loc.rot = -20f;
		index = 3;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.fx = 15f;
		this.ammo[a].shot[index].obj.loc.rot = 10f;
		index = 4;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.fx = 30f;
		this.ammo[a].shot[index].obj.loc.rot = 20f;
		a = 7;
		this.add_ammo(a, 1);
		this.ammo[a].name = "Mine";
		this.ammo[a].tile = 0x3f;
		this.ammo[a].reload = 15;
		this.ammo[a].sound = 13;
		index = 0;
		this.ammo[a].shot[index].obj.tile = 0x3f;
		this.ammo[a].shot[index].bomb = 1;
		this.ammo[a].shot[index].bomb_ammo = 11;
		this.ammo[a].shot[index].obj.cx = this.tile[this.ammo[a].shot[index].obj.tile].w / 2;
		this.ammo[a].shot[index].obj.cy = this.tile[this.ammo[a].shot[index].obj.tile].h / 2;
		this.ammo[a].shot[index].obj.loc.turn_speed = 12f;
		this.ammo[a].shot[index].obj.loc.speed = 5f;
		this.ammo[a].shot[index].obj.loc.rot = this.me.obj.loc.rot;
		this.ammo[a].shot[index].obj.loc.lock_rot = 1;
		this.ammo[a].shot[index].obj.loc.spin_speed = 20f;
		this.ammo[a].shot[index].trail = 1;
		a = 11;
		this.add_ammo(a, 12);
		this.ammo[a].name = "Explosion";
		this.ammo[a].tile = 0;
		this.ammo[a].sound = 5;
		index = 0;
		this.ammo[a].shot[index].expend = 1;
		this.ammo[a].shot[index].obj.loc.dur = 9f;
		this.ammo[a].shot[index].obj.tile = 0x41;
		this.ammo[a].shot[index].damage = 1f;
		this.ammo[a].shot[index].obj.loc.scale = 0.75f;
		this.ammo[a].shot[index].obj.cx = this.tile[this.ammo[a].shot[index].obj.tile].w / 2;
		this.ammo[a].shot[index].obj.cy = this.tile[this.ammo[a].shot[index].obj.tile].h / 2;
		this.ammo[a].shot[index].obj.loc.turn_speed = 12f;
		this.ammo[a].shot[index].obj.loc.speed = 13f;
		this.ammo[a].shot[index].obj.loc.rot = this.me.obj.loc.rot;
		this.ammo[a].shot[index].obj.loc.lock_rot = 1;
		this.ammo[a].shot[index].obj.loc.color = LColor.purple;
		this.ammo[a].shot[index].obj.loc.alpha = 0xff;
		this.ammo[a].shot[index].obj.dest.alpha = 100;
		this.ammo[a].shot[index].trail = 1;
		index++;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.rot = 30f;
		index++;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.rot = 60f;
		index++;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.rot = 90f;
		index++;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.rot = 120f;
		index++;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.rot = 150f;
		index++;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.rot = 180f;
		index++;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.rot = 210f;
		index++;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.rot = 240f;
		index++;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.rot = 270f;
		index++;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.rot = 300f;
		index++;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.rot = 330f;
		a = 9;
		this.add_ammo(a, 1);
		this.ammo[a].name = "Missile";
		this.ammo[a].tile = 0x4c;
		this.ammo[a].reload = 10;
		this.ammo[a].sound = 12;
		index = 0;
		this.ammo[a].shot[index].damage = 1.5f;
		this.ammo[a].shot[index].bomb_ammo = 10;
		this.ammo[a].shot[index].bomb = 2;
		this.ammo[a].shot[index].obj.tile = 0x4c;
		this.ammo[a].shot[index].obj.cx = this.tile[this.ammo[a].shot[index].obj.tile].w / 2;
		this.ammo[a].shot[index].obj.cy = this.tile[this.ammo[a].shot[index].obj.tile].h / 2;
		this.ammo[a].shot[index].obj.loc.fx = 0f;
		this.ammo[a].shot[index].obj.loc.fy = 0f;
		this.ammo[a].shot[index].obj.loc.rot = 0f;
		this.ammo[a].shot[index].obj.loc.lock_rot = 1;
		this.ammo[a].shot[index].obj.loc.turn_speed = 12f;
		this.ammo[a].shot[index].obj.loc.speed = 1f;
		this.ammo[a].shot[index].obj.loc.speed_up = 0.25f;
		this.ammo[a].shot[index].obj.dest.speed = 13f;
		this.ammo[a].shot[index].trail = 1;
		this.ammo[a].shot[index].target_npc = 1;
		a = 10;
		this.add_ammo(a, 12);
		this.ammo[a].name = "Missile Explosion";
		this.ammo[a].tile = 0x3e;
		this.ammo[a].sound = 6;
		index = 0;
		this.ammo[a].shot[index].expend = 1;
		this.ammo[a].shot[index].obj.loc.dur = 8f;
		this.ammo[a].shot[index].obj.tile = 0x3e;
		this.ammo[a].shot[index].damage = 0.5f;
		this.ammo[a].shot[index].obj.loc.scale = 0.65f;
		this.ammo[a].shot[index].obj.cx = this.tile[this.ammo[a].shot[index].obj.tile].w / 2;
		this.ammo[a].shot[index].obj.cy = this.tile[this.ammo[a].shot[index].obj.tile].h / 2;
		this.ammo[a].shot[index].obj.loc.turn_speed = 12f;
		this.ammo[a].shot[index].obj.loc.speed = 8f;
		this.ammo[a].shot[index].obj.loc.rot = this.me.obj.loc.rot;
		this.ammo[a].shot[index].obj.loc.lock_rot = 1;
		this.ammo[a].shot[index].obj.loc.color = LColor.white;
		this.ammo[a].shot[index].obj.loc.alpha = 0xff;
		this.ammo[a].shot[index].obj.dest.alpha = 100;
		this.ammo[a].shot[index].trail = 1;
		index++;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.rot = 30f;
		index++;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.rot = 60f;
		index++;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.rot = 90f;
		index++;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.rot = 120f;
		index++;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.rot = 150f;
		index++;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.rot = 180f;
		index++;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.rot = 210f;
		index++;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.rot = 240f;
		index++;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.rot = 270f;
		index++;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.rot = 300f;
		index++;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.rot = 330f;
		a = 8;
		this.add_ammo(a, 5);
		this.ammo[a].name = "Electric";
		this.ammo[a].tile = 0x51;
		this.ammo[a].reload = 11;
		this.ammo[a].sound = 0x12;
		index = 0;
		this.ammo[a].shot[index].damage = 0.1f;
		this.ammo[a].shot[index].expend = 1;
		this.ammo[a].shot[index].obj.tile = 0x51;
		this.ammo[a].shot[index].obj.loc.scale = 0.4f;
		this.ammo[a].shot[index].obj.cx = this.tile[this.ammo[a].shot[index].obj.tile].w / 2;
		this.ammo[a].shot[index].obj.cy = this.tile[this.ammo[a].shot[index].obj.tile].h / 2;
		this.ammo[a].shot[index].obj.loc.turn_speed = 12f;
		this.ammo[a].shot[index].obj.loc.speed = 10f;
		this.ammo[a].shot[index].obj.loc.rot = this.me.obj.loc.rot;
		this.ammo[a].shot[index].obj.loc.lock_rot = 1;
		this.ammo[a].shot[index].obj.loc.color = LColor.white;
		this.ammo[a].shot[index].obj.loc.show_rot = 0;
		this.ammo[a].shot[index].obj.loc.spin_speed = 320 + random
				.Next(0, 0x51);
		this.ammo[a].shot[index].obj.loc.hold = 10;
		this.ammo[a].shot[index].obj.loc.speed = 3f;
		index = 1;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].expend = 1;
		this.ammo[a].shot[index].damage = 0.2f;
		this.ammo[a].shot[index].obj.loc.scale = 0.6f;
		this.ammo[a].shot[index].obj.loc.hold = 9;
		this.ammo[a].shot[index].obj.loc.speed = 4.5f;
		this.ammo[a].shot[index].obj.loc.spin_speed = 320 + random
				.Next(0, 0x51);
		index = 2;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].expend = 1;
		this.ammo[a].shot[index].damage = 0.3f;
		this.ammo[a].shot[index].obj.loc.scale = 0.8f;
		this.ammo[a].shot[index].obj.loc.hold = 7;
		this.ammo[a].shot[index].obj.loc.speed = 6f;
		this.ammo[a].shot[index].obj.loc.spin_speed = 320 + random
				.Next(0, 0x51);
		index = 3;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].expend = 1;
		this.ammo[a].shot[index].damage = 0.4f;
		this.ammo[a].shot[index].obj.loc.scale = 1f;
		this.ammo[a].shot[index].obj.loc.hold = 4;
		this.ammo[a].shot[index].obj.loc.speed = 7.5f;
		this.ammo[a].shot[index].obj.loc.spin_speed = 320 + random
				.Next(0, 0x51);
		index = 4;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].expend = 0;
		this.ammo[a].shot[index].damage = 0.5f;
		this.ammo[a].shot[index].obj.loc.scale = 1.2f;
		this.ammo[a].shot[index].obj.loc.hold = 0;
		this.ammo[a].shot[index].obj.loc.speed = 9f;
		this.ammo[a].shot[index].obj.loc.spin_speed = 320 + random
				.Next(0, 0x51);
		a = 12;
		this.add_ammo(a, 1);
		this.ammo[a].name = "NPC Rocket";
		this.ammo[a].tile = 0x39;
		this.ammo[a].reload = 13;
		this.ammo[a].sound = 11;
		index = 0;
		this.ammo[a].shot[index].obj.tile = 0x39;
		this.ammo[a].shot[index].obj.loc.scale = 0.75f;
		this.ammo[a].shot[index].obj.loc.color = LColor.red;
		this.ammo[a].shot[index].obj.cx = this.tile[this.ammo[a].shot[index].obj.tile].w / 2;
		this.ammo[a].shot[index].obj.cy = this.tile[this.ammo[a].shot[index].obj.tile].h / 2;
		this.ammo[a].shot[index].obj.loc.fx = 0f;
		this.ammo[a].shot[index].obj.loc.fy = 0f;
		this.ammo[a].shot[index].obj.loc.rot = 0f;
		this.ammo[a].shot[index].obj.loc.lock_rot = 1;
		this.ammo[a].shot[index].obj.loc.turn_speed = 12f;
		this.ammo[a].shot[index].obj.loc.speed = 2f;
		this.ammo[a].shot[index].obj.loc.speed_up = 0.075f;
		this.ammo[a].shot[index].obj.dest.speed = 5f;
		this.ammo[a].shot[index].trail = 1;
		a = 13;
		this.add_ammo(a, 3);
		this.ammo[a].name = "NPC Fire";
		this.ammo[a].tile = 0x3e;
		this.ammo[a].reload = 12;
		this.ammo[a].sound = 15;
		index = 0;
		this.ammo[a].shot[index].expend = 1;
		this.ammo[a].shot[index].obj.loc.dur = 90f;
		this.ammo[a].shot[index].obj.tile = 0x3e;
		this.ammo[a].shot[index].damage = 1f;
		this.ammo[a].shot[index].obj.loc.scale = 0.65f;
		this.ammo[a].shot[index].obj.cx = this.tile[this.ammo[a].shot[index].obj.tile].w / 2;
		this.ammo[a].shot[index].obj.cy = this.tile[this.ammo[a].shot[index].obj.tile].h / 2;
		this.ammo[a].shot[index].obj.loc.turn_speed = 12f;
		this.ammo[a].shot[index].obj.loc.speed = 4f;
		this.ammo[a].shot[index].obj.loc.rot = this.me.obj.loc.rot;
		this.ammo[a].shot[index].obj.loc.lock_rot = 1;
		this.ammo[a].shot[index].obj.loc.color = LColor.lightCoral;
		this.ammo[a].shot[index].obj.loc.alpha = 0xff;
		this.ammo[a].shot[index].obj.dest.alpha = 100;
		this.ammo[a].shot[index].trail = 1;
		index = 1;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.fx = -15f;
		this.ammo[a].shot[index].obj.loc.rot = -10f;
		index = 2;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.fx = 15f;
		this.ammo[a].shot[index].obj.loc.rot = 10f;
		a = 14;
		this.add_ammo(a, 2);
		this.ammo[a].name = "NPC Wide";
		this.ammo[a].tile = 0x48;
		this.ammo[a].reload = 10;
		this.ammo[a].sound = 0x11;
		index = 0;
		this.ammo[a].shot[index].obj.tile = 0x3a;
		this.ammo[a].shot[index].obj.cx = this.tile[this.ammo[a].shot[index].obj.tile].w / 2;
		this.ammo[a].shot[index].obj.cy = this.tile[this.ammo[a].shot[index].obj.tile].h / 2;
		this.ammo[a].shot[index].obj.loc.turn_speed = 11f;
		this.ammo[a].shot[index].obj.loc.speed = 6f;
		this.ammo[a].shot[index].obj.loc.scale = 1f;
		this.ammo[a].shot[index].obj.loc.rot = this.me.obj.loc.rot;
		this.ammo[a].shot[index].obj.loc.lock_rot = 1;
		this.ammo[a].shot[index].obj.loc.color = LColor.orange;
		this.ammo[a].shot[index].obj.loc.fx = -15f;
		index = 1;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.fx = 15f;
		a = 15;
		this.add_ammo(a, 1);
		this.ammo[a].name = "NPC Ball";
		this.ammo[a].tile = 0x38;
		this.ammo[a].reload = 7;
		this.ammo[a].sound = 14;
		index = 0;
		this.ammo[a].shot[index].obj.tile = 0x38;
		this.ammo[a].shot[index].obj.loc.scale = 0.5f;
		this.ammo[a].shot[index].obj.cx = this.tile[this.ammo[a].shot[index].obj.tile].w / 2;
		this.ammo[a].shot[index].obj.cy = this.tile[this.ammo[a].shot[index].obj.tile].h / 2;
		this.ammo[a].shot[index].obj.loc.turn_speed = 12f;
		this.ammo[a].shot[index].obj.loc.speed = 4f;
		this.ammo[a].shot[index].obj.loc.rot = this.me.obj.loc.rot;
		this.ammo[a].shot[index].obj.loc.lock_rot = 1;
		this.ammo[a].shot[index].obj.loc.color = LColor.red;
		this.ammo[a].shot[index].trail = 2;
		a = 0x10;
		this.add_ammo(a, 2);
		this.ammo[a].name = "NPC Burst";
		this.ammo[a].tile = 0x49;
		this.ammo[a].reload = 12;
		this.ammo[a].sound = 10;
		index = 0;
		this.ammo[a].shot[index].obj.tile = 0x3a;
		this.ammo[a].shot[index].obj.loc.scale = 1f;
		this.ammo[a].shot[index].obj.cx = this.tile[this.ammo[a].shot[index].obj.tile].w / 2;
		this.ammo[a].shot[index].obj.cy = this.tile[this.ammo[a].shot[index].obj.tile].h / 2;
		this.ammo[a].shot[index].obj.loc.turn_speed = 14f;
		this.ammo[a].shot[index].obj.loc.speed = 4f;
		this.ammo[a].shot[index].obj.loc.rot = this.me.obj.loc.rot;
		this.ammo[a].shot[index].obj.loc.lock_rot = 1;
		this.ammo[a].shot[index].obj.loc.color = LColor.purple;
		index = 1;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.tile = 0x3a;
		this.ammo[a].shot[index].obj.loc.hold = 5;
		a = 0x11;
		this.add_ammo(a, 2);
		this.ammo[a].name = "NPC Spray";
		this.ammo[a].tile = 0x4b;
		this.ammo[a].reload = 12;
		this.ammo[a].sound = 0x10;
		index = 0;
		this.ammo[a].shot[index].obj.tile = 0x38;
		this.ammo[a].shot[index].damage = 1f;
		this.ammo[a].shot[index].obj.loc.scale = 0.6f;
		this.ammo[a].shot[index].obj.cx = this.tile[this.ammo[a].shot[index].obj.tile].w / 2;
		this.ammo[a].shot[index].obj.cy = this.tile[this.ammo[a].shot[index].obj.tile].h / 2;
		this.ammo[a].shot[index].obj.loc.turn_speed = 12f;
		this.ammo[a].shot[index].obj.loc.speed = 6f;
		this.ammo[a].shot[index].obj.loc.rot = this.me.obj.loc.rot;
		this.ammo[a].shot[index].obj.loc.lock_rot = 1;
		this.ammo[a].shot[index].trail = 2;
		this.ammo[a].shot[index].obj.loc.color = LColor.red;
		index = 1;
		this.ammo[a].shot[index] = this.ammo[a].shot[0].cpy();
		this.ammo[a].shot[index].obj.loc.fx = 0f;
		this.ammo[a].shot[index].obj.loc.rot = -90f;
	}

	public void load_data() {

	}

	public void load_tiles() {

		this.tiles = 250;
		this.number_tile = new int[10];
		this.tile = new tile_struct[this.tiles];
		for (int i = 0; i < tile.length; i++) {
			if (tile[i] == null) {
				tile[i] = new tile_struct();
			}
		}
		int index = 0;
		this.number_tile[1] = index;
		this.tile[index].texture = 1;
		this.tile[index].x = 0;
		this.tile[index].w = 32 - this.tile[index].x;
		this.tile[index].y = 0;
		this.tile[index].h = 0x40;
		index = 1;
		this.number_tile[2] = index;
		this.tile[index].texture = 1;
		this.tile[index].x = 0x40;
		this.tile[index].w = 0x70 - this.tile[index].x;
		this.tile[index].y = 0;
		this.tile[index].h = 0x40;
		index = 2;
		this.number_tile[3] = index;
		this.tile[index].texture = 1;
		this.tile[index].x = 0x80;
		this.tile[index].w = 0xb0 - this.tile[index].x;
		this.tile[index].y = 0;
		this.tile[index].h = 0x40;
		index = 3;
		this.number_tile[4] = index;
		this.tile[index].texture = 1;
		this.tile[index].x = 0xc0;
		this.tile[index].w = 0xf1 - this.tile[index].x;
		this.tile[index].y = 0;
		this.tile[index].h = 0x40;
		index = 4;
		this.number_tile[5] = index;
		this.tile[index].texture = 1;
		this.tile[index].x = 0x100;
		this.tile[index].w = 0x12f - this.tile[index].x;
		this.tile[index].y = 0;
		this.tile[index].h = 0x40;
		index = 5;
		this.number_tile[6] = index;
		this.tile[index].texture = 1;
		this.tile[index].x = 320;
		this.tile[index].w = 0x170 - this.tile[index].x;
		this.tile[index].y = 0;
		this.tile[index].h = 0x40;
		index = 6;
		this.number_tile[7] = index;
		this.tile[index].texture = 1;
		this.tile[index].x = 0x180;
		this.tile[index].w = 430 - this.tile[index].x;
		this.tile[index].y = 0;
		this.tile[index].h = 0x40;
		index = 7;
		this.number_tile[8] = index;
		this.tile[index].texture = 1;
		this.tile[index].x = 0x1c0;
		this.tile[index].w = 0x1f1 - this.tile[index].x;
		this.tile[index].y = 0;
		this.tile[index].h = 0x40;
		index = 8;
		this.number_tile[9] = index;
		this.tile[index].texture = 1;
		this.tile[index].x = 0x200;
		this.tile[index].w = 560 - this.tile[index].x;
		this.tile[index].y = 0;
		this.tile[index].h = 0x40;
		index = 9;
		this.number_tile[0] = index;
		this.tile[index].texture = 1;
		this.tile[index].x = 0x240;
		this.tile[index].w = 630 - this.tile[index].x;
		this.tile[index].y = 0;
		this.tile[index].h = 0x40;
		index = 10;
		this.tile[index].texture = 1;
		this.tile[index].x = 0x23;
		this.tile[index].w = 0x3f - this.tile[index].x;
		this.tile[index].y = 0;
		this.tile[index].h = 0x40;
		index = 20;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x80;
		this.tile[index].w = 0x40;
		this.tile[index].y = 0;
		this.tile[index].h = 0x40;
		this.tile[index].clicked = 0x16;
		index = 0x15;
		this.tile[index].texture = 0;
		this.tile[index].x = 0xc0;
		this.tile[index].w = 0x40;
		this.tile[index].y = 0;
		this.tile[index].h = 0x40;
		this.tile[index].clicked = 0x17;
		index = 0x16;
		this.tile[index].texture = 0;
		this.tile[index].x = 320;
		this.tile[index].w = 0x40;
		this.tile[index].y = 0;
		this.tile[index].h = 0x40;
		index = 0x17;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x100;
		this.tile[index].w = 0x40;
		this.tile[index].y = 0;
		this.tile[index].h = 0x40;
		index = 0x18;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x180;
		this.tile[index].w = 0x40;
		this.tile[index].y = 0;
		this.tile[index].h = 0x40;
		index = 0x19;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x1c0;
		this.tile[index].w = 0x40;
		this.tile[index].y = 0;
		this.tile[index].h = 0x40;
		index = 0x1a;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x200;
		this.tile[index].w = 0x40;
		this.tile[index].y = 0;
		this.tile[index].h = 0x40;
		index = 0x1b;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x240;
		this.tile[index].w = 32;
		this.tile[index].y = 0;
		this.tile[index].h = 32;
		index = 0x1c;
		this.tile[index].texture = 0;
		this.tile[index].x = 640;
		this.tile[index].w = 0x40;
		this.tile[index].y = 0;
		this.tile[index].h = 0x40;
		index = 30;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x300;
		this.tile[index].w = 0x40;
		this.tile[index].y = 0;
		this.tile[index].h = 0x40;
		index = 0x1f;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x2e0;
		this.tile[index].w = 32;
		this.tile[index].y = 0x1c0;
		this.tile[index].h = 32;
		index = 32;
		this.tile[index].texture = 0;
		this.tile[index].x = 320;
		this.tile[index].w = 0x100;
		this.tile[index].y = 0x240;
		this.tile[index].h = 0x180;
		index = 0x21;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x2c0;
		this.tile[index].w = 0x40;
		this.tile[index].y = 0;
		this.tile[index].h = 0x40;
		index = 0x22;
		this.tile[index].texture = 0;
		this.tile[index].x = 0;
		this.tile[index].w = 0x40;
		this.tile[index].y = 0x40;
		this.tile[index].h = 0x40;
		index = 0x23;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x60;
		this.tile[index].w = 480;
		this.tile[index].y = 0x58;
		this.tile[index].h = 0x5c;
		index = 0x24;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x360;
		this.tile[index].w = 0x40;
		this.tile[index].y = 0x40;
		this.tile[index].h = 0x40;
		index = 0x25;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x260;
		this.tile[index].w = 32;
		this.tile[index].y = 32;
		this.tile[index].h = 32;
		index = 0x26;
		this.tile[index].texture = 4;
		this.tile[index].x = 0;
		this.tile[index].w = getWidth();
		this.tile[index].y = 0;
		this.tile[index].h = getHeight();
		index = 0x27;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x100;
		this.tile[index].w = 0xc0;
		this.tile[index].y = 0xe0;
		this.tile[index].h = 0xc0;
		index = 40;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x260;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0x60;
		this.tile[index].h = 0x30;
		index = 50;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x60;
		this.tile[index].w = 0x60;
		this.tile[index].y = 0x100;
		this.tile[index].h = 0x60;
		index = 0x33;
		this.tile[index].texture = 0;
		this.tile[index].x = 0;
		this.tile[index].w = 0x60;
		this.tile[index].y = 0x100;
		this.tile[index].h = 0x60;
		index = 0x34;
		this.tile[index].texture = 0;
		this.tile[index].x = 0;
		this.tile[index].w = 0x40;
		this.tile[index].y = 0xc0;
		this.tile[index].h = 0x40;
		index = 0x35;
		this.tile[index].texture = 0;
		this.tile[index].x = 0;
		this.tile[index].w = 0x40;
		this.tile[index].y = 0x80;
		this.tile[index].h = 0x40;
		index = 0x36;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x1c0;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0;
		this.tile[index].h = 0x30;
		index = 0x37;
		this.tile[index].texture = 0;
		this.tile[index].x = 800;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0;
		this.tile[index].h = 0x30;
		index = 0x38;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x200;
		this.tile[index].w = 20;
		this.tile[index].y = 0;
		this.tile[index].h = 20;
		index = 0x39;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x220;
		this.tile[index].w = 14;
		this.tile[index].y = 0;
		this.tile[index].h = 0x1b;
		index = 0x3a;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x220;
		this.tile[index].w = 8;
		this.tile[index].y = 32;
		this.tile[index].h = 15;
		index = 0x3b;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x200;
		this.tile[index].w = 0x10;
		this.tile[index].y = 32;
		this.tile[index].h = 0x10;
		index = 60;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x2a0;
		this.tile[index].w = 160;
		this.tile[index].y = 0x60;
		this.tile[index].h = 32;
		index = 0x3d;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x2a0;
		this.tile[index].w = 160;
		this.tile[index].y = 160;
		this.tile[index].h = 32;
		index = 0x3e;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x360;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0x60;
		this.tile[index].h = 0x30;
		index = 0x3f;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x360;
		this.tile[index].w = 32;
		this.tile[index].y = 160;
		this.tile[index].h = 32;
		this.tile[index].frames = 2;
		this.tile[index].frame = new int[this.tile[index].frames];
		int num2 = 0;
		this.tile[index].frame[num2] = 0x3f;
		num2 = 1;
		this.tile[index].frame[num2] = 0x40;
		index = 0x40;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x360;
		this.tile[index].w = 32;
		this.tile[index].y = 0xe0;
		this.tile[index].h = 32;
		index = 0x41;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x360;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0x120;
		this.tile[index].h = 0x30;
		index = 0x42;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x360;
		this.tile[index].w = 0x40;
		this.tile[index].y = 0x160;
		this.tile[index].h = 0x40;
		index = 0x43;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x356;
		this.tile[index].w = 0x54;
		this.tile[index].y = 0x1b6;
		this.tile[index].h = 0x54;
		index = 0x44;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x360;
		this.tile[index].w = 0x2c;
		this.tile[index].y = 0x220;
		this.tile[index].h = 0x2c;
		index = 0x45;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x360;
		this.tile[index].w = 0x40;
		this.tile[index].y = 0x260;
		this.tile[index].h = 0x40;
		index = 70;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x2e0;
		this.tile[index].w = 0x60;
		this.tile[index].y = 0x260;
		this.tile[index].h = 0x2a;
		index = 0x47;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x2e0;
		this.tile[index].w = 0xc0;
		this.tile[index].y = 0x2c0;
		this.tile[index].h = 0x2a;
		index = 0x48;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x2a0;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0xe0;
		this.tile[index].h = 0x30;
		index = 0x49;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x2e0;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0xe0;
		this.tile[index].h = 0x30;
		index = 0x4a;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x2a0;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0x120;
		this.tile[index].h = 0x30;
		index = 0x4b;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x2e0;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0x120;
		this.tile[index].h = 0x30;
		index = 0x4c;
		this.tile[index].texture = 0;
		this.tile[index].x = 800;
		this.tile[index].w = 0x21;
		this.tile[index].y = 0x120;
		this.tile[index].h = 0x21;
		index = 0x4d;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x2e0;
		this.tile[index].w = 0x54;
		this.tile[index].y = 0x220;
		this.tile[index].h = 0x10;
		index = 0x4e;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x200;
		this.tile[index].w = 0x3d;
		this.tile[index].y = 0x100;
		this.tile[index].h = 0x3d;
		index = 0x4f;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x200;
		this.tile[index].w = 0x3d;
		this.tile[index].y = 320;
		this.tile[index].h = 0x3d;
		index = 80;
		this.tile[index].texture = 0;
		this.tile[index].x = 640;
		this.tile[index].w = 0x2a;
		this.tile[index].y = 0x180;
		this.tile[index].h = 0x2a;
		index = 0x51;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x2c0;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0x180;
		this.tile[index].h = 0x30;
		this.tile[index].frames = 4;
		this.tile[index].frame = new int[this.tile[index].frames];
		num2 = 0;
		this.tile[index].frame[num2] = index;
		num2 = 1;
		this.tile[index].frame[num2] = 0x52;
		num2 = 2;
		this.tile[index].frame[num2] = 0x53;
		num2 = 3;
		this.tile[index].frame[num2] = 0x54;
		index = 0x52;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x2c0;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0x1c0;
		this.tile[index].h = 0x30;
		index = 0x53;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x300;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0x180;
		this.tile[index].h = 0x30;
		index = 0x54;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x300;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0x1c0;
		this.tile[index].h = 0x30;
		index = 0x55;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x1c0;
		this.tile[index].w = 0xb6;
		this.tile[index].y = 0x1a0;
		this.tile[index].h = 0xb6;
		index = 0x56;
		this.tile[index].texture = 0;
		this.tile[index].x = 0xe0;
		this.tile[index].w = 0xb6;
		this.tile[index].y = 0x220;
		this.tile[index].h = 32;
		index = 0x57;
		this.tile[index].texture = 0;
		this.tile[index].x = 0xf6;
		this.tile[index].w = 0xb6;
		this.tile[index].y = 480;
		this.tile[index].h = 32;
		index = 0x58;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x108;
		this.tile[index].w = 0xb0;
		this.tile[index].y = 0x1b0;
		this.tile[index].h = 32;
		index = 0x59;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x360;
		this.tile[index].w = 0x40;
		this.tile[index].y = 0x1c0;
		this.tile[index].h = 13;
		index = 90;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x360;
		this.tile[index].w = 0x40;
		this.tile[index].y = 0x1d0;
		this.tile[index].h = 13;
		index = 0x5b;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x240;
		this.tile[index].w = 0x3d;
		this.tile[index].y = 0x100;
		this.tile[index].h = 0x3d;
		index = 0x5c;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x240;
		this.tile[index].w = 0x3d;
		this.tile[index].y = 320;
		this.tile[index].h = 0x3d;
		index = 0x5d;
		this.tile[index].texture = 0;
		this.tile[index].x = 160;
		this.tile[index].w = 0x47;
		this.tile[index].y = 0x2c0;
		this.tile[index].h = 0x1a;
		index = 0x5e;
		this.tile[index].texture = 0;
		this.tile[index].x = 160;
		this.tile[index].w = 0x7b;
		this.tile[index].y = 0x2e0;
		this.tile[index].h = 0x1a;
		index = 0x5f;
		this.tile[index].texture = 0;
		this.tile[index].x = 0;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0x3d8;
		this.tile[index].h = 0x30;
		index = 0x60;
		this.tile[index].texture = 5;
		this.tile[index].x = 0;
		this.tile[index].w = 480;
		this.tile[index].y = 0;
		this.tile[index].h = 800;
		index = 200;
		this.tile[index].texture = 0;
		this.tile[index].x = 0xa8;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0x300;
		this.tile[index].h = 0x30;
		this.tile[index].clicked = 0xca;
		index = 0xc9;
		this.tile[index].texture = 0;
		this.tile[index].x = 240;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0x300;
		this.tile[index].h = 0x30;
		this.tile[index].clicked = 0xcb;
		index = 0xca;
		this.tile[index].texture = 0;
		this.tile[index].x = 0xa8;
		this.tile[index].w = 0x30;
		this.tile[index].y = 840;
		this.tile[index].h = 0x30;
		this.tile[index].clicked = 200;
		index = 0xcb;
		this.tile[index].texture = 0;
		this.tile[index].x = 240;
		this.tile[index].w = 0x30;
		this.tile[index].y = 840;
		this.tile[index].h = 0x30;
		this.tile[index].clicked = 0xc9;
		index = 0xcc;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x48;
		this.tile[index].w = 0x1b0;
		this.tile[index].y = 0x3d8;
		this.tile[index].h = 0x48;
		index = 0xcd;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x18;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0x438;
		this.tile[index].h = 0x30;
		index = 0xce;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x60;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0x438;
		this.tile[index].h = 0x30;
		index = 0xcf;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x210;
		this.tile[index].w = 240;
		this.tile[index].y = 960;
		this.tile[index].h = 0x30;
		index = 0xd0;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x210;
		this.tile[index].w = 240;
		this.tile[index].y = 0x3fc;
		this.tile[index].h = 0x30;
		index = 0xd1;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x210;
		this.tile[index].w = 240;
		this.tile[index].y = 0x438;
		this.tile[index].h = 0x30;
		index = 210;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x210;
		this.tile[index].w = 240;
		this.tile[index].y = 0x474;
		this.tile[index].h = 0x30;
		index = 0xd3;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x210;
		this.tile[index].w = 240;
		this.tile[index].y = 0x4b0;
		this.tile[index].h = 0x30;
		index = 0xd4;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x108;
		this.tile[index].w = 240;
		this.tile[index].y = 0x4b0;
		this.tile[index].h = 0x30;
		index = 0xd5;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x318;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0x3a8;
		this.tile[index].h = 0x30;
		index = 0xd6;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x18;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0x480;
		this.tile[index].h = 0x30;
		index = 0xd7;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x18;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0x4c8;
		this.tile[index].h = 0x30;
		index = 0xd8;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x210;
		this.tile[index].w = 240;
		this.tile[index].y = 0x4ec;
		this.tile[index].h = 0x48;
		index = 0xd9;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x210;
		this.tile[index].w = 240;
		this.tile[index].y = 0x534;
		this.tile[index].h = 0x48;
		index = 0xda;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x210;
		this.tile[index].w = 240;
		this.tile[index].y = 0x57c;
		this.tile[index].h = 0x48;
		index = 0xdb;
		this.tile[index].texture = 0;
		this.tile[index].x = 480;
		this.tile[index].w = 0x48;
		this.tile[index].y = 0x360;
		this.tile[index].h = 0x48;
		index = 220;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x160;
		this.tile[index].w = 160;
		this.tile[index].y = 0x460;
		this.tile[index].h = 0x40;
		index = 0xdd;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x200;
		this.tile[index].w = 440;
		this.tile[index].y = 0x5cb;
		this.tile[index].h = 0x2a;
		index = 0xde;
		this.tile[index].texture = 0;
		this.tile[index].x = 0xb6;
		this.tile[index].w = 0x48;
		this.tile[index].y = 0x480;
		this.tile[index].h = 0x48;
		index = 0xdf;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x6c;
		this.tile[index].w = 0x48;
		this.tile[index].y = 0x480;
		this.tile[index].h = 0x48;
		index = 0xe0;
		this.tile[index].texture = 8;
		this.tile[index].x = 0;
		this.tile[index].w = 480;
		this.tile[index].y = 0;
		this.tile[index].h = 800;
		index = 0xe1;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x108;
		this.tile[index].w = 240;
		this.tile[index].y = 0x428;
		this.tile[index].h = 0x30;
		index = 0xe3;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x18;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0x510;
		this.tile[index].h = 0x30;
		index = 0xe4;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x60;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0x510;
		this.tile[index].h = 0x30;
		index = 0xe5;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x360;
		this.tile[index].w = 0x48;
		this.tile[index].y = 0x3a8;
		this.tile[index].h = 0x48;
		index = 230;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x18;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0x558;
		this.tile[index].h = 0x30;
		index = 0xe7;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x60;
		this.tile[index].w = 0x30;
		this.tile[index].y = 0x558;
		this.tile[index].h = 0x30;
		index = 0xe8;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x18;
		this.tile[index].w = 60;
		this.tile[index].y = 0x5a0;
		this.tile[index].h = 60;
		index = 100;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x60;
		this.tile[index].w = 32;
		this.tile[index].y = 0x260;
		this.tile[index].h = 32;
		this.tile[index].frames = 3;
		this.tile[index].frame = new int[this.tile[index].frames];
		num2 = 0;
		this.tile[index].frame[num2] = 100;
		num2 = 1;
		this.tile[index].frame[num2] = 0x65;
		num2 = 2;
		this.tile[index].frame[num2] = 0x66;
		index = 0x65;
		this.tile[index].texture = 0;
		this.tile[index].x = 160;
		this.tile[index].w = 32;
		this.tile[index].y = 0x260;
		this.tile[index].h = 32;
		index = 0x66;
		this.tile[index].texture = 0;
		this.tile[index].x = 0xe0;
		this.tile[index].w = 32;
		this.tile[index].y = 0x260;
		this.tile[index].h = 32;
		index = 0x6f;
		this.tile[index].x = 0;
		this.tile[index].y = 0x260;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x70;
		this.tile[index].x = 32;
		this.tile[index].y = 0x260;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x71;
		this.tile[index].x = 0x40;
		this.tile[index].y = 0x260;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x72;
		this.tile[index].x = 0;
		this.tile[index].y = 640;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x73;
		this.tile[index].x = 32;
		this.tile[index].y = 640;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x74;
		this.tile[index].x = 0x40;
		this.tile[index].y = 640;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x75;
		this.tile[index].x = 0;
		this.tile[index].y = 0x2a0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x76;
		this.tile[index].x = 32;
		this.tile[index].y = 0x2a0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x77;
		this.tile[index].x = 0x40;
		this.tile[index].y = 0x2a0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 120;
		this.tile[index].x = 0;
		this.tile[index].y = 0x2c0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x79;
		this.tile[index].x = 32;
		this.tile[index].y = 0x2c0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x7a;
		this.tile[index].x = 0;
		this.tile[index].y = 0x2e0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x7b;
		this.tile[index].x = 32;
		this.tile[index].y = 0x2e0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x7c;
		this.tile[index].x = 0xc0;
		this.tile[index].y = 0x1a0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x7d;
		this.tile[index].x = 0xc0;
		this.tile[index].y = 0x1c0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x7e;
		this.tile[index].x = 0xc0;
		this.tile[index].y = 480;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x7f;
		this.tile[index].x = 0x60;
		this.tile[index].y = 0x300;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x80;
		this.tile[index].x = 0x60;
		this.tile[index].y = 800;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x81;
		this.tile[index].x = 0x60;
		this.tile[index].y = 0x340;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 130;
		this.tile[index].x = 0x80;
		this.tile[index].y = 0x300;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x83;
		this.tile[index].x = 0;
		this.tile[index].y = 0x1a0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x84;
		this.tile[index].x = 0;
		this.tile[index].y = 0x1c0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x85;
		this.tile[index].x = 0;
		this.tile[index].y = 480;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x86;
		this.tile[index].x = 0x40;
		this.tile[index].y = 0x1a0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x87;
		this.tile[index].x = 0x40;
		this.tile[index].y = 0x1c0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x88;
		this.tile[index].x = 0x40;
		this.tile[index].y = 480;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x89;
		this.tile[index].x = 32;
		this.tile[index].y = 0x1c0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x8d;
		this.tile[index].x = 0x60;
		this.tile[index].y = 0x1a0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x8e;
		this.tile[index].x = 0x60;
		this.tile[index].y = 0x1c0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x8f;
		this.tile[index].x = 0x60;
		this.tile[index].y = 480;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x90;
		this.tile[index].x = 160;
		this.tile[index].y = 0x1a0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x91;
		this.tile[index].x = 160;
		this.tile[index].y = 0x1c0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x92;
		this.tile[index].x = 160;
		this.tile[index].y = 480;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x93;
		this.tile[index].x = 0x80;
		this.tile[index].y = 0x1c0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x95;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x130;
		this.tile[index].w = 160;
		this.tile[index].y = 0x318;
		this.tile[index].h = 160;
		index = 150;
		this.tile[index].texture = 0;
		this.tile[index].x = 480;
		this.tile[index].w = 160;
		this.tile[index].y = 0x270;
		this.tile[index].h = 160;
		index = 0x97;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x130;
		this.tile[index].w = 160;
		this.tile[index].y = 0x270;
		this.tile[index].h = 160;
		index = 0x98;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x160;
		this.tile[index].w = 160;
		this.tile[index].y = 0x4f0;
		this.tile[index].h = 160;
		index = 0x99;
		this.tile[index].texture = 0;
		this.tile[index].x = 160;
		this.tile[index].w = 160;
		this.tile[index].y = 0x4f0;
		this.tile[index].h = 160;
		index = 0x9a;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x310;
		this.tile[index].w = 160;
		this.tile[index].y = 0x420;
		this.tile[index].h = 160;
		index = 0x9b;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x310;
		this.tile[index].w = 160;
		this.tile[index].y = 0x4e0;
		this.tile[index].h = 160;
		index = 0x9c;
		this.tile[index].texture = 0;
		this.tile[index].x = 160;
		this.tile[index].w = 160;
		this.tile[index].y = 0x598;
		this.tile[index].h = 160;
		index = 0xc7;
		this.tile[index].texture = 0;
		this.tile[index].x = 0x1c8;
		this.tile[index].w = 160;
		this.tile[index].y = 0x1b0;
		this.tile[index].h = 160;
		for (index = 0; index < this.tiles; index++) {
			this.tile[index].cx = this.tile[index].w / 2;
			this.tile[index].cy = this.tile[index].h / 2;
		}
	}

	public void manage_level() {
		int num5;
		if ((this.me.win == 1) && (this.hold == 90)) {
			num5 = 0;
			while (num5 < 10) {
				this.add_effect(240, 360, random.Next(0, 360),
						random.Next(1, 3), random.Next(30, 60), 1f,
						LColor.yellow, random.Next(0x4b, 0xff), 3);
				this.add_effect(240, 360, random.Next(0, 360),
						random.Next(2, 5), random.Next(30, 60), 1f,
						LColor.lime, random.Next(0x4b, 0xff), 3);
				num5++;
			}
		}
		if ((this.level[this.me.level].won == 1) && (this.me.win != 1)) {
			this.me.win = 1;
			this.mode_fade = this.FADE_OUT;
			this.hold_mode = 8;
			this.hold = 150;
			num5 = this.add_object(-300, 360, 0x5d);
			if (num5 >= 0) {
				this.obj[num5].loc.dur = 120f;
				this.obj[num5].loc.alpha_hold = 70;
				this.obj[num5].loc.dur = 150f;
				this.obj[num5].loc.hold = 0;
				this.obj[num5].dest.fx = 230 - this.tile[0x5d].w;
				this.obj[num5].loc.speed = 10f;
			}
			num5 = this.add_object(780 - this.tile[0x5e].w, 360, 0x5e);
			if (num5 >= 0) {
				this.obj[num5].loc.alpha_hold = 70;
				this.obj[num5].loc.dur = 120f;
				this.obj[num5].loc.hold = 30;
				this.obj[num5].dest.fx = 250f;
				this.obj[num5].loc.speed = 10f;
			}
		}
		if (((this.me.win != 1) && ((this.pause_frame <= 0) && (this.level[this.me.level].pause <= 0)))
				&& ((this.lv_marathon != this.me.level) || (this.npc_count <= 0))) {
			this.progress_level();
		}
	}

	public int npc_hurt(int npc_id, float dmg, float angle) {

		int index = npc_id;
		int num3 = 0;
		float scale = 1.5f;
		this.npc[index].shake = 10;
		this.npc[index].life -= dmg;
		if (this.npc[index].life <= 0f) {
			if (this.npc[index].end == 1) {
				this.level[this.me.level].won = 1;
			}
			num3 = 1;
			this.end_npc(index);
			if (this.npc[index].boost_tile > 0) {
				this.add_effect(this.npc[index].obj.loc.x,
						this.npc[index].obj.loc.y, angle, 0f, 15,
						scale * 0.85f, this.npc[index].boost_color, 0xff, 2);
			} else if (this.npc[index].obj.tile_top > 0) {
				this.add_effect(this.npc[index].obj.loc.x,
						this.npc[index].obj.loc.y, angle, 0f, 15,
						scale * 0.85f, this.npc[index].obj.top_color, 0xff, 2);
			} else {
				this.add_effect(this.npc[index].obj.loc.x,
						this.npc[index].obj.loc.y, angle, 0f, 15,
						scale * 0.85f, this.npc[index].obj.loc.color, 0xff, 2);
			}
			if (this.npc[index].boost_tile < 1) {
				this.play_sound(4);
				this.add_effect(this.npc[index].obj.loc.x,
						this.npc[index].obj.loc.y, angle, 0f, 15, scale,
						Pool.getColor(0xe1, 0xff, 0xff), 200, 1);
				this.add_effect(this.npc[index].obj.loc.x,
						this.npc[index].obj.loc.y, angle, 0f, 15,
						scale * 0.75f, Pool.getColor(0xff, 150, 0), 0xff, 1);
				this.add_effect(this.npc[index].obj.loc.x,
						this.npc[index].obj.loc.y, angle, 0f, 15, scale * 0.5f,
						Pool.getColor(0xff, 0xff, 0), 0xff, 1);
				this.add_effect(this.npc[index].obj.loc.x,
						this.npc[index].obj.loc.y, angle, 0f, 15,
						scale * 0.25f, LColor.white, 0xff, 1);
				return num3;
			}
			for (int i = 0; i < 360; i += 30) {
				angle = i;
				this.add_effect(this.npc[index].obj.loc.x,
						this.npc[index].obj.loc.y, angle, 2f, 0x19, scale,
						this.npc[index].boost_color, 0xff, 3);
				this.add_effect(this.npc[index].obj.loc.x,
						this.npc[index].obj.loc.y, angle, 1f, 20, scale,
						this.npc[index].boost_color, 0xff, 3);
			}
			return num3;
		}
		if ((dmg > 0f) && (this.npc[index].boost_tile < 1)) {
			this.play_sound(1);
		}
		this.add_effect(this.npc[index].obj.loc.x, this.npc[index].obj.loc.y,
				angle, 0f, 15, scale / 2f, this.npc[index].obj.loc.color, 0xff,
				2);
		return num3;
	}

	public void play_sound(int s) {

	}

	public void player_hurt(int dmg, float angle) {
		if (((this.me.immune <= 0) && (this.me.dead <= 0))
				&& ((this.me.shake <= 0) && ((this.level[this.me.level].won <= 0) && (this.me.win <= 0)))) {
			float scale = dmg - (dmg * this.me.rewards.mitigate);
			if (scale <= 0f) {
				scale = 0.1f;
			}
			this.me.life -= scale;
			if (this.me.life < 0f) {
				this.me.life = 0f;
			}
			if (dmg > 0) {
				this.play_sound(0);
			}
			this.me.power_ball--;
			if (this.me.power_ball < 0) {
				this.me.power_ball = 0;
			}
			if (this.me.life == 0f) {
				this.me.dead = 1;
				this.hold = 120;
				this.mode_fade = this.FADE_OUT;
				this.hold_mode = 7;
				this.play_sound(8);
				int index = this.add_object(240, 360, 0x58);
				if (index >= 0) {
					this.obj[index].cx = this.tile[0x58].w / 2;
					this.obj[index].cy = this.tile[0x58].h / 2;
					this.obj[index].loc.scale = 0f;
					this.obj[index].dest.scale = 2f;
					this.obj[index].loc.alpha_hold = 70;
					this.obj[index].dest.alpha = 0;
					this.obj[index].loc.dur = 120f;
					this.obj[index].loc.hold = 30;
				}
				for (index = 0; index < 10; index++) {
					this.add_effect(this.me.obj.loc.x, this.me.obj.loc.y,
							random.Next(0, 360), random.Next(1, 3),
							random.Next(30, 60), 1f, LColor.red,
							random.Next(0x4b, 0xff), 3);
					this.add_effect(this.me.obj.loc.x, this.me.obj.loc.y,
							random.Next(0, 360), random.Next(2, 5),
							random.Next(30, 60), 1f, LColor.black,
							random.Next(0x4b, 0xff), 3);
				}
				this.add_effect(this.me.obj.loc.x, this.me.obj.loc.y,
						random.Next(0, 360), random.Next(1, 1),
						random.Next(30, 60), 2f, LColor.red,
						random.Next(0x4b, 0xff), 2);
			}
			this.held_down = 0;
			if (this.me.shake < 1) {
				this.me.shake = 60;
				this.me.shield -= dmg;
				if (this.me.shield < 1) {
					scale = 1.5f;
					this.add_effect(this.me.obj.loc.x, this.me.obj.loc.y,
							angle, 0f, 15, scale,
							Pool.getColor(0xe1, 0xff, 0xff), 200, 1);
					this.add_effect(this.me.obj.loc.x, this.me.obj.loc.y,
							angle, 0f, 15, scale * 0.75f,
							Pool.getColor(0xff, 150, 0), 0xff, 1);
					this.add_effect(this.me.obj.loc.x, this.me.obj.loc.y,
							angle, 0f, 15, scale * 0.5f,
							Pool.getColor(0xff, 0xff, 0), 0xff, 1);
					this.add_effect(this.me.obj.loc.x, this.me.obj.loc.y,
							angle, 0f, 15, scale * 0.25f, LColor.white, 0xff, 1);
				}
				if (this.me.shield <= 0) {
					this.me.shield = 0;
				}
			}
		}
	}

	public void progress_level() {
		int num2;
		int num3;
		int num4;
		if ((this.me.level != this.lv_marathon)
				&& (((this.waves >= this.level[this.me.level].waves) && (this.level[this.me.level].waves > 0)) || ((this.wave_total >= (this.level[this.me.level].wave_total * this.me.difficulty)) && ((this.level[this.me.level].wave_total * this.me.difficulty) > 0f)))) {
			num2 = this.add_npc(random.Next(0, 480), -200, 0,
					this.me.difficulty, 0, 0, 0, 0xe8, 0);
			if (num2 >= 0) {
				this.npc[num2].shoot_min = 0x2d;
				this.npc[num2].shoot_max = 90;
				this.npc[num2].shoot_min += this.npc[num2].shoot_min
						- ((int) (this.npc[num2].shoot_min * this.me.difficulty));
				this.npc[num2].shoot_max += this.npc[num2].shoot_max
						- ((int) (this.npc[num2].shoot_max * this.me.difficulty));
				this.npc[num2].accuracy = 0.5f + ((this.me.level * 5f) / 10f);
				this.npc[num2].pause = 1;
				this.npc[num2].end = 1;
				this.npc[num2].ai_speed = 1f + ((this.me.level * 2f) / 10f);
				this.npc[num2].obj.loc.show_rot = 0;
				this.npc[num2].obj.loc.spin_speed = 5f;
				this.npc[num2].obj.loc.turn_speed = 6f;
				this.npc[num2].shield = this.npc[num2].obj.loc.fw + 30f;
				this.npc[num2].shield_count = this.me.level + 1;
				this.npc[num2].life = this.npc[num2].life_max = 15 + (this.me.level * 6);
				this.npc[num2].solid = 1;
				this.npc[num2].points = 0x9c4;
				this.npc[num2].ammo = 13;
				this.npc[num2].boss = 1;
				num4 = 0;
				while (num4 < this.npc[num2].shield_count) {
					num3 = this.add_npc(this.npc[num2].obj.loc.x,
							this.npc[num2].obj.loc.y, 0, this.me.difficulty, 7,
							num4 * (360 / this.npc[num2].shield_count), 0, 40,
							0);
					if (num3 >= 0) {
						this.npc[num3].ai_npc = num2;
						this.npc[num3].ai_circle_distance = 80 + (this.me.level * 0x17);
						this.npc[num3].obj.loc.turn_speed = 0f;
						this.npc[num3].obj.loc.show_rot = 0;
						this.npc[num3].obj.loc.spin_speed = -15f;
						this.npc[num3].shielding = num2;
						this.npc[num3].life = this.npc[num3].life_max = 10 + (this.me.level * 2);
						this.npc[num3].obj.loc.color = LColor.white;
						this.npc[num3].solid = 1;
						this.npc[num3].points = 300;
						this.npc[num3].ai_circle_entered = 1;
						this.npc[num3].ammo = -1;
					}
					num4++;
				}
			}
		} else {
			int num5;
			int num9;
			float num16 = 1f;
			if (this.me.level == 0) {
				num16 = 1 + random.Next(0, 1);
			}
			if (this.me.level == 1) {
				num16 = 2 + random.Next(0, 2);
			}
			if (this.me.level == 2) {
				num16 = 3 + random.Next(0, 3);
			}
			if (this.me.level == 3) {
				num16 = 4 + random.Next(0, 4);
			}
			if (this.me.level == 4) {
				num16 = 5 + random.Next(0, 5);
			}
			if (this.me.level == 5) {
				num16 = 6 + random.Next(0, 6);
			}
			if (this.me.level == this.lv_marathon) {
				num16 = this.waves + 1;
			}
			if (((this.level[this.me.level].wave_total * this.me.difficulty) > 0f)
					&& ((this.wave_total + num16) > (this.level[this.me.level].wave_total * this.me.difficulty))) {
				num16 = (this.level[this.me.level].wave_total * this.me.difficulty)
						- this.wave_total;
			}
			float num15 = 0f;
			do {
				num9 = random.Next(0, this.ais);
			} while ((num9 == 8) || (num9 == 6));
			num9 = 0;
			int num10 = random.Next(0, 2);
			if (num9 == 7) {
				num5 = 270 + random.Next(0, 0xb5);
			} else if (this.ai[num9].ways <= 1) {
				num5 = 0;
			} else {
				num5 = random.Next(0, this.ai[num9].ways);
			}
			int num12 = 1;
			Vector2f vector = this.start_point();
			int x = (int) vector.x;
			int y = (int) vector.y;
			if (this.me.level == this.lv_marathon) {
				if (this.me.score >= this.level[this.me.level].high_score) {
					this.level[this.me.level].high_score = this.me.score;
					this.level[this.me.level].high_score_reached = 1;
					this.save_data();
				}
				num3 = this.add_float_number(240, 240, LColor.white, 1, 1f,
						60f, this.waves + 1);
				if (num3 >= 0) {
					this.float_number[num3].dest.scale = 3f;
					this.float_number[num3].loc.alpha_hold = 30;
					this.float_number[num3].dest.alpha = 0;
				}
			}
			for (num2 = 0; num2 < 100; num2++) {
				int num;
				int num11;
				if (num10 == 1) {
					num11 = 0;
					if (num9 == 7) {
						num5 = 270 + random.Next(0, 0xb5);
					} else if ((this.ai[num9].ways <= 1) || (num9 == 0)) {
						num5 = 0;
						vector = this.start_point();
						x = (int) vector.x;
						y = (int) vector.y;
					} else {
						num5 = num12;
						num12++;
						if (num12 >= this.ai[num9].ways) {
							num12 = 0;
						}
					}
				} else {
					num11 = num2 * 50;
				}
				do {
					num4 = random.Next(0, 7);
					switch (num4) {
					case 0:
						num4 = 1;
						break;

					case 1:
						num4 = 3;
						break;

					case 2:
						num4 = 6;
						break;

					case 3:
						num4 = 2;
						break;

					case 4:
						num4 = 7;
						break;

					case 5:
						num4 = 8;
						break;

					case 6:
						num4 = 9;
						break;
					}
					if ((num4 == 8) && (random.Next(0, 100) > 0x19)) {
						num4 = -1;
					}
					if (this.me.level == 0) {
						if ((num4 == 6) && (random.Next(0, 100) > 0x19)) {
							num4 = -1;
						}
						if (num4 == 3) {
							num4 = -1;
						}
						if (num4 == 2) {
							num4 = -1;
						}
						if (num4 == 7) {
							num4 = -1;
						}
						if (num4 == 8) {
							num4 = -1;
						}
						if (num4 == 9) {
							num4 = -1;
						}
					}
					if (this.me.level == 1) {
						if (num4 == 3) {
							num4 = -1;
						}
						if ((num4 == 7) && (random.Next(0, 100) > 0x19)) {
							num4 = -1;
						}
						if ((num4 == 9) && (random.Next(0, 100) > 15)) {
							num4 = -1;
						}
					}
					if (this.me.level == 2) {
						if ((num4 == 1) && (random.Next(0, 100) > 0x19)) {
							num4 = -1;
						}
						if ((num4 == 3) && (random.Next(0, 100) > 20)) {
							num4 = -1;
						}
						if ((num4 == 7) && (random.Next(0, 100) > 0x23)) {
							num4 = -1;
						}
					}
					if (this.me.level == 2) {
						if (num4 == 1) {
							num4 = -1;
						}
						if ((num4 == 3) && (random.Next(0, 100) > 20)) {
							num4 = -1;
						}
						if ((num4 == 9) && (random.Next(0, 100) > 0x4b)) {
							num4 = -1;
						}
						if ((num4 == 7) && (random.Next(0, 100) > 0x23)) {
							num4 = -1;
						}
					}
					if (this.me.level == 2) {
						if (num4 == 1) {
							num4 = -1;
						}
						if ((num4 == 3) && (random.Next(0, 100) > 20)) {
							num4 = -1;
						}
						if ((num4 == 7) && (random.Next(0, 100) > 0x23)) {
							num4 = -1;
						}
					}
					if (this.me.level == 2) {
						if (num4 == 1) {
							num4 = -1;
						}
						if ((num4 == 3) && (random.Next(0, 100) > 20)) {
							num4 = -1;
						}
						if ((num4 == 7) && (random.Next(0, 100) > 0x23)) {
							num4 = -1;
						}
					}
					if (num4 == 3) {
						num3 = 0;
						while (num3 < this.npcs) {
							if ((this.npc[num3].active == 1)
									&& (this.npc[num3].type == 3)) {
								num4 = -1;
								break;
							}
							num3++;
						}
					}
					if (num4 == 7) {
						num3 = 0;
						while (num3 < this.npcs) {
							if ((this.npc[num3].active == 1)
									&& (this.npc[num3].type == 7)) {
								num4 = -1;
								break;
							}
							num3++;
						}
					}
				} while (num4 == -1);
				if (num4 == 8) {
					num = 6;
				} else {
					num = num9;
				}
				num3 = this.add_npc(x, y, num4, this.me.difficulty, num, num5,
						num11, 0, 0);
				if (num3 >= 0) {
					this.npc[num3].obj.loc.hold = num2 * random.Next(5, 10);
					int num8 = this.npc[num3].kill_time;
					num8 += num8 - ((int) (num8 * this.me.difficulty));
					num8 += 10 - (this.me.level * 2);
					if (this.me.level != this.lv_marathon) {
						this.pause_frame += num8;
					}
					num15 += this.npc[num3].mod;
				}
				if (num15 >= num16) {
					break;
				}
			}
			if (this.me.level == this.lv_marathon) {
				this.pause_frame += 60;
			}
			this.pause_frame += 30;
			this.waves++;
			this.wave_total += num15;
		}
	}

	public LColor random_color() {
		switch (random.Next(0, 7)) {
		case 0:
			return LColor.red;

		case 1:
			return LColor.orange;

		case 2:
			return LColor.yellow;

		case 3:
			return LColor.green;

		case 4:
			return LColor.blue;

		case 5:
			return LColor.purple;
		}
		return LColor.white;
	}

	public void save_data() {

	}

	public void set_npc(int n_id, int npc_type, float mod, int t1, int t2) {
		int index = n_id;
		this.npc[index].points = 100;
		this.npc[index].next_shot = random.Next(30, 120);
		this.npc[index].life = this.npc[index].life_max = 1f;
		this.npc[index].boost_ammo = -1;
		this.npc[index].shielding = -1;
		this.npc[index].shoot_min = 0x69;
		this.npc[index].shoot_max = 210;
		this.npc[index].obj.loc.color = LColor.white;
		this.npc[index].obj.loc.alpha = 0xff;
		this.npc[index].obj.loc.scale = 1f;
		this.npc[index].obj.loc.turn_speed = 15f;
		this.npc[index].obj.loc.dur = -1f;
		this.npc[index].obj.loc.show_rot = 1;
		this.npc[index].obj.loc.lock_rot = 0;
		this.npc[index].obj.loc.slow_dist = 150f;
		this.npc[index].obj.loc.color = LColor.white;
		this.npc[index].obj.top_color = random_color();
		this.npc[index].obj.loc.speed = 3f;
		this.npc[index].ammo = 15;
		this.npc[index].accuracy = 0.66f * mod;
		this.npc[index].obj.tile = t1;
		this.npc[index].obj.tile_top = t2;
		this.npc[index].obj.dest = this.npc[index].obj.loc.cpy();
		if (npc_type == 5) {
			if (t1 < 1) {
				this.npc[index].obj.tile = 0x42;
				this.npc[index].obj.tile_top = 0;
			}
			this.npc[index].obj.loc.turn_speed = 0f;
			this.npc[index].next_shot = -1;
			this.npc[index].life_max = this.npc[index].life = 0.01f;
			this.npc[index].boost_ammo = -1;
			this.npc[index].boost_life = -1;
			this.npc[index].ammo = -1;
			this.npc[index].obj.loc.show_rot = 0;
			this.npc[index].obj.loc.rot = 180f;
			this.npc[index].obj.loc.lock_rot = 1;
		} else if (npc_type == 9) {
			this.npc[index].points = 160;
			if (t1 < 1) {
				this.npc[index].obj.tile = 230;
				this.npc[index].obj.tile_top = 0xe7;
			}
			this.npc[index].ai_speed = 1.75f;
			this.npc[index].obj.loc.show_rot = 0;
			this.npc[index].obj.loc.spin_speed = -15f;
			this.npc[index].ammo = 0x11;
			this.npc[index].kill_time = 0x2d;
			this.npc[index].life = this.npc[index].life_max = 1.5f;
		} else if (npc_type == 8) {
			this.npc[index].points = 0x87;
			this.npc[index].life = this.npc[index].life_max = 2f;
			if (t1 < 1) {
				this.npc[index].obj.tile = 0xe3;
				this.npc[index].obj.tile_top = 0xe4;
			}
			this.npc[index].ai_speed = 1.75f;
			this.npc[index].obj.loc.show_rot = 1;
			this.npc[index].ammo = -1;
			this.npc[index].kill_time = 0x2d;
		} else if (npc_type == 7) {
			this.npc[index].obj.top_color = LColor.white;
			this.npc[index].obj.loc.color = random_color();
			this.npc[index].points = 200;
			this.npc[index].life = this.npc[index].life_max = 5f;
			if (t1 < 1) {
				this.npc[index].obj.tile = 0xde;
				this.npc[index].obj.tile_top = 0xdf;
			}
			this.npc[index].solid = 1;
			this.npc[index].ai_speed = 0.9f;
			this.npc[index].obj.loc.show_rot = 1;
			this.npc[index].ammo = 12;
			this.npc[index].kill_time = 0x55;
		} else if (npc_type == 4) {
			this.npc[index].obj.loc.color = random_color();
			this.npc[index].points = 0;
			if (t1 < 1) {
				this.npc[index].obj.tile = 0xd5;
				this.npc[index].obj.tile_top = 0;
			}
			this.npc[index].ai_speed = 1f;
			this.npc[index].obj.loc.lock_rot = 1;
			this.npc[index].obj.loc.show_rot = 0;
			this.npc[index].obj.loc.rot = 180f;
			this.npc[index].life = this.npc[index].life_max = 1f;
			this.npc[index].solid = 1;
			this.npc[index].ammo = -1;
			this.npc[index].kill_time = 0x2d;
		} else if (npc_type == 1) {
			this.npc[index].points = 100;
			if (t1 < 1) {
				this.npc[index].obj.tile = 0x4e;
				this.npc[index].obj.tile_top = 0x4f;
			}
			this.npc[index].ai_speed = 1.5f;
			this.npc[index].obj.loc.show_rot = 0;
			this.npc[index].ammo = 15;
			this.npc[index].kill_time = 30;
		} else if (npc_type == 3) {
			this.npc[index].open_min = 270f;
			this.npc[index].open_max = 90f;
			this.npc[index].points = 250;
			if (t1 < 1) {
				this.npc[index].obj.tile = 0x5b;
				this.npc[index].obj.tile_top = 0x5c;
			}
			this.npc[index].ai_speed = 1f;
			this.npc[index].trail = 1;
			this.npc[index].life_max = this.npc[index].life = 2f;
			this.npc[index].ammo = 12;
			this.npc[index].kill_time = 0x55;
		} else if (npc_type == 2) {
			this.npc[index].points = 150;
			if (t1 < 1) {
				this.npc[index].obj.tile = 0xcd;
				this.npc[index].obj.tile_top = 0xce;
			}
			this.npc[index].ai_speed = 2.1f;
			this.npc[index].ammo = 14;
			this.npc[index].kill_time = 30;
		} else if (npc_type == 6) {
			this.npc[index].points = 0x7d;
			if (t1 < 1) {
				this.npc[index].obj.tile = 0xd6;
				this.npc[index].obj.tile_top = 0xd7;
			}
			this.npc[index].ai_speed = 1.75f;
			this.npc[index].ammo = 0x10;
			this.npc[index].kill_time = 30;
		}
		this.npc[index].ai_speed *= mod;
		if (this.npc[index].ai_speed <= 0f) {
			this.npc[index].ai_speed = 0.1f;
		}
		this.npc[index].shoot_min += this.npc[index].shoot_min
				- ((int) (this.npc[index].shoot_min * mod));
		this.npc[index].shoot_max += this.npc[index].shoot_max
				- ((int) (this.npc[index].shoot_max * mod));
		if (this.npc[index].shoot_min < 15) {
			this.npc[index].shoot_min = 15;
		}
		if (this.npc[index].shoot_max < 15) {
			this.npc[index].shoot_max = 15;
		}
		this.npc[index].points = (int) (this.npc[index].points * mod);
		this.npc[index].obj.loc.fw = this.npc[index].obj.loc.w = this.tile[this.npc[index].obj.tile].w;
		this.npc[index].obj.loc.fh = this.npc[index].obj.loc.h = this.tile[this.npc[index].obj.tile].h;
		this.npc[index].obj.cx = this.tile[this.npc[index].obj.tile].w / 2;
		this.npc[index].obj.cy = this.tile[this.npc[index].obj.tile].h / 2;
	}

	public void set_rewards() {
		if (this.me.rewards == null) {
			this.me.rewards = new reward_struct();
		}
		for (int i = 0; i < this.levels; i++) {
			for (int j = 0; j < this.level[i].stars; j++) {
				if ((this.level[i].star_reward[j] >= 0)
						&& (this.level[i].high_score >= this.level[i].star_score[j])) {
					if (!(this.reward[this.level[i].star_reward[j]].drop_rate == 0f)) {
						this.me.rewards.drop_rate = this.reward[this.level[i].star_reward[j]].drop_rate;
					}
					if (!(this.reward[this.level[i].star_reward[j]].life == 0f)) {
						this.me.rewards.life = this.me.life = this.me.life_max += this.reward[this.level[i].star_reward[j]].life;
					}
					if (!(this.reward[this.level[i].star_reward[j]].mitigate == 0f)) {
						this.me.rewards.mitigate = this.reward[this.level[i].star_reward[j]].mitigate;
					}
					if (!(this.reward[this.level[i].star_reward[j]].score_increase == 0f)) {
						this.me.rewards.score_increase = this.reward[this.level[i].star_reward[j]].score_increase;
					}
					if (!(this.reward[this.level[i].star_reward[j]].weapon_power == 0f)) {
						this.me.rewards.weapon_power = this.reward[this.level[i].star_reward[j]].weapon_power;
					}
					if (!(this.reward[this.level[i].star_reward[j]].weapon_speed == 0f)) {
						this.me.rewards.weapon_speed = this.reward[this.level[i].star_reward[j]].weapon_speed;
					}
				}
			}
		}
	}

	public void start_level(int l) {
		int num3;
		int num4;
		int num5;
		this.me.level = l;
		if (float_number == null) {
			this.float_number = new float_number_struct[this.float_numbers];
		}
		for (int i = 0; i < float_number.length; i++) {
			if (float_number[i] == null) {
				float_number[i] = new float_number_struct();
			}
		}
		if (obj == null) {
			this.obj = new object_struct[this.objects];
		}
		for (int i = 0; i < obj.length; i++) {
			if (obj[i] == null) {
				obj[i] = new object_struct();
			}
		}
		if (button == null) {
			this.button = new button_struct[this.buttons];
		}
		for (int i = 0; i < button.length; i++) {
			if (button[i] == null) {
				button[i] = new button_struct();
			}
		}
		if (shot == null) {
			this.shot = new shot_struct[this.shots];
		}
		for (int i = 0; i < shot.length; i++) {
			if (shot[i] == null) {
				shot[i] = new shot_struct();
			}
		}
		if (shot_group == null) {
			this.shot_group = new int[this.shots][this.group_shots];
		}
		if (npc == null) {
			this.npc = new npc_struct[this.npcs];
		}
		for (int i = 0; i < npc.length; i++) {
			if (npc[i] == null) {
				npc[i] = new npc_struct();
			}
		}
		int index = 0;
		this.level[index].y_scroll = 3;
		this.level[index].name = "green";
		this.level[index].music = "music_level_1";
		this.level[index].wave_total = 100f;
		this.level[index].bg_tile = 100;
		this.level[index].mid_tile = 0x26;
		this.level[index].mid_alpha = 190;
		this.level[index].grid_y = 50;
		if (this.level[index].grid == null) {
			this.level[index].grid = new int[15][this.level[index].grid_y];
		}
		int[][] numArray = new int[15][this.level[index].grid_y];
		for (num5 = 0; num5 < this.level[index].grid_y; num5++) {
			num4 = 0;
			while (num4 < 15) {
				num3 = random.Next(0, 100);
				if (num3 < 0x4b) {
					num3 = 0x73;
				} else if (num3 < 0x5d) {
					num3 = 0x7c;
				} else if (num3 < 0x61) {
					num3 = 0x7d;
				} else {
					num3 = 0x7e;
				}
				numArray[num4][num5] = num3;
				num4++;
			}
		}
		this.level[index].grid = numArray;
		index = 1;
		this.level[index].name = "Snow";
		this.level[index].music = "music_level_1";
		this.level[index].y_scroll = 4;
		this.level[index].wave_total = 200f;
		this.level[index].bg_tile = 100;
		this.level[index].mid_tile = 0x26;
		this.level[index].mid_alpha = 0xff;
		this.level[index].grid_y = 50;
		this.level[index].grid = new int[15][this.level[index].grid_y];
		numArray = new int[15][this.level[index].grid_y];
		for (num5 = 0; num5 < this.level[index].grid_y; num5++) {
			num4 = 0;
			while (num4 < 15) {
				num3 = random.Next(0, 100);
				if (num3 < 0x4b) {
					num3 = 130;
				} else if (num3 < 0x5d) {
					num3 = 0x7f;
				} else if (num3 < 0x61) {
					num3 = 0x80;
				} else {
					num3 = 0x81;
				}
				numArray[num4][num5] = num3;
				num4++;
			}
		}
		this.level[index].grid = CollectionUtils.copyOf(numArray);
		index = 2;
		this.level[index].y_scroll = 5;
		this.level[index].name = "Ocean";
		this.level[index].music = "music_level_1";
		this.level[index].wave_total = 275f;
		this.level[index].bg_tile = 100;
		this.level[index].mid_tile = 0x26;
		this.level[index].mid_alpha = 200;
		this.level[index].grid_y = 50;
		this.level[index].grid = new int[15][this.level[index].grid_y];
		numArray = new int[15][this.level[index].grid_y];
		for (num5 = 0; num5 < this.level[index].grid_y; num5++) {
			num4 = 0;
			while (num4 < 15) {
				num3 = 100 + random.Next(0, 3);
				numArray[num4][num5] = num3;
				num4++;
			}
		}
		this.level[index].grid = CollectionUtils.copyOf(numArray);
		index = 0x83;
		this.tile[index].x = 0;
		this.tile[index].y = 0x1a0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x84;
		this.tile[index].x = 0;
		this.tile[index].y = 0x1c0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x85;
		this.tile[index].x = 0;
		this.tile[index].y = 480;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x86;
		this.tile[index].x = 0x40;
		this.tile[index].y = 0x1a0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x87;
		this.tile[index].x = 0x40;
		this.tile[index].y = 0x1c0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x88;
		this.tile[index].x = 0x40;
		this.tile[index].y = 480;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 0x89;
		this.tile[index].x = 32;
		this.tile[index].y = 0x1c0;
		this.tile[index].w = this.tile[index].h = 32;
		this.tile[index].texture = 0;
		index = 3;
		this.level[index].y_scroll = 6;
		this.level[index].name = "Desert";
		this.level[index].music = "music_level_1";
		this.level[index].wave_total = 350f;
		this.level[index].bg_tile = 100;
		this.level[index].mid_tile = 0x26;
		this.level[index].mid_alpha = 250;
		this.level[index].grid_y = 50;
		this.level[index].grid = new int[15][this.level[index].grid_y];
		numArray = new int[15][this.level[index].grid_y];
		for (num5 = 0; num5 < this.level[index].grid_y; num5++) {
			num4 = 0;
			while (num4 < 15) {
				num3 = 0x89;
				switch (num4) {
				case 0:
					if (((random.Next(0, 100) < 0x19) && (num5 < (this.level[index].grid_y - 1)))
							&& (num5 > 0)) {
						num3 = 0x85;
						if (numArray[num4][num5] == 0) {
							numArray[num4][num5 + 1] = 0x83;
						}
					} else {
						num3 = 0x84;
					}
					break;

				case 14:
					if (((random.Next(0, 100) < 0x19) && (num5 < (this.level[index].grid_y - 1)))
							&& (num5 > 0)) {
						num3 = 0x88;
						if (numArray[num4][num5] == 0) {
							numArray[num4][num5 + 1] = 0x86;
						}
					} else {
						num3 = 0x87;
					}
					break;
				}
				if (numArray[num4][num5] == 0) {
					numArray[num4][num5] = num3;
				}
				num4++;
			}
		}
		this.level[index].grid = CollectionUtils.copyOf(numArray);
		index = 4;
		this.level[index].y_scroll = 7;
		this.level[index].name = "Field";
		this.level[index].music = "music_level_1";
		this.level[index].wave_total = 425f;
		this.level[index].bg_tile = 100;
		this.level[index].mid_tile = 0x26;
		this.level[index].mid_alpha = 200;
		this.level[index].grid_y = 50;
		this.level[index].grid = new int[15][this.level[index].grid_y];
		numArray = new int[15][this.level[index].grid_y];
		for (num5 = 0; num5 < this.level[index].grid_y; num5++) {
			for (num4 = 0; num4 < 15; num4++) {
				num3 = 0x93;
				switch (num4) {
				case 0:
					if (((random.Next(0, 100) < 0x19) && (num5 < (this.level[index].grid_y - 1)))
							&& (num5 > 0)) {
						num3 = 0x8f;
						if (numArray[num4][num5] == 0) {
							numArray[num4][num5 + 1] = 0x8d;
						}
					} else {
						num3 = 0x8e;
					}
					break;

				case 14:
					if (((random.Next(0, 100) < 0x19) && (num5 < (this.level[index].grid_y - 1)))
							&& (num5 > 0)) {
						num3 = 0x92;
						if (numArray[num4][num5] == 0) {
							numArray[num4][num5 + 1] = 0x90;
						}
					} else {
						num3 = 0x91;
					}
					break;
				}
				if (numArray[num4][num5] == 0) {
					numArray[num4][num5] = num3;
				}
			}
		}
		this.level[index].grid = CollectionUtils.copyOf(numArray);
		index = 5;
		this.level[index].y_scroll = 8;
		this.level[index].name = "Space";
		this.level[index].music = "music_level_1";
		this.level[index].wave_total = 500f;
		this.level[index].bg_tile = 100;
		this.level[index].mid_tile = 0xe0;
		this.level[index].mid_alpha = 200;
		this.level[index].grid_y = 50;
		this.level[index].grid = new int[15][this.level[index].grid_y];
		index = 6;
		this.level[index].y_scroll = 8;
		this.level[index].name = "Space - Marathon";
		this.level[index].music = "music_level_1";
		this.level[index].wave_total = 250f;
		this.level[index].bg_tile = 100;
		this.level[index].mid_tile = 0xe0;
		this.level[index].mid_alpha = 200;
		this.level[index].grid_y = 50;
		this.level[index].grid = new int[15][this.level[index].grid_y];
		if (this.level[this.me.level].y_scroll < 0) {
			this.me.obj.loc.rot = this.me.obj.dest.rot = 180f;
			this.me.obj.loc.fy = -(this.me.obj.loc.h * 2);
			this.me.obj.dest.fy = 150f;
		} else if (this.level[this.me.level].y_scroll > 0) {
			this.me.obj.loc.fy = 0x2d1 + (this.me.obj.loc.h * 2);
			this.me.obj.dest.fy = 571f;
		} else {
			this.me.obj.loc.fy = getHeight() / 2;
			this.me.obj.dest.fy = this.me.obj.loc.fy;
		}
		this.me.obj.loc.fx = this.me.obj.dest.fx = 240f;
		this.me.obj.loc.x = this.me.obj.dest.x = 240;
		this.ammo_start = 0;
		this.me.score = this.me.score_display = 0;
		this.me.shield = 0;
		this.me.ammo = this.ammo_start;
		this.me.shots = 100;
		this.me.bomb = -1;
		this.me.life = this.me.life_max = 3f;
		this.me.streak = 0;
		this.me.combo = 0;
		this.me.last_shot = 0;
		this.me.power_ball = 0;
		this.me.dead = 0;
		this.me.immune = 0;
		this.me.win = 0;
		this.next_save = 0;
		this.prog_frame = 0;
		this.pause_frame = 0;
		this.waves = 0;
		this.wave_total = 0f;
		this.got_weapon = 0;
		this.win_end_score = 0;
		this.win_new_reward = 0;
		this.level[this.me.level].y = 0;
		this.level[this.me.level].frame = 0;
		this.level[this.me.level].high_score_reached = 0;
		this.level[this.me.level].pause = 0;
		this.level[this.me.level].won = 0;
		this.fn_score = -1;
		this.fn_accuracy = -1;
		this.fn_streak = -1;
		this.float_number = new float_number_struct[this.float_numbers];
		for (int i = 0; i < float_number.length; i++) {
			if (float_number[i] == null) {
				float_number[i] = new float_number_struct();
			}
		}
		num3 = this.add_float_number(0x89, 0x2f5, LColor.orange, 1, 0.5f, -1f,
				0);
		if (num3 >= 0) {
			this.fn_streak = num3;
		}
		num3 = this.add_float_number(0x11c, 0x2f5, LColor.yellow, 1, 0.5f, -1f,
				0);
		if (num3 >= 0) {
			this.fn_score = num3;
		}
		num3 = this.add_float_number(430, 0x2f5, LColor.white, 1, 0.5f, -1f, 0);
		if (num3 >= 0) {
			this.fn_accuracy = num3;
		}
		this.set_rewards();
	}

	public void start_music(String mname) {

	}

	public Vector2f start_point() {
		float num5;
		float num6;
		int num4 = random.Next(4);
		if (num4 == 0) {
			num5 = 0x22b + random.Next(50);
			num6 = -125 + random.Next(240);
		} else if (num4 == 1) {
			num5 = -75 - random.Next(50);
			num6 = -125 + random.Next(240);
		} else {
			num5 = -100 + random.Next(0x2a9);
			num6 = -75 - random.Next(0x4b);
		}
		return new Vector2f(num5, num6);
	}

	private loc_struct update_loc(loc_struct src, loc_struct dst) {
		loc_struct source = src.cpy();
		loc_struct dest = dst.cpy();
		float num2;
		float num3 = 0f;
		float num16 = 0f;
		source.dest_reached = 0;
		if (source.hold > 0) {
			source.hold--;
			if (source.hold < 0) {
				source.hold = 0;
			}
			return source;
		}
		if ((source.fx != dest.fx) || (source.fy != dest.fy)) {
			dest.rot = this.get_angle(source.fx, source.fy, dest.fx, dest.fy);
			float num14 = MathUtils.abs((source.fx - dest.fx));
			float num15 = MathUtils.abs((source.fy - dest.fy));
			num16 = num14;
			if (num15 > num14) {
				num16 = num15;
			}
		}
		if ((source.rot != dest.rot) && (source.lock_rot != 1)) {
			if ((source.turn_speed == 0f)
					|| ((num16 > 0f) && ((num16 < source.sw) || (num16 < source.sh)))) {
				source.rot = dest.rot;
			} else {
				float num9 = source.turn_speed;
				if ((num9 == -1f) && (num16 > 100f)) {
					if (num16 > 500f) {
						num16 = 500f;
					}
					num3 = (500f - num16) / 500f;
					if (num3 > 10f) {
						num3 = 10f;
					}
					if (num3 < 1f) {
						num3 = 1f;
					}
					num9 = num3;
				}
				float num27 = 360f;
				if ((source.rot > 270f) && (dest.rot < 90f)) {
					num27 = (360f - source.rot) + dest.rot;
				} else if ((dest.rot > 270f) && (source.rot < 90f)) {
					num27 = (360f - dest.rot) + source.rot;
				} else {
					num27 = MathUtils.abs((dest.rot - source.rot));
				}
				if (num27 < source.turn_speed) {
					source.rot = dest.rot;
				} else {
					if (source.rot < 180f) {
						if ((dest.rot >= (source.rot + 180f))
								|| (dest.rot <= source.rot)) {
							num9 = -num9;
						}
					} else if ((dest.rot >= (source.rot - 180f))
							&& (dest.rot <= source.rot)) {
						num9 = -num9;
					}
					source.rot += num9;
					if (source.rot < 0f) {
						source.rot += 360f;
					}
					if (source.rot >= 360f) {
						source.rot -= 360f;
					}
				}
			}
		}
		if (((source.fx != dest.fx) || (source.fy != dest.fy))
				|| (source.lock_rot == 1)) {
			Vector2f vector = this.get_location(source.fx, source.fy,
					source.rot, source.speed);
			source.fx = vector.x;
			source.fy = vector.y;
			if (((((source.fx - dest.fx) < (source.speed / 2f)) && ((source.fx - dest.fx) >= (-source.speed / 2f))) && ((source.fy - dest.fy) < (source.speed / 2f)))
					&& ((source.fy - dest.fy) >= (-source.speed / 2f))) {
				source.fx = dest.fx;
				source.fy = dest.fy;
			}
			if ((source.fx == dest.fx) && (source.fy == dest.fy)) {
				source.dest_reached = 1;
			}
		}
		source.x = (int) source.fx;
		source.y = (int) source.fy;
		if ((source.alpha_hold == 0) && (source.alpha != dest.alpha)) {
			num2 = dest.alpha - source.alpha;
			if (!(source.dur == -1f)) {
				num3 = num2 / source.dur;
			} else {
				num3 = num2 / 10f;
			}
			source.alpha += (int) num3;
			if (source.alpha > 0xff) {
				source.alpha = 0xff;
			}
			if (source.alpha < 0) {
				source.alpha = 0;
			}
		}
		if ((source.scale_hold == 0) && (source.scale != dest.scale)) {
			num2 = dest.scale - source.scale;
			if (!(source.dur == -1f)) {
				num3 = num2 / source.dur;
			} else {
				num3 = num2 / 10f;
			}
			source.scale += num3;
			if (source.scale < 0f) {
				source.scale = 0f;
			}
		}
		if (source.speed_up != 0f) {
			if (source.speed < dest.speed) {
				source.speed += source.speed_up;
				if (source.speed >= dest.speed) {
					source.speed = dest.speed;
				}
			}
			if (source.speed > dest.speed) {
				source.speed += source.speed_up;
				if (source.speed <= dest.speed) {
					source.speed = dest.speed;
				}
			}
		}
		source.sw = source.fw * source.scale;
		source.sh = source.fh * source.scale;
		source.spin += source.spin_speed;
		if (source.alpha_hold > 0) {
			source.alpha_hold--;
		}
		if (source.scale_hold > 0) {
			source.scale_hold--;
		}
		if (source.dur > 0f) {
			source.dur--;
			if (source.dur < 0f) {
				source.dur = 0f;
			}
		}
		return source;
	}

	private LColor gray = new LColor(1f, 1f, 1f, 1f);

	@Override
	public void draw(SpriteBatch batch) {

		if(!isOnLoadComplete()){
			return;
		}
		int x;
		int y;
		int num5;
		int num6;
		int level;
		int alpha;
		int tile;
		float hold;
		float num19;
		String text;
		Vector2f vector;

		if (this.mode == 5) {
			y = getHeight() - 40;
			text = "Loading...";
			vector = font.getOrigin(text);
			batch.drawString(this.font, text, getHalfWidth(), y,
					LColor.darkGray, 0f, vector.x, vector.y, 0.5f);
		} else if (this.mode == 2) {
			x = y = 0;
			batch.draw(this.texture[2], x, y, getWidth(), getHeight(), x, y,
					getWidth(), getHeight(), LColor.white, 0f, 0, 0,
					SpriteEffects.None);
			y = getHeight() - 40;

		} else if (this.mode == 10) {
			batch.draw(this.texture[5], 0, 0, getWidth(), getHeight(), 0, 0,
					getWidth(), getHeight(), Pool.getColor(50, 50, 50, 50), 0f,
					0f, 0f, SpriteEffects.None);
			x = y = 0;
			text = "Game Settings";
			batch.drawString(this.font, text, 0f, y, LColor.white, 0f, 0f, 0f,
					1f);
			y += 50;
			text = "Sound:";
			batch.drawString(this.font, text, 10f, (y += 50),
					LColor.lightYellow, 0f, 0, 0, 0.75f);
			text = "Toggle sound effects on or off.\n(this can also be toggled when the game is paused)";
			batch.drawString(this.font, text, 10f, (y += 0x4c),
					LColor.lightBlue, 0f, 0, 0, 0.6f);
			y += 50;
			text = "Music:";
			batch.drawString(this.font, text, 10f, (y += 50),
					LColor.lightYellow, 0f, 0, 0, 0.75f);
			text = "Toggle game music on or off.\n(this can also be toggled when the game is paused)\n\nNOTE: Game music will automatically be toggled off\nif your own music is playing.";
			batch.drawString(this.font, text, 10f, (y += 0x4c),
					LColor.lightBlue, 0f, 0, 0, 0.6f);
		} else if (this.mode == 9) {
			batch.draw(this.texture[5], 0, 0, getWidth(), getHeight(), 0, 0,
					getWidth(), getHeight(), Pool.getColor(50, 50, 50, 50), 0f,
					0, 0, SpriteEffects.None);
			x = y = 0;
			text = "How to Play:";
			batch.drawString(this.font, text, 0f, y, LColor.white);
			text = "Movement:";
			batch.drawString(this.font, text, 0f, (y += 0x2d),
					LColor.lightYellow, 0f, 0, 0, 0.75f);
			text = "Swipe the screen in the direction you wish to fly.";
			batch.drawString(this.font, text, 0f, (y += 30), LColor.lightBlue,
					0f, 0, 0, 0.6f);
			text = "Weapons & Firing:";
			batch.drawString(this.font, text, 0f, (y += 0x19),
					LColor.lightYellow, 0f, 0, 0, 0.75f);
			text = "Tap the screen quickly in the direction you wish to fire.\n- Weapon upgrades may appear throughout the level.\n- Rockets and Missiles can be 'locked' onto an enemy.";
			batch.drawString(this.font, text, 0f, (y += 30), LColor.lightBlue,
					0f, 0, 0, 0.6f);
			text = "Levels:";
			batch.drawString(this.font, text, 0f, (y += 0x4b),
					LColor.lightYellow, 0f, 0, 0, 0.75f);
			text = "Levels unlock when the previous level is completed.";
			batch.drawString(this.font, text, 0f, (y += 30), LColor.lightBlue,
					0f, 0, 0, 0.6f);
			text = "Scoring:";
			batch.drawString(this.font, text, 0f, (y += 0x19),
					LColor.lightYellow, 0f, 0, 0, 0.75f);
			text = "Points are awarded for enemies destroyed.\nAdditional 'streak' points are displayed in orange and are\nawarded when no shots have been missed between enemy\nkills. The streak point bonus grows with each enemy\ndestroyed but resets to 0 when a shot misses completely.\nCombo points are displayed in green and are awarded\nwhen multiple enemies are killed with a single shot.";
			batch.drawString(this.font, text, 0f, (y += 30), LColor.lightBlue,
					0f, 0, 0, 0.6f);
			y = 0x1d2;
			text = "Star Rankings and Rewards:";
			batch.drawString(this.font, text, 0f, y, LColor.lightYellow, 0f, 0,
					0, 0.75f);
			text = "Star rankings for each level can be achieved by reaching\na target score for that level. Permanent rewards and\nupgrades are obtained by achieving higher star rankings.\nThese permanent rewards are displayed when you select\na level to play. NOTE: You must complete the level to\nrecord your high score and star ranking for that level.";
			batch.drawString(this.font, text, 0f, (y += 30), LColor.lightBlue,
					0f, 0, 0, 0.6f);
			text = "Copyright (c) 2011 Nathan Darst\nnathandarst@gmail.com";
			batch.drawString(this.font, text, 240f, 660f, LColor.lightGray, 0f,
					0, 0, 0.5f);
			text = "Your feedback is appreciated!\nYour comment and rating can\nbe a big help with future updates.";
			batch.drawString(this.font, text, 0f, 720f, LColor.lightGreen, 0f,
					0, 0, 0.6f);
		} else if (this.mode == 3) {
			x = y = 0;
			batch.draw(this.texture[5], x, y, getWidth(), getHeight(), x, y,
					getWidth(), getHeight(), LColor.white, 0f, 0f, 0f,
					SpriteEffects.None);
			tile = 0xcc;
			batch.draw(this.texture[this.tile[tile].texture], 24f, 20f,
					this.tile[tile].x, this.tile[tile].y, this.tile[tile].w,
					this.tile[tile].h, LColor.white);
			y = getHeight() - 40;
		} else {
			float rot;
			if (this.mode == 6) {
				x = y = 0;
				batch.draw(this.texture[5], x, y, getWidth(), getHeight(), x,
						y, getWidth(), getHeight(), LColor.white, 0f, 0, 0,
						SpriteEffects.None);
				text = "Choose a difficulty level:";
				vector = font.getOrigin(text);
				batch.drawString(this.font, text, 202f, 22f, LColor.black, 0f,
						0, 0, 0.75f);
				batch.drawString(this.font, text, 200f, 20f, LColor.white, 0f,
						0, 0, 0.75f);
				text = "More points and higher\nstar rankings can be\nachieved by choosing a\ngreater difficulty level.";
				vector = font.getOrigin(text);
				batch.drawString(this.font, text, 202f, 62f, LColor.black, 0f,
						0, 0, 0.65f);
				batch.drawString(this.font, text, 200f, 60f, LColor.lightGreen,
						0f, 0, 0, 0.65f);
				if (this.me.level == this.lv_marathon) {
					x = 20;
					y = 0x220;
					text = "About Marathon:";
					batch.drawString(this.font, text, ((x + 9) + 2),
							((y - 32) + 2), LColor.black, 0f, 0f, 0f, 0.8f);
					batch.drawString(this.font, text, (x + 9), (y - 32),
							LColor.white, 0f, 0f, 0f, 0.8f);
					y += 40;
					text = "Marathon mode is endless play consisting of\nincreasingly difficult waves of enemies.\n\nYour high score is only recorded after\nthe current wave is complete.\n\n..How long can you last?";
					batch.drawString(this.font, text, ((x + 9) + 2),
							((y - 32) + 2), LColor.black, 0f, 0f, 0f, 0.65f);
					batch.drawString(this.font, text, (x + 9), (y - 32),
							LColor.lightGreen, 0f, 0f, 0f, 0.65f);
				} else {
				}
			} else {
				int num13;
				if (this.mode == 8) {
					x = y = 0;
					y = 0x4f;
					batch.draw(this.texture[7], x, y, getWidth(), getHeight(),
							0, 0, getWidth(), getHeight(), LColor.white, 0f, 0,
							0, SpriteEffects.None);
					x = 10;
					y += 0x87;
					if (this.me.level == (this.levels - 1)) {
						text = "Look for more new levels\nin the next game update!\nHave you achieved\n4 stars in all levels?";
						batch.drawString(this.font, text, 246f, 541f,
								LColor.black, 0f, 0, 0, 0.55f);
						batch.drawString(this.font, text, 245f, 541f,
								LColor.lime, 0f, 0, 0, 0.55f);
					}
					if (this.win_bonuses > 0) {
						text = "Bonus:";
						batch.drawString(this.font, text, (x + 10) + 1,
								((y + 0x24) + 1), LColor.black, 0f, 0, 0, 0.65f);
						batch.drawString(this.font, text, (x + 10), (y + 0x24),
								LColor.orange, 0f, 0, 0, 0.65f);
					}
					text = "Level " + (this.me.level + 1) + " ";
					if (this.me.difficulty < 1f) {
						text = text + "(EASY) ";
					} else if (this.me.difficulty == 1f) {
						text = text + "(NORMAL) ";
					} else if (this.me.difficulty > 1f) {
						text = text + "(HARD) ";
					}
					if (this.win_bonus > 0) {
						text = text + "Subtotal: "
								+ (new Integer(this.win_end_score)) + ("N00")
								+ "\n\n" + this.win_bonus_text + "\n";
						vector = new Vector2f(font.stringWidth(text));
						num13 = this.win_bonuses;
						for (num5 = 0; num5 < num13; num5++) {
							tile = 0xdd;
							batch.draw(this.texture[this.tile[tile].texture],
									(x - 8), ((y + 0x3f) + (num5 * 0x21)),
									this.tile[tile].x, this.tile[tile].y,
									this.tile[tile].w, this.tile[tile].h,
									LColor.white);
						}
					} else {
						text = text + "\n \n ";
						batch.drawString(this.font, "(no bonus points)",
								(x + 1), ((y + 32) + 1), LColor.black, 0f, 0,
								0, 0.8f);
						batch.drawString(this.font, "(no bonus points)", x,
								(y + 32), LColor.lightGray, 0f, 0, 0, 0.8f);
					}
					vector = font.getOrigin(text);

					batch.drawString(this.font, text, (x + 1), (y + 1),
							LColor.black, 0f, 0, 0, 0.8f);
					batch.drawString(this.font, text, x, y, LColor.white, 0f,
							0, 0, 0.8f);
					y += ((int) vector.y) + 20;
					this.float_number[this.fn_score].loc.fx = this.float_number[this.fn_score].loc.x = x + 200;
					this.float_number[this.fn_score].loc.fy = this.float_number[this.fn_score].loc.y = y - 5;
					text = "Total Score:";
					batch.drawString(this.font, text, (x + 1), (y + 1),
							LColor.black, 0f, 0, 0, 0.9f);
					batch.drawString(this.font, text, x, y, LColor.yellow, 0f,
							0, 0, 0.9f);
					y += 0x2d;

					if (this.me.score >= this.level[this.me.level].high_score) {
						tile = 0xe5;
						num19 = 1f + ((((this.mode_frame / 2) % 30)) / 100f);
						y += 50;
						batch.draw(this.texture[this.tile[tile].texture],
								(x + 0x48), y, this.tile[tile].x,
								this.tile[tile].y, this.tile[tile].w,
								this.tile[tile].h, LColor.white, 0f,
								(this.tile[tile].w / 2),
								(this.tile[tile].h / 2), num19,
								SpriteEffects.None);
						y += 15;
					} else {
						y += 10;
						text = "Existing High Score: "
								+ (new Integer(
										this.level[this.me.level].high_score))
								+ ("N00");
						batch.drawString(this.font, text, (x + 1), (y + 1),
								LColor.black, 0f, 0, 0, 0.75f);
						batch.drawString(this.font, text, x, y,
								LColor.lightCoral, 0f, 0, 0, 0.75f);
						y += 10;
					}

					y += 0x23;
					text = "Star Ranking:";
					batch.drawString(this.font, text, (x + 1), (y + 1),
							LColor.black, 0f, 0, 0, 0.9f);
					batch.drawString(this.font, text, x, y, LColor.lightYellow,
							0f, 0, 0, 0.9f);
					for (num5 = 0; num5 < this.level[this.me.level].stars; num5++) {
						tile = 0xdb;
						num19 = 0.75f;
						num6 = num5;
						if (this.me.score >= this.level[this.me.level].star_score[num6]) {
							if (this.me.difficulty < 1f) {
								gray = Pool.getColor(0xc6, 0x9c, 0x6d, 0xff);
							} else if (this.me.difficulty == 1f) {
								gray = Pool.getColor(0xff, 0xff, 0xff, 0xff);
							} else if (this.me.difficulty > 1f) {
								gray = Pool.getColor(0xff, 0xff, 0, 0xff);
							}
							rot = this.mode_frame % 360;

						} else {
							rot = 0f;
							alpha = 0x4b;
							gray = Pool.getColor(alpha, alpha, alpha, alpha);
						}

						batch.draw(this.texture[this.tile[tile].texture],
								((x + 0xe0) + (num6 * 0x37)), (y + 20),
								this.tile[tile].x, this.tile[tile].y,
								this.tile[tile].w, this.tile[tile].h, gray,
								rot, (this.tile[tile].w / 2),
								(this.tile[tile].h / 2), num19,
								SpriteEffects.None);
					}
					y += 0x34;
					if (this.win_new_reward == 1) {
						for (num5 = this.level[this.me.level].stars - 1; num5 >= 0; num5--) {
							if ((this.level[this.me.level].star_reward[num5] >= 0)
									&& (this.me.score >= this.level[this.me.level].star_score[num5])) {
								text = "New Reward Unlocked:";
								batch.drawString(this.font, text, (x + 1),
										(y + 1), LColor.black, 0f, 0f, 0f,
										0.75f);
								batch.drawString(this.font, text, x, y,
										LColor.lightYellow, 0f, 0f, 0f, 0.75f);
								y += 0x16;
								tile = 0xdd;
								gray = LColor.lime;
								text = this.reward[this.level[this.me.level].star_reward[num5]].text;
								batch.drawString(this.font, text, (x + 2),
										(y + 2), LColor.black, 0f, 0f, 0f, 0.8f);
								batch.drawString(this.font, text, x, y, gray,
										0f, 0f, 0f, 0.8f);
								break;
							}
						}
					} else {
						text = "(no new star rank achieved)";
						batch.drawString(this.font, text, (x + 1), (y + 1),
								LColor.black, 0f, 0f, 0f, 0.75f);
						batch.drawString(this.font, text, x, y, LColor.gray,
								0f, 0f, 0f, 0.75f);
					}
				} else if (this.mode == 7) {
					x = y = 0;
					y = 0x4f;
					batch.draw(this.texture[6], x, y, getWidth(), getHeight(),
							0, 0, getWidth(), getHeight(), LColor.white, 0f,
							0f, 0f, SpriteEffects.None);
					if (this.me.level != this.lv_marathon) {
						text = "NOTE: Your high score and star\nranking can only be recorded\nupon completing the level.";
						y = 0x29d;
						batch.drawString(this.font, text, 240f, (y + 2),
								LColor.black, 0f, 0f, 0f, 0.5f);
						batch.drawString(this.font, text, 240f, y, LColor.gray,
								0f, 0f, 0f, 0.5f);
					}
				} else {

					int num2;
					int num12;

					level = -this.level[this.me.level].y / 3;
					int index = level / 32;

				
					if (!LSystem.base().isMobile()) {

						for (y = -1; y < 26; y++) {
							for (x = 0; x < 15; x++) {
								int tx = x;
								int ty = index + y;
								if (ty < 0) {
									ty = this.level[this.me.level].grid_y
											+ (ty % this.level[this.me.level].grid_y);
									if (ty == this.level[this.me.level].grid_y) {
										ty = 0;
									}
								} else if (ty >= this.level[this.me.level].grid_y) {
									ty = ty % this.level[this.me.level].grid_y;
								}
								if ((ty < this.level[this.me.level].grid_y)
										&& (ty >= 0)) {
									tile = this.level[this.me.level].grid[tx][ty];
									if (tile > 0) {
										if ((this.tile[tile].frames > 0)
												&& (this.mode == 1)) {
											tile = this.tile[tile].frame[this.ani_frame
													% this.tile[tile].frames];
										}
										int num = x * 32;
										num2 = (y * 32) - (level % 32);
										batch.draw(
												this.texture[this.tile[tile].texture],
												num, num2, this.tile[tile].x,
												this.tile[tile].y,
												this.tile[tile].w,
												this.tile[tile].h);
									}
								}
							}
						}
						if (this.level[this.me.level].mid_tile > 0) {
							tile = this.level[this.me.level].mid_tile;
							x = 0;
							y = -getHeight();
							alpha = this.level[this.me.level].mid_alpha / 2;
							for (num2 = 0; num2 < (getHeight() * 2); num2 += getHeight()) {
								batch.draw(
										this.texture[this.tile[tile].texture],
										x,
										((y + num2) + ((this.level[this.me.level].y / 2) % this.tile[tile].h)),
										this.tile[tile].x, this.tile[tile].y,
										this.tile[tile].w, this.tile[tile].h,
										Pool.getColor(alpha, alpha, alpha,
												alpha), 0f, 0, 0, 1f,
										SpriteEffects.None);
							}
							y = -getHeight();
							alpha = this.level[this.me.level].mid_alpha / 2;
							for (num2 = 0; num2 < (getHeight() * 2); num2 += getHeight()) {
								batch.draw(
										this.texture[this.tile[tile].texture],
										x,
										((y + num2) + (this.level[this.me.level].y % this.tile[tile].h)),
										this.tile[tile].x, this.tile[tile].y,
										this.tile[tile].w, this.tile[tile].h,
										Pool.getColor(alpha, alpha, alpha,
												alpha), 0f, 0, 0, 1f,
										SpriteEffects.None);
							}
						}
					}

					num5 = 0;
					while (num5 < this.npcs) {
						if ((this.npc[num5].active == 1)
								&& (this.npc[num5].obj.loc.hold == 0)) {
							tile = this.npc[num5].obj.tile;
							if ((this.tile[tile].frames > 0)
									&& (this.mode == 1)) {
								tile = this.tile[tile].frame[this.ani_frame
										% this.tile[tile].frames];
							}
							if (this.npc[num5].obj.loc.show_rot == 1) {
								rot = this.npc[num5].obj.loc.rot;
							} else {
								rot = 0f;
							}
							rot += this.npc[num5].obj.loc.spin;

							x = this.npc[num5].obj.loc.x;
							y = this.npc[num5].obj.loc.y;
							if ((this.npc[num5].shake > 0) && (this.mode == 1)) {
								x += -3 + random.Next(7);
								y += -3 + random.Next(7);
								alpha = 0x7d;
							}
							alpha = 0x23;
							if (y < -150) {
								alpha = 0;
							} else if (y < 0) {
								alpha = 0x23 - ((int) (35f * (this.npc[num5].obj.loc.fy / -150f)));
							}
							gray = Pool.getColor(0, 0, 0,
									(float) (alpha / 255f));
							num19 = this.npc[num5].obj.loc.scale / 2f;
							if (this.me.level != 5) {
								batch.draw(
										this.texture[this.tile[tile].texture],
										x, (y + 150), this.tile[tile].x,
										this.tile[tile].y, this.tile[tile].w,
										this.tile[tile].h, gray, rot,
										this.npc[num5].obj.cx,
										this.npc[num5].obj.cy, num19,
										SpriteEffects.None);
							}
							if (this.npc[num5].shake > 0) {
								alpha = (int) (500f * ((this.npc[num5].shake) / 10f));
								if (((this.npc[num5].shake / 2) % 2) == 0) {
									gray = Pool
											.getColor(
													this.npc[num5].obj.loc.color.r,
													this.npc[num5].obj.loc.color.g,
													this.npc[num5].obj.loc.color.b,
													(float) (this.npc[num5].obj.loc.alpha / 255f));
								} else {
									gray = Pool
											.getColor(
													this.npc[num5].obj.loc.color.r,
													this.npc[num5].obj.loc.color.g,
													this.npc[num5].obj.loc.color.b,
													(float) (this.npc[num5].obj.loc.alpha / 255f));
								}
							} else {
								gray = Pool
										.getColor(
												this.npc[num5].obj.loc.color.r,
												this.npc[num5].obj.loc.color.g,
												this.npc[num5].obj.loc.color.b,
												(float) (this.npc[num5].obj.loc.alpha / 255f));
							}
							batch.draw(this.texture[this.tile[tile].texture],
									x, y, this.tile[tile].x, this.tile[tile].y,
									this.tile[tile].w, this.tile[tile].h, gray,
									rot, this.npc[num5].obj.cx,
									this.npc[num5].obj.cy,
									this.npc[num5].obj.loc.scale,
									SpriteEffects.None);
							if (this.npc[num5].obj.tile_top > 0) {
								if (this.npc[num5].shake > 0) {
									alpha = (int) (500f * ((this.npc[num5].shake) / 10f));
									if (((this.npc[num5].shake / 2) % 2) == 0) {
										gray = Pool
												.getColor(
														this.npc[num5].obj.top_color.r,
														this.npc[num5].obj.top_color.g,
														this.npc[num5].obj.top_color.b,
														(float) (this.npc[num5].obj.loc.alpha / 255f));
									} else {
										gray = Pool
												.getColor(
														this.npc[num5].obj.top_color.r,
														this.npc[num5].obj.top_color.g,
														this.npc[num5].obj.top_color.b,
														(float) (this.npc[num5].obj.loc.alpha / 255f));
									}
								} else {
									gray = Pool
											.getColor(
													this.npc[num5].obj.top_color.r,
													this.npc[num5].obj.top_color.g,
													this.npc[num5].obj.top_color.b,
													(float) (this.npc[num5].obj.loc.alpha / 255f));
								}
								tile = this.npc[num5].obj.tile_top;
								batch.draw(
										this.texture[this.tile[tile].texture],
										x, y, this.tile[tile].x,
										this.tile[tile].y, this.tile[tile].w,
										this.tile[tile].h, gray, rot,
										this.npc[num5].obj.cx,
										this.npc[num5].obj.cy,
										this.npc[num5].obj.loc.scale,
										SpriteEffects.None);
							}
							if (this.npc[num5].boost_tile > 0) {
								tile = this.npc[num5].boost_label;
								if (tile > 0) {
									batch.draw(
											this.texture[this.tile[tile].texture],

											x,
											y
													- (this.npc[num5].obj.loc.fh / 2f),
											this.tile[tile].x,
											this.tile[tile].y,
											this.tile[tile].w,
											this.tile[tile].h, LColor.white,
											rot, (this.tile[tile].w / 2),
											(this.tile[tile].h / 2), 1f,
											SpriteEffects.None);
								}
								tile = this.npc[num5].boost_tile;
								batch.draw(
										this.texture[this.tile[tile].texture],
										x, y, this.tile[tile].x,
										this.tile[tile].y, this.tile[tile].w,
										this.tile[tile].h,
										this.npc[num5].boost_tile_color, 0f,
										(this.tile[tile].w / 2),
										(this.tile[tile].h / 2), 1f,
										SpriteEffects.None);
							}
							if (this.npc[num5].shield > 0f) {
								tile = 0x27;
								alpha = 0x7d;
								num19 = this.npc[num5].shield / 192f;
								if (this.mode == 1) {
									gray = Pool
											.getColor(random.Next(0x100)
													- (0xff - alpha),
													random.Next(0x100)
															- (0xff - alpha),
													random.Next(0x100)
															- (0xff - alpha),
													alpha);
								} else {
									gray = LColor.pink;
								}
								batch.draw(
										this.texture[this.tile[tile].texture],
										x, y, this.tile[tile].x,
										this.tile[tile].y, this.tile[tile].w,
										this.tile[tile].h, gray, rot,
										(this.tile[tile].w / 2),
										(this.tile[tile].h / 2), num19,
										SpriteEffects.None);
							}
							if (this.npc[num5].shielding >= 0) {
								tile = 0x38;
								hold = 4f;
								for (level = 0; level < hold; level++) {
									float distance = (this.npc[this.npc[num5].shielding].shield / 2f)
											+ (level * ((this.npc[num5].ai_circle_distance - (this.npc[this.npc[num5].shielding].shield / 2f)) / hold));
									vector = this
											.get_location(
													this.npc[this.npc[num5].shielding].obj.loc.fx,
													this.npc[this.npc[num5].shielding].obj.loc.fy,
													this.npc[num5].ai_circle_angle,
													distance);
									alpha = 100;
									if (this.mode == 1) {
										gray = Pool
												.getColor(
														random.Next(0x100)
																- (0xff - alpha),
														random.Next(0x100)
																- (0xff - alpha),
														random.Next(0x100)
																- (0xff - alpha),
														alpha);
									} else {
										gray = LColor.yellow;
									}
									batch.draw(
											this.texture[this.tile[tile].texture],
											vector.x,
											vector.y,
											this.tile[tile].x,
											this.tile[tile].y,
											this.tile[tile].w,
											this.tile[tile].h,
											gray,
											rot,

											(this.tile[tile].w / 2),
											(this.tile[tile].h / 2),
											(this.npc[num5].obj.loc.scale / 2f),
											SpriteEffects.None);
								}
							}
							if (this.npc[num5].life < this.npc[num5].life_max) {
								num19 = this.npc[num5].life
										/ this.npc[num5].life_max;
								alpha = 0xaf;
								tile = 60;
								batch.draw(
										this.texture[this.tile[tile].texture],

										x
												- ((int) (((this.tile[tile].w) / 4f) / 2f)),
										(y - ((int) (this.npc[num5].obj.loc.sh / 2f))) - 20,
										this.tile[tile].w / 4,
										this.tile[tile].h / 4,
										this.tile[tile].x, this.tile[tile].y,
										this.tile[tile].w, this.tile[tile].h,
										Pool.getColor(alpha, alpha, alpha,
												alpha));
								tile = 0x3d;
								batch.draw(
										this.texture[this.tile[tile].texture],

										x
												- ((int) (((this.tile[tile].w) / 4f) / 2f)),
										(y - ((int) (this.npc[num5].obj.loc.sh / 2f))) - 20,
										(int) ((this.tile[tile].w / 4) * num19),
										this.tile[tile].h / 4,
										this.tile[tile].x, this.tile[tile].y,
										this.tile[tile].w, this.tile[tile].h,
										Pool.getColor(alpha, 0, 0, alpha));
							}
						}
						num5++;
					}
					if ((this.mode != 2) && (this.me.dead == 0)) {
						x = this.me.obj.loc.x;
						y = this.me.obj.loc.y;
						alpha = 0xff;
						if ((this.me.shake > 0) && (this.mode == 1)) {
							x += -3 + random.Next(7);
							y += -3 + random.Next(7);
							alpha = 0x7d;
						}
						tile = this.me.obj.tile;
						if ((this.tile[tile].frames > 0) && (this.mode == 1)) {
							tile = this.tile[tile].frame[this.ani_frame
									% this.tile[tile].frames];
						}
						if (this.me.obj.loc.show_rot == 1) {
							rot = this.me.obj.loc.rot;
						} else {
							rot = 0f;
						}

						gray = Pool.getColor(0, 0, 0, 0x23);
						num19 = this.me.obj.loc.scale / 2f;
						if (this.me.level != 5) {
							batch.draw(this.texture[this.tile[tile].texture],
									x, (y + 150), this.tile[tile].x,
									this.tile[tile].y, this.tile[tile].w,
									this.tile[tile].h, gray, rot,
									this.me.obj.cx, this.me.obj.cy, num19,
									SpriteEffects.None);
						}
						if (this.me.immune > 0) {
							tile = 0x27;
							alpha = 0x7d;
							num19 = ((70 + (this.level[this.me.level].frame % 20))) / 192f;
							if (this.mode == 1) {
								gray = Pool.getColor(random.Next(0x100)
										- (0xff - alpha), random.Next(0x100)
										- (0xff - alpha), random.Next(0x100)
										- (0xff - alpha), alpha);
							} else {
								gray = LColor.yellow;
							}
							batch.draw(this.texture[this.tile[tile].texture],
									x, y, this.tile[tile].x, this.tile[tile].y,
									this.tile[tile].w, this.tile[tile].h, gray,
									rot, (this.tile[tile].w / 2),
									(this.tile[tile].h / 2), num19,
									SpriteEffects.None);

						}

						tile = this.me.obj.tile;
						gray = Pool.getColor(alpha, alpha, alpha, alpha);
						if ((this.me.immune > 0)
								&& ((this.level[this.me.level].frame % 2) == 0)) {
							gray = random_color();
						}
						batch.draw(this.texture[this.tile[tile].texture], x, y,
								this.tile[tile].x, this.tile[tile].y,
								this.tile[tile].w, this.tile[tile].h, gray,
								rot, this.me.obj.cx, this.me.obj.cy,
								this.me.obj.loc.scale, SpriteEffects.None);

						if (this.me.power_ball > 0) {
							tile = 0x38;
							index = 0;
							while (index < this.me.power_ball) {
								gray = LColor.lime;
								rot = (this.me.obj.loc.rot - 90f)
										+ (index * 180);
								vector = this.get_location(this.me.obj.loc.x,
										this.me.obj.loc.y, rot, 60f);
								batch.draw(
										this.texture[this.tile[tile].texture],
										vector.x, vector.y, this.tile[tile].x,
										this.tile[tile].y, this.tile[tile].w,
										this.tile[tile].h, gray, 0f,
										(this.tile[tile].w / 2),
										(this.tile[tile].h / 2),
										this.me.obj.loc.scale,
										SpriteEffects.None);
								index++;
							}
						}
					}
					batch.submit();
					batch.draw(this.texture[3], 0, 780, 480, 20, 0, 0, 480, 20,
							Pool.getColor(0, 0, 0, 180), 0f, 0f, 0f,
							SpriteEffects.None);
					if (this.me.level == this.lv_marathon) {
						text = "Marathon - Wave " + this.waves;
					} else {
						num12 = (int) (480f * (this.wave_total / (this.level[this.me.level].wave_total * this.me.difficulty)));
						batch.draw(this.texture[3], 0, 780, num12, 20, 0, 0,
								480, 20, Pool.getColor(0, 50, 0, 50), 0f, 0, 0,
								SpriteEffects.None);
						text = "Level " + (this.me.level + 1);
					}
					batch.drawString(this.font, text, 10f, 780f,
							Pool.getColor(0xff, 0xff, 0xff, 0xff), 0f, 0, 0,
							0.5f);
					if (this.me.difficulty < 1f) {
						text = "EASY";
					} else if (this.me.difficulty > 1f) {
						text = "HARD";
					} else {
						text = "NORMAL";
					}
					batch.drawString(this.font, text, 400f, 780f,
							Pool.getColor(0xff, 0xff, 0xff, 0xff), 0f, 0, 0,
							0.5f);
					num19 = 0.25f;
					x = 200;
					y = 790;
					tile = 0xdb;
					level = this.me.level;
					num6 = 0;

					while (num6 < this.level[level].stars) {
						if (this.me.score >= this.level[level].star_score[num6]) {
							alpha = 0xff;
							if (this.me.difficulty < 1f) {
								gray = Pool.getColor(0xc6, 0x9c, 0x6d, alpha);
							} else if (this.me.difficulty == 1f) {
								gray = Pool.getColor(0xff, 0xff, 0xff, alpha);
							} else if (this.me.difficulty > 1f) {
								gray = Pool.getColor(0xff, 0xff, 0, alpha);
							}
						} else {
							if (this.level[this.me.level].high_score >= this.level[level].star_score[num6]) {
								if (this.level[this.me.level].high_score_difficulty == 0) {
									gray = Pool
											.getColor(0xc6, 0x9c, 0x6d, 0xff);
								} else if (this.level[this.me.level].high_score_difficulty == 1) {
									gray = Pool
											.getColor(0xff, 0xff, 0xff, 0xff);
								} else if (this.level[this.me.level].high_score_difficulty == 2) {
									gray = Pool.getColor(0xff, 0xff, 0, 0xff);
								}

								batch.draw(
										this.texture[this.tile[tile].texture],
										x, y, this.tile[tile].x,
										this.tile[tile].y, this.tile[tile].w,
										this.tile[tile].h, gray, 0f,
										(this.tile[tile].w / 2),
										(this.tile[tile].h / 2),
										(num19 + 0.1f), SpriteEffects.None);
							}
							alpha = 0x23;
							gray = Pool.getColor(0, 0, 0, 0xff);
						}
						batch.draw(this.texture[this.tile[tile].texture], x, y,
								this.tile[tile].x, this.tile[tile].y,
								this.tile[tile].w, this.tile[tile].h, gray, 0f,
								(this.tile[tile].w / 2),
								(this.tile[tile].h / 2), num19,
								SpriteEffects.None);
						x += 20;
						num6++;
					}
					tile = 0x44;
					x = 0x4e;
					y = 0x2fe;
					num19 = 0.4f;
					for (num5 = 0; num5 < this.me.life_max; num5++) {
						if (this.me.life > num5) {
							alpha = 0xff;
						} else {
							alpha = 0x37;
						}
						batch.draw(this.texture[this.tile[tile].texture], x,
								(y - (num5 * 0x13)), this.tile[tile].x,
								this.tile[tile].y, this.tile[tile].w,
								this.tile[tile].h,
								Pool.getColor(alpha, alpha, alpha, alpha), 0f,
								(this.tile[tile].w / 2),
								(this.tile[tile].h / 2), num19,
								SpriteEffects.None);
					}
					alpha = 0xff;
					tile = 0x4d;
					batch.draw(this.texture[this.tile[tile].texture],
							(this.float_number[this.fn_streak].loc.x - 0x2a),
							(this.float_number[this.fn_streak].loc.y - 0x25),
							this.tile[tile].x, this.tile[tile].y,
							this.tile[tile].w, this.tile[tile].h,
							Pool.getColor(alpha, alpha, alpha, alpha), 0f, 0,
							0, 1f, SpriteEffects.None);
					batch.drawString(this.font, "Streak",
							(this.float_number[this.fn_streak].loc.x - 0x2a),
							(this.float_number[this.fn_streak].loc.y - 40),
							LColor.white, 0f, 0, 0, 0.5f);
					tile = 70;
					batch.draw(this.texture[this.tile[tile].texture],
							this.float_number[this.fn_streak].loc.x,
							this.float_number[this.fn_streak].loc.y,
							this.tile[tile].x, this.tile[tile].y,
							this.tile[tile].w, this.tile[tile].h,
							Pool.getColor(alpha, alpha, alpha, alpha), 0f,
							(this.tile[tile].w / 2), (this.tile[tile].h / 2),
							1f, SpriteEffects.None);
					tile = 0x4d;
					batch.draw(this.texture[this.tile[tile].texture],

					(this.float_number[this.fn_accuracy].loc.x - 0x2a),
							(this.float_number[this.fn_accuracy].loc.y - 0x25),
							this.tile[tile].x, this.tile[tile].y,
							this.tile[tile].w, this.tile[tile].h,
							Pool.getColor(alpha, alpha, alpha, alpha), 0f, 0f,
							0f, 1f, SpriteEffects.None);
					batch.drawString(this.font, "Accuracy %",
							(this.float_number[this.fn_accuracy].loc.x - 0x2a),
							(this.float_number[this.fn_accuracy].loc.y - 40),
							LColor.white, 0f, 0f, 0f, 0.5f);
					tile = 70;
					batch.draw(this.texture[this.tile[tile].texture],

					this.float_number[this.fn_accuracy].loc.x,
							this.float_number[this.fn_accuracy].loc.y,
							this.tile[tile].x, this.tile[tile].y,
							this.tile[tile].w, this.tile[tile].h,
							Pool.getColor(alpha, alpha, alpha, alpha), 0f,
							(this.tile[tile].w / 2), (this.tile[tile].h / 2),
							1f, SpriteEffects.None);
					tile = 0x4d;
					batch.draw(this.texture[this.tile[tile].texture],
							(this.float_number[this.fn_score].loc.x - 0x56),
							(this.float_number[this.fn_score].loc.y - 0x25),
							this.tile[tile].x, this.tile[tile].y,
							this.tile[tile].w, this.tile[tile].h,
							Pool.getColor(alpha, alpha, alpha, alpha), 0f, 0f,
							0f, 1f, SpriteEffects.None);
					if (this.level[this.me.level].high_score_reached == 1) {
						text = "New High!";
						batch.drawString(this.font, text,

						(this.float_number[this.fn_score].loc.x - 0x56),
								(this.float_number[this.fn_score].loc.y - 40),
								LColor.lime, 0f, 0, 0, 0.5f);
					} else {
						text = "High Score:  "
								+ (new Integer(
										this.level[this.me.level].high_score))
								+ ("N00");
						batch.drawString(this.font, text,

						(this.float_number[this.fn_score].loc.x - 0x56),
								(this.float_number[this.fn_score].loc.y - 40),
								LColor.white, 0f, 0, 0, 0.5f);
					}

					tile = 0x47;
					batch.draw(this.texture[this.tile[tile].texture],

					this.float_number[this.fn_score].loc.x,
							this.float_number[this.fn_score].loc.y,
							this.tile[tile].x, this.tile[tile].y,
							this.tile[tile].w, this.tile[tile].h,
							Pool.getColor(alpha, alpha, alpha, alpha), 0f,
							(this.tile[tile].w / 2), (this.tile[tile].h / 2),
							1f, SpriteEffects.None);
					tile = 0x45;
					x = 0x23;
					y = 0x2eb;

					batch.draw(this.texture[this.tile[tile].texture], x, y,
							this.tile[tile].x, this.tile[tile].y,
							this.tile[tile].w, this.tile[tile].h,
							Pool.getColor(alpha, alpha, alpha, alpha), 0f,
							(this.tile[tile].w / 2), (this.tile[tile].h / 2),
							1f, SpriteEffects.None);
					tile = this.ammo[this.me.ammo].tile;

					batch.draw(this.texture[this.tile[tile].texture], x, y,
							this.tile[tile].x, this.tile[tile].y,
							this.tile[tile].w, this.tile[tile].h,
							this.ammo[this.me.ammo].shot[0].obj.loc.color, 0f,
							(this.tile[tile].w / 2), (this.tile[tile].h / 2),
							1f, SpriteEffects.None);
					if (this.me.last_shot < this.ammo[this.me.ammo].reload) {
						x = 6;
						y = 0x2ad;
						num12 = 0x40;
						num13 = 10;
						num19 = (num12 - 4)
								* ((this.me.last_shot) / (this.ammo[this.me.ammo].reload));
						alpha = 0xaf;
						tile = 60;
						tile = 0x3d;
						alpha = 0xaf;

						batch.draw(this.texture[this.tile[tile].texture],
								x + 2, y + 2, (int) num19, num13 - 4,
								this.tile[tile].x, this.tile[tile].y,
								this.tile[tile].w, this.tile[tile].h, Pool
										.getColor(alpha, alpha - 0x4b, alpha,
												alpha));
					}
					for (num5 = 0; num5 < this.objects; num5++) {
						if ((this.obj[num5].active == 1)
								&& (this.obj[num5].loc.hold == 0)) {
							tile = this.obj[num5].tile;
							if ((this.tile[tile].frames > 0)
									&& (this.mode == 1)) {
								tile = this.tile[tile].frame[this.ani_frame
										% this.tile[tile].frames];
							}
							if (this.obj[num5].loc.show_rot == 1) {
								rot = this.obj[num5].loc.rot;
							} else {
								rot = 0f;
							}
							rot += this.obj[num5].loc.spin;
							alpha = this.obj[num5].loc.alpha;
							gray = Pool.getColor(this.obj[num5].loc.color.r,
									this.obj[num5].loc.color.g,
									this.obj[num5].loc.color.b,
									(float) (alpha / 255f));
							batch.draw(this.texture[this.tile[tile].texture],
									this.obj[num5].loc.x, this.obj[num5].loc.y,
									this.tile[tile].x, this.tile[tile].y,
									this.tile[tile].w, this.tile[tile].h, gray,
									rot, this.obj[num5].cx, this.obj[num5].cy,
									this.obj[num5].loc.scale,
									SpriteEffects.None);

						}
					}
					for (num5 = 0; num5 < this.shots; num5++) {
						if ((this.shot[num5].active == 1)
								&& (this.shot[num5].obj.loc.hold == 0)) {
							index = this.shot[num5].target_npc;
							if ((index >= 0) && (this.npc[index].active == 1)) {
								tile = 0x25;
								rot = (this.mode_frame % 360) * 2;
								alpha = 200;
								batch.draw(
										this.texture[this.tile[tile].texture],
										this.npc[index].obj.loc.x,
										this.npc[index].obj.loc.y,
										this.tile[tile].x, this.tile[tile].y,
										this.tile[tile].w, this.tile[tile].h,
										Pool.getColor(alpha, alpha, alpha,
												alpha), rot,
										(this.tile[tile].w / 2),
										(this.tile[tile].h / 2), 1f,
										SpriteEffects.None);

							}
							tile = this.shot[num5].obj.tile;
							if (this.tile[tile].frames > 0) {
								tile = this.tile[tile].frame[this.ani_frame
										% this.tile[tile].frames];
							}
							if (this.shot[num5].obj.loc.show_rot == 1) {
								rot = this.shot[num5].obj.loc.rot;
							} else {
								rot = 0f;
							}
							rot += this.shot[num5].obj.loc.spin;
							gray = Pool
									.getColor(
											this.shot[num5].obj.loc.color.r,
											this.shot[num5].obj.loc.color.g,
											this.shot[num5].obj.loc.color.b,
											(float) (this.shot[num5].obj.loc.alpha) / 255f);
							batch.draw(this.texture[this.tile[tile].texture],
									this.shot[num5].obj.loc.x,
									this.shot[num5].obj.loc.y,
									this.tile[tile].x, this.tile[tile].y,
									this.tile[tile].w, this.tile[tile].h, gray,
									rot, this.shot[num5].obj.cx,
									this.shot[num5].obj.cy,
									this.shot[num5].obj.loc.scale,
									SpriteEffects.None);

						}
					}

					if (this.hold > 0) {
						tile = 0x1c;
						if (this.tile[tile].frames > 0) {
							tile = this.tile[tile].frame[this.ani_frame
									% this.tile[tile].frames];
						}
						rot = (this.hold * 9) % 360;
						batch.draw(this.texture[this.tile[tile].texture],
								(getWidth() / 2), 179f, this.tile[tile].x,
								this.tile[tile].y, this.tile[tile].w,
								this.tile[tile].h, LColor.white, rot,
								(this.tile[tile].w / 2),
								(this.tile[tile].h / 2), 1f, SpriteEffects.None);
					}

				}

			}
		}
		batch.submit();
		for (num5 = 0; num5 < this.float_numbers; num5++) {
			if ((this.float_number[num5].active == 1)
					&& (this.float_number[num5].loc.hold == 0)) {
				if (this.float_number[num5].center == 0) {
					x = this.float_number[num5].loc.x;
					y = this.float_number[num5].loc.y;
				} else {
					x = this.float_number[num5].loc.x
							- ((int) (this.float_number[num5].sw / 2f));
					y = this.float_number[num5].loc.y
							- ((int) (this.float_number[num5].sh / 2f));
				}
				num6 = this.float_number[num5].count - 1;
				while (num6 >= 0) {
					tile = this.float_number[num5].num_tile[num6];
					gray = Pool.getColor(this.float_number[num5].loc.color.r,
							this.float_number[num5].loc.color.g,
							this.float_number[num5].loc.color.b,
							(float) (this.float_number[num5].loc.alpha / 255f));
					batch.draw(this.texture[this.tile[tile].texture], x, y,
							this.tile[tile].x, this.tile[tile].y,
							this.tile[tile].w, this.tile[tile].h, gray, 0f, 0f,
							0f, this.float_number[num5].loc.scale,
							SpriteEffects.None);
					x += (int) (this.tile[tile].w * this.float_number[num5].loc.scale);
					num6--;
				}
			}
		}

		if (this.mode == 4) {
			batch.draw(this.texture[3], 0, 0, getWidth(), getHeight(), 0, 0,
					getWidth(), getHeight(), Pool.getColor(0, 0, 0, 100), 0f,
					0f, 0f, SpriteEffects.None);
			x = getWidth() / 2;
			y = 0x5d;
			text = "PAUSED";
			vector = font.getOrigin(text);
			batch.drawString(this.font, text, ((x + 50) + 3), (y + 3),
					LColor.black, 0f, 0f, 0f, 1.3f);
			batch.drawString(this.font, text, (x + 50), y, LColor.white, 0f,
					0f, 0f, 1.3f);
		} else if (((this.mode == 1) && (this.hold > 0))
				&& (this.mode_frame < 120)) {
			tile = 0xdd;
			x = 240;
			y = 0x1df;
			batch.draw(this.texture[this.tile[tile].texture], x, y,
					this.tile[tile].x, this.tile[tile].y, this.tile[tile].w,
					this.tile[tile].h, gray, 0f, (this.tile[tile].w / 2),
					(this.tile[tile].h / 2), 1f, SpriteEffects.None);
			text = "Press the BACK button to Pause at any time..";
			vector = font.getOrigin(text);
			batch.drawString(this.font, text, (x + 1), (y + 1), LColor.black,
					0f, vector.x, vector.y, 0.65f);
			batch.drawString(this.font, text, x, y, LColor.white, 0f, vector.x,
					vector.y, 0.65f);
		}
		num5 = 0;
		while (num5 < this.buttons) {
			if ((this.button[num5].active == 1)
					&& (this.button[num5].loc.hold == 0)) {
				x = this.button[num5].loc.x;
				y = this.button[num5].loc.y;
				alpha = 0xff;
				num6 = 0;
				while (num6 < this.button[num5].tiles) {
					tile = this.button[num5].tile[num6];
					if (this.button[num5].clicked > 0) {
						if (this.tile[tile].clicked > 0) {
							tile = this.tile[tile].clicked;
						}
					} else if (this.tile[tile].frames > 0) {
						tile = this.tile[tile].frame[this.ani_frame
								% this.tile[tile].frames];
					}
					gray = Pool.getColor(this.button[num5].tile_color[num6].r
							- (0xff - this.button[num5].loc.alpha),
							this.button[num5].tile_color[num6].g
									- (0xff - this.button[num5].loc.alpha),
							this.button[num5].tile_color[num6].b
									- (0xff - this.button[num5].loc.alpha),
							this.button[num5].loc.alpha);
					batch.draw(this.texture[this.tile[tile].texture], x, y,
							this.tile[tile].x, this.tile[tile].y,
							this.tile[tile].w, this.tile[tile].h, gray, 0f, 0f,
							0f, this.button[num5].loc.scale, SpriteEffects.None);
					num6++;
				}
				if (this.button[num5].text_tile != null) {
					text = this.button[num5].text_tile;
					vector = new Vector2f(this.font.stringWidth(text)
							* (this.button[num5].loc.scale * 0.75f));
					gray = Pool.getColor(
							0xff - (0xff - this.button[num5].loc.alpha),
							0xff - (0xff - this.button[num5].loc.alpha),
							0xff - (0xff - this.button[num5].loc.alpha),
							this.button[num5].loc.alpha);
					batch.drawString(this.font, text,
							((x + this.button[num5].loc.sw) - vector.x)
									- (8f * this.button[num5].loc.scale),
							(y + this.button[num5].loc.sh) - vector.y, gray,
							0f, 0, 0, (this.button[num5].loc.scale * 0.75f));
				}
				if (this.button[num5].text_label != null) {
					text = this.button[num5].text_label;
					vector = new Vector2f(this.font.stringWidth(text)
							* (this.button[num5].loc.scale * 1f));
					gray = Pool.getColor(0, 0, 0, this.button[num5].loc.alpha);
					batch.drawString(
							this.font,
							text,
							((x + 2) + this.button[num5].loc.sw)
									+ (20f * this.button[num5].loc.scale),
							(y + 2)
									+ ((this.button[num5].loc.sh - vector.y) / 2f),
							gray, 0f, 0, 0, (this.button[num5].loc.scale * 1f));
					gray = Pool.getColor(
							0xff - (0xff - this.button[num5].loc.alpha),
							0xff - (0xff - this.button[num5].loc.alpha),
							0xff - (0xff - this.button[num5].loc.alpha),
							this.button[num5].loc.alpha);
					batch.drawString(this.font, text,
							(x + this.button[num5].loc.sw)
									+ (20f * this.button[num5].loc.scale),
							y + ((this.button[num5].loc.sh - vector.y) / 2f),
							gray, 0f, 0, 0, (this.button[num5].loc.scale * 1f));
				}
			}
			num5++;
		}

		if ((this.mode == 3) || (this.mode == 6)) {
			for (num5 = 0; num5 < this.buttons; num5++) {
				if ((this.button[num5].active == 1)
						&& (this.button[num5].type == 3)) {
					level = this.button[num5].val;
					tile = this.number_tile[level + 1];
					alpha = 0xff;
					x = (int) ((this.button[num5].loc.w - 0x23) * this.button[num5].loc.scale);
					y = (int) (34f * this.button[num5].loc.scale);
					num19 = 0.6f * this.button[num5].loc.scale;
					if (level != this.lv_marathon) {
						if (this.level[level].locked == 0) {
							batch.draw(this.texture[this.tile[tile].texture],
									this.button[num5].loc.fx + x,
									this.button[num5].loc.fy + y,
									this.tile[tile].x, this.tile[tile].y,
									this.tile[tile].w, this.tile[tile].h,
									LColor.lightGreen, 0f,
									(this.tile[tile].w / 2),
									(this.tile[tile].h / 2), num19,
									SpriteEffects.None);
						} else {
							batch.draw(this.texture[this.tile[tile].texture],
									this.button[num5].loc.fx + x,
									this.button[num5].loc.fy + y,
									this.tile[tile].x, this.tile[tile].y,
									this.tile[tile].w, this.tile[tile].h,
									LColor.white, 0f, (this.tile[tile].w / 2),
									(this.tile[tile].h / 2), num19,
									SpriteEffects.None);
						}
					}
					if ((this.level[level].locked == 0)
							|| (level == this.lv_marathon)) {
						alpha = 150;
						if (this.level[level].high_score > 0) {
							alpha = 0xff;
						}
						x = (int) (21f * this.button[num5].loc.scale);
						text = "High Score:";
						y = (int) ((this.button[num5].loc.h - 90) * this.button[num5].loc.scale);
						num19 = 0.5f * this.button[num5].loc.scale;
						batch.drawString(this.font, text,
								this.button[num5].loc.fx + x,
								this.button[num5].loc.fy + y,
								Pool.getColor(alpha, alpha, alpha, alpha), 0f,
								0f, 0f, num19);
						if (this.level[level].high_score > 0) {
							text = (new Integer(this.level[level].high_score))
									+ ("N00");
							num19 = 0.75f * this.button[num5].loc.scale;
						} else {
							if (level == this.lv_marathon) {
								text = "(not yet played)";
							} else {
								text = "(not complete)";
							}
							num19 = 0.6f * this.button[num5].loc.scale;
						}
						y = (int) ((this.button[num5].loc.h - 0x4b) * this.button[num5].loc.scale);
						batch.drawString(this.font, text,
								(this.button[num5].loc.fx + x) + 1f,
								(this.button[num5].loc.fy + y) + 1f,
								LColor.black, 0f, 0f, 0f, num19);
						batch.drawString(this.font, text,
								this.button[num5].loc.fx + x,
								this.button[num5].loc.fy + y, LColor.lime, 0f,
								0, 0, num19);
						tile = 0xdb;
						x = (int) (21f * this.button[num5].loc.scale);
						y = (int) ((this.button[num5].loc.h - 0x2b) * this.button[num5].loc.scale);
						num19 = 0.43f * this.button[num5].loc.scale;
						for (num6 = 0; num6 < this.level[level].stars; num6++) {
							if (this.level[level].high_score >= this.level[level].star_score[num6]) {
								alpha = 0xff;
								if (this.level[level].high_score_difficulty == 0) {
									gray = Pool.getColor(0xc6, 0x9c, 0x6d,
											alpha);
								} else if (this.level[level].high_score_difficulty == 1) {
									gray = Pool.getColor(0xff, 0xff, 0xff,
											alpha);
								} else if (this.level[level].high_score_difficulty == 2) {
									gray = Pool.getColor(0xff, 0xff, 0, alpha);
								}
							} else {
								alpha = 50;
								gray = Pool
										.getColor(alpha, alpha, alpha, alpha);
							}
							batch.draw(this.texture[this.tile[tile].texture],
									this.button[num5].loc.fx + x,
									this.button[num5].loc.fy + y,
									this.tile[tile].x, this.tile[tile].y,
									this.tile[tile].w, this.tile[tile].h, gray,
									0f, 0, 0, num19, SpriteEffects.None);
							x += (int) (33f * this.button[num5].loc.scale);
						}
					}
				}
				if (this.mode == 6) {
					break;
				}
			}
		}
		x = 150;
		y = 640;
		text = "hits:" + this.me.hits;
		if (this.mode_fade != this.FADE_NONE) {
			level = 20;
			alpha = 0;
			if (((this.mode_fade == this.FADE_IN) || (this.mode_fade == this.FADE_IN_OUT))
					&& (this.mode_frame < level)) {
				alpha = 0;
				hold = level - this.mode_frame;
				alpha = (int) (255f * (hold / (level)));
			}
			if (((this.mode_fade == this.FADE_OUT) || (this.mode_fade == this.FADE_IN_OUT))
					&& (this.hold < level)) {
				alpha = 0xff;
				hold = this.hold;
				alpha = 0xff - ((int) (255f * (hold / (level))));
			}
			x = y = 0;
			if (alpha > 0) {
				batch.draw(this.texture[3], x, y, getWidth(), getHeight(), x,
						y, getWidth(), getHeight(),
						Pool.getColor(0, 0, 0, alpha), 0f, 0f, 0f,
						SpriteEffects.None);
			}
		}

	}

	@Override
	public void loadContent() {
		if (!LSystem.base().isMobile()) {
			this.texture[0] = LTextures.loadTexture("assets/tiles.png");
		} else {
			// 原版纹理大小为960x1600,部分Android机不能识别大于1024x1024的纹理(目前该种手机数量大约1成左右,大于此的纹理都无法显示)，
			// 所以需先缩小图片到一定范围内再加载(代价是画面失真)……
			Image image = BaseIO.loadImage("assets/tiles.png");
			Image newImage = Image.getResize(image,576, 960);
			image.close();
			// 处理纹理数据
			LTexture texture = newImage.texture().scale(960, 1600);
			this.texture[0] = texture;
		}
		this.texture[1] = LTextures.loadTexture("assets/numbers.png");
		this.texture[3] = LTextures.loadTexture("assets/overlay.png");
		this.texture[2] = LTextures.loadTexture("assets/intro.png");
		this.texture[4] = LTextures.loadTexture("assets/bg_1.png");
		this.texture[5] = LTextures.loadTexture("assets/bg_3.png");
		this.texture[6] = LTextures.loadTexture("assets/bg_5.png");
		this.texture[7] = LTextures.loadTexture("assets/bg_6.png");
		this.texture[8] = LTextures.loadTexture("assets/bg_7.png");

	}

	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void unloadContent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pressed(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void released(GameTouch e) {

	}

	@Override
	public void move(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drag(GameTouch e) {
		// TODO Auto-generated method stub

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
		if (SysKey.isKeyPressed(SysKey.BACK)) {
			if (this.mode == 1) {
				this.change_mode(4);
			} else if (((((this.mode == 10) || (this.mode == 9)) || ((this.mode == 4) || (this.mode == 6))) || (this.mode == 7))
					|| (this.mode == 8)) {
				this.do_button(6, 0);
			} else {
				LSystem.exit();
			}
		}
		this.mode_frame++;
		if (this.mode_frame > 0x1869f) {
			this.mode_frame = 0x2710;
		}
		this.frame++;
		if (this.frame >= this.ani_dur) {
			this.ani_frame++;
			this.frame = 0;
			if (this.ani_frame >= 0x3e8) {
				this.ani_frame = 0;
			}
		}
		if (this.hold > 0) {
			this.hold--;
			if ((this.hold == 0) && (this.hold_mode != 0)) {
				this.change_mode(this.hold_mode);
			}
		}
		if (this.next_save > 0) {
			this.next_save--;
		}

		this.get_input();
		this.game_loop();

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
