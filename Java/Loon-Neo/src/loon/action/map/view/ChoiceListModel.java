package loon.action.map.view;

import loon.LSystem;
import loon.utils.MathUtils;

public class ChoiceListModel {
	public static final int NOT_CHOICED = -1;
	private ChoiceModel[] choices;
	private ChoiceModel choicedArea;

	public ChoiceListModel(ChoiceModel[] choices) {
		this.choices = choices;
		choicedArea = new ChoiceModel(0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public ChoiceListModel(int x, int y, int width, int height, int nums, int dx, int dy) {
		createEvenSizeChoices(x, y, width, height, nums, dx, dy);
	}

	private void createEvenSizeChoices(int x, int y, int width, int height, int nums, int dx, int dy) {
		choicedArea = new ChoiceModel(x, y, width, height);
		int splitWidth = width;
		if (dx > 0) {
			splitWidth = (int) MathUtils.round(1f * width / nums);
		}
		int splitHeight = height;
		if (dy > 0) {
			splitHeight = (int) MathUtils.round(1f * height / nums);
		}
		choices = new ChoiceModel[nums];
		for (int i = 0; i < nums; i++) {
			choices[i] = new ChoiceModel(x + i * dx, y + i * dy, splitWidth, splitHeight);
		}
	}

	public int getChoicedPlace(int x, int y) {
		if (!choicedArea.isChoiced(x, y))
			return NOT_CHOICED;
		for (int i = 0; i < choices.length; i++) {
			ChoiceModel area = choices[i];
			if (area.isChoiced(x, y))
				return i;
		}
		return NOT_CHOICED;
	}
}
