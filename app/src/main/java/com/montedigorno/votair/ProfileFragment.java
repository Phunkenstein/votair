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
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;


import com.example.uwm.myapplication.backend.request.Request;
import com.example.uwm.myapplication.backend.request.model.ProfileModel;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

//import com.montedigorno.votair.models.ProfileModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private SharedPreferences profile;
    private Context context;
    private ProfileModel profileModel;
    private View profView;
    public static final String PREFS_NAME = "ProfilePrefs";


//    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(Context context) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profile = context.getSharedPreferences(PREFS_NAME, 0);
        String regId = profile.getString("registration", "");

        Button saveButton = (Button) profView.findViewById(R.id.saveButtonId);
        saveButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                SaveProfileTask saveProfile = new SaveProfileTask();
                saveProfile.execute();
            }
        });

        FetchProfileTask profileTask = new FetchProfileTask();
        profileTask.execute(regId);
        //profileModel = profileTask.execute(regId);

        profileModel = new ProfileModel();
        profileModel.setCity("Milwaukee");
        profileModel.setDayOfBirthDD(3);
        profileModel.setFirstName("Juba");
        profileModel.setLastName("Dance");
        profileModel.setHouseNumber(4);
        profileModel.setStreetName("Tor Street");

        System.out.println("election task complete");

    }


    private Date generateDateOfBirth() {
        String day = profileModel.getDayOfBirthDD().toString();
        String month = profileModel.getMonthOfBirthMM().toString();
        String year = profileModel.getYearOfBirthCCYY().toString();

        String completeDate = year + "." + month + "." + day;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.mm.dd");

        Date retVal = null;

        try {
            retVal = dateFormat.parse(completeDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        profView = (View) rootView.findViewById(R.id.expandableListView);

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    public class SaveProfileTask extends AsyncTask<String, Void, Boolean> {
        private Request reqService = null;

        @Override
        protected Boolean doInBackground(String... params) {

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
                profileModel.setRegId(params[0]);
                reqService.updateProfile(profileModel);
                return true;
            } catch (IOException e) {
                System.out.println("Error retrieving profile data from server");
            }
            return null;
        }
    }

    public class FetchProfileTask extends AsyncTask<String, Void, ProfileModel> {
        private Request reqService = null;
        private ProfileModel profileModel = null;

        @Override
        protected ProfileModel doInBackground(String... params) {
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
                String regId = params[0];
                profileModel = reqService.getProfile(regId).execute();
            }
            catch (IOException e) {
                System.out.println("Error retrieving profile data from server");
            }
            return null;
        }

        protected void onPostExecute(String[] strings) {
            System.out.println("in post execute");

            TextView firstName= (TextView)profView.findViewById(R.id.firstNameid);
            TextView lastName = (TextView)profView.findViewById(R.id.lastNameid);
            TextView dateOfBirth = (TextView)profView.findViewById(R.id.dateOfBirthid);
            TextView homeNo = (TextView)profView.findViewById(R.id.houseNumberId);
            TextView homeSt = (TextView)profView.findViewById(R.id.homeAddressStreetId);

            if (profileModel != null) {
                firstName.setText(profileModel.getFirstName());
                lastName.setText(profileModel.getLastName());

                dateOfBirth.setText(generateDateOfBirth().toString());
                homeNo.setText(profileModel.getHouseNumber());
                homeSt.setText(profileModel.getStreetName());
            }

        }
    }
    }



//
//        }
/*    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

/*    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
//    public interface OnFragmentInteractionListener {
 //       // TODO: Update argument type and name
 //       void onFragmentInteraction(Uri uri);
//    }

