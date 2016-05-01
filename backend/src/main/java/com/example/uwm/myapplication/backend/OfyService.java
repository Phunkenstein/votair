package com.example.uwm.myapplication.backend;

import com.example.uwm.myapplication.backend.models.ElectionModel;
import com.example.uwm.myapplication.backend.models.ProfileModel;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

/**
 * Objectify service wrapper so we can statically register our persistence classes
 * More on Objectify here : https://github.com/objectify/objectify/wiki
 *
 */
public class OfyService {
    static {
        ObjectifyService.register(RegistrationRecord.class);
        ObjectifyService.register(ProfileModel.class);
        ObjectifyService.register(ElectionModel.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}