package de.unidue.inf.is;


import de.unidue.inf.is.utils.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

public final class NewRatingServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int fid = Integer.parseInt(request.getParameter("fid"), 10);
        String tmp = request.getParameter("nid");
        int nid = 1;
        if (tmp != null){
            nid = Integer.parseInt(tmp, 10);
        }
        request.setAttribute("fid", fid);
        request.setAttribute("nid", nid);
        try {
            Connection con = DBUtil.getExternalConnection();
            PreparedStatement stmt = con.prepareStatement("SELECT ANBIETER FROM dbp167.SEARCH WHERE FID = ?");

            stmt.setInt(1, fid);

            ResultSet query = stmt.executeQuery();
            while (query.next()) {
                //int id = query.getInt("fid");
                //String startort = query.getString("startort");
                //String zielort = query.getString("zielort");
                //Date fahrtdatumzeit = query.getDate("fahrtdatumzeit");
                //String status = query.getString("status");
                String anbieter = query.getString("anbieter");
                //String transportmittel = query.getString("transportmittel");
                request.setAttribute("fahrer", anbieter);
            }
            query.close();
            stmt.close();
            con.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        request.getRequestDispatcher("new_rating.ftl").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int rating = Integer.parseInt(request.getParameter("rating"), 10);
        int fid = Integer.parseInt(request.getParameter("fid"), 10);
        int nid = Integer.parseInt(request.getParameter("nid"), 10);
        String msg = request.getParameter("msg");

        try {

            Connection con = DBUtil.getExternalConnection();
            PreparedStatement test = con.prepareStatement("SELECT * FROM SCHREIBEN s WHERE s.BENUTZER = ? and s.FAHRT = ?");
            test.setInt(1, nid);
            test.setInt(2, fid);
            ResultSet test_res = test.executeQuery();
            boolean rating_exist = false;
            int update_beid = -1;
            if(test_res.next()){
                rating_exist = true;
                update_beid =test_res.getInt("Bewertung");
            }
            test_res.close();
            test.close();
            if(rating_exist){
                // user has already rated that drive -> update instead of insert.
                PreparedStatement stmt_updatebew = con.prepareStatement("UPDATE DBP167.BEWERTUNG SET textnachricht = ?, rating = ? WHERE beid=?");
                stmt_updatebew.setString(1, msg);
                stmt_updatebew.setInt(2, rating);
                stmt_updatebew.setInt(3, update_beid);
                stmt_updatebew.executeUpdate();
                stmt_updatebew.close();
            }else {
                // insert new row.
                PreparedStatement stmt1 = con.prepareStatement("INSERT INTO BEWERTUNG (TEXTNACHRICHT, RATING) VALUES (?, ?)");

                stmt1.setString(1, msg);
                stmt1.setInt(2, rating);

                stmt1.executeUpdate();

                PreparedStatement stmt1r = con.prepareStatement("SELECT IDENTITY_VAL_LOCAL() AS VAL FROM SYSIBM.SYSDUMMY1");
                ResultSet result = stmt1r.executeQuery();
                result.next();
                int beid = result.getInt(1);
                result.close();
                stmt1.close();

                PreparedStatement stmt2 = con.prepareStatement("INSERT INTO SCHREIBEN (BENUTZER, FAHRT, BEWERTUNG) VALUES (?, ?, ?)");

                stmt2.setInt(1, nid);
                stmt2.setInt(2, fid);
                stmt2.setInt(3, beid);

                stmt2.executeUpdate();
                stmt2.close();
            }
            con.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        response.sendRedirect("view_drive?kunden_id=" + nid + "&fahrt_id=" + fid);
    }

}
