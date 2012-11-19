using System.Collections.Generic;
using Loon.Core.Geom;
using System;
using Loon.Utils.Debug;
using Loon.Utils;
using Loon.Core.Graphics.OpenGL.Particle;
namespace Loon.Core.Graphics.OpenGL.Particle
{
    
	public interface Value {

	   float GetValue(float time);
	}

	public class SimpleValue : Value {

		private float value;

        internal SimpleValue(float value)
        {
			this.value = value;
		}

		public float GetValue(float time) {
			return value;
		}

		public void SetValue(float value) {
			this.value = value;
		}
	}

	public class RandomValue : Value {

		private float value;

        internal RandomValue(float value)
        {
			this.value = value;
		}

		public float GetValue(float time) {
			return (MathUtils.Random() * value);
		}

		public void SetValue(float value) {
			this.value = value;
		}

		public float GetValue() {
			return value;
		}
	}

	public class LinearInterpolator : Value {

		private List<Vector2f> curve;

		private bool active;

		private int min;

		private int max;

		public LinearInterpolator(List<Vector2f> curve, int min, int max) {
			this.curve = curve;
			this.min = min;
			this.max = max;
			this.active = false;
		}

		public void SetCurve(List<Vector2f> curve) {
			this.curve = curve;
		}

		public List<Vector2f> GetCurve() {
			return curve;
		}

		public float GetValue(float t) {
			Vector2f p0 = (Vector2f) curve[0];
			for (int i = 1; i < curve.Count; i++) {
				Vector2f p1 = (Vector2f) curve[i];

				if (t >= p0.GetX() && t <= p1.GetX()) {
					float st = (t - p0.GetX()) / (p1.GetX() - p0.GetX());
					float r = p0.GetY() + st * (p1.GetY() - p0.GetY());
					return r;
				}

				p0 = p1;
			}
			return 0;
		}

		public bool IsActive() {
			return active;
		}

		public void SetActive(bool active) {
			this.active = active;
		}

		public int GetMax() {
			return max;
		}

		public void SetMax(int max) {
			this.max = max;
		}

		public int GetMin() {
			return min;
		}

		public void SetMin(int min) {
			this.min = min;
		}
	}

	public class ColorRecord {

		public float pos;

		public LColor col;

		public ColorRecord(float pos, LColor col) {
			this.pos = pos;
			this.col = col;
		}
	}


	public class Range {

		private float max;

		private float min;

		private bool enabled = false;

		internal Range(float min, float max) {
			this.min = min;
			this.max = max;
		}

		public float Random() {
			return (min + (MathUtils.Random() * (max - min)));
		}

		public bool IsEnabled() {
			return enabled;
		}

		public void SetEnabled(bool enabled) {
			this.enabled = enabled;
		}

		public float GetMax() {
			return max;
		}

		public void SetMax(float max) {
			this.max = max;
		}

		public float GetMin() {
			return min;
		}

		public void SetMin(float min) {
			this.min = min;
		}
	}

    public class ConfigEmitter : ParticleEmitter
    {
        
	private static string relativePath = "";

	public static void SetRelativePath(string path) {
		if (!path.EndsWith("/")) {
			path += "/";
		}
		relativePath = path;
	}

	public Range spawnInterval = new Range(100, 100);

	public Range spawnCount = new Range(5, 5);

	public Range initialLife = new Range(1000, 1000);

	public Range initialSize = new Range(10, 10);

	public Range xOffset = new Range(0, 0);

	public Range yOffset = new Range(0, 0);

	public RandomValue spread = new RandomValue(360);

	public SimpleValue angularOffset = new SimpleValue(0);

	public Range initialDistance = new Range(0, 0);

	public Range speed = new Range(50, 50);

	public SimpleValue growthFactor = new SimpleValue(0);

	public SimpleValue gravityFactor = new SimpleValue(0);

	public SimpleValue windFactor = new SimpleValue(0);

	public Range length = new Range(1000, 1000);

	public List<ColorRecord> colors = new List<ColorRecord>();

	public SimpleValue startAlpha = new SimpleValue(255);

	public SimpleValue endAlpha = new SimpleValue(0);

	public LinearInterpolator alpha;

	public LinearInterpolator size;

	public LinearInterpolator velocity;

	public LinearInterpolator scaleY;

	public Range emitCount = new Range(1000, 1000);

	public int usePoints = Particle.INHERIT_POINTS;

