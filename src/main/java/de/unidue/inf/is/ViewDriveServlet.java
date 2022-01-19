package de.unidue.inf.is;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class ViewDriveServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //TODO values should be loaded from database
        request.setAttribute("anbieter", "DER Anbieter");
        request.setAttribute("datetime", "2022-01-19, 18:37");
        request.setAttribute("start", "Muelheim");
        request.setAttribute("ziel", "Oer-Erkenschwieg");
        request.setAttribute("nfreiePlaetze", "2");
        request.setAttribute("status", "offen");
        request.setAttribute("description", "ganz entspantes, meistens nicht tödliches Fahrerlebnis.");
        request.setAttribute("averageRating", "3.5");
        //TODO add list of all ratings
        request.getRequestDispatcher("view_drive.ftl").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

}
