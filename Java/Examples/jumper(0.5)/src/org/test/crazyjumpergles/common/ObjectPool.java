package org.test.crazyjumpergles.common;

public class ObjectPool<T>
{
	private java.util.ArrayList<T> m_FreeList = new java.util.ArrayList<T>();
	
	private java.util.ArrayList<T> m_UsedList = new java.util.ArrayList<T>();

	public final void AddObject(T pObject)
	{
		this.m_FreeList.add(pObject);
	}

	public final java.util.ArrayList<T> GetFirstFree()
	{
		return this.m_FreeList;
	}

	public final T GetNextFree()
	{
		T item = null;
		if (this.m_FreeList.size() > 0)
		{
			item = this.m_FreeList.remove(0);
		}
		else
		{
			item = this.m_UsedList.remove(0);
		}
		this.m_UsedList.add(item);
		return item;
	}

	public final int GetUsedCount()
	{
		return this.m_UsedList.size();
	}

	public final java.util.ArrayList<T> GetUsedList()
	{
		return this.m_UsedList;
	}

	public final void Release()
	{
		while (this.m_UsedList.size() != 0)
		{
			T item = this.m_UsedList.remove(0);
			this.m_FreeList.add(item);
		}
	}

	public final void Release(T pObject)
	{
		this.m_UsedList.remove(pObject);
		this.m_FreeList.remove(pObject);
		this.m_FreeList.add(pObject);
	}
}