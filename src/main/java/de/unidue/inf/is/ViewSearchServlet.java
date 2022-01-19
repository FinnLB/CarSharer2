package de.unidue.inf.is;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class ViewSearchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String raw_query = request.getQueryString();
        if (raw_query == null){
            request.setAttribute("query", "");
            request.setAttribute("results", "");
        }else {
            String start = request.getParameter("from");
            String destination = request.getParameter("to");
            String date = request.getParameter("date");
            request.setAttribute("query", start + ">>" + destination + " on " + date);
            request.setAttribute("results", "");
        }

        request.getRequestDispatcher("view_search.ftl").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

}
