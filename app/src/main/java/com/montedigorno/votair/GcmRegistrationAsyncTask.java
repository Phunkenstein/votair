package com.montedigorno.votair;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.content.Context;

import com.example.uwm.myapplication.backend.registration.Registration;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Chris Harmon on 3/5/2016.
 * Ref: https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
 */
class GcmRegistrationAsyncTask extends AsyncTask<Void, Void, String> {
    // Fields
    private static Registration regService = null;
    private GoogleCloudMessaging gcm;
    private Context context;
    private static final String SENDER_ID = "933682362008";
    public static final String PREFS_NAME = "ProfilePrefs";
//    public static final String BACKEND_ADDR = "http://10.0.2.2:8080/_ah/api/";
    public static final String BACKEND_ADDR = "https://voter-helper-1239.appspot.com/_ah/api/";

    public GcmRegistrationAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        if (regService == null) {
            Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
                    // otherwise they can be skipped
                    .setRootUrl(BACKEND_ADDR)
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                                throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });

            regService = builder.build();
        }

        String msg = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
            String regId = gcm.register(SENDER_ID);
            msg = "Device registered, registration ID=" + regId;

            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            regService.register(regId).execute();
            SharedPreferences profile = context.getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = profile.edit();
            editor.putString("registration", regId);
            editor.commit();

        } catch (IOException ex) {
            ex.printStackTrace();
            msg = "Error: " + ex.getMessage();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {
        Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
    }
}