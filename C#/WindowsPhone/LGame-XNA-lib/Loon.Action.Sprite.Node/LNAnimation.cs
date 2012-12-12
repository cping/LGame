using Loon.Core;
using System;
using System.Collections.Generic;
using Loon.Utils;
namespace Loon.Action.Sprite.Node {
	
	
	public class LNAnimation : LRelease {
	
		protected internal float _duration;
	
		private List<LNFrameStruct> _fsList;
	
		protected internal string _name;
	
		private List<Single> _timeList;
	
		protected internal float _totalDuration;
	
		public LNAnimation() {
			this._fsList = new List<LNFrameStruct>();
			this._timeList = new List<Single>();
			this._totalDuration = 0f;
		}
	
		public LNAnimation(string aName, float duration) {
			this._fsList = new List<LNFrameStruct>();
			this._timeList = new List<Single>();
			this._totalDuration = 0f;
			this._name = aName;
			this._duration = duration;
		}
	
		public LNAnimation(string aName, float duration, params string[] lists) {
			this._fsList = new List<LNFrameStruct>();
			this._timeList = new List<Single>();
			this._name = aName;
			this._duration = duration;
			for (int i = 0; i < lists.Length; i++) {
				this.AddFrameStruct(lists[i]);
			}
		}
	
		public void AddFrameStruct(string fs) {
			this.AddFrameStruct(fs, this._duration);
		}
	
		public void AddFrameStruct(string fs, float time) {
			LNFrameStruct item = Node.LNDataCache.GetFrameStruct(fs);
			CollectionUtils.Add(this._fsList,item);
			CollectionUtils.Add(this._timeList,time);
			this._totalDuration += time;
		}
	
		public int FrameCount() {
			return this._fsList.Count;
		}
	
		public LNFrameStruct GetFrame(int idx) {
			return this._fsList[idx];
		}
	
		public LNFrameStruct GetFrameByTime(float Time) {
			if (Time == 0f) {
				return this._fsList[0];
			}
			Time *= this._totalDuration;
			for (int i = 0; i < this._timeList.Count; i++) {
				float num2 = (this._timeList[i]);
				if (Time > num2) {
					Time -= num2;
				} else {
					return this._fsList[i];
				}
			}
			return this._fsList[this._fsList.Count - 1];
		}
	
		public float GetFrameTime(int idx) {
			return (this._timeList[idx]);
		}
	
		public void SetAnimationTime(float total) {
			int count = this._timeList.Count;
			if (count > 0) {
				float item = total / ((float) count);
				CollectionUtils.Clear(this._timeList);
				for (int i = 0; i < count; i++) {
					CollectionUtils.Add(this._timeList,item);
				}
			}
		}
	
		public float GetDuration() {
			return this._duration;
		}
	
		public string GetName() {
			return this._name;
		}
	
		public void Dispose() {
			if (_fsList != null) {
				CollectionUtils.Clear(_fsList);
			}
		}
	}
}
