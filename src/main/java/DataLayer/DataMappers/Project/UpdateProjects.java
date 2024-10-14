package DataLayer.DataMappers.Project;

import DataLayer.DBCPDBConnectionPool;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class UpdateProjects implements Runnable {

    @Override
    public void run() {
        try {
            System.out.println("Update Projects Begin");
            Connection conn = DBCPDBConnectionPool.getConnection();
            ProjectMapper.fillTable(false);
            conn.close();
            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}