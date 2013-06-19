/**
 * Copyright 2008 - 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.3.3
 */
package loon.action.sprite.node;

import java.util.HashMap;

import loon.core.geom.Vector2f;
import loon.core.input.LInputFactory.Touch;
import loon.utils.MathUtils;

public class LNButton extends LNUI {

	protected HashMap<String, LNNode> _buttonElement = new HashMap<String, LNNode>();

	protected HashMap<String, LNAction> _touchBeganAction = new HashMap<String, LNAction>();

	protected HashMap<String, LNAction> _touchClickedAction = new HashMap<String, LNAction>();

	protected HashMap<String, LNAction> _touchMoveOutAction = new HashMap<String, LNAction>();

	public LNFrameStruct fs;

	public static LNButton buttonWithFadeoutTwinkle(String fsName1,
			float Opacity) {
		LNButton button = new LNButton();
		button.initButtonWithFadeoutTwinkle(fsName1, Opacity);
		return button;
	}

	public static LNButton buttonWithoutTexture(int width, int height) {
		LNButton button = new LNButton();
		button.initButtonWithoutTexture(width, height);
		return button;
	}

	public static LNButton buttonWithTextureTwinkle(String fsName1,
			String fsName2) {
		LNButton button = new LNButton();
		button.initButtonWithTextureTwinkle(fsName1, fsName2);
		return button;
	}

	public static LNButton buttonWithToggleTwinkle(String fsName1) {
		LNButton button = new LNButton();
		button.initButtonWithToggleTwinkle(fsName1);
		return button;
	}

	public static LNButton checkboxWithPressingTexture(String fsName1,
			String fsName2, String fsName3, String fsName4) {
		LNButton button = new LNButton();
		button.initCheckboxWithPrssingTexture(fsName1, fsName2, fsName3,
				fsName4);
		return button;
	}

	public static LNButton checkboxWithTexture(String fsName1, String fsName2) {
		LNButton button = new LNButton();
		button.initCheckboxWithPrssingTexture(fsName1, fsName2, "", "");
		return button;
	}

	@Override
	public void setAlpha(float a) {
		super._alpha = a;
		super._color.a = a;
		for (String name : this._buttonElement.keySet()) {
			LNNode node = this._buttonElement.get(name);
			node.setAlpha(a);
		}
	}

	public boolean getClicked() {
		if (this._buttonElement.containsKey("ImageOn")) {
			LNNode node = this._buttonElement.get("ImageOn");
			return node._visible;
		}
		if (this._buttonElement.containsKey("ImageOff")) {
			LNNode node2 = this._buttonElement.get("ImageOff");
			return !node2._visible;
		}
		return true;
	}

	public void initButtonWithFadeoutTwinkle(String fsName1, float Opacity) {
		LNSprite sprite = new LNSprite(fsName1);
		this._buttonElement.put("Image", sprite);
		super.addNode(sprite, 0);
		super.setNodeSize(sprite.getWidth(),sprite.getHeight());
		super.setAnchor(new Vector2f(super.getWidth() / 2f, super.getWidth() / 2f));
		LNAction action = LNSequence.Action(new LNAction[] { LNAlphaAction
				.Action(Opacity) });
		action.assignTarget(sprite);
		LNAction action2 = LNSequence.Action(new LNAction[] { LNAlphaAction
				.Action(1f) });
		action2.assignTarget(sprite);
		LNSequence sequence = LNSequence.Action(new LNAction[] {
				LNAlphaAction.Action(1f), LNDelay.Action(0.1f),
				LNAlphaAction.Action(0.5f), LNDelay.Action(0.1f) });
		LNAction action3 = LNSequence.Action(new LNAction[] {
				LNRepeat.Action(sequence, 3), LNShow.Action(),
				LNAlphaAction.Action(1f) });
		action3.assignTarget(sprite);
		this._touchBeganAction.put("Image", action);
		this._touchMoveOutAction.put("Image", action2);
		this._touchClickedAction.put("Image", action3);
	}

