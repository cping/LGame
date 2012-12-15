using Loon.Core;
using Loon.Core.Input;
using System.Collections.Generic;
using Loon.Core.Geom;
using Loon.Core.Graphics;
using System.Runtime.CompilerServices;
using System;
using Loon.Utils;
using Loon.Java.Collections;
namespace Loon.Action.Sprite.Node {
	
	public class LNNode : LRelease {
	
		public object Tag;
	
		public void Down(LTouch e) {
	
		}
	
		public void Up(LTouch e) {
	
		}
	
		public void Drag(LTouch e) {
	
		}
	
		private static readonly IComparer<LNNode> DEFAULT_COMPARATOR = new LNNode.Anonymous_C0 ();
		private IComparer<LNNode> comparator = LNNode.DEFAULT_COMPARATOR;
	
		public sealed class Anonymous_C0 : IComparer<LNNode> {
			private int Match(int x, int y) {
				return (x < y) ? -1 : ((x == y) ? 0 : 1);
			}
	
			public int Compare(LNNode p1, LNNode p2) {
				if (p1 == null || p2 == null) {
					if (p1 != null) {
						return p1._zOrder;
					}
					if (p2 != null) {
						return p2._zOrder;
					}
					return 0;
				}
				if (Touch.IsDrag()) {
					return p2._zOrder - p1._zOrder;
				}
				return Match(p2._zOrder, p1._zOrder);
			}
		}
	
		public interface CallListener {
	
			void Act(float dt);
	
		}
	
		public LNClickListener Click;
	
		public void SetClick(LNClickListener c) {
			Click = c;
		}
	
		public LNClickListener GetClick() {
			return Click;
		}
	
		public LNNode.CallListener Call;
	
		public void SetCall(LNNode.CallListener  u) {
			Call = u;
		}
	
		public LNNode.CallListener  GetCall() {
			return Call;
		}
	
		protected internal bool _locked;
	
		public LNNode[] childs = new LNNode[0];
	
		protected internal int _childCount = 0;
	
		private LNNode latestInserted = null;
	
		protected internal List<LNAction> _actionList;
	
		protected internal Vector2f _anchor = new Vector2f();
	
		protected internal LColor _color;
	
		protected internal int _size_width, _size_height;
	
		protected internal int _orig_width, _orig_height;
	
		protected internal void SetWidth(int w) {
			this._size_width = w;
			if (_orig_width == 0) {
				this._orig_width = w;
			}
		}
	
		protected internal void SetHieght(int h) {
			this._size_height = h;
			if (_orig_height == 0) {
				this._orig_height = h;
			}
		}
	
		internal void SetNodeSize(int w, int h) {
			SetWidth(w);
			SetHieght(h);
		}
	
		protected internal int _top;
	
		protected internal int _left;
	
		protected internal float _alpha;
	
		protected internal LNNode _parent = null;
	
		protected internal readonly Vector2f _position = new Vector2f();
	
		protected internal float _rotation;
	
		protected internal float _rotationAlongX;
	
		protected internal float _rotationAlongY;
	
		protected internal readonly Vector2f _scale = new Vector2f(1f, 1f);
	
		protected internal bool _visible = true;
	
		protected internal int _zOrder = 0;
	
		protected internal bool _autoDestroy;
	
		protected internal bool _isClose;
	
		private int cam_x, cam_y;
	
		protected internal int _screenX, _screenY;
	
		protected internal bool _enabled = true;
	
		protected internal bool _focusable = true;
	
		protected internal bool _selected = false;
	
		protected internal bool _limitMove;
	
		protected internal SpriteBatchScreen _screen;
	
		protected internal RectBox _screenRect;
	
		protected internal LInput _input;
	
		protected internal readonly Vector2f _offset = new Vector2f();
	
		private float[] scale = new float[2];

		internal LNNode():	this(LSystem.screenRect) {
		
		}
	
		public LNNode(int x, int y, int width, int height):this(null, x, y, width, height) {
			
		}
	
