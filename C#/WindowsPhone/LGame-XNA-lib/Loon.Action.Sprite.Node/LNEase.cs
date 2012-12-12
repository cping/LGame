namespace Loon.Action.Sprite.Node
{
    public class LNEase : LNAction
    {
        protected LNAction _action;

        internal LNEase()
        {

        }

        public static LNEase Action(Easing e, LNAction act)
        {
            LNEase action = new LNEase();
            action._duration = act._duration;
            action._action = act;
            act._easing = e;
            return action;
        }

        public override void SetTarget(LNNode node)
        {
            base.SetTarget(node);
            if (_action != null)
            {
                _action.SetTarget(node);
            }
        }

        public override void Step(float dt)
        {
            if (_action != null)
            {
                _action.Step(dt);
                _isEnd = _action.IsEnd();
            }
        }

        public override LNAction Copy()
        {
            return Action(_easing, _action);
        }
    }
}
