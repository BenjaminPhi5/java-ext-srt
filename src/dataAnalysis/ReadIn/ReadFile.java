package dataAnalysis.ReadIn;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ReadFile {

    public void readF(String filename){
        // gets file size

        File file = new File(filename);
        long time;
        time = System.nanoTime();
        System.out.println("filesize: " + file.length());
        System.out.println("time: " + (System.nanoTime()-time));

        try {
            RandomAccessFile fis = new RandomAccessFile(filename, "r");
            time = System.nanoTime();
            System.out.println("length: " + fis.length());
            System.out.println("time: " + (System.nanoTime() - time));
        } catch (IOException e){e.printStackTrace();};


    }

}
