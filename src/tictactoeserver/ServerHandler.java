/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    
     DataInputStream dis;
    DataOutputStream dos;
    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    static Vector<ServerHandler> clientVector = new Vector<ServerHandler>();

    /**
     *
     * @param s
     */
    public ServerHandler(Socket s) {
        try {
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());
            clientVector.add(this);
            try {
                 DriverManager.registerDriver(new ClientDriver());
                 con = DriverManager.getConnection("jdbc:derby://172.20.10.3:1527/Players", "seif", "mmhzs");
            } catch (SQLException ex) {
                Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            start();
        } catch (IOException ex) {
            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void run(){
    while(true){
        try {
            String key =dis.readUTF();
            System.out.println(key);
            authintication(key);
        } catch (IOException ex) {
            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    }
    public void registerUser(String name,String email,String password ){
     try {
               ps = con.prepareStatement("INSERT INTO PLAYERS VALUES ( ? , ? ,? )");
               ps.setString(1, name);
               ps.setString(2, email);
               ps.setString(3, password);
               ps.executeUpdate();
                con.close();  
                JOptionPane.showMessageDialog(null, "Saved");
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    public void loginUser(String email , String password){
        
        boolean isFound = false;
         try {
             ps = con.prepareStatement("SELECT * FROM PLAYERS");
              rs=ps.executeQuery();
            while(rs.next()){
                System.out.println(rs.getString("USERNAME"));
            
                if (email.equals(rs.getString("USERNAME")) && password.equals(rs.getString("PASSWORD"))){
                    isFound = true;
                    break;
                }             
            }
         } catch (SQLException ex) {
             Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
         }
    
         showMessage(isFound);
    
    }

    private void showMessage(boolean isFound) {
        if(isFound) JOptionPane.showMessageDialog(null, "Successfully Logged In");
        else JOptionPane.showMessageDialog(null, "Incorrect Email or Password");
    }
    public void authintication(String key){
    if(key.equals("Login")){
        try {
            String email = dis.readUTF();
            String password = dis.readUTF();
            loginUser(email, password);
        } catch (IOException ex) {
            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
}
             else  {
        try {
            String name = dis.readUTF();
            String email = dis.readUTF();
            String password = dis.readUTF();
            registerUser(name, email, password);
        } catch (IOException ex) {
            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
          } 
    
    }
}    
    

