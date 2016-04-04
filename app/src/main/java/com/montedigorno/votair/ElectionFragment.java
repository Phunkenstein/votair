package com.montedigorno.votair;

import android.content.Context;
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

import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Override;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.example.uwm.myapplication.backend.request.model.ElectionModel;
import com.example.uwm.myapplication.backend.request.Request;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

/**
 * A placeholder fragment containing a simple view.
 */
public class ElectionFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private Context context;
    private ArrayList<String> elections;
    private HashMap<String, List<String>> electionItemHash;
    private ExpandableListView expView;
    private ExpandableListAdapter mElectionAdapter;

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
    public static ElectionFragment newInstance(Context context) {
        ElectionFragment fragment = new ElectionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        expView = (ExpandableListView) rootView.findViewById(R.id.expandableListView);

        populateDummyList();
        mElectionAdapter = new ExpandableListAdapter(this.getContext(),
                elections,
                electionItemHash);
        expView.setAdapter(mElectionAdapter); //I have no idea why this doesn't work right.
        //Something about it being a fragment and not an activity or something?

        return rootView;
    }

    private void populateDummyList(){
        elections = new ArrayList<String>();
        electionItemHash = new HashMap<String, List<String>>();

        elections.add("Primaries");
        elections.add("General Election");
        elections.add("dummy Election");

        List<String> primaries = new ArrayList<String>();
        primaries.add("Republican");
        primaries.add("Democrat");
        primaries.add("Date to vote\n04-05-2016");
        primaries.add("more stuff you need to know");

        List<String> general = new ArrayList<String>();
        general.add("Republican");
        general.add("Democrat");
        general.add("Date to vote\n11-03-2016");
        general.add("vote already guies");

        List<String> dummy = new ArrayList<String>();
        dummy.add("Dummy item");
        dummy.add("Another dummy item");

        electionItemHash.put(elections.get(0), primaries);
        electionItemHash.put(elections.get(1), general);
        electionItemHash.put(elections.get(2), dummy);
    }

    public class FetchElectionTask extends AsyncTask<String, Void, String[]> {
        private Request reqService = null;
        @Override
        protected String[] doInBackground(String... params) {
            System.out.println("point 1");
            if(reqService == null) {
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

            // This will now contain the json string from the server.
            String electionName;
            try {
                ElectionModel eleModel = reqService.getElections().execute();
                electionName = eleModel.getElectionName();
            } catch (IOException e) {
                System.out.println("Error Getting Data From Server");
                electionName = "{}";
            }

            System.out.println(electionName);
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//
//            String electionJsonString = null;
//
//            try {
//                URL url = new URL("https://rolz.org/api/?6d6");
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                System.out.println("point 2");
//                int status = urlConnection.getResponseCode();
//                System.out.println(status);
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//                if (inputStream == null) {
//                    return null;
//                }
//
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line + "\n");
//                }
//
//                if (buffer.length() == 0) {
//                    return null;
//                }
//
//                electionJsonString = buffer.toString();
//                System.out.println(electionJsonString);
//
//            } catch (IOException e) {
//                Log.e("ElectionFragment", "Error ", e);
//                return null;
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final IOException e) {
//                        Log.e("ElectionFragment", "Error closing stream ", e);
//                    }
//                }
//            }


            return null;

        }

    }
}