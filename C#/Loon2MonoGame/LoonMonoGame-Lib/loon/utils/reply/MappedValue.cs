namespace loon.utils.reply
{

    public abstract class MappedValue<T> : AbstractValue<T>
{


		protected internal Connection _connection;

		protected internal abstract Connection Connect();

		protected internal virtual void Disconnect()
		{
			if (_connection != null)
			{
				_connection.Close();
				_connection = null;
			}
		}

		protected internal virtual void Reconnect()
		{
			Disconnect();
			_connection = Connect();
		}

		protected internal override void ConnectionAdded()
		{
			base.ConnectionAdded();
			if (_connection == null)
			{
				_connection = Connect();
			}
		}

		protected internal override void ConnectionRemoved()
		{
			base.ConnectionRemoved();
			if (!HasConnections())
			{
				Disconnect();
			}
		}

	}

}
