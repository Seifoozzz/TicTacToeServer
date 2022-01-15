/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author DELL
 */
public class TicTacToeServer {
public static final int PLAYER1 = 1;
    public static final int PLAYER2 = 2;
    public static final int PLAYER1_WON = 1;
    public static final int PLAYER2_WON = 2;
    public static final int DRAW = 3;
    public static final int CONTINUE = 4;
   
   
    ServerSocket serverSocket;
    Socket s;
   
    
    public TicTacToeServer() {
         try {
           
             serverSocket = new ServerSocket(5004);
             while(true){
             s = serverSocket.accept();
             new ServerHandler(s);
//            new OnlineHandler(s);
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
