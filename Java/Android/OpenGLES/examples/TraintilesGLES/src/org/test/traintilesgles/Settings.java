package org.test.traintilesgles;

import java.util.ArrayList;


public class Settings
{
	public java.util.ArrayList<Integer> m_levels;
	public boolean m_sounds;

	public Settings()
	{
		  this.m_levels = new ArrayList<Integer>();
          for (int i = 0; i < 150; i++)
          {
              this.m_levels.add(-1);
          }
	}

	public final void Save()
	{

	}

	public void setDefaultSettings()
	{
		this.m_sounds = true;
		for (int i = 0; i < 150; i++)
		{
			this.m_levels.set(i, -1);
		}
		this.Save();
	}
}