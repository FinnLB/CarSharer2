package de.unidue.inf.is;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class NewDriveServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("new_drive.ftl").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String start = request.getParameter("von");
        String ziel = request.getParameter("bis");
        int capacity = 0, costs = 0;
        try {
            capacity = Integer.parseInt(request.getParameter("kapazitaet"));
        }catch (Exception e) {
            System.out.println("cant cast \""+ request.getParameter("kapazitaet") +"\" to int");
        }
        try {
            costs = Integer.parseInt(request.getParameter("kosten"));
        }catch (Exception e){
            System.out.println("cant cast \""+request.getParameter("kosten") +"\" to int");
        }

        Timestamp fahrtdatum = Timestamp.valueOf("2000-01-01 12:00");
        try {
            fahrtdatum = Timestamp.valueOf(request.getParameter("fahrtdatumzeit").replace("T", " "));// TODO how does long parse to date?
        } catch (Exception e) {
            System.out.println("cant cast date and time " + request.getParameter("fahrtdatumzeit"));
            e.printStackTrace();
        }
        System.out.println(new Timestamp(fahrtdatum.getTime()));

        int anbieter = 0;
        int transportmittel = 0;

        Connection connection = null;
        try {
            connection = DBUtil.getExternalConnection();
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO DBP167.FAHRT (startort, zielort, fahrtdatumzeit, maxplaetze, fahrtkosten, status, anbieter, transportmittel, beschreibung) VALUES (?,?,?,?,?,?,?,?,?)");
            Clob beschreibung = connection.createClob();
            beschreibung.setString(1, "grandiose beschreibung, die auch in einem VARCHAR(500) haette sein können.");
            stmt.setString(1, start);
            stmt.setString(2, ziel);
            stmt.setTimestamp(3, new Timestamp(fahrtdatum.getTime()));
            stmt.setInt(4, capacity);
            stmt.setInt(5, costs);
            stmt.setString(6, "offen");
            stmt.setInt(7, anbieter);
            stmt.setInt(8, transportmittel);
            stmt.setClob(9, beschreibung);
            stmt.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
