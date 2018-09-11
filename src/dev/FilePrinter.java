package dev;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FilePrinter {

    public void run(String filename){
        try {
            RandomAccessFile f = new RandomAccessFile(filename, "rw");
            File fi = new File(filename);
            System.out.println("length: " + fi.length());
            for(int i = 0; i < fi.length()/4; i++){
                System.out.print(", "+ f.readInt());
            }
            System.out.println();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
