package org.alignment;
//import org.ids;
import java.io.IOException;
import java.sql.*;
import org.ids.DBConnectivity;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GlobalAlignment {

    DBConnectivity conn = new DBConnectivity();
    PreparedStatement pstate = null;
    ResultSet resultSet = null;
    String[] p= new String[5];
    public void GetDataFromDB(int userid) throws Exception
    {
    int i=0;
    Connection connect = conn.ConnectDatabase();
    //Statement statement = connect.createStatement();
      // Result set get the result of the SQL query
     //ResultSet resultSet = statement.executeQuery("select usage from nba.user where userID=?");
     pstate = connect.prepareStatement("Select * from nba.user where userID=?");
     pstate.setInt(1, userid); 
     resultSet = pstate.executeQuery();
     //String usage= resultSet.getString("usage");
     //if (resultSet.next())
        // p2=resultSet.getString("usage");
        while(resultSet.next())
        {
            //System.out.println(resultSet.getString("usage"));
            p[i++]=resultSet.getString("usageString");
        }
        System.out.println(p[0]);
        System.out.println(p[1]);
    } 
	public static int maximum(int x , int y , int z)
	{
		if(x>y && x>z)
			return x;
		else if(y>z && y>x)
			return y;
		else 
			return z;
	}
	public static int FindAlignment(String a, String b)
    {
        a = a.toLowerCase();
        b = b.toLowerCase();
        int[][] scorematrix = new int[b.length()+2][a.length()+2];
        Scanner sc = new Scanner(System.in);
       // System.out.println("Enter the match score \n");
        //int match_score = sc.nextInt();
        int match_score = 100;
        //System.out.println("Enter the mismatch score");
        //int mismatch_score = sc.nextInt();
        int mismatch_score = -10;
        //System.out.println("Enter the gap penality");
        //int gap = sc.nextInt();
        int gap = 0;
        int i , j;
        for(i=0;i<=b.length();i++)
        {
        	for(j=0;j<=a.length();j++)
        	{
        		if(i==0||j==0){
        			scorematrix[i][j] = 0;
        		}
        	}
        }
        //Calculation
        for(i=1;i<=b.length();i++)
        {
        	for(j=1;j<=a.length();j++)
        	{
        		if(a.charAt(j-1)==b.charAt(i-1))
        	    {
        			scorematrix[i][j] = maximum(scorematrix[i][j] + match_score , scorematrix[i-1][j]+gap,scorematrix[i][j-1] +gap);
        		}
        		else
        		{
        			scorematrix[i][j] = maximum(scorematrix[i][j] + mismatch_score , scorematrix[i-1][j]+gap,scorematrix[i][j-1] +gap);
        		}
        			
        	}
        
       }
        
        sc.close();
        
		return scorematrix[b.length()][a.length()];
        
    }
	
	
	public static void main(String args[])
	{
		Scanner sc = new Scanner(System.in);
		String sub;
                GlobalAlignment gobj = new GlobalAlignment();
        try {
            gobj.GetDataFromDB(1);
        } catch (Exception ex) {
            Logger.getLogger(GlobalAlignment.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        //String text = "1-3-5-8-4-3-2-6-7-8-6-5-4-2-4-2-1-1-3-2";
        //System.out.println("System genetated string is: \n'" + text + "'");
        //System.out.println("Enter the keyword to search: ");
        //String keyword = sc.nextLine();

        int cost = FindAlignment( gobj.p[0] , gobj.p[1]);
        float calc;
       // System.out.println(cost);
        
        calc = ((float)cost)*100/(gobj.p[0].length()); 
        
        System.out.println("The percentage of similarity is "+calc);
        sc.close(); 
        if(calc<20)
        {
            System.out.println("Alert network admin");
                    try {
                        ProcessBuilder pb = new ProcessBuilder("python","sms.py");
                        Process p = pb.start();
                    } catch (IOException ex) {
                        Logger.getLogger(GlobalAlignment.class.getName()).log(Level.SEVERE, null, ex);
                    }
        }

	}

}
