package dataAnalysis;

import java.io.*;
import java.nio.ByteBuffer;

public class ByteBufferInput {

    public void run(String f1, int bufferSize) throws IOException {

        System.out.println("USING BYTE BUFFER AND INPUT STREAM");

        RandomAccessFile f = new RandomAccessFile(f1,"rw");
        //RandomAccessFile f = new RandomAccessFile("test-suite/test10a.dat", "rw");
        DataInputStream d = new DataInputStream(new BufferedInputStream(new FileInputStream(f.getFD())));
        // This code above is similar to the output stuff, except this time it reads.

        int intSize = 4;
        byte[] data = new byte[intSize * bufferSize];

        int bytesRead = d.read(data);
        //bytes read returns the number of bytes read.
        //I will play around with buffer size.

        while(bytesRead != -1) {
            doSomethingWithData(data, bytesRead);

            bytesRead = d.read(data);
        }
        d.close();

    }

    private void doSomethingWithData(byte[] data, int bytesRead){
        ByteBuffer bb = ByteBuffer.wrap(data);
        int negatives = 0;
        for(int i = 0; i < bytesRead/4; i++){
            //System.out.print(bb.getInt() + ", ");
            if(bb.getInt() < 0) {
                negatives += 1;
                //System.out.println("ooh a negative"); //there are no negatives, im just giving it a pointless comparison to do.
            }
        }
    }

}
