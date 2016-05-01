package com.montedigorno.votair;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.io.IOException;

// Import our backend API and data models, as well as necessary google APIs
import com.example.uwm.myapplication.backend.request.Request;
import com.example.uwm.myapplication.backend.request.model.InfoModel;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;


/**
 * A simple Fragment subclass for the Voter Info Page (3).
 */
public class InfoFragment extends Fragment {

    // Fields for shared preferences.
    public static final String PREFS_NAME = "ProfilePrefs";
    private SharedPreferences profile;

    // Fields for displaying the Info
    private View infoView;
    private Context context;
    private InfoModel infoModel;
    private String regDeadline;
    private String otherDeadlineTitle;
    private String otherDeadline;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static InfoFragment newInstance(Context context) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the view and initialize the fields.
        infoView = inflater.inflate(R.layout.fragment_info, container, false);
        context = this.getActivity();
        profile = context.getSharedPreferences(PREFS_NAME, 0);

        // Fetch the Info from the backend and display.
        FetchInfoTask infoTask = new FetchInfoTask();
        infoTask.execute();
        return infoView;
    }


    public class FetchInfoTask extends AsyncTask<String, Void, InfoModel> {
        private Request reqService = null;


        @Override
        protected InfoModel doInBackground(String... params) {
            // Initialize the request service if we hav not yet.
            if (reqService == null) {
                Request.Builder builder = new Request.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                builder.setApplicationName("Votair!");
                reqService = builder.build();
            } try {
                infoModel = reqService.getVoterInfo().execute();
            } catch (IOException e) {
                System.out.println("Error retrieving info data from server");
            }
            return infoModel;
        }


        @Override
        protected void onPostExecute(InfoModel model) {
            // Grab all the textviews.
            TextView pollingAddressField= (TextView)infoView.findViewById(R.id.pollingAddressFieldID);
            TextView pollHours= (TextView)infoView.findViewById(R.id.pollHoursID);
            TextView documentationData= (TextView)infoView.findViewById(R.id.documentationDataID);
            TextView registrationDeadlineField= (TextView)infoView.findViewById(R.id.registrationDeadlineFieldID);
            TextView otherDeadlinesTitle= (TextView)infoView.findViewById(R.id.otherDeadlinesTitleID);
            TextView otherDeadlinesField= (TextView)infoView.findViewById(R.id.otherDeadlinesFieldID);
            TextView restrictionsField= (TextView)infoView.findViewById(R.id.restrictionsFieldID);

            // If the infoModel is set, set the textviews.
            if (infoModel != null) {
                regDeadline = profile.getString("regdeadline", "Loading...");
                otherDeadlineTitle = profile.getString("otherdeadlinetitle", "Other Deadline");
                otherDeadline = profile.getString("otherdeadline", "Loading...");

                registrationDeadlineField.setText(regDeadline);
                otherDeadlinesTitle.setText(otherDeadlineTitle + ": ");
                otherDeadlinesField.setText(otherDeadline);
                pollingAddressField.setText(infoModel.getPollingPlace());
                pollHours.setText(infoModel.getVotingHoursStart() + " - " + model.getVotingHoursEnd());
                documentationData.setText(infoModel.getRequiredDocumentation());
                restrictionsField.setText(infoModel.getRestrictions());
            }
        }
    }
}