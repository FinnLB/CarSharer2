package de.unidue.inf.is;

import de.unidue.inf.is.utils.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;

public final class ViewSearchServlet extends HttpServlet {

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");

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
            StringBuilder result = new StringBuilder();

            try {
                Connection con = DBUtil.getExternalConnection();
                PreparedStatement stmt = con.prepareStatement("SELECT * From fahrt WHERE startort = ? AND zielort = ?");

                stmt.setString(1, start);
                stmt.setString(2, destination);

                // TODO setze datum in SQL
                //Date von = Date.valueOf(date);
                //Date bis = Date.valueOf(date);

                ResultSet query = stmt.executeQuery();
                while (query.next()) {
                    String startort = query.getString("startort");
                    String zielort = query.getString("zielort");
                    Date fahrtdatumzeit = query.getDate("fahrtdatumzeit");
                    String status = query.getString("status");
                    String anbieter = query.getString("anbieter");

                    result.append("<div><table><tr><td>");
                    result.append("Fahrer:");
                    result.append("</td><td>");
                    result.append(anbieter);
                    result.append("</td></tr><tr><td>");
                    result.append("Von:");
                    result.append("</td><td>");
                    result.append(startort);
                    result.append("</td></tr><tr><td>");
                    result.append("Nach:");
                    result.append("</td><td>");
                    result.append(zielort);
                    result.append("</td></tr><tr><td>");
                    result.append("Start Zeit:");
                    result.append("</td><td>");
                    result.append(sdf.format(fahrtdatumzeit));
                    result.append("</td></tr><tr><td>");
                    result.append("Status:");
                    result.append("</td><td>");
                    result.append(status);
                    result.append("</td></tr><tr><td></table></div>");
                }
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }

            request.setAttribute("results", result.toString());
        }

        request.getRequestDispatcher("view_search.ftl").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

}
