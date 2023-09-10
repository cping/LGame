package loon.stg;

import loon.LSystem;
import loon.LTexture;
import loon.LTexture.Format;
import loon.LTextures;
import loon.Screen;
import loon.action.avg.drama.Command;
import loon.action.avg.drama.Conversion;
import loon.action.map.Config;
import loon.action.sprite.effect.ScrollEffect;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.events.GameTouch;
import loon.events.Updateable;
import loon.font.Font.Style;
import loon.font.LFont;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.opengl.LTexturePack;
import loon.opengl.TextureUtils;
import loon.stg.STGHero.HeroTouch;
import loon.stg.effect.Picture;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.ObjectMap.Values;
import loon.utils.TArray;
import loon.utils.ObjectMap.Keys;
import loon.utils.StringUtils;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

public abstract class STGScreen extends Screen {

	public static final int HERO = 0;

	public static final int HERO_SHOT = 1;

	public static final int ENEMY = 2;

	public static final int ENEMY_SHOT = 3;

	public static final int NO_HIT = 4;

	public static final int ITEM = 5;

	public static final int GET_ITEM = 6;

	public static final int SUICIDE = 7;

	public static final int ALL_HIT = 8;

	final static int NULL_MODE = 0;

	final static int GRP_MODE = 1;

	final static int STR_MODE = 2;

	final static int CENTER_STR_MODE = 3;

	final static int DRW_MODE = 4;

	final static int BACK_OTHER_MODE = 0;

	final static int BACK_STAR_MODE = 1;

	final static int BACK_SCROLL_MODE = 2;

	final static int BACK_EMPTY_MODE = 3;

	private int spriteLength;

	private int count;

	private int dot_size;

	private int[] dot;

	private boolean dirty;

	private LTimer scrollDelay;

	private ScrollEffect scroll;

	private LColor starColor;

	private int backgroundMode;

	private Command command;

	private String cmd_pack;

	private int cmd_enemy_no = 1000;

	private LTexturePack bitmapPack;

	private int sequenceCount = 0;

	private int planeFontSize = 20;

	protected ObjectMap<Integer, STGPlane> planes;

	protected STGObjects stgObjects;

	private int[] spriteList;

	private String commandName;

	private String packXmlName;

	private int threadSpeed = 30;

	private HeroTouch touch;

	/**
	 * 这是一个STG模块使用的访问限制器，用于制约用户仅可使用必要的图像功能。
	 * 
	 * PS:STG模块会将所有游戏图打包入LTexturePack当中，作为一张独立图片 使用(默认清空实际加载的图像)，所以需要特别的图像处理方式。
	 */
	public static class DrawableVisit {

		private STGScreen screen;

		private Format format = Format.DEFAULT;

		DrawableVisit(STGScreen screen) {
			this.screen = screen;
		}

		public Format getFormat() {
			return format;
		}

		public void setFormat(Format format) {
			this.format = format;
		}

		public int getWidth() {
			return screen.getWidth();
		}

		public int getHeight() {
			return screen.getHeight();
		}

		public int add(String res) {
			return screen.addBitmap(res);
		}

		public int[] add(String res, int width, int height) {
			LTexture[] textures = TextureUtils.getSplitTextures(res, width, height);
			return add(textures, width, height);
		}

		public int[] add(LTexture[] tex2d, int width, int height) {
			int size = tex2d.length;
			int[] result = new int[size];
			for (int i = 0; i < size; i++) {
				result[i] = add(tex2d[i]);
			}
			return result;
		}

		public int add(Image img) {
			return screen.addBitmap(img);
		}

		public int add(LTexture img) {
			return screen.addBitmap(img);
		}

		public boolean setPlaneScale(int index, float scale) {
			return screen.setPlaneScale(index, scale);
		}

		public void setPlaneSize(int index, int w, int h) {
			screen.setPlaneSize(index, w, h);
		}

		public void setPlaneGraphics(int index, int imgId, float x, float y) {
			setPlane(index, imgId, x, y, true);
		}

		public void setPlane(int index, int imgId, float x, float y, boolean v) {
			screen.setPlaneBitmap(index, 0, imgId);
			screen.setPlaneView(index, v);
			screen.setPlanePos(index, x, y);
		}

		public boolean setPlaneBitmap(int index, int animeNo, int imgId) {
			return screen.setPlaneBitmap(index, animeNo, imgId);
		}

