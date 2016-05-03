package com.montedigorno.votair;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.util.Calendar;


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
    private int mYear;
    private int mMonth;
    private int mDay;
    public static TextView dayOfBirthView;
    public static final String BACKEND_ADDR = "https://voter-helper-1239.appspot.com/_ah/api/";

    public ProfileFragment() {
    }

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        profView = inflater.inflate(R.layout.fragment_profile, container, false);

        Button saveButton = (Button) profView.findViewById(R.id.saveButtonId);

        if (profileModel == null){
            profileModel = new ProfileModel();
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
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
                TextView dateOfBirthText = (TextView)profView.findViewById(R.id.dateOfBirthid);
                EditText addressStreet = (EditText)profView.findViewById(R.id.homeAddressStreetId);
                EditText addressNumber = (EditText)profView.findViewById(R.id.houseNumberId);
                TextView numDaysReminder = (TextView)profView.findViewById(R.id.daysNumberFieldID);
                EditText cityEdit = (EditText)profView.findViewById(R.id.cityEditID);
                EditText zipText = (EditText)profView.findViewById(R.id.zipEditID);

                String zipString = zipText.getText().toString();
                String firstNameString = firstNameText.getText().toString();
                String lastNameString = lastNameText.getText().toString();
                String dateOfBirthDate = dateOfBirthText.getText().toString();
                String addressStreetString = addressStreet.getText().toString();
                String addressNumberString = addressNumber.getText().toString();
                String cityString = cityEdit.getText().toString();

                String daysStatusText = numDaysReminder.getText().toString();
                int reminderDays = parseDaysFromDayStatusText(daysStatusText);

                profileModel.setZipCode(zipString);
                profileModel.setNumDaysOut(reminderDays);
                profileModel.setFirstName(firstNameString);
                profileModel.setLastName(lastNameString);
                profileModel.setCity(cityString);
                String[] birthDate = dateOfBirthDate.split("/");
                if (birthDate.length == 3){
                    profileModel.setMonthOfBirthMM(Integer.parseInt(birthDate[0]));
                    profileModel.setDayOfBirthDD(Integer.parseInt(birthDate[1]));
                    profileModel.setYearOfBirthCCYY(Integer.parseInt(birthDate[2]));
                }

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
                R.array.state_array, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        // Apply the adapter to the spinner
        stateSpinner.setAdapter(adapter);

        Spinner partySpinner = (Spinner)profView.findViewById(R.id.party_spinner);
        ArrayAdapter<CharSequence> partyAdapter = ArrayAdapter.createFromResource(context,
                R.array.party_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        partySpinner.setAdapter(partyAdapter);

        dayOfBirthView = (TextView) profView.findViewById(R.id.dateOfBirthid);
        dayOfBirthView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

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
                        .setRootUrl(BACKEND_ADDR)
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

    public void showDatePickerDialog() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends android.support.v4.app.DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public DatePickerDialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            dayOfBirthView.setText((month + 1) + "/" + day + "/" + year);
        }

    }

    public class FetchProfileTask extends AsyncTask<String, Void, ProfileModel> {
        private Request reqService = null;
        private ProfileModel profileModel = null;

        @Override
        protected ProfileModel doInBackground(String... params) {
            if (reqService == null) {
                Request.Builder builder = new Request.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        .setRootUrl(BACKEND_ADDR)
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
            SeekBar reminderBar = (SeekBar)profView.findViewById(R.id.numDaysSeekBarID);
            TextView numDaysText = (TextView)profView.findViewById(R.id.daysNumberFieldID);
            TextView city = (TextView)profView.findViewById(R.id.cityEditID);
            Spinner state = (Spinner)profView.findViewById(R.id.state_spinner);
            TextView zipCode = (TextView)profView.findViewById(R.id.zipEditID);

            if (profileModel != null) {
                zipCode.setText(profileModel.getZipCode());
                city.setText(profileModel.getCity());
                state.setSelection(0);
                firstName.setText(profileModel.getFirstName());
                lastName.setText(profileModel.getLastName());

                reminderBar.setProgress(profileModel.getNumDaysOut());

                if (profileModel.getNumDaysOut()== 0) numDaysText.setText("1 Day Before Elections");
                else numDaysText.setText(Integer.toString(profileModel.getNumDaysOut() + 1) + " Days Before Election");


                int month = profileModel.getMonthOfBirthMM();
                int year = profileModel.getYearOfBirthCCYY();
                int day = profileModel.getDayOfBirthDD();
                dateOfBirth.setText((month + 1) + "/" + day + "/" + year);

                homeNo.setText(profileModel.getHouseNumber());
                homeSt.setText(profileModel.getStreetName());
            }

        }
    }
}
