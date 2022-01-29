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
        String kid = request.getParameter("kunden_id");//TODO diese Loesung koennte Sicherheitsluecken enthalten.
        int fahrt_id = 4;
        int kunden_id = 1;
        try{
            fahrt_id = Integer.parseInt(sid);
        }catch (Exception e){
            System.out.println("ViewDriveServlet: cant parse fahrt_id "+sid+" to int");
        }
        try{
            kunden_id = Integer.parseInt(kid);
        }catch (Exception e){
            System.out.println("ViewDriveServlet: cant parse kunden_id "+kid+" to int");
        }
        Connection connection = null;
        try{
            connection = DBUtil.getExternalConnection();
            // get general information
            PreparedStatement stmt_generalInf = connection.prepareStatement(
                    "SELECT t.name AS anbieter, t.fid, t.startort, t.zielort, t.fahrtdatumzeit, t.maxplaetze, t.status, t.beschreibung FROM (SELECT * FROM ((SELECT * FROM DBP167.fahrt fi WHERE fi.fid = ?) f JOIN (SELECT * FROM DBP167.benutzer) b ON (b.bid = f.anbieter))) t");
            stmt_generalInf.setInt(1, fahrt_id);

            ResultSet query_generalInf = stmt_generalInf.executeQuery();
            String startort = "startort";
            String zielort = "zielort";
            String fahrtdatumzeit = "0000-00-00 OO:OO";
            int maxplaetze = 0;
            String status = "invalid";
            String anbieter = "invalid";
            String beschreibung = "values for fahrt "+fahrt_id+" could not be loaded.";
            if(query_generalInf.next()) {
                startort = query_generalInf.getString("startort");
                zielort = query_generalInf.getString("zielort");
                fahrtdatumzeit = query_generalInf.getDate("fahrtdatumzeit").toString()+" "+query_generalInf.getTime("fahrtdatumzeit").toString();
                maxplaetze = query_generalInf.getInt("maxplaetze");
                status = query_generalInf.getString("status");
                anbieter = query_generalInf.getString("anbieter");
                beschreibung = query_generalInf.getString("beschreibung");
                if(beschreibung == null){beschreibung="";}
            }
            query_generalInf.close();
            stmt_generalInf.close();
            PreparedStatement stmt_plaetzeFrei = connection.prepareStatement("SELECT t.summ FROM (SELECT SUM(r.anzplaetze) AS summ, r.FAHRT FROM DBP167.RESERVIEREN r GROUP BY r.FAHRT) t WHERE t.FAHRT = ?");
            stmt_plaetzeFrei.setInt(1, fahrt_id);
            ResultSet query_plaetzeFrei = stmt_plaetzeFrei.executeQuery();
            int belegtPlaetze = 0;
            if(query_plaetzeFrei.next()){
                belegtPlaetze = query_plaetzeFrei.getInt("summ");
            }
            query_plaetzeFrei.close();
            stmt_plaetzeFrei.close();
            // get ratings
            //Fahrt_bewertungen
            PreparedStatement stmt_ratings = connection.prepareStatement("SELECT b.EMAIL AS email, r.TEXTNACHRICHT AS textnachricht, r.RATING AS rating FROM FAHRT_BEWERTUNG r JOIN BENUTZER b ON b.BID = r.BENUTZER");
            StringBuilder ratings_tabledata = new StringBuilder();
            double avg_rating = 0;
            ResultSet query_ratings = stmt_ratings.executeQuery();
            int i =0;
            while (query_ratings.next()){
                String textnachricht = query_ratings.getString("textnachricht");
                int rating = query_ratings.getInt("rating");
                String email = query_ratings.getString("email");
                ratings_tabledata.append("<tr><th>").append(email).append("</th><th>").append(textnachricht).append("</th><th>").append(rating).append("</th></tr>");
                avg_rating += rating;
                i++;
            }
            query_ratings.close();
            stmt_ratings.close();
            //get if this Kunde has already reserved this Fahrt:
            String sqlAlreadyReserved = "SELECT * FROM DBP167.RESERVIEREN WHERE (KUNDE = ? and FAHRT = ?)";
            PreparedStatement stmt_alreadyRes = connection.prepareStatement(sqlAlreadyReserved);
            stmt_alreadyRes.setInt(1, kunden_id);
            stmt_alreadyRes.setInt(2, fahrt_id);
            ResultSet query_alreadyRes = stmt_alreadyRes.executeQuery();
            String aktion_ftl =
                    "\t\t\t\t<label>\n" +
                    "\t\t\t\t\tAnzahl Plaetze fuer Reservierung: <input type=\"number\" id=\"resplaetze\" name=\"resplaetze\" min=\"1\" max=\"2\">\n" +
                    "\t\t\t\t</label> <br>\n" +
                    "\t\t\t\t<input type=\"submit\" value=\"pla(e)tz(e) reservieren\">\n";
            if(query_alreadyRes.next()){
                aktion_ftl = "you already reserved "+query_alreadyRes.getInt("anzplaetze")+" seats in this drive";
            }
            query_alreadyRes.close();
            stmt_alreadyRes.close();
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
            request.setAttribute("aktion_res", aktion_ftl);

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
            stmt.executeUpdate();
            stmt.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