	public void initButtonWithoutTexture(int width, int height) {
		super.setNodeSize(width, height);
		super.setAnchor(new Vector2f(super.getWidth() / 2f, super.getHeight() / 2f));
	}

	public void initButtonWithTextureTwinkle(String fsName1, String fsName2) {
		this.fs = LNDataCache.getFrameStruct(fsName1);
		LNAnimation anim = new LNAnimation("Frame", 0.1f, fsName1, fsName2);
		LNSprite sprite = new LNSprite();
		sprite.addAnimation(anim);
		sprite.setFrame("Frame", 0);
		this._buttonElement.put("Image", sprite);
		super.addNode(sprite, 0);
		super.setNodeSize(sprite.getWidth(),sprite.getHeight());
		super.setAnchor(new Vector2f(super.getWidth() / 2f, super.getHeight() / 2f));
		LNAction action = LNSequence.Action(LNFrameAction.Action("Frame", 1));
		action.assignTarget(sprite);
		LNAction action2 = LNSequence.Action(LNFrameAction.Action("Frame", 0));
		action2.assignTarget(sprite);
		LNAction action3 = LNSequence.Action(
				LNRepeat.Action(LNAnimate.Action(anim), 1),
				LNFrameAction.Action("Frame", 0));
		action3.assignTarget(sprite);
		this._touchBeganAction.put("Image", action);
		this._touchMoveOutAction.put("Image", action2);
		this._touchClickedAction.put("Image", action3);
	}

	public void initButtonWithToggleTwinkle(String fsName1) {
		LNSprite sprite = new LNSprite(fsName1);
		this._buttonElement.put("Image", sprite);
		super.addNode(sprite, 0);
		super.setNodeSize(sprite.getWidth(),sprite.getHeight());
		super.setAnchor(new Vector2f(super.getWidth() / 2f, super.getHeight() / 2f));
		LNAction action = LNSequence.Action(LNAlphaAction.Action(0.8f));
		action.assignTarget(sprite);
		LNAction action2 = LNSequence.Action(LNAlphaAction.Action(1f));
		action2.assignTarget(sprite);
		LNSequence sequence = LNSequence.Action(LNToggleVisibility.Action(),
				LNDelay.Action(0.1f), LNToggleVisibility.Action(),
				LNDelay.Action(0.1f));
		LNAction action3 = LNSequence.Action(LNAlphaAction.Action(1f),
				LNRepeat.Action(sequence, 1), LNShow.Action(),
				LNAlphaAction.Action(1f));
		action3.assignTarget(sprite);
		this._touchBeganAction.put("Image", action);
		this._touchMoveOutAction.put("Image", action2);
		this._touchClickedAction.put("Image", action3);
	}

