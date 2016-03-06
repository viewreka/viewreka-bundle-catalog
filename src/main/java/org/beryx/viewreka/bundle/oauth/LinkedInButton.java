package org.beryx.viewreka.bundle.oauth;

import java.util.Map;

import com.github.scribejava.apis.LinkedInApi20;
import com.github.scribejava.core.builder.api.Api;
import com.vaadin.server.FontAwesome;

public class LinkedInButton extends OAuthButton {
	private static final long serialVersionUID = 1L;

	/**
     * Creates a "Log in with LinkedIn" button that will use the given API
     * key/secret to authenticate the user with LinkedIn, and then call the
     * given callback with {@link User} details.
     * 
     * @param apiKey
     *            API key from the service providing OAuth
     * @param apiSecret
     *            API secret from the service providing OAuth
     * @param authListener
     *            called once the user has been authenticated
     */
    public LinkedInButton(String apiKey, String apiSecret, OAuthListener authListener) {
        this("LinkedIn", apiKey, apiSecret, authListener);
    }

    /**
     * Creates a button with the given caption that will use the given API
     * key/secret to authenticate the user with LinkedIn, and then call the
     * given callback with {@link User} details.
     * 
     * @param caption
     *            button caption
     * @param apiKey
     *            API key from the service providing OAuth
     * @param apiSecret
     *            API secret from the service providing OAuth
     * @param authListener
     *            called once the user has been authenticated
     */
    public LinkedInButton(String caption, String apiKey, String apiSecret, OAuthListener authListener) {
        super(caption, apiKey, apiSecret, authListener);
        setIcon(FontAwesome.LINKEDIN_SQUARE);
    }

    @Override
    protected Class<? extends Api> getApi() {
        return LinkedInApi20.class;
    }

    @Override
    protected String getJsonDataUrl() {
        return "https://api.linkedin.com/v1/people/~?format=json";
    }

    @Override
    protected Class<? extends User> getUserClass() {
        return LinkedInUser.class;
    }

    public static class LinkedInUser implements User {
		private static final long serialVersionUID = 1L;

		private String firstName;
		private String lastName;
        private String id;
        private Map<String, String> siteStandardProfileRequest;
        private String token;
        private String tokenSecret;

        public String getToken() {
            return token;
        }

        public String getTokenSecret() {
            return tokenSecret;
        }

        public String getName() {
            return firstName + " " + lastName;
        }

        public String getScreenName() {
            return getName();
        }

        public String getPictureUrl() {
            return null;
        }

        public String getId() {
            return id;
        }

        public String getPublicProfileUrl() {
            return (siteStandardProfileRequest == null) ? null : siteStandardProfileRequest.get("url");
        }

        public String getService() {
            return "LinkedIn";
        }

        public String getEmail() {
            return null;
        }
    }

}