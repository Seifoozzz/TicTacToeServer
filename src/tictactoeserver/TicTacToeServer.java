/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.derby.jdbc.ClientDriver;

/**
 *
 * @author DELL
 */
public class TicTacToeServer {

    /**
     * @param args the command line arguments
     *
     */
   
    ServerSocket serverSocket;
    Socket s;
    public TicTacToeServer() {
         try {
           
             serverSocket = new ServerSocket(5004);
             while(true){
             s = serverSocket.accept();
             new ServerHandler(s);

           }
        } catch (IOException ex) {
            Logger.getLogger(TicTacToeServer.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    
    /**
     * @param args the command line arguments
     * 
     */
    
     
    public static void main(String[] args) {
        // TODO code application logic here
        new TicTacToeServer();
    }
    
}
