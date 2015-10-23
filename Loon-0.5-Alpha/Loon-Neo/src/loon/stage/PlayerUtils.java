package loon.stage;

import loon.Director;
import loon.LSystem;
import loon.canvas.Canvas;
import loon.canvas.LColor;
import loon.font.LFont;
import loon.font.TextLayout;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.reply.Act;
import loon.utils.reply.Closeable;
import loon.utils.reply.Port;
import loon.utils.timer.GameClock;

public class PlayerUtils extends Director {

	public static Player createTextPlayer(LFont font, String text) {
		TextLayout layout = font.getLayoutText(text);
		Canvas canvas = LSystem
				.base()
				.graphics()
				.createCanvas(MathUtils.ceil(layout.stringWidth(text)),
						MathUtils.ceil(layout.getHeight()));
		canvas.setColor(LColor.white);
		canvas.setFont(font);
		canvas.drawText(text, 0f, 0f);
		CanvasPlayer player = new CanvasPlayer(LSystem.base().graphics(), canvas);
		return player;
	}

	public static Vector2f transform(Vector2f p, Player from, Player to,
			Vector2f result) {
		PlayerUtils.layerToScreen(from, p, result);
		PlayerUtils.screenToPlayer(to, result, result);
		return result;
	}

	public static Vector2f transform(Vector2f p, Player from, Player to) {
		return transform(p, from, to, new Vector2f());
	}

	public static void reparent(Player player, GroupPlayer target) {
		Vector2f pos = new Vector2f(player.tx(), player.ty());
		PlayerUtils.layerToScreen(player.parent(), pos, pos);
		target.add(player);
		PlayerUtils.screenToPlayer(player.parent(), pos, pos);
		player.setTranslation(pos.x, pos.y);
	}

	public static boolean contains(GroupPlayer group, Player player) {
		while (player != null) {
			player = player.parent();
			if (player == group) {
				return true;
			}
		}
		return false;
	}

	public static GroupPlayer group(Player... children) {
		GroupPlayer gl = new GroupPlayer();
		for (Player l : children)
			gl.add(l);
		return gl;
	}

	public static <T extends Player> T addChild(GroupPlayer parent, T child) {
		parent.add(child);
		return child;
	}

	public static GroupPlayer addNewGroup(GroupPlayer parent) {
		return addChild(parent, new GroupPlayer());
	}

	public static Player solid(final int color, final float width,
			final float height) {
		return new Player() {
			@Override
			public float width() {
				return width;
			}

			@Override
			public float height() {
				return height;
			}

			@Override
			protected void paintImpl(GLEx gl) {
				gl.setFillColor(color).fillRect(0, 0, width, height);
			}

			@Override
			public void update(long elapsedTime) {

			}
		};
	}

	public static RectBox totalBounds(Player root) {
		RectBox r = new RectBox(root.originX(), root.originY(), 0, 0);
		addBounds(root, root, r, new Vector2f());
		return r;
	}

	protected static void addBounds(Player root, Player l, RectBox bounds,
			Vector2f scratch) {
		float w = l.width(), h = l.height();
		if (w != 0 || h != 0) {
			bounds.add(PlayerUtils.layerToParent(l, root, scratch.set(0, 0),
					scratch));
			bounds.add(PlayerUtils.layerToParent(l, root, scratch.set(w, h),
					scratch));
		}

		if (l instanceof GroupPlayer) {
			GroupPlayer group = (GroupPlayer) l;
			for (int ii = 0, ll = group.children(); ii < ll; ++ii) {
				addBounds(root, group.childAt(ii), bounds, scratch);
			}
		}
	}

	public static Vector2f layerToScreen(Player player, XY point, Vector2f into) {
		return layerToParent(player, null, point, into);
	}

	public static Vector2f layerToScreen(Player player, float x, float y) {
		Vector2f into = new Vector2f(x, y);
		return layerToScreen(player, into, into);
	}

	public static Vector2f layerToParent(Player player, Player parent, XY point,
			Vector2f into) {
		into.set(point);
		while (player != parent) {
			if (player == null) {
				throw new IllegalArgumentException("the player is null");
			}
			into.x -= player.originX();
			into.y -= player.originY();
			player.affine().transform(into, into);
			player = player.parent();
		}
		return into;
	}

