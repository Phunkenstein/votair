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

import com.example.uwm.myapplication.backend.request.Request;
import com.example.uwm.myapplication.backend.request.model.InfoModel;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoFragment extends Fragment {

    public static final String PREFS_NAME = "ProfilePrefs";
    private SharedPreferences profile;
    private View infoView;
    private Context context;
    private InfoModel infoModel;
    private String regDeadline;
    private String otherDeadlineTitle;
    private String otherDeadline;

    private OnFragmentInteractionListener mListener;

    public InfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoFragment.
     */
    // TODO: Rename and change types and number of parameters
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        infoView = inflater.inflate(R.layout.fragment_info, container, false);
        context = this.getActivity();
        profile = context.getSharedPreferences(PREFS_NAME, 0);
        FetchInfoTask profileTask = new FetchInfoTask();
        profileTask.execute("1234");
        return infoView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class FetchInfoTask extends AsyncTask<String, Void, InfoModel> {
        private Request reqService = null;

        @Override
        protected InfoModel doInBackground(String... params) {
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
            }
            try {
                infoModel = reqService.getVoterInfo().execute();
            }
            catch (IOException e) {
                System.out.println("Error retrieving info data from server");
            }
            return infoModel;
        }

        @Override
        protected void onPostExecute(InfoModel model) {
            TextView pollingAddressField= (TextView)infoView.findViewById(R.id.pollingAddressFieldID);
            TextView pollHours= (TextView)infoView.findViewById(R.id.pollHoursID);
            TextView documentationData= (TextView)infoView.findViewById(R.id.documentationDataID);
            TextView registrationDeadlineField= (TextView)infoView.findViewById(R.id.registrationDeadlineFieldID);
            TextView otherDeadlinesTitle= (TextView)infoView.findViewById(R.id.otherDeadlinesTitleID);
            TextView otherDeadlinesField= (TextView)infoView.findViewById(R.id.otherDeadlinesFieldID);
            TextView restrictionsField= (TextView)infoView.findViewById(R.id.restrictionsFieldID);

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
