package de.unidue.inf.is;

import de.unidue.inf.is.utils.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class viewMainServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String kid = request.getParameter("kunden_id");//TODO diese Loesung koennte Sicherheitsluecken enthalten.
        int kunden_id = 1;
        try{
            kunden_id = Integer.parseInt(kid);
        }catch (Exception e){
            System.out.println("ViewDirveServlet: cant parse kunden_id "+kid+" to int");
        }
        request.setAttribute("kunden_id", kunden_id);
        try {
            Connection connection = DBUtil.getExternalConnection();
            // reservierte fahrten anzeigen
            PreparedStatement stmt_res = connection.prepareStatement("SELECT f.STARTORT, f.ZIELORT, f.STATUS, r.ANZPLAETZE, f.FID FROM (SELECT * FROM DBP167.RESERVIEREN WHERE KUNDE = ?) r JOIN (SELECT * FROM DBP167.FAHRT) f ON r.FAHRT = f.FID");
            stmt_res.setInt(1, kunden_id);
            ResultSet qerry_res = stmt_res.executeQuery();
            StringBuilder res_list = new StringBuilder("");
            //for every fahrt that kunde has reserviert: show buttun(view_drive), von, nach, status
            while (qerry_res.next()){
                String von = qerry_res.getString("Startort");
                String nach = qerry_res.getString("Zielort");
                String status = qerry_res.getString("status");
                int fahrt_id = qerry_res.getInt("FID");
                int anzplaetze = qerry_res.getInt("anzPlaetze");
                res_list.append("<div class=\"border\"><a href=\"view_drive?kunden_id=").append(kunden_id)
                        .append("&fahrt_id=").append(fahrt_id).append("\">show details</a> <br> von:").append(von)
                        .append("<br> nach:").append(nach).append("<br> status: ").append(status)
                        .append("<br> reserviert: ").append(anzplaetze).append("</div>");
            }
            qerry_res.close();
            stmt_res.close();
            request.setAttribute("res_list", res_list.toString());

            // offene fahrten anzeigen
            PreparedStatement stmt_open = connection.prepareStatement("SELECT * FROM DBP167.FAHRT WHERE STATUS=?");
            stmt_open.setString(1, "offen");
            ResultSet querry_open = stmt_open.executeQuery();
            StringBuilder open_list = new StringBuilder("");
            while(querry_open.next()){
                String von = querry_open.getString("Startort");
                String nach = querry_open.getString("Zielort");
                String status = querry_open.getString("status");
                int fahrt_id = querry_open.getInt("FID");
                int kosten = querry_open.getBigDecimal("Fahrtkosten").intValue();
                int maxPlaetze = querry_open.getInt("maxplaetze");
                //get belegte plaetze
                PreparedStatement stmt_plaetzeFrei = connection.prepareStatement("SELECT t.summ FROM (SELECT SUM(r.anzplaetze) AS summ, r.FAHRT FROM DBP167.RESERVIEREN r GROUP BY r.FAHRT) t WHERE t.FAHRT = ?");
                stmt_plaetzeFrei.setInt(1, fahrt_id);
                ResultSet query_plaetzeFrei = stmt_plaetzeFrei.executeQuery();
                int belegtPlaetze = 0;
                if(query_plaetzeFrei.next()){
                    belegtPlaetze = query_plaetzeFrei.getInt("summ");
                }
                query_plaetzeFrei.close();
                stmt_plaetzeFrei.close();
                //
                open_list.append("<div class=\"border\"><a href=\"view_drive?kunden_id=").append(kunden_id)
                        .append("&fahrt_id=").append(fahrt_id).append("\">show details</a> <br> von:").append(von)
                        .append("<br> nach:").append(nach).append("<br> status: ").append(status)
                        .append("<br> kosten: ").append(kosten).append("€ <br> Plaetze: ").append(maxPlaetze-belegtPlaetze)
                        .append(" von ").append(maxPlaetze).append(" sind frei")
                        .append("</div>");
            }
            querry_open.close();
            stmt_open.close();
            request.setAttribute("open_list", open_list.toString());
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        request.getRequestDispatcher("view_main.ftl").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("viewMainServlet.doPost wurde aufgerufen");
    }

}
