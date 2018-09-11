package dataAnalysis.ReadIn;

import java.io.File;

public class ReadFile {

    public void readF(String filename){
        // gets file size

        File file = new File(filename);
        long time;
        time = System.currentTimeMillis();
        System.out.println("filesize: " + file.length());
        System.out.println("time: " + (System.currentTimeMillis()-time));


    }

}
