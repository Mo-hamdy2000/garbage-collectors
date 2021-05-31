import java.io.*;
import java.util.*;

public class MarkAndCompact {

    public static void main(String[] args) throws Exception {

        InputHandler inputHandler = new InputHandler(args);
        inputHandler.getInput();
        HashMap<Integer, HeapObject> heap = inputHandler.getHeap();
        List<Integer> roots = inputHandler.getRoots();
        FileWriter csvWriter = inputHandler.getOutputFile();

        for (int root: roots)
            markFromRoot(heap.get(root));

        List<HeapObject> sortingList = new ArrayList<>();
        for (Map.Entry<Integer, HeapObject> ho: heap.entrySet()) {
            if (ho.getValue().isMarked())
                sortingList.add(ho.getValue());
        }
        sortingList.sort(Comparator.comparingInt(HeapObject::getMemoryStart));

        int nextByte = 0;
        for (HeapObject heapObject: sortingList) {
            nextByte = heapObject.move(nextByte);
            csvWriter.append(heapObject.toString()).append("\n");
        }
        csvWriter.flush();
        csvWriter.close();
    }

    static void markFromRoot(HeapObject root) {
        HeapObject previousObject = null;
        HeapObject currentObject = root;
        boolean deeper;
        do {
            deeper = false;
            currentObject.setMarked();
            for (HeapObject child: currentObject.getReferences()) {
                // branch and reverse pointer
                if (child != null && !child.isMarked()) {
                    currentObject.getReferences().remove(child);
                    currentObject.getReferences().add(previousObject);
                    previousObject =currentObject; // notice the last pointer in previous object references is reversed pointer
                    currentObject = child;
                    deeper = true;
                    break;
                }
            }
            if (deeper)
                continue;
            // return via the reversed pointer
            if (previousObject==null)
                break;
            previousObject.getReferences().add(currentObject);
            currentObject = previousObject;
            previousObject = previousObject.getReferences().get(previousObject.getReferences().size()-2);
            if (previousObject!= null)
                currentObject.getReferences().remove(previousObject);
        } while (currentObject != root);
    }
}
