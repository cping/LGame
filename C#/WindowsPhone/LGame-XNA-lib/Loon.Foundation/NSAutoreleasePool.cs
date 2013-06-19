namespace Loon.Foundation
{

    public class NSAutoreleasePool : NSObject
    {

        internal bool _enable = true;

        internal static int _id;

        internal static NSAutoreleasePool _instance;

        internal NSArray _arrays;

        public NSAutoreleasePool()
        {
            _arrays = new NSArray();
            Init();
        }

        public NSAutoreleasePool GetMainAutoreleasePool()
        {
            return _instance;
        }

        private void Init()
        {
            if (_instance == null)
            {
                _instance = this;
            }
            _id++;
        }

        public void AddObject(NSObject o)
        {
            this._arrays.AddObject(o);
        }

        public void RemoveObject(NSObject o)
        {
            this._arrays.RemoveObject(o);
        }

        public void EnableFreedObjectCheck(bool enable)
        {
            this._enable = enable;
        }

        public bool IsEnableFreedObjectCheck()
        {
            return _enable;
        }

        public string Drain()
        {
            string res = ToSequence();
            _arrays._list.Clear();
            return res;
        }

        public static int GetID()
        {
            return _id;
        }

        public override void Dispose()
        {
            Drain();
        }

        protected override internal void AddSequence(System.Text.StringBuilder sbr, string indent)
        {
            _arrays.AddSequence(sbr, indent + "  ");
        }

    }
}
