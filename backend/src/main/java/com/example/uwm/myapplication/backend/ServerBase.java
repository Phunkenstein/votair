package com.example.uwm.myapplication.backend;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import static com.example.uwm.myapplication.backend.OfyService.ofy;


import com.example.uwm.myapplication.backend.models.ElectionModel;
import com.example.uwm.myapplication.backend.models.ProfileModel;
import com.example.uwm.myapplication.backend.models.InfoModel;
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

    @ApiMethod(name = "getVoterInfo")
    public InfoModel getVoterInfo( @Named( "regId" )String regId ) {
        ProfileModel prof = ofy().load().type(ProfileModel.class).filter("regId", regId).first().now();
        // If we could actually find a place to pull the info from. We would use their Profile
        // (Name, DOB, and Address) to find their polling place, registration status, etc.

        InfoModel infoModel = new InfoModel();
        infoModel.setPollingPlace("3400 N Maryland Ave, Milwaukee WI, 53211");
        infoModel.setRequiredDocumentation("State Issued ID");
        infoModel.setVotingHoursStart("7:00 AM");
        infoModel.setVotingHoursEnd("8:00 PM");
        infoModel.setRestrictions("No Jerks");

        return infoModel;
    }

    @ApiMethod(name = "getElectionIds")
    public MyResponse getElectionIds() {
        List<ElectionModel> models = ofy().load().type(ElectionModel.class).limit(10).list();
        List<Long> ids = new ArrayList<>();
        MyResponse resp = new MyResponse();
        for (ElectionModel m : models) {
            ids.add(m.id);
        }
        resp.setMyData(ids);
        return resp;
    }

    @ApiMethod(name = "getElection")
    public ElectionModel getElection(@Named("id") Long id) {
        return ofy().load().type(ElectionModel.class).filter("id", id).first().now();
    }

    @ApiMethod(name = "updateProfile")
    public MyResponse updateProfile( ProfileModel profile ) {
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
        ProfileModel profRec = ofy().load().type(ProfileModel.class).filter("regId", profile.getRegId()).first().now();
        if (profRec != null) ofy().delete().entity(profRec);
        ofy().save().entity(profile).now();

        resp.setSuccess(true);

        return resp;
    }

    @ApiMethod(name = "getProfile")
    public ProfileModel getProfile( @Named( "regId" )String regId ) {
        ProfileModel prof = ofy().load().type(ProfileModel.class).filter("regId", regId).first().now();
        if (prof ==  null) return new ProfileModel();
        return prof;
    }
}
