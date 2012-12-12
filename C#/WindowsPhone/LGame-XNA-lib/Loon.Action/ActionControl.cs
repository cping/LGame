namespace Loon.Action {
	
	using System;
	using System.Collections;
	using System.ComponentModel;
	using System.IO;
	using System.Runtime.CompilerServices;
	
	/// <summary>
	/// Copyright 2008 - 2011
	/// Licensed under the Apache License, Version 2.0 (the "License"); you may not
	/// use this file except in compliance with the License. You may obtain a copy of
	/// the License at
	/// http://www.apache.org/licenses/LICENSE-2.0
	/// Unless required by applicable law or agreed to in writing, software
	/// distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
	/// WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
	/// License for the specific language governing permissions and limitations under
	/// the License.
	/// </summary>
	///
	/// @project loonframework
	/// @email javachenpeng@yahoo.com
	public class ActionControl {
	
		private static ActionControl instanceAction;
	
		private Actions actions;
	
		private bool pause;
	
		public static ActionControl GetInstance() {
			if (instanceAction != null) {
				return instanceAction;
			}
			 lock (typeof(ActionControl)) {
						if (instanceAction == null) {
							instanceAction = new ActionControl();
						}
						return instanceAction;
					}
		}
	
		private void Call(long elapsedTime) {
			if (pause || actions.GetCount() == 0) {
				return;
			}
			actions.Update(elapsedTime);
		}
	
		public static void Update(long elapsedTime) {
			if (instanceAction != null) {
				instanceAction.Call(elapsedTime);
			}
		}
	
		private ActionControl() {
			actions = new Actions();
		}
	
		public void AddAction(ActionEvent action, ActionBind obj, bool paused) {
			actions.AddAction(action, obj, paused);
		}
	
		public void AddAction(ActionEvent action, ActionBind obj) {
			AddAction(action, obj, false);
		}
	
		public void RemoveAllActions(ActionBind actObject) {
			actions.RemoveAllActions(actObject);
		}
	
		public int GetCount() {
			return actions.GetCount();
		}
	
		public void RemoveAction(object tag, ActionBind actObject) {
			actions.RemoveAction(tag, actObject);
		}
	
		public void RemoveAction(ActionEvent action) {
			actions.RemoveAction(action);
		}
	
		public ActionEvent GetAction(object tag, ActionBind actObject) {
			return actions.GetAction(tag, actObject);
		}
	
		public void Stop(ActionBind actObject) {
			actions.Stop(actObject);
		}
	
		public void Start(ActionBind actObject) {
			actions.Start(actObject);
		}
	
		public void Paused(bool pause_0, ActionBind actObject) {
			actions.Paused(pause_0, actObject);
		}
	
		public bool IsPause() {
			return pause;
		}
	
		public void SetPause(bool pause_0) {
			this.pause = pause_0;
		}
	
		public void Clear() {
			actions.Clear();
		}
	
		public void StopAll() {
			Clear();
			Stop();
		}
	
		public void Stop() {
			pause = true;
		}
	
	}
}