	public static Vector2f layerToParent(Player player, Player parent, float x,
			float y) {
		Vector2f into = new Vector2f(x, y);
		return layerToParent(player, parent, into, into);
	}

	public static Vector2f screenToPlayer(Player player, XY point, Vector2f into) {
		Player parent = player.parent();
		XY cur = (parent == null) ? point : screenToPlayer(parent, point, into);
		return parentToPlayer(player, cur, into);
	}

	public static Vector2f screenToPlayer(Player player, float x, float y) {
		Vector2f into = new Vector2f(x, y);
		return screenToPlayer(player, into, into);
	}

	public static Vector2f parentToPlayer(Player player, XY point, Vector2f into) {
		player.affine().inverseTransform(into.set(point), into);
		into.x += player.originX();
		into.y += player.originY();
		return into;
	}

	public static Vector2f parentToPlayer(Player parent, Player player, XY point,
			Vector2f into) {
		into.set(point);
		Player immediateParent = player.parent();
		if (immediateParent != parent) {
			parentToPlayer(parent, immediateParent, into, into);
		}
		parentToPlayer(player, into, into);
		return into;
	}

	public static Player getHitPlayer(Player root, Vector2f p) {
		root.affine().inverseTransform(p, p);
		p.x += root.originX();
		p.y += root.originY();
		return root.hitTest(p);
	}

	public static boolean hitTest(Player player, XY pos) {
		return hitTest(player, pos.getX(), pos.getY());
	}

	public static boolean hitTest(Player player, float x, float y) {
		Vector2f point = screenToPlayer(player, x, y);
		return (point.x() >= 0 && point.y() >= 0 && point.x() <= player.width() && point
				.y() <= player.height());
	}

	public static Player playerUnderPoint(Player root, float x, float y) {
		Vector2f p = new Vector2f(x, y);
		root.affine().inverseTransform(p, p);
		p.x += root.originX();
		p.y += root.originY();
		return layerUnderPoint(root, p);
	}

	public static int indexInParent(Player player) {
		GroupPlayer parent = player.parent();
		if (parent == null) {
			return -1;
		}
		for (int ii = parent.children() - 1; ii >= 0; ii--) {
			if (parent.childAt(ii) == player) {
				return ii;
			}
		}
		throw new AssertionError();
	}

	public static void bind(Player player, final Act<GameClock> paint,
			final Port<GameClock> onPaint) {

		player.state.connectNotify(new Port<Player.State>() {
			public void onEmit(Player.State state) {
				_pcon = Closeable.Shutdown.close(_pcon);
				if (state == Player.State.ADDED) {
					_pcon = paint.connect(onPaint);
				}
			}

			private Closeable _pcon = Closeable.Shutdown.DEF;
		});
	}

	public static void bind(Player player, final Act<GameClock> update,
			final Port<GameClock> onUpdate, final Act<GameClock> paint,
			final Port<GameClock> onPaint) {
		player.state.connectNotify(new Port<Player.State>() {
			public void onEmit(Player.State state) {
				_pcon = Closeable.Shutdown.close(_pcon);
				_ucon = Closeable.Shutdown.close(_ucon);
				if (state == Player.State.ADDED) {
					_ucon = update.connect(onUpdate);
					_pcon = paint.connect(onPaint);
				}
			}

			private Closeable _ucon = Closeable.Shutdown.DEF,
					_pcon = Closeable.Shutdown.DEF;
		});
	}

	public static int graphDepth(Player player) {
		int depth = -1;
		while (player != null) {
			player = player.parent();
			depth++;
		}
		return depth;
	}

	protected static Player layerUnderPoint(Player player, Vector2f pt) {
		float x = pt.x, y = pt.y;
		if (player instanceof GroupPlayer) {
			GroupPlayer gl = (GroupPlayer) player;
			for (int ii = gl.children() - 1; ii >= 0; ii--) {
				Player child = gl.childAt(ii);
				if (!child.visible()) {
					continue;
				}
				try {
					child.affine().inverseTransform(pt.set(x, y), pt);
					pt.x += child.originX();
					pt.y += child.originY();
					Player l = layerUnderPoint(child, pt);
					if (l != null) {
						return l;
					}
				} catch (Exception ex) {
					continue;
				}
			}
		}
		if (x >= 0 && x < player.width() && y >= 0 && y < player.height()) {
			return player;
		}
		return null;
	}
	
	
}
