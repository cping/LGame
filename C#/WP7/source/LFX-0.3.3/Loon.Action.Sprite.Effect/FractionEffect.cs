using Loon.Core;
using Loon.Core.Timer;
using Loon.Core.Graphics;
using Loon.Core.Graphics.OpenGL;
using Loon.Java;
using Loon.Core.Geom;
using Loon.Utils;
using Microsoft.Xna.Framework;

namespace Loon.Action.Sprite.Effect
{
   public class FractionEffect : LObject , ISprite {

	private int maxElements = 6;

	private LTimer timer = new LTimer(40);

	private int width, height, scaleWidth, scaleHeight, size;

	private float expandLimit = 1.2f;

	private int exWidth, exHeigth;

	// 0 = x
	// 1 = y
	// 2 = vx
	// 3 = vy
	// 4 = color
	// 5 = countToCrush;
	private float[] fractions;

	private LPixmapData pixmap;

	private bool isClose, isVisible;

	private int loopCount, loopMaxCount = 16;

	private long elapsed;

	private LTexture tmp;

	private PixelThread pixelThread;

	public FractionEffect(string resName, bool remove, float scale) {
		Init(LTextures.LoadTexture(resName), 1.2f, remove, scale);
	}

	public FractionEffect(string resName, float limit, bool remove,
			float scale) {
		Init(LTextures.LoadTexture(resName), limit, remove, scale);
	}

	public FractionEffect(string resName):this(resName, 1.2f) {

	}

	public FractionEffect(string resName, float scale) {
		Init(LTextures.LoadTexture(resName), 1.2f, false, scale);
	}

	public FractionEffect(string resName, float limit, float scale) {
		Init(LTextures.LoadTexture(resName), limit, false, scale);
	}

	public FractionEffect(LTexture texture, float scale) {
		Init(texture, 1.2f, false, scale);
	}

	public FractionEffect(LTexture texture, float limit, float scale) {
		Init(texture, limit, false, scale);
	}

	public FractionEffect(LTexture texture, float limit, bool remove,
			float scale) {
		Init(texture, limit, remove, scale);
	}

	private void Init(LTexture tex2d, float limit, bool remove, float scale) {
		this.isVisible = true;
		this.expandLimit = limit;
		this.width = tex2d.GetWidth();
		this.height = tex2d.GetHeight();
		this.scaleWidth = (int) (width * scale);
		this.scaleHeight = (int) (height * scale);
		this.loopMaxCount = (MathUtils.Max(scaleWidth, scaleHeight) / 2) + 1;
		this.fractions = new float[(scaleWidth * scaleHeight) * maxElements];
		this.exWidth = (int) (scaleWidth * expandLimit);
		this.exHeigth = (int) (scaleHeight * expandLimit);
		LPixmap image = tex2d.GetImage().ScaledInstance(scaleWidth, scaleHeight);
		Color[] pixels = image.GetData();
		if (image != null) {
			image.Dispose();
			image = null;
		}
		this.size = pixels.Length;
		this.pixmap = new LPixmapData(exWidth, exHeigth, true);
		int no = 0, idx = 0;
        int length = fractions.Length;
		float angle = 0;
		float speed = 0;
		System.Random random = LSystem.random;
		for (int y = 0; y < scaleHeight; y++) {
			for (int x = 0; x < scaleWidth; x++) {
				if (idx + maxElements < length) {
					no = y * scaleWidth + x;
					angle = random.Next(360);
                    speed = 10f / random.Next(30);
					fractions[idx + 0] = x;
					fractions[idx + 1] = y;
					fractions[idx + 2] = (MathUtils.Cos(angle * MathUtils.PI
							/ 180) * speed);
					fractions[idx + 3] = (MathUtils.Sin(angle * MathUtils.PI
							/ 180) * speed);
					fractions[idx + 4] = (pixels[no].PackedValue == 0xff00 ? 0xffffff
							: pixels[no].PackedValue);
					fractions[idx + 5] = x / 6 + random.Next(10);
					idx += maxElements;
				}
			}
		}
		if (remove) {
			if (tex2d != null) {
				tex2d.Destroy();
				tex2d = null;
			}
		}
		this.tmp = tex2d;
		this.StartUsePixelThread();
	}

