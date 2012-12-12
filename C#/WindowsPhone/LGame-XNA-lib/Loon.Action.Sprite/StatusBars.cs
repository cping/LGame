using Loon.Core;
using System.Collections.Generic;
using Loon.Utils;
using Loon.Core.Graphics.Opengl;
using Loon.Core.Geom;
namespace Loon.Action.Sprite {
	
	public sealed class StatusBars : LObject, ISprite {
	
		private List<StatusBar> barCaches;
	
		private bool visible;
	
		public StatusBars() {
            this.barCaches = new List<StatusBar>(
					CollectionUtils.INITIAL_CAPACITY);
			this.visible = true;
		}
	
		public StatusBar AddBar(int value_ren, int maxValue, int x, int y, int w, int h) {
			 lock (barCaches) {
						StatusBar bar = new StatusBar(value_ren, maxValue, x, y, w, h);
						CollectionUtils.Add(barCaches,bar);
						return bar;
					}
		}
	
		public StatusBar AddBar(int x, int y, int width, int height) {
			return AddBar(100, 100, x, y, width, height);
		}
	
		public StatusBar AddBar(int width, int height) {
			return AddBar(100, 100, 0, 0, width, height);
		}
	
		public void AddBar(StatusBar bar) {
			 lock (barCaches) {
						CollectionUtils.Add(barCaches,bar);
					}
		}
	
		public bool RemoveBar(StatusBar bar) {
			if (bar == null) {
				return false;
			}
			 lock (barCaches) {
						return CollectionUtils.Remove(barCaches,bar);
					}
		}
	
		public int Size() {
			return barCaches.Count;
		}
	
		public void Clear() {
			 lock (barCaches) {
						CollectionUtils.Clear(barCaches);
					}
		}
	
		public void Hide(StatusBar bar) {
			if (bar != null) {
				bar.SetVisible(false);
			}
		}
	
		public void Show(StatusBar bar) {
			if (bar != null) {
				bar.SetVisible(true);
			}
		}
	
		public void CreateUI(GLEx g) {
			if (!visible) {
				return;
			}
			int size = barCaches.Count;
			if (size > 0) {
				 lock (barCaches) {
								StatusBar.GLBegin();
								for (int i = 0; i < size; i++) {
									StatusBar bar = barCaches[i];
									if (bar != null && bar.visible) {
										bar.CreateUI(g);
									}
								}
								StatusBar.GLEnd();
							}
			}
		}

        public override void Update(long elapsedTime)
        {
			if (!visible) {
				return;
			}
			int size = barCaches.Count;
			if (size > 0) {
				 lock (barCaches) {
								for (int i = 0; i < size; i++) {
									StatusBar bar = barCaches[i];
									if (bar != null && bar.visible) {
										bar.Update(elapsedTime);
									}
								}
							}
			}
		}
	
		public void SetVisible(bool v) {
			this.visible = v;
		}
	
		public bool IsVisible() {
			return visible;
		}
	
		public RectBox GetCollisionBox() {
			return null;
		}
	
		public LTexture GetBitmap() {
			return null;
		}

        public override int GetWidth()
        {
			return 0;
		}

        public override int GetHeight()
        {
			return 0;
		}
	
		public void Dispose() {
			this.visible = false;
			int size = barCaches.Count;
			for (int i = 0; i < size; i++) {
				StatusBar bar = barCaches[i];
				if (bar != null) {
					bar.Dispose();
					bar = null;
				}
			}
			CollectionUtils.Clear(barCaches);
			barCaches = null;
		}
	}
}
