namespace loon.utils.reply
{
    public abstract class MappedAct<T> : AbstractAct<T>
{

        protected internal Connection _conn;

        protected internal abstract Connection Connect();

        protected internal override void ConnectionAdded()
    {
        base.ConnectionAdded();
        if (_conn == null)
        {
            _conn = Connect();
        }
    }

    protected internal override void ConnectionRemoved()
    {
        base.ConnectionRemoved();
        if (!HasConnections() && _conn != null)
        {
            _conn.Close();
            _conn = null;
        }
    }

}

}
