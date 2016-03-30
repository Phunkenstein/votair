package com.example.uwm.myapplication.backend;

import java.io.IOException;

import javax.inject.Named;
import static com.example.uwm.myapplication.backend.OfyService.ofy;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

/**
 * Created by Chris Harmon on 3/20/2016.
 */
@Api(
        name = "request",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.myapplication.uwm.example.com",
                ownerName = "backend.myapplication.uwm.example.com",
                packagePath=""
        )
)

public class ServerBase {

    @ApiMethod(name = "doGet")
    public MyResponse doGet() {
        MyResponse resp = new MyResponse();
        resp.setData( "{\n" +
                "    \"election\": {\n" +
                "    \"election_date\": \"20160415\",\n" +
                "    \"polling_location\": \"locationstring\",\n" +
                "    \n" +
                "    \"ballot_items\": {\n" +
                "        \"ballot_item_1\": {\n" +
                "            \"info_link\": \"link_here\",\n" +
                "            \"choice1\": \"choice1\",\n" +
                "            \"choice2\": \"choice2\"    \n" +
                "        },\n" +
                "        \"ballot_item_2\": {\n" +
                "            \"info_link\": \"link_here\",\n" +
                "            \"choice1\": \"choice1\",\n" +
                "            \"choice2\": \"choice2\"    \n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}");
        return resp;
    }

    @ApiMethod(name = "updateProfile")
    public MyResponse updateProfile( @Named( "num" )int num, @Named( "regId" )int regId ) {
        MyResponse resp = new MyResponse();

        //// Within the ProfileFragment we should have the following code:
        // public static final String PREFS_NAME = "ProfilePrefs";
        // SharedPreferences profile = context.getSharedPreferences(PREFS_NAME, 0);
        // String regId = profile.getString("registration", false);
        // private Request reqService = null;
//            if(reqService == null) {
//                Request.Builder builder = new Request.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
//                        .setRootUrl("http://10.0.2.2:8080/_ah/api/")
//                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
//                            @Override
//                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
//                                abstractGoogleClientRequest.setDisableGZipContent(true);
//                            }
//                        });
//                builder.setApplicationName("Votair!");
//                reqService = builder.build();
//            }
//
//            String resultJsonString;
//            try {
//                electionJsonString = reqService.updateProfile(regId, <numHours>).execute().getData();
        //       // Check if the json object contains "success" or "failure"
//            } catch (IOException e) {
//                System.out.println("Error Getting Data From Server");
//                electionJsonString = "{}";
//            }

        // Now find the user's profile via the regId, and update their values.
        RegistrationRecord regRec = ofy().load().type(RegistrationRecord.class).filter("regId", regId).first().now();
        regRec.setNumHoursOut(num);
        ofy().save().entity(regRec).now();

        resp.setData("{\"status\": \"success\"}");
        //resp.setData("{\"status\": \"failure\"}");

        return resp;
    }
}
