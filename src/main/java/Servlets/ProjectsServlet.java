package Servlets;

import ContentProviders.ProjectContentProvider;
import Exceptions.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import org.json.JSONObject;
import org.json.JSONArray;


@WebServlet(name = "ProjectsServlet",  urlPatterns = { "/projects" }, initParams = {
        @WebInitParam(name = "q" , value = ""),
        })
public class ProjectsServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(response.SC_NOT_IMPLEMENTED);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String Uid = (String) request.getAttribute("username");
        if(request.getParameter("q") == "" || request.getParameter("q").equals("")) {
            response.setContentType("application/json;charset=UTF-8");
            System.out.println("in ProjectsServlet");
            response.setStatus(response.SC_OK);
            JSONArray map = null;
            try {
                System.out.println("here in projectsservlet for q null");
                map = ProjectContentProvider.getContentsForAllProjects(Uid);
            } catch (ProjectNotFoundException e) {
                e.printStackTrace();
            }
            // catch (UserNotFoundException e){
            // printApiOutputError(e, 401,response);
            // }
            // catch (ProjectAccessForbiddenException e){
            // printApiOutputError(e, 403,response);
            // }
            catch (SQLException e) {
                e.printStackTrace();
            }
            PrintWriter out = response.getWriter();
            out.println(map);
        }
        else {
            response.setContentType("application/json;charset=UTF-8");
            String q = request.getParameter("q");
            JSONArray map = ProjectContentProvider.getSearchedProjects(q, Uid);
            response.setStatus(response.SC_OK);
            PrintWriter out = response.getWriter();
            out.println(map);
        }

    }
    private void printApiOutputError(Throwable e, int statusCode, HttpServletResponse response) throws IOException{
        JSONObject instance = new JSONObject();
        instance.put("status", statusCode);
        instance.put("message", e.getMessage());
        response.setStatus(statusCode);
        PrintWriter out = response.getWriter();
        out.println(instance);
    }
}
