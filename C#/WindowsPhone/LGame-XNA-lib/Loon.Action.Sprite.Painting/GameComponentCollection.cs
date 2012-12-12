using System.Collections.Generic;
using Loon.Utils;
using Loon.Java.Collections;
using Loon.Core.Timer;
namespace Loon.Action.Sprite.Painting {
	
	public sealed class GameComponentCollection {

        public sealed class GameComponent_0 : IComparer<IGameComponent>
        {
			public int Compare(IGameComponent one, IGameComponent two) {
				if (one  is  DrawableGameComponent
						&& two  is  DrawableGameComponent) {
					return ((DrawableGameComponent) one).GetDrawOrder()
							- ((DrawableGameComponent) two).GetDrawOrder();
				}
				return 0;
			}
		}

        public sealed class GameComponent_1 : IComparer<IGameComponent>
        {
			public int Compare(IGameComponent one, IGameComponent two) {
				if (one  is  GameComponent && two  is  GameComponent) {
					return ((GameComponent) one).GetUpdateOrder()
							- ((GameComponent) two).GetUpdateOrder();
				}
				return 0;
			}
		}
	
		private List<IGameComponent> collections;

        private List<IGameComponent> collectionsToUpdate;

        private List<IGameComponent> collectionsToDraw;

        private IComparer<IGameComponent> igameDrawComparator = new GameComponentCollection.GameComponent_0();

        private IComparer<IGameComponent> igameUpdateComparator = new GameComponentCollection.GameComponent_1();
	
		public GameComponentCollection() {
			this.collections = new List<IGameComponent>();
			this.collectionsToUpdate = new List<IGameComponent>();
			this.collectionsToDraw = new List<IGameComponent>();
		}
	
		public int Size() {
			return collections.Count;
		}
	
		public void SortDraw() {
			 Arrays.Sort(collections,igameDrawComparator);
		}
	
		public void SortUpdate() {
            Arrays.Sort(collections, igameUpdateComparator);
		}
	
		public List<IGameComponent> List() {
			return new List<IGameComponent>(collections);
		}
	
		public IGameComponent Get(int idx) {
			return collections[idx];
		}
	
		internal void Draw(SpriteBatch batch, GameTime gameTime) {
			if (isClear) {
				return;
			}
			if (collectionsToDraw.Count > 0) {
				CollectionUtils.Clear(collectionsToDraw);
			}
			foreach (IGameComponent drawable  in  collections) {
				CollectionUtils.Add(collectionsToDraw,drawable);
			}
			foreach (IGameComponent drawable  in  collectionsToDraw) {
				if (drawable  is  IDrawable) {
					IDrawable comp = (IDrawable) drawable;
					comp.Draw(batch, gameTime);
				}
			}
		}
	
		internal void Load() {
			isClear = false;
			foreach (IGameComponent comp  in  collections) {
				comp.Initialize();
			}
		}
	
		internal void Update(GameTime gameTime) {
			if (isClear) {
				return;
			}
			if (collectionsToUpdate.Count > 0) {
				CollectionUtils.Clear(collectionsToUpdate);
			}
			foreach (IGameComponent drawable  in  collections) {
				CollectionUtils.Add(collectionsToUpdate,drawable);
			}
            IGameComponent _drawable;
			int screenIndex;
			for (; collectionsToUpdate.Count > 0;) {
	
				screenIndex = collectionsToUpdate.Count - 1;
                _drawable = collectionsToUpdate[screenIndex];
	
				CollectionUtils.RemoveAt(collectionsToUpdate,screenIndex);

                if (_drawable is IUpdateable)
                {
                    IUpdateable comp = (IUpdateable)_drawable;
					comp.Update(gameTime);
				}
			}
	
		}
	
		private bool isClear;
	
		public void Clear() {
			if (!isClear) {
				 lock (typeof(GameComponentCollection)) {
								CollectionUtils.Clear(collections);
								CollectionUtils.Clear(collectionsToUpdate);
								CollectionUtils.Clear(collectionsToDraw);
								isClear = true;
							}
			}
		}
	
		public bool Add(IGameComponent gc) {
			if (isClear) {
				return false;
			}
			gc.Initialize();
			bool result = CollectionUtils.Add(collections,gc);
			if (gc != null && Added != null) {
				Added.Invoke(gc);
			}
			if (gc  is  DrawableGameComponent) {
				if (((DrawableGameComponent) gc).GetDrawOrder() != 0) {
					SortDraw();
				}
			}
			return result;
		}
	
		public bool Add(IGameComponent gc, int index) {
			if (isClear) {
				return false;
			}
			gc.Initialize();
			bool result = CollectionUtils.Add(collections,gc);
			for (int i = 0; i < collections.Count; i++) {
				if (collections[i]   is  DrawableGameComponent) {
					if (i == index) {
						((DrawableGameComponent) collections[i])
								.SetEnabled(true);
					} else {
						((DrawableGameComponent) collections[i])
								.SetEnabled(false);
					}
				}
			}
			if (gc != null && Added != null) {
				Added.Invoke(gc);
			}
			if (gc  is  DrawableGameComponent) {
				if (((DrawableGameComponent) gc).GetDrawOrder() != 0) {
					SortDraw();
				}
			}
			return result;
		}
	
		public bool Remove(IGameComponent gc) {
			if (isClear) {
				return false;
			}
			bool result = false;
			if (gc != null && gc  is  DrawableGameComponent) {
				DrawableGameComponent comp = (DrawableGameComponent) gc;
				comp.UnloadContent();
				result = CollectionUtils.Remove(collections,gc);
				CollectionUtils.Remove(collectionsToUpdate,gc);
				if (Removed != null) {
					Removed.Invoke(gc);
				}
			}
			return result;
		}
	
		public bool RemoveAt(int idx) {
			if (isClear) {
				return false;
			}
            IGameComponent comp = (IGameComponent)CollectionUtils.RemoveAt(collections, idx);
			bool result = (comp != null);
			if (result) {
				if (comp  is  DrawableGameComponent) {
					((DrawableGameComponent) comp).UnloadContent();
				}
				CollectionUtils.Remove(collectionsToUpdate,comp);
				if (Removed != null) {
					Removed.Invoke(comp);
				}
			}
			return result;
		}
	
		private ComponentEvent Added;
	
		private ComponentEvent Removed;
	
		public ComponentEvent GetAdded() {
			return Added;
		}
	
		public void SetAdded(ComponentEvent added) {
			Added = added;
		}
	
		public ComponentEvent GetRemoved() {
			return Removed;
		}
	
		public void SetRemoved(ComponentEvent removed) {
			Removed = removed;
		}
	}
}
