package com.example.uwm.myapplication.backend;

import java.io.IOException;

import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    public MyResponse get(@Named("name") String name) {
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
}
