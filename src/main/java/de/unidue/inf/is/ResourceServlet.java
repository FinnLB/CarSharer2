package de.unidue.inf.is;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class ResourceServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String file = request.getQueryString();
        File template = new File("src/main/webapp/WEB-INF/res/" + file);

        byte[] fileBytes = Files.readAllBytes(template.toPath());  //Path.of(URI.create("webapp/WEB-INF/res/" + file))
        response.getOutputStream().write(fileBytes);
        response.getOutputStream().close();
        //request.getRequestDispatcher("/res/" + file).forward(request, response);
    }


}
