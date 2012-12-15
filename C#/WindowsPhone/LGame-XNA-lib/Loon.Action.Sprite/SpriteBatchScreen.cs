using Loon.Core.Graphics;
using Loon.Action.Map;
using Loon.Utils;
using Loon.Utils.Collection;
using System.Collections.Generic;
using Loon.Core.Geom;
using Loon.Core;
using Loon.Action.Sprite.Node;
using System.Runtime.CompilerServices;
using System.IO;
using System;
using Loon.Core.Input;
using Loon.Core.Event;
using Loon.Core.Timer;
using Loon.Core.Graphics.Opengl;

namespace Loon.Action.Sprite
{

    public abstract class SpriteBatchScreen : Screen
    {

        private int keySize = 0;

        private float objX = 0, objY = 0;

        private ArrayMap keyActions = new ArrayMap(CollectionUtils.INITIAL_CAPACITY);

        private SpriteBatch batch;

        private List<SpriteBatchObject> objects;

        private List<SpriteBatchObject> pendingAdd;

        private List<SpriteBatchObject> pendingRemove;

        private List<TileMap> tiles = new List<TileMap>(10);

        private Vector2f offset = new Vector2f();

        private LObject follow;

        private TileMap indexTile;

        private LNNode content;

        private LNNode modal;

        private LNNode hoverNode;

        private LNNode selectedNode;

        private LNNode[] clickNode = new LNNode[1];

        private bool isClicked;

        protected internal UpdateListener updateListener;

        public void SetUpdateListener(UpdateListener u)
        {
            this.updateListener = u;
        }

        public interface UpdateListener
        {

