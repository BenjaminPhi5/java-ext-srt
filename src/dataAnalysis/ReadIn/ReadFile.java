package dataAnalysis.ReadIn;

import java.io.File;

public class ReadFile {

    public void readF(String filename){
        // gets file size

        File file = new File(filename);
        System.out.println("filesize: " + file.length());


    }

}
