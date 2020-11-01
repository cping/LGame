using loon.utils;

namespace loon
{
    class SaveBatchImpl : Batch {

	protected readonly Save storage;
	private ObjectMap<string, string> updates = new ObjectMap<string, string>();

	public SaveBatchImpl(Save storage)
	{
		this.storage = storage;
	}

	public void SetItem(string key, string data)
	{
		updates.Put(key, data);
	}

	
	public void RemoveItem(string key)
	{
		updates.Put(key, null);
	}

	
	public void Commit()
	{
		try
		{
			OnBeforeCommit();
			foreach (ObjectMap<string, string>.Entry<string, string> entry in updates.GetEntries())
			{
				string key = entry.key, data = entry.value;
				if (data == null)
					RemoveImpl(key);
				else
					SetImpl(key, data);
			}
			OnAfterCommit();

		}
		finally
		{
			updates = null;
		}
	}

	protected void OnBeforeCommit()
	{
	}

	protected void SetImpl(string key, string data)
	{
		storage.SetItem(key, data);
	}

	protected void RemoveImpl(string key)
	{
		storage.RemoveItem(key);
	}

	protected void OnAfterCommit()
	{
	}
}
}