	public bool useOriented = false;

	public bool useAdditive = false;

	public string name;

	public string imageName = "";

	private LTexture image;

	private bool updateImage;

	private bool enabled = true;

	private float x;

	private float y;

    private long nextSpawn = 0;

	private long timeout;

	private int particleCount;

	private ParticleSystem engine;

	private int leftToEmit;

	protected bool wrapUp = false;

	protected bool completed = false;

	protected bool adjust;

	protected float adjustx;

	protected float adjusty;

	public ConfigEmitter(string name) {
		this.name = name;
		leftToEmit = (int) emitCount.Random();
		timeout = (int) (length.Random());

		colors.Add(new ColorRecord(0, LColor.white));
		colors.Add(new ColorRecord(1, LColor.red));

		List<Vector2f> curve = new List<Vector2f>();
		curve.Add(new Vector2f(0.0f, 0.0f));
		curve.Add(new Vector2f(1.0f, 255.0f));
		alpha = new LinearInterpolator(curve, 0, 255);

		curve = new List<Vector2f>();
		curve.Add(new Vector2f(0.0f, 0.0f));
		curve.Add(new Vector2f(1.0f, 255.0f));
		size = new LinearInterpolator(curve, 0, 255);

		curve = new List<Vector2f>();
		curve.Add(new Vector2f(0.0f, 0.0f));
		curve.Add(new Vector2f(1.0f, 1.0f));
		velocity = new LinearInterpolator(curve, 0, 1);

		curve = new List<Vector2f>();
		curve.Add(new Vector2f(0.0f, 0.0f));
		curve.Add(new Vector2f(1.0f, 1.0f));
		scaleY = new LinearInterpolator(curve, 0, 1);
	}

	public void SetImageName(string img) {
		if (img.Length == 0) {
			img = null;
		}
		this.imageName = img;
		if (img == null) {
			image = null;
		} else {
			updateImage = true;
		}
	}

	public string GetImageName() {
		return imageName;
	}

	public void SetPosition(float x, float y) {
		SetPosition(x, y, true);
	}

	public void SetPosition(float x, float y, bool moveParticles) {
		if (moveParticles) {
			adjust = true;
			adjustx -= this.x - x;
			adjusty -= this.y - y;
		}
		this.x = x;
		this.y = y;
	}

	public float GetX() {
		return x;
	}

	public float GetY() {
		return y;
	}

	public bool IsEnabled() {
		return enabled;
	}

	public void SetEnabled(bool enabled) {
		this.enabled = enabled;
	}

	public void Update(ParticleSystem system, long delta) {
		this.engine = system;

		if (!adjust) {
			adjustx = 0;
			adjusty = 0;
		} else {
			adjust = false;
		}

		if (updateImage) {
			updateImage = false;
			string name = (relativePath + imageName).Trim();
			if (name.Length > 0) {
				try {
					image = LTextures.LoadTexture(name);
				} catch (Exception e) {
					image = null;
					Log.Exception(e);
				}
			}
		}

		if ((wrapUp) || ((length.IsEnabled()) && (timeout < 0))
				|| ((emitCount.IsEnabled() && (leftToEmit <= 0)))) {
			if (particleCount == 0) {
				completed = true;
			}
		}
		particleCount = 0;

		if (wrapUp) {
			return;
		}

		if (length.IsEnabled()) {
			if (timeout < 0) {
				return;
			}
			timeout -= delta;
		}
		if (emitCount.IsEnabled()) {
			if (leftToEmit <= 0) {
				return;
			}
		}

		nextSpawn -= delta;
		if (nextSpawn < 0) {
			nextSpawn = (int) spawnInterval.Random();
			int count = (int) spawnCount.Random();

			for (int i = 0; i < count; i++) {
				Particle p = system.GetNewParticle(this, initialLife.Random());
				p.SetSize(initialSize.Random());
				p.SetPosition(x + xOffset.Random(), y + yOffset.Random());
				p.SetVelocity(0, 0, 0);

				float dist = initialDistance.Random();
				float power = speed.Random();
				if ((dist != 0) || (power != 0)) {
					float s = spread.GetValue(0);
					float ang = (s + angularOffset.GetValue(0) - (spread
							.GetValue() / 2)) - 90;
					float xa = MathUtils.Cos(MathUtils.ToRadians(ang)) * dist;
					float ya = MathUtils.Sin(MathUtils.ToRadians(ang)) * dist;
					p.AdjustPosition(xa, ya);

					float xv = MathUtils.Cos(MathUtils.ToRadians(ang));
					float yv = MathUtils.Sin(MathUtils.ToRadians(ang));
					p.SetVelocity(xv, yv, power * 0.001f);
				}

				if (image != null) {
					p.SetImage(image);
				}

				ColorRecord start = (ColorRecord) colors[0];
				p.SetColor(start.col.r, start.col.g, start.col.b,
						startAlpha.GetValue(0) / 255.0f);
				p.SetUsePoint(usePoints);
				p.SetOriented(useOriented);

				if (emitCount.IsEnabled()) {
					leftToEmit--;
					if (leftToEmit <= 0) {
						break;
					}
				}
			}
		}
	}

