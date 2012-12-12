using Loon.Core.Geom;
using Loon.Core;
using Loon.Utils;
namespace Loon.Action.Sprite.Node {
	
	public class LNFollow : LNAction {
	
		internal LNFollow() {
	
		}
	
		protected internal LNNode _followedNode;
	
		protected internal bool _boundarySet;
	
		protected internal bool _boundaryFullyCovered;
	
		internal RectBox winRect;
	
		internal Vector2f halfScreenSize;
	
		internal Vector2f fullScreenSize;
	
		internal float leftBoundary;
	
		internal float rightBoundary;
	
		internal float topBoundary;
	
		internal float bottomBoundary;
	
		public void SetBoundarySet(bool flag) {
			_boundarySet = flag;
		}
	
		public bool GetBoundarySet() {
			return _boundarySet;
		}
	
		public static LNFollow Action(LNNode followedNode) {
			LNFollow follow = new LNFollow();
			follow._followedNode = followedNode;
			follow._boundarySet = false;
			follow._boundaryFullyCovered = false;
	
			follow.winRect = LSystem.screenRect;
	
			follow.fullScreenSize = new Vector2f(follow.winRect.width,
					follow.winRect.height);
			follow.halfScreenSize = Vector2f.Mul(follow.fullScreenSize, 0.5f);
			return follow;
		}
	
		public static LNFollow Action(LNNode followedNode, RectBox rect) {
			LNFollow follow = new LNFollow();
			follow._followedNode = followedNode;
			follow._boundarySet = true;
			follow._boundaryFullyCovered = false;
	
			follow.winRect = LSystem.screenRect;
			follow.fullScreenSize = new Vector2f(follow.winRect.width,
					follow.winRect.height);
            follow.halfScreenSize = follow.fullScreenSize.Mul(0.5f);
	
			follow.leftBoundary = -((rect.x + rect.width) - follow.fullScreenSize.x);
			follow.rightBoundary = -rect.x;
			follow.topBoundary = -rect.y;
			follow.bottomBoundary = -((rect.y + rect.height) - follow.fullScreenSize.y);
	
			if (follow.rightBoundary < follow.leftBoundary) {
				follow.rightBoundary = follow.leftBoundary = (follow.leftBoundary + follow.rightBoundary) / 2;
			}
	
			if (follow.topBoundary < follow.bottomBoundary) {
				follow.topBoundary = follow.bottomBoundary = (follow.topBoundary + follow.bottomBoundary) / 2;
			}
			if ((follow.topBoundary == follow.bottomBoundary)
					&& (follow.leftBoundary == follow.rightBoundary)) {
				follow._boundaryFullyCovered = true;
			}
			return follow;
		}
	
		public override void Step(float dt) {
			if (_boundarySet) {
				if (_boundaryFullyCovered) {
					return;
				}
				Vector2f pos = halfScreenSize.Sub(_followedNode.GetPosition());
				base._target.SetPosition(
						MathUtils.Clamp(pos.x, leftBoundary, rightBoundary),
						MathUtils.Clamp(pos.y, bottomBoundary, topBoundary));
			} else {
				base._target.SetPosition(halfScreenSize.Sub(_followedNode
						.GetPosition()));
			}
		}
	
		public override void Update(float time) {
			if (_followedNode._isClose) {
				base._isEnd = true;
			}
		}
	
		public override LNAction Copy() {
			return Action(_followedNode, winRect);
		}
	
	}
}
