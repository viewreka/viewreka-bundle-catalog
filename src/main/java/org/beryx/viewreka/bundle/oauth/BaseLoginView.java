package org.beryx.viewreka.bundle.oauth;


import com.google.gson.Gson;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import org.beryx.viewreka.bundle.oauth.OAuthButton.OAuthListener;
import org.beryx.viewreka.bundle.oauth.OAuthButton.User;
import org.jsoup.helper.StringUtil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.Map;

public abstract class BaseLoginView extends VerticalLayout implements View {
	private static final long serialVersionUID = 1L;
	
	protected final Navigator navigator;
	protected final OAuthListener oauthListener;

	protected abstract View createMainView(Navigator navigator, User user);
	
    public BaseLoginView(final Navigator navigator) {
    	this.navigator = navigator;

		this.oauthListener = new OAuthListener() {
			private static final long serialVersionUID = 1L;
			
			public void userAuthenticated(User user) {
				System.err.println("User authenticated: " + user.getScreenName());
								
				navigator.addView("", createMainView(navigator, user));

				System.err.println("Navigating to the main view...");
				navigator.navigateTo("");
			}

			public void failed(String reason) {
				Notification.show("Login failed", reason, Type.ERROR_MESSAGE);
			}
		};
    }

    protected static interface OAuthButtonBuilder {
    	OAuthButton createButton(String clientId, String secret, OAuthListener oauthListener);
    }

    protected OAuthButton addOAuthButton(ComponentContainer container, String serviceName, final Class<? extends OAuthButton> buttonClass) {
    	OAuthButtonBuilder builder = new OAuthButtonBuilder() {
			@Override
			public OAuthButton createButton(String clientId, String secret, OAuthListener oauthListener) {
				try {
					Constructor<? extends OAuthButton> butCtor = buttonClass.getConstructor(String.class, String.class, OAuthListener.class);
					return butCtor.newInstance(clientId, secret, oauthListener);
				} catch (Exception e) {
					throw new RuntimeException("Cannot create OAuthButton of type " + buttonClass.getName(), e);
				}
			}    		
    	};
    	return addOAuthButton(container, serviceName, builder);
    }
    
    protected OAuthButton addOAuthButton(ComponentContainer container, String serviceName, OAuthButtonBuilder builder) {
    	OAuthButton button = null;
        InputStream githubAuthStream = getClass().getResourceAsStream("/" + serviceName.toLowerCase() + ".auth");
        if(githubAuthStream != null) {
    		@SuppressWarnings("unchecked")
			Map<String, String> authMap = new Gson().fromJson(new InputStreamReader(githubAuthStream), Map.class);
    		String clientId = authMap.get("clientId");
    		String secret = authMap.get("secret");
    		if(!StringUtil.isBlank(clientId) && !StringUtil.isBlank(secret)) {
        		button = builder.createButton(clientId, secret, oauthListener);
        		button.setSizeUndefined();
				container.addComponent(button);
    		}
        }
    	return button;
    }
    
	@Override
	public void enter(ViewChangeEvent event) {
		System.err.println("Entered LoginView");		
	}
}
