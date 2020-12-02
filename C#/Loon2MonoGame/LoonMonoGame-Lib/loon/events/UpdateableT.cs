namespace loon.events
{
    public abstract class UpdateableT<T> : Updateable
    {

        protected T _context;

        protected string _name;

        public UpdateableT(T context, string name)
        {
            this._context = context;
            this._name = name;
        }

        public abstract void Action(object a);

        public T GetContext()
        {
            return _context;
        }

        public string GetName()
        {
            return _name;
        }

    }

}
