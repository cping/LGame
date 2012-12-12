using Loon.Utils;
using Loon.Core.Geom;
using Loon.Core.Input;
namespace Loon.Action.Sprite.Node {
	
	public class LNButton : LNUI {

        protected internal System.Collections.Generic.Dictionary<string, LNNode> _buttonElement;

        protected internal System.Collections.Generic.Dictionary<string, LNAction> _touchBeganAction;

        protected internal System.Collections.Generic.Dictionary<string, LNAction> _touchClickedAction;

        protected internal System.Collections.Generic.Dictionary<string, LNAction> _touchMoveOutAction;

		public LNButton() {
			this._buttonElement = new System.Collections.Generic.Dictionary<string, LNNode>();
			this._touchBeganAction = new System.Collections.Generic.Dictionary<string, LNAction>();
			this._touchClickedAction = new System.Collections.Generic.Dictionary<string, LNAction>();
			this._touchMoveOutAction = new System.Collections.Generic.Dictionary<string, LNAction>();
		}
	
	
		public LNFrameStruct fs;
	
		public static LNButton ButtonWithFadeoutTwinkle(string fsName1,
				float Opacity) {
			LNButton button = new LNButton();
			button.InitButtonWithFadeoutTwinkle(fsName1, Opacity);
			return button;
		}
	
		public static LNButton ButtonWithoutTexture(int width, int height) {
			LNButton button = new LNButton();
			button.InitButtonWithoutTexture(width, height);
			return button;
		}
	
		public static LNButton ButtonWithTextureTwinkle(string fsName1,
				string fsName2) {
			LNButton button = new LNButton();
			button.InitButtonWithTextureTwinkle(fsName1, fsName2);
			return button;
		}
	
		public static LNButton ButtonWithToggleTwinkle(string fsName1) {
			LNButton button = new LNButton();
			button.InitButtonWithToggleTwinkle(fsName1);
			return button;
		}
	
		public static LNButton CheckboxWithPressingTexture(string fsName1,
				string fsName2, string fsName3, string fsName4) {
			LNButton button = new LNButton();
			button.InitCheckboxWithPrssingTexture(fsName1, fsName2, fsName3,
					fsName4);
			return button;
		}
	
		public static LNButton CheckboxWithTexture(string fsName1, string fsName2) {
			LNButton button = new LNButton();
			button.InitCheckboxWithPrssingTexture(fsName1, fsName2, "", "");
			return button;
		}

        public override void SetAlpha(float a)
        {
            base._alpha = a;
            base._color.a = a;
            foreach (string name in this._buttonElement.Keys)
            {
                LNNode node = (LNNode)CollectionUtils.Get(this._buttonElement, name);
                node.SetAlpha(a);
            }
        }
	
		public bool GetClicked() {
			if (this._buttonElement.ContainsKey("ImageOn")) {
                LNNode node = (LNNode)CollectionUtils.Get(this._buttonElement, "ImageOn");
				return node._visible;
			}
			if (this._buttonElement.ContainsKey("ImageOff")) {
                LNNode node2 = (LNNode)CollectionUtils.Get(this._buttonElement, "ImageOff");
				return !node2._visible;
			}
			return true;
		}
	
		public void InitButtonWithFadeoutTwinkle(string fsName1, float Opacity) {
			LNSprite sprite = new LNSprite(fsName1);
			CollectionUtils.Put(this._buttonElement,"Image",sprite);
			base.AddNode(sprite,0);
			base.SetNodeSize(sprite.GetWidth(),sprite.GetHeight());
			base.SetAnchor(new Vector2f(base.GetWidth() / 2f,
							base.GetWidth() / 2f));
			LNAction action = LNSequence.Action(new LNAction[] { LNAlphaAction
					.Action(Opacity) });
			action.AssignTarget(sprite);
			LNAction action2 = LNSequence.Action(new LNAction[] { LNAlphaAction
					.Action(1f) });
			action2.AssignTarget(sprite);
			LNSequence sequence = LNSequence.Action(new LNAction[] {
					LNAlphaAction.Action(1f), LNDelay.Action(0.1f),
					LNAlphaAction.Action(0.5f), LNDelay.Action(0.1f) });
			LNAction action3 = LNSequence.Action(new LNAction[] {
					LNRepeat.Action(sequence, 3), LNShow.Action(),
					LNAlphaAction.Action(1f) });
			action3.AssignTarget(sprite);
			CollectionUtils.Put(this._touchBeganAction,"Image",action);
			CollectionUtils.Put(this._touchMoveOutAction,"Image",action2);
			CollectionUtils.Put(this._touchClickedAction,"Image",action3);
		}
	
