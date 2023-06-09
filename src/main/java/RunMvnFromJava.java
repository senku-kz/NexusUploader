import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class RunMvnFromJava {
    static public String[] runCommand(String cmd) throws IOException {

        // The actual procedure for process execution:
        //runCommand(String cmd);

        // Create a list for storing output.
        ArrayList list = new ArrayList();

        // Execute a command and get its process handle
        Process proc = Runtime.getRuntime().exec(cmd);

        // Get the handle for the processes InputStream
        InputStream istr = proc.getInputStream();

        // Create a BufferedReader and specify it reads
        // from an input stream.
        BufferedReader br = new BufferedReader(new InputStreamReader(istr));
        String str; // Temporary String variable

        // Read to Temp Variable, Check for null then
        // add to (ArrayList)list
        while ((str = br.readLine()) != null)
            list.add(str);

        // Wait for process to terminate and catch any Exceptions.
        try {
            proc.waitFor();
        } catch (InterruptedException e) {
            System.err.println("Process was interrupted");
        }
        // Note: proc.exitValue() returns the exit value.
        // (Use if required)
        br.close(); // Done.

        // Convert the list to a string and return
        return (String[])list.toArray(new String[0]);
    }


    // Actual execution starts here
    public static void main(String args[]){
        try{
            // Run and get the output.
            String outlist[] = runCommand("mvn integration-test -DskipTests -P interactive -e");
            // Print the output to screen character by character.
            // Safe and not very inefficient.
            for (String el:outlist){
                System.out.println(el);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

}
