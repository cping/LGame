using Loon.Core;
using System;
using Loon.Core.Timer;
namespace Loon.Action.Sprite.Painting {
	
	public class GameComponent : IGameComponent, IUpdateable,
			IComparable<GameComponent>, LRelease {
	
		private DrawableScreen _game;
	
		private int _updateOrder;
	
		private bool _enabled;
	
		public GameComponent(DrawableScreen game) {
			_game = game;
			SetEnabled(true);
		}
	
		public DrawableScreen GetGame() {
			return _game;
		}
	
		public virtual void Initialize() {
		}
	
		public void Update(GameTime gameTime) {
		}
	
		public SpriteBatch GetGraphicsDevice() {
			return _game.GetSpriteBatch();
		}
	
		public bool GetEnabled() {
			return _enabled;
		}
	
		public DrawableEvent EnabledChanged;
	
		public void SetEnabled(bool value_ren) {
			_enabled = value_ren;
			Raise(EnabledChanged);
			OnEnabledChanged(this);
		}
	
		public int GetUpdateOrder() {
			return _updateOrder;
		}
	
		public DrawableEvent UpdateOrder;
	
		public void SetUpdateOrder(int value_ren) {
			_updateOrder = value_ren;
			Raise(UpdateOrder);
			OnUpdateOrderChanged(this);
		}
	
		private void Raise(DrawableEvent evt0) {
			if (evt0 != null) {
				evt0.Invoke();
			}
		}
	
		protected internal void OnUpdateOrderChanged(GameComponent cmp) {
		}
	
		protected internal void OnEnabledChanged(GameComponent cmp) {
		}
	
		protected internal void Dispose(bool disposing) {
		}
	
		public void Dispose() {
			Dispose(true);
		}
	
		public int CompareTo(GameComponent other) {
			return other.GetUpdateOrder() - this.GetUpdateOrder();
		}
	
		public DrawableEvent GetEnabledChanged() {
			return EnabledChanged;
		}
	
		public void SetEnabledChanged(DrawableEvent enabledChanged) {
			EnabledChanged = enabledChanged;
		}
	
		public void SetUpdateOrder(DrawableEvent updateOrder) {
			UpdateOrder = updateOrder;
		}
	
	}}
