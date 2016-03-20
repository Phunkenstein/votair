package com.example.uwm.myapplication.backend;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Chris Harmon on 3/20/2016.
 */
public class ServerBase extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().write("{\n" +
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
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }

}
