using loon.geom;
using loon.utils;

namespace loon.events
{
   public class LTouchLocation
	{

		protected int id;

		private Vector2f position;

		private Vector2f previousPosition;

		private LTouchLocationState state = LTouchLocationState.Invalid;

		private LTouchLocationState previousState = LTouchLocationState.Invalid;

		private float pressure;

		private float previousPressure;

		public bool IsDrag()
		{
			return SysTouch.IsDrag()
					&& (previousState == LTouchLocationState.Dragged && state == LTouchLocationState.Dragged);
		}

		public bool IsDown()
		{
			return SysTouch.IsDown()
					&& (previousState == LTouchLocationState.Pressed && (state == LTouchLocationState.Pressed || state == LTouchLocationState.Dragged));
		}

		public bool IsUp()
		{
			return SysTouch.IsUp()
					&& (previousState == LTouchLocationState.Pressed || previousState == LTouchLocationState.Dragged)
					&& (state == LTouchLocationState.Released);
		}

		public int GetId()
		{
			return id;
		}

		public Vector2f GetPosition()
		{
			return position;
		}

		public void SetPosition(float x, float y)
		{
			previousPosition.Set(position);
			position.Set(x, y);
		}

		public void SetPosition(Vector2f value)
		{
			previousPosition.Set(position);
			position.Set(value);
		}

		public int GetX()
		{
			return position.X();
		}

		public int GetY()
		{
			return position.Y();
		}

		public float X()
		{
			return position.x;
		}

		public float Y()
		{
			return position.y;
		}

		public float GetPressure()
		{
			return pressure;
		}

		public float GetPrevPressure()
		{
			return previousPressure;
		}

		public Vector2f GetPrevPosition()
		{
			return previousPosition;
		}

		public void SetPrevPosition(Vector2f value)
		{
			previousPosition.Set(value);
		}

		public LTouchLocationState GetState()
		{
			return state;
		}

		public void SetState(LTouchLocationState value)
		{
			previousState = state;
			state = value;
		}

		public LTouchLocationState GetPrevState()
		{
			return previousState;
		}

		public void SetPrevState(LTouchLocationState value)
		{
			previousState = value;
		}

		public LTouchLocation() : this(0, LTouchLocationState.Invalid, new Vector2f(0, 0),
					LTouchLocationState.Invalid, new Vector2f(0, 0))
		{
		
		}

		public LTouchLocation(int aId, LTouchLocationState aState,
				Vector2f aPosition, LTouchLocationState aPreviousState,
				Vector2f aPreviousPosition)
		{
			id = aId;
			position = aPosition;
			previousPosition = aPreviousPosition;
			state = aState;
			previousState = aPreviousState;
			pressure = 0.0f;
			previousPressure = 0.0f;
		}

		public LTouchLocation(int aId, LTouchLocationState aState,
				Vector2f aPosition)
		{
			id = aId;
			position = aPosition;
			previousPosition = Vector2f.ZERO();
			state = aState;
			previousState = LTouchLocationState.Invalid;
			pressure = 0.0f;
			previousPressure = 0.0f;
		}

		public LTouchLocation(int aId, LTouchLocationState aState, float x, float y)
		{
			id = aId;
			position = new Vector2f(x, y);
			previousPosition = Vector2f.ZERO();
			state = aState;
			previousState = LTouchLocationState.Invalid;
			pressure = 0.0f;
			previousPressure = 0.0f;
		}

		public LTouchLocation(int aId, LTouchLocationState aState,
				Vector2f aPosition, float aPressure,
				LTouchLocationState aPreviousState, Vector2f aPreviousPosition,
				float aPreviousPressure)
		{
			id = aId;
			position = aPosition;
			previousPosition = aPreviousPosition;
			state = aState;
			previousState = aPreviousState;
			pressure = aPressure;
			previousPressure = aPreviousPressure;
		}

		public LTouchLocation(int aId, LTouchLocationState aState,
				Vector2f aPosition, float aPressure)
		{
			id = aId;
			position = aPosition;
			previousPosition = Vector2f.ZERO();
			state = aState;
			previousState = LTouchLocationState.Invalid;
			pressure = aPressure;
			previousPressure = 0.0f;
		}

		public bool TryGetPreviousLocation(
				RefObject<LTouchLocation> aPreviousLocation)
		{
			if (aPreviousLocation.argvalue == null)
			{
				aPreviousLocation.argvalue = new LTouchLocation();
			}
			if (previousState == LTouchLocationState.Invalid)
			{
				aPreviousLocation.argvalue.id = -1;
				aPreviousLocation.argvalue.state = LTouchLocationState.Invalid;
				aPreviousLocation.argvalue.position = Vector2f.ZERO();
				aPreviousLocation.argvalue.previousState = LTouchLocationState.Invalid;
				aPreviousLocation.argvalue.previousPosition = Vector2f.ZERO();
				aPreviousLocation.argvalue.pressure = 0.0f;
				aPreviousLocation.argvalue.previousPressure = 0.0f;
				return false;
			}
			else
			{
				aPreviousLocation.argvalue.id = this.id;
				aPreviousLocation.argvalue.state = this.previousState;
				aPreviousLocation.argvalue.position = this.previousPosition.Cpy();
				aPreviousLocation.argvalue.previousState = LTouchLocationState.Invalid;
				aPreviousLocation.argvalue.previousPosition = Vector2f.ZERO();
				aPreviousLocation.argvalue.pressure = this.previousPressure;
				aPreviousLocation.argvalue.previousPressure = 0.0f;
				return true;
			}
		}

	public override bool Equals(object obj)
		{
			bool result = false;
			if (obj is LTouchLocation t) {
				result = Equals(t);
			}
			return result;
		}

		public bool Equals(LTouchLocation other)
		{
			return (id == other.id)
					&& (this.GetPosition().Equals(other.GetPosition()))
					&& (this.previousPosition.Equals(other.previousPosition));
		}

	public override int GetHashCode()
		{
			return id;
		}


	public override string ToString()
		{
			return "Touch id:" + id + " state:" + state + " position:" + position
					+ " pressure:" + pressure + " prevState:" + previousState
					+ " prevPosition:" + previousPosition + " previousPressure:"
					+ previousPressure;
		}

		public LTouchLocation Cpy()
		{
			LTouchLocation varCopy = new LTouchLocation();

			varCopy.id = this.id;
			varCopy.position.Set(this.position);
			varCopy.previousPosition.Set(this.previousPosition);
			varCopy.state = this.state;
			varCopy.previousState = this.previousState;
			varCopy.pressure = this.pressure;
			varCopy.previousPressure = this.previousPressure;

			return varCopy;
		}

	}
}