		public void InitButtonWithoutTexture(int width, int height) {
			base.SetNodeSize(width,height);
			base.SetAnchor(new Vector2f(base.GetWidth() / 2f,
							base.GetHeight() / 2f));
		}
	
		public void InitButtonWithTextureTwinkle(string fsName1, string fsName2) {
			this.fs = LNDataCache.GetFrameStruct(fsName1);
			LNAnimation anim = new LNAnimation("Frame", 0.1f, fsName1, fsName2);
			LNSprite sprite = new LNSprite();
			sprite.AddAnimation(anim);
			sprite.SetFrame("Frame", 0);
			CollectionUtils.Put(this._buttonElement,"Image",sprite);
			base.AddNode(sprite,0);
			base.SetNodeSize(sprite.GetWidth(),sprite.GetHeight());
			base.SetAnchor(new Vector2f(base.GetWidth() / 2f,
							base.GetHeight() / 2f));
			LNAction action = LNSequence.Action(LNFrameAction.Action("Frame", 1));
			action.AssignTarget(sprite);
			LNAction action2 = LNSequence.Action(LNFrameAction.Action("Frame", 0));
			action2.AssignTarget(sprite);
			LNAction action3 = LNSequence.Action(
					LNRepeat.Action(LNAnimate.Action(anim), 1),
					LNFrameAction.Action("Frame", 0));
			action3.AssignTarget(sprite);
			CollectionUtils.Put(this._touchBeganAction,"Image",action);
			CollectionUtils.Put(this._touchMoveOutAction,"Image",action2);
			CollectionUtils.Put(this._touchClickedAction,"Image",action3);
		}
	
		public void InitButtonWithToggleTwinkle(string fsName1) {
			LNSprite sprite = new LNSprite(fsName1);
			CollectionUtils.Put(this._buttonElement,"Image",sprite);
			base.AddNode(sprite,0);
			base.SetNodeSize(sprite.GetWidth(),sprite.GetHeight());
			base.SetAnchor(new Vector2f(base.GetWidth() / 2f,
							base.GetHeight() / 2f));
			LNAction action = LNSequence.Action(LNAlphaAction.Action(0.8f));
			action.AssignTarget(sprite);
			LNAction action2 = LNSequence.Action(LNAlphaAction.Action(1f));
			action2.AssignTarget(sprite);
			LNSequence sequence = LNSequence.Action(LNToggleVisibility.Action(),
					LNDelay.Action(0.1f), LNToggleVisibility.Action(),
					LNDelay.Action(0.1f));
			LNAction action3 = LNSequence.Action(LNAlphaAction.Action(1f),
					LNRepeat.Action(sequence, 1), LNShow.Action(),
					LNAlphaAction.Action(1f));
			action3.AssignTarget(sprite);
			CollectionUtils.Put(this._touchBeganAction,"Image",action);
			CollectionUtils.Put(this._touchMoveOutAction,"Image",action2);
			CollectionUtils.Put(this._touchClickedAction,"Image",action3);
		}
	
