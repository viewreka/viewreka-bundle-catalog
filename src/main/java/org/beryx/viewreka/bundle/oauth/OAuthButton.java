package org.beryx.viewreka.bundle.oauth;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.helper.StringUtil;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.Api;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuthService;
import com.google.gson.Gson;
import com.vaadin.server.Page;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;


/**
 * Starting point to create a {@link Button} that allows the user to log in
 * using OAuth; e.g log in with Facebook or Twitter.
 * <p>
 * Uses the Scribe oauth library, and it should be fairly straightforward to
 * implement a button for all supported services.
 * </p>
 * <p>
 * Generally, you just give the buttons the API keys that can be obtained from
 * the service in question, and a callback that will receive some user data once
 * the user has been authenticated. Some buttons implementations might provide
 * additional options (e.g get user email address from Facebook).
 * </p>
 * <p>
 * This approach is intentionally simplistic for this specific use-case: log in
 * with X. For more flexible OAuth interactions, the Scribe library can be used
 * directly.
 * </p>
 * <p>
 * 	March 2, 2013: Modified by asarraf21 to make it compatible by Vaadin 7.
 * </p>
 */
public abstract class OAuthButton extends Button {
	private static final long serialVersionUID = 1L;

//	transient protected OAuthService service = null;
    protected Token requestToken = null;
    protected Token accessToken = null;

    protected String apiKey;
    protected String apiSecret;
    
    protected String scope;

    protected RequestHandler handler;

    protected OAuthListener authListener;

