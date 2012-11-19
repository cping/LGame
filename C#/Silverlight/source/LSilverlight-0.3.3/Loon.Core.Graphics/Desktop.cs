using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Core.Input;
using Loon.Core.Graphics.Component;
using Loon.Core.Graphics.OpenGL;
using Loon.Utils;

namespace Loon.Core.Graphics
{
   public class Desktop : LRelease {
	
		// 空桌面布局
		public static readonly Desktop EMPTY_DESKTOP = new Desktop();
	
		// 输入设备监听
		protected internal readonly LInput input;
	
		private LContainer contentPane;
	
		private LComponent modal;
	
		private LComponent hoverComponent;
	
		private LComponent selectedComponent;
	
		private LComponent[] clickComponent;
	
		/// <summary>
		/// 构造一个可用桌面
		/// </summary>
		///
		/// <param name="input"></param>
		/// <param name="width"></param>
		/// <param name="height"></param>
		public Desktop(LInput input, int width, int height) {
			this.clickComponent = new LComponent[1];
			this.contentPane = new LPanel(0, 0, width, height);
			this.input = input;
			this.SetDesktop(this.contentPane);
		}
	
		/// <summary>
		/// 空桌面布局
		/// </summary>
		///
		private Desktop() {
			this.clickComponent = new LComponent[1];
			this.contentPane = new LPanel(0, 0, 1, 1);
			this.input = null;
			this.SetDesktop(this.contentPane);
		}
	
		public int Size() {
			return contentPane.GetComponentCount();
		}
	
		public void Add(LComponent comp) {
			if (comp == null) {
				return;
			}
			if (comp.isFull) {
				this.input.SetRepaintMode(Screen.SCREEN_NOT_REPAINT);
			}
			this.contentPane.Add(comp);
			this.ProcessTouchMotionEvent();
		}
	
		public int Remove(LComponent comp) {
			int removed = this.RemoveComponent(this.contentPane, comp);
			if (removed != -1) {
				this.ProcessTouchMotionEvent();
			}
			return removed;
		}
	
		public int Remove(Type clazz) {
			int removed = this.RemoveComponent(this.contentPane, clazz);
			if (removed != -1) {
				this.ProcessTouchMotionEvent();
			}
			return removed;
		}
	
		private int RemoveComponent(LContainer container, LComponent comp) {
			int removed = container.Remove(comp);
			LComponent[] components = container.GetComponents();
			int i = 0;
			while (removed == -1 && i < components.Length - 1) {
				if (components[i].IsContainer()) {
					removed = this
							.RemoveComponent((LContainer) components[i], comp);
				}
				i++;
			}
	
			return removed;
		}
	
		private int RemoveComponent(LContainer container,
				Type clazz) {
			int removed = container.Remove(clazz);
			LComponent[] components = container.GetComponents();
			int i = 0;
			while (removed == -1 && i < components.Length - 1) {
				if (components[i].IsContainer()) {
					removed = this.RemoveComponent((LContainer) components[i],
							clazz);
				}
				i++;
			}
			return removed;
		}
	
		internal bool isClicked;
	
		/// <summary>
		/// 刷新当前桌面
		/// </summary>
		///
		public void Update(long timer) {
			if (!this.contentPane.IsVisible()) {
				return;
			}
			this.ProcessEvents();
			// 刷新桌面中子容器组件
			this.contentPane.Update(timer);
		}
	
		public void SetAutoDestory(bool a) {
			if (contentPane != null) {
				contentPane.SetAutoDestroy(a);
			}
		}
	
		public bool IsAutoDestory() {
			if (contentPane != null) {
				return contentPane.IsAutoDestroy();
			}
			return false;
		}
	
		public void DoClick(int x, int y) {
			if (!this.contentPane.IsVisible()) {
				return;
			}
			LComponent[] components = contentPane.GetComponents();
			for (int i = 0; i < components.Length; i++) {
				LComponent component = components[i];
				if (component != null && component.Intersects(x, y)) {
					component.Update(0);
					component.ProcessTouchPressed();
				}
			}
			isClicked = true;
		}
	
		public void DoClicked(int x, int y) {
			if (!this.contentPane.IsVisible()) {
				return;
			}
			LComponent[] components = contentPane.GetComponents();
			for (int i = 0; i < components.Length; i++) {
				LComponent component = components[i];
				if (component != null && component.Intersects(x, y)) {
					component.Update(0);
					component.ProcessTouchReleased();
					component.ProcessTouchClicked();
				}
			}
			isClicked = true;
		}
	
		public void CreateUI(GLEx g) {
			this.contentPane.CreateUI(g);
		}
	
