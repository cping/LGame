using static loon.utils.reply.Bypass;

namespace loon.utils.reply
{

    public class Cons : Connection
    {

        protected internal abstract class ListenerRef
        {
            public abstract bool IsWeak();

            public abstract void Defang(GoListener def);

            public abstract GoListener Get(Cons cons);
        }

        protected internal class StrongRef : ListenerRef
        {
            private GoListener _lner;

            public StrongRef(GoListener lner)
            {
                _lner = lner;
            }


            public override bool IsWeak()
            {
                return false;
            }

            public override void Defang(GoListener def)
            {
                _lner = def;
            }

            public override GoListener Get(Cons cons)
            {
                return _lner;
            }
        }

        private Bypass _owner;
        private ListenerRef _ref;
        private bool _oneShot = false;
        private bool _closed = false;
        private int _priority;

        public Cons next;

        public bool OneShot()
        {
            return _oneShot;
        }

        public GoListener Listener()
        {
            return _ref.Get(this);
        }


        public override void Close()
        {
            if (_owner != null)
            {
                _ref.Defang(_owner.DefaultListener());
                _owner.Disconnect(this);
                _owner = null;
            }
            _closed = true;
        }

        public bool IsClosed()
        {
            return _closed;
        }


        public override Connection Once()
        {
            _oneShot = true;
            return this;
        }


        public override Connection SetPriority(int priority)
        {
            if (_owner == null)
            {
                throw new LSysException("cannot change priority of disconnected connection.");
            }
            //_owner.Disconnect(this);
            next = null;
            _priority = priority;
            //_owner.AddCons(this);
            return this;
        }

        protected internal Cons(Bypass owner, GoListener listener)
        {
            _owner = owner;
            _ref = new StrongRef(listener);
        }


        protected internal static Cons Insert(Cons head, Cons cons)
        {
            if (head == null)
            {
                return cons;
            }
            else if (cons._priority > head._priority)
            {
                cons.next = head;
                return cons;
            }
            else
            {
                head.next = Insert(head.next, cons);
                return head;
            }
        }

        protected internal static Cons Remove(Cons head, Cons cons)
        {
            if (head == null)
            {
                return head;
            }
            if (head == cons)
            {
                return head.next;
            }
            head.next = Remove(head.next, cons);
            return head;
        }

        protected internal static Cons RemoveAll(Cons head, GoListener listener)
        {
            if (head == null)
            {
                return null;
            }
            if (head.Listener() == listener)
            {
                return RemoveAll(head.next, listener);
            }
            head.next = RemoveAll(head.next, listener);
            return head;
        }

        public override string ToString()
        {
            StringKeyValue builder = new StringKeyValue("Cons");
            builder.Kv("owner", _owner)
            .Comma()
            .Kv("priority", _priority)
            .Comma()
            .Kv("listener", Listener())
            .Comma()
            .Kv("hasNext", (next != null))
            .Comma()
            .Kv("oneShot", OneShot());
            return builder.ToString();
        }

    }

}
