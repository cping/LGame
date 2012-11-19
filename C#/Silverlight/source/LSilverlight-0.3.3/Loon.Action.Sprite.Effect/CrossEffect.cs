namespace Loon.Action.Sprite.Effect
{
    using Loon.Core;
    using Loon.Core.Graphics.OpenGL;
    using Loon.Core.Timer;
    using Loon.Core.Geom;
    using Loon.Core.Graphics;

    public class CrossEffect : LObject, ISprite
    {

	private int width, height;

	private bool visible, complete;

	private LTexture otexture, ntexture;

	private LTimer timer;

	private int count, code;

	private int maxcount = 16;

	private int part;

	private int left;

	private int right;

	private LTexture tmp;

    public CrossEffect(int c, string fileName)
        : this(c, new LTexture(fileName))
    {
		
	}

	public CrossEffect(int c, string file1, string file2):this(c, new LTexture(file1), new LTexture(file2)){
		
	}

	public CrossEffect(int c, LTexture o):	this(c, o, null) {
	
	}

	public CrossEffect(int c, LTexture o, LTexture n) {
		this.code = c;
		this.otexture = o;
		this.ntexture = n;
		this.width = o.GetWidth();
		this.height = o.GetHeight();
		if (width > height) {
			maxcount = 16;
		} else {
			maxcount = 8;
		}
		this.timer = new LTimer(160);
		this.visible = true;
	}

	public void SetDelay(long delay) {
		timer.SetDelay(delay);
	}

	public long GetDelay() {
		return timer.GetDelay();
	}

	public bool IsComplete() {
		return complete;
	}

	public override int GetHeight() {
		return height;
	}

    public override int GetWidth()
    {
		return width;
	}

    public override void Update(long elapsedTime)
    {

		if (complete) {
			return;
		}
		if (this.count > this.maxcount) {
			this.complete = true;
		}
		if (timer.Action(elapsedTime)) {
			count++;
		}
	}

	public void CreateUI(GLEx g) {
		if (!visible) {
			return;
		}
		if (complete) {
			if (ntexture != null) {
				if (alpha > 0 && alpha < 1) {
					g.SetAlpha(alpha);
				}
				g.DrawTexture(ntexture, X(), Y());
				if (alpha > 0 && alpha < 1) {
					g.SetAlpha(1f);
				}
			}
			return;
		}
		if (alpha > 0 && alpha < 1) {
			g.SetAlpha(alpha);
		}
		part = 0;
		left = 0;
		right = 0;
		tmp = null;
		switch (code) {
		default:
			part = width / this.maxcount / 2;
			for (int i = 0; i <= this.maxcount; i++) {
				if (i <= this.count) {
					tmp = this.ntexture;
					if (tmp == null) {
						continue;
					}
				} else {
					tmp = this.otexture;
				}
				tmp.GLBegin();
				left = i * 2 * part;
				right = width - ((i + 1) * 2 - 1) * part;
				tmp.Draw(X() + left, Y(), part, height, left, 0, left + part,
						height);
				tmp.Draw(X() + right, Y(), part, height, right, 0,
						right + part, height);
				tmp.GLEnd();
			}
			break;
		case 1:
            
			part = height / this.maxcount / 2;
			for (int i = 0; i <= this.maxcount; i++) {
				if (i <= this.count) {
					tmp = this.ntexture;
					if (tmp == null) {
						continue;
					}
				} else {
					tmp = this.otexture;
				}
				int up = i * 2 * part;
				int down = height - ((i + 1) * 2 - 1) * part;
				tmp.GLBegin();
				tmp.Draw(0, up, width, part, 0, up, width, up + part);
				tmp.Draw(0, down, width, part, 0, down, width, down + part);
				tmp.GLEnd();
			}
			break;
		}
		if (alpha > 0 && alpha < 1) {
			g.SetAlpha(1f);
		}
	}

	public void Reset() {
		this.complete = false;
		this.count = 0;
	}

	public LTexture GetBitmap() {
		return otexture;
	}

	public RectBox GetCollisionBox() {
		return GetRect(X(), Y(), width, height);
	}

	public bool IsVisible() {
		return visible;
	}

	public void SetVisible(bool visible) {
		this.visible = visible;
	}

	public int GetMaxCount() {
		return maxcount;
	}

	public void SetMaxCount(int maxcount) {
		this.maxcount = maxcount;
	}

	public void Dispose() {
		if (otexture != null) {
			otexture.Destroy();
			otexture = null;
		}
		if (ntexture != null) {
			ntexture.Destroy();
			ntexture = null;
		}
	}

    }
}
