package Servlets;
import DataLayer.DataMappers.Project.ProjectMapper;
import Entities.Project;
import Exceptions.*;
import ContentProviders.*;

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




@WebServlet(  name = "ProjectServlet",  urlPatterns = { "/project"} , initParams = {
        @WebInitParam(name = "id" , value = "Not provided") } )


public class ProjectServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(response.SC_NOT_IMPLEMENTED);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        response.setContentType("application/json;charset=UTF-8");
        String pathInfo = request.getPathInfo();
        String path = (request).getRequestURI();
        //StringTokenizer tokenizer = new StringTokenizer(path, "/");
        //String context = tokenizer.nextToken();
       // String projectID = tokenizer.nextToken();
        String projectID = request.getParameter("id");
        String userId = (String) request.getAttribute("username");
        request.setAttribute("projectID", projectID);
        System.out.println("userId in project: " +userId);
        boolean hasBade = false;

        try {
            //JSONObject map = projectContentProvider.getHTMLContentsForProject("1", projectID);
            ProjectMapper pm = new ProjectMapper(false);
            if (userId != null){
                Project p = pm.find(projectID);
                ProjectContentProvider.checkAccess(userId,projectID);
                JSONObject map = ProjectContentProvider.getProjectContent(p);
                response.setStatus(response.SC_OK);
                PrintWriter out = response.getWriter();
                out.println(map);
            }
            else {
                throw new UserNotFoundException("user not found");
            }
        }
        catch (ProjectNotFoundException e) {
            printApiOutputError(e, 404,response);
        }

        catch (ProjectAccessForbiddenException e){
            printApiOutputError(e, 403,response);
        }
        catch (UserNotFoundException e){
            printApiOutputError(e, 401,response);
        }
        catch (SQLException e) {
            printApiOutputError(e, 404,response);
            e.printStackTrace();
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
