package com.montedigorno.votair;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

// Import our backend API and data Models, as well as necessary google APIs
import com.example.uwm.myapplication.backend.request.model.ElectionModel;
import com.example.uwm.myapplication.backend.request.Request;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

/**
 * Fragment for the Election page (2) - the main page in the app.
 */
public class ElectionFragment extends Fragment {

    // Fields for displaying elections.
    private Context context;
    private ArrayList<String> elections;
    private HashMap<String, List<String>> electionItemHash;
    private ExpandableListView expView;
    private ExpandableListAdapter mElectionAdapter;
    private ArrayList<ElectionModel> electionModels;

    // Fields for saving the regID and deadlines
    private SharedPreferences profile;
    public static final String PREFS_NAME = "ProfilePrefs";


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Initialize fields.
        context = this.getActivity();
        profile = context.getSharedPreferences(PREFS_NAME, 0);
        electionModels = new ArrayList<>();

        // Fetch the election models from the backend API.
        FetchElectionTask electionTask = new FetchElectionTask();
        electionTask.execute();
    }


    /**
     * Returns a new instance of this fragment.
     */
    public static ElectionFragment newInstance(Context context) {
        ElectionFragment fragment = new ElectionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment and grab the expandable list view.
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        expView = (ExpandableListView) rootView.findViewById(R.id.expandableListView);

        // If election is null we are still waiting for data from the server. Display the loading text.
        if (elections == null) populateDummyList();

        // Create the adapter.
        mElectionAdapter = new ExpandableListAdapter(this.getContext(), elections, electionItemHash);

        // Connect the adapter.
        expView.setAdapter(mElectionAdapter);
        expView.expandGroup(0); // Set top item expanded by default
        expView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id){
                int numBallotItems = electionModels.get(groupPosition).getBallotItems().size();

                // First item is always the date, create and add a calendar item.
                if (childPosition == 0) {
                    Intent calIntent = new Intent(Intent.ACTION_INSERT);
                    calIntent.setData(CalendarContract.Events.CONTENT_URI);
                    calIntent.setType("vnd.android.cursor.item/event");
                    calIntent.putExtra(CalendarContract.Events.TITLE, "Vote in " + electionModels.get(groupPosition).getElectionName());
                    calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "Voting Place"); //TODO get actual polling location here
                    calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

                    // Parse the date with the date format.
                    try {
                        Date electionDate = new SimpleDateFormat(electionModels.get(groupPosition).getElectionDateFormat()).parse(electionModels.get(groupPosition).getElectionDate());
                        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, electionDate);
                        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, electionDate);
                    } catch (Exception e) {
                        System.out.println("Error parsing date string");
                    }

                    startActivity(calIntent);
                } else if (childPosition <= numBallotItems){
                    // Ballot items (others are info strings)
                    final String[] items = electionModels.get(groupPosition).getBallotItems().get(childPosition - 1).split(",");

                    // Append some titles to these fields.
                    String selectedItem = items[0];
                    items[0] = "Ballot Item: " + selectedItem;
                    for( int i = 1; i < items.length; i++ ) {
                        items[i] = "Choice " + i + ": " + items[i];
                    }

                    // Create the dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Select an item for more information")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String searchKey = items[which].split(": ")[1];
                                Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                                search.putExtra(SearchManager.QUERY, searchKey);
                                startActivity(search);
                            }
                        }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

                    // Display it.
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return true;
            }
        });

        return rootView;
    }


    private void populateDummyList(){
        // While the app is fetching data, display the loading string.
        elections = new ArrayList<>();
        electionItemHash = new HashMap<>();
        elections.add("Loading...");
        List<String> primaries = new ArrayList<>();
        electionItemHash.put(elections.get(0), primaries);
    }


    private void populateList(){
        // Clear the lists to prevent duplicates.
        elections.clear();
        electionItemHash.clear();

        // Loop over election models.
        for( ElectionModel election : electionModels ) {
            elections.add(election.getElectionName());

            // Parse the list of ballot items.
            List<List<String>> ballotItems = new ArrayList<>();
            for ( String l : election.getBallotItems() ) {
                ballotItems.add(Arrays.asList(l.split(",")));
            }

            // Loop over ballot items
            List<String> electionSections = new ArrayList<>();
            electionSections.add("Date To Vote: " + election.getElectionDate());
            for (List<String> section : ballotItems) {
                // Item 1 is section name (Displayed as the item to click to expand this data)
                electionSections.add(section.get(0));
            }

            // After choices, display info.
            if( election.getInfo() != null ) {
                for ( String info : election.getInfo() ) {
                    electionSections.add(info);
                }
            }

            electionItemHash.put(election.getElectionName(), electionSections);
        }
    }


    public class FetchElectionTask extends AsyncTask<String, Void, String[]> {
        private Request reqService = null;

        @Override
        protected String[] doInBackground(String... params) {
            // Initialize the request service if we have not yet.
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

            // Call the backend API.
            try {
                // Get election IDS
                List<Long> electionIds = reqService.getElectionIds().execute().getMyData();

                // Get each election one at a time and insert it into sorted order.
                for (Long id : electionIds ) {
                    ElectionModel eleModel = reqService.getElection(id).execute();

                    // Just add to the list if it is empty.
                    if( electionModels.size() == 0 ) {
                        electionModels.add(eleModel);
                    } else {
                        // Otherwise go through the list and place ourselves int the correct spot.
                        long electionDate = 0;
                        try {
                            electionDate = new SimpleDateFormat(eleModel.getElectionDateFormat()).parse(eleModel.getElectionDate()).getTime();
                        } catch (Exception r) {}

                        // Using a temp list to build the new list.
                        ArrayList<ElectionModel> newElectionModels = new ArrayList();

                        // So we don't add ourselves twice.
                        Boolean wasAdded = false;

                        // Go through the list.
                        for (int i = 0; i < electionModels.size(); i++) {
                            ElectionModel otherEleModel = electionModels.get(i);
                            long otherElectionDate = 0;
                            try {
                                otherElectionDate = new SimpleDateFormat(otherEleModel.getElectionDateFormat()).parse(otherEleModel.getElectionDate()).getTime();
                            } catch (Exception r) {}

                            // Place ourselves in the new list before the other election.
                            if (electionDate < otherElectionDate && !wasAdded) {
                                newElectionModels.add(eleModel);
                                wasAdded = true;
                            }
                            newElectionModels.add(otherEleModel);
                        }

                        // If we were not added, we go at the end.
                        if(!wasAdded) newElectionModels.add(eleModel);

                        // Replace the old model with the new one.
                        electionModels = newElectionModels;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error Getting Data From Server");
            }

            return null;
        }

        protected void onPostExecute(String[] strings) {
            // After getting the data save the deadlines so the Info page can access them.
            SharedPreferences.Editor editor = profile.edit();
            editor.putString("regdeadline", electionModels.get(0).getRegistrationDeadline());
            editor.putString("otherdeadlinetitle", electionModels.get(0).getOtherDeadlineTitle());
            editor.putString("otherdeadline", electionModels.get(0).getOtherDeadline());
            editor.commit();

            // And then populate the list and display the adaptor.
            populateList();
            mElectionAdapter.notifyDataSetChanged();
        }
    }
}