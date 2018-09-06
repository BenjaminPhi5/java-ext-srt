package dataAnalysis.readSpeeds;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class ByteArrayAndByteBuffer {

    private static final int BUFFER_SIZE = 1<<16;

    public void run(String f1) throws IOException {

        try {
            RandomAccessFile f = new RandomAccessFile(f1, "rw");
            int negatives = 0;

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = f.read(buffer);

            while(bytesRead != -1){
                if(bytesRead > BUFFER_SIZE){
                    System.out.println("boi you got an error here, you need to cap the amount it reads in okay");
                }

                ByteBuffer wrapped = ByteBuffer.wrap(buffer);
                for(int i = 0; i < bytesRead/4; i++){
                    if(wrapped.getInt() < 0){
                        negatives++;
                    }
                }

                bytesRead = f.read(buffer);

            }

        } catch (EOFException e){
            //I dont think this catch needs to be in anymore wont happen due to while loop
            System.out.println("end of file error not handled");
            return;
        }
    }

    public void runAnalysis(String f1) throws IOException {

        try {
            RandomAccessFile f = new RandomAccessFile(f1, "rw");
            int negatives = 0;
            int lowest = Integer.MAX_VALUE;
            int smallest = Integer.MAX_VALUE;
            int highest = Integer.MIN_VALUE;
            int current;
            int total = 0;

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = f.read(buffer);

            while(bytesRead != -1){
                if(bytesRead > BUFFER_SIZE){
                    System.out.println("boi you got an error here, you need to cap the amount it reads in okay");
                }

                ByteBuffer wrapped = ByteBuffer.wrap(buffer);
                for(int i = 0; i < bytesRead/4; i++){
                    current = wrapped.getInt();
                    total++;
                    if(current < lowest){
                        lowest = current;
                    }
                    if(current > highest){
                        highest = current;
                    }
                    if(current < 0){
                        negatives++;
                        current *= -1;
                    }
                    if(current < smallest){
                        smallest = current;
                    }
                }

                bytesRead = f.read(buffer);

            }
            System.out.println("total: " + total + "\tnegatives: " + negatives);
            System.out.println("lowest: " + lowest + "\thighest: " + highest +"\tsmallest:"+smallest);

        } catch (EOFException e){
            //I dont think this catch needs to be in anymore wont happen due to while loop
            System.out.println("end of file error not handled");
            return;
        }
    }


}
