using loon.events;

namespace loon.utils.reply
{
    public abstract class UnitPort : Port<object>, Updateable
    {

        internal class ToPortImpl : UnitPort
        {
            private readonly Updateable update;

            public ToPortImpl(Updateable u)
            {
                this.update = u;
            }

            public override void OnEmit()
            {
                update.Action(this);
            }
        }

        internal class AndThenImpl : UnitPort
        {
            private readonly UnitPort after;
            private readonly UnitPort before;

            public AndThenImpl(UnitPort a, UnitPort b)
            {
                this.after = a;
                this.before = b;
            }

            public override void OnEmit()
            {
                before.OnEmit();
                after.OnEmit();
            }
        }

        public static UnitPort ToPort(Updateable update)
        {
            return new ToPortImpl(update);
        }
        public UnitPort AndThen(UnitPort after)
        {
            return new AndThenImpl(after, this);
        }

        public override void OnEmit(object e)
        {
            OnEmit();
        }

        public abstract void OnEmit();
        public void Action(object o)
        {
            OnEmit();
        }
    }
}
