import java.io.*;

// Class to handle file processing in a separate thread
class FileProcessor extends Thread {
    private File file;

    public FileProcessor(File file) {
        this.file = file;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + " is processing file: " + file.getName());
        int wordCount = countWordsInFile(file);
        System.out.println(threadName + " finished processing file: " + file.getName() + ", Word Count: " + wordCount);
    }

    private int countWordsInFile(File file) {
        int wordCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                wordCount += line.split("\\s+").length;
            }
        } catch (IOException e) {
            System.err.println("Error reading file " + file.getName() + ": " + e.getMessage());
        }
        return wordCount;
    }
}

// Main class to start multiple threads
public class MultiThreadedFileProcessing {
    private String[] filePaths;

    public MultiThreadedFileProcessing(String[] filePaths) {
        this.filePaths = filePaths;
        processFiles();
    }

    private void processFiles() {
        for (String filePath : filePaths) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                FileProcessor fileProcessor = new FileProcessor(file);
                fileProcessor.start();
            } else {
                System.out.println("Invalid file: " + filePath);
            }
        }
    }

    public static void main(String[] args) {
        String[] filePaths = {
            "very-large-file.txt",
            "even-bigger-file.txt",
            "fruits.txt",
            "vegetables.txt",
            "MultiThreadedFileProcessing.java"
        };
        MultiThreadedFileProcessing processor = new MultiThreadedFileProcessing(filePaths);
    }
}