		public LNNode(RectBox rect):this(null, rect.X(), rect.Y(), rect.width, rect.height) {
			
		}
	
		public LNNode(SpriteBatchScreen screen, RectBox rect):this(screen, rect.X(), rect.Y(), rect.width, rect.height) {
			
		}
	
		public LNNode(SpriteBatchScreen screen, int x, int y, int width, int height) {
			this.SetLocation(x, y);
			this._rotation = 0f;
            this.scale[0] = 1f;
            this.scale[1] = 1f;
			this._scale.x = 1f;
			this._scale.y = 1f;
			this._alpha = 1f;
			this._left = 0;
			this._top = 0;
			this._screen = screen;
			this._color = new LColor(0xff, 0xff, 0xff, 0xff);
			this._actionList = new List<LNAction>();
			this._limitMove = true;
			this._locked = true;
			this._size_width = width;
			this._size_height = height;
			this._screenRect = LSystem.screenRect;
			if (this._size_width == 0) {
				this._size_width = 10;
			}
			if (this._size_height == 0) {
				this._size_height = 10;
			}
		}

        public virtual bool IsLocked()
        {
			return _locked;
		}

        public virtual void SetLocked(bool locked)
        {
			this._locked = locked;
		}

        public virtual void AddNode(LNNode node)
        {
			this.AddNode(node, 0);
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
        public virtual void AddNode(LNNode node, int z)
        {
			if (this.Contains(node)) {
				return;
			}
			if (node.GetContainer() != null) {
				node.SetContainer(null);
			}
			node.SetContainer(this);
			int index = 0;
			bool flag = false;
			for (int i = 0; i < this._childCount; i++) {
				LNNode node2 = this.childs[i];
				int zd = 0;
				if (node2 != null) {
					zd = node2.GetZOrder();
				}
				if (zd > z) {
					flag = true;
					this.childs = (LNNode[]) CollectionUtils.Expand(this.childs, 1,
							false);
					childs[index] = node;
					_childCount++;
					node.SetScreen(_screen);
					this.latestInserted = node;
					break;
				}
				index++;
			}
			if (!flag) {
				this.childs = (LNNode[]) CollectionUtils.Expand(this.childs, 1,
						false);
				this.childs[0] = node;
				this._childCount++;
				node.SetScreen(_screen);
				this.latestInserted = node;
			}
			node.SetZOrder(z);
			node.SetParent(this);
			Arrays.Sort(childs, comparator);
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
        public virtual void Add(LNNode node, int index)
        {
			if (node.GetContainer() != null) {
				throw new InvalidOperationException(node
						+ " already reside in another node!!!");
			}
			node.SetContainer(this);
			LNNode[] newChilds = new LNNode[this.childs.Length + 1];
			this._childCount++;
			int ctr = 0;
			for (int i = 0; i < this._childCount; i++) {
				if (i != index) {
					newChilds[i] = this.childs[ctr];
					ctr++;
				}
			}
			this.childs = newChilds;
			this.childs[index] = node;
			node.SetScreen(_screen);
			this.SortComponents();
			this.latestInserted = node;
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
        public virtual bool Contains(LNNode node)
        {
			if (node == null) {
				return false;
			}
			if (childs == null) {
				return false;
			}
			for (int i = 0; i < this._childCount; i++) {
				if (childs[i] != null && node.Equals(childs[i])) {
					return true;
				}
			}
			return false;
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
        public virtual int RemoveNode(LNNode node)
        {
			for (int i = 0; i < this._childCount; i++) {
				if (this.childs[i] == node) {
                    this.RemoveNode(i);
					return i;
				}
			}
			return -1;
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
        public virtual int RemoveNode(Type clazz)
        {
			if (clazz == null) {
				return -1;
			}
			int count = 0;
			for (int i = _childCount; i > 0; i--) {
				int index = i - 1;
				LNNode node =  this.childs[index];
                Type cls = node.GetType();
                if (clazz == null || clazz == cls || clazz.IsInstanceOfType(node)
                    || clazz.Equals(cls))
                {
					this.RemoveNode(index);
					count++;
				}
			}
			return count;
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
        public virtual LNNode RemoveNode(int index)
        {
			LNNode node = this.childs[index];
			this._screen.SetNodeStat(node, false);
			node.SetContainer(null);
			this.childs = (LNNode[]) CollectionUtils.Cut(this.childs, index);
			this._childCount--;
			return node;
		}

        public virtual void Clear()
        {
			this._screen.ClearNodesStat(this.childs);
			for (int i = 0; i < this._childCount; i++) {
				this.childs[i].SetContainer(null);
			}
			this.childs = new LNNode[0];
			this._childCount = 0;
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
        public virtual void Replace(LNNode oldComp, LNNode newComp)
        {
			int index = this.RemoveNode(oldComp);
			this.Add(newComp, index);
		}

        public virtual void Close()
        {
			if (_screen != null) {
				if (_screen.RemoveNode(this) != -1) {
					this.Dispose();
				}
			}
		}
	
		public virtual void Update(float dt) {
	
		}

        public virtual void UpdateNode(float dt)
        {
			if (_isClose) {
				return;
			}
			 lock (childs) {
						if (_isClose) {
							return;
						}
						if (_parent != null) {
							ValidatePosition();
						}
						if (Call != null) {
							Call.Act(dt);
						}
						for (int i = 0; i < _actionList.Count; i++) {
							if (this._actionList[i].IsEnd()) {
								this._actionList.Remove(this._actionList[i]);
								i--;
							} else {
								this._actionList[i].Step(dt);
								if (this._actionList.Count==0) {
									break;
								}
								if (this._actionList[i].IsEnd()) {
									this._actionList.Remove(this._actionList[i]);
									i--;
								}
							}
						}
						LNNode component;
						for (int i = 0; i < this._childCount; i++) {
							component = childs[i];
							if (component != null) {
								component.UpdateNode(dt);
							}
						}
					}
			Update(dt);
		}

        protected virtual internal void ValidateSize()
        {
			for (int i = 0; i < this._childCount; i++) {
				if (this.childs[i] != null) {
					this.childs[i].ValidateSize();
				}
			}
		}

        public virtual void SendToFront(LNNode node)
        {
			if (this.childs == null) {
				return;
			}
			if (this._childCount <= 1 || this.childs[0] == node) {
				return;
			}
			if (childs[0] == node) {
				return;
			}
			for (int i = 0; i < this._childCount; i++) {
				if (this.childs[i] == node) {
					this.childs = (LNNode[]) CollectionUtils.Cut(this.childs, i);
					this.childs = (LNNode[]) CollectionUtils.Expand(this.childs, 1,
							false);
					this.childs[0] = node;
					this.SortComponents();
					break;
				}
			}
		}

        public virtual void SendToBack(LNNode node)
        {
			if (this._childCount <= 1 || this.childs[this._childCount - 1] == node) {
				return;
			}
			if (childs[this._childCount - 1] == node) {
				return;
			}
			for (int i = 0; i < this._childCount; i++) {
				if (this.childs[i] == node) {
					this.childs = (LNNode[]) CollectionUtils.Cut(this.childs, i);
					this.childs = (LNNode[]) CollectionUtils.Expand(this.childs, 1,
							true);
					this.childs[this._childCount - 1] = node;
					this.SortComponents();
					break;
				}
			}
		}

        public virtual void SortComponents()
        {
			Arrays.Sort(this.childs, this.comparator);
		}

        protected virtual internal void TransferFocus(LNNode component)
        {
			for (int i = 0; i < this._childCount; i++) {
				if (component == this.childs[i]) {
					int j = i;
					do {
						if (--i < 0) {
							i = this._childCount - 1;
						}
						if (i == j) {
							return;
						}
					} while (!this.childs[i].RequestFocus());
	
					break;
				}
			}
		}

        protected virtual internal void TransferFocusBackward(LNNode component)
        {
			for (int i = 0; i < this._childCount; i++) {
				if (component == this.childs[i]) {
					int j = i;
					do {
						if (++i >= this._childCount) {
							i = 0;
						}
						if (i == j) {
							return;
						}
					} while (!this.childs[i].RequestFocus());
	
					break;
				}
			}
		}

        public virtual IComparer<LNNode> GetComparator()
        {
			return this.comparator;
		}

        public virtual void SetComparator(IComparer<LNNode> c)
        {
			if (c == null) {
				throw new NullReferenceException("Comparator can not null !");
			}
	
			this.comparator = c;
			this.SortComponents();
		}

        public virtual LNNode FindNode(int x1, int y1)
        {
			if (!this.Intersects(x1, y1)) {
				return null;
			}
			for (int i = 0; i < this._childCount; i++) {
				if (childs[i] != null) {
					if (this.childs[i].Intersects(x1, y1)) {
						LNNode node = (IsContainer()) ? this.childs[i]
								: ( this.childs[i]).FindNode(x1, y1);
						return node;
					}
				}
			}
			return this;
		}

        public virtual int GetNodeCount()
        {
			return this._childCount;
		}

        public virtual LNNode[] GetNodes()
        {
			return this.childs;
		}

        public virtual LNNode Get()
        {
			return this.latestInserted;
		}

        public virtual void Draw(SpriteBatch batch)
        {
	
		}

        public virtual void DrawNode(SpriteBatch batch)
        {
			if (_isClose) {
				return;
			}
			if (!this._visible) {
				return;
			}
			for (int i = this._childCount - 1; i >= 0; i--) {
				if (childs[i] != null && childs[i].GetZOrder() < 0) {
					childs[i].DrawNode(batch);
				}
			}
			this.Draw(batch);
			int zOrder = 0;
			for (int i = this._childCount - 1; i >= 0; i--) {
				LNNode o = this.childs[i];
				if (o != null) {
					if (o.GetZOrder() >= 0) {
						if (zOrder == 0) {
							zOrder = o.GetZOrder();
						} else {
							zOrder = o.GetZOrder();
						}
	
						o.DrawNode(batch);
					}
				}
			}
		}

        public virtual void SetOffset(float x, float y)
        {
			this._offset.Set(x, y);
		}

        public virtual void SetOffset(Vector2f v)
        {
			this._offset.Set(v);
		}

        public virtual Vector2f GetOffset()
        {
			return this._offset;
		}
	
		private float[] pos = new float[2];
	
		public float[] ConvertToWorldPos() {
			pos[0] = _offset.x + _position.x;
			pos[1] = _offset.y + _position.y;
			if (this._parent != null) {
				float[] result = this._parent.ConvertToWorldPos();
				pos[0] += result[0];
				pos[1] += result[1];
			}
			return pos;
		}
	
	
		public float[] ConvertToWorldScale() {
			scale[0] = _scale.x;
			scale[1] = _scale.y;
			if (this._parent != null) {
				float[] result = this._parent.ConvertToWorldScale();
				scale[0] *= result[0];
				scale[1] *= result[1];
			}
			return scale;
		}
	
		public float ConvertToWorldRot() {
			float num = 0f;
			if (this._parent != null) {
				num += this._parent.ConvertToWorldRot();
			}
			return (num + this._rotation);
		}

        public virtual void OnSceneActive()
        {
		}

        public virtual void PauseAllAction()
        {
			foreach (LNAction action  in  this._actionList) {
				action.Pause();
			}
		}

        public virtual void ReorderNode(LNNode node, int NewOrder)
        {
			this.RemoveNode(node);
			this.AddNode(node, NewOrder);
		}

        public virtual void ResumeAllAction()
        {
			foreach (LNAction action  in  this._actionList) {
				action.Resume();
			}
		}

        public virtual void RemoveAction(LNAction action)
        {
			_actionList.Remove(action);
			action._target = null;
		}

        public virtual void StopAllAction()
        {
			foreach (LNAction action  in  _actionList) {
				action._isEnd = true;
			}
			this._actionList.Clear();
		}

        public virtual void RunAction(LNAction action)
        {
			this._actionList.Add(action);
			action.SetTarget(this);
		}

        public virtual void SetColor(LColor c)
        {
			this._color.SetColor(c);
		}

        public virtual void SetColor(int r, int g, int b)
        {
            this._color.SetIntColor(r, g, b, (int)(255 * this._alpha));
        }

        public virtual void SetColor(int r, int g, int b, int a)
        {
			this._color.SetColor(r, g, b, a);
		}

        public virtual void SetColor(float r, float g, float b)
        {
            this._color.SetColor(r, g, b, _alpha);
		}

        public virtual void SetColor(float r, float g, float b, float a)
        {
			this._color.SetColor(r, g, b, a);
		}

        public virtual void SetLimitMove(bool v)
        {
			this._limitMove = v;
		}

        public virtual bool IsLimitMove()
        {
			return _limitMove;
		}

        public virtual void SetPosition(float x, float y)
        {
			this._position.Set(x, y);
		}

        public virtual void SetPositionOrig(Vector2f v)
        {
			SetPositionOrig(v.x, v.y);
		}

        public virtual void SetPositionOrig(float x, float y)
        {
			this._position.Set((x + this._anchor.x) - (_screenRect.width / 2),
					(_screenRect.height / 2) - (y + this._anchor.y));
		}

        public virtual void SetPosition(Vector2f newPosition)
        {
			if (!newPosition.Equals(this._position)) {
				this.Position(newPosition);
			}
		}

        public virtual void SetPositionBL(float x, float y)
        {
			this.SetPosition(
					(x + this._anchor.x) - (_screenRect.width / 2),
					((_screenRect.height / 2) - (((_screenRect.height - y) - this._size_height) + this._anchor.y)));
		}

        public virtual void SetPositionBR(float x, float y)
        {
			this.SetPosition(
					(((_screenRect.width - x) - this._size_width) + this._anchor.x)
							- (_screenRect.width / 2),
					(_screenRect.height / 2)
							- (((_screenRect.height - y) - this._size_height) + this._anchor.y));
		}

        public virtual void SetPositionTL(float x, float y)
        {
			this.SetPosition(
					(x + this._anchor.x) - (_screenRect.width / 2),
					((_screenRect.height / 2) - (_screenRect.height - (y + this._anchor.y))));
		}

        public virtual void SetPositionTR(float x, float y)
        {
			this.SetPosition(
					((((_screenRect.width - x) - this._size_width) + this._anchor.x) - (_screenRect.width / 2)) + 240,
					((_screenRect.height / 2) - (y + this._anchor.y)) + 160);
	
		}

        public virtual Vector2f GetAnchor()
        {
			return this._anchor;
		}

        public virtual void SetAnchor(Vector2f v)
        {
			this._anchor = v;
		}

        public virtual LColor GetColor()
        {
			return _color;
		}

        public virtual float GetAlpha()
        {
			return _alpha;
		}

        public virtual float GetOpacity()
        {
			return _alpha * 255;
		}

        public virtual void SetOpacity(float o)
        {
			this._alpha = o / 255f;
		}
	
		public virtual void SetAlpha(float v) {
			this._alpha = v;
			this._color.a = this._alpha;
		}

        public virtual LNNode GetParent()
        {
			return this._parent;
		}

        public virtual void SetParent(LNNode v)
        {
			this._parent = v;
		}

        public virtual Vector2f GetPosition()
        {
			return this._position;
		}

        public virtual void Position(Vector2f v)
        {
			this._position.Set(v);
		}

        public virtual float GetRotation()
        {
			return MathUtils.ToDegrees(this._rotation);
		}

        public virtual void SetRotation(float v)
        {
			this._rotation = MathUtils.ToRadians(v);
		}

        public virtual Vector2f GetScale()
        {
			return this._scale;
		}

        public virtual void SetScale(Vector2f v)
        {
			this._scale.Set(v);
		}

        public virtual void SetScale(float x, float y)
        {
			this._scale.Set(x, y);
		}

        public virtual float GetScaleX()
        {
			return this._scale.x;
		}

        public virtual void SetScaleX(float value_ren)
        {
			this._scale.x = value_ren;
		}

        public virtual float GetScaleY()
        {
			return this._scale.y;
		}

        public virtual void SetScaleY(float value_ren)
        {
			this._scale.y = value_ren;
		}

        public virtual int GetZOrder()
        {
			return this._zOrder;
		}

        public virtual void SetZOrder(int value_ren)
        {
			this._zOrder = value_ren;
		}

        public virtual int GetScreenWidth()
        {
			return _screenRect.width;
		}

        public virtual int GetScreenHeight()
        {
			return _screenRect.height;
		}

        public virtual int GetWidth()
        {
			return (int) (_size_width * scale[0]);
		}

        public virtual int GetHeight()
        {
			return (int) (_size_height * scale[1]);
		}

        public virtual void MoveCamera(int x, int y)
        {
			if (!this._limitMove) {
				SetLocation(x, y);
				return;
			}
			int tempX = x;
			int tempY = y;
			int tempWidth = (GetWidth() - _screenRect.width);
			int tempHeight = (GetHeight() - _screenRect.height);
	
			int limitX = tempX + tempWidth;
			int limitY = tempY + tempHeight;
	
			if (_size_width >= _screenRect.width) {
				if (limitX > tempWidth) {
					tempX = (int) (_screenRect.width - _size_width);
				} else if (limitX < 1) {
					tempX = _position.X();
				}
			} else {
				return;
			}
			if (_size_height >= _screenRect.height) {
				if (limitY > tempHeight) {
					tempY = (int) (_screenRect.height - _size_height);
				} else if (limitY < 1) {
					tempY = _position.Y();
				}
			} else {
				return;
			}
			this.cam_x = tempX;
			this.cam_y = tempY;
			this.SetLocation(cam_x, cam_y);
		}
	
		protected virtual internal bool IsNotMoveInScreen(int x, int y) {
			if (!this._limitMove) {
				return false;
			}
			int width = (GetWidth() - _screenRect.width);
			int height = (GetHeight() - _screenRect.height);
			int limitX = x + width;
			int limitY = y + height;
			if (GetWidth() >= _screenRect.width) {
				if (limitX >= width - 1) {
					return true;
				} else if (limitX <= 1) {
					return true;
				}
			} else {
				if (!_screenRect.Contains(x, y, GetWidth(), GetHeight())) {
					return true;
				}
			}
			if (GetHeight() >= _screenRect.height) {
				if (limitY >= height - 1) {
					return true;
				} else if (limitY <= 1) {
					return true;
				}
			} else {
				if (!_screenRect.Contains(x, y, GetWidth(), GetHeight())) {
					return true;
				}
			}
			return false;
		}

        public virtual bool IsContainer()
        {
			return true;
		}

        public virtual bool Contains(int x, int y)
        {
			return Contains(x, y, 0, 0);
		}

        public virtual bool Contains(int x, int y, int width, int height)
        {
			return (this._visible)
					&& (x >= pos[0] && y >= pos[1]
							&& ((x + width) <= (pos[0] + GetWidth())) && ((y + height) <= (pos[1] + GetHeight())));
		}

        public virtual bool Intersects(int x1, int y1)
        {
			return (this._visible)
					&& (x1 >= pos[0] && x1 <= pos[0] + GetWidth() && y1 >= pos[1] && y1 <= pos[1]
							+ GetHeight());
		}

        public virtual bool Intersects(LNNode node)
        {
			float[] nodePos = node.ConvertToWorldPos();
			return (this._visible)
					&& (node._visible)
					&& (pos[0] + GetWidth() >= nodePos[0]
							&& pos[0] <= nodePos[0] + node.GetWidth()
							&& pos[1] + GetWidth() >= nodePos[1] && pos[1] <= nodePos[1]
							+ node.GetHeight());
		}

        public virtual bool IsVisible()
        {
			return this._visible;
		}

        public virtual void SetVisible(bool visible)
        {
			if (this._visible == visible) {
				return;
			}
			this._visible = visible;
			if (_screen != null) {
				this._screen.SetNodeStat(this, this._visible);
			}
		}

        public virtual bool IsEnabled()
        {
			return (this._parent == null) ? this._enabled
					: (this._enabled && this._parent.IsEnabled());
		}

        public virtual void SetEnabled(bool b)
        {
			if (this._enabled == b) {
				return;
			}
			this._enabled = b;
			this._screen.SetNodeStat(this, this._enabled);
		}

        public virtual bool IsSelected()
        {
			if (!_selected) {
				for (int i = 0; i < this._childCount; i++) {
					if (this.childs[i].IsSelected()) {
						return true;
					}
				}
				return false;
	
			} else {
				return true;
			}
		}

        public virtual void SetSelected(bool b)
        {
			this._selected = b;
		}

        public virtual bool RequestFocus()
        {
			return this._screen.SelectNode(this);
		}

        public virtual void TransferFocus()
        {
			if (this.IsSelected() && this._parent != null) {
				this._parent.TransferFocus(this);
			}
		}

        public virtual void TransferFocusBackward()
        {
			if (this.IsSelected() && this._parent != null) {
				this._parent.TransferFocusBackward(this);
			}
		}

        public virtual bool IsFocusable()
        {
			return this._focusable;
		}

        public virtual void SetFocusable(bool b)
        {
			this._focusable = b;
		}

        public virtual LNNode GetContainer()
        {
			return this._parent;
		}
	
		internal void SetContainer(LNNode node) {
			this._parent = node;
			this.ValidatePosition();
		}

        public virtual void SetScreen(SpriteBatchScreen s)
        {
			if (s == _screen) {
				return;
			}
			this._screen = s;
			this._input = s.GetInput();
		}

        public virtual void SetBounds(float dx, float dy, int width, int height)
        {
			SetLocation(dx, dy);
			if (this._size_width != width || this._size_height != height) {
				this._size_width = width;
				this._size_height = height;
				if (width == 0) {
					width = 1;
				}
				if (height == 0) {
					height = 1;
				}
				this.ValidateSize();
			}
		}

        public virtual int GetX()
        {
			return _position.X();
		}

        public virtual int GetY()
        {
			return _position.Y();
		}

        public virtual void SetX(Int32 x)
        {
			if (this._position.x != x || x == 0) {
				this._position.x = x;
				this.ValidatePosition();
			}
		}

        public virtual void SetX(float x)
        {
			if (this._position.x != x || x == 0) {
				this._position.x = x;
				this.ValidatePosition();
			}
		}

        public virtual void SetY(Int32 y)
        {
			if (this._position.y != y || y == 0) {
				this._position.y = y;
				this.ValidatePosition();
			}
		}

        public virtual void SetY(float y)
        {
			if (this._position.y != y || y == 0) {
				this._position.y = y;
				this.ValidatePosition();
			}
		}

        public virtual void SetLocation(Vector2f location)
        {
			SetLocation(location.x, location.y);
		}

        public virtual void SetLocation(float dx, float dy)
        {
			if (this._position.x != dx || this._position.y != dy || dx == 0
					|| dy == 0) {
				this._position.Set(dx, dy);
				this.ValidatePosition();
			}
		}

        public virtual void Move(float dx, float dy)
        {
			if (dx != 0 || dy != 0) {
					if (_parent != null && _limitMove) {
						if (_parent.Contains((int) (pos[0] + dx),
								(int) (pos[1] + dy), _size_width, _size_height)) {
							this._position.Move(dx, dy);
							this.ValidatePosition();
						}
					} else {
						this._position.Move(dx, dy);
						this.ValidatePosition();
					}
			}
		}

        public virtual void SetSize(int w, int h)
        {
			if (this._size_width != w || this._size_height != h) {
				this._size_width = w;
				this._size_height = h;
				if (this._size_width == 0) {
					this._size_width = 1;
				}
				if (this._size_height == 0) {
					this._size_height = 1;
				}
				this.ValidateSize();
			}
		}

        public virtual void ValidatePosition()
        {
			if (_isClose) {
				return;
			}
			if (_parent != null) {
				this._screenX = (int) pos[0];
				this._screenY = (int) pos[1];
			} else {
				this._screenX = _position.X();
				this._screenY = _position.Y();
			}
			for (int i = 0; i < this._childCount; i++) {
				if (this.childs[i] != null) {
					this.childs[i].ValidatePosition();
				}
			}
		}
	
		private RectBox temp_rect;

        public virtual RectBox GetRectBox()
        {
			if (_rotation != 0) {
				int[] result = MathUtils.GetLimit(_position.GetX(),
						_position.GetY(), GetWidth(), GetHeight(),
						MathUtils.ToDegrees(_rotation));
				if (temp_rect == null) {
					temp_rect = new RectBox(result[0], result[1], result[2],
							result[3]);
				} else {
					temp_rect.SetBounds(result[0], result[1], result[2], result[3]);
				}
			} else {
				if (temp_rect == null) {
					temp_rect = new RectBox(_position.GetX(), _position.GetY(),
							GetWidth(), GetHeight());
				} else {
					temp_rect.SetBounds(_position.GetX(), _position.GetY(),
							GetWidth(), GetHeight());
				}
			}
			return temp_rect;
		}

        public virtual int GetScreenX()
        {
			return this._screenX;
		}

        public virtual int GetScreenY()
        {
			return this._screenY;
		}
	
		public virtual void ProcessTouchPressed() {
			if (!_visible || !_enabled) {
				return;
			}
			if (Click != null) {
				Click.DownClick(this, Touch.GetX(), Touch.GetY());
			}
		}
	
		public virtual void ProcessTouchReleased() {
			if (!_visible || !_enabled) {
				return;
			}
			if (Click != null) {
				Click.UpClick(this, Touch.GetX(), Touch.GetY());
			}
		}
	
		public virtual void ProcessTouchDragged() {
			if (!_visible || !_enabled) {
				return;
			}
			if (!_locked && _input != null) {
				if (GetContainer() != null) {
					GetContainer().SendToFront(this);
				}
				this.Move(this._input.GetTouchDX(), this._input.GetTouchDY());
			}
			if (Click != null) {
				Click.DragClick(this, Touch.GetX(), Touch.GetY());
			}
		}
	
		public virtual void ProcessKeyPressed() {
	
		}

        public virtual void ProcessKeyReleased()
        {
		}

        public virtual void KeyPressed()
        {
			this.CheckFocusKey();
			this.ProcessKeyPressed();
		}
	
		virtual internal void CheckFocusKey() {
			if (_input != null && this._input.GetKeyPressed() == Key.ENTER) {
				this.TransferFocus();
			} else {
				this.TransferFocusBackward();
			}
		}

        public virtual int GetCamX()
        {
			return (cam_x == 0) ? _position.X() : cam_x;
		}

        public virtual int GetCamY()
        {
			return (cam_y == 0) ? _position.Y() : cam_y;
		}

        public virtual bool IsClose()
        {
			return _isClose;
		}

        public virtual void SetAutoDestroy(bool flag)
        {
			this._autoDestroy = flag;
		}

        public virtual bool IsAutoDestory()
        {
			return _autoDestroy;
		}
	
		public void Dispose() {
			this._isClose = true;
			if (this._parent != null) {
				this._parent.RemoveNode(this);
			}
			this._selected = false;
			this._visible = false;
			if (_screen != null) {
				this._screen.SetNodeStat(this, false);
			}
			if (_autoDestroy) {
				if (childs != null) {
					foreach (LNNode c  in  childs) {
						if (c != null) {
							c.Dispose();
						}
					}
				}
			}
		}
	
	}
}
