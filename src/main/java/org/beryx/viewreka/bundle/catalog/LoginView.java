package org.beryx.viewreka.bundle.catalog;


import org.beryx.viewreka.bundle.oauth.DefaultLoginView;
import org.beryx.viewreka.bundle.oauth.OAuthButton.User;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;

public class LoginView extends DefaultLoginView {
	private static final long serialVersionUID = 1L;

	public LoginView(Navigator navigator) {
		super(navigator, "Viewreka Bundle Manager");
	}

	@Override
	protected View createMainView(Navigator navigator, User user) {
		return new ManagerView(navigator, user);
	}

}
