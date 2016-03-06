package org.beryx.viewreka.bundle.oauth;

import java.util.Map;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.api.Api;
import com.vaadin.server.FontAwesome;

public class GoogleButton extends OAuthButton {
	private static final long serialVersionUID = 1L;

	/**
     * Creates a "Google" button that will use the given API
     * key/secret to authenticate the user with Google, and then call the
     * given callback with {@link User} details.
     * 
     * @param apiKey
     *            API key from the service providing OAuth
     * @param apiSecret
     *            API secret from the service providing OAuth
     * @param authListener
     *            called once the user has been authenticated
     */
    public GoogleButton(String apiKey, String apiSecret, OAuthListener authListener) {
        this("Google", apiKey, apiSecret, authListener);
    }

    /**
     * Creates a button with the given caption that will use the given API
     * key/secret to authenticate the user with Google, and then call the
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
    public GoogleButton(String caption, String apiKey, String apiSecret, OAuthListener authListener) {
        super(caption, apiKey, apiSecret, authListener);
        setIcon(FontAwesome.GOOGLE_PLUS);
        this.scope = "profile";
    }

    @Override
    protected Class<? extends Api> getApi() {
        return GoogleApi20.class;
    }

    @Override
    protected String getJsonDataUrl() {
        return "https://www.googleapis.com/plus/v1/people/me";
    }

    @Override
    protected Class<? extends User> getUserClass() {
        return GoogleUser.class;
    }

    public static class GoogleUser implements User {
		private static final long serialVersionUID = 1L;

		private String displayName;
        private String id;
        private String url;
        private Map<String, String> image;
        private String token;
        private String tokenSecret;

        public String getToken() {
            return token;
        }

        public String getTokenSecret() {
            return tokenSecret;
        }

        public String getName() {
            return displayName;
        }

        public String getScreenName() {
            return displayName;
        }

        public String getPictureUrl() {
            return (image == null) ? null : image.get("url");
        }

        public String getId() {
            return id;
        }

        public String getPublicProfileUrl() {
            return url;
        }

        public String getService() {
            return "Google";
        }

        public String getEmail() {
            return null;
        }
    }

}