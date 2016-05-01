package com.example.uwm.myapplication.backend;

import java.util.ArrayList;
import java.util.List;

// Import our custom objectify wrapper
import static com.example.uwm.myapplication.backend.OfyService.ofy;

// Import the data models
import com.example.uwm.myapplication.backend.models.ElectionModel;
import com.example.uwm.myapplication.backend.models.ProfileModel;
import com.example.uwm.myapplication.backend.models.InfoModel;

// Import notations for the backend API configuration.
import javax.inject.Named;
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
    public InfoModel getVoterInfo() {
        // If we could actually find a place to pull the info from. We would use their Profile
        // (Name, DOB, and Address) to find their polling place, registration status, etc.
        // ProfileModel prof = ofy().load().type(ProfileModel.class).filter("regId", regId).first().now();

        // Create the static data model.
        InfoModel infoModel = new InfoModel();
        infoModel.setPollingPlace("3200 N. Cramer St. Milwaukee WI, 53212");
        infoModel.setRequiredDocumentation("State Issued Photo ID");
        infoModel.setVotingHoursStart("7:00am");
        infoModel.setVotingHoursEnd("8:00pm");
        infoModel.setRestrictions("Voters must be at least 18 years old");

        return infoModel;
    }

    @ApiMethod(name = "getElectionIds")
    public MyResponse getElectionIds() {
        // Loop over the ElectionModels in the datastore
        List<ElectionModel> models = ofy().load().type(ElectionModel.class).limit(10).list();
        List<Long> ids = new ArrayList<>();
        MyResponse resp = new MyResponse();

        // Add them to the response.
        for (ElectionModel m : models) {
            ids.add(m.id);
        }

        resp.setMyData(ids);
        return resp;
    }

    @ApiMethod(name = "getElection")
    public ElectionModel getElection(@Named("id") Long id) {
        // Return the requested ElectionModel, or null of none was found.
        return ofy().load().type(ElectionModel.class).filter("id", id).first().now();
    }

    @ApiMethod(name = "updateProfile")
    public MyResponse updateProfile( ProfileModel profile ) {
        MyResponse resp = new MyResponse();

        // Now find the user's profile via the regId, and update their values.
        ProfileModel profRec = ofy().load().type(ProfileModel.class).filter("regId", profile.getRegId()).first().now();
        if (profRec != null) ofy().delete().entity(profRec);
        ofy().save().entity(profile).now();

        resp.setSuccess(true);
        return resp;
    }

    @ApiMethod(name = "getProfile")
    public ProfileModel getProfile( @Named( "regId" )String regId ) {
        // Get profile model by regId
        ProfileModel prof = ofy().load().type(ProfileModel.class).filter("regId", regId).first().now();

        // Return it or an empty model if not found.
        if (prof ==  null) return new ProfileModel();
        return prof;
    }
}