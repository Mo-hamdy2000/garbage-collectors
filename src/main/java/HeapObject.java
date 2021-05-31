import java.util.ArrayList;
import java.util.List;

public class HeapObject {
    private int identifier;
    private int memoryStart;
    private int memoryEnd;
    private boolean mark;
    private final List<HeapObject> references = new ArrayList<>();

    public HeapObject(int identifier, int memoryStart, int memoryEnd) {
        this.identifier = identifier;
        this.memoryStart = memoryStart;
        this.memoryEnd = memoryEnd;
    }

    /*
    * returns the object identifier
    * */
    public int getIdentifier() {
        return identifier;
    }

    /*
     * returns index of the first byte of the object
     * */
    public int getMemoryStart() {
        return memoryStart;
    }

    /*
     * returns index of the last byte of the object
     * */
    public int getMemoryEnd() {
        return memoryEnd;
    }

    /*
    * function shifts the object's memory bounds to given index
    * returns the index of the next byte of memory
    * */
    public int move(int startIndex) {
        int offset = startIndex - memoryStart;
        memoryStart = startIndex;
        memoryEnd += offset;
        return memoryEnd+1;
    }

    /*
    * returns whether the object is marked or not
    * */
    public boolean isMarked() {
        return mark;
    }

    /*
    * set the current object as marked
    * */
    public void setMarked() {
        mark = true;
    }

    /*
    * returns current object references list
    * */
    public List<HeapObject> getReferences() {
        return references;
    }

}
