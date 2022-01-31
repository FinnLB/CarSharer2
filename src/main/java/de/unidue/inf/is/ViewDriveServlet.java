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

    public static int getNbelegtPlaetze(int fahrt_id, Connection con) throws SQLException {
        PreparedStatement stmt_plaetzeFrei = con.prepareStatement("SELECT t.summ FROM (SELECT SUM(r.anzplaetze) AS summ, r.FAHRT FROM DBP167.RESERVIEREN r GROUP BY r.FAHRT) t WHERE t.FAHRT = ?");
        stmt_plaetzeFrei.setInt(1, fahrt_id);
        ResultSet query_plaetzeFrei = stmt_plaetzeFrei.executeQuery();
        int belegtPlaetze = 0;
        if(query_plaetzeFrei.next()){
            belegtPlaetze = query_plaetzeFrei.getInt("summ");
        }
        query_plaetzeFrei.close();
        stmt_plaetzeFrei.close();
        return belegtPlaetze;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sid = request.getParameter("fahrt_id");
        String kid = request.getParameter("kunden_id");//diese Loesung koennte Sicherheitsluecken enthalten.
        int fahrt_id = 4;
        int kunden_id = 1;  // dieser defaultwert könnte für den kunden nr 1 probleme haben.
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
                    "SELECT t.name AS anbieter, t.email, t.BID, t.fid, t.startort, t.zielort, t.fahrtdatumzeit, t.maxplaetze, t.status, t.beschreibung, t.FAHRTKOSTEN, trans.NAME, trans.ICON FROM (SELECT * FROM ((SELECT * FROM DBP167.fahrt fi WHERE fi.fid = ?) f JOIN (SELECT * FROM DBP167.benutzer) b ON (b.bid = f.anbieter))) t JOIN DBP167.TRANSPORTMITTEL trans ON t.TRANSPORTMITTEL = trans.TID");
            stmt_generalInf.setInt(1, fahrt_id);

            ResultSet query_generalInf = stmt_generalInf.executeQuery();
            String startort = "startort";
            String zielort = "zielort";
            String fahrtdatumzeit = "0000-00-00 OO:OO";
            int maxplaetze = 0;
            String status = "invalid";
            String anbieter = "invalid";
            int anbieter_id = -1;
            String anbieter_email = "invalid@undefined.NaN";
            String beschreibung = "values for fahrt "+fahrt_id+" could not be loaded.";
            String kosten = "invalid";
            String transportmittel = "invalid";
            String transportmittel_icon_path = "pfad/icons/invalid.png";
            if(query_generalInf.next()) {
                startort = query_generalInf.getString("startort");
                zielort = query_generalInf.getString("zielort");
                fahrtdatumzeit = query_generalInf.getDate("fahrtdatumzeit").toString()+" "+query_generalInf.getTime("fahrtdatumzeit").toString();
                maxplaetze = query_generalInf.getInt("maxplaetze");
                status = query_generalInf.getString("status");
                anbieter = query_generalInf.getString("anbieter");
                beschreibung = query_generalInf.getString("beschreibung");
                if(beschreibung == null){beschreibung="";}
                kosten = String.valueOf(query_generalInf.getInt("Fahrtkosten"));
                anbieter_email = query_generalInf.getString("email");
                transportmittel = query_generalInf.getString("name");
                transportmittel_icon_path = "res?"+query_generalInf.getString("Icon");
                anbieter_id = query_generalInf.getInt("BID");

            }
            query_generalInf.close();
            stmt_generalInf.close();

            int belegtPlaetze = getNbelegtPlaetze(fahrt_id, connection);

            // get ratings
            //Fahrt_bewertungen
            PreparedStatement stmt_ratings = connection.prepareStatement("SELECT ben.EMAIL, bew_sch.TEXTNACHRICHT, bew_sch.RATING FROM ((SELECT sch.BENUTZER, sch.FAHRT, bew.TEXTNACHRICHT, bew.RATING, bew.ERSTELLUNGSDATUM FROM (DBP167.BEWERTUNG bew JOIN DBP167.SCHREIBEN sch on bew.BEID = sch.BEWERTUNG)) bew_sch JOIN DBP167.BENUTZER ben ON ben.BID = bew_sch.BENUTZER) WHERE FAHRT = ? ORDER BY bew_sch.ERSTELLUNGSDATUM");
            stmt_ratings.setInt(1, fahrt_id);
            ResultSet query_ratings = stmt_ratings.executeQuery();

            StringBuilder ratings_tabledata = new StringBuilder();
            double avg_rating = 0;
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

            //aktionsleiste
            String delete_drive = "";
            String aktion_ftl = "";
            if(anbieter_id == kunden_id){
                //currently loged in kunde is owner of this drive
                delete_drive = "<input type=\"submit\" value=\"Diese Fahrt Loeschen. (diese Aktion kann nicht rueckgaengig gemacht werden)\">";
                aktion_ftl = "Du kannst keine eigene Fahrt resevieren.";
            }else{
                //get if this Kunde has already reserved this Fahrt:
                PreparedStatement stmt_alreadyRes = connection.prepareStatement("SELECT * FROM DBP167.RESERVIEREN WHERE (KUNDE = ? and FAHRT = ?)");
                stmt_alreadyRes.setInt(1, kunden_id);
                stmt_alreadyRes.setInt(2, fahrt_id);
                ResultSet query_alreadyRes = stmt_alreadyRes.executeQuery();

                if(query_alreadyRes.next()){
                    aktion_ftl = "you already reserved "+query_alreadyRes.getInt("anzplaetze")+" seats in this drive";
                }else{
                    //make action to reserve seats possible
                    aktion_ftl =
                            "<label>Anzahl Plaetze fuer Reservierung: " +
                                    "<input type=\"number\" id=\"resplaetze\" name=\"resplaetze\" min=\"1\" max=\""+Math.min(maxplaetze-belegtPlaetze, 2)+"\">" +
                                    "</label> <br>" +
                                    "<input type=\"submit\" value=\"pla(e)tz(e) reservieren\">";
                }

                query_alreadyRes.close();
                stmt_alreadyRes.close();
            }

            connection.close();
            //do complicated math to calculate the features to show from the raw data.
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
            request.setAttribute("kosten", kosten);
            request.setAttribute("anbieter_email", anbieter_email);
            request.setAttribute("transportmittel", transportmittel);
            request.setAttribute("transportmittel_icon_path", transportmittel_icon_path);
            request.setAttribute("delete_drive", delete_drive);

        }catch (Exception e){
            e.printStackTrace();
        }

        request.getRequestDispatcher("view_drive.ftl").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = request.getParameter("type");
        int kunden_id = Integer.parseInt(request.getParameter("kunden_id"));
        int fahrt_id = Integer.parseInt(request.getParameter("fahrt_id"));
        if ("reserve".equals(type)) {
            int resplaetze = Integer.parseInt(request.getParameter("resplaetze"));
            System.out.println("ViewDriveServlet.doPost: reserve resplaetze = " + resplaetze + ", kid = " + kunden_id + ", fid = " + fahrt_id);
            try {
                Connection connection = DBUtil.getExternalConnection();
                PreparedStatement stmt = connection.prepareStatement("INSERT INTO DBP167.RESERVIEREN (KUNDE, FAHRT, ANZPLAETZE) VALUES (?, ?, ?)");
                stmt.setInt(1, kunden_id);
                stmt.setInt(2, fahrt_id);
                stmt.setInt(3, resplaetze);
                stmt.executeUpdate();
                stmt.close();


                //check if closed.
                int belegtPlaetze = getNbelegtPlaetze(fahrt_id, connection);
                int maxPlaetze = -1;
                String status = "invalid";
                PreparedStatement stmt_mp = connection.prepareStatement("SELECT f.maxplaetze, f.status FROM DBP167.Fahrt f WHERE f.fid = ?");
                stmt_mp.setInt(1, fahrt_id);
                ResultSet res_mp = stmt_mp.executeQuery();
                if(res_mp.next()){
                    maxPlaetze = res_mp.getInt("maxplaetze");
                    status = res_mp.getString("status");
                }
                res_mp.close();
                stmt_mp.close();

                if(maxPlaetze <= belegtPlaetze && !"geschlossen".equals(status)){
                    System.out.println("fahrt "+fahrt_id+" shoud be closed, but its "+status);
                    PreparedStatement update = connection.prepareStatement("UPDATE DBP167.Fahrt SET status = ? WHERE fid = ?");
                    update.setString(1, "geschlossen");
                    update.setInt(2, fahrt_id);
                    update.executeUpdate();
                    update.close();
                }
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            response.sendRedirect("view_drive?kunden_id=" + kunden_id + "&fahrt_id=" + fahrt_id);
        }else {
            if ("delete".equals(type)) {

                //type == delete
                System.out.println("ViewDriveServlet.doPost: delete, kid = " + kunden_id + ", fid = " + fahrt_id);
                //delete drive including references
                /*
                SELECT s.BEWERTUNG FROM SCHREIBEN s WHERE FAHRT=84;
                DELETE FROM SCHREIBEN WHERE FAHRT=84;
                DELETE FROM BEWERTUNG b WHERE b.BEID = 83;
                DELETE FROM RESERVIEREN WHERE FAHRT=84;
                DELETE FROM FAHRT WHERE FID=84;
                */

                try {
                    Connection con = DBUtil.getExternalConnection();
                    PreparedStatement getBEID = con.prepareStatement("SELECT s.BEWERTUNG AS BEID FROM SCHREIBEN s WHERE FAHRT=?");
                    getBEID.setInt(1, fahrt_id);
                    ResultSet getBEID_res = getBEID.executeQuery();
                    LinkedList<Integer> beids = new LinkedList<Integer>();
                    while (getBEID_res.next()) {
                        beids.add(getBEID_res.getInt("BEID"));
                    }
                    getBEID_res.close();
                    getBEID.close();

                    //start transaction
                    con.setAutoCommit(false);
                    PreparedStatement dels = con.prepareStatement("DELETE FROM SCHREIBEN WHERE FAHRT=?");
                    dels.setInt(1, fahrt_id);
                    dels.executeUpdate();
                    dels.close();

                    for (Integer beid : beids) {
                        PreparedStatement delb = con.prepareStatement("DELETE FROM BEWERTUNG b WHERE b.BEID = ?");
                        delb.setInt(1, beid);
                        delb.executeUpdate();
                        delb.close();
                    }

                    PreparedStatement delr = con.prepareStatement("DELETE FROM RESERVIEREN WHERE FAHRT=?");
                    delr.setInt(1, fahrt_id);
                    delr.executeUpdate();
                    delr.close();

                    PreparedStatement delf = con.prepareStatement("DELETE FROM FAHRT WHERE FID=?");
                    delf.setInt(1, fahrt_id);
                    delf.executeUpdate();
                    delf.close();
                    con.commit(); // execute transaction
                    con.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                //auf hauptseite weiterleiten
                response.sendRedirect("view_main?kunden_id=" + kunden_id);
            }else {
                //type == gotoMain
                response.sendRedirect("view_main?kunden_id=" + kunden_id);
            }
        }
    }
}
