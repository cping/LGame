package org.test.common;

import loon.core.graphics.opengl.LTexture;


public class SpriteAnim extends Sprite
{
	private boolean m_bAnimIsForward;
	private boolean m_bPlaying;
	private int m_iAnimDuration;
	private int m_iAnimLoopType;
	private int m_iCurrentFrame;
	private int m_iCurrentFrameDuration;
	private int m_iNumFrames;
	private int m_iTotalCycles;
	private java.util.HashMap<Integer, LTexture> m_TextureArray;
	private java.util.HashMap<Integer, String> m_TextureNameArray;

	public SpriteAnim()
	{
		super();
		this.m_iAnimLoopType = 1;
		this.m_bAnimIsForward = true;
		this.m_iCurrentFrame = -1;
		this.m_TextureNameArray = new java.util.HashMap<Integer, String>();
		this.m_TextureArray = new java.util.HashMap<Integer, LTexture>();
	}

	@Override
	public void AddTexture(LTexture pTexRes)
	{
		this.m_TextureNameArray.put(this.m_iNumFrames, pTexRes.getFileName());
		this.m_TextureArray.put(this.m_iNumFrames, pTexRes);
		this.m_iNumFrames++;
		if (super.m_pTexture == null)
		{
			super.m_pTexture = pTexRes;
		}
	}

	@Override
	public void AddTextureByName(String texname, boolean bAutoLoad)
	{
		LTexture textured = null;
		if (bAutoLoad)
		{
			textured = new LTexture("assets/"+texname+".png");
		}
		this.m_TextureNameArray.put(this.m_iNumFrames, texname);
		this.m_TextureArray.put(this.m_iNumFrames, textured);
		this.m_iNumFrames++;
		if (super.m_pTexture == null)
		{
			super.m_pTexture = textured;
		}
	}

	public final int CountObjectsToLoad()
	{
		int num = 0;
		for (int i = 0; i < this.m_iNumFrames; i++)
		{
			if (this.m_TextureArray.get(i) == null)
			{
				num++;
			}
		}
		return num;
	}

	public final void Finish()
	{
		this.m_iTotalCycles++;
		this.m_iCurrentFrame = this.m_iNumFrames - 1;
		this.m_iCurrentFrameDuration = this.GetAnimTimePerFrame();
	}

	public final int GetAnimDuration()
	{
		return this.m_iAnimDuration;
	}

	public final boolean GetAnimIsForward()
	{
		return this.m_bAnimIsForward;
	}

	public final int GetAnimLoopType()
	{
		return this.m_iAnimLoopType;
	}

	public final int GetAnimTimePerFrame()
	{
		return (this.m_iAnimDuration / this.m_iNumFrames);
	}

	public final int GetCurrentFrame()
	{
		return this.m_iCurrentFrame;
	}

	public final int GetCurrentFrameDuration()
	{
		return this.m_iCurrentFrameDuration;
	}

	public final int GetFramesCount()
	{
		return this.m_iNumFrames;
	}

	public final LTexture GetTexture(int iAtIndex)
	{
		if ((iAtIndex >= 0) && (iAtIndex < this.m_iNumFrames))
		{
			return this.m_TextureArray.get(iAtIndex);
		}
		return null;
	}

	public final int GetTotalCycles()
	{
		return this.m_iTotalCycles;
	}

	public final boolean IsAnimFinished()
	{
		return (((this.m_iAnimLoopType == 2) || (this.m_iAnimLoopType == 4)) && (this.m_iTotalCycles > 0));
	}

	public final boolean IsLastFrame()
	{
		return (this.m_iCurrentFrame == (this.m_iNumFrames - 1));
	}

	@Override
	public boolean IsLoaded()
	{
		for (int i = 0; i < this.m_iNumFrames; i++)
		{
			if (this.m_TextureArray.get(i) == null)
			{
				return false;
			}
		}
		return true;
	}

	public final boolean IsPlaying()
	{
		return this.m_bPlaying;
	}

	public final void Play()
	{
		this.m_bPlaying = true;
	}

	@Override
	public void Release()
	{
		this.m_TextureArray.clear();
		super.m_pTexture = null;
	}

	@Override
	public void Reload()
	{
		for (int i = 0; i < this.m_iNumFrames; i++)
		{
			this.m_TextureArray.put(i,new LTexture("assets/"+this.m_TextureNameArray.get(i)+".png"));
		}
		this.SetCurrentFrame(this.m_iCurrentFrame, false);
	}

