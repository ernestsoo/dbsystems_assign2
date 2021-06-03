public class DataPointer implements Comparable<DataPointer> {
    // This key will be the SDTName (or other keys) converted to integer format.
    long key;

    // This field will point to the heap file.
    String value;

    // Positions of key and pointer;
    private static final int PAGE_POS = 0;
    private static final int OFFSET_POS = 1;

    public DataPointer(long key, String value) {
        this.key = key;
        this.value = value;
    }

    // Get page number in heap file.
    public int getPage(){
        String[] pointers = value.split("/");
        return Integer.parseInt(pointers[PAGE_POS]);
    }

    // Get record offset of current page in heap file.
    public int getRecordOffset(){
        String[] pointers = value.split("/");
        return Integer.parseInt(pointers[OFFSET_POS]);
    }

    // Override Comparable function to compare long key values for bplus tree.
    @Override
    public int compareTo(DataPointer dPointer) {
        if (key == dPointer.key) { 
            return 0; 
        }
        else if (key > dPointer.key) { 
            return 1; 
        }
        return -1; 
    }
}