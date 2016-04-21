package com.example.uwm.myapplication.backend;


import com.example.uwm.myapplication.backend.models.ElectionModel;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static com.example.uwm.myapplication.backend.OfyService.ofy;

/**
 * Created by Chris Harmon on 4/11/2016.
 */
public class StartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // startup code here
        // ofy().clear();
        List<Key<ElectionModel>> keys = ofy().load().type(ElectionModel.class).keys().list();
        ofy().delete().keys(keys).now();

        //Election 1
        ElectionModel eleModel1 = new ElectionModel();
        eleModel1.setElectionName("Primaries");
        eleModel1.setElectionDate("02-17-2016");
        List<String> ballotItems1 = new ArrayList<>();
        ballotItems1.add("Democratic,<info about this ballot item>,Bernie,Clint");
        ballotItems1.add("Republican,<info about this ballot item>,Zodiac,Hairpiece");
        ballotItems1.add("Other,<info about this ballot item>,Judge,Different Judge");
        List<String> info1 = new ArrayList<>();
        info1.add("www.google.com");
        info1.add("<information about this election>");
        eleModel1.setInfo(info1);
        eleModel1.setBallotItems(ballotItems1);

        // Election 2
        ElectionModel eleModel2 = new ElectionModel();
        eleModel2.setElectionName("Referendum");
        eleModel2.setElectionDate("04-18-2016");
        List<String> ballotItems2 = new ArrayList<>();
        ballotItems2.add("New School,<info about this ballot item>,Yes,No");
        ballotItems2.add("Superintendant,<info about this ballot item>,Julia Harris,Benny Hanna");
        ballotItems2.add("Other,<info about this ballot item>,Judge,Different Judge");
        List<String> info2 = new ArrayList<>();
        info2.add("www.google.com");
        info2.add("<information about this election>");
        eleModel2.setInfo(info2);
        eleModel2.setBallotItems(ballotItems2);

        // Election 3
        ElectionModel eleModel3 = new ElectionModel();
        eleModel3.setElectionName("Emergency Vote");
        eleModel3.setElectionDate("07-20-2016");
        List<String> ballotItems3 = new ArrayList<>();
        ballotItems3.add("Surrender to Alien Overlords?,<info about this ballot item>,Yes,Yes");
        ballotItems3.add("Secret Handshake,<info about this ballot item>,The Turkey,Patty Cake");
        ballotItems3.add("Other,<info about this ballot item>,Alien Judge,Different Alien Judge");
        List<String> info3 = new ArrayList<>();
        info3.add("www.google.com");
        info3.add("<information about this election>");
        eleModel3.setInfo(info3);
        eleModel3.setBallotItems(ballotItems3);

        // Saving them to the datastore.
        ofy().save().entity(eleModel1).now();
        ofy().save().entity(eleModel2).now();
        ofy().save().entity(eleModel3).now();



    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // shutdown code here
    }

}
