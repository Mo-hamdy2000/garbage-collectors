import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InputHandler {
    private final String[] args;

    private HashMap<Integer, HeapObject> heap;
    private List<Integer> roots;
    private String outputFile;

    public InputHandler(String[] args) {
        this.args = args;
    }

    /**
     * sets the heap and roots attributes in this object according to the input in the files
     * specified by heapFile, rootsFile, and pointersFile.
     * Set the files, use this method, then get heap and roots
     */
    public void getInput() throws IOException {
        if (args.length < 4)
            throw new IOException("Lacking arguments expected " + 4 + " received " + (args.length));

        String heapFile = args[0];
        String rootsFile = args[1];
        String pointersFile = args[2];
        outputFile = args[3];

        heap = new HashMap<>();
        roots = new ArrayList<>();

        try {
            /*read heap file*/
            InputStream inputStream = new FileInputStream(heapFile);
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));
            String row;
            while ((row = fileReader.readLine()) != null) {
                String[] data = row.replaceAll("[^\\d,]", "").split(",");
                HeapObject heapObject = new HeapObject( Integer.parseInt(data[0]),
                        Integer.parseInt(data[1]),
                        Integer.parseInt(data[2]));
                heap.put(heapObject.getIdentifier(), heapObject);
            }
            fileReader.close();

            /*read roots file*/
            inputStream = new FileInputStream(rootsFile);
            fileReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((row = fileReader.readLine()) != null) {
                roots.add(Integer.parseInt(row));
            }

            fileReader.close();

            /*read pointers file*/
            inputStream = new FileInputStream(pointersFile);
            fileReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((row = fileReader.readLine()) != null) {
                String[] data = row.replaceAll("[^\\d,]", "").split(",");
                int parentIdentifier = Integer.parseInt(data[0]);
                int childIdentifier = Integer.parseInt(data[1]);
                heap.get(parentIdentifier).addChild(heap.get(childIdentifier));
            }
            fileReader.close();

        } catch (IOException e) {
            System.err.println("Error: File Not Found\nPlease put the file in the resources");
            e.printStackTrace();
            System.exit(-1);
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid format\nPlease put the files in expected format");
            e.printStackTrace();
            System.exit(-1);
        }

    }

    public HashMap<Integer, HeapObject> getHeap() {
        return heap;
    }

    public List<Integer> getRoots() {
        return roots;
    }

    public FileWriter getOutputFile() throws IOException {
        try {
            return new FileWriter(outputFile);
        } catch (IOException e) {
            throw new IOException("Output file not found");
        }
    }
}