            void Act(SpriteBatchObject obj, long elapsedTime);

        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void LoadNodeDef(string resName)
        {
            DefinitionReader.Get().Load(resName);
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void LoadNodeDef(Stream res)
        {
            DefinitionReader.Get().Load(res);
        }

        public SpriteBatchScreen()
            : base()
        {
            this.objects = new List<SpriteBatchObject>(10);
            this.pendingAdd = new List<SpriteBatchObject>(10);
            this.pendingRemove = new List<SpriteBatchObject>(10);
            this.Init();
        }

        public virtual SpriteBatch GetSpriteBatch()
        {
            return batch;
        }

        private void Init()
        {
            SetNode(new LNNode(this, LSystem.screenRect));
        }

        public virtual void SetNode(LNNode node)
        {
            if (content == node)
            {
                return;
            }
            this.content = node;
        }

        public virtual LNNode Node()
        {
            return content;
        }

        public virtual int Size()
        {
            return (content == null) ? 0 : content.GetNodeCount();
        }

        public virtual void RunAction(LNAction action)
        {
            if (content != null)
            {
                content.RunAction(action);
            }
        }

        public virtual void AddNode(LNNode node)
        {
            AddNode(node, 0);
        }

        public virtual void Add(LNNode node)
        {
            AddNode(node, 0);
        }

        public virtual void AddNode(LNNode node, int z)
        {
            if (node == null)
            {
                return;
            }
            this.content.AddNode(node, z);
            this.ProcessTouchMotionEvent();
        }

        public virtual int RemoveNode(LNNode node)
        {
            int removed = this.RemoveNode(this.content, node);
            if (removed != -1)
            {
                this.ProcessTouchMotionEvent();
            }
            return removed;
        }

        public virtual int RemoveNode(Type clazz)
        {
            int removed = this.RemoveNode(this.content, clazz);
            if (removed != -1)
            {
                this.ProcessTouchMotionEvent();
            }
            return removed;
        }

        private int RemoveNode(LNNode container, LNNode node)
        {
            int removed = container.RemoveNode(node);
            LNNode[] nodes = container.childs;
            int i = 0;
            while (removed == -1 && i < nodes.Length - 1)
            {
                if (nodes[i].IsContainer())
                {
                    removed = this.RemoveNode(nodes[i], node);
                }
                i++;
            }

            return removed;
        }

        private int RemoveNode(LNNode container, Type clazz)
        {
            int removed = container.RemoveNode(clazz);
            LNNode[] nodes = container.childs;
            int i = 0;
            while (removed == -1 && i < nodes.Length - 1)
            {
                if (nodes[i].IsContainer())
                {
                    removed = this.RemoveNode(nodes[i], clazz);
                }
                i++;
            }
            return removed;
        }

        private void ProcessEvents()
        {
            this.ProcessTouchMotionEvent();
            if (this.hoverNode != null && this.hoverNode.IsEnabled())
            {
                this.ProcessTouchEvent();
            }
            if (this.selectedNode != null && this.selectedNode.IsEnabled())
            {
                this.ProcessKeyEvent();
            }
        }

        private void ProcessTouchMotionEvent()
        {
            if (this.hoverNode != null && this.hoverNode.IsEnabled()
                    && this.GetInput().IsMoving())
            {
                if (GetTouchDY() != 0 || GetTouchDY() != 0)
                {
                    this.hoverNode.ProcessTouchDragged();
                }
            }
            else
            {
                if (Touch.IsDrag() || Touch.IsMove() || Touch.IsDown())
                {
                    LNNode node = this.FindNode(GetTouchX(), GetTouchY());
                    if (node != null)
                    {
                        this.hoverNode = node;
                    }
                }
            }
        }

        private void ProcessTouchEvent()
        {
            int pressed = GetTouchPressed(), released = GetTouchReleased();
            if (pressed > NO_BUTTON)
            {
                if (!isClicked)
                {
                    this.hoverNode.ProcessTouchPressed();
                }
                this.clickNode[0] = this.hoverNode;
                if (this.hoverNode.IsFocusable())
                {
                    if ((pressed == Touch.TOUCH_DOWN || pressed == Touch.TOUCH_UP)
                            && this.hoverNode != this.selectedNode)
                    {
                        this.SelectNode(this.hoverNode);
                    }
                }
            }
            if (released > NO_BUTTON)
            {
                if (!isClicked)
                {
                    this.hoverNode.ProcessTouchReleased();
                }
            }
            this.isClicked = false;
        }

        private void ProcessKeyEvent()
        {
            if (GetKeyPressed() != NO_KEY)
            {
                this.selectedNode.KeyPressed();
            }
            if (GetKeyReleased() != NO_KEY && this.selectedNode != null)
            {
                this.selectedNode.ProcessKeyReleased();
            }
        }

        public virtual LNNode FindNode(int x, int y)
        {
            if (content == null)
            {
                return null;
            }
            if (this.modal != null && !this.modal.IsContainer())
            {
                return content.FindNode(x, y);
            }
            LNNode panel = (this.modal == null) ? this.content
                    : (this.modal);
            LNNode node = panel.FindNode(x, y);
            return node;
        }

        public virtual void ClearFocus()
        {
            this.DeselectNode();
        }

        internal void DeselectNode()
        {
            if (this.selectedNode == null)
            {
                return;
            }
            this.selectedNode.SetSelected(false);
            this.selectedNode = null;
        }

        public virtual bool SelectNode(LNNode node)
        {
            if (!node.IsVisible() || !node.IsEnabled() || !node.IsFocusable())
            {
                return false;
            }
            this.DeselectNode();
            node.SetSelected(true);
            this.selectedNode = node;
            return true;
        }

        public virtual void SetNodeStat(LNNode node, bool active)
        {
            if (!active)
            {
                if (this.hoverNode == node)
                {
                    this.ProcessTouchMotionEvent();
                }
                if (this.selectedNode == node)
                {
                    this.DeselectNode();
                }
                this.clickNode[0] = null;
                if (this.modal == node)
                {
                    this.modal = null;
                }
            }
            else
            {
                this.ProcessTouchMotionEvent();
            }
            if (node == null)
            {
                return;
            }
            if (node.IsContainer())
            {
                LNNode[] nodes = (node).childs;
                int size = (node).GetNodeCount();
                for (int i = 0; i < size; i++)
                {
                    this.SetNodeStat(nodes[i], active);
                }
            }
        }

        public virtual void ClearNodesStat(LNNode[] node)
        {
            bool checkTouchMotion = false;
            for (int i = 0; i < node.Length; i++)
            {
                if (this.hoverNode == node[i])
                {
                    checkTouchMotion = true;
                }

                if (this.selectedNode == node[i])
                {
                    this.DeselectNode();
                }

                this.clickNode[0] = null;

            }

            if (checkTouchMotion)
            {
                this.ProcessTouchMotionEvent();
            }
        }

        internal void ValidateContainer(LNNode container)
        {
            if (content == null)
            {
                return;
            }
            LNNode[] nodes = container.childs;
            int size = container.GetNodeCount();
            for (int i = 0; i < size; i++)
            {
                if (nodes[i].IsContainer())
                {
                    this.ValidateContainer(nodes[i]);
                }
            }
        }

        public virtual List<LNNode> GetNodes(Type clazz)
        {
            if (content == null)
            {
                return null;
            }
            if (clazz == null)
            {
                return null;
            }
            LNNode[] nodes = content.childs;
            int size = nodes.Length;
            List<LNNode> l = new List<LNNode>(size);
            for (int i = size; i > 0; i--)
            {
                LNNode node = nodes[i - 1];
                Type cls = node.GetType();
                if (clazz == null || clazz == cls || clazz.IsInstanceOfType(node)
                        || clazz.Equals(cls))
                {
                    l.Add(node);
                }
            }
            return l;
        }

        public virtual LNNode GetTopNode()
        {
            if (content == null)
            {
                return null;
            }
            LNNode[] nodes = content.childs;
            int size = nodes.Length;
            if (size > 1)
            {
                return nodes[1];
            }
            return null;
        }

        public virtual LNNode GetBottomNode()
        {
            if (content == null)
            {
                return null;
            }
            LNNode[] nodes = content.childs;
            int size = nodes.Length;
            if (size > 0)
            {
                return nodes[size - 1];
            }
            return null;
        }

        public virtual void SetSize(int w, int h)
        {
            if (content != null)
            {
                this.content.SetSize(w, h);
            }
        }

        public virtual LNNode GetHoverNode()
        {
            return this.hoverNode;
        }

        public virtual LNNode GetSelectedNode()
        {
            return this.selectedNode;
        }

        public virtual LNNode GetModal()
        {
            return this.modal;
        }

        public virtual void SetModal(LNNode node)
        {
            if (node != null && !node.IsVisible())
            {
                throw new Exception(
                        "Can't set invisible node as modal node!");
            }
            this.modal = node;
        }

        public virtual LNNode Get()
        {
            if (content != null)
            {
                return content.Get();
            }
            return null;
        }

        public virtual void Commits()
        {
            if (IsClose())
            {
                return;
            }
            int additionCount = pendingAdd.Count;
            if (additionCount > 0)
            {
                for (int i = 0; i < additionCount; i++)
                {
                    SpriteBatchObject obj = pendingAdd[i];
                    objects.Add(obj);
                }
                pendingAdd.Clear();
            }
            int removalCount = pendingRemove.Count;
            if (removalCount > 0)
            {
                for (int i = 0; i < removalCount; i++)
                {
                    SpriteBatchObject obj = pendingRemove[i];
                    CollectionUtils.Remove(objects, obj);
                }
                pendingRemove.Clear();
            }
        }

        public virtual void Add(SpriteBatchObject obj0)
        {
            pendingAdd.Add(obj0);
        }

        public virtual void Remove(SpriteBatchObject obj0)
        {
            pendingRemove.Add(obj0);
        }

        public virtual void RemoveTileObjects()
        {
            int count = objects.Count;
            SpriteBatchObject[] objectArray = objects.ToArray();
            for (int i = 0; i < count; i++)
            {
                pendingRemove.Add(objectArray[i]);
            }
            pendingAdd.Clear();
        }

        public virtual SpriteBatchObject FindObject(float x, float y)
        {
            foreach (SpriteBatchObject o in objects)
            {
                if (o.GetX() == x && o.GetY() == y)
                {
                    return o;
                }
            }
            return null;
        }

        public virtual TileMap GetIndexTile()
        {
            return indexTile;
        }

        public virtual void SetIndexTile(TileMap indexTile)
        {
            this.indexTile = indexTile;
        }

        public virtual void Follow(LObject o)
        {
            this.follow = o;
        }

        public override void OnLoad()
        {
            if (batch == null)
            {
                batch = new SpriteBatch(1024);
            }
            content.SetScreen(this);
            foreach (LNNode node in content.childs)
            {
                if (node != null)
                {
                    node.OnSceneActive();
                }
            }
        }

        public override void OnLoaded()
        {
            Create();
        }

        public abstract void Create();

        public virtual void AddActionKey(Int32 keyCode, ActionKey e)
        {
            keyActions.Put(keyCode, e);
            keySize = keyActions.Size();
        }

        public virtual void RemoveActionKey(Int32 keyCode)
        {
            keyActions.Remove(keyCode);
            keySize = keyActions.Size();
        }

        public virtual void PressActionKey(Int32 keyCode)
        {
            ActionKey key = (ActionKey)keyActions.GetValue(keyCode);
            if (key != null)
            {
                key.Press();
            }
        }

        public virtual void ReleaseActionKey(Int32 keyCode)
        {
            ActionKey key = (ActionKey)keyActions.GetValue(keyCode);
            if (key != null)
            {
                key.Release();
            }
        }

        public virtual void ClearActionKey()
        {
            keyActions.Clear();
            keySize = 0;
        }

        public virtual void ReleaseActionKeys()
        {
            keySize = keyActions.Size();
            if (keySize > 0)
            {
                for (int i = 0; i < keySize; i++)
                {
                    ActionKey act = (ActionKey)keyActions.Get(i);
                    act.Release();
                }
            }
        }

        public virtual void SetOffset(TileMap tile, float sx, float sy)
        {
            offset.Set(sx, sy);
            tile.SetOffset(offset);
        }

        public virtual Vector2f GetOffset()
        {
            return offset;
        }

        public virtual void putTileMap(TileMap t)
        {
            tiles.Add(t);
        }

        public virtual void RemoveTileMap(TileMap t)
        {
            tiles.Remove(t);
        }

        public virtual void AddTileObject(SpriteBatchObject o)
        {
            Add(o);
        }

        public virtual JumpObject AddJumpObject(float x, float y, float w, float h,
                Animation a)
        {
            JumpObject o = null;
            if (indexTile != null)
            {
                o = new JumpObject(x, y, w, h, a, indexTile);
            }
            else if (tiles.Count > 0)
            {
                o = new JumpObject(x, y, w, h, a, tiles[0]);
            }
            else
            {
                return null;
            }
            Add(o);
            return o;
        }

        public virtual MoveObject AddMoveObject(float x, float y, float w, float h,
                Animation a)
        {
            MoveObject o = null;
            if (indexTile != null)
            {
                o = new MoveObject(x, y, w, h, a, indexTile);
            }
            else if (tiles.Count > 0)
            {
                o = new MoveObject(x, y, w, h, a, tiles[0]);
            }
            else
            {
                return null;
            }
            Add(o);
            return o;
        }

        public virtual void RemoveTileObject(SpriteBatchObject o)
        {
            Remove(o);
        }

        public override void Alter(LTimerContext timer)
        {
            for (int i = 0; i < keySize; i++)
            {
                ActionKey act = (ActionKey)keyActions.Get(i);
                if (act.IsPressed())
                {
                    act.Act(elapsedTime);
                    if (act.isReturn)
                    {
                        return;
                    }
                }
            }
            if (content.IsVisible())
            {
                ProcessEvents();
                content.UpdateNode(timer.GetMilliseconds());
            }
            if (follow != null)
            {
                foreach (TileMap tile in tiles)
                {
                    float offsetX = GetHalfWidth() - follow.GetX();
                    offsetX = MathUtils.Min(offsetX, 0);
                    offsetX = MathUtils.Max(offsetX, GetWidth() - tile.GetWidth());

                    float offsetY = GetHalfHeight() - follow.GetY();
                    offsetY = MathUtils.Min(offsetY, 0);
                    offsetY = MathUtils
                            .Max(offsetY, GetHeight() - tile.GetHeight());

                    SetOffset(tile, offsetX, offsetY);
                    tile.Update(elapsedTime);
                }
            }
            foreach (SpriteBatchObject o in objects)
            {
                o.Update(elapsedTime);
                if (updateListener != null)
                {
                    updateListener.Act(o, elapsedTime);
                }
            }
            Update(elapsedTime);
            Commits();
        }

        public override void Draw(GLEx g)
        {
            if (IsOnLoadComplete())
            {
                batch.Begin();
                Before(batch);
                foreach (TileMap tile in tiles)
                {
                    tile.Draw(g, batch, offset.X(), offset.Y());
                }
                foreach (SpriteBatchObject o in objects)
                {
                    objX = o.GetX() + offset.x;
                    objY = o.GetY() + offset.y;
                    if (Contains(objX, objY))
                    {
                        o.Draw(batch, offset.x, offset.y);
                    }
                }
                if (content.IsVisible())
                {
                    content.DrawNode(batch);
                }
                After(batch);
                batch.End();
            }
        }

        public abstract void After(SpriteBatch batch);

        public abstract void Before(SpriteBatch batch);

        public override void OnKeyDown(LKey e)
        {
            keySize = keyActions.Size();
            if (keySize > 0)
            {
                int keyCode = e.GetKeyCode();
                for (int i = 0; i < keySize; i++)
                {
                    Int32 code = (Int32)keyActions.GetKey(i);
                    if (code == keyCode)
                    {
                        ActionKey act = (ActionKey)keyActions.GetValue(code);
                        act.Press();
                    }
                }
            }
            Press(e);
        }

        public abstract void Press(LKey e);

        public override void OnKeyUp(LKey e)
        {
            keySize = keyActions.Size();
            if (keySize > 0)
            {
                int keyCode = e.GetKeyCode();
                for (int i = 0; i < keySize; i++)
                {
                    Int32 code = (Int32)keyActions.GetKey(i);
                    if (code == keyCode)
                    {
                        ActionKey act = (ActionKey)keyActions.GetValue(code);
                        act.Release();
                    }
                }
            }
            Release(e);
        }

        public abstract void Release(LKey e);

        public abstract void Update(long elapsedTime);

        public abstract void Close();

        public override void SetAutoDestory(bool a)
        {
            base.SetAutoDestory(a);
            if (content != null)
            {
                content.SetAutoDestroy(a);
            }
        }

        public override bool IsAutoDestory()
        {
            if (content != null)
            {
                return content.IsAutoDestory();
            }
            return base.IsAutoDestory();
        }

        public override void Dispose()
        {
            this.keySize = 0;
            if (batch != null)
            {
                batch.Dispose();
                batch = null;
            }
            if (content != null)
            {
                content.Dispose();
                content = null;
            }
            if (indexTile != null)
            {
                indexTile.Dispose();
                indexTile = null;
            }
            if (objects != null)
            {
                objects.Clear();
                objects = null;
            }
            if (pendingAdd != null)
            {
                pendingAdd.Clear();
                pendingAdd = null;
            }
            if (pendingRemove != null)
            {
                pendingRemove.Clear();
                pendingRemove = null;
            }
            tiles.Clear();
            Close();
        }

    }
}
