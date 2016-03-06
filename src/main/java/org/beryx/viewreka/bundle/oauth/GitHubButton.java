package org.beryx.viewreka.bundle.oauth;

import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.core.builder.api.Api;
import com.vaadin.server.FontAwesome;

public class GitHubButton extends OAuthButton {
	private static final long serialVersionUID = 1L;

	/**
     * Creates a "Log in with GitHub" button that will use the given API
     * key/secret to authenticate the user with GitHub, and then call the
     * given callback with {@link User} details.
     * 
     * @param apiKey
     *            API key from the service providing OAuth
     * @param apiSecret
     *            API secret from the service providing OAuth
     * @param authListener
     *            called once the user has been authenticated
     */
    public GitHubButton(String apiKey, String apiSecret, OAuthListener authListener) {
        this("GitHub", apiKey, apiSecret, authListener);
    }

    /**
     * Creates a button with the given caption that will use the given API
     * key/secret to authenticate the user with GitHub, and then call the
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
    public GitHubButton(String caption, String apiKey, String apiSecret, OAuthListener authListener) {
        super(caption, apiKey, apiSecret, authListener);
        setIcon(FontAwesome.GITHUB);
    }

    @Override
    protected Class<? extends Api> getApi() {
        return GitHubApi.class;
    }

    @Override
    protected String getJsonDataUrl() {
        return "https://api.github.com/user";
    }

    @Override
    protected Class<? extends User> getUserClass() {
        return GitHubUser.class;
    }

    public static class GitHubUser implements User {
		private static final long serialVersionUID = 1L;

		private String name;
        private String login;
        private String avatar_url;
        private String id;
        private String html_url;
        private String email;
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
            return login;
        }

        public String getPictureUrl() {
            return avatar_url;
        }

        public String getId() {
            return id;
        }

        public String getPublicProfileUrl() {
            return html_url;
        }

        public String getService() {
            return "GitHub";
        }

        public String getEmail() {
            return email;
        }

    }

}