/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dictionary_server_client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author bereket
 */
public class Dictionary_Server {

    public static dictionary_file_API server_dictionary_API;
    public static ArrayList<RequestHandler> clientList = new ArrayList<RequestHandler>();
    
    public static int changed;
    public static boolean isRunning = true;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        
        Dictionary_server_UI server_ui = new Dictionary_server_UI();
        server_ui.setVisible(isRunning);
        
//        Thread server_UI_thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Dictionary_server_UI server_ui = new Dictionary_server_UI();
//                server_ui.setVisible(isRunning);
//            }
//            
//        });
        
        ServerSocket echoServer = null;
        try {
          echoServer = new ServerSocket(5566);
          while (isRunning) {
              
            Socket client = echoServer.accept();
            RequestHandler handler = new RequestHandler(client);
            clientList.add(handler);
            handler.start();
            
          }
        }
        catch (Exception e) {
          System.err.println("Exception caught:" + e);
        }   
    }
    
    public static void updateAllClientsDictionary(){
        for (int i = 0; i < clientList.size(); i++){
            clientList.get(i).updateDictionary();
            System.out.println("changed block");
        }
    }
    
}



class RequestHandler extends Thread {
    
    public dictionary_file_API client_dictionary_API;
    
  Socket client;
  RequestHandler (Socket client) {
    this.client = client;
     client_dictionary_API = new dictionary_file_API();
  }

  public void updateDictionary(){
      client_dictionary_API.loadDictionaryFile();
  }
  
  public void run () {
     
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
      PrintWriter writer = new PrintWriter(client.getOutputStream(), true);

      while (true) {
        String line = reader.readLine();
        if (line.trim().equals("bye")) {
          writer.println("bye!");
          break;
        }
        
        // Main logic comes here
        String request_parts[] = line.split(" ");
        
        if(request_parts[0].equals("meaning")){
            String res = client_dictionary_API.getMeaning(request_parts[1]);
            writer.println(res);  // why does the thread exits here??
        }
        
        else if(request_parts[0].equals("add")){
            String meaning = "";
            
            for(int i =2 ; i < request_parts.length; i++){
                meaning += (request_parts[i]) + " " ;
            }
            System.out.println(meaning);
            String temp = client_dictionary_API.addWord(request_parts[1],meaning.toString());
            Dictionary_Server.updateAllClientsDictionary();
            writer.println(temp); 
            
        }
        
        else if(request_parts[0].equals("delete")){
            String temp_ = client_dictionary_API.deleteWord(request_parts[1]);
            Dictionary_Server.updateAllClientsDictionary();
            writer.println(temp_);
            
        }
        
        System.out.println(line);
        
      }
    }
    catch (Exception e) {
      System.err.println("Exception caught: client disconnected.");
    }
    finally {
      try { client.close(); }
      catch (Exception e ){ ; }
    }
  }
}