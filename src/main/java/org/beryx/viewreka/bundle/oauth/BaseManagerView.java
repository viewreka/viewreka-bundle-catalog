package org.beryx.viewreka.bundle.oauth;

import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import org.beryx.viewreka.bundle.oauth.OAuthButton.User;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.VerticalLayout;

public class BaseManagerView extends Panel implements View {
	private static final long serialVersionUID = 1L;

    private final VerticalLayout viewLayout;
	private final Navigator navigator;
	private final User user;

	public BaseManagerView(final Navigator navigator, User user) {
		this.navigator = navigator;
		this.user = user;
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        addStyleName(ValoTheme.PANEL_SCROLL_INDICATOR);

        this.viewLayout = new VerticalLayout();
        setContent(viewLayout);
        viewLayout.setSizeFull();
        viewLayout.setSpacing(true);
        viewLayout.setMargin(true);
	}

    public VerticalLayout getViewLayout() {
        return viewLayout;
    }

    protected void logout() {
		System.err.println("logout: Navigating to " + BaseMainUI.LOGIN_VIEW + "...");		
		navigator.removeView("");
		if(getUI() instanceof BaseMainUI) {
			View loginView = ((BaseMainUI)getUI()).createLoginView(navigator);
			System.err.println("logout: Adding new loginView...");
			navigator.addView("", loginView);
			navigator.addView(BaseMainUI.LOGIN_VIEW, loginView);
		}
		navigator.navigateTo(BaseMainUI.LOGIN_VIEW);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		System.err.println("Entered ManagerView for user " + user.getScreenName());		
	}
	
	protected Navigator getNavigator() {
		return navigator;
	}
	
	protected User getUser() {
		return user;
	}
}