		public void InitCheckboxWithPrssingTexture(string fsName1, string fsName2,
				string fsName3, string fsName4) {
			if ((fsName3 == "") && (fsName4 == "")) {
				LNSprite node = new LNSprite(fsName1);
				LNSprite sprite2 = new LNSprite(fsName2);
				sprite2.SetVisible(false);
				base.AddNode(node,0);
				base.AddNode(sprite2,0);
				CollectionUtils.Put(this._buttonElement,"ImageOn",node);
				CollectionUtils.Put(this._buttonElement,"ImageOff",sprite2);
				base.SetNodeSize(node.GetWidth(),node.GetHeight());
				base.SetAnchor(new Vector2f(base.GetWidth() / 2f, base.GetHeight() / 2f));
				LNAction action = LNSequence.Action(LNToggleVisibility.Action());
				action.AssignTarget(node);
				CollectionUtils.Put(this._touchClickedAction,"ImageOn",action);
				LNAction action2 = LNSequence.Action(LNToggleVisibility.Action());
				action2.AssignTarget(sprite2);
				CollectionUtils.Put(this._touchClickedAction,"ImageOff",action2);
			} else {
				LNAnimation anim = new LNAnimation("Frame", 0.1f, new string[] {
						fsName1, fsName3 });
				LNAnimation animation2 = new LNAnimation("Frame", 0.1f,
						new string[] { fsName2, fsName4 });
				LNSprite sprite3 = new LNSprite();
				LNSprite sprite4 = new LNSprite();
				sprite3.AddAnimation(anim);
				sprite4.AddAnimation(animation2);
				sprite3.SetFrame("Frame", 0);
				sprite4.SetFrame("Frame", 0);
				base.AddNode(sprite3,0);
				base.AddNode(sprite4,0);
				sprite4.SetVisible(false);
				CollectionUtils.Put(this._buttonElement,"ImageOn",sprite3);
				CollectionUtils.Put(this._buttonElement,"ImageOff",sprite4);
				base.SetNodeSize(sprite3.GetWidth(),sprite3.GetHeight());
				base.SetAnchor(new Vector2f(base.GetWidth() / 2f, base.GetHeight() / 2f));
				LNAction action3 = LNSequence.Action(LNFrameAction.Action("Frame",
						1));
				action3.AssignTarget(sprite3);
				LNAction action4 = LNSequence.Action(LNFrameAction.Action("Frame",
						0));
				action4.AssignTarget(sprite3);
				LNAction action5 = LNSequence.Action(LNToggleVisibility.Action(),
						LNFrameAction.Action("Frame", 0));
				action5.AssignTarget(sprite3);
				CollectionUtils.Put(this._touchBeganAction,"ImageOn",action3);
				CollectionUtils.Put(this._touchMoveOutAction,"ImageOn",action4);
				CollectionUtils.Put(this._touchClickedAction,"ImageOn",action5);
				LNAction action6 = LNSequence.Action(LNFrameAction.Action("Frame",
						1));
				action6.AssignTarget(sprite4);
				LNAction action7 = LNSequence.Action(LNFrameAction.Action("Frame",
						0));
				action7.AssignTarget(sprite4);
				LNAction action8 = LNSequence.Action(LNToggleVisibility.Action(),
						LNFrameAction.Action("Frame", 0));
				action8.AssignTarget(sprite4);
				CollectionUtils.Put(this._touchBeganAction,"ImageOff",action6);
				CollectionUtils.Put(this._touchMoveOutAction,"ImageOff",action7);
				CollectionUtils.Put(this._touchClickedAction,"ImageOff",action8);
			}
		}
	
		public LNCallFunc.Callback ActionCallBack;
	
		private bool isPressed, isDraging;
	
		public override void ProcessTouchPressed() {
			if (!isPressed) {
				base.ProcessTouchPressed();
				foreach (string str  in  this._buttonElement.Keys) {
                    LNNode node = (LNNode)CollectionUtils.Get(this._buttonElement, str);
					node.StopAllAction();
					if (this._touchBeganAction.ContainsKey(str)) {
						node.RunAction((LNAction)CollectionUtils.Get(this._touchBeganAction,str));
					}
				}
				isPressed = true;
	
			}
		}
	
		public override void ProcessTouchReleased() {
			if (isPressed) {
				base.ProcessTouchReleased();
				float num = 0f;
				foreach (string str  in  this._buttonElement.Keys) {
                    LNNode node = (LNNode)CollectionUtils.Get(this._buttonElement, str);
					node.StopAllAction();
					if (this._touchClickedAction.ContainsKey(str)) {
                        num = MathUtils.Max(num, ((LNAction)CollectionUtils.Get(this._touchClickedAction, str))
								.GetDuration());
                        node.RunAction((LNAction)CollectionUtils.Get(this._touchClickedAction, str));
					}
				}
				if (ActionCallBack != null) {
					if (num > 0f) {
						base.RunAction(LNSequence.Action(LNDelay.Action(num),
													LNCallFunc.Action(ActionCallBack)));
					} else {
						base.RunAction(LNCallFunc.Action(ActionCallBack));
					}
				}
				isPressed = false;
			}
		}

        public override void ProcessTouchDragged()
        {
            base.ProcessTouchDragged();
            foreach (string key in this._buttonElement.Keys)
            {
                LNNode node = (LNNode)CollectionUtils.Get(this._buttonElement, key);
                node.StopAllAction();
                if (this._touchBeganAction.ContainsKey(key))
                {
                    node.RunAction((LNAction)CollectionUtils.Get(this._touchBeganAction, key));
                }
            }
            isDraging = true;
        }
	
		public override void Update(float dt) {
			base.Update(dt);
			if (isDraging && !Touch.IsDrag()) {
				foreach (string key  in  this._buttonElement.Keys) {
					LNNode node = (LNNode)CollectionUtils.Get(this._buttonElement,key);
					node.StopAllAction();
					if (this._touchMoveOutAction.ContainsKey(key)) {
						node.RunAction((LNAction)CollectionUtils.Get(this._touchMoveOutAction,key));
					}
				}
				isDraging = false;
			}
		}
	
		public LNCallFunc.Callback GetActionCallBack() {
			return ActionCallBack;
		}
	
		public void SetActionCallBack(LNCallFunc.Callback ac) {
			ActionCallBack = ac;
		}
	}
}
