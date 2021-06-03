


import java.io.*;
import java.util.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class treefetch {

    private int pageNo;
    private int recordOffset;
    private String filename;

    public treefetch(String fn){
        filename = fn;
    }

    public String fetch(long order) throws IOException {
        String record = "";
        // Page Size
        int pageSize = 128;
        
        // Data file
        String datafile = filename;
        
        // For Performance Measurement.
        long startTime = 0;
        long finishTime = 0;
        
        // Intialize Constant Variables.
        int numBytesInOneRecord = constants.TOTAL_SIZE_BPLUS;
        int numRecordsPerPage = pageSize/numBytesInOneRecord;


        long pointer = order - 1;
        this.recordOffset = (int)pointer % numRecordsPerPage;
        this.pageNo = (int)pointer / numRecordsPerPage;

        // Initialize page.
        byte[] page = new byte[pageSize];
        
        FileInputStream inStream = null;
        
        try {
            
            inStream = new FileInputStream(datafile);
            int numBytesRead = 0;
            startTime = System.nanoTime();
            
            // Create byte arrays for each field in the record.
            byte[] keysBytes = new byte[constants.KEYS_SIZE];
            byte[] childrenBytes = new byte[constants.CHILDREN_SIZE];
            
            // Skip in stream to read a specific page. Avoid iterating through every single page for performance.
            inStream.skip(this.pageNo * pageSize);
            
            numBytesRead = inStream.read(page);
            
            int i = this.recordOffset;
            
            /*
            * Fixed Length Records (total size = 128 bytes):
            * key field = 64 bytes, offset = 0
            * pointer field = 64 bytes, offset = 64
            *
            */
            System.arraycopy(page, (i*numBytesInOneRecord), keysBytes, 0, constants.KEYS_SIZE);
            System.arraycopy(page, ((i*numBytesInOneRecord) + constants.CHILDREN_OFFSET), childrenBytes, 0, constants.CHILDREN_SIZE);

            // Get a string representation of the record for printing to stdout
            record = new String(keysBytes).trim() + "-" + new String(childrenBytes).trim();
            //System.out.println(record);
            
            finishTime = System.nanoTime();
        }
        catch (FileNotFoundException e) {
            System.err.println("File not found " + e.getMessage());
        }
        catch (IOException e) {
            System.err.println("IO Exception " + e.getMessage());
        }
        finally {
            if (inStream != null) {
                inStream.close();
            }
        }

        // Calculate time taken for accessing data at a specifice page + offset.
        long timeInMilliseconds = (finishTime - startTime)/constants.MILLISECONDS_PER_SECOND;
        //System.out.println("Time taken to fetch from heap: " + timeInMilliseconds + " ms");

        return record;
    }

    public String fetchRange(long order, long lower, long upper) throws IOException {
        String record = "";
        // Page Size
        int pageSize = 128;
        
        // Data file
        String datafile = filename;
        
        // For Performance Measurement.
        long startTime = 0;
        long finishTime = 0;
        
        // Intialize Constant Variables.
        int numBytesInOneRecord = constants.TOTAL_SIZE_BPLUS;
        int numRecordsPerPage = pageSize/numBytesInOneRecord;


        long pointer = order - 1;
        this.recordOffset = (int)pointer % numRecordsPerPage;
        this.pageNo = (int)pointer / numRecordsPerPage;

        // Initialize page.
        byte[] page = new byte[pageSize];
        
        FileInputStream inStream = null;
        
        try {
            
            inStream = new FileInputStream(datafile);
            int numBytesRead = 0;
            startTime = System.nanoTime();

            boolean ended = false;

            // Skip in stream to read a specific page. Avoid iterating through every single page for performance.
                
            inStream.skip(this.pageNo * pageSize);

            int nodeExplored = 0;
            int recordsExplored = 0;
            int recordsMatched = 0;
                

            while ((numBytesRead = inStream.read(page)) != -1 && ended == false){

                nodeExplored++;

                //System.out.println(numBytesRead);
                
                // Create byte arrays for each field in the record.
                byte[] keysBytes = new byte[constants.KEYS_SIZE];
                byte[] childrenBytes = new byte[constants.CHILDREN_SIZE];
                
                
                //numBytesRead = inStream.read(page);
                
                int i = this.recordOffset;
                
                /*
                * Fixed Length Records (total size = 128 bytes):
                * key field = 64 bytes, offset = 0
                * pointer field = 64 bytes, offset = 64
                *
                */
                System.arraycopy(page, (i*numBytesInOneRecord), keysBytes, 0, constants.KEYS_SIZE);
                System.arraycopy(page, ((i*numBytesInOneRecord) + constants.CHILDREN_OFFSET), childrenBytes, 0, constants.CHILDREN_SIZE);

                // Get a string representation of the record for printing to stdout
                record = new String(keysBytes).trim() + "-" + new String(childrenBytes).trim();
                
                String key = new String(keysBytes).trim();
                String hPointer = new String(childrenBytes).trim();

                String[] keys = key.split(",");
                String[] pointers = hPointer.split(",");

                for(int q=0; q<keys.length; q++){
                    if (keys[q].equals(".")==false){
                        recordsExplored++;
                        // If is more than upper, stop querying.
                        if (Long.valueOf(keys[q]) > upper){
                            ended = true;
                        }
                        if (lower <= Long.valueOf(keys[q]) &&  Long.valueOf(keys[q]) <= upper){
                            recordsMatched++;
                            String[] heap = pointers[q].split("/");
                       
                            heapfetch hp = new heapfetch(Integer.valueOf(heap[0]),Integer.valueOf(heap[1]));
                        }
                    }
                }

            }
            
            finishTime = System.nanoTime();

            System.out.println("\n\n=======================================");
            System.out.println("RANGE QUERY STATS:");
            System.out.println("=======================================\n");

            // Calculate time taken for accessing data at a specifice page + offset.
            long timeInMilliseconds = (finishTime - startTime)/constants.MILLISECONDS_PER_SECOND;

            System.out.println("Nodes Explored: "+nodeExplored);
            System.out.println("Records Explored: "+recordsExplored);
            System.out.println("Records Matched: "+recordsMatched);
            System.out.println("Query Time Taken: "+timeInMilliseconds+"ms");
        }
        catch (FileNotFoundException e) {
            System.err.println("File not found " + e.getMessage());
        }
        catch (IOException e) {
            System.err.println("IO Exception " + e.getMessage());
        }
        finally {
            if (inStream != null) {
                inStream.close();
            }
        }

        
        //System.out.println("Time taken to fetch from heap: " + timeInMilliseconds + " ms");

        return record;
    }

 

    public static void main(String[] args) throws IOException {

     
    
    }
    
}