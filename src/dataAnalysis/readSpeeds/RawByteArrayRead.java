package dataAnalysis.readSpeeds;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RawByteArrayRead {

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

                int[] int_arr = new int[bytesRead / 4];
                for(int i = 0; i < bytesRead / 4; i++){
                    int_arr[i] = (((int) buffer[4*i+3]) & 255)
                            |   ((((int) buffer[4*i+2]) & 255) << 8)
                            |   ((((int) buffer[4*i+2]) & 255) << 16)
                            |   ((((int) buffer[4*i+2]) & 255) << 24);
                }

                bytesRead = f.read(buffer);

            }

        } catch (EOFException e){
            //I dont think this catch needs to be in anymore wont happen due to while loop
            System.out.println("end of file error not handled");
            return;
        }
    }

}
