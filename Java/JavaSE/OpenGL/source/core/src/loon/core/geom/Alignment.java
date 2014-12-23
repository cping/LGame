package loon.core.geom;

public enum Alignment {
	
	TOP_LEFT(0.0, 1.0) {
		public Vector2f align(Vector2f o, Dimension s) {
			return new Vector2f(o.getX(), o.getY() + s.getHeight() - 1);
		}

		public Vector2f alignBox(Vector2f o, Dimension s, Dimension b) {
			return new Vector2f(o.getX(), o.getY() + s.getHeight()
					- b.getHeight());
		}

	},

	TOP(0.5, 1.0) {
		public Vector2f align(Vector2f o, Dimension s) {
			return new Vector2f(o.getX() + (s.getWidth() - 1) / 2, o.getY()
					+ s.getHeight() - 1);
		}

		public Vector2f alignBox(Vector2f o, Dimension s, Dimension b) {
			return new Vector2f(o.getX() + (s.getWidth() - b.getWidth()) / 2,
					o.getY() + s.getHeight() - b.getHeight());
		}
	},

	TOP_RIGHT(1.0, 1.0) {
		public Vector2f align(Vector2f o, Dimension s) {
			return new Vector2f(o.getX() + s.getWidth() - 1, o.getY()
					+ s.getHeight() - 1);
		}

		public Vector2f alignBox(Vector2f o, Dimension s, Dimension b) {
			return new Vector2f(o.getX() + s.getWidth() - b.getWidth(),
					o.getY() + s.getHeight() - b.getHeight());
		}
	},

	LEFT(0.0, 0.5) {
		public Vector2f align(Vector2f o, Dimension s) {
			return new Vector2f(o.getX(), o.getY() + (s.getHeight() - 1) / 2);
		}

		public Vector2f alignBox(Vector2f o, Dimension s, Dimension b) {
			return new Vector2f(o.getX(), o.getY()
					+ (s.getHeight() - b.getHeight()) / 2);
		}
	},

	MIDDLE(0.5, 0.5) {
		public Vector2f align(Vector2f o, Dimension s) {
			return new Vector2f(o.getX() + (s.getWidth() - 1) / 2, o.getY()
					+ (s.getHeight() - 1) / 2);
		}

		public Vector2f alignBox(Vector2f o, Dimension s, Dimension b) {
			return new Vector2f(o.getX() + (s.getWidth() - b.getWidth()) / 2,
					o.getY() + (s.getHeight() - b.getHeight()) / 2);
		}
	},

	RIGHT(1.0, 0.5) {
		public Vector2f align(Vector2f o, Dimension s) {
			return new Vector2f(o.getX() + s.getWidth() - 1, o.getY()
					+ (s.getHeight() - 1) / 2);
		}

		public Vector2f alignBox(Vector2f o, Dimension s, Dimension b) {
			return new Vector2f(o.getX() + s.getWidth() - b.getWidth(),
					o.getY() + (s.getHeight() - b.getHeight()) / 2);
		}
	},

	BOTTOM_LEFT(0.0, 0.0) {
		public Vector2f align(Vector2f o, Dimension s) {
			return new Vector2f(o.getX(), o.getY());
		}

		public Vector2f alignBox(Vector2f o, Dimension s, Dimension b) {
			return new Vector2f(o.getX(), o.getY());
		}
	},

	BOTTOM(0.5, 0.0) {
		public Vector2f align(Vector2f o, Dimension s) {
			return new Vector2f(o.getX() + (s.getWidth() - 1) / 2, o.getY());
		}

		public Vector2f alignBox(Vector2f o, Dimension s, Dimension b) {
			return new Vector2f(o.getX() + (s.getWidth() - b.getWidth()) / 2,
					o.getY());
		}
	},

	BOTTOM_RIGHT(1.0, 0.0) {
		public Vector2f align(Vector2f o, Dimension s) {
			return new Vector2f(o.getX() + s.getWidth() - 1, o.getY());
		}

		public Vector2f alignBox(Vector2f o, Dimension s, Dimension b) {
			return new Vector2f(o.getX() + s.getWidth() - b.getWidth(),
					o.getY());
		}
	};

	private double along;

	private double up;

	Alignment(double along, double up) {
		this.along = along;
		this.up = up;
	}

	public double fromLeft() {
		return along;
	}

	public double fromBottom() {
		return up;
	}

	public abstract Vector2f align(Vector2f origin, Dimension size);

	public int alignX(int availableWidth, int width) {
		return (int) (fromLeft() * availableWidth - fromLeft() * width);
	}

	public int alignY(int availableHeight, int height) {
		return (int) (fromBottom() * availableHeight - fromBottom() * height);
	}

	public abstract Vector2f alignBox(Vector2f origin, Dimension size,
			Dimension box);

}