		public float getPlanePosX(int index) {
			return screen.getPlanePosX(index);
		}

		public float getPlanePosY(int index) {
			return screen.getPlanePosY(index);
		}

		public boolean setPlaneAnimeDelay(int index, long delay) {
			return screen.setPlaneAnimeDelay(index, delay);
		}

		public boolean setPlaneAnime(int index, boolean anime) {
			return screen.setPlaneAnime(index, anime);
		}

		public boolean setPlaneString(int index, String mes) {
			return screen.setPlaneString(index, mes);
		}

		public boolean setPlaneCenterString(int index, String mes) {
			return screen.setPlaneCenterString(index, mes);
		}

		public boolean setPlaneFont(int index, String font, Style style, int size) {
			return screen.setPlaneFont(index, font, style, size);
		}

		public boolean setPlaneColor(int index, int r, int g, int b) {
			return screen.setPlaneColor(index, r, g, b);
		}

		public boolean setPlaneView(int index, boolean v) {
			return screen.setPlaneView(index, v);
		}

		public boolean setPlaneDraw(int index, Picture draw) {
			return screen.setPlaneDraw(index, draw);
		}

		public boolean setPlaneMov(int index, int x, int y) {
			return screen.setPlaneMov(index, x, y);
		}

		public boolean setLocation(int index, int x, int y) {
			return screen.setLocation(index, x, y);
		}

		public boolean setPlanePos(int index, int x, int y) {
			return screen.setPlanePos(index, x, y);
		}

		public boolean deletePlane(int index) {
			return screen.deletePlane(index);
		}

		public boolean deletePlaneAll() {
			return screen.deletePlaneAll();
		}

	}

	float getPlanePosX(int index) {
		STGPlane plane = planes.get(index);
		return plane == null ? 0 : plane.posX;
	}

	float getPlanePosY(int index) {
		STGPlane plane = planes.get(index);
		return plane == null ? 0 : plane.posY;
	}

	boolean setPlaneAnimeDelay(int index, long delay) {
		STGPlane plane = planes.get(index);
		if (plane == null) {
			return false;
		} else if (plane.planeMode != GRP_MODE) {
			return false;
		} else {
			if (plane.delay != null) {
				plane.delay.setDelay(delay);
			} else {
				plane.delay = new LTimer(delay);
			}
			return true;
		}
	}

	boolean setPlaneScale(int index, float scale) {
		STGPlane plane = planes.get(index);
		if (plane == null) {
			return false;
		} else {
			plane.scaleX = scale;
			plane.scaleY = scale;
			return true;
		}
	}

	boolean setPlaneAnime(int index, boolean anime) {
		STGPlane plane = planes.get(index);
		if (plane == null) {
			return false;
		} else if (plane.planeMode != GRP_MODE) {
			return false;
		} else {
			if (plane.animation = anime) {
				if (plane.animeList == null || (plane.animeList.length != plane.images.size)) {
					plane.animeList = new int[plane.images.size];
				}
				Keys<Integer> enumeration = plane.images.keys();
				for (int j = 0; enumeration.hasNext(); ++j) {
					plane.animeList[j] = enumeration.next();
				}
				for (int i = 0; i < plane.animeList.length - 1; ++i) {
					for (int j = i + 1; j < plane.animeList.length; ++j) {
						if (plane.animeList[i] > plane.animeList[j]) {
							int animeNo = plane.animeList[i];
							plane.animeList[i] = plane.animeList[j];
							plane.animeList[j] = animeNo;
						}
					}
				}
			} else {
				plane.animeList = null;
			}
			return true;
		}
	}

	boolean setPlaneString(int index, String mes) {
		STGPlane plane = planes.get(index);
		if (plane == null) {
			plane = new STGPlane();
			this.planes.put(index, plane);
		}
		plane.font = LFont.getFont(LSystem.getSystemGameFontName(), planeFontSize);
		plane.color = new LColor(0, 0, 0);
		plane.str = mes;
		plane.planeMode = STR_MODE;
		plane.view = true;
		plane.images.clear();
		plane.animation = false;
		plane.animeNo = 0;
		plane.draw = null;
		return true;
	}

