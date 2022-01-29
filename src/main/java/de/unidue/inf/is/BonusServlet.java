package de.unidue.inf.is;

import de.unidue.inf.is.domain.Drive;
import de.unidue.inf.is.utils.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BonusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Connection connection = DBUtil.getExternalConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT b2.EMAIL, a.ANBIETER, a.AVG_RATING FROM ((" +
                        "SELECT f2.ANBIETER, AVG(CAST(RATING AS DECIMAL)) as AVG_RATING FROM ( " +
                        "        (SELECT f.ANBIETER, s.BEWERTUNG as BID FROM FAHRT f JOIN SCHREIBEN s on f.FID = s.FAHRT) f2 " +
                        "    JOIN " +
                        "        BEWERTUNG b " +
                        "    ON b.BEID = f2.BID " +
                        ") GROUP BY f2.ANBIETER ORDER BY AVG_RATING DESC FETCH FIRST ROW ONLY) a " +
                        "JOIN BENUTZER b2 ON b2.BID = a.ANBIETER)");

            if(!resultSet.next()) return;

            request.setAttribute("driver", resultSet.getString("EMAIL"));
            request.setAttribute("average_rating", resultSet.getString("AVG_RATING"));

            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM FAHRT WHERE ANBIETER = ? AND STATUS = 'offen'"
            );
            stmt.setInt(1, resultSet.getInt("ANBIETER"));
            statement.close();
            ResultSet openDrivesRS = stmt.executeQuery();
            List<Drive> drives = new ArrayList<>();
            while(openDrivesRS.next()) {
                drives.add(new Drive(
                        openDrivesRS.getInt("FID"),
                        openDrivesRS.getString("STARTORT"),
                        openDrivesRS.getString("ZIELORT"),
                        openDrivesRS.getTimestamp("FAHRTDATUMZEIT"),
                        openDrivesRS.getInt("MAXPLAETZE"),
                        openDrivesRS.getInt("FAHRTKOSTEN"),
                        openDrivesRS.getString("STATUS"),
                        openDrivesRS.getInt("ANBIETER"),
                        openDrivesRS.getInt("TRANSPORTMITTEL")
                        )
                );
            }
            request.setAttribute("drives", drives);
            stmt.close();
            connection.close();

            request.getRequestDispatcher("bonus.ftl").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();

        }


    }

}
