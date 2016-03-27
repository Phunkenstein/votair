package com.montedigorno.votair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Override;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class ElectionFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private ArrayAdapter<String> mElectionAdapter;

    public ElectionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        FetchElectionTask electionTask = new FetchElectionTask();
        electionTask.execute();
        System.out.println("election task complete");
    }


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ElectionFragment newInstance(int sectionNumber) {
        ElectionFragment fragment = new ElectionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String[] dummyData = {
                "Election 1",
                "Election 2",
                "Election 3"
        };

        List<String> elections = new ArrayList<String>(Arrays.asList(dummyData));

//        mElectionAdapter = new ArrayAdapter<String>(
        //              getActivity(),
        //            R.layout.list_item_election,
        //          R.id.expandableListView,
        //      elections
        //);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        return rootView;
    }

    public class FetchElectionTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String electionJsonString = null;

            try {
                URL url = new URL("https://rolz.org/api/?6d6");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                System.out.println("point 1");
                int status = urlConnection.getResponseCode();
                System.out.println(status);
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                electionJsonString = buffer.toString();
                System.out.println(electionJsonString);

            } catch (IOException e) {
                Log.e("ElectionFragment", "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("ElectionFragment", "Error closing stream ", e);
                    }
                }
            }


            return null;

        }

    }
}