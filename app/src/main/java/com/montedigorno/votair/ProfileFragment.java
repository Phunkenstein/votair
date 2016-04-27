package com.montedigorno.votair;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


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
public class ProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener {

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
        context = this.getActivity();
        profile = context.getSharedPreferences(PREFS_NAME, 0);
        String regId = profile.getString("registration", "");

        FetchProfileTask profileTask = new FetchProfileTask();
        profileTask.execute(regId);

        System.out.println("election task complete");

    }


    private Date generateDateOfBirth() {

        Date retVal = null;

        if (profileModel.getDayOfBirthDD() != null & profileModel.getMonthOfBirthMM() != null & profileModel.getYearOfBirthCCYY() != null) {


            String day = profileModel.getDayOfBirthDD().toString();
            String month = profileModel.getMonthOfBirthMM().toString();
            String year = profileModel.getYearOfBirthCCYY().toString();

            String completeDate = year + "." + month + "." + day;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.mm.dd");

            try {
                retVal = dateFormat.parse(completeDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return retVal;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        profView = inflater.inflate(R.layout.fragment_profile, container, false);

        Button saveButton = (Button) profView.findViewById(R.id.saveButtonId);

        if (profileModel == null){
            profileModel = new ProfileModel();
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
                    //("clicked", "clicked");
            public void onClick(View v){
                if (profileModel == null) {profileModel = new ProfileModel();}
                Log.w("clicked", "clicked");
                setProfileModel();
                SaveProfileTask task = new SaveProfileTask();
                task.execute(profileModel);
            }

            private void setProfileModel(){
                profileModel.setRegId(profile.getString("registration", ""));
                EditText firstNameText = (EditText)profView.findViewById(R.id.firstNameid);
                EditText lastNameText = (EditText)profView.findViewById(R.id.lastNameid);
                //EditText dateOfBirthText = (EditText)profView.findViewById(R.id.dateOfBirthid);
                EditText addressStreet = (EditText)profView.findViewById(R.id.homeAddressStreetId);
                EditText addressNumber = (EditText)profView.findViewById(R.id.houseNumberId);
                TextView numDaysReminder = (TextView)profView.findViewById(R.id.daysNumberFieldID);

                String firstNameString = firstNameText.getText().toString();
                String lastNameString = lastNameText.getText().toString();
                //DateTime dateOfBirthDate = dateOfBirthText.getText().toString().
                String addressStreetString = addressStreet.getText().toString();
                String addressNumberString = addressNumber.getText().toString();

                String daysStatusText = numDaysReminder.getText().toString();
                int reminderDays = parseDaysFromDayStatusText(daysStatusText);

                profileModel.setNumDaysOut(reminderDays);
                profileModel.setFirstName(firstNameString);
                profileModel.setLastName(lastNameString);
                try {
                    profileModel.setHouseNumber(addressNumberString);
                }
                catch (NumberFormatException e){
                    System.out.println(e);
                }
                profileModel.setStreetName(addressStreetString);
            }
        });

        SeekBar daysBar = (SeekBar)profView.findViewById(R.id.numDaysSeekBarID);
        daysBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                progressChanged = progress;
                TextView daysText = (TextView)profView.findViewById(R.id.daysNumberFieldID);

                if (progress == 0) daysText.setText("1 Day Before Elections");
                else daysText.setText(Integer.toString(progress + 1) + " Days Before Election");
            }

            public void onStartTrackingTouch(SeekBar seekbar){
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }


        });


        Spinner stateSpinner = (Spinner) profView.findViewById((R.id.state_spinner));
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.state_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        stateSpinner.setAdapter(adapter);

        Spinner partySpinner = (Spinner)profView.findViewById(R.id.party_spinner);
        ArrayAdapter<CharSequence> partyAdapter = ArrayAdapter.createFromResource(context,
                R.array.party_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        partySpinner.setAdapter(partyAdapter);

        return profView;
    }

    private int parseDaysFromDayStatusText(String daysStatusText) {
        String[] splitString = daysStatusText.split(" ");
        return Integer.parseInt(splitString[0]);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class SaveProfileTask extends AsyncTask<ProfileModel, Void, Boolean> {
        private Request reqService = null;

        @Override
        protected Boolean doInBackground(ProfileModel... params) {

            if (reqService == null) {
                Request.Builder builder = new Request.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
//                Request.Builder builder = new Request.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
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
                String regId = profile.getString("registration", "");
                ProfileModel localProfileModel = params[0];

                System.out.println("regId" + regId);
                localProfileModel.setRegId(regId);
                reqService.updateProfile(localProfileModel).execute();
                return true;
            } catch (IOException e) {
                System.out.println("Error retrieving profile data from server");
                System.out.println(e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result){
            CharSequence text = "Profile Saved";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
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
            return profileModel;
        }

        protected void onPostExecute(ProfileModel model) {
            System.out.println("in post for getProfile execute");
            profileModel = model;
            TextView firstName= (TextView)profView.findViewById(R.id.firstNameid);
            TextView lastName = (TextView)profView.findViewById(R.id.lastNameid);
            TextView dateOfBirth = (TextView)profView.findViewById(R.id.dateOfBirthid);
            TextView homeNo = (TextView)profView.findViewById(R.id.houseNumberId);
            TextView homeSt = (TextView)profView.findViewById(R.id.homeAddressStreetId);

            if (profileModel != null) {
                firstName.setText(profileModel.getFirstName());
                lastName.setText(profileModel.getLastName());

                Date dob = generateDateOfBirth();
                if (dob != null)
                    dateOfBirth.setText(dob.toString());
                homeNo.setText(profileModel.getHouseNumber());
                homeSt.setText(profileModel.getStreetName());
            }

        }
    }
}
