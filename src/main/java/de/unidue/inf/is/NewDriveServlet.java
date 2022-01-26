package de.unidue.inf.is;

import de.unidue.inf.is.domain.Vehicle;
import de.unidue.inf.is.utils.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class NewDriveServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int kundenId = -1;
        try {
             kundenId = Integer.parseInt(request.getParameter("kunden_id"));
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Can't parse parameter kunden_id.");
            request.getRequestDispatcher("error").forward(request, response);
        }

        try {
            List<Vehicle> vehicles = new ArrayList<>();
            Connection connection = DBUtil.getExternalConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM TRANSPORTMITTEL");
            while (resultSet.next()) {
                vehicles.add(new Vehicle(
                        resultSet.getInt("TID"),
                        resultSet.getString("NAME"),
                        resultSet.getString("ICON")
                ));
            }
            statement.close();
            connection.close();
            request.setAttribute("vehicles", vehicles);
            request.setAttribute("kunden_id", kundenId);
        } catch(Exception exception) {
            exception.printStackTrace();
        }
        request.getRequestDispatcher("new_drive.ftl").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int capacity, costs, transportmittel, fahrtId, kundenId;
        try {
            kundenId = Integer.parseInt(request.getParameter("kunden_id"));
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Can't parse parameter kunden_id.");
            request.getRequestDispatcher("error.ftl").forward(request, response); return;
        }

        String start = request.getParameter("von");
        String ziel = request.getParameter("bis");

        try {
            capacity = Integer.parseInt(request.getParameter("kapazitaet"));
        }catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Can't parse parameter kapazitaet.");
            request.getRequestDispatcher("error.ftl").forward(request, response); return;
        }
        try {
            costs = Integer.parseInt(request.getParameter("kosten"));
        }catch (Exception e){
            e.printStackTrace();
            request.setAttribute("error", "Can't parse parameter kosten.");
            request.getRequestDispatcher("error.ftl").forward(request, response); return;
        }

        Timestamp fahrtdatum;
        try {
            fahrtdatum = Timestamp.valueOf(request.getParameter("fahrtdatumzeit").replace("T", " ") + ":00");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Can't parse parameter fahrtdatumzeit.");
            request.getRequestDispatcher("error.ftl").forward(request, response); return;
        }
        System.out.println(new Timestamp(fahrtdatum.getTime()));

        try {
            transportmittel = Integer.parseInt(request.getParameter("transportmittel"));
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Can't parse parameter transportmittel.");
            request.getRequestDispatcher("error.ftl").forward(request, response); return;
        }

        String beschreibung = "";
        try {
            beschreibung = request.getParameter("beschreibung");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Can't parse parameter beschreibung.");
            request.getRequestDispatcher("error.ftl").forward(request, response);
            return;
        }

        try {
            Connection connection = DBUtil.getExternalConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO FAHRT (startort, zielort, fahrtdatumzeit, maxplaetze, fahrtkosten, status, anbieter, transportmittel, beschreibung) VALUES (?,?,?,?,?,?,?,?,?)",
                    new String[] {"FID"}
            );
            Clob beschreibungClob = connection.createClob();
            beschreibungClob.setString(1, beschreibung);
            stmt.setString(1, start);
            stmt.setString(2, ziel);
            stmt.setTimestamp(3, new Timestamp(fahrtdatum.getTime()));
            stmt.setInt(4, capacity);
            stmt.setInt(5, costs);
            stmt.setString(6, "offen");
            stmt.setInt(7, kundenId);
            stmt.setInt(8, transportmittel);
            stmt.setClob(9, beschreibungClob);
            stmt.executeUpdate();

            ResultSet resultSet = stmt.getGeneratedKeys();
            resultSet.next();
            fahrtId = resultSet.getInt("FID");
            stmt.close();
            connection.close();
            response.sendRedirect("view_drive?kunden_id=" + kundenId + "&fahrt_id=" + fahrtId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            request.setAttribute("error", "SQL/Database error.");
            request.getRequestDispatcher("error.ftl").forward(request, response);
        }

    }

}
