package dataAnalysis.readSpeeds;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class StandardFileRead {

    public void run(String f1) throws IOException {

        System.out.println("USING READ INT");
        try {
            RandomAccessFile f = new RandomAccessFile(f1, "rw");
            //RandomAccessFile f = new RandomAccessFile("test-suite/test10a.dat", "rw");
            int negatives = 0;
            for (int i = 0; i < 10000; i++) {
                //System.out.print(f.readInt() + ", ");
                if (f.readInt() < 0) {
                    negatives += 1;
                    //System.out.println("ooh a negative"); //there are no negatives, im just giving it a pointless comparison to do.
                }
            }
            System.out.println("Error: reached cap, your bound isnt big enough");
        } catch (EOFException e){
            return;
        }
    }

}
