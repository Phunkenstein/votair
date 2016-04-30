package com.example.uwm.myapplication.backend;


import com.example.uwm.myapplication.backend.models.ElectionModel;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
        eleModel1.setElectionName("Presidential");
        eleModel1.setElectionDate("11-08-2016");
        eleModel1.setRegistrationDeadline("10-08-2016");
        eleModel1.setOtherDeadline("11-00-2016");
        eleModel1.setOtherDeadlineTitle("Deadline to Register With a Party");
        List<String> ballotItems1 = new ArrayList<>();
        ballotItems1.add("Presidential,Bernie,Hairpeice,Green");
        ballotItems1.add("Tax Referendum,Yes,No");
        ballotItems1.add("Other,Judge,Different Judge");
        List<String> info1 = new ArrayList<>();
        info1.add("In this well publicized contest The Grassroots Democrat goes up against The Don");
        eleModel1.setInfo(info1);
        eleModel1.setBallotItems(ballotItems1);

        // Election 2
        ElectionModel eleModel2 = new ElectionModel();
        eleModel2.setElectionName("Local Election");
        eleModel2.setElectionDate("09-18-2016");
        eleModel2.setRegistrationDeadline("08-25-2016");
        eleModel2.setOtherDeadline("08-30-2016");
        eleModel2.setOtherDeadlineTitle("Other Deadline");
        List<String> ballotItems2 = new ArrayList<>();
        ballotItems2.add("New School,Yes,No");
        ballotItems2.add("Superintendent,Julia Harris,Benny Hanna");
        ballotItems2.add("Other,Judge,Different Judge");
        List<String> info2 = new ArrayList<>();
        info2.add("An important local election to decide on a new School and Superintendent.");
        eleModel2.setInfo(info2);
        eleModel2.setBallotItems(ballotItems2);

        // Election 3
        ElectionModel eleModel3 = new ElectionModel();
        eleModel3.setElectionName("Emergency Vote");
        eleModel3.setElectionDate("06-01-2016");
        eleModel3.setRegistrationDeadline("05-25-2016");
        eleModel3.setOtherDeadline("05-19-2016");
        eleModel3.setOtherDeadlineTitle("Deadline to Swear Allegiance");
        List<String> ballotItems3 = new ArrayList<>();
        ballotItems3.add("Surrender to Alien Overlords?,Yes,Yes");
        ballotItems3.add("Secret Handshake,The Turkey,Patty Cake");
        ballotItems3.add("Other,Alien Judge,Different Alien Judge");
        List<String> info3 = new ArrayList<>();
        info3.add("Our Gracious Alien Overlords have seen fit to offer us this democratic Election.");
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
