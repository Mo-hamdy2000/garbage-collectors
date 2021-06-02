import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CopyGC {

    public static void main(String[] args) {
        try {
            /*taking user input*/
            InputHandler inputHandler = new InputHandler(args);
            inputHandler.getInput();
            HashMap<Integer, HeapObject> heap = inputHandler.getHeap();
            List<Integer> roots = inputHandler.getRoots();
            FileWriter csvWriter = inputHandler.getOutputFile();

            /*garbage collector*/
            List<HeapObject> list = collectGarbage(roots, heap);

            /*writing to output file*/
            for (HeapObject heapObject : list) {
                csvWriter.append(heapObject.toString()).append("\n");
            }
            csvWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

    private static List<HeapObject> collectGarbage(List<Integer> roots, HashMap<Integer, HeapObject> oldHeap) {
        List<HeapObject> secondHeap = new ArrayList<>();
        int nextByte = 0;

        if (roots.isEmpty()) return secondHeap;

        /*put all roots in the new heap*/
        for (Integer rootID : roots) {
            HeapObject heapObj = oldHeap.get(rootID);
            if (heapObj.isMarked()) continue;
            nextByte = heapObj.move(nextByte);
            secondHeap.add(heapObj);
            heapObj.setMarked();
        }

        /*loop over the new list to add children of each to list*/
        for (int i = 0; i < secondHeap.size(); i++) { //for each parent
            HeapObject parent = secondHeap.get(i);

            for (HeapObject child : parent.getReferences()) { //for each child
                if (child.isMarked()) continue;
                nextByte = child.move(nextByte);
                secondHeap.add(child);
                child.setMarked();
            }
        }

        return secondHeap;
    }

}
