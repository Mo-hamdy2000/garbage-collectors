import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InputHandler {
    private String heapFile;
    private String rootsFile;
    private String pointersFile;

    private HashMap<Integer, HeapObject> heap;
    private List<Integer> roots;

    public void setHeapFile(String heapFile) {
        this.heapFile = heapFile;
    }

    public void setRootsFile(String rootsFile) {
        this.rootsFile = rootsFile;
    }

    public void setPointersFile(String pointersFile) {
        this.pointersFile = pointersFile;
    }

    public HashMap<Integer, HeapObject> getHeap() {
        return heap;
    }

    public List<Integer> getRoots() {
        return roots;
    }

    public void getInput() {
        if (heapFile==null || rootsFile==null || pointersFile==null)
            throw new RuntimeException("Please set all input files");

        heap = new HashMap<>();
        roots = new ArrayList<>();

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
        } catch (IOException e) {
            System.err.println("Error: File Not Found\nPlease put the file in the resources");
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid format\nPlease put the files in expected format");
        }

    }
}
