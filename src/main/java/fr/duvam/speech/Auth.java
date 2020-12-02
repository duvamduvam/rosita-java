package fr.duvam.speech;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;

import org.apache.log4j.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.PlusScopes;

public class Auth {

	/** Global instance of the HTTP transport. */
	private static HttpTransport httpTransport;
	private static FileDataStoreFactory dataStoreFactory;
	private static final Logger LOGGER = Logger.getLogger(Auth.class);

	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"),
			".store/rosita");
	private static final String APPLICATION_NAME = "Rosita";
	private static Plus plus;

	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	public static void main(String[] args) {
		try {
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
			// authorization
			Credential credential = authorize();
			// set up global Plus instance
			LOGGER.info("authorization ok");
			plus = new Plus.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)
					.build();
		} catch (Exception e) {
			LOGGER.error(e);
		}
		// ...
	}

	private static Credential authorize() {
		// load client secrets
		GoogleClientSecrets clientSecrets;
		Credential credential = null;
		try {
			String secret = "/home/david/tmp/client_secret_376661250377-7acqo96ipabhgedtbnsc6gu04jabdc0t.apps.googleusercontent.com.json";
			File initialFile = new File(secret);
			clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new FileReader(initialFile));
			GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
					clientSecrets, Collections.singleton(PlusScopes.PLUS_ME)).setDataStoreFactory(dataStoreFactory)
							.build();
			// authorize
			credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("daviddubreuil");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e);
		}
		return credential;

	}

}
