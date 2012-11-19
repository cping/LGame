namespace Loon.Action.Sprite
{
    using System;
    using System.Collections.Generic;
    using System.Runtime.CompilerServices;
    using System.Linq;
    using Loon.Core;
    using Loon.Utils;
    using Loon.Java.Collections;
    using Loon.Core.Geom;
    using Loon.Core.Graphics.OpenGL;

    public class Sprites
    {

        internal sealed class _Comparer : IComparer<object>
        {
            public int Compare(object o1, object o2)
            {
                return ((ISprite)o2).GetLayer() - ((ISprite)o1).GetLayer();
            }
        }

	    public const int TYPE_FADE_IN = 0;

        public const int TYPE_FADE_OUT = 1;

        public interface SpriteListener
        {

            void update(ISprite spr);

        }

        private int viewX;

        private int viewY;

        private bool isViewWindowSet, visible;

        private Sprites.SpriteListener sprListerner;

        private static readonly IComparer<Object> DEFAULT_COMPARATOR = new Sprites._Comparer();

        private IComparer<Object> comparator = Sprites.DEFAULT_COMPARATOR;

        private int capacity = 1000;

        private ISprite[] sprites;

        private int size;

        private int width, height;

        
		public Sprites(int w, int h) {
			this.visible = true;
			this.width = w;
			this.height = h;
			this.sprites = new ISprite[capacity];
		}
	
		public Sprites() {
			this.visible = true;
			this.width = LSystem.screenRect.width;
			this.height = LSystem.screenRect.height;
			this.sprites = new ISprite[capacity];
		}
	
		/// <summary>
		/// 设定指定对象到图层最前
		/// </summary>
		///
		/// <param name="sprite"></param>
		public void SendToFront(ISprite sprite) {
			if (this.size <= 1 || this.sprites[0] == sprite) {
				return;
			}
			if (sprites[0] == sprite) {
				return;
			}
			for (int i = 0; i < this.size; i++) {
				if (this.sprites[i] == sprite) {
					this.sprites = (ISprite[]) CollectionUtils.Cut(this.sprites, i);
					this.sprites = (ISprite[]) CollectionUtils.Expand(this.sprites,
							1, false);
					this.sprites[0] = sprite;
					this.SortSprites();
					break;
				}
			}
		}
	
		/// <summary>
		/// 设定指定对象到图层最后
		/// </summary>
		///
		/// <param name="sprite"></param>
		public void SendToBack(ISprite sprite) {
			if (this.size <= 1 || this.sprites[this.size - 1] == sprite) {
				return;
			}
			if (sprites[this.size - 1] == sprite) {
				return;
			}
			for (int i = 0; i < this.size; i++) {
				if (this.sprites[i] == sprite) {
					this.sprites = (ISprite[]) CollectionUtils.Cut(this.sprites, i);
					this.sprites = (ISprite[]) CollectionUtils.Expand(this.sprites,
							1, true);
					this.sprites[this.size - 1] = sprite;
					this.SortSprites();
					break;
				}
			}
		}
	
		/// <summary>
		/// 按所在层级排序
		/// </summary>
		///
		public void SortSprites() {
			Arrays.Sort(this.sprites, this.comparator);
		}
	
		/// <summary>
		/// 扩充当前集合容量
		/// </summary>
		///
		/// <param name="capacity"></param>
		private void ExpandCapacity(int capacity) {
			if (sprites.Length < capacity) {
				ISprite[] bagArray = new ISprite[capacity];
				Array.Copy(sprites, 0, bagArray, 0, size);
				sprites = bagArray;
			}
		}
	
		/// <summary>
		/// 压缩当前集合容量
		/// </summary>
		///
		/// <param name="capacity"></param>
		private void CompressCapacity(int capacity) {
			if (capacity + this.size < sprites.Length) {
				ISprite[] newArray = new ISprite[this.size + 2];
				Array.Copy(sprites, 0, newArray, 0, this.size);
				sprites = newArray;
			}
		}
	
		/// <summary>
		/// 查找指定位置的精灵对象
		/// </summary>
		///
		/// <param name="x"></param>
		/// <param name="y"></param>
		/// <returns></returns>
		public ISprite Find(int x, int y) {
			ISprite[] snapshot = sprites;
			for (int i = snapshot.Length - 1; i >= 0; i--) {
				ISprite child = snapshot[i];
				RectBox rect = child.GetCollisionBox();
				if (rect != null && rect.Contains(x, y)) {
					return child;
				}
			}
			return null;
		}
	
		/// <summary>
		/// 查找指定名称的精灵对象
		/// </summary>
		///
		/// <param name="name"></param>
		/// <returns></returns>
		public ISprite Find(string name) {
			ISprite[] snapshot = sprites;
			for (int i = snapshot.Length - 1; i >= 0; i--) {
				ISprite child = snapshot[i];
				if (child  is  LObject) {
					string childName = ((LObject) child).GetName();
					if (name.Equals(childName,StringComparison.InvariantCultureIgnoreCase)) {
						return child;
					}
				}
			}
			return null;
		}
	
		/// <summary>
		/// 在指定索引处插入一个精灵
		/// </summary>
		///
		/// <param name="index"></param>
		/// <param name="sprite"></param>
		/// <returns></returns>
		public bool Add(int index, ISprite sprite) {
			if (sprite == null) {
				return false;
			}
			if (index > this.size) {
				index = this.size;
			}
			if (index == this.size) {
				this.Add(sprite);
			} else {
				Array.Copy(this.sprites, index, this.sprites, index + 1,
						this.size - index);
				this.sprites[index] = sprite;
				if (++this.size >= this.sprites.Length) {
					ExpandCapacity((size + 1) * 2);
				}
			}
			return sprites[index] != null;
		}
	
		public ISprite GetSprite(int index) {
			if (index < 0 || index > size || index >= sprites.Length) {
				return null;
			}
			return sprites[index];
		}
	
		/// <summary>
		/// 返回位于顶部的精灵
		/// </summary>
		///
		/// <returns></returns>
		public ISprite GetTopSprite() {
			if (size > 0) {
				return sprites[0];
			}
			return null;
		}
	
		/// <summary>
		/// 返回位于底部的精灵
		/// </summary>
		///
		/// <returns></returns>
		public ISprite GetBottomSprite() {
			if (size > 0) {
				return sprites[size - 1];
			}
			return null;
		}
	
		/// <summary>
		/// 返回所有指定类产生的精灵
		/// </summary>
		///
		/// <param name="clazz"></param>
		/// <returns></returns>
		public List<ISprite> GetSprites(
				Type clazz) {
			if (clazz == null) {
				return null;
			}
			List<ISprite> l = new List<ISprite>(size);
			for (int i = size; i > 0; i--) {
				ISprite sprite = (ISprite) sprites[i - 1];
				Type cls = sprite.GetType();
				if (clazz == null || clazz == cls || clazz.IsInstanceOfType(sprite)
						|| clazz.Equals(cls)) {
					l.Add(sprite);
				}
			}
			return l;
		}
	
		/// <summary>
		/// 顺序添加精灵
		/// </summary>
		///
		/// <param name="sprite"></param>
		/// <returns></returns>
		public bool Add(ISprite sprite) {
			if (Contains(sprite)) {
				return false;
			}
			if (this.size == this.sprites.Length) {
				ExpandCapacity((size + 1) * 2);
			}
			return (sprites[size++] = sprite) != null;
		}
	
		/// <summary>
		/// 顺序添加精灵
		/// </summary>
		///
		/// <param name="sprite"></param>
		/// <returns></returns>
		public void Append(ISprite sprite) {
			Add(sprite);
		}
	
		/// <summary>
		/// 检查指定精灵是否存在
		/// </summary>
		///
		/// <param name="sprite"></param>
		/// <returns></returns>
		public bool Contains(ISprite sprite) {
			if (sprite == null) {
				return false;
			}
			if (sprites == null) {
				return false;
			}
			for (int i = 0; i < size; i++) {
				if (sprites[i] != null && sprite.Equals(sprites[i])) {
					return true;
				}
			}
			return false;
		}
	
		/// <summary>
		/// 删除指定索引处精灵
		/// </summary>
		///
		/// <param name="index"></param>
		/// <returns></returns>
		public ISprite Remove(int index) {
			ISprite removed = this.sprites[index];
			int size = this.size - index - 1;
			if (size > 0) {
				Array.Copy(this.sprites, index + 1, this.sprites, index, size);
			}
			this.sprites[--this.size] = null;
			if (size == 0) {
				sprites = new ISprite[0];
			}
			return removed;
		}
	
		/// <summary>
		/// 清空所有精灵
		/// </summary>
		///
		[MethodImpl(MethodImplOptions.Synchronized)]
		public void RemoveAll() {
			Clear();
			this.sprites = new ISprite[0];
		}
	
		/// <summary>
		/// 删除所有指定类
		/// </summary>
		///
		/// <param name="clazz"></param>
		/// <returns></returns>
		public void Remove(Type clazz) {
			if (clazz == null) {
				return;
			}
			for (int i = size; i > 0; i--) {
				ISprite sprite = (ISprite) sprites[i - 1];
				Type cls = sprite.GetType();
				if (clazz == null || clazz == cls || clazz.IsInstanceOfType(sprite)
						|| clazz.Equals(cls)) {
					size--;
					sprites[i - 1] = sprites[size];
					sprites[size] = null;
					if (size == 0) {
						sprites = new ISprite[0];
					} else {
						CompressCapacity(2);
					}
				}
			}
		}
	
		/// <summary>
		/// 删除指定精灵
		/// </summary>
		///
		/// <param name="sprite"></param>
		/// <returns></returns>
		public bool Remove(ISprite sprite) {
			if (sprite == null) {
				return false;
			}
			if (sprites == null) {
				return false;
			}
			bool removed = false;
	
			for (int i = size; i > 0; i--) {
				if (sprite.Equals(sprites[i - 1])) {
					removed = true;
					size--;
					sprites[i - 1] = sprites[size];
					sprites[size] = null;
					if (size == 0) {
						sprites = new ISprite[0];
					} else {
						CompressCapacity(2);
					}
					return removed;
				}
			}
			return removed;
		}
	
		/// <summary>
		/// 删除指定范围内精灵
		/// </summary>
		///
		/// <param name="startIndex"></param>
		/// <param name="endIndex"></param>
		[MethodImpl(MethodImplOptions.Synchronized)]
		public void Remove(int startIndex, int endIndex) {
			int numMoved = this.size - endIndex;
			Array.Copy(this.sprites, endIndex, this.sprites, startIndex,
					numMoved);
			int newSize = this.size - (endIndex - startIndex);
			while (this.size != newSize) {
				this.sprites[--this.size] = null;
			}
			if (size == 0) {
				sprites = new ISprite[0];
			}
		}

        public Point.Point2i GetMinPos()
        {
            Point.Point2i p = new Point.Point2i(0, 0);
            for (int i = 0; i < size; i++)
            {
                ISprite sprite = sprites[i];
                p.x = MathUtils.Min(p.x, sprite.X());
                p.y = MathUtils.Min(p.y, sprite.Y());
            }
            return p;
        }

        public Point.Point2i GetMaxPos()
        {
            Point.Point2i p = new Point.Point2i(0, 0);
            for (int i = 0; i < size; i++)
            {
                ISprite sprite = sprites[i];
                p.x = MathUtils.Max(p.x, sprite.X());
                p.y = MathUtils.Max(p.y, sprite.Y());
            }
            return p;
        }

		/// <summary>
		/// 清空当前精灵集合
		/// </summary>
		///
		[MethodImpl(MethodImplOptions.Synchronized)]
		public void Clear() {
			for (int i = 0; i < sprites.Length; i++) {
				sprites[i] = null;
			}
			size = 0;
		}
	
		/// <summary>
		/// 刷新事务
		/// </summary>
		///
		/// <param name="elapsedTime"></param>
		public void Update(long elapsedTime) {
			bool listerner = (sprListerner != null);
			for (int i = size - 1; i >= 0; i--) {
				ISprite child = sprites[i];
				if (child.IsVisible()) {
					child.Update(elapsedTime);
					if (listerner) {
						sprListerner.update(child);
					}
				}
			}
		}
	
		/// <summary>
		/// 创建UI图像
		/// </summary>
		///
		/// <param name="g"></param>
		public void CreateUI(GLEx g) {
			CreateUI(g, 0, 0);
		}
	
		/// <summary>
		/// 创建UI图像
		/// </summary>
		///
		/// <param name="g"></param>
		public void CreateUI(GLEx g, int x, int y) {
			if (!visible) {
				return;
			}
			int minX, minY, maxX, maxY;
			int clipWidth = g.GetClipWidth();
			int clipHeight = g.GetClipHeight();
			if (this.isViewWindowSet) {
				g.SetClip(x, y, this.width, this.height);
				minX = this.viewX;
				maxX = minX + this.width;
				minY = this.viewY;
				maxY = minY + this.height;
			} else {
				minX = x;
				maxX = x + clipWidth;
				minY = y;
				maxY = y + clipHeight;
			}
			g.Translate(x - this.viewX, y - this.viewY);
			for (int i = 0; i < this.size; i++) {
	
				ISprite spr = sprites[i];
				if (spr.IsVisible()) {
	
					int layerX = spr.X();
					int layerY = spr.Y();
	
					int layerWidth = spr.GetWidth();
					int layerHeight = spr.GetHeight();
	
					if (layerX + layerWidth < minX || layerX > maxX
							|| layerY + layerHeight < minY || layerY > maxY) {
						continue;
					}
	
					spr.CreateUI(g);
				}
			}
			g.Translate(-(x - this.viewX), -(y - this.viewY));
			if (this.isViewWindowSet) {
				g.ClearClip();
			}
		}
	
		/// <summary>
		/// 设定精灵集合在屏幕中的位置与大小
		/// </summary>
		///
		/// <param name="x"></param>
		/// <param name="y"></param>
		/// <param name="width"></param>
		/// <param name="height"></param>
		public void SetViewWindow(int x, int y, int width, int height) {
			this.isViewWindowSet = true;
			this.viewX = x;
			this.viewY = y;
			this.width = width;
			this.height = height;
		}
	
		/// <summary>
		/// 设定精灵集合在屏幕中的位置
		/// </summary>
		///
		/// <param name="x"></param>
		/// <param name="y"></param>
		public void SetLocation(int x, int y) {
			this.isViewWindowSet = true;
			this.viewX = x;
			this.viewY = y;
		}
	
		public ISprite[] GetSprites() {
			return this.sprites;
		}
	
		public int Size() {
			return this.size;
		}
	
		public int GetHeight() {
			return height;
		}
	
		public int GetWidth() {
			return width;
		}
	
		public bool IsVisible() {
			return visible;
		}
	
		public void SetVisible(bool visible) {
			this.visible = visible;
		}
	
		public Sprites.SpriteListener  GetSprListerner() {
			return sprListerner;
		}
	
		public void SetSprListerner(Sprites.SpriteListener  sprListerner) {
			this.sprListerner = sprListerner;
		}
	
		public virtual void Dispose() {
			this.visible = false;
			foreach (ISprite spr  in  sprites) {
				if (spr != null) {
					spr.Dispose();
				}
			}
		}

    }
}
