

import java.io.*;
import java.util.*;


public class treequery{
    public ArrayList<Long> keysLong;
    public ArrayList<Long> pointersLong;
    public ArrayList<String> pointersString;
    int nType;
    treefetch tff;
    int nodeExplored;

    public treequery(){
        keysLong = new ArrayList<Long>();
        pointersLong = new ArrayList<Long>();
        pointersString = new ArrayList<String>();
        nType = -1;
        tff = new treefetch();
        nodeExplored = 0;
    }
    public static void main(String[] args) {

        if (args.length != 1 && args.length != 2 ){
            System.out.print("Please enter one (for equality) or two (for range) SDTName fields as arguments for querying.");
            return;
        }
        treefetch tf = new treefetch();
        treequery q = new treequery();

        String strSearchKey = "102018010106";

        int queryType = args.length;

        if (queryType == 2){
            q.rangeSearch(q.convertSDTStringtoLong(args[0]),q.convertSDTStringtoLong(args[1]));


            
        } else if (queryType == 1) {
            long startTime = System.nanoTime();
            // Range Search
            Long searchKey = q.convertSDTStringtoLong(args[0]);
            q.search(searchKey);

            for (int i=0; i<q.keysLong.size();i++){
                if (q.keysLong.get(i)!= null){
                    if (q.keysLong.get(i).equals(searchKey)){
                        System.out.println(q.keysLong.get(i));
                        String[] pointers = q.pointersString.get(i).split("/");
                        try{
                            System.out.println("\n\n\n\n=======================================");
                            System.out.println("QUERY RESULT(S):");
                            System.out.println("=======================================\n");
                            heapfetch hp = new heapfetch(Integer.valueOf(pointers[0]),Integer.valueOf(pointers[1]));
                            System.out.println("\n\n\n");
                        } catch (IOException e){}
                    }
                } 
            }

            long finishTime = System.nanoTime();

            long timeInMilliseconds = (finishTime - startTime)/constants.MILLISECONDS_PER_SECOND;

            System.out.println("\n=======================================");
            System.out.println("EQUALITY SEARCH STATS:");
            System.out.println("=======================================\n\n");
            System.out.println("Nodes Explored: "+ q.nodeExplored);
            System.out.println("Query Time: "+ timeInMilliseconds + "ms\n\n");
        }
    }

    public void rangeSearch(long lower, long upper){
        try {

            String[] pointers;
            pointers = parseStringText(tff.fetch(1));

            if(pointers[0] != null){
                rangeSearch(Long.valueOf(pointers[0]),lower,upper);
            }

        } catch(IOException e){
        }
    }

    public void rangeSearch(long id, long lower, long upper){

        try {
            String[] pointers;
            pointers = parseStringText(tff.fetch(id));
            
            if (nType == 0){
                if(pointers[0] != null){
                    rangeSearch(Long.valueOf(pointers[0]), lower, upper);
                }
            } else if(nType == 1){
                //System.out.println("First Leaf Found!");
                //System.out.println(id);

                System.out.println("\n\n\n\n=======================================");
                System.out.println("QUERY RESULT(S):");
                System.out.println("=======================================\n");
                tff.fetchRange(id,lower,upper);

                
            }
        } catch(IOException e){
        }
    }

    public Long convertSDTStringtoLong(String sdtNameString){
        String[] split = sdtNameString.split(" ");

        String[] sd = split[0].split("/");
        String[] t = split[1].split(":");

        String sensor_id = "";
        String month = "";
        String strIndex;


        if (sd[0].length() == 3){
            sensor_id = sd[0].substring(0,1);
            month = sd[0].substring(1,3);
        } else if (sd[0].length() == 4){
            sensor_id = sd[0].substring(0,2);
            month = sd[0].substring(2,4);
        }
       
        if(split[2].equals("PM")){
            int temp = Integer.valueOf(t[0]) + 12;
            strIndex = sensor_id + sd[2] + month + sd[1] + String.valueOf(temp);
        } else {
            strIndex = sensor_id + sd[2] + month + sd[1] + t[0];
        }       
        return Long.valueOf(strIndex);
    }

    public String[] parseStringText(String str) {
        keysLong.clear();
        pointersLong.clear();
        pointersString.clear();


        String[] split = str.split("-");


        String[] keys = split[0].split(",");
        String[] pointers = split[1].split(",");


        for (int i=0; i<keys.length; i++){
            if (keys[i].equals(".")){
                this.keysLong.add(null);
            } else {
                this.keysLong.add(Long.valueOf(keys[i]));
            }
        }

        if (keys.length == pointers.length){
            //System.out.println("is leaf");

            nType = 1;

            for (int i=0; i<pointers.length; i++){
                if (pointers[i].equals(".")){
                    this.pointersString.add(null);
                } else {
                    this.pointersString.add(pointers[i]);
                }
            }
        } else {
            //System.out.println("is not leaf.");
            nType = 0;

            for (int i=0; i<pointers.length; i++){
                if (pointers[i].equals(".")){
                    this.pointersLong.add(null);
                } else {
                    this.pointersLong.add(Long.valueOf(pointers[i]));
                }
            }
        }
        /*
        for(int i=0; i< keysLong.size();i++){
            System.out.println(keysLong.get(i));
        }
        for(int i=0; i< pointersLong.size();i++){
            System.out.println(pointersLong.get(i));
        }
        for(int i=0; i< pointersString.size();i++){
            System.out.println(pointersString.get(i));
        }
        */
        return pointers;
    }

    public ArrayList<Long> search(long key){

        System.out.println("\nSearch Key:" + key);

        System.out.println("\n\n=======================================");
        System.out.println("NODES EXPLORED BY TREE:");
        System.out.println("=======================================\n\n");

        // Get Root;
        try{
            String[] pointers;

            nodeExplored++;

            String nodeEx = tff.fetch(1);

            System.out.println(nodeEx);
            pointers = parseStringText(nodeEx);
        
            int degree=-1;

                    // Get Degree;
                    for (int i=0; i<pointers.length; i++){
                        if (pointers[i].equals(".")){
                            degree = i;
                            break;
                        }
                    }
            
            int q;

            for (q = 0; q < degree-1; q++) {
                if (key < keysLong.get(q)) { break; }
            }
            
            // Is inner node
            if (nType == 0){
                search(pointersLong.get(q), key);
            }
            // Is leaf node 
            else if (nType == 1) {
                return keysLong;
            }

       
        } catch(IOException e){

            
        }
        return null;
    }

    public ArrayList<Long> search(long id, long key ){
        // Get Root;
        try{
            String[] pointers;
            nodeExplored++;

            String nodeEx = tff.fetch(id);
            System.out.println(nodeEx);
            pointers = parseStringText(nodeEx);

            
    
            int degree=-1;

            // Get Degree;
            for (int i=0; i<pointers.length; i++){
                if (pointers[i].equals(".")){
                    degree = i;
                    break;
                }
            }

            int q;

            for (q = 0; q < degree -1 ; q++) {
                if (key < keysLong.get(q)) { break; }
            }

            // Is inner node
            if (nType == 0){
                search(pointersLong.get(q), key);
            }
            // Is leaf node 
            else if (nType == 1) {
                return keysLong;
            }

        } catch(IOException e){

        }

        return null;
    }

    
}