	boolean setPlaneCenterString(int index, String mes) {
		STGPlane plane = planes.get(index);
		if (plane == null) {
			plane = new STGPlane();
			this.planes.put(index, plane);
		}
		plane.font = LFont.getFont(LSystem.getSystemGameFontName(), planeFontSize);
		plane.color = new LColor(0, 0, 0);
		plane.str = mes;
		plane.planeMode = CENTER_STR_MODE;
		plane.view = true;
		plane.images.clear();
		plane.animation = false;
		plane.animeNo = 0;
		plane.draw = null;
		return true;
	}

	boolean setPlaneFont(int index, String font, Style style, int size) {
		STGPlane plane = planes.get(index);
		if (plane == null) {
			return false;
		} else if ((plane.planeMode & STR_MODE) == 0) {
			return false;
		} else {
			if (font == null) {
				font = LSystem.getSystemGameFontName();
			}

			if (size < 0) {
				size = planeFontSize;
			}
			plane.font = LFont.getFont(font, style, size);
			return true;
		}
	}

	boolean setPlaneColor(int index, int r, int g, int b) {
		STGPlane plane = planes.get(index);
		if (plane == null) {
			return false;
		} else if ((plane.planeMode & STR_MODE) == 0) {
			return false;
		} else {
			plane.color = new LColor(r, g, b);
			return true;
		}
	}

	boolean setPlaneView(int index, boolean v) {
		STGPlane plane = planes.get(index);
		if (plane == null) {
			return false;
		} else if ((plane.planeMode & STR_MODE) == 0) {
			return false;
		} else {
			plane.view = v;
			return true;
		}
	}

	boolean setPlaneDraw(int index, Picture draw) {
		STGPlane plane = planes.get(index);
		if (plane == null) {
			plane = new STGPlane();
			this.planes.put(index, plane);
		}
		plane.font = null;
		plane.color = null;
		plane.str = null;
		plane.planeMode = DRW_MODE;
		plane.view = true;
		plane.images.clear();
		plane.animation = false;
		plane.animeNo = 0;
		plane.draw = draw;
		return true;
	}

	boolean setPlaneMov(int index, float x, float y) {
		STGPlane plane = planes.get(index);
		if (plane == null) {
			return false;
		} else {
			plane.posX += x;
			plane.posY += y;
			return true;
		}
	}

	boolean setPlaneScale(int index, float sx, float sy) {
		STGPlane plane = planes.get(index);
		if (plane == null) {
			return false;
		} else {
			plane.scaleX = sx;
			plane.scaleY = sy;
			return true;
		}
	}

	boolean setPlaneAngle(int index, float rotation) {
		STGPlane plane = planes.get(index);
		if (plane == null) {
			return false;
		} else {
			plane.rotation = rotation;
			return true;
		}
	}

	boolean setPlaneBitmapColor(int index, LColor c) {
		STGPlane plane = planes.get(index);
		if (plane == null) {
			return false;
		} else {
			plane.drawColor = c;
			return true;
		}
	}

	boolean setLocation(int index, float x, float y) {
		return setPlanePos(index, x, y);
	}

	boolean setPlanePos(int index, float x, float y) {
		STGPlane plane = planes.get(index);
		if (plane == null) {
			return false;
		} else {
			plane.posX = x;
			plane.posY = y;
			return true;
		}
	}

	boolean setPosX(int index, float x) {
		STGPlane plane = planes.get(index);
		if (plane == null) {
			return false;
		} else {
			plane.posX = x;
			return true;
		}
	}

	boolean setPosY(int index, float y) {
		STGPlane plane = planes.get(index);
		if (plane == null) {
			return false;
		} else {
			plane.posY = y;
			return true;
		}
	}

	void setPlaneSize(int index, int w, int h) {
		STGPlane plane = planes.get(index);
		if (plane == null) {
			plane = new STGPlane();
			this.planes.put(index, plane);
		}
		if (plane.rect == null) {
			plane.rect = new RectBox(0, 0, w, h);
		} else {
			plane.rect.setBounds(0, 0, w, h);
		}
	}

