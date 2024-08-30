package loon.tea.dom;

import org.teavm.jso.JSMethod;

public interface ElementWrapper extends NodeWrapper {

	int getScrollTop();

	int getScrollLeft();

	int getClientWidth();

	int getClientHeight();

	void setAttribute(String qualifiedName, String value);

	StyleWrapper getStyle();

	@JSMethod
	void remove();
}
