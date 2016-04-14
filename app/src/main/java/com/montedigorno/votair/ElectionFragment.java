package com.montedigorno.votair;

import android.content.Context;
import android.content.Intent;
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
                if (childPosition==0){//Date, add to calender
                    Intent calIntent = new Intent(Intent.ACTION_INSERT);
                    calIntent.setData(CalendarContract.Events.CONTENT_URI);
                    calIntent.setType("vnd.android.cursor.item/event");
                    calIntent.putExtra(CalendarContract.Events.TITLE, "Vote in " + electionModels.get(groupPosition).getElectionName());
                    calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "Voting Place"); //TODO get actual polling location here
                    calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
                    try{
                            Date electionDate = new SimpleDateFormat(
                                    electionModels.get(groupPosition).getElectionDateFormat()).
                                    parse(electionModels.get(groupPosition).getElectionDate());
                        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, electionDate);
                        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, electionDate);
                    } catch (Exception e){
                        System.out.println("Error parsing date string");
                    }
                    startActivity(calIntent);
                }
                //TODO Logic to control what to do when a child item is clicked, and to do it.
                return false;
            }
        });

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
        System.out.println("populating dummy list complete");
    }

    private void populateList(){
        //TODO get this working for multiple elections, with less hard coding.
        elections.clear();
        electionItemHash.clear();

        ElectionModel electionModel1 = electionModels.get(0);
        List<List<String>> ballotItems = electionModel1.getBallotItems();

        System.out.println(electionModel1);
        System.out.println(electionModel1.getBallotItems());


        elections.add(electionModel1.getElectionName());
        elections.add("General Election");
        elections.add("dummy Election");

        List<String> primaries = new ArrayList<String>();
        primaries.add(ballotItems.get(0).get(0));
        primaries.add(ballotItems.get(1).get(0));
        primaries.add("Date to vote\n" + electionModel1.getElectionDate());
        primaries.add("server does not currently return an info string with the election, only with each ballot item");

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
                ElectionModel eleModel = reqService.getElections().execute();
                electionModels.add(eleModel);
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