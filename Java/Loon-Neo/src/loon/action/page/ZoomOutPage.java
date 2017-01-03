package loon.action.page;

import loon.Screen;
import loon.utils.MathUtils;

public class ZoomOutPage extends BasePage{

	public void onTransform(Screen screen, float position) {
        final float scale = 1f + MathUtils.abs(position);
        screen.setScaleX(scale);
        screen.setScaleY(scale);
        screen.setPivotX(screen.getWidth() * 0.5f);
        screen.setPivotY(screen.getWidth() * 0.5f);
        screen.setAlpha(position < -1f || position > 1f ? 0f : 1f - (scale - 1f));
        if(position < -0.9){
        	screen.setX(screen.getWidth() * position);
        }
    }
}
