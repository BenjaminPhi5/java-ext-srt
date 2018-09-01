package dataAnalysis;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class StandardFileRead {

    public void run(String f1) throws IOException {

        System.out.println("USING READ INT");
        try {
            RandomAccessFile f = new RandomAccessFile(f1, "rw");
            for (int i = 0; i < 10000; i++) {
                //System.out.println("value read in is: " + f.readInt());
                if (f.readInt() < 0) {
                    System.out.println("ooh a negative"); //there are no negatives, im just giving it a pointless comparison to do.
                }
                System.out.println("Error: reached cap, your bound isnt big enough");
            }
        } catch (EOFException e){
            return;
        }
    }

}
