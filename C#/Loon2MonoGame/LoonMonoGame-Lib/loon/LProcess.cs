using loon.events;
using loon.geom;
using loon.utils;

namespace loon
{
   public class LProcess
{
		protected internal TArray<Updateable> resumes;

		protected internal TArray<Updateable> loads;

		protected internal TArray<Updateable> unloads;

		protected internal EmulatorListener emulatorListener;

		private EmulatorButtons emulatorButtons;

		private readonly ListMap<string, Screen> _screenMap;

		private readonly TArray<Screen> _screens;

		private bool isInstance;

		private int id;

		private bool _waitTransition;

		private bool _running;

		private Screen _currentScreen, _loadingScreen;

		private readonly SysInputFactory _currentInput;

		public EmulatorButtons GetEmulatorButtons()
		{
			return emulatorButtons;
		}

		public int GetWidth()
        {
			return -1;
        }
		public int GetHeight()
		{
			return -1;
		}

		private static void CallUpdateable(TArray<Updateable> list)
		{
			lock (typeof(LProcess))
			{
				TArray<Updateable> loadCache;
				lock (list)
				{
					loadCache = new TArray<Updateable>(list);
					list.Clear();
				}
				for (int i = 0, size = loadCache.size; i < size; i++)
				{
					Updateable r = loadCache.Get(i);
					if (r == null)
					{
						continue;
					}
					lock (r)
					{
						try
						{
							r.Action(null);
						}
						catch (System.Exception cause)
						{
							LSystem.Error("Updateable dispatch failure", cause);
						}
					}
				}
				loadCache = null;
			}
		}


		public virtual void AddResume(Updateable u)
		{
			lock (resumes)
			{
				resumes.Add(u);
			}
		}

		public virtual void RemoveResume(Updateable u)
		{
			lock (resumes)
			{
				resumes.Remove(u);
			}
		}

		// --- Load start ---//

		public virtual void AddLoad(Updateable u)
		{
			lock (loads)
			{
				loads.Add(u);
			}
		}

		public virtual bool ContainsLoad(Updateable u)
		{
			lock (loads)
			{
				return loads.Contains(u);
			}
		}

		public virtual void RemoveLoad(Updateable u)
		{
			lock (loads)
			{
				loads.Remove(u);
			}
		}

		public virtual void RemoveAllLoad()
		{
			lock (loads)
			{
				loads.Clear();
			}
		}

		public virtual void Load()
		{
			if (isInstance)
			{
				int count = loads.size;
				if (count > 0)
				{
					CallUpdateable(loads);
				}
			}
		}

		public virtual void AddUnLoad(Updateable u)
		{
			lock (unloads)
			{
				unloads.Add(u);
			}
		}

		public virtual bool ContainsUnLoad(Updateable u)
		{
			lock (unloads)
			{
				return unloads.Contains(u);
			}
		}

		public virtual void RemoveUnLoad(Updateable u)
		{
			lock (unloads)
			{
				unloads.Remove(u);
			}
		}

		public virtual void RemoveAllUnLoad()
		{
			lock (unloads)
			{
				unloads.Clear();
			}
		}

		public virtual void Unload()
		{
			if (isInstance)
			{
				int count = unloads.size;
				if (count > 0)
				{
					CallUpdateable(unloads);
				}
			}
		}


		public virtual void Resume()
		{
			if (isInstance)
			{
				int count = resumes.size;
				if (count > 0)
				{
					CallUpdateable(resumes);
				}
				_currentInput.Reset();
				_currentScreen.Resume();
			}
		}


		public virtual void Pause()
		{
			if (isInstance)
			{
				_currentInput.Reset();
				_currentScreen.Pause();
			}
		}
		public virtual void ResetTouch()
		{
			_currentInput.ResetSysTouch();
		}

		public float GetX()
        {
			return -1;
        }

		public float GetY()
		{
			return -1;
		}

		public virtual float GetScaleX()
		{
			if (isInstance)
			{
				return _currentScreen.GetScaleX();
			}
			return 1f;
		}

		public virtual float GetScaleY()
		{
			if (isInstance)
			{
				return _currentScreen.GetScaleY();
			}
			return 1f;
		}

