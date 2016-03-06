package org.beryx.viewreka.bundle.oauth;


import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

@Widgetset("org.beryx.viewreka.bundle.catalog.CatalogWidgetSet")
@PreserveOnRefresh
public abstract class BaseMainUI extends UI {
	private static final long serialVersionUID = 1L;
	
	public static final String LOGIN_VIEW = "login";

	protected abstract View createLoginView(Navigator navigator);

	@Override
    public void init(VaadinRequest request) {
		final Navigator navigator = new Navigator(UI.getCurrent(), this);
		View loginView = createLoginView(navigator);
		navigator.addView("", loginView);
		navigator.addView(LOGIN_VIEW, loginView);
		navigator.navigateTo(LOGIN_VIEW);
    }

    @Override
    protected void refresh(VaadinRequest request) {
        System.err.println("Refresh called for request: " + request.getParameterMap());
    }
}
