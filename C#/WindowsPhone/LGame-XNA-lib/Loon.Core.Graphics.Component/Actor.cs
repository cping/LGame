namespace Loon.Core.Graphics.Component
{
    using System;
    using System.Collections;
    using Loon.Core.Graphics.Opengl;
    using Loon.Core.Geom;
    using Loon.Core.Timer;
    using Loon.Action.Sprite;
    using Loon.Utils;
    using Loon.Action;
    using Loon.Action.Map;
using Loon.Java;

    public class Actor : LObject, LRelease, Loon.Action.ActionBind
    {
        
	private static int sequenceNumber = 0;

	private int noSequenceNumber;

	private int lastPaintSequenceNumber;

	internal bool visible = true, drag = true, click = true;

	private ActorLayer gameLayer;

	private LTexture image;

	internal object data, tag;

	private RectBox boundingRect;

	private float[] xs = new float[4];

	private float[] ys = new float[4];

	private LTimer timer = new LTimer(0);

	private Animation animation;

	protected internal bool isAnimation;

	protected internal LColor filterColor;

	protected internal ActorListener actorListener;

	protected internal float scaleX = 1, scaleY = 1;

    public bool isConsumerDrawing = true;

	public Actor(Animation animation):this(animation, 0, 0) {
		
	}

	public Actor(Animation animation, int x, int y) {
		if (animation == null) {
			throw new RuntimeException("Animation is null !");
		}
		this.noSequenceNumber = sequenceNumber++;
		this.animation = animation;
		this.isAnimation = true;
		this.location.Set(x, y);
		this.SetImage(animation.GetSpriteImage());
	}

	public Actor():this((LTexture) null) {
		
	}

	public Actor(LTexture image, int x, int y) {
		this.noSequenceNumber = sequenceNumber++;
		this.location.Set(x, y);
		this.SetImage(image);
	}

	public Actor(LTexture image):this(image, 0, 0) {
		
	}

	public Actor(string fileName, int x, int y):this(LTextures.LoadTexture(fileName), x, y) {
		
	}

	public Actor(string fileName):this(fileName, 0, 0) {
		
	}

	public virtual void StopAnimation() {
		this.isAnimation = false;
	}

    public virtual void StartAnimation()
    {
		this.isAnimation = true;
	}

    protected virtual void SetSize(int w, int h)
    {
		if (boundingRect != null) {
			boundingRect.SetBounds(location.x, location.y, w, h);
		} else {
			boundingRect = new RectBox(location.x, location.y, w, h);
		}
	}

	public MoveTo MoveTo(int x, int y) {
		FailIfNotInLayer();
		return gameLayer.CallMoveTo(this, x, y);
	}

    public MoveTo MoveTo(int x, int y, bool flag)
    {
		FailIfNotInLayer();
		return gameLayer.CallMoveTo(this, x, y, flag);
	}

	public FadeTo FadeOut() {
		FailIfNotInLayer();
		return gameLayer.CallFadeOutTo(this, 60);
	}

	public FadeTo FadeIn() {
		FailIfNotInLayer();
		return gameLayer.CallFadeInTo(this, 60);
	}

	public RotateTo RotateTo(float rotate, float speed) {
		FailIfNotInLayer();
		return gameLayer.CallRotateTo(this, rotate, speed);
	}

	public RotateTo RotateTo(float rotate) {
		return RotateTo(rotate, 2.0F);
	}

	public JumpTo JumpTo(int jump, float g) {
		FailIfNotInLayer();
		return gameLayer.CallJumpTo(this, -jump, g);
	}


	public JumpTo JumpTo(int jump) {
		return JumpTo(jump, 0.3F);
	}

	public CircleTo CircleTo(int radius, int velocity) {
		FailIfNotInLayer();
		return gameLayer.CallCircleTo(this, radius, velocity);
	}

	public FireTo FireTo(int endX, int endY, double speed) {
		FailIfNotInLayer();
		return gameLayer.CallFireTo(this, endX, endY, speed);
	}

	public FireTo FireTo(int endX, int endY) {
		return FireTo(endX, endY, 10);
	}

	public ScaleTo ScaleTo(float sx, float sy) {
		FailIfNotInLayer();
		return gameLayer.CallScaleTo(this, sx, sy);
	}

	public ScaleTo ScaleTo(float s) {
		FailIfNotInLayer();
		return gameLayer.CallScaleTo(this, s, s);
	}

	public ArrowTo ArrowTo(float tx, float ty) {
		FailIfNotInLayer();
		return gameLayer.CallArrowTo(this, tx, ty);
	}

    public virtual void RemoveActionEvents()
    {
		FailIfNotInLayer();
		gameLayer.RemoveActionEvents(this);
	}

    public virtual void SetScale(float s)
    {
		this.SetScale(s, s);
	}

    public virtual void SetScale(float sx, float sy)
    {
		if (this.scaleX == sx && this.scaleY == sy) {
			return;
		}
		this.scaleX = sx;
		this.scaleY = sy;
	}

    public virtual float GetScaleX()
    {
		return this.scaleX;
	}

    public virtual float GetScaleY()
    {
		return this.scaleY;
	}

	public virtual void DownClick(int x, int y) {

	}

	public virtual void UpClick(int x, int y) {

	}

	public virtual void DownKey() {

	}

	public virtual void UpKey() {

	}

	public virtual void Drag(int x, int y) {

	}

	public override void Update(long elapsedTime) {
		if (timer.Action(elapsedTime)) {
			if (isAnimation) {
				if (animation != null) {
					animation.Update(elapsedTime);
					SetImage(animation.GetSpriteImage());
				}
			}
			lock (typeof(LLayer)) {
				Action(elapsedTime);
			}
		}
	}

    public virtual void SetDelay(long delay)
    {
		timer.SetDelay(delay);
	}

    public virtual long GetDelay()
    {
		return timer.GetDelay();
	}

	public virtual void Action(long elapsedTime) {

	}

    public override int X()
    {
        return location.X();
    }

    public override int Y()
    {
        return location.Y();
    }

    public override float GetX()
    {
        return location.x;
    }

    public override float GetY()
    {
        return location.y;
    }

    public override float GetRotation()
    {
        return this.rotation;
    }

    /// <summary>
    /// 决定当前对象旋转方向
    /// </summary>
    ///
    /// <param name="rotation_0"></param>
    public override void SetRotation(float rotation_0)
    {
        if (rotation_0 >= 360)
        {
            if (rotation_0 < 720)
            {
                rotation_0 -= 360;
            }
            else
            {
                rotation_0 %= 360;
            }
        }
        else if (rotation_0 < 0)
        {
            if (rotation_0 >= -360)
            {
                rotation_0 += 360;
            }
            else
            {
                rotation_0 = 360 + rotation_0 % 360;
            }
        }
        if (this.rotation != rotation_0)
        {
            this.rotation = rotation_0;
            this.boundingRect = null;
            this.SizeChanged();
        }
    }

    public override int GetWidth()
    {
        if (image != null)
        {
            return image.GetWidth();
        }
        return GetRectBox().width;
    }

    public override int GetHeight()
    {
        if (image != null)
        {
            return image.GetHeight();
        }
        return GetRectBox().height;
    }

    /// <summary>
    /// 根据旋转方向移动坐标
    /// </summary>
    ///
    /// <param name="distance"></param>
    public virtual void Move(double distance)
    {
        double angle = MathUtils.ToRadians(GetRotation());
        int x = (int)MathUtils.Round(GetX() + MathUtils.Cos(angle) * distance);
        int y = (int)MathUtils.Round(GetY() + MathUtils.Sin(angle) * distance);
        SetLocation(x, y);
    }

    public override void Move(Vector2f v)
    {
        Move(v.x, v.y);
    }

    public virtual void Move(int x, int y)
    {
        Move(x, y);
    }

    public override void Move(float x, float y)
    {
        SetLocationDrag(location.X() + x, location.Y() + y);
    }

    public override void SetX(int x)
    {
        this.SetLocationDrag(x, Y());
    }

    public override void SetY(int y)
    {
        this.SetLocationDrag(X(), y);
    }

    public override void SetX(float x)
    {
        this.SetLocation(x, GetY());
    }

    public override void SetY(float y)
    {
        this.SetLocation(GetX(), y);
    }

    public override void SetLocation(int x, int y)
    {
        this.SetLocationDrag(x, y);
    }

    public override void SetLocation(float x, float y)
    {
        SetLocationDrag(x, y);
    }

    private void SetLocationDrag(float x, float y)
    {
        this.FailIfNotInLayer();
        float oldX = location.GetX();
        float oldY = location.GetY();
        if (this.gameLayer.IsBounded())
        {
            location.x = this.LimitValue(x, this.gameLayer.GetWidth()
                    - GetWidth());
            location.y = this.LimitValue(y, this.gameLayer.GetHeight()
                    - GetHeight());
        }
        else
        {
            location.x = x;
            location.y = y;
        }
        if (location.x != oldX || location.y != oldY)
        {
            if (this.boundingRect != null)
            {
                float dx = (location.GetX() - oldX) * this.gameLayer.cellSize;
                float dy = (location.GetY() - oldY) * this.gameLayer.cellSize;
                this.boundingRect.SetX(this.boundingRect.GetX() + dx);
                this.boundingRect.SetY(this.boundingRect.GetY() + dy);
                for (int i = 0; i < 4; ++i)
                {
                    this.xs[i] += dx;
                    this.ys[i] += dy;
                }
            }
            this.LocationChanged(oldX, oldY);
        }
    }

    private float LimitValue(float v, int limit)
    {
        if (v < 0)
        {
            v = 0;
        }
        if (limit < v)
        {
            v = limit;
        }
        return v;
    }

    public virtual ActorLayer GetLLayer()
    {
        return this.gameLayer;
    }

    protected internal virtual void AddLayer(ActorLayer g)
    {
    }

    public LTexture GetImage()
    {
        return this.image;
    }

    public void SetImage(string filename)
    {
        this.SetImage(LTextures.LoadTexture(filename));
    }

    public void SetImage(LTexture img)
    {
        if (img != null || this.image != null)
        {
            bool sizeChanged = true;
            if (img != null && this.image != null
                    && img.GetWidth() == this.image.GetWidth()
                    && img.GetHeight() == this.image.GetHeight())
            {
                sizeChanged = false;
            }
            if (image != null && image.GetParent() == null
                    && image.IsChildAllClose())
            {
                if (image != null)
                {
                    image.Destroy();
                    image = null;
                }
            }
            this.image = img;
            if (sizeChanged)
            {
                this.boundingRect = null;
                this.SizeChanged();
            }
        }
    }

    public void SetLocationInPixels(float x, float y)
    {
        float xCell = this.gameLayer.ToCellFloor(x);
        float yCell = this.gameLayer.ToCellFloor(y);
        if (xCell != location.x || yCell != location.y)
        {
            this.SetLocationDrag(xCell, yCell);
        }
    }

    internal void SetLayer(ActorLayer g)
    {
        this.gameLayer = g;
    }

    internal void AddLayer(float x, float y, ActorLayer g)
    {
        if (g.IsBounded())
        {
            x = this.LimitValue(x, g.GetWidth() - GetWidth());
            y = this.LimitValue(y, g.GetHeight() - GetHeight());
        }
        this.boundingRect = null;
        this.SetLayer(g);
        this.SetLocation(x, y);
    }

    /// <summary>
    /// 获得当前Actor碰撞盒
    /// </summary>
    ///
    /// <returns></returns>
    public RectBox GetRectBox()
    {
        RectBox tmp = GetBoundingRect();
        if (tmp == null)
        {
            return GetRect(location.x, location.y, GetWidth() * scaleX,
                    GetHeight() * scaleY);
        }
        return GetRect(location.x, location.y, tmp.width * scaleX, tmp.height
                * scaleY);
    }

    /// <summary>
    /// 获得当前Actor碰撞盒(内部使用)
    /// </summary>
    ///
    /// <returns></returns>
    internal RectBox GetBoundingRect()
    {
        if (this.boundingRect == null)
        {
            this.CalcBounds();
        }
        return this.boundingRect;
    }

    /// <summary>
    /// 绘图接口，用以绘制额外的图形到Actor
    /// </summary>
    ///
    /// <param name="g"></param>
    public virtual void Draw(GLEx g)
    {

    }

    /// <summary>
    /// 矫正当前图像大小
    /// </summary>
    ///
    private void CalcBounds()
    {
        ActorLayer layer = this.GetLLayer();
        if (layer != null)
        {
            int width;
            int height;
            int cellSize = layer.GetCellSize();
            int minY = 0;
            if (this.image == null)
            {
                width = location.X() * cellSize + cellSize;
                height = location.Y() * cellSize + cellSize;
                this.boundingRect = new RectBox(width, height, 0, 0);
                for (minY = 0; minY < 4; ++minY)
                {
                    this.xs[minY] = width;
                    this.ys[minY] = height;
                }
            }
            else
            {
                this.boundingRect = MathUtils.GetBounds(location.x,
                        location.y, this.image.GetWidth(),
                        this.image.GetHeight(), rotation);
            }
        }
    }

    public RectBox GetRandLocation()
    {
        if (gameLayer != null)
        {
            return gameLayer.GetRandomLayerLocation(this);
        }
        return null;
    }

    public void SetTag(object o)
    {
        this.tag = o;
    }

    public object GetTag()
    {
        return this.tag;
    }

    internal void SetData(object o)
    {
        this.data = o;
    }

    internal object GetData()
    {
        return this.data;
    }

    public void SendToFront()
    {
        if (gameLayer != null)
        {
            gameLayer.SendToFront(this);
        }
    }

    public void SendToBack()
    {
        if (gameLayer != null)
        {
            gameLayer.SendToBack(this);
        }
    }

    public float ToPixel(float x)
    {
        if (gameLayer == null)
        {
            return 0;
        }
        else
        {
            return x * gameLayer.cellSize + gameLayer.cellSize / 2;
        }
    }

    private float[] pos = new float[2];

    public float[] ToPixels()
    {
        float size = gameLayer.cellSize / 2;
        pos[0] = location.x * gameLayer.cellSize + size;
        pos[1] = location.y * gameLayer.cellSize + size;
        return pos;
    }

    private void SizeChanged()
    {
        if (this.gameLayer != null)
        {
            this.gameLayer.UpdateObjectSize(this);
        }
    }

    private void LocationChanged(float oldX, float oldY)
    {
        if (this.gameLayer != null)
        {
            this.gameLayer.UpdateObjectLocation(this, oldX, oldY);
        }
    }

    private void FailIfNotInLayer()
    {
        if (this.gameLayer == null)
        {
            throw new InvalidOperationException(
                    "The actor has not been inserted into a Layer so it has no location yet !");
        }
    }

    private static bool CheckOutside(float[] myX, float[] myY,
            float[] otherX, float[] otherY)
    {
        for (int v = 0; v < 4; ++v)
        {
            int v1 = v + 1 & 3;
            float edgeX = myX[v] - myX[v1];
            float edgeY = myY[v] - myY[v1];
            float reX = -edgeY;
            float reY = edgeX;
            if (reX != 0 || edgeX != 0)
            {
                for (int e = 0; e < 4; ++e)
                {
                    float scalar = reX * (otherX[e] - myX[v1]) + reY
                            * (otherY[e] - myY[v1]);
                    if (scalar < 0)
                    {
                        continue;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public virtual bool Intersects(Actor other)
    {
        int thisBounds1;
        if (this.image == null)
        {
            if (other.image != null)
            {
                thisBounds1 = this.gameLayer.GetCellSize();
                return other.ContainsPoint(location.X() * thisBounds1
                        + thisBounds1 / 2, location.Y() * thisBounds1
                        + thisBounds1 / 2);
            }
            else
            {
                return location.x == other.location.x
                        && location.y == other.location.y;
            }
        }
        else if (other.image == null)
        {
            thisBounds1 = this.gameLayer.GetCellSize();
            return this.ContainsPoint(other.location.X() * thisBounds1
                    + thisBounds1 / 2, other.location.Y() * thisBounds1
                    + thisBounds1 / 2);
        }
        else
        {
            RectBox thisBounds = this.GetBoundingRect();
            RectBox otherBounds = other.GetBoundingRect();
            if (this.rotation == 0 && other.rotation == 0)
            {
                return thisBounds.Intersects(otherBounds);
            }
            else if (!thisBounds.Intersects(otherBounds))
            {
                return false;
            }
            else
            {
                float[] myX = this.xs;
                float[] myY = this.ys;
                float[] otherX = other.xs;
                float[] otherY = other.ys;
                return (CheckOutside(myX, myY, otherX, otherY)) ? false
                        : !CheckOutside(otherX, otherY, myX, myY);
            }
        }
    }

    public virtual IList GetNeighbours(float distance, bool diagonal,
            Type cls)
    {
        this.FailIfNotInLayer();
        return this.gameLayer.GetNeighbours(this, distance, diagonal, cls);
    }

    public virtual IList GetCollisionObjects(float dx, float dy,
            Type cls)
    {
        this.FailIfNotInLayer();
        return this.gameLayer.GetCollisionObjectsAt(location.X() + dx,
                location.Y() + dy, cls);
    }

    public virtual Actor GetOnlyCollisionObject(float dx, float dy,
            Type cls)
    {
        this.FailIfNotInLayer();
        return this.gameLayer.GetOnlyObjectAt(this, location.x + dx, location.y
                + dy, cls);
    }

    public virtual IList GetCollisionObjects(float radius, Type cls)
    {
        this.FailIfNotInLayer();
        IList inRange = this.gameLayer.GetObjectsInRange(location.x,
                location.y, radius, cls);
        inRange.Remove(this);
        return inRange;
    }

    public virtual IList GetCollisionObjects()
    {
        return GetCollisionObjects(this.GetType());
    }

    public virtual IList GetCollisionObjects(Type cls)
    {
        this.FailIfNotInLayer();
        IList list = this.gameLayer.GetIntersectingObjects(this, cls);
        list.Remove(this);
        return list;
    }

    public virtual Actor GetOnlyCollisionObject()
    {
        return GetOnlyCollisionObject(typeof(Actor));
    }

    public virtual Actor GetOnlyCollisionObject(Type cls)
    {
        this.FailIfNotInLayer();
        return this.gameLayer.GetOnlyIntersectingObject(this, cls);
    }

    public Actor GetOnlyCollisionObjectAt(float x, float y)
    {
        this.FailIfNotInLayer();
        return this.gameLayer.GetOnlyCollisionObjectsAt(x, y);
    }

    public Actor GetOnlyCollisionObjectAt(float x, float y, object t)
    {
        this.FailIfNotInLayer();
        return this.gameLayer.GetOnlyCollisionObjectsAt(x, y, t);
    }

    public bool ContainsPoint(float px, float py)
    {
        this.FailIfNotInLayer();
        if (this.image == null)
        {
            return false;
        }
        else
        {
            if (this.boundingRect == null)
            {
                this.CalcBounds();
            }
            if (this.rotation != 0 && this.rotation != 90
                    && this.rotation != 270)
            {
                for (int v = 0; v < 4; ++v)
                {
                    int v1 = v + 1 & 3;
                    float edgeX = this.xs[v] - this.xs[v1];
                    float edgeY = this.ys[v] - this.ys[v1];
                    float reX = -edgeY;
                    if (reX != 0 || edgeX != 0)
                    {
                        float scalar = reX * (px - this.xs[v1]) + edgeX
                                * (py - this.ys[v1]);
                        if (scalar >= 0)
                        {
                            return false;
                        }
                    }
                }

                return true;
            }
            else
            {
                return px >= this.boundingRect.GetX()
                        && px < this.boundingRect.GetRight()
                        && py >= this.boundingRect.GetY()
                        && py < this.boundingRect.GetBottom();
            }
        }
    }

    public bool IsVisible()
    {
        return visible;
    }

    public void SetVisible(bool v)
    {
        this.visible = v;
    }

    public bool IsDrag()
    {
        return drag;
    }

    public void SetDrag(bool d)
    {
        this.drag = d;
    }

    public bool IsClick()
    {
        return click;
    }

    public void SetClick(bool c)
    {
        this.click = c;
    }

    internal void SetLastPaintSeqNum(int num)
    {
        this.lastPaintSequenceNumber = num;
    }

    public int GetSequenceNumber()
    {
        return this.noSequenceNumber;
    }

    public int GetLastPaintSeqNum()
    {
        return this.lastPaintSequenceNumber;
    }

    public Animation GetAnimation()
    {
        return animation;
    }

    public virtual void SetAnimation(Animation anm)
    {
        if (anm == null)
        {
            throw new Exception("Animation is null !");
        }
        this.animation = anm;
        this.isAnimation = true;
        this.SetImage(anm.GetSpriteImage());
    }

    public bool IsAnimation()
    {
        return isAnimation;
    }

    public virtual void SetAnimation(bool i)
    {
        this.isAnimation = i;
    }

    public virtual void Dispose()
    {
        if (image != null)
        {
            image.Destroy();
            image = null;
        }
        if (animation != null)
        {
            animation.Dispose();
            animation = null;
        }
    }


    public Field2D GetField2D()
    {
        return gameLayer.GetField2D();
    }

    public bool IsBounded()
    {
        return gameLayer.IsBounded();
    }

    public bool IsContainer()
    {
        return gameLayer.IsContainer();
    }

    public bool InContains(int x, int y, int w, int h)
    {
        return gameLayer.Contains(x, y, w, h);
    }

    public int GetContainerWidth()
    {
        return gameLayer.GetWidth();
    }

    public int GetContainerHeight()
    {
        return gameLayer.GetHeight();
    }

    }
}
