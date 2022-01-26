package de.unidue.inf.is;

import de.unidue.inf.is.utils.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public final class ViewSearchServlet extends HttpServlet {

    static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
    static SimpleDateFormat sdf_s = new SimpleDateFormat("dd/MM/yyyy");
    static SimpleDateFormat sql_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return sql_date.format(cal.getTime());
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String raw_query = request.getQueryString();
        if (raw_query == null){
            request.setAttribute("query", "");
            request.setAttribute("results", "");
        }else {
            String start = request.getParameter("from").toLowerCase();
            String destination = request.getParameter("to").toLowerCase();
            Date date = Date.valueOf(request.getParameter("date"));
            request.setAttribute("query", start + " >> " + destination + " am " + sdf_s.format(date));
            StringBuilder result = new StringBuilder();

            try {
                Connection con = DBUtil.getExternalConnection();
                PreparedStatement stmt = con.prepareStatement("SELECT * FROM SEARCH WHERE STARTORT = ? AND ZIELORT = ? AND FAHRTDATUMZEIT >= ? AND FAHRTDATUMZEIT <= ?");

                stmt.setString(1, start);
                stmt.setString(2, destination);

                Date von = date;
                String bis = addDays(von, 1);
                stmt.setString(3, sql_date.format(von));
                stmt.setString(4, bis);

                ResultSet query = stmt.executeQuery();
                while (query.next()) {
                    int id = query.getInt("fid");
                    String startort = query.getString("startort");
                    String zielort = query.getString("zielort");
                    Date fahrtdatumzeit = query.getDate("fahrtdatumzeit");
                    String status = query.getString("status");
                    String anbieter = query.getString("anbieter");
                    String transportmittel = query.getString("transportmittel");

                    result.append("<div><table><tr><td>");
                    result.append("Fahrer:");
                    result.append("</td><td>");
                    result.append(anbieter);
                    result.append("</td></tr><tr><td>");
                    result.append("Fahrzeug:");
                    result.append("</td><td>");
                    result.append(transportmittel);
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
                    result.append("</td></tr><tr><td>");
                    result.append("<button onclick=\"watch(1,");
                    result.append(id);
                    result.append(")\">Buchen</button>");
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
