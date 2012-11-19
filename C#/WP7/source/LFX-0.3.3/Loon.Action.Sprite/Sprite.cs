namespace Loon.Action.Sprite
{

    using System;
    using System.Collections.Generic;
    using System.Linq;
    using Microsoft.Xna.Framework;
    using Loon.Core;
    using Loon.Core.Graphics.Device;
    using Loon.Core.Graphics.OpenGL;
    using Loon.Action.Collision;
    using Loon.Core.Geom;
    using Loon.Java;
    using Loon.Utils;
    using Loon.Core.Graphics;

    public class Sprite : LObject, ISprite
    {
    
        
		// 默认每帧刷新时间
		private const long defaultTimer = 150;
	
		// 是否可见
		private bool visible;
	
		// 精灵名称
		private string spriteName;
	
		// 精灵图片
		private LTexture image;
	
		// 动画
		private Animation animation;
	
		private int transform;
	
		private float scaleX, scaleY;
	
		/// <summary>
		/// 默认构造函数
		/// </summary>
		///
		public Sprite():this(0, 0) {
			
		}
	
		/// <summary>
		/// 以下参数分别为 坐标x,坐标y
		/// </summary>
		///
		/// <param name="x"></param>
		/// <param name="y"></param>
		public Sprite(float x, float y):this("Sprite" + DateTime.Now.Millisecond, x, y) {
			
		}
	
		/// <summary>
		/// 以下参数分别为 精灵名,坐标x,坐标y
		/// </summary>
		///
		/// <param name="spriteName_0"></param>
		/// <param name="x"></param>
		/// <param name="y"></param>
		private Sprite(string spriteName_0, float x, float y) {
			this.visible = true;
			this.animation = new Animation();
			this.scaleX = 1;
			this.scaleY = 1;
			this.alpha = 1;
			this.SetLocation(x, y);
			this.spriteName = spriteName_0;
			this.visible = true;
            this.transform = LTrans.TRANS_NONE;
		}
	
		/// <summary>
		/// 以下参数分别为 取材文件,每行取材宽度,每列取材长度
		/// </summary>
		///
		/// <param name="fileName"></param>
		/// <param name="row"></param>
		/// <param name="col"></param>
        public Sprite(string fileName, int row, int col)
            : this(fileName, -1, 0, 0, row, col, defaultTimer)
        {
			
		}
	
		/// <summary>
		/// 以下参数分别为 取材文件,每行取材宽度,每列取材长度,平均每桢显示时间
		/// </summary>
		///
		/// <param name="fileName"></param>
		/// <param name="row"></param>
		/// <param name="col"></param>
		/// <param name="timer"></param>
        public Sprite(string fileName, int row, int col, long timer)
            : this(fileName, -1, 0, 0, row, col, timer)
        {
			
		}
	
		/// <summary>
		/// 以下参数分别为 取材文件,坐标x,坐标y,每行取材宽度,每列取材长度
		/// </summary>
		///
		/// <param name="fileName"></param>
		/// <param name="x"></param>
		/// <param name="y"></param>
		/// <param name="row"></param>
		/// <param name="col"></param>
		public Sprite(string fileName, float x, float y, int row, int col):this(fileName, x, y, row, col, defaultTimer) {
			
		}
	
		/// <summary>
		/// 以下参数分别为 取材文件,坐标x,坐标y,每行取材宽度,每列取材长度,平均每桢显示时间
		/// </summary>
		///
		/// <param name="fileName"></param>
		/// <param name="x"></param>
		/// <param name="y"></param>
		/// <param name="row"></param>
		/// <param name="col"></param>
		/// <param name="timer"></param>
		private Sprite(string fileName, float x, float y, int row, int col,
				long timer) :	this(fileName, -1, x, y, row, col, timer){
		
		}
	
		/// <summary>
		/// 以下参数分别为 取材文件,最大分解桢数,坐标x,坐标y,每行取材宽度,每列取材长度
		/// </summary>
		///
		/// <param name="fileName"></param>
		/// <param name="maxFrame"></param>
		/// <param name="x"></param>
		/// <param name="y"></param>
		/// <param name="row"></param>
		/// <param name="col"></param>
		public Sprite(string fileName, int maxFrame, float x, float y, int row,
                int col)
            : this(fileName, maxFrame, x, y, row, col, defaultTimer)
        {
			
		}
	
		/// <summary>
		/// 以下参数分别为 取材文件,最大分解桢数,坐标x,坐标y,每行取材宽度,每列取材长度,平均每桢显示时间
		/// </summary>
		///
		/// <param name="fileName"></param>
		/// <param name="maxFrame"></param>
		/// <param name="x"></param>
		/// <param name="y"></param>
		/// <param name="row"></param>
		/// <param name="col"></param>
		/// <param name="timer"></param>
		public Sprite(string fileName, int maxFrame, float x, float y, int row,
                int col, long timer)
            : this("Sprite" + DateTime.Now.Millisecond, fileName, maxFrame, x, y,
                row, col, timer)
        {
			
		}
	
		/// <summary>
		/// 以下参数分别为 精灵名，取材文件，最大分解桢数,坐标x,坐标y,每行取材宽度,每列取材长度,平均每桢显示时间
		/// </summary>
		///
		/// <param name="spriteName_0"></param>
		/// <param name="fileName"></param>
		/// <param name="maxFrame"></param>
		/// <param name="x"></param>
		/// <param name="y"></param>
		/// <param name="row"></param>
		/// <param name="col"></param>
		/// <param name="timer"></param>
		public Sprite(string spriteName_0, string fileName, int maxFrame, float x,
                float y, int row, int col, long timer)
            : this(spriteName_0, TextureUtils.GetSplitTextures(fileName, row, col),
                maxFrame, x, y, timer)
        {
			
		}
	
		/// <summary>
		/// 注入指定图片
		/// </summary>
		///
		/// <param name="fileName"></param>
        public Sprite(string fileName)
            : this(new LTexture(fileName))
        {
			
		}
	
		/// <summary>
		/// 注入指定图片
		/// </summary>
		///
		/// <param name="images"></param>
        public Sprite(LTexture img)
            : this(new LTexture[] { img }, 0, 0)
        {
			
		}
	
		/// <summary>
		/// 以下参数分别为 图像数组
		/// </summary>
		///
		/// <param name="images"></param>
        public Sprite(LTexture[] images)
            : this(images, 0, 0)
        {
			
		}
	
		/// <summary>
		/// 以下参数分别为 图像数组,坐标x,坐标y
		/// </summary>
		///
		/// <param name="images"></param>
		/// <param name="x"></param>
		/// <param name="y"></param>
        public Sprite(LTexture[] images, float x, float y)
            : this(images, x, y, defaultTimer)
        {
			
		}
	
		/// <summary>
		/// 以下参数分别为 图像数组,平均每桢显示时间
		/// </summary>
		///
		/// <param name="images"></param>
		/// <param name="timer"></param>
        public Sprite(LTexture[] images, long timer)
            : this(images, -1, 0, 0, defaultTimer)
        {
			
		}
	
		/// <summary>
		/// 以下参数分别为 图像数组,坐标x,坐标y,平均每桢显示时间
		/// </summary>
		///
		/// <param name="images"></param>
		/// <param name="x"></param>
		/// <param name="y"></param>
		/// <param name="timer"></param>
        public Sprite(LTexture[] images, float x, float y, long timer)
            : this(images, -1, x, y, timer)
        {
			
		}
	
		/// <summary>
		/// 以下参数分别为 图像数组,最大分解桢数,坐标x,坐标y,平均每桢显示时间
		/// </summary>
		///
		/// <param name="spriteName"></param>
		/// <param name="images"></param>
		/// <param name="maxFrame"></param>
		/// <param name="x"></param>
		/// <param name="y"></param>
		/// <param name="timer"></param>
        public Sprite(LTexture[] images, int maxFrame, float x, float y, long timer)
            : this("Sprite" + JavaRuntime.CurrentTimeMillis(), images, maxFrame, x, y,
                timer)
        {
			
		}
	
		/// <summary>
		/// 以下参数分别为 精灵名，图像数组，最大分解桢数,坐标x,坐标y,平均每桢显示时间
		/// </summary>
		///
		/// <param name="spriteName_0"></param>
		/// <param name="images"></param>
		/// <param name="maxFrame"></param>
		/// <param name="x"></param>
		/// <param name="y"></param>
		/// <param name="timer"></param>
		public Sprite(string spriteName_0, LTexture[] images, int maxFrame, float x,
				float y, long timer) {
			this.visible = true;
					this.animation = new Animation();
					this.scaleX = 1;
					this.scaleY = 1;
					this.alpha = 1;
			this.SetLocation(x, y);
			this.spriteName = spriteName_0;
			this.SetAnimation(animation, images, maxFrame, timer);
			this.visible = true;
			this.transform = LTrans.TRANS_NONE;
		}
	
		/// <summary>
		/// 是否在播放动画
		/// </summary>
		///
		/// <param name="running"></param>
		public void SetRunning(bool running) {
			animation.SetRunning(running);
		}
	
		/// <summary>
		/// 返回当前总桢数
		/// </summary>
		///
		/// <returns></returns>
		public int GetTotalFrames() {
			return animation.GetTotalFrames();
		}
	
		/// <summary>
		/// 设定当前帧
		/// </summary>
		///
		/// <param name="index"></param>
		public void SetCurrentFrameIndex(int index) {
			animation.SetCurrentFrameIndex(index);
		}
	
		/// <summary>
		/// 返回当前桢索引
		/// </summary>
		///
		/// <returns></returns>
		public int GetCurrentFrameIndex() {
			return animation.GetCurrentFrameIndex();
		}
	
		/// <summary>
		/// 获得当前精灵的窗体居中横坐标
		/// </summary>
		///
		/// <param name="x"></param>
		/// <returns></returns>
		public int CenterX(int x) {
			return CenterX(this, x);
		}
	
		/// <summary>
		/// 获得指定精灵的窗体居中横坐标
		/// </summary>
		///
		/// <param name="sprite"></param>
		/// <param name="x"></param>
		/// <returns></returns>
		public static int CenterX(Sprite sprite, int x) {
			int newX = x - (sprite.GetWidth() / 2);
			if (newX + sprite.GetWidth() >= LSystem.screenRect.width) {
				return (LSystem.screenRect.width - sprite.GetWidth() - 1);
			}
			if (newX < 0) {
				return x;
			} else {
				return newX;
			}
		}
	
		/// <summary>
		/// 获得当前精灵的窗体居中纵坐标
		/// </summary>
		///
		/// <param name="y"></param>
		/// <returns></returns>
		public int CenterY(int y) {
			return CenterY(this, y);
		}
	
		/// <summary>
		/// 获得指定精灵的窗体居中纵坐标
		/// </summary>
		///
		/// <param name="sprite"></param>
		/// <param name="y"></param>
		/// <returns></returns>
		public static int CenterY(Sprite sprite, int y) {
			int newY = y - (sprite.GetHeight() / 2);
			if (newY + sprite.GetHeight() >= LSystem.screenRect.height) {
				return (LSystem.screenRect.height - sprite.GetHeight() - 1);
			}
			if (newY < 0) {
				return y;
			} else {
				return newY;
			}
		}
	
		/// <summary>
		/// 插入指定动画
		/// </summary>
		///
		/// <param name="myAnimation"></param>
		/// <param name="images"></param>
		/// <param name="maxFrame"></param>
		/// <param name="timer"></param>
		private void SetAnimation(Animation myAnimation, LTexture[] images,
				int maxFrame, long timer) {
			if (maxFrame != -1) {
				for (int i = 0; i < maxFrame; i++) {
					myAnimation.AddFrame(images[i], timer);
				}
			} else {
				for (int i_0 = 0; i_0 < images.Length; i_0++) {
					myAnimation.AddFrame(images[i_0], timer);
				}
			}
		}
	
		/// <summary>
		/// 插入指定动画
		/// </summary>
		///
		/// <param name="fileName"></param>
		/// <param name="maxFrame"></param>
		/// <param name="row"></param>
		/// <param name="col"></param>
		/// <param name="timer"></param>
		public void SetAnimation(string fileName, int maxFrame, int row, int col,
				long timer) {
			SetAnimation(new Animation(),
					TextureUtils.GetSplitTextures(fileName, row, col), maxFrame,
					timer);
		}
	
		/// <summary>
		/// 插入指定动画
		/// </summary>
		///
		/// <param name="fileName"></param>
		/// <param name="row"></param>
		/// <param name="col"></param>
		/// <param name="timer"></param>
		public void SetAnimation(string fileName, int row, int col, long timer) {
			SetAnimation(fileName, -1, row, col, timer);
		}
	
		/// <summary>
		/// 插入指定动画
		/// </summary>
		///
		/// <param name="images"></param>
		/// <param name="maxFrame"></param>
		/// <param name="timer"></param>
		public void SetAnimation(LTexture[] images, int maxFrame, long timer) {
			SetAnimation(new Animation(), images, maxFrame, timer);
		}
	
		/// <summary>
		/// 插入指定动画
		/// </summary>
		///
		/// <param name="images"></param>
		/// <param name="timer"></param>
		public void SetAnimation(LTexture[] images, long timer) {
			SetAnimation(new Animation(), images, -1, timer);
		}
	
		/// <summary>
		/// 插入指定动画
		/// </summary>
		///
		/// <param name="an"></param>
		public void SetAnimation(Animation an) {
			this.animation = an;
		}
	
		public Animation GetAnimation() {
			return animation;
		}
	
		/// <summary>
		/// 变更动画
		/// </summary>
		///
		public override void Update(long timer) {
			if (visible) {
				animation.Update(timer);
			}
		}
	
		/// <summary>
		/// 变更定位器坐标
		/// </summary>
		///
		/// <param name="vector"></param>
		public void UpdateLocation(Vector2f vector) {
			this.SetX(MathUtils.Round(vector.GetX()));
            this.SetY(MathUtils.Round(vector.GetY()));
		}
	
		public LTexture GetImage() {
			return animation.GetSpriteImage();
		}
	
		public override int GetWidth() {
			LTexture si = animation.GetSpriteImage();
			if (si == null) {
				return -1;
			}
			return si.GetWidth();
		}
	
		public override int GetHeight() {
			LTexture si = animation.GetSpriteImage();
			if (si == null) {
				return -1;
			}
			return si.GetHeight();
		}
	
		/// <summary>
		/// 获得精灵的中间位置
		/// </summary>
		///
		/// <returns></returns>
		public Loon.Core.Geom.Point GetMiddlePoint() {
            return new Loon.Core.Geom.Point(GetLocation().X() + GetWidth() / 2, GetLocation().Y()
					+ GetHeight() / 2);
		}
	
		/// <summary>
		/// 获得两个精灵的中间距离
		/// </summary>
		///
		/// <param name="second"></param>
		/// <returns></returns>
		public float GetDistance(Sprite second) {
			return (float) this.GetMiddlePoint()
					.DistanceTo(second.GetMiddlePoint());
		}
	
		/// <summary>
		/// 返回碰撞盒
		/// </summary>
		///
		/// <returns></returns>
		public virtual RectBox GetCollisionBox() {
			return GetRect(GetLocation().X(), GetLocation().Y(), GetWidth(),
					GetHeight());
		}
	
		/// <summary>
		/// 检查是否与指定精灵位置发生了矩形碰撞
		/// </summary>
		///
		/// <param name="sprite"></param>
		/// <returns></returns>
		public bool IsRectToRect(Sprite sprite) {
			return CollisionHelper.IsRectToRect(this.GetCollisionBox(),
					sprite.GetCollisionBox());
		}
	
		/// <summary>
		/// 检查是否与指定精灵位置发生了圆形碰撞
		/// </summary>
		///
		/// <param name="sprite"></param>
		/// <returns></returns>
		public bool IsCircToCirc(Sprite sprite) {
			return CollisionHelper.IsCircToCirc(this.GetCollisionBox(),
					sprite.GetCollisionBox());
		}
	
		/// <summary>
		/// 检查是否与指定精灵位置发生了方形与圆形碰撞
		/// </summary>
		///
		/// <param name="sprite"></param>
		/// <returns></returns>
		public bool IsRectToCirc(Sprite sprite) {
			return CollisionHelper.IsRectToCirc(this.GetCollisionBox(),
					sprite.GetCollisionBox());
		}
	
		private LColor filterColor;
	
		public virtual void CreateUI(GLEx g) {
			if (!visible) {
				return;
			}
			image = animation.GetSpriteImage();
			if (image == null) {
				return;
			}
			float width = (image.GetWidth() * scaleX);
			float height = (image.GetHeight() * scaleY);
			if (filterColor == null) {
				if (alpha > 0 && alpha < 1) {
					g.SetAlpha(alpha);
				}
				if (LTrans.TRANS_NONE == transform) {
					g.DrawTexture(image, X(), Y(), width, height, rotation);
				} else {
					g.DrawRegion(image, 0, 0, GetWidth(), GetHeight(), transform,
                            X(), Y(), LTrans.TOP | LTrans.LEFT);
				}
				if (alpha > 0 && alpha < 1) {
					g.SetAlpha(1);
				}
				return;
			} else {
				Color old = g.GetColor();
				if (alpha > 0 && alpha < 1) {
					g.SetAlpha(alpha);
				}
				g.SetColor(filterColor);
				if (LTrans.TRANS_NONE == transform) {
					g.DrawTexture(image, X(), Y(), width, height, rotation);
				} else {
					g.DrawRegion(image, 0, 0, GetWidth(), GetHeight(), transform,
                            X(), Y(), LTrans.TOP | LTrans.LEFT);
				}
				g.SetColor(old);
				if (alpha > 0 && alpha < 1) {
					g.SetAlpha(1);
				}
				return;
			}
		}
	
		public virtual bool IsVisible() {
			return visible;
		}
	
		public virtual void SetVisible(bool visible_0) {
			this.visible = visible_0;
		}
	
		public string GetSpriteName() {
			return spriteName;
		}
	
		public void SetSpriteName(string spriteName_0) {
			this.spriteName = spriteName_0;
		}
	
		public int GetTransform() {
			return transform;
		}
	
		public void SetTransform(int t) {
			this.transform = t;
		}
	
		public LColor GetFilterColor() {
			return filterColor;
		}
	
		public void SetFilterColor(LColor c) {
			this.filterColor = c;
		}
	
		public virtual LTexture GetBitmap() {
			return this.image;
		}
	
		public float GetScaleX() {
			return scaleX;
		}
	
		public void SetScaleX(float x) {
			this.scaleX = x;
		}
	
		public float GetScaleY() {
			return scaleY;
		}
	
		public void SetScaleY(float y) {
			this.scaleY = y;
		}
	
		public virtual void Dispose() {
			this.visible = false;
			if (image != null) {
				image.Dispose();
				image = null;
			}
			if (animation != null) {
				animation.Dispose();
				animation = null;
			}
		}
	
    }
}