		/// <summary>
		/// 事件监听
		/// </summary>
		///
		private void ProcessEvents() {
			// 鼠标滑动
			this.ProcessTouchMotionEvent();
			// 鼠标事件
			if (this.hoverComponent != null && this.hoverComponent.IsEnabled()) {
				this.ProcessTouchEvent();
			}
			// 键盘事件
			if (this.selectedComponent != null
					&& this.selectedComponent.IsEnabled()) {
				this.ProcessKeyEvent();
			}
		}
	
		/// <summary>
		/// 鼠标运动事件
		/// </summary>
		///
		private void ProcessTouchMotionEvent() {

			if (this.hoverComponent != null && this.hoverComponent.IsEnabled()
					&& input.IsMoving()) {
				if (this.input.GetTouchDY() != 0 || this.input.GetTouchDY() != 0) {
					this.hoverComponent.ProcessTouchDragged();
				}
	
			} else {
                if (Touch.IsDrag() || Touch.IsMove())
                {
                    // 获得当前窗体下鼠标坐标
                    LComponent comp = this.FindComponent(this.input.GetTouchX(),
                            this.input.GetTouchY());
                    if (comp != null)
                    {
                        if (this.input.GetTouchDX() != 0
                                || this.input.GetTouchDY() != 0)
                        {
                            comp.ProcessTouchMoved();
                        }

                        if (this.hoverComponent == null)
                        {
                            comp.ProcessTouchEntered();

                        }
                        else if (comp != this.hoverComponent)
                        {
                            this.hoverComponent.ProcessTouchExited();
                            comp.ProcessTouchEntered();
                        }

                    }
                    else
                    {
                        if (this.hoverComponent != null)
                        {
                            this.hoverComponent.ProcessTouchExited();
                        }
                    }

                    this.hoverComponent = comp;
                }
			}
		}
	
		/// <summary>
		/// 鼠标按下事件
		/// </summary>
		///
		private void ProcessTouchEvent() {
			int pressed = this.input.GetTouchPressed(), released = this.input
					.GetTouchReleased();
			if (pressed > Screen.NO_BUTTON) {
				if (!isClicked) {
					this.hoverComponent.ProcessTouchPressed();
				}
				this.clickComponent[0] = this.hoverComponent;
				if (this.hoverComponent.IsFocusable()) {
					if ((pressed == Touch.TOUCH_DOWN || pressed == Touch.TOUCH_UP)
							&& this.hoverComponent != this.selectedComponent) {
						this.SelectComponent(this.hoverComponent);
					}
				}
			}
			if (released > Screen.NO_BUTTON) {
				if (!isClicked) {
					this.hoverComponent.ProcessTouchReleased();
					// 当释放鼠标时，点击事件生效
					if (this.clickComponent[0] == this.hoverComponent) {
						this.hoverComponent.ProcessTouchClicked();
					}
				}
			}
			this.isClicked = false;
		}
	
		/// <summary>
		/// 触发键盘事件
		/// </summary>
		///
		private void ProcessKeyEvent() {
			if (this.input.GetKeyPressed() != Screen.NO_KEY) {
				this.selectedComponent.KeyPressed();
			}
			if (this.input.GetKeyReleased() != Screen.NO_KEY
					&& this.selectedComponent != null) {
				this.selectedComponent.ProcessKeyReleased();
			}
		}
	
		/// <summary>
		/// 查找指定坐标点成员
		/// </summary>
		///
		/// <param name="x"></param>
		/// <param name="y"></param>
		/// <returns></returns>
		private LComponent FindComponent(int x, int y) {
			if (this.modal != null && !this.modal.IsContainer()) {
				return null;
			}
			// 返回子容器
			LContainer panel = (this.modal == null) ? this.contentPane
					: ((LContainer) this.modal);
			LComponent comp = panel.FindComponent(x, y);
			return comp;
		}
	
		/// <summary>
		/// 清除容器焦点
		/// </summary>
		///
		public void ClearFocus() {
			this.DeselectComponent();
		}
	
		internal void DeselectComponent() {
			if (this.selectedComponent == null) {
				return;
			}
			this.selectedComponent.SetSelected(false);
			this.selectedComponent = null;
		}
	
		/// <summary>
		/// 查找指定容器
		/// </summary>
		///
		/// <param name="comp"></param>
		/// <returns></returns>
		internal bool SelectComponent(LComponent comp) {
			if (!comp.IsVisible() || !comp.IsEnabled() || !comp.IsFocusable()) {
				return false;
			}
	
			// 清除最后部分
			this.DeselectComponent();
	
			// 设定选中状态
			comp.SetSelected(true);
			this.selectedComponent = comp;
	
			return true;
		}
	
		internal void SetDesktop(LComponent comp) {
			if (comp.IsContainer()) {
				LComponent[] child = ((LContainer) comp).GetComponents();
				for (int i = 0; i < child.Length; i++) {
					this.SetDesktop(child[i]);
				}
			}
			comp.SetDesktop(this);
		}
	