		public virtual float GetRotation()
		{
			if (isInstance)
			{
				return _currentScreen.GetRotation();
			}
			return 0;
		}

		public bool IsFlipX()
		{
			if (isInstance)
			{
				return _currentScreen.IsFlipX();
			}
			return false;
		}

		public bool IsFlipY()
		{
			if (isInstance)
			{
				return _currentScreen.IsFlipY();
			}
			return false;
		}

		private static readonly Vector2f _tmpLocaltion = new Vector2f();

		public Vector2f ConvertXY(float x, float y)
		{
			float newX = ((x - GetX()) / (LSystem.GetScaleWidth()));
			float newY = ((y - GetY()) / (LSystem.GetScaleHeight()));
			if (isInstance && _currentScreen.IsTxUpdate())
			{
				float oldW = GetWidth();
				float oldH = GetHeight();
				float newW = GetWidth() * GetScaleX();
				float newH = GetHeight() * GetScaleY();
				float offX = oldW / 2f - newW / 2f;
				float offY = oldH / 2f - newH / 2f;
				float nx = (newX - offX);
				float ny = (newY - offY);
				int r = (int)GetRotation();
				switch (r)
				{
					case -90:
						offX = oldH / 2f - newW / 2f;
						offY = oldW / 2f - newH / 2f;
						nx = (newX - offY);
						ny = (newY - offX);
						_tmpLocaltion.Set(nx / GetScaleX(), ny / GetScaleY()).RotateSelf(-90);
						_tmpLocaltion.Set(-(_tmpLocaltion.x - GetWidth()), MathUtils.Abs(_tmpLocaltion.y));
						break;
					case 0:
					case 360:
						_tmpLocaltion.Set(nx / GetScaleX(), ny / GetScaleY());
						break;
					case 90:
						offX = oldH / 2f - newW / 2f;
						offY = oldW / 2f - newH / 2f;
						nx = (newX - offY);
						ny = (newY - offX);
						_tmpLocaltion.Set(nx / GetScaleX(), ny / GetScaleY()).RotateSelf(90);
						_tmpLocaltion.Set(-_tmpLocaltion.x, MathUtils.Abs(_tmpLocaltion.y - GetHeight()));
						break;
					case -180:
					case 180:
						_tmpLocaltion.Set(nx / GetScaleX(), ny / GetScaleY()).RotateSelf(GetRotation()).AddSelf(GetWidth(),
								GetHeight());
						break;
					default: // 原则上不处理非水平角度的触点
						_tmpLocaltion.Set(newX, newY);
						break;
				}
			}
			else
			{
				_tmpLocaltion.Set(newX, newY);
			}
			if (IsFlipX() || IsFlipY())
			{
				HelperUtils.Local2Global(IsFlipX(), IsFlipY(), GetWidth() / 2, GetHeight() / 2, _tmpLocaltion.x,
						_tmpLocaltion.y, _tmpLocaltion);
				return _tmpLocaltion;
			}
			return _tmpLocaltion;
		}


		public virtual void KeyDown(GameKey e)
		{
			if (isInstance)
			{
				_currentScreen.KeyPressed(e);
			}
		}

		public virtual void KeyUp(GameKey e)
		{
			if (isInstance)
			{
				_currentScreen.KeyReleased(e);
			}
		}

		public virtual void KeyTyped(GameKey e)
		{
			if (isInstance)
			{
				_currentScreen.KeyTyped(e);
			}
		}

		public virtual void MousePressed(GameTouch e)
		{
			if (isInstance)
			{
				_currentScreen.MousePressed(e);
			}
		}

		public virtual void MouseReleased(GameTouch e)
		{
			if (isInstance)
			{
				_currentScreen.MouseReleased(e);
			}
		}

		public virtual void MouseMoved(GameTouch e)
		{
			if (isInstance)
			{
				_currentScreen.MouseMoved(e);
			}
		}

		public virtual void MouseDragged(GameTouch e)
		{
			if (isInstance)
			{
				_currentScreen.MouseDragged(e);
			}
		}

	}
}