	public void initCheckboxWithPrssingTexture(String fsName1, String fsName2,
			String fsName3, String fsName4) {
		if ((fsName3.equals("")) && (fsName4.equals(""))) {
			LNSprite node = new LNSprite(fsName1);
			LNSprite sprite2 = new LNSprite(fsName2);
			sprite2.setVisible(false);
			super.addNode(node, 0);
			super.addNode(sprite2, 0);
			this._buttonElement.put("ImageOn", node);
			this._buttonElement.put("ImageOff", sprite2);
			super.setNodeSize(node.getWidth(),node.getHeight());
			super.setAnchor(new Vector2f(super.getWidth() / 2f, super.getHeight() / 2f));
			LNAction action = LNSequence.Action(LNToggleVisibility.Action());
			action.assignTarget(node);
			this._touchClickedAction.put("ImageOn", action);
			LNAction action2 = LNSequence.Action(LNToggleVisibility.Action());
			action2.assignTarget(sprite2);
			this._touchClickedAction.put("ImageOff", action2);
		} else {
			LNAnimation anim = new LNAnimation("Frame", 0.1f, new String[] {
					fsName1, fsName3 });
			LNAnimation animation2 = new LNAnimation("Frame", 0.1f,
					new String[] { fsName2, fsName4 });
			LNSprite sprite3 = new LNSprite();
			LNSprite sprite4 = new LNSprite();
			sprite3.addAnimation(anim);
			sprite4.addAnimation(animation2);
			sprite3.setFrame("Frame", 0);
			sprite4.setFrame("Frame", 0);
			super.addNode(sprite3, 0);
			super.addNode(sprite4, 0);
			sprite4.setVisible(false);
			this._buttonElement.put("ImageOn", sprite3);
			this._buttonElement.put("ImageOff", sprite4);
			super.setNodeSize(sprite3.getWidth(),sprite3.getHeight());
			super.setAnchor(new Vector2f(super.getWidth() / 2f, super.getHeight() / 2f));
			LNAction action3 = LNSequence.Action(LNFrameAction.Action("Frame",
					1));
			action3.assignTarget(sprite3);
			LNAction action4 = LNSequence.Action(LNFrameAction.Action("Frame",
					0));
			action4.assignTarget(sprite3);
			LNAction action5 = LNSequence.Action(LNToggleVisibility.Action(),
					LNFrameAction.Action("Frame", 0));
			action5.assignTarget(sprite3);
			this._touchBeganAction.put("ImageOn", action3);
			this._touchMoveOutAction.put("ImageOn", action4);
			this._touchClickedAction.put("ImageOn", action5);
			LNAction action6 = LNSequence.Action(LNFrameAction.Action("Frame",
					1));
			action6.assignTarget(sprite4);
			LNAction action7 = LNSequence.Action(LNFrameAction.Action("Frame",
					0));
			action7.assignTarget(sprite4);
			LNAction action8 = LNSequence.Action(LNToggleVisibility.Action(),
					LNFrameAction.Action("Frame", 0));
			action8.assignTarget(sprite4);
			this._touchBeganAction.put("ImageOff", action6);
			this._touchMoveOutAction.put("ImageOff", action7);
			this._touchClickedAction.put("ImageOff", action8);
		}
	}

	public LNCallFunc.Callback ActionCallBack;
	
	private boolean isPressed, isDraging;

	@Override
	public void processTouchPressed() {
		if (!isPressed) {
			super.processTouchPressed();
			for (String str : this._buttonElement.keySet()) {
				LNNode node = this._buttonElement.get(str);
				node.stopAllAction();
				if (this._touchBeganAction.containsKey(str)) {
					node.runAction(this._touchBeganAction.get(str));
				}
			}
			isPressed = true;
		
		}
	}

	@Override
	public void processTouchReleased() {
		if (isPressed) {
			super.processTouchReleased();
			float num = 0f;
			for (String str : this._buttonElement.keySet()) {
				LNNode node = this._buttonElement.get(str);
				node.stopAllAction();
				if (this._touchClickedAction.containsKey(str)) {
					num = MathUtils.max(num, this._touchClickedAction.get(str)
							.getDuration());
					node.runAction(this._touchClickedAction.get(str));
				}
			}
			if (ActionCallBack != null) {
				if (num > 0f) {
					super.runAction(LNSequence.Action(LNDelay.Action(num),
							LNCallFunc.Action(ActionCallBack)));
				} else {
					super.runAction(LNCallFunc.Action(ActionCallBack));
				}
			}
			isPressed = false;
		}
	}

	@Override
	public void processTouchDragged() {
		super.processTouchDragged();
		for (String key : this._buttonElement.keySet()) {
			LNNode node = this._buttonElement.get(key);
			node.stopAllAction();
			if (this._touchBeganAction.containsKey(key)) {
				node.runAction(this._touchBeganAction.get(key));
			}
		}
		isDraging = true;
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		if (isDraging && !Touch.isDrag()) {
			for (String key : this._buttonElement.keySet()) {
				LNNode node = this._buttonElement.get(key);
				node.stopAllAction();
				if (this._touchMoveOutAction.containsKey(key)) {
					node.runAction(this._touchMoveOutAction.get(key));
				}
			}
			isDraging = false;
		}
	}

	public LNCallFunc.Callback getActionCallBack() {
		return ActionCallBack;
	}

	public void setActionCallBack(LNCallFunc.Callback ac) {
		ActionCallBack = ac;
	}
}