	public void SetDelay(long d) {
		timer.SetDelay(d);
	}

	public long GetDelay() {
		return timer.GetDelay();
	}

	private class PixelThread : Thread {

        private FractionEffect effect;

        public PixelThread(FractionEffect e)
        {
            this.effect = e;
        }

         static void FilterFractions(int size, float[] fractions, int width,
            int height, Color[] pixels, int numElements)
        {
                int x, y;
                int idx = 0;
                for (int j = 0; j < size; j++)
                {
                    idx = j * numElements;
                    if (fractions[idx + 4] != 0xffffff)
                    {
                        if (fractions[idx + 5] <= 0)
                        {
                            fractions[idx + 0] += fractions[idx + 2];
                            fractions[idx + 1] += fractions[idx + 3];
                            fractions[idx + 3] += 0.1f;
                        }
                        else
                        {
                            fractions[idx + 5]--;
                        }
                        x = (int)fractions[idx + 0];
                        y = (int)fractions[idx + 1];
                        if (x > -1 && y > -1 && x < width && y < height)
                        {
                            pixels[x + y * width].PackedValue = System.Convert.ToUInt32(fractions[idx + 4]);
                        }
                    }
                }
        
        }

		public override void Run() {
            for (; !effect.isClose && !effect.IsComplete(); )
            {
                if (!effect.isVisible)
                {
					continue;
				}
                if (effect.timer.Action(effect.elapsed))
                {
                    if (effect.pixmap.IsDirty())
                    {
						continue;
					}
                    effect.pixmap.Reset();
                    FilterFractions(effect.size, effect.fractions,
                            effect.pixmap.GetWidth(), effect.pixmap.GetHeight(),
                            effect.pixmap.GetPixels(), effect.maxElements);
                    effect.pixmap.Submit();
                    effect.loopCount++;
				}
			}
		}
	}

	 void StartUsePixelThread() {
		if (pixelThread == null) {
			pixelThread = new PixelThread(this);
			pixelThread.Start();
		}
	}

	 void EndUsePixelThread() {
		if (pixelThread != null) {
			try {
				pixelThread.Interrupt();
				pixelThread = null;
			} catch (System.Exception) {
				pixelThread = null;
			}
		}
	}

	public override void Update(long elapsedTime) {
		this.elapsed = elapsedTime;
	}

	public void CreateUI(GLEx g) {
		if (isClose) {
			return;
		}
		if (!isVisible) {
			return;
		}
		if (IsComplete()) {
			return;
		}
		if (alpha > 0 && alpha < 1) {
			g.SetAlpha(alpha);
		}
		pixmap.Draw(g, X(), Y(), width, height);
		if (alpha > 0 && alpha < 1) {
			g.SetAlpha(1f);
		}
	}

	public void Reset() {
		pixmap.Reset();
		loopCount = 0;
	}

	public bool IsComplete() {
		bool stop = pixmap.IsClose() || loopCount > loopMaxCount;
		if (!stop) {
			StartUsePixelThread();
		} else {
			EndUsePixelThread();
		}
		return stop;
	}

	public override int GetHeight() {
		return height;
	}

    public override int GetWidth()
    {
		return width;
	}

	public LTexture GetBitmap() {
		return tmp;
	}

	public RectBox GetCollisionBox() {
		return GetRect(X(), Y(), width, height);
	}

	public bool IsVisible() {
		return isVisible;
	}

	public void SetVisible(bool visible) {
		this.isVisible = true;
	}

	public int GetLoopCount() {
		return loopCount;
	}

	public void SetLoopCount(int loopCount) {
		this.loopCount = loopCount;
	}

	public int GetLoopMaxCount() {
		return loopMaxCount;
	}

	public void SetLoopMaxCount(int loopMaxCount) {
		this.loopMaxCount = loopMaxCount;
	}

	public void Dispose() {
		this.isClose = true;
		this.EndUsePixelThread();
		if (pixmap != null) {
			pixmap.Dispose();
		}
		if (tmp != null) {
			tmp.Destroy();
			tmp = null;
		}
	}

}
}