    public void AddColorPoint(float pos, LColor col)
    {
        colors.Add(new ColorRecord(pos, col));
    }

	public void UpdateParticle(Particle particle, long delta) {
		particleCount++;

		particle.x += adjustx;
		particle.y += adjusty;

		particle.AdjustVelocity(windFactor.GetValue(0) * 0.00005f * delta,
				gravityFactor.GetValue(0) * 0.00005f * delta);

		float offset = particle.GetLife() / particle.GetOriginalLife();
		float inv = 1 - offset;
		float colOffset = 0;
		float colInv = 1;

		LColor startColor = null;
		LColor endColor = null;
		for (int i = 0; i < colors.Count - 1; i++) {
			ColorRecord rec1 = (ColorRecord) colors[i];
			ColorRecord rec2 = (ColorRecord) colors[i + 1];

			if ((inv >= rec1.pos) && (inv <= rec2.pos)) {
				startColor = rec1.col;
				endColor = rec2.col;

				float step = rec2.pos - rec1.pos;
				colOffset = inv - rec1.pos;
				colOffset /= step;
				colOffset = 1 - colOffset;
				colInv = 1 - colOffset;
			}
		}

		if (startColor != null) {
			float r = (startColor.r * colOffset) + (endColor.r * colInv);
			float g = (startColor.g * colOffset) + (endColor.g * colInv);
			float b = (startColor.b * colOffset) + (endColor.b * colInv);

			float a;
			if (alpha.IsActive()) {
				a = alpha.GetValue(inv) / 255.0f;
			} else {
				a = ((startAlpha.GetValue(0) / 255.0f) * offset)
						+ ((endAlpha.GetValue(0) / 255.0f) * inv);
			}
			particle.SetColor(r, g, b, a);
		}

		if (size.IsActive()) {
			float s = size.GetValue(inv);
			particle.SetSize(s);
		} else {
			particle.AdjustSize(delta * growthFactor.GetValue(0) * 0.001f);
		}

		if (velocity.IsActive()) {
			particle.SetSpeed(velocity.GetValue(inv));
		}

		if (scaleY.IsActive()) {
			particle.SetScaleY(scaleY.GetValue(inv));
		}
	}

	public bool Completed() {
		if (engine == null) {
			return false;
		}

		if (length.IsEnabled()) {
			if (timeout > 0) {
				return false;
			}
			return completed;
		}
		if (emitCount.IsEnabled()) {
			if (leftToEmit > 0) {
				return false;
			}
			return completed;
		}

		if (wrapUp) {
			return completed;
		}

		return false;
	}

	public void Replay() {
		Reset();
		nextSpawn = 0;
		leftToEmit = (int) emitCount.Random();
		timeout = (int) (length.Random());
	}

	public void Reset() {
		completed = false;
		if (engine != null) {
			engine.ReleaseAll(this);
		}
	}

	public void ReplayCheck() {
		if (Completed()) {
			if (engine != null) {
				if (engine.GetParticleCount() == 0) {
					Replay();
				}
			}
		}
	}

	public bool UseAdditive() {
		return useAdditive;
	}

	public bool IsOriented() {
		return this.useOriented;
	}

	public bool UsePoints(ParticleSystem system) {
		return (this.usePoints == Particle.INHERIT_POINTS)
				&& (system.UsePoints())
				|| (this.usePoints == Particle.USE_POINTS);
	}

	public LTexture GetImage() {
		return image;
	}

	public void WrapUp() {
		wrapUp = true;
	}

	public void ResetState() {
		wrapUp = false;
		Replay();
	}

	public override string ToString() {
		return "[" + name + "]";
	}
    }
}