    /**
     * @param caption
     *            button caption
     * @param apiKey
     *            API key from the service providing OAuth
     * @param apiSecret
     *            API secret from the service providing OAuth
     * @param authListener
     *            called once the user has been authenticated
     */
    public OAuthButton(String caption, String apiKey, String apiSecret, OAuthListener authListener) {
        super(caption);
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.authListener = authListener;
        super.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				authenticate();
			}
		});
    }


    /**
     * Gets the {@link Api} implementation class that this service uses.
     *
     * @return {@link Api} implementation class
     */
    protected abstract Class<? extends Api> getApi();

    private static final String[] oauthFails = new String[] { "oauth_problem" };

    /**
     * Gets the names of parameters that the OAuth service uses to indicate a
     * problem during authentication - e.g if the user presses 'Cancel' at the
     * authentication page.
     *
     * @return
     */
    protected String[] getFailureParameters() {
        return oauthFails;
    }

    /**
     * Gets the URL from which JSON formatted user data can be fetched.
     *
     * @return JSON user data url
     */
    protected abstract String getJsonDataUrl();

    /**
     * Gets the {@link User} implementation class for the user data that this
     * service provides.
     *
     * @return {@link User} implementation class
     */
    protected abstract Class<? extends User> getUserClass();

    
    private String callbackUri = null;
    
    /**
     * Gets the URL that the user will be sent to in order to authenticate. Most
     * implementations will also create the requestToken at this point.
     *
     * @return authentication url for the OAuth service
     */
    protected String getAuthUrl() {
        return getService().getAuthorizationUrl(null);
    }

    /**
     * Gets the name of the parameter that will contain the verifier when the
     * user returns from the OAuth service.
     *
     * @return verifier parameter name
     */
    protected String getVerifierName() {
        return "code";
    }

    /**
     * Gets the OAuth service singleton.
     *
     * @return OAuth service singleton
     */
    protected OAuthService getService() {
    	if(callbackUri == null) {
    		callbackUri = Page.getCurrent().getLocation().toString();
    		System.err.println("Initial callbackUri: " + callbackUri);
    		
    		int pos1 = callbackUri.indexOf('?');
    		int pos2 = callbackUri.indexOf('#');
    		int pos = (pos1 < 0) ? pos2 : (pos2 < 0) ? pos1 : Math.min(pos1,  pos2);
    		if(pos >= 0) {
    			callbackUri = callbackUri.substring(0, pos);
    		}
    		System.err.println("Final callbackUri: " + callbackUri);
    	}
    	ServiceBuilder builder = new ServiceBuilder();
		builder.provider(getApi()).apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback(callbackUri);
		if(!StringUtil.isBlank(scope)) {
			builder.scope(scope);
		}
        return builder.build();
    }

    /**
     * Connects the parameter handler that will be invoked when the user comes
     * back, and sends the user to the authentication url for the OAuth service.
     */
    protected void authenticate() {
    	System.err.println("authenticate() called.");
        if (handler == null) {
            handler = createRequestHandler();
            getSession().addRequestHandler(handler);
        }
        Page.getCurrent().open(getAuthUrl(), "_self");
    }

    /**
     * Creates the parameter handler that will be invoked when the user returns
     * from the OAuth service.
     *
     * @return the parameter handler
     */
    protected RequestHandler createRequestHandler() {
        System.err.println("createRequestHandler() called.");
        return new RequestHandler() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean handleRequest(VaadinSession session,
                                         VaadinRequest request, VaadinResponse response) throws IOException {
                try {
                    Map<String, String[]> parameters = request.getParameterMap();
                    System.err.println("handleRequest() called. parameters");
                    for (Entry<String, String[]> entry : parameters.entrySet()) {
                        System.err.println("\t" + entry.getKey() + ": " + Arrays.asList(entry.getValue()));
                    }
                    if (parameters.containsKey(getVerifierName())) {
                        String v = parameters.get(getVerifierName())[0];
                        System.err.println("v: " + v);
                        Verifier verifier = new Verifier(v);

                        getService().getConfig().getCallback();

                        accessToken = getService().getAccessToken(requestToken, verifier);
                        System.err.println("accessToken: " + accessToken);

                        User user = getUser();

                        getSession().removeRequestHandler(handler);
                        handler = null;

                        authListener.userAuthenticated(user);

                        return false;

                    } else if (getFailureParameters() != null) {
                        for (String key : getFailureParameters()) {
                            if (parameters.containsKey(key)) {
                                authListener.failed(parameters.get(key)[0]);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false; // anyways, the request should go up so that the UI loads itself
            }
        };
    }

    /**
     * Creates and returns the {@link User} instance, usually by retreiving JSON
     * data from the url provided by {@link #getJsonDataUrl()}.
     *
     * @return the {@link User} instance containing user data from the service
     */
    protected User getUser() {
    	System.err.println("getUser() called.");
    	OAuthService service = getService();
        OAuthRequest request = new OAuthRequest(Verb.GET, getJsonDataUrl(), service);
        request.addHeader("User-Agent", "viewreka-bundle-catalog");
        service.signRequest(accessToken, request);
        Response response = request.send();
    	System.err.println("responseBody: " + response.getBody());

        Gson gson = new Gson();
        User user = gson.fromJson(response.getBody(), getUserClass());

        // TODO set the token/secret here?
        try {
            Field tokenField = user.getClass().getDeclaredField("token");
            if (tokenField != null) {
                tokenField.setAccessible(true);
                tokenField.set(user, accessToken.getToken());
            }

            Field tokenSecretField = user.getClass().getDeclaredField("tokenSecret");
            if (tokenSecretField != null) {
                tokenSecretField.setAccessible(true);
                tokenSecretField.set(user, accessToken.getSecret());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return user;
    }

    /**
     * Called when the {@link User} instance has been successfully created, or
     * the OAuth service returned a problem code.
     */
    public static interface OAuthListener extends Serializable {
        public void userAuthenticated(User user);

        public void failed(String reason);
    }

    /**
     * Contains user data common for most services. Some services might add own
     * data, or leave some data as null - for instance 'email' is quite seldom
     * available trough the APIs.
     * <p>
     * The default {@link OAuthButton#getUser()} implementation sets the 'token'
     * and 'tokenSecret' member fields if such exist, so that the {@link User}
     * implementation can just return these in {@link #getToken()} and
     * {@link #getTokenSecret()}.
     * </p>
     */
    public static interface User extends Serializable {

        /**
         * Name of the OAuth service, e.g "facebook".
         *
         * @return
         */
        public String getService();

        /**
         * Often "Firstname Lastname", but not always - e.g Twitter users have a
         * single 'name' that can be changed to pretty much anything.
         *
         * @return user name
         */
        public String getName();

        /**
         * The screen name is usually a short username used no the service, most
         * often unique, and quite often used to identify the user profile (e.g
         * http://twitter.com/screenname).
         *
         * @return
         */
        public String getScreenName();

        /**
         * Url to the avatar picture for the user.
         *
         * @return
         */
        public String getPictureUrl();

        /**
         * Id form the OAuth service; this is unique within the service. A
         * "globaly unique" id can be created for instance by combining this id
         * with the service name (e.g "facebook:12345").
         *
         * @return
         */
        public String getId();

        /**
         * Url to the users public profile on the service (e.g
         * http://twitter.com/screenname).
         *
         * @return
         */
        public String getPublicProfileUrl();

        /**
         * Email address - NOTE that this is quite seldom provided. Also, it
         * might be better to allow the user to provide an email address of
         * choice while registering for your service.
         *
         * @return email address or (quite often) null
         */
        public String getEmail();

        /**
         * Gets the OAuth access token that can be used together with the token
         * secret ({@link #getTokenSecret()}) in order to access the OAuth
         * service API.
         *
         * @return OAuth access token
         */
        public String getToken();

        /**
         * Gets the OAuth access token secret that can be used together with the
         * token ({@link #getToken()}) in order to access the OAuth service API.
         *
         * @return OAuth access token secret
         */
        public String getTokenSecret();

    }
}
