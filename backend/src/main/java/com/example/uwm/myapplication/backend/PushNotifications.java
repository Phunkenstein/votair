package com.example.uwm.myapplication.backend;

import com.example.uwm.myapplication.backend.models.ElectionModel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import static com.example.uwm.myapplication.backend.OfyService.ofy;

@SuppressWarnings("serial")
public class PushNotifications extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // Get a list of elections, max 10.
            List<ElectionModel> records = ofy().load().type(ElectionModel.class).limit(10).list();
            for(ElectionModel record : records) {
                // Compute the number of days from the election.
                Date electionDate = new SimpleDateFormat(record.getElectionDateFormat()).parse(record.getElectionDate());
                Date todaysDate = new Date();
                long diffInMillies = electionDate.getTime() - todaysDate.getTime();
                int difInDays = (int) TimeUnit.DAYS.convert(diffInMillies,TimeUnit.MILLISECONDS);

                // Only attempt to send the message if the date hasn't passed.
                if( difInDays >= 0 ) {
                    MessagingEndpoint gcm = new MessagingEndpoint();
                    // The endpoint takes care of filtering profiles according to this difInDays
                    gcm.sendMessage(difInDays, "Election Coming Up On" + electionDate.toString() + ": " + record.getElectionName());
                }
            }
        }
        catch (Exception ex) {}
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}