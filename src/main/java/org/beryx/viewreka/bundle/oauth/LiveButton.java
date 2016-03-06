package org.beryx.viewreka.bundle.oauth;

import com.github.scribejava.apis.LiveApi;
import com.github.scribejava.core.builder.api.Api;
import com.vaadin.server.FontAwesome;

public class LiveButton extends OAuthButton {
	private static final long serialVersionUID = 1L;

	/**
     * Creates a "Microsoft" button that will use the given API
     * key/secret to authenticate the user with Microsoft, and then call the
     * given callback with {@link User} details.
     * 
     * @param apiKey
     *            API key from the service providing OAuth
     * @param apiSecret
     *            API secret from the service providing OAuth
     * @param authListener
     *            called once the user has been authenticated
     */
    public LiveButton(String apiKey, String apiSecret, OAuthListener authListener) {
        this("Microsoft", apiKey, apiSecret, authListener);
    }

    /**
     * Creates a button with the given caption that will use the given API
     * key/secret to authenticate the user with Microsoft, and then call the
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
    public LiveButton(String caption, String apiKey, String apiSecret, OAuthListener authListener) {
        super(caption, apiKey, apiSecret, authListener);
        setIcon(FontAwesome.WINDOWS);
        this.scope = "wl.basic";
    }

    @Override
    protected Class<? extends Api> getApi() {
        return LiveApi.class;
    }

    @Override
    protected String getJsonDataUrl() {
        return "https://apis.live.net/v5.0/me";
    }

    @Override
    protected Class<? extends User> getUserClass() {
        return MicrosoftUser.class;
    }

    public static class MicrosoftUser implements User {
		private static final long serialVersionUID = 1L;

		private String name;
        private String id;
        private String link;
        private String token;
        private String tokenSecret;

        public String getToken() {
            return token;
        }

        public String getTokenSecret() {
            return tokenSecret;
        }

        public String getName() {
            return name;
        }

        public String getScreenName() {
            return name;
        }

        public String getPictureUrl() {
            return null;
        }

        public String getId() {
            return id;
        }

        public String getPublicProfileUrl() {
            return link;
        }

        public String getService() {
            return "Microsoft";
        }

        public String getEmail() {
            return null;
        }
    }

}