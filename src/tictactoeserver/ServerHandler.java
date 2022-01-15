/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.derby.jdbc.ClientDriver;


/**
 *
 * @author DELL
 */
public class ServerHandler extends Thread {
   
    
     public static final String separator = ",";
    public static final String iWantToPlay = "iWantToPlay";
    public static final String  letsPlay= "letsPlay";
    public static final String  yourSymbole= "yourSymbole";
    public static final String  move= "move";
    public static final String X="X";
    public static final String O="O";
    public static final String LOGIN ="Login";
    public static final String SIGNUP="Sign Up";
    public static final String UPDATE_PROFILE ="Exit";
    public static final String GET_PROFILE ="Your Profile";
    
    
  Socket s;
     DataInputStream dis;
    DataOutputStream dos;
    Connection con;
    PreparedStatement ps;
    
    ResultSet rs;
     boolean isFound = false;
   ServerSocket serverSocket;
   String name="NONAme";
    static Vector<ServerHandler> clientVector = new Vector<ServerHandler>();
    int row;
    
    public static ServerHandler availToPlay;
    ServerHandler otherPlayer;

    /**
     *
     * @param s
     * 
     */
    public ServerHandler(Socket s) {
        try {
            this.s=s;
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());
            clientVector.add(this);
            // firstPlayer = serverSocket.accept();
            
            System.out.println("name from database = is :"+name);
           

            try {
                
                 DriverManager.registerDriver(new ClientDriver());
                 con = DriverManager.getConnection("jdbc:derby://127.0.0.1:1527/Players", "seif", "mmhzs");
           } catch (SQLException ex) {
                Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
           }
            start();
        } catch (IOException ex) {
            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void sendToBothPlayer(String message) throws IOException{
        dos.writeUTF(message);
        System.out.println(message);
        otherPlayer.dos.writeUTF(message);
        System.out.println("2: "+message);
    }
    
    public void run(){
    while(true){
        
        try {
            String key =dis.readUTF();
            System.out.println(key);
            String[] request = key.split(separator);
            
            
             if (key.equals(LOGIN) || key.equals(SIGNUP)||key.equals(UPDATE_PROFILE)){
                authintication(key);
             }
             else if(request.length!=0 ){
                 System.out.println("name from database = is :"+name);
                    if(request[0].equals(iWantToPlay)){
                       name=request[1];
                        if(availToPlay == null){
                            availToPlay = this;
                            System.out.println(iWantToPlay+" 1");
                        }else{
                            otherPlayer = availToPlay;
                            availToPlay.otherPlayer = this;
                            availToPlay = null;
                           // dos.writeUTF(yourSymbole+separator+X);
                           // otherPlayer.dos.writeUTF(yourSymbole+separator+O);
                            dos.writeUTF(yourSymbole+separator+X+separator+otherPlayer.name);
                            otherPlayer.dos.writeUTF(yourSymbole+separator+O+separator+name);
                            sendToBothPlayer(letsPlay);
                            System.out.println(iWantToPlay+" 2");
                        }
                    }else if(request[0].equals(move)){
                       
                        String nextTurn;
                        if(request[2].equals(X))
                            nextTurn =O;
                        else
                            nextTurn =X;
                        System.out.println(key+separator+nextTurn);
                        sendToBothPlayer(key+separator+nextTurn);
                    }
                }
           
            
          
            
             
        } catch (IOException ex) {
//            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("client closed");
            break;
        }
          
    
    }
    }
    public void registerUser(String name,String email,String password ){
     try {
         String signupQuery="INSERT INTO PLAYERS VALUES ( ? , ? ,?,?,?,? )";

               ps = con.prepareStatement(signupQuery);
               ps.setString(1, email);
               ps.setString(2, name);
               ps.setString(3, password);
               ps.setInt(4,0);
               ps.setInt(5,0);
               ps.setInt(6,0);
               ps.executeUpdate();
                isFound=true;
                dos.writeBoolean(isFound);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    public void loginUser(String email , String password){
        
       
         try {
           String  loginQuery="SELECT * FROM PLAYERS";
             ps = con.prepareStatement(loginQuery);
              rs=ps.executeQuery();
            while(rs.next()){
               
            
                if (email.equals(rs.getString("EMAIL")) && password.equals(rs.getString("PASSWORD"))){
                    name=rs.getString("USERNAME");
                    isFound = true;
                   
                   
                    
                    break;
                  
                }             
            }
         } catch (SQLException ex) {
             Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        
         }
          try {
             dos.writeBoolean(isFound);
              System.out.println(isFound);
         } catch (IOException ex) {
             Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
         }
                   // System.out.println(dos);
    }
    

   
    public void authintication(String key){
    if(key.equals("Login")){
        try {
            String email = dis.readUTF();
          
            String password = dis.readUTF();
            
            loginUser(email, password);
            getUserInfo(email);
        } catch (IOException ex) {
            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
}
             else if (key.equals("Sign Up")) {
        try {
            String name = dis.readUTF();
            String email = dis.readUTF();
            String password = dis.readUTF();
            registerUser(name, email, password);
            getUserInfo(email);
        } catch (IOException ex) {
            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
          } else if (key.equals("Exit")){
        try {
            String email = dis.readUTF();
            int games=dis.readInt();
            int wins=dis.readInt();
            System.out.println("wiins"+wins);
            System.out.println("gammmes"+games);
            int lose=dis.readInt();
             System.out.println("loooooose"+lose);
            updateProfile(email, games, wins, lose);
            getUserInfo(email);
        } catch (IOException ex) {
            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
          
          }
    
    }
    
    public void getUserInfo(String email ){
         try {
             
          String  userInfoQuery="SELECT * FROM PLAYERS WHERE EMAIL =\'"+ email + "\' ";
             ps = con.prepareStatement(userInfoQuery);
              rs=ps.executeQuery();
              while(rs.next()){
             if (email.equals(rs.getString("EMAIL"))){
             dos.writeUTF(rs.getString("EMAIL"));
             dos.writeUTF(rs.getString("USERNAME"));
             dos.writeInt(rs.getInt("GAMES"));
             dos.writeInt(rs.getInt("WINS"));
             dos.writeInt(rs.getInt("LOSE"));
             name=rs.getString("USERNAME");
             break;
             }
             }
             
             
         } catch (SQLException ex) {
             Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
         } catch (IOException ex) {
             Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
         }
    
    
    }
    
    public void updateProfile(String email,int games,int wins,int lose){
         try {
             String updateQuery="UPDATE PLAYERS SET GAMES=?,WINS=?,LOSE=? WHERE EMAIL = \'"+ email + "\' ";
             ps = con.prepareStatement(updateQuery);
             ps.setInt(1, games);
             ps.setInt(2, wins);
             ps.setInt(3, lose);
             ps.executeUpdate();
         } catch (SQLException ex) {
             Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
         }
               
    }
}
   
    

