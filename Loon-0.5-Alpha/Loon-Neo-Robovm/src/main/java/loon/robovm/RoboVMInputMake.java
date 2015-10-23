/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon.robovm;

import loon.LObject;
import loon.event.InputMake;
import loon.event.TouchMake;
import loon.geom.Vector2f;
import loon.utils.reply.GoFuture;
import loon.utils.reply.GoPromise;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.foundation.NSSet;
import org.robovm.apple.uikit.UIAlertView;
import org.robovm.apple.uikit.UIAlertViewDelegateAdapter;
import org.robovm.apple.uikit.UIAlertViewStyle;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIKeyboardType;
import org.robovm.apple.uikit.UIReturnKeyType;
import org.robovm.apple.uikit.UITextAutocapitalizationType;
import org.robovm.apple.uikit.UITextAutocorrectionType;
import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UITouch;

@SuppressWarnings("deprecation")
public class RoboVMInputMake extends InputMake {

	private final RoboVMGame game;

	public RoboVMInputMake(RoboVMGame game) {
		this.game = game;
	}

	@Override
	public GoFuture<String> getText(loon.event.KeyMake.TextType textType,
			String label, String initVal) {
		final GoPromise<String> result = game.asyn().deferredPromise();

		UIAlertView view = new UIAlertView();
		if (label != null){
			view.setTitle(label);
		}
		view.addButton("Cancel");
		view.addButton("OK");
		view.setAlertViewStyle(UIAlertViewStyle.PlainTextInput);

		final UITextField field = view.getTextField(0);
		field.setReturnKeyType(UIReturnKeyType.Done);
		if (initVal != null) {
			field.setText(initVal);
		}

		switch (textType) {
		case NUMBER:
			field.setKeyboardType(UIKeyboardType.NumberPad);
			break;
		case EMAIL:
			field.setKeyboardType(UIKeyboardType.EmailAddress);
			break;
		case URL:
			field.setKeyboardType(UIKeyboardType.URL);
			break;
		case DEFAULT:
			field.setKeyboardType(UIKeyboardType.Default);
			break;
		}

		field.setAutocorrectionType(UITextAutocorrectionType.Yes);
		field.setAutocapitalizationType(UITextAutocapitalizationType.Sentences);
		field.setSecureTextEntry(false); 

		view.setDelegate(new UIAlertViewDelegateAdapter() {
			public void clicked(UIAlertView view, long buttonIndex) {
				result.succeed(buttonIndex == 0 ? null : field.getText());
			}
		});
		view.show();

		return result;
	}

	void onTouchesBegan(NSSet<UITouch> touches, UIEvent event) {
		touchEvents.emit(toEvents(touches, event, TouchMake.Event.Kind.START));
	}

	void onTouchesMoved(NSSet<UITouch> touches, UIEvent event) {
		touchEvents.emit(toEvents(touches, event, TouchMake.Event.Kind.MOVE));
	}

	void onTouchesEnded(NSSet<UITouch> touches, UIEvent event) {
		touchEvents.emit(toEvents(touches, event, TouchMake.Event.Kind.END));
	}

	void onTouchesCancelled(NSSet<UITouch> touches, UIEvent event) {
		touchEvents.emit(toEvents(touches, event, TouchMake.Event.Kind.CANCEL));
	}

	private TouchMake.Event[] toEvents(NSSet<UITouch> touches, UIEvent event,
			TouchMake.Event.Kind kind) {
		final TouchMake.Event[] events = new TouchMake.Event[touches.size()];
		int idx = 0;
		for (UITouch touch : touches) {
			CGPoint loc = touch.getLocationInView(touch.getView());
			Vector2f xloc = game.graphics().transformTouch((float) loc.getX(),
					(float) loc.getY());
			int id = (int) touch.getHandle();
			events[idx++] = new TouchMake.Event(0, touch.getTimestamp() * 1000,
					xloc.x(), xloc.y(), kind, id);
		}
		return events;
	}

	@Override
	public boolean hasTouch() {
		return true;
	}
	
	@Override
	public void callback(LObject o) {

	}
}
