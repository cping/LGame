using System.Collections.Generic;
using Loon.Java.Collections;
namespace Loon.Action.Sprite.Node {
	
	public class LNEaseSequence : LNAction {
	
		internal LNEaseSequence() {
	
		}
	
		protected internal List<LNAction> _actionList;
	
		protected internal int _index;
	
		public static LNEaseSequence Action(Easing easing,
                List<LNAction> actions)
        {
			LNEaseSequence ease = new LNEaseSequence();
			ease._actionList = actions;
			ease._duration = 0f;
			ease._index = 0;
			ease._easing = easing;
			for (int i = 0; i < actions.Count; i++) {
				actions[i]._easing = easing;
				ease._duration += actions[i].GetDuration();
			}
			return ease;
		}
	
		public static LNEaseSequence Action(Easing easing, params LNAction[] actions) {
			int size = actions.Length;
			LNEaseSequence ease = new LNEaseSequence();
            ease._actionList = new List<LNAction>(size);
			ease._actionList.AddRange(Arrays.AsList<Node.LNAction>(actions));
			ease._duration = 0f;
			ease._easing = easing;
			ease._index = 0;
			for (int i = 0; i < size; i++) {
				actions[i]._easing = easing;
				ease._duration += actions[i].GetDuration();
			}
			return ease;
		}
	
		public override void SetTarget(LNNode node) {
			base._firstTick = true;
			this._index = 0;
			base._isEnd = false;
			base._target = node;
			if (this._actionList.Count > 0) {
				this._actionList[0].SetTarget(base._target);
			}
		}
	
		public override void Step(float dt) {
			if (this._index < this._actionList.Count) {
				do {
					this._actionList[this._index].Step(dt);
					if (this._actionList[this._index].IsEnd()) {
						dt = this._actionList[this._index].GetElapsed()
								- this._actionList[this._index].GetDuration();
						this._index++;
						if (this._index >= this._actionList.Count) {
							return;
						}
						this._actionList[this._index].SetTarget(base._target);
					}
					if (this._actionList[this._index].GetDuration() != 0f) {
						return;
					}
				} while (dt >= 0f);
			} else {
				base._isEnd = true;
			}
		}
	
		public override LNAction Copy() {
			return Action(_easing, _actionList);
		}
	}
}