		internal void SetComponentStat(LComponent comp, bool active) {
			if (this == Desktop.EMPTY_DESKTOP) {
				return;
			}
	
			if (active == false) {
				if (this.hoverComponent == comp) {
					this.ProcessTouchMotionEvent();
				}
	
				if (this.selectedComponent == comp) {
					this.DeselectComponent();
				}
	
				this.clickComponent[0] = null;
	
				if (this.modal == comp) {
					this.modal = null;
				}
	
			} else {
				this.ProcessTouchMotionEvent();
			}
	
			if (comp.IsContainer()) {
				LComponent[] components = ((LContainer) comp).GetComponents();
				int size = ((LContainer) comp).GetComponentCount();
				for (int i = 0; i < size; i++) {
					this.SetComponentStat(components[i], active);
				}
			}
		}
	
		internal void ClearComponentsStat(LComponent[] comp) {
			if (this == Desktop.EMPTY_DESKTOP) {
				return;
			}
	
			bool checkTouchMotion = false;
			for (int i = 0; i < comp.Length; i++) {
				if (this.hoverComponent == comp[i]) {
					checkTouchMotion = true;
				}
	
				if (this.selectedComponent == comp[i]) {
					this.DeselectComponent();
				}
	
				this.clickComponent[0] = null;
	
			}
	
			if (checkTouchMotion) {
				this.ProcessTouchMotionEvent();
			}
		}
	
		public void ValidateUI() {
			this.ValidateContainer(this.contentPane);
		}
	
		internal void ValidateContainer(LContainer container) {
			LComponent[] components = container.GetComponents();
			int size = container.GetComponentCount();
			for (int i = 0; i < size; i++) {
				if (components[i].IsContainer()) {
					this.ValidateContainer((LContainer) components[i]);
				}
			}
		}
	
		public List<LComponent> GetComponents(Type clazz) {
			if (clazz == null) {
				return null;
			}
			LComponent[] components = contentPane.GetComponents();
			int size = components.Length;
			List<LComponent> l = new List<LComponent>(size);
			for (int i = size; i > 0; i--) {
				LComponent comp = (LComponent) components[i - 1];
				Type cls = comp.GetType();
				if (clazz == null || clazz == cls || clazz.IsInstanceOfType(comp)
						|| clazz.Equals(cls)) {
					CollectionUtils.Add(l,comp);
				}
			}
			return l;
		}
	
		public LComponent GetTopComponent() {
			LComponent[] components = contentPane.GetComponents();
			int size = components.Length;
			if (size > 1) {
				return components[1];
			}
			return null;
		}
	
		public LComponent GetBottomComponent() {
			LComponent[] components = contentPane.GetComponents();
			int size = components.Length;
			if (size > 0) {
				return components[size - 1];
			}
			return null;
		}
	
		public LLayer GetTopLayer() {
			LComponent[] components = contentPane.GetComponents();
			int size = components.Length;
			Type clazz = typeof(LLayer);
			for (int i = 0; i < size; i++) {
				LComponent comp = (LComponent) components[i];
				Type cls = comp.GetType();
				if (clazz == null || clazz == cls || clazz.IsInstanceOfType(comp)
						|| clazz.Equals(cls)) {
					return (LLayer) comp;
				}
			}
			return null;
		}
	
		public LLayer GetBottomLayer() {
			LComponent[] components = contentPane.GetComponents();
			int size = components.Length;
			Type clazz = typeof(LLayer);
			for (int i = size; i > 0; i--) {
				LComponent comp = (LComponent) components[i - 1];
				Type cls = comp.GetType();
				if (clazz == null || clazz == cls || clazz.IsInstanceOfType(comp)
						|| clazz.Equals(cls)) {
					return (LLayer) comp;
				}
			}
			return null;
		}
	
		public int GetWidth() {
			return this.contentPane.GetWidth();
		}
	
		public int GetHeight() {
			return this.contentPane.GetHeight();
		}
	
		public void SetSize(int w, int h) {
			this.contentPane.SetSize(w, h);
		}
	
		public LContainer GetContentPane() {
			return this.contentPane;
		}
	
		public void SetContentPane(LContainer pane) {
			pane.SetBounds(0, 0, this.GetWidth(), this.GetHeight());
			this.contentPane = pane;
			this.SetDesktop(this.contentPane);
		}
	
		public LComponent GetHoverComponent() {
			return this.hoverComponent;
		}
	
		public LComponent GetSelectedComponent() {
			return this.selectedComponent;
		}
	
		public LComponent GetModal() {
			return this.modal;
		}
	
		public void SetModal(LComponent comp) {
			if (comp != null && !comp.IsVisible()) {
				throw new Exception(
						"Can't set invisible component as modal component!");
			}
			this.modal = comp;
		}
	
		public LComponent Get() {
			return this.contentPane.Get();
		}
	
		public virtual void Dispose() {
			if (contentPane != null) {
				contentPane.Dispose();
			}
		}
	
	}
}