	boolean setPlaneBitmap(int index, int animeNo, int imgId) {
		if (bitmapPack == null) {
			return false;
		}
		try {
			STGPlane plane = planes.get(index);
			if (plane == null) {
				plane = new STGPlane();
				this.planes.put(index, plane);
			}
			plane.animeNo = animeNo;
			plane.rect = bitmapPack.getImageRect(imgId);
			plane.images.put(plane.animeNo, imgId);
			plane.planeMode = GRP_MODE;
			plane.view = true;
			plane.str = null;
			plane.font = null;
			plane.color = null;
			plane.draw = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	int addBitmap(String res) {
		try {
			return bitmapPack.putImage(res);
		} catch (Exception e) {
		}
		return -1;
	}

	int addBitmap(Image img) {
		try {
			return bitmapPack.putImage(img);
		} catch (Exception e) {
		}
		return -1;
	}

	int addBitmap(LTexture img) {
		try {
			return bitmapPack.putImage(img);
		} catch (Exception e) {
		}
		return -1;
	}

	public void deleteIndex(int id) {
		stgObjects.delObj(id);
		dirty = true;
	}

	public boolean deletePlane(int index) {
		STGPlane plane = planes.remove(index);
		dirty = true;
		return plane != null;
	}

	public boolean deletePlaneAll() {
		planes.clear();
		dirty = true;
		return planes.size == 0;
	}

	public boolean clearScore() {
		return stgObjects.clearObjects();
	}

	public int getScore() {
		return stgObjects.score;
	}

	public int getHeroHP() {
		return getHero().getHP();
	}

	public int getHeroMP() {
		return getHero().getMP();
	}

	public void addBombHero(String packageName) {
		stgObjects.addBombHero(packageName);
		dirty = true;
	}

	public void addBombHero(String packageName, float x, float y) {
		stgObjects.addBombHero(packageName, x, y);
		dirty = true;
	}

	public void addClass(Class<?> clazz, float x, float y, int tpno) {
		addClass(clazz.getName(), x, y, tpno);
	}

	public void addScreenPackageClass(String className, float x, float y, int tpno) {
		addClass(getScreenPackName() + className, x, y, tpno);
	}

	public void addClass(String className, float x, float y, int tpno) {
		stgObjects.addClass(className, x, y, tpno);
		dirty = true;
	}

	public STGObject newPlane(String className, float x, float y, int tpno) {
		STGObject o = stgObjects.newPlane(className, x, y, tpno);
		dirty = true;
		return o;
	}

	public void addPlane(STGObject o) {
		stgObjects.addPlane(o);
		dirty = true;
	}

	public void addHeroClass(Class<?> clazz, float x, float y) {
		addHeroClass(clazz.getName(), x, y);
	}

	public void addHeroScreenPackageClass(String className, float x, float y) {
		addHeroClass(getScreenPackName() + className, x, y);
	}

	public void addHeroClass(String className, float x, float y) {
		stgObjects.addHero(className, x, y, stgObjects.heroPlnNo);
		STGHero hero = stgObjects.getHero();
		if (hero != null) {
			touch = new HeroTouch(hero, getWidth(), getHeight(), true);
		}
		dirty = true;
	}

	public int getHeroNo() {
		return stgObjects.heroPlnNo;
	}

	public STGScreen(String path, String pack) {
		this.commandName = path;
		this.packXmlName = pack;
	}

	public STGScreen(String path) {
		this(path, null);
	}

	@Override
	public void onCreate(int width, int height) {
		super.onCreate(width, height);
		if (planes == null) {
			planes = new ObjectMap<Integer, STGPlane>(100);
		} else {
			planes.clear();
		}
		if (stgObjects != null) {
			stgObjects.close();
			stgObjects = null;
		}
		this.stgObjects = new STGObjects(this, 0);
		if (width > height) {
			this.scrollDelay = new LTimer(50);
		} else {
			this.scrollDelay = new LTimer(40);
		}
		this.dot_size = height / 10;
		this.count = 0;
		if (dot != null) {
			dot = null;
		}
		this.dot = new int[dot_size];
		for (int i = 0; i < dot_size; ++i) {
			this.dot[i] = (int) (MathUtils.random() * width);
		}
	}

	@Override
	public final void onLoad() {
		if (bitmapPack != null) {
			bitmapPack.close();
			bitmapPack = null;
		}
		if (packXmlName == null) {
			bitmapPack = new LTexturePack();
		} else {
			bitmapPack = new LTexturePack(packXmlName);
		}
		if (commandName != null) {
			this.openCommand(commandName, true);
		}
		final DrawableVisit visit = new DrawableVisit(this);
		this.loadDrawable(visit);
		this.bitmapPack.packed(visit.format);
		this.onLoading();
	}

	public abstract void onLoading();

	/**
	 * 返回当前游戏主角
	 * 
	 * @return
	 */
	public STGHero getHero() {
		return stgObjects.getHero();
	}

	/**
	 * 返回当前Screen所在包
	 * 
	 * @return
	 */
	public String getScreenPackName() {
		if (cmd_pack == null) {
			Class<?> clazz = this.getClass();
			cmd_pack = clazz.getPackage().getName();
		}
		return cmd_pack;
	}

	/**
	 * STG游戏主循环
	 * 
	 */
	public void mainLoop() {
		if (!nextCommand()) {
			onCommandAchieve();
			if (stgObjects.isAliveHero() && stgObjects.size == 1) {
				onEnemyClear();
			}
		} else if (!stgObjects.isAliveHero()) {
			onHeroDeath();
		}
		this.stgObjects.running();
		this.stgObjects.hitCheckHero();
		this.stgObjects.hitCheckHeroShot();
		this.onGameLoop();
		if (dirty) {
			updateSprite();
		}
	}

	/**
	 * 加载游戏脚本
	 * 
	 * @param resName
	 * @return
	 */
	public boolean openCommand(String resName) {
		return openCommand(resName, false);
	}

	/**
	 * 加载游戏脚本
	 * 
	 * @param resName
	 * @param thread
	 * @return
	 */
	public boolean openCommand(String resName, boolean thread) {
		this.sequenceCount = 0;
		this.cmd_pack = getScreenPackName();
		Command.resetCache();
		if (command == null) {
			command = new Command(resName);
		} else {
			command.formatCommand(resName);
		}

		return command != null;
	}

	/**
	 * 读取游戏脚本
	 * 
	 * @return
	 */
	boolean nextCommand() {
		if (this.sequenceCount < 0) {
			Values<STGObject> e = this.stgObjects.values();
			while (e.hasNext()) {
				STGObject shot = e.next();
				if (shot.attribute == ENEMY) {
					return true;
				}
			}
			this.sequenceCount = 0;
		}
		if (this.sequenceCount > 0) {
			--this.sequenceCount;
			return true;
		} else {
			try {
				for (; command.next();) {
					String cmd = command.doExecute();
					if (cmd == null) {
						continue;
					} else {
						if (onCommandAction(cmd)) {
							return true;
						}
						TArray<String> commands = Conversion.splitToList(cmd, ' ');
						if (commands.size > 0) {
							String cmdName = (String) commands.get(0);
							if (cmdName.equalsIgnoreCase("sleep")) {
								this.sequenceCount = Integer.parseInt((String) commands.get(1));
							} else if (cmdName.equalsIgnoreCase("wait")) {
								this.sequenceCount = -1;
							} else if (cmdName.equalsIgnoreCase("enemy")) {
								String enemy = (String) commands.get(1);
								int x = Integer.parseInt((String) commands.get(2));
								int y = Integer.parseInt((String) commands.get(3));
								if (StringUtils.charCount(enemy, '.') > 0) {
									addClass(enemy, x, y, cmd_enemy_no);
								} else {
									addClass(cmd_pack + "." + enemy, x, y, cmd_enemy_no);
								}
							} else if (cmdName.equalsIgnoreCase("package")) {
								this.cmd_pack = (String) commands.get(1);
							} else if (cmdName.equalsIgnoreCase("leader")) {
								String hero = (String) commands.get(1);
								int x = Integer.parseInt((String) commands.get(2));
								int y = Integer.parseInt((String) commands.get(3));
								if (StringUtils.charCount(hero, '.') > 0) {
									addHeroClass(hero, x, y);
								} else {
									addHeroClass(cmd_pack + "." + hero, x, y);
								}
							}
							return true;
						}
					}
				}
				return false;
			} catch (Exception ex) {
				return false;
			}
		}
	}

	/**
	 * 默认的Screen主线程中（非ShotLoop线程）事件
	 */
	@Override
	public final void alter(LTimerContext context) {
		long elapsedTime = context.timeSinceLastUpdate;
		if (touch != null) {
			touch.update(elapsedTime);
		}
		if (scrollDelay.action(elapsedTime)) {
			switch (backgroundMode) {
			case BACK_STAR_MODE:
				this.dot[this.count] = (int) (MathUtils.random() * this.getWidth());
				++this.count;
				this.count %= dot_size;
				break;
			case BACK_SCROLL_MODE:
				if (scroll != null) {
					scroll.update(context.timeSinceLastUpdate);
				}
				break;
			case BACK_EMPTY_MODE:
				emptyBackground();
				break;
			}
		}
		update(elapsedTime);
	}

	public void emptyBackground() {

	}

	public abstract void update(long elapsedTime);

	/**
	 * 非使用默认的游戏背景效果时，将调用此函数
	 * 
	 * @param g
	 */
	protected void drawOtherBackground(GLEx g) {

	}

	/**
	 * 游戏背景绘制
	 * 
	 * @param g
	 */
	protected void background(GLEx g) {
		switch (backgroundMode) {
		case BACK_STAR_MODE:
			if (starColor != null) {
				g.setColor(starColor);
			}
			for (int j = 0; j < dot_size; this.count = (this.count + 1) % dot_size) {
				int index = this.dot[this.count] % 3;
				g.drawPoint(dot[count] - index, getHeight() - j * 10);
				g.drawPoint(dot[count] + index, getHeight() - j * 10);
				g.drawPoint(dot[count], getHeight() - j * 10 - index);
				g.drawPoint(dot[count], getHeight() - j * 10 + index);
				++j;
			}
			if (starColor != null) {
				g.resetColor();
			}
			break;
		case BACK_SCROLL_MODE:
			if (scroll != null) {
				scroll.createUI(g);
			}
			break;
		case BACK_OTHER_MODE:
			drawOtherBackground(g);
			break;
		case BACK_EMPTY_MODE:
		default:
			break;
		}
	}

	/**
	 * 游戏前景绘制
	 * 
	 * @param g
	 */
	protected void foreground(GLEx g) {

	}

	private void updateSprite() {
		synchronized (planes) {
			final int size = planes.size;
			if (spriteList == null) {
				this.spriteList = new int[size];
			} else if (spriteLength != size) {
				this.spriteList = null;
				this.spriteList = new int[size];
			}
			spriteLength = size;
			Keys<Integer> keys = this.planes.keys();
			for (int i = 0; keys.hasNext(); ++i) {
				this.spriteList[i] = keys.next();
			}
			for (int i = 0; i < spriteLength - 1; ++i) {
				for (int j = i + 1; j < spriteLength; ++j) {
					if (this.spriteList[i] > this.spriteList[j]) {
						int index = this.spriteList[i];
						this.spriteList[i] = this.spriteList[j];
						this.spriteList[j] = index;
					}
				}
			}
			dirty = false;
		}
	}

	public void reset() {
		stgObjects.reset();
	}

	@Override
	public final synchronized void draw(GLEx g) {
		background(g);
		if (isOnLoadComplete()) {
			if (spriteLength == 0) {
				return;
			}
			bitmapPack.glBegin();
			for (int j = 0; j < spriteLength; ++j) {
				if (spriteList == null) {
					continue;
				}
				final int id = spriteList[j];
				STGPlane plane = planes.get(id);
				if (plane == null) {
					continue;
				}
				if (onDrawPlane(g, id)) {
					continue;
				}
				if (plane.view) {
					if (plane.planeMode == GRP_MODE) {
						if (plane.animation) {
							if (plane.delay.action(elapsedTime)) {
								int index;
								for (index = 0; plane.animeList[index] != plane.animeNo; ++index) {
									;
								}
								index = (index + 1) % plane.animeList.length;
								plane.animeNo = plane.animeList[index];
							}
						}
						if (plane.scaleX == 1 && plane.scaleY == 1) {
							bitmapPack.draw(plane.images.get(plane.animeNo), plane.posX, plane.posY, plane.rotation,
									plane.drawColor);
						} else {
							bitmapPack.draw(plane.images.get(plane.animeNo), plane.posX, plane.posY,
									plane.rect.width * plane.scaleX, plane.rect.height * plane.scaleY, plane.rotation,
									plane.drawColor);
						}
					} else if (plane.planeMode == STR_MODE) {
						g.setFont(plane.font);
						g.setColor(plane.color);
						g.drawString(plane.str, plane.posX, plane.posY + plane.font.getSize());
					} else if (plane.planeMode == CENTER_STR_MODE) {
						g.setFont(plane.font);
						g.setColor(plane.color);
						g.drawString(plane.str, plane.posX - plane.font.stringWidth(plane.str) / 2,
								plane.posY + plane.font.getSize());
					} else if (plane.planeMode == DRW_MODE) {
						plane.draw.paint(g, plane);
					}
				}
			}
			bitmapPack.glEnd();
		}
		foreground(g);
	}

	/**
	 * 单独绘制某一指定图像画面(当返回True时，不会自动绘制该精灵)
	 * 
	 * @param g
	 * @param id
	 * @return
	 */
	public abstract boolean onDrawPlane(GLEx g, int id);

	/**
	 * 统一的游戏图片资源管理接口
	 * 
	 * @param drawable
	 */
	public abstract void loadDrawable(final DrawableVisit drawable);

	/**
	 * 当游戏线程每次循环时都将触发此函数
	 * 
	 */
	public abstract void onGameLoop();

	/**
	 * 当游戏主角死亡时将触发此函数
	 * 
	 */
	public abstract void onHeroDeath();

	/**
	 * 当敌人被全部清空时，将触发此函数
	 * 
	 */
	public abstract void onEnemyClear();

	/**
	 * 当所有脚本读取完毕时，将触发此函数
	 * 
	 */
	public abstract void onCommandAchieve();

	/**
	 * 当每次读取脚本时，将触发此函数(如果返回True，则中断默认脚本规则)
	 * 
	 * @param cmd
	 * @return
	 */
	public abstract boolean onCommandAction(String cmd);

	@Override
	public final void touchDown(final GameTouch e) {
		Updateable runnable = new Updateable() {
			@Override
			public void action(Object a) {
				if (touch != null) {
					touch.onTouch(e);
				}
			}
		};
		LSystem.load(runnable);
		onDown(e);
	}

	public abstract void onDown(final GameTouch e);

	@Override
	public final void touchMove(GameTouch e) {
		onMove(e);
	}

	public abstract void onMove(final GameTouch e);

	@Override
	public final void touchUp(GameTouch e) {
		onUp(e);
	}

	public abstract void onUp(final GameTouch e);

	public void setScrollModeBackground(String resName) {
		setScrollModeBackground(Config.UP, resName);
	}

	public void setScrollModeBackground(int c, String resName) {
		setScrollModeBackground(c, LTextures.loadTexture(resName));
	}

	public void setScrollModeBackground(int c, LTexture t) {
		if (scroll != null) {
			scroll.close();
			scroll = null;
		}
		scroll = new ScrollEffect(c, t);
		scroll.setDelay(0);
		this.backgroundMode = BACK_SCROLL_MODE;
	}

	public void setStarModeBackground(LColor c) {
		if (c != null && LColor.white.equals(c)) {
			starColor = null;
		} else {
			starColor = c;
		}
		this.backgroundMode = BACK_STAR_MODE;
	}

	public void setNotBackground() {
		this.backgroundMode = BACK_EMPTY_MODE;
	}

	public LTexturePack getBitmapPack() {
		return bitmapPack;
	}

	public int getCmdEnemyNo() {
		return cmd_enemy_no;
	}

	public void setCmdEnemyNo(int no) {
		this.cmd_enemy_no = no;
	}

	public String getCmdPack() {
		return cmd_pack;
	}

	public void setCmdPack(String pack) {
		this.cmd_pack = pack;
	}

	public Command getCommand() {
		return command;
	}

	public String getCommandName() {
		return commandName;
	}

	public LTimer getScrollDelay() {
		return scrollDelay;
	}

	public String getPackXmlName() {
		return packXmlName;
	}

	public int getPlaneFontSize() {
		return planeFontSize;
	}

	public void setPlaneFontSize(int planeFontSize) {
		this.planeFontSize = planeFontSize;
	}

	public int getSequenceCount() {
		return sequenceCount;
	}

	public void setSequenceCount(int sequenceCount) {
		this.sequenceCount = sequenceCount;
	}

	public int[] getSpriteList() {
		return spriteList;
	}

	public int getThreadSpeed() {
		return threadSpeed;
	}

	public void setThreadSpeed(int threadSpeed) {
		this.threadSpeed = threadSpeed;
	}

	public HeroTouch getHeroTouch() {
		return touch;
	}

	@Override
	public void close() {

		if (stgObjects != null) {
			stgObjects.close();
			stgObjects = null;
		}
		if (bitmapPack != null) {
			bitmapPack.close();
			bitmapPack = null;
		}
	}

}
