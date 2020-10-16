using loon.canvas;

namespace loon.font
{
	public interface FontSet<T>
	{

		 T SetFont(IFont font);

		 IFont GetFont();

		 T SetFontColor(LColor color);

		 LColor GetFontColor();
	}
}
