package loon.component.skin;

import loon.canvas.LColor;

public class SkinManager {

	private static SkinManager instance;

	public final static SkinManager make() {
		return new SkinManager();
	}

	public final static SkinManager get() {
		if (instance != null) {
			return instance;
		}
		synchronized (SkinManager.class) {
			if (instance == null) {
				instance = make();
			}
			return instance;
		}
	}

	private CheckBoxSkin checkBoxSkin;

	private ClickButtonSkin clickButtonSkin;

	private ControlSkin controlSkin;

	private MenuSkin menuSkin;

	private MessageSkin messageSkin;

	private ProgressSkin progressSkin;

	private ScrollBarSkin scrollBarSkin;

	private SliderSkin sliderSkin;

	private TableSkin tableSkin;

	private TextBarSkin textBarSkin;

	private TextListSkin textListSkin;

	private WindowSkin windowSkin;

	public CheckBoxSkin getCheckBoxSkin() {
		if (checkBoxSkin == null) {
			setCheckBoxSkin(CheckBoxSkin.def());
		}
		return checkBoxSkin;
	}

	public void setCheckBoxSkin(CheckBoxSkin checkBoxSkin) {
		this.checkBoxSkin = checkBoxSkin;
	}

	public ClickButtonSkin getClickButtonSkin() {
		if (clickButtonSkin == null) {
			setClickButtonSkin(ClickButtonSkin.def());
		}
		return clickButtonSkin;
	}

	public void setClickButtonSkin(ClickButtonSkin clickButtonSkin) {
		this.clickButtonSkin = clickButtonSkin;
	}

	public ControlSkin getControlSkin() {
		if (controlSkin == null) {
			setControlSkin(ControlSkin.def());
		}
		return controlSkin;
	}

	public void setControlSkin(ControlSkin controlSkin) {
		this.controlSkin = controlSkin;
	}

	public MenuSkin getMenuSkin() {
		if (menuSkin == null) {
			setMenuSkin(MenuSkin.def());
		}
		return menuSkin;
	}

	public void setMenuSkin(MenuSkin menuSkin) {
		this.menuSkin = menuSkin;
	}

	public MessageSkin getMessageSkin() {
		if (messageSkin == null) {
			setMessageSkin(MessageSkin.def());
		}
		return messageSkin;
	}

	public void setMessageSkin(MessageSkin messageSkin) {
		this.messageSkin = messageSkin;
	}

	public ProgressSkin getProgressSkin() {
		if (progressSkin == null) {
			setProgressSkin(ProgressSkin.def());
		}
		return progressSkin;
	}

	public void setProgressSkin(ProgressSkin progressSkin) {
		this.progressSkin = progressSkin;
	}

	public ScrollBarSkin getScrollBarSkin() {
		if (scrollBarSkin == null) {
			setScrollBarSkin(ScrollBarSkin.def());
		}
		return scrollBarSkin;
	}

	public void setScrollBarSkin(ScrollBarSkin scrollBarSkin) {
		this.scrollBarSkin = scrollBarSkin;
	}

	public SliderSkin getSliderSkin(LColor sliderColor, LColor barColor,
			boolean v) {
		if (sliderSkin == null) {
			return SliderSkin.def(sliderColor, barColor, v);
		}
		return sliderSkin;
	}

	public SliderSkin getSliderSkin() {
		return getSliderSkin(LColor.gray.darker(), LColor.white, false);
	}

	public void setSliderSkin(SliderSkin sliderSkin) {
		this.sliderSkin = sliderSkin;
	}

	public TableSkin getTableSkin() {
		if (tableSkin == null) {
			setTableSkin(TableSkin.def());
		}
		return tableSkin;
	}

	public void setTableSkin(TableSkin tableSkin) {
		this.tableSkin = tableSkin;
	}

	public TextBarSkin getTextBarSkin() {
		if (textBarSkin == null) {
			setTextBarSkin(TextBarSkin.def());
		}
		return textBarSkin;
	}

	public void setTextBarSkin(TextBarSkin textBarSkin) {
		this.textBarSkin = textBarSkin;
	}

	public TextListSkin getTextListSkin() {
		if (textListSkin == null) {
			setTextListSkin(TextListSkin.def());
		}
		return textListSkin;
	}

	public void setTextListSkin(TextListSkin textListSkin) {
		this.textListSkin = textListSkin;
	}

	public WindowSkin getWindowSkin() {
		if (windowSkin == null) {
			setWindowSkin(WindowSkin.def());
		}
		return windowSkin;
	}

	public void setWindowSkin(WindowSkin windowSkin) {
		this.windowSkin = windowSkin;
	}

	public void set(SkinManager skin) {
		this.checkBoxSkin = skin.checkBoxSkin;
		this.clickButtonSkin = skin.clickButtonSkin;
		this.controlSkin = skin.controlSkin;
		this.menuSkin = skin.menuSkin;
		this.messageSkin = skin.messageSkin;
		this.progressSkin = skin.progressSkin;
		this.scrollBarSkin = skin.scrollBarSkin;
		this.sliderSkin = skin.sliderSkin;
		this.tableSkin = skin.tableSkin;
		this.textBarSkin = skin.textBarSkin;
		this.textListSkin = skin.textListSkin;
		this.windowSkin = skin.windowSkin;
	}

	public void clear() {
		this.checkBoxSkin = null;
		this.clickButtonSkin = null;
		this.controlSkin = null;
		this.menuSkin = null;
		this.messageSkin = null;
		this.progressSkin = null;
		this.scrollBarSkin = null;
		this.sliderSkin = null;
		this.tableSkin = null;
		this.textBarSkin = null;
		this.textListSkin = null;
		this.windowSkin = null;
	}

}
