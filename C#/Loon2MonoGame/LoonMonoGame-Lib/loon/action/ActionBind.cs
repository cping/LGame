using loon.action.map;
using loon.canvas;
using loon.geom;

namespace loon.action
{
	public interface ActionBind
	{

		 Field2D GetField2D();

		 void SetVisible(bool v);

		 bool IsVisible();

		 int X();

		 int Y();

		 float GetX();

		 float GetY();

		 float GetScaleX();

		 float GetScaleY();

		 void SetColor(LColor color);

		 LColor GetColor();

		 void SetScale(float sx, float sy);

		 float GetRotation();

		 void SetRotation(float r);

		 float GetWidth();

		 float GetHeight();

		 float GetAlpha();

		 void SetAlpha(float alpha);

		 void SetLocation(float x, float y);

		 void SetX(float x);

		 void SetY(float y);

		 bool IsBounded();

		 bool IsContainer();

		 bool InContains(float x, float y, float w, float h);

		 RectBox GetRectBox();

		 float GetContainerWidth();

		 float GetContainerHeight();

		 ActionTween SelfAction();

		 bool isActionCompleted();
	}

}
