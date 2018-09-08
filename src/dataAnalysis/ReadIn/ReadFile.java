package dataAnalysis.ReadIn;

import java.io.File;

public class ReadFile {

    public void readF(String filename){
        // regardless of which method called, this should read in a file,
        // find the min and max value and total
        // print those values out.

        File file = new File(filename);
        System.out.println("filesize: " + file.length());


    }

}
