package com.example.uwm.myapplication.backend;

import java.io.IOException;

import javax.inject.Named;
import static com.example.uwm.myapplication.backend.OfyService.ofy;

import com.example.uwm.myapplication.backend.models.ElectionModel;
import com.example.uwm.myapplication.backend.models.ProfileModel;
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

    @ApiMethod(name = "getElections")
    public ElectionModel getElections() {
        ElectionModel eleModel = new ElectionModel();
        eleModel.setElectionName("Hopefully not trump.");
        return eleModel;
    }

    @ApiMethod(name = "updateProfile")
    public MyResponse updateProfile( ProfileModel profile, @Named( "regId" )int regId ) {
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
//                electionJsonString = reqService.updateProfile(profile, regId).execute().getData();
        //       // Check if the json object contains "success" or "failure"
//            } catch (IOException e) {
//                System.out.println("Error Getting Data From Server");
//                electionJsonString = "{}";
//            }

        // Now find the user's profile via the regId, and update their values.
        RegistrationRecord regRec = ofy().load().type(RegistrationRecord.class).filter("regId", regId).first().now();
        regRec.setProfile(profile);

        resp.setSuccess(true);

        return resp;
    }

    @ApiMethod(name = "getProfile")
    public ProfileModel getProfile( @Named( "regId" )int regId ) {
        RegistrationRecord regRec = ofy().load().type(RegistrationRecord.class).filter("regId", regId).first().now();
        return regRec.getProfile();
    }
}
