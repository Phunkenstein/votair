package com.montedigorno.votair;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
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


import java.io.IOException;

import java.lang.Override;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
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
    private ArrayList<ElectionModel> electionModels;


    public ElectionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        electionModels = new ArrayList<>();

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

        if (elections == null) populateDummyList();
        mElectionAdapter = new ExpandableListAdapter(this.getContext(),
                elections,
                electionItemHash)
//        {
//            //TODO this is where we allow for alternate views from textview, and define them using our data.
//            @Override
//            public View getChildView(int groupPosition, final int childPosition,
//                                     boolean isLastChild, View convertView, ViewGroup parent) {
//
//                final String childText = (String) getChild(groupPosition, childPosition);
//
//                if (convertView == null) {
//                    LayoutInflater infalInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    convertView = infalInflater.inflate(R.layout.main_list_item, null);
//                }
//
//                TextView txtListChild = (TextView) convertView
//                        .findViewById(R.id.lblListItem);
//
//                txtListChild.setText(childText);
//                return convertView;
//            }
//
//        }
        ;
        expView.setAdapter(mElectionAdapter);
        expView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id){
                int numBallotItems = electionModels.get(groupPosition).getBallotItems().size();
                //This will likely all need to change when I implement three-layer expandablelistviews...
                //If I do that at all.  It looks incredibly tricky and cumbersome.  Might replace the 'search'
                //with a context menu of some kind.  Maybe a popup?
                if (childPosition == 0) {
                    Intent calIntent = new Intent(Intent.ACTION_INSERT);
                    calIntent.setData(CalendarContract.Events.CONTENT_URI);
                    calIntent.setType("vnd.android.cursor.item/event");
                    calIntent.putExtra(CalendarContract.Events.TITLE, "Vote in " + electionModels.get(groupPosition).getElectionName());
                    calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "Voting Place"); //TODO get actual polling location here
                    calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
                    try {
                        Date electionDate = new SimpleDateFormat(
                                electionModels.get(groupPosition).getElectionDateFormat()).
                                parse(electionModels.get(groupPosition).getElectionDate());
                        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, electionDate);
                        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, electionDate);
                    } catch (Exception e) {
                        System.out.println("Error parsing date string");
                    }
                    startActivity(calIntent);
                }
                else if (childPosition > numBallotItems){}  //Infostrings here, maybe.
                else { //Ballot item
                    final String[] items = electionModels.get(groupPosition).getBallotItems().
                            get(childPosition - 1).split(",");
                    String selectedItem = items[0];
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Select an item for more information")
                            .setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String searchKey = items[which];
                                    Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                                    search.putExtra(SearchManager.QUERY, searchKey);
                                    startActivity(search);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    // User cancelled the dialog
                                }
                            });
                    ;
//                    String searchKey = electionModels.get(groupPosition).
//                            getBallotItems().get(childPosition - 1).spli  t(",")[0];
//                    Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
//                    search.putExtra(SearchManager.QUERY, searchKey);
//                    startActivity(search);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return true;
            }
        });

        return rootView;
    }

    private void populateDummyList(){
        elections = new ArrayList<>();
        electionItemHash = new HashMap<>();
        elections.add("Loading...");
        List<String> primaries = new ArrayList<>();
        electionItemHash.put(elections.get(0), primaries);
    }

    private void populateList(){
        //TODO get this working for multiple elections, with less hard coding.
        elections.clear();
        electionItemHash.clear();

        for( ElectionModel election : electionModels ) {
            elections.add(election.getElectionName());

            List<List<String>> ballotItems = new ArrayList<>();
            for ( String l : election.getBallotItems() ) {
                ballotItems.add(Arrays.asList(l.split(",")));
            }

            List<String> electionSections = new ArrayList<>();
            electionSections.add("Date To Vote: " + election.getElectionDate());
            for (List<String> section : ballotItems) {
                // Item 1 is section name (Displayed as the item to click to expand this data)
                electionSections.add(section.get(0));
                // Item 2 is section info

                // Further items are choices
                for( int i = 2; i < section.size(); i++) {

                }
            }
            // After choices, display info (may be links)
            if( election.getInfo() != null ) {
                for ( String info : election.getInfo() ) {
                    electionSections.add(info);
                }
            }
            electionItemHash.put(election.getElectionName(), electionSections);
        }
        System.out.println("populating list complete");
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
            System.out.println("in post execute");
            System.out.println(electionItemHash);
            populateList();
            mElectionAdapter.notifyDataSetChanged();
        }
    }
}