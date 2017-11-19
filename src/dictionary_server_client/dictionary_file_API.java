/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dictionary_server_client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bereket
 */
public class dictionary_file_API {
    
    
    Map<String,String> DictionaryMap;
    FileWriter fw;

    public dictionary_file_API() {
        loadDictionaryFile();
    }
    
    public void updateDict_file(){
         File tempFile = new File("tempDict.txt");
         File inputFile = new File("dict.txt");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            
            Iterator<Entry<String, String>> it = DictionaryMap.entrySet().iterator();

            int count=0;
            // then use the iterator to loop through the map, stopping when we reach the
            // last record in the map or when we have printed enough records
            while (it.hasNext() && count < DictionaryMap.size()) {

                // the key/value pair is stored here in pairs
                if(count > 0) writer.newLine();
                Map.Entry<String, String> pairs = it.next();
                writer.write(pairs.getKey() + "\t" + pairs.getValue());
                
                count++;
            }
            writer.flush();
            writer.close();
            
            tempFile.renameTo(inputFile);

        } catch (IOException ex) {
            Logger.getLogger(dictionary_file_API.class.getName()).log(Level.SEVERE, null, ex);
        }
         
    }
    
    public void loadDictionaryFile(){
        try {
            DictionaryMap = new HashMap<String, String>();
            BufferedReader in = new BufferedReader(new FileReader("dict.txt"));
            String line = "";
            while ((line = in.readLine()) != null) {
                String parts[] = line.split("\t");
                DictionaryMap.put(parts[0], parts[1]);
            }
            System.out.println(DictionaryMap.toString());
            in.close();
        } catch (IOException ex) {
            System.out.println("Error: " + ex.toString());
        }
    }
    
    public String getMeaning(String word){
        if(DictionaryMap.containsKey(word)){
            return "meaning_res " + DictionaryMap.get(word);
        }else {
            return "meaning_res "+"Word not found in the dictionary.";
        }
    }
    
    public synchronized String addWord(String wrd, String meaning){
        
        if(DictionaryMap.containsKey(wrd)) return "add_res Word already exist";
        
        try{ 
            fw = new FileWriter("dict.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
        
            bw.newLine();
            bw.write( wrd +"\t"+meaning);
            bw.flush();
            //more code
        } catch (Exception e) {
            return ("add_res Error: " + e.toString());
        }
        
        this.loadDictionaryFile();
        return "add_res Successfully added";
    }
    
    public synchronized String deleteWord(String wrd){
        
        DictionaryMap.remove(wrd);
        updateDict_file();
        
        return "del_res sucessfully deleted";
        
        
      
//        File tempFile = new File("tempDict.txt");
//        File inputFile = new File("dict.txt");
//        int status=0;
//        BufferedReader reader = null;
//        int lineNo = 0;
//        try {
//                reader = new BufferedReader(new FileReader(inputFile));
//                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
//
//                String currentLine;
//
//                while((currentLine = reader.readLine()) != null) {
//                    
//                    String parts[] = currentLine.split("\t");
//                    DictionaryMap.put(parts[0], parts[1]);
//                    if(wrd.equals(parts[0])){
//                        this.loadDictionaryFile(); //reload the dictionary
//                        status = 1;
//                        continue;
//                    }
//                    
//                    if(lineNo > 0) writer.newLine();                     
//                     writer.write(currentLine);
//                     lineNo++;
//                }
//                
//                writer.flush();
//                writer.close(); 
//                reader.close(); 
//                boolean successful = tempFile.renameTo(inputFile);
//
//            } catch (Exception ex) {
//                System.out.println("Error: " + ex.toString());
//            }
//            
//           if(status == 1) return "Successfully deleted";
//        
//        return "Word not found";
    }
    
    
}
