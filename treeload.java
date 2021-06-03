import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;

public class treeload {

    // Reads in a binary file of the argument-specified pagesize, prints out matching records
    public static void main(String[] args) throws IOException {

        if (args.length != 1){
            System.out.println("Please input heap page size as argument.");
            return;
        }

        BPlusTree bpt = null;

        int pageSize = Integer.valueOf(args[0]);

        String datafile = "heap." + pageSize;
        long startTime = 0;
        long finishTime = 0;
        int numBytesInOneRecord = constants.TOTAL_SIZE;
        int numBytesInSdtnameField = constants.STD_NAME_SIZE;
        int numBytesIntField = Integer.BYTES;
        int numRecordsPerPage = pageSize/numBytesInOneRecord;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        byte[] page = new byte[pageSize];
        FileInputStream inStream = null;

        int pageNo = 0;

        int index = 0;
        int prev =-1;
        
        ArrayList<Integer> arr= new ArrayList<Integer>();
        
        try {
            inStream = new FileInputStream(datafile);
            int numBytesRead = 0;
            startTime = System.nanoTime();
            // Create byte arrays for each field
            byte[] sdtnameBytes = new byte[numBytesInSdtnameField];

            bpt = new BPlusTree();
            //int noRec = 0;
            // until the end of the binary file is reached
            while ((numBytesRead = inStream.read(page)) != -1) {
                // Process each record in page
                for (int i = 0; i < numRecordsPerPage; i++) {

                    // Copy record's SdtName (field is located at multiples of the total record byte length)
                    System.arraycopy(page, (i*numBytesInOneRecord), sdtnameBytes, 0, numBytesInSdtnameField);
                    
                    // Check if field is empty; if so, end of all records found (packed organisation)
                    if (sdtnameBytes[0] == 0) {
                        // can stop checking records
                        break;
                    }
                    
                    if ( index == prev + 1){
                        
                        
                        // Check for match to "text"
                        String sdtNameString = new String(sdtnameBytes);

                     
                        String[] split = sdtNameString.split(" ");

                        String[] sd = split[0].split("/");
                        String[] t = split[1].split(":");

                        String sensor_id = "";
                        String month = "";


                        if (sd[0].length() == 3){
                            sensor_id = sd[0].substring(0,1);
                            month = sd[0].substring(1,3);
                        } else if (sd[0].length() == 4){
                            sensor_id = sd[0].substring(0,2);
                            month = sd[0].substring(2,4);
                        }

                        String strIndex;
                        if(split[2].equals("PM")){

                            int temp = Integer.valueOf(t[0]) + 12;
                            strIndex = sensor_id + sd[2] + month + sd[1] + String.valueOf(temp);
                        } else {
                            strIndex = sensor_id + sd[2] + month + sd[1] + t[0];
                        }

                        
                        long longIndex = Long.valueOf(strIndex);
                        System.out.println(longIndex);

                        if (Long.valueOf(1234) == Long.valueOf(index) ){
                            System.out.println("Its here");
                        }

                        //noRec++;
                        bpt.insertTuple(Long.valueOf(longIndex), String.valueOf(pageNo) + "/" + String.valueOf(i));
                    }

                    prev = index;
                    index++;
                    
                    
                }
                pageNo++;
            }

            //String pointer = bpt.equalityQuery(Long.valueOf("142019110119"));
            //String split[] = pointer.split("/");

            bpt.printToFile();

            //heapfetch hp = new heapfetch(Integer.valueOf(split[0]),Integer.valueOf(split[1]));

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

        long timeInMilliseconds = (finishTime - startTime)/constants.MILLISECONDS_PER_SECOND;
        System.out.println("Time taken to created index file: " + timeInMilliseconds + " ms");
    }
}


