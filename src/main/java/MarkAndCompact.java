import java.io.*;
import java.util.*;

public class MarkAndCompact {

    public static void main(String[] args) throws Exception {

        if (args.length < 4) {
            System.err.println("Lacking arguments expected " + 4 + " received " + (args.length));
            return;
        }
        String heapFile = args[0];
        String rootsFile = args[1];
        String pointersFile = args[2];
        String outputFile = args[3];
        HashMap<Integer, HeapObject> heap = new HashMap<>();
        List<Integer> roots = new ArrayList<>();
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader("src/main/resources/" + heapFile));
            String row;
            while ((row = fileReader.readLine()) != null) {
                String[] data = row.replaceAll("\\p{C}", "").split(",");
                HeapObject heapObject = new HeapObject( Integer.parseInt(data[0]),
                                                        Integer.parseInt(data[1]),
                                                        Integer.parseInt(data[2]));
                heap.put(heapObject.getIdentifier(), heapObject);
            }
            fileReader.close();
            fileReader = new BufferedReader(new FileReader("src/main/resources/" + rootsFile));
            while ((row = fileReader.readLine()) != null) {
                roots.add(Integer.parseInt(row));
            }

            fileReader.close();
            fileReader = new BufferedReader(new FileReader("src/main/resources/" + pointersFile));
            while ((row = fileReader.readLine()) != null) {
                String[] data = row.replaceAll("\\p{C}", "").split(",");
                int parentIdentifier = Integer.parseInt(data[0]);
                int childIdentifier = Integer.parseInt(data[1]);
                heap.get(parentIdentifier).getReferences().add(heap.get(childIdentifier));
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            System.err.println("Error: File Not Found");
            System.err.println("Please put the file in the resources");
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid format");
            System.err.println("Please put the files in expected format");
        }



        for (int root: roots)
            markFromRoot(heap.get(root));
        List<HeapObject> sortingList = new ArrayList<>();
        for (Map.Entry<Integer, HeapObject> ho: heap.entrySet()) {
            if (ho.getValue().isMarked())
                sortingList.add(ho.getValue());
        }
        sortingList.sort(Comparator.comparingInt(HeapObject::getMemoryStart));
        FileWriter csvWriter = new FileWriter("src/main/resources/" + outputFile);
        int nextByte = 0;
        for (HeapObject heapObject: sortingList) {
            nextByte = heapObject.move(nextByte);
            String record = heapObject.getIdentifier() + "," + heapObject.getMemoryStart() + "," + heapObject.getMemoryEnd();
            csvWriter.append(record);
            csvWriter.append("\n");
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
