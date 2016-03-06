package org.beryx.viewreka.bundle.catalog;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringUI;
import org.beryx.viewreka.bundle.oauth.BaseMainUI;

@SpringUI(path = "/main")
@Theme("viewreka")
public class MainUI extends BaseMainUI {
	private static final long serialVersionUID = 1L;

	@Override
	protected View createLoginView(Navigator navigator) {
		return new LoginView(navigator);
	}
}
