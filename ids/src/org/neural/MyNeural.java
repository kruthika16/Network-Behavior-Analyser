package org.neural;
import java.sql.*;
import org.ids.DBConnectivity;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyNeural {
	private static float iweights[][] = new float[4][3];
	private static float oweights[][] = new float[3][4];
	private static float oldweights[][] = new float[3][4];
	private static float[][] oldinputweights = new float[4][3];
	private static float hidden[] = new float[3];
	private static float error3[] = new float[4];
	private static float error2[] = new float[3];
	private static float learningrate = 0.9f;
	private static float output[] = new float[4];
        
    DBConnectivity conn = new DBConnectivity();
    PreparedStatement pstate = null;
    ResultSet resultSet = null;
    String[] input= new String[5];
    public void GetDataFromDB(int s1,int s2) throws Exception
    {
    int i=0;
    String temp;
    Connection connect = conn.ConnectDatabase();
    //Statement statement = connect.createStatement();
      // Result set get the result of the SQL query
     //ResultSet resultSet = statement.executeQuery("select usage from nba.user where userID=?");
     pstate = connect.prepareStatement("Select * from nba.session where sessionID=?");
     pstate.setInt(1, s1); 
     resultSet = pstate.executeQuery();
     //String usage= resultSet.getString("usage");
     //if (resultSet.next())
        // p2=resultSet.getString("usage");
        while(resultSet.next())
        {
            //System.out.println(resultSet.getString("usage"));
            temp=resultSet.getString("sessionString");
            String[] t=temp.replace(" ",",").split(",");
            input[i++]=t[0];
            input[i++]=t[1];
           // System.out.println(input[0]);
            //System.out.println(input[1]);
        }
        pstate = connect.prepareStatement("Select * from nba.session where sessionID=?");
     pstate.setInt(1, s2); 
     resultSet = pstate.executeQuery();
     //String usage= resultSet.getString("usage");
     //if (resultSet.next())
        // p2=resultSet.getString("usage");
        while(resultSet.next())
        {  
            //System.out.println(resultSet.getString("usage"));
            temp=resultSet.getString("sessionString");
            //System.out.println(temp);
            String[] t=temp.replace(" ",",").split(",");
            input[i++]=t[0];
            input[i++]=t[1];
            //System.out.println(input[2]);
            //System.out.println(input[3]);
        }
        //System.out.println(input);
    } 
	public static class MNN
	{
		/*private static float iweights[][] = new float[4][3];
		private static float oweights[][] = new float[3][4];
		private static float oldweights[][] = new float[3][4];
		private static float[][] oldinputweights = new float[4][3];
		private static float hidden[] = new float[3];
		private static float error3[] = new float[4];
		private static float error2[] = new float[3];
		private static float learningrate = 0.002f;
		private static float output[] = new float[4]; */
		public float sigmoid(float x)
		{
			return x;
		}
		/*public MNN(float iweights[][] , float oweights[][])
		{
			MNN.iweights = iweights;
			MNN.oweights = oweights;
			
		} */
		public void InputToHidden(float input[])
		{
			int i , j;
			
			for(i=0;i<4;i++)
			{
				for(j=0;j<3;j++)
				{
					hidden[j]+= input[i]*iweights[i][j];
				}
			}
				for(j=0;j<3;j++)
				{
					hidden[j] = sigmoid(hidden[j]);
				}
			
	     }
		public void BackPropagate()
		{
			float delta3[] = new float[4];
			//float delta2[] = new float[4];
			int i,j,k;
			for(i=0;i<4;i++)
				delta3[i] = -error3[i]*output[i]*(1-output[i]);
			
			float deltasum = delta3[0]+delta3[1]+delta3[2];
			for(i=0;i<3;i++)
				error2[i] = deltasum*hidden[i];
			
			float alpha = 0.1f;
			
			for(i=0;i<3;i++)
			{
				for(j=0;j<4;j++)
				{
					oldweights[i][j] = oweights[i][j];
				}
			}
			for(i=0;i<3;i++)
			{
				for(j=0;j<4;j++)
				{
					
					oweights[i][j] = oweights[i][j] - learningrate*error2[i] + alpha*oldweights[i][j];
				}
			}
			float deltaweights[] = new float[4];
			i=0; j=3;
			for(k=0;k<3;k++){
				
			  deltaweights[k] = (delta3[0]*oweights[k][0]+ delta3[1]*oweights[k][1] + delta3[2]*oweights[k][2] + delta3[3]*oweights[k][3]);
			}
			
			for(i=0;i<4;i++)
			{
				for(j=0;j<3;j++)
				{
					oldinputweights[i][j] = iweights[i][j];
				}
			}
			for(i=0;i<4;i++)
			{
				for(j=0;j<3;j++)
				{
					
					iweights[i][j]+= -learningrate*hidden[j]*(1-hidden[j])*deltaweights[j]+ alpha*oldinputweights[i][j];
				}
			}
		
			
		}
		public float[] HiddenToOutput(float expected[])
		{
			int i ,j;
			
			for(i=0;i<3;i++)
			{
				for(j=0;j<4;j++)
				{
					output[j] += hidden[i]*oweights[i][j];
				}
			}
			for(i=0;i<4;i++)
				error3[i] = expected[i] - output[i];
			
			BackPropagate();
			//for(i=0;i<4;i++)
		    //	System.out.println(output[i]);
			//BackPropagate();
			return output;
		}
		
	}
	public static void main(String args[])
	{
		int i,j;
		Scanner sc = new Scanner(System.in);
		float[] input = new float[4];
                MyNeural nobj = new MyNeural();
        
            try {
                nobj.GetDataFromDB(1000,1001);
            } catch (Exception ex) {
                Logger.getLogger(MyNeural.class.getName()).log(Level.SEVERE, null, ex);
            }
        
		//float[][] inputweights = new float[4][3];
		//float[][] outputweights = new float[3][4];
		float outputex[] = new float[4];
		System.out.println("input");
	    for(i=0;i<4;i++)
            {input[i] = Float.parseFloat(nobj.input[i]);
            System.out.println(input[i]);
            }
	    
            double[] temp={0.1,0.2,0.2,0.1,0.1,0.1,0.2,0.1,0.1,0.2,0.1,0.1};
            int k=0;
	    //System.out.println("Enter the input weights");
	    for(i=0;i<4;i++)
	    {
	    	for(j=0;j<3;j++)
	    	{ iweights[i][j] = (float)temp[k++];
	    	}
	    	
	    }
            
            double[] temp1={0.2,0.2,0.1,0.1,0.2,0.1,0.1,0.2,0.2,0.1,0.1,0.2};
            int k1=0;
	    //System.out.println("Enter the  output weights");
	    for(i=0;i<3;i++)
	    {
	    	for(j=0;j<4;j++)
	    	 {
	    		oweights[i][j] = (float)temp[k1++];
	    	 
	    	 }
	    }
            
	    //System.out.println("Enter the predicted output");
	    //for(i=0;i<4;i++)
	    	//output[i] = sc.nextFloat();
            output[0]= (float)4.0;
            output[1]= (float)5.0;
            output[2]= (float)2.0;
            output[3]= (float)-2.0;
	
	    MNN m = new MNN();
	    float errorsum;
            float[] fin;
            String s1,s2;
	    do{
	    	
	    
	    errorsum=0;
	    m.InputToHidden(input);
	    fin = m.HiddenToOutput(outputex);
	    for(i=0;i<4;i++)
	    {	System.out.println(fin[i]);
	        errorsum+=outputex[i]-fin[i];
	    
	    }
	    
	    }while(errorsum>0.05 && errorsum<-0.05);
            
            
            //System.out.println("finish");
            s1=Float.toString(fin[0])+" ";
            s1+=Float.toString(fin[1]);
            System.out.println(s1);
            
            s2=Float.toString(fin[2])+" ";
            s2+=Float.toString(fin[3]);
            System.out.println(s2);
            DBConnectivity conn = new DBConnectivity();
            PreparedStatement pstate = null;
           
            try {
                Connection connect = conn.ConnectDatabase();
                pstate = connect.prepareStatement("insert into nba.session values (?,?,?)");
                pstate.setInt(1, 1);
                pstate.setInt(2, 3000);
                pstate.setString(3, s1);
                pstate.executeUpdate();
                
                pstate = connect.prepareStatement("insert into nba.session values (?,?,?)");
                pstate.setInt(1, 1);
                pstate.setInt(2, 3001);
                pstate.setString(3, s2);
                pstate.executeUpdate();
                /*m.InputToHidden(input);
                fin = m.HiddenToOutput(outputex);
                for(i=0;i<4;i++)
                {	System.out.println(fin[i]);
                
                
                }
                
                m.InputToHidden(input);
                fin = m.HiddenToOutput(output);
                for(i=0;i<4;i++)
                System.out.println(fin[i]); */
            } catch (Exception ex) {
                Logger.getLogger(MyNeural.class.getName()).log(Level.SEVERE, null, ex);
            }


	    }
}
