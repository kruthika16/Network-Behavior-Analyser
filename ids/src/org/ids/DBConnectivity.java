/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ids;
import java.sql.*;
/**
 *
 * @author KRUTHIKA
 */
public class DBConnectivity {
     public Connection ConnectDatabase() throws Exception {
      // This will load the MySQL driver, each DB has its own driver
      Class.forName("com.mysql.jdbc.Driver");
      // Setup the connection with the DB
       Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/nba","root","root");
       return connect;
     }

    public PreparedStatement prepareStatement(String select_usage_from_nbauser_where_userID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