	public final void RemoveAllTextures()
	{
		this.m_iNumFrames = 0;
		this.m_TextureNameArray.clear();
		this.m_TextureArray.clear();
	}

	public final void ReplaceTexture(int iAtIndex, LTexture pTexRes)
	{
		this.m_TextureNameArray.put(iAtIndex, pTexRes.getFileName());
		this.m_TextureArray.put(iAtIndex, pTexRes);
	}

	@Override
	public void Reset()
	{
		this.m_bPlaying = false;
		this.m_iTotalCycles = 0;
		this.SetCurrentFrame(0, true);
		super.Reset();
	}

	public final void SetAnimDuration(int animDuration)
	{
		this.m_iAnimDuration = (short) animDuration;
	}

	public final void SetAnimIsForward(boolean bIsForward)
	{
		this.m_bAnimIsForward = bIsForward;
	}

	public final void SetAnimLoopType(int loopType)
	{
		this.m_iAnimLoopType = loopType;
	}

	public final void SetCurrentFrame(int newCurFrame, boolean bResetFrameDuration)
	{
		if ((newCurFrame >= 0) && (newCurFrame < this.m_iNumFrames))
		{
			this.m_iCurrentFrame = newCurFrame;
			if (bResetFrameDuration)
			{
				this.m_iCurrentFrameDuration = 0;
			}
			super.m_pTexture = this.GetTexture(this.m_iCurrentFrame);
		}
	}

	public final void SetCurrentFrameByTexName(String texName, boolean bResetFrameDuration)
	{
		for (int i = 0; i < this.m_iNumFrames; i++)
		{
			if (this.m_TextureNameArray.get(i).compareTo(texName) == 0)
			{
				this.SetCurrentFrame(i, bResetFrameDuration);
				return;
			}
		}
	}

	public final void SetCurrentFrameDuration(short newCurFrameDuration)
	{
		this.m_iCurrentFrameDuration = newCurFrameDuration;
	}

	public final void Stop()
	{
		this.m_bPlaying = false;
	}

	@Override
	public void Tick(int deltaMS)
	{
		super.Tick(deltaMS);
		if (!this.IsAnimFinished() && this.m_bPlaying)
		{
			this.m_iCurrentFrameDuration += (short) deltaMS;
			if ((this.m_iAnimLoopType != 0) && (this.m_iCurrentFrameDuration >= this.GetAnimTimePerFrame()))
			{
				if (this.m_bAnimIsForward)
				{
					this.m_iCurrentFrame++;
				}
				else
				{
					this.m_iCurrentFrame--;
				}
				if (this.m_iCurrentFrame >= this.m_iNumFrames)
				{
					if ((this.m_iAnimLoopType == 3) || (this.m_iAnimLoopType == 4))
					{
						this.m_bAnimIsForward = false;
						this.m_iCurrentFrame = (short)(this.m_iNumFrames - 2);
					}
					else if (this.m_iAnimLoopType == 2)
					{
						this.m_iCurrentFrame = (short)(this.m_iNumFrames - 1);
					}
					else
					{
						this.m_iCurrentFrame = 0;
					}
					if ((this.m_iAnimLoopType != 3) && (this.m_iAnimLoopType != 4))
					{
						this.m_iTotalCycles++;
					}
				}
				else if (this.m_iCurrentFrame < 0)
				{
					this.m_bAnimIsForward = true;
					this.m_iCurrentFrame = 1;
					if ((this.m_iAnimLoopType == 3) || (this.m_iAnimLoopType == 4))
					{
						this.m_iTotalCycles++;
					}
				}
				this.SetCurrentFrame(this.m_iCurrentFrame, true);
			}
		}
	}

	@Override
	public void Unload()
	{
		for (int i = 0; i < this.m_iNumFrames; i++)
		{
			this.m_TextureArray.put(i, null);
		}
	}

	public enum EAnimLoopType
	{
		AnimLoop_Frame,
		AnimLoop_Forever,
		AnimLoop_Once,
		AnimLoop_PingPong,
		AnimLoop_PingPongOnce;

		public int getValue()
		{
			return this.ordinal();
		}

		public static EAnimLoopType forValue(int value)
		{
			return values()[value];
		}
	}
}