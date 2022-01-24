package de.unidue.inf.is;

import de.unidue.inf.is.utils.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.LinkedList;

public final class ViewDriveServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //TODO request should contain fahrt_id and kunden_id
        String sid = request.getParameter("fahrt_id");
        String kid = request.getParameter("kunden_id");
        int fahrt_id = 4;
        int kunden_id = 1;
        try{
            fahrt_id = Integer.parseInt(sid);
        }catch (Exception e){
            System.out.println("ViewDirveServlet: cant parse fahrt_id "+sid+" to int");
        }
        try{
            kunden_id = Integer.parseInt(kid);
        }catch (Exception e){
            System.out.println("ViewDirveServlet: cant parse kunden_id "+kid+" to int");
        }
        Connection connection = null;
        try{
            connection = DBUtil.getExternalConnection();
            // get general information
            String sql = "SELECT t.name AS anbieter, t.fid, t.startort, t.zielort, t.fahrtdatumzeit, t.maxplaetze, t.status, t.beschreibung FROM " +
                    "    (" +
                    "        SELECT * FROM ( " +
                    "            (SELECT * FROM DBP167.fahrt fi WHERE fi.fid = ?) f " +
                    "            JOIN " +
                    "            (SELECT * FROM DBP167.benutzer) b " +
                    "            ON (b.bid = f.anbieter) " +
                    "        ) " +
                    "    ) t";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, fahrt_id);

            ResultSet query = stmt.executeQuery();
            String startort = "startort";
            String zielort = "zielort";
            Date fahrtdatumzeit = Date.valueOf("2000-01-01");
            int maxplaetze = 0;
            String status = "invalid";
            String anbieter = "invalid";
            String beschreibung = "values for fahrt "+String.valueOf(fahrt_id)+" could not be loaded.";
            if(query.next()) {
                startort = query.getString("startort");
                zielort = query.getString("zielort");
                fahrtdatumzeit = query.getDate("fahrtdatumzeit");
                maxplaetze = query.getInt("maxplaetze");
                status = query.getString("status");
                anbieter = query.getString("anbieter");
                beschreibung = query.getString("beschreibung");
            }
            query.close();
            stmt.close();
            sql = "SELECT t.summ FROM (SELECT SUM(r.anzplaetze) AS summ, r.FAHRT FROM DBP167.RESERVIEREN r GROUP BY r.FAHRT) t WHERE t.FAHRT = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, fahrt_id);
            query = stmt.executeQuery();
            int belegtPlaetze = 0;
            if(query.next()){
                belegtPlaetze = query.getInt("summ");
            }
            query.close();
            stmt.close();
            // get ratings
            sql = "SELECT b.EMAIL AS email, r.TEXTNACHRICHT AS textnachricht, r.RATING AS rating FROM (SELECT * FROM DBP167.BENUTZER) b JOIN (SELECT * FROM DBP167.BEWERTUNG) r ON (b.BID = r.BEID)";
            stmt = connection.prepareStatement(sql);
            StringBuilder ratings_tabledata = new StringBuilder();
            double avg_rating = 0;
            query = stmt.executeQuery();
            int i =0;
            while (query.next()){
                String textnachricht = query.getString("textnachricht");
                int rating = query.getInt("rating");
                String email = query.getString("email");
                ratings_tabledata.append("<tr><th>").append(email).append("</th><th>").append(textnachricht).append("</th><th>").append(rating).append("</th></tr>");
                avg_rating += rating;
                i++;
            }
            query.close();
            stmt.close();
            connection.close();
            //do complicated math to calculate the data to show from the data.
            avg_rating = avg_rating/i;
            String nfreieplaetze = (maxplaetze-belegtPlaetze)+" von "+maxplaetze+" frei";
            //add data to request
            //System.out.println("ViewDriveServlet: query results: (startort="+startort+", zielort="+zielort+", fahrtdatumzeit="+fahrtdatumzeit.toString()+"maxplaetze="+maxplaetze+", status="+status+", anbieter="+anbieter+", beschreibung="+beschreibung+")");
            request.setAttribute("anbieter", anbieter);
            request.setAttribute("datetime", fahrtdatumzeit.toString());
            request.setAttribute("start", startort);
            request.setAttribute("ziel", zielort);
            request.setAttribute("nfreiePlaetze", nfreieplaetze);
            request.setAttribute("status", status);
            request.setAttribute("description", beschreibung);
            request.setAttribute("averageRating", String.valueOf(avg_rating));
            request.setAttribute("ratings_tabledata", ratings_tabledata.toString());
            request.setAttribute("kunden_id", kunden_id);
            request.setAttribute("fahrt_id", fahrt_id);
        }catch (Exception e){
            e.printStackTrace();
        }

        request.getRequestDispatcher("view_drive.ftl").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int resplaetze = Integer.parseInt(request.getParameter("resplaetze"));
        int kunden_id = Integer.parseInt(request.getParameter("kunden_id"));
        int fahrt_id = Integer.parseInt(request.getParameter("fahrt_id"));
        System.out.println("ViewDriveServlet.doPost: resplaetze = "+resplaetze+", kid = "+kunden_id+", fid = "+ fahrt_id);
        try {
            Connection connection = DBUtil.getExternalConnection();
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO DBP167.RESERVIEREN (KUNDE, FAHRT, ANZPLAETZE) VALUES (?, ?, ?)");
            stmt.setInt(1, kunden_id);
            stmt.setInt(2, fahrt_id);
            stmt.setInt(3, resplaetze);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
