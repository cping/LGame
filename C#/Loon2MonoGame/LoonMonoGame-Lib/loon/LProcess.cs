using loon.events;
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

	}
}
