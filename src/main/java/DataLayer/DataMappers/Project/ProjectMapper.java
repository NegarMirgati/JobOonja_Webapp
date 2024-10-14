package DataLayer.DataMappers.Project;
import DataLayer.DBCPDBConnectionPool;
import DataLayer.DataMappers.Mapper;
import DataLayer.DataMappers.user.UserMapper;
import Entities.Project;
import HttpConnection.HttpConnection;
import Parsers.MyJsonParser;
import com.google.gson.JsonElement;
//import javafx.util.Pair;
import utils.Pair;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ProjectMapper extends Mapper<Project, String> implements IProjectMapper {
    private static final String COLUMNS = " id, title, description, imageURL, budget, deadline, creationDate, winner";
    private static String creationDate;

    public ProjectMapper(boolean init) throws SQLException, IOException {
        if (init == true) {
            Connection con = DBCPDBConnectionPool.getConnection();
            Statement st =
                    con.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS " + "project" + " " + "(id VARCHAR(256) PRIMARY KEY, title TEXT," +
                    " description TEXT, imageURL TEXT, budget INTEGER, deadline BIGINT, creationDate BIGINT, winner VARCHAR(256))");
            ProjectSkillMapper psm = new ProjectSkillMapper();
            try {
                creationDate = "0";
                fillTable( true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            st.close();
            con.close();
        }
    }


    @Override
    protected String getFindStatement() {
        return "SELECT " + COLUMNS +
                " FROM project" +
                " WHERE id = ?";
    }

    @Override
    protected Project convertResultSetToDomainModel(ResultSet rs) throws SQLException {
        ProjectSkillMapper pm = new ProjectSkillMapper();
        UserMapper um = new UserMapper();
        return  new Project(
                rs.getString(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                rs.getInt(5),
                rs.getLong(6),
                pm.findProjectSkillsById(rs.getString(1)),
                rs.getString(8)
        );
    }


    public static void fillTable(boolean init) throws Exception {
        try {
            String creationDateUpdate = "0";
            System.out.println("In fill table");
            HttpConnection connection = new HttpConnection();
            // http://142.93.134.194:8000/joboonja/project
            ArrayList<JsonElement> projectlist = connection.httpGet(new URL("http://127.0.0.1:5002/project"));
            System.out.println("HERE");
            Pair<ArrayList<ArrayList<String>>, ArrayList<ArrayList<ArrayList<String>>>> tempPair = MyJsonParser.parseProjectList(projectlist);
            System.out.println("HERE23");
            ArrayList<ArrayList<String>> values_list = new ArrayList<ArrayList<String>>(tempPair.getFirst());
            ArrayList<ArrayList<ArrayList<String>>> allProjectsSkill = new ArrayList<ArrayList<ArrayList<String>>>(tempPair.getSecond());
            System.out.println("HERE3");
            ArrayList<String> attrs = createAttribute();
            ArrayList<Integer> toBeAdded = new ArrayList<>();
            boolean anythingAdded = false;
            System.out.println(values_list.size());
            for (int i = 0; i < values_list.size(); i++) {
                String creationDateTemp = new String(values_list.get(i).get(6));
                System.out.println("creationDateTemp");
                System.out.println(creationDateTemp);
                if (init == true) {
                    //System.out.println(values_list.get(i));
                    System.out.println("creationDateTemp");
                    System.out.println(creationDateTemp);
                    if (Long.parseLong(creationDateTemp) > Long.parseLong(creationDate)) {
                        creationDate = creationDateTemp;
                        System.out.println("creationDate");
                        System.out.println(creationDate);
                    }
                    try {
                        values_list.get(i).add("");
                        addToTable("project", attrs, values_list.get(i));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                } else if (!init) {
                    if (Long.parseLong(creationDateTemp) > Long.parseLong(creationDate)) {
                        System.out.println("found new project");
                        anythingAdded = true;
                        creationDateUpdate = creationDateTemp;
                        toBeAdded.add(i);
                        try {
                            values_list.get(i).add("");
                            addToTable("project", attrs, values_list.get(i));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            ArrayList<String> attr = ProjectSkillMapper.createAttribute();
            for (int j = 0; j < allProjectsSkill.size(); j++) {
                if ((init == false && toBeAdded.contains(j)) || (init == true)) {
                    System.out.println("add new project");
                    for (int k = 0; k < allProjectsSkill.get(j).size(); k++) {

                        try {
                            ProjectSkillMapper.addToTable("projectSkill", attr, allProjectsSkill.get(j).get(k));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
            if (init == false && anythingAdded == true) {
                creationDate = creationDateUpdate;
                System.out.println("creationDate");
                System.out.println(creationDate);
            }
            if (init == true) {
                System.out.println("init");
                System.out.println(init);
                System.out.println("creationDate");
                System.out.println(creationDate);
            }
        }catch (Throwable e){
            e.printStackTrace();
         }
    }


    public static ArrayList<String> createAttribute() {
        ArrayList<String> attrs = new ArrayList<String>();
        attrs.add("id");
        attrs.add("title");
        attrs.add("description");
        attrs.add("imageURL");
        attrs.add("budget");
        attrs.add("deadline");
        attrs.add("creationDate");
        attrs.add("winner");
        return attrs;
    }

    public static void addToTable(String tableName,ArrayList<String> attrs,ArrayList<String> values  ) throws SQLException {
        Connection con = DBCPDBConnectionPool.getConnection();
        String sqlCommand = insertCommand(tableName,attrs);
        PreparedStatement prp = con.prepareStatement(sqlCommand);
        for(int j = 1; j <= values.size(); j++)
            prp.setString(j, values.get(j-1));
        prp.executeUpdate();
        prp.close();
        con.close();
    }

    private static String insertCommand(String tableName, ArrayList<String> attributes){
        String sqlCommand = "INSERT IGNORE INTO " + tableName + "(";
        for(String attr: attributes)
            sqlCommand += attr + ",";
        sqlCommand = sqlCommand.substring(0, sqlCommand.length()-1);
        sqlCommand += ") VALUES(";
        for(int i = 0; i < attributes.size(); i++)
            sqlCommand += "?,";
        sqlCommand = sqlCommand.substring(0, sqlCommand.length()-1);
        sqlCommand += ");";
        return sqlCommand;
    }

    public ArrayList<Project> findAllOrderBycreationDate() throws SQLException {
        ArrayList<Project> projects = null;
        ResultSet rs = null;
        PreparedStatement prps = null;
        Connection con = null;
        try {
            String sqlCommand = getFindSortByDateStatement();
            con = DBCPDBConnectionPool.getConnection();
            prps = con.prepareStatement(sqlCommand);
            rs = prps.executeQuery();
            projects = loadAll(rs);
//            rs.close();
//            prps.close();
//            con.close();
           // return projects;
        }catch(SQLException e){
            e.printStackTrace();
        }
        finally {
            rs.close();
            prps.close();
            con.close();
        }
        return projects;
    }

    protected String getFindSortByDateStatement(){
        String sqlCommand = "SELECT * FROM project ORDER BY creationDate DESC";
       // String sqlCommand = "SELECT * FROM project";
        return sqlCommand;
    }

    private ArrayList<Project> loadAll(ResultSet rs) throws SQLException{
        ArrayList <Project> result = new ArrayList<>();
        while(rs.next()) {
            Project p = convertResultSetToDomainModel(rs);
            result.add(p);
        }
        return result;
    }

    public ArrayList<Project> findbyTitleOrDes(String query) throws SQLException {
        ResultSet rs = null;
        PreparedStatement prps = null;
        Connection con = null;
        ArrayList<Project> projects = null;
        try {
            String sqlCommand = getFindByTitleOrDesStatement();
            con = DBCPDBConnectionPool.getConnection();
            prps = con.prepareStatement(sqlCommand);
            prps.setString(1, "%" + query + "%");
            prps.setString(2, "%" + query + "%");
            rs = prps.executeQuery();
            projects = loadAll(rs);
//            prps.close();
//            con.close();
            return projects;
        }catch(SQLException e){
            e.printStackTrace();
        }
        finally {
            rs.close();
            prps.close();
            con.close();
        }
        return projects;
    }

    public ArrayList<Project> findFinishedProjects() throws SQLException {
        ResultSet rs = null;
        PreparedStatement prps = null;
        Connection con = null;
        ArrayList<Project> projects = null;
        try {
            String sqlCommand = getFindFinneshedStatement();
            con = DBCPDBConnectionPool.getConnection();
            prps = con.prepareStatement(sqlCommand);
            prps.setString(1, "");
            prps.setString(2, String.valueOf(System.currentTimeMillis()));
            rs = prps.executeQuery();
            projects = loadAll(rs);
            prps.close();
            con.close();
            return projects;
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            rs.close();
            prps.close();
            con.close();
        }
        return projects;
    }

    public void updateWinner(String winner, String projectId) throws SQLException {
        PreparedStatement prps = null;
        Connection con = null;
        try {
            String sqlCommand = updateWinnerStatement();
            con = DBCPDBConnectionPool.getConnection();
            prps = con.prepareStatement(sqlCommand);
            prps.setString(1, winner);
            prps.setString(2, projectId);
            int result = prps.executeUpdate();
            //System.out.println("update result");
            //System.out.println(result);
//            prps.close();
//            con.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        finally {
            con.close();
            prps.close();
        }
    }

    private String getFindByTitleOrDesStatement() {
        String sqlCommand = "SELECT * FROM project WHERE title LIKE ? OR description LIKE ? ORDER BY creationDate DESC";

        return sqlCommand;
    }
    private String getFindFinneshedStatement() {
        String sqlCommand = "SELECT * FROM project WHERE winner = ? AND ? > deadline";

        return sqlCommand;
    }
    protected String updateWinnerStatement() {
        return "UPDATE project SET winner = ? WHERE id = ?";
    }


}

