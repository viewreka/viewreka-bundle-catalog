package org.beryx.viewreka.bundle.oauth;


import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import java.util.ArrayList;
import java.util.List;

public abstract class DefaultLoginView extends BaseLoginView {
	private static final long serialVersionUID = 1L;

	public DefaultLoginView(Navigator navigator, String title) {
		super(navigator);

		setSizeFull();
		
		Label lbTitle = new Label(title);
        lbTitle.addStyleName("v-label-huge");
		lbTitle.setSizeUndefined();
        addComponent(lbTitle);
        setComponentAlignment(lbTitle, Alignment.TOP_CENTER);
        
        VerticalLayout authLayout = new VerticalLayout();
        authLayout.setSpacing(true);
        addComponent(authLayout);
        setComponentAlignment(lbTitle, Alignment.MIDDLE_CENTER);
        
		Label lbLogin = new Label("Choose a login method");
        lbLogin.addStyleName("v-label-large");
		lbLogin.setSizeUndefined();
		authLayout.addComponent(lbLogin);
		authLayout.setComponentAlignment(lbLogin, Alignment.TOP_CENTER);
        
        
        HorizontalLayout butLayout = new HorizontalLayout();
        butLayout.setSpacing(true);
        butLayout.setSizeUndefined();
        authLayout.addComponent(butLayout);
        authLayout.setComponentAlignment(butLayout, Alignment.TOP_CENTER);

        List<OAuthButton> buttons = new ArrayList<OAuthButton>();
        buttons.add(addOAuthButton(butLayout, "GitHub", GitHubButton.class));
        buttons.add(addOAuthButton(butLayout, "LinkedIn", LinkedInButton.class));
        buttons.add(addOAuthButton(butLayout, "Google", GoogleButton.class));
        buttons.add(addOAuthButton(butLayout, "Microsoft", LiveButton.class));

        boolean hasButtons = false;
        for(OAuthButton button : buttons) {
            if(button != null) {
                hasButtons = true;
                break;
            }
        }
        if(!hasButtons) {
            Label lbNoButtons = new Label("No .auth files available. Did you run the configureXXXAuth gradle tasks?");
            lbNoButtons.addStyleName("v-label-failure");
            lbNoButtons.setSizeUndefined();
            butLayout.addComponent(lbNoButtons);
            butLayout.setComponentAlignment(lbNoButtons, Alignment.TOP_CENTER);
        }
	}
}
