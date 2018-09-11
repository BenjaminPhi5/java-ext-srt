package dev.buffers.TreeMap;

import dev.processors.HashTreeProcessor;
import dev.processors.Processor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

public class NoConcurrentTreeMap {

    byte[] bufIn;
    byte[] bufOut;
    private static final int BUF_SIZE = 1<<16;
    FileInputStream fis;
    String in;
    FileOutputStream fos; // WRITE OUT method of choice

    public void run(){

        int n; int newInt; HashTreeProcessor p = new HashTreeProcessor();

        try {
            while ((n = fis.read(bufIn)) != -1) {


                //READ IN METHOD OF CHOICE
                for (int i = 0; i < n / 4; i++) {
                    newInt = (((int) bufIn[4 * i + 3]) & 255)
                            | ((((int) bufIn[4 * i + 2]) & 255) << 8)
                            | ((((int) bufIn[4 * i + 1]) & 255) << 16)
                            | ((((int) bufIn[4 * i]) & 255) << 24);
                    // END OF READ IN CHOICE



                    // select the negatives then the positives
                    if (newInt < 0){
                        p.add(newInt);
                    }


                }
            }
            fis.close();

            // USING THE TREE MAP DATA STRUCTURE
            // writes to the buffer one int at a time from the data structure
            // write the buffer when reaches max buffer size
            // writes the left over smaller buffer if there is one.
            TreeMap<Integer, Byte> values = p.getValues();
            Map.Entry<Integer, Byte> x = values.pollFirstEntry();
            int bufSize = 0; byte[] result = new byte[4]; int r;


            while (x != null){
                r = x.getKey();
                result[0] = (byte) (r >> 24); result[1] = (byte) (r >> 16);
                result[2] = (byte) (r >> 8); result[3] = (byte) (r);
                for(int i = 0; i < x.getValue(); i++) {
                    bufOut[bufSize] = result[0]; bufOut[1+bufSize] = result[1]; bufOut[2+bufSize] = result[2]; bufOut[3+bufSize] = result[3];
                    bufSize+= 4;
                    if (bufSize == BUF_SIZE) {
                        bufSize = 0;
                        fos.write(bufOut);
                    }
                }
                x = values.pollFirstEntry();

            }
            if(bufSize > 0) {
                byte[] bufE = new byte[bufSize]; //copy the last buffer, probably smaller than bufsize, to the file.
                for (int i = 0; i < bufSize; i++) {
                    bufE[i] = bufOut[i];
                }
                fos.write(bufE);
            }

            p = new HashTreeProcessor();
            fis = new FileInputStream(in); // have to create new file stream to start from beginning

            while ((n = fis.read(bufIn)) != -1) {


                //READ IN METHOD OF CHOICE
                for (int i = 0; i < n / 4; i++) {
                    newInt = (((int) bufIn[4 * i + 3]) & 255)
                            | ((((int) bufIn[4 * i + 2]) & 255) << 8)
                            | ((((int) bufIn[4 * i + 1]) & 255) << 16)
                            | ((((int) bufIn[4 * i]) & 255) << 24);
                    // END OF READ IN CHOICE



                    // select the negatives then the positives
                    if (newInt >= 0){
                        p.add(newInt);
                    }


                }
            }

            //Second write out
            values = p.getValues();
            x = values.pollFirstEntry();

            while (x != null){
                r = x.getKey();
                result[0] = (byte) (r >> 24); result[1] = (byte) (r >> 16);
                result[2] = (byte) (r >> 8); result[3] = (byte) (r);
                for(int i = 0; i < x.getValue(); i++) {
                    bufOut[bufSize] = result[0]; bufOut[1+bufSize] = result[1]; bufOut[2+bufSize] = result[2]; bufOut[3+bufSize] = result[3];
                    bufSize+= 4;
                    if (bufSize == BUF_SIZE) {
                        bufSize = 0;
                        fos.write(bufOut);
                    }
                }
                x = values.pollFirstEntry();

            }
            if(bufSize > 0) {
                byte[] bufE = new byte[bufSize]; //copy the last buffer, probably smaller than bufsize, to the file.
                for (int i = 0; i < bufSize; i++) {
                    bufE[i] = bufOut[i];
                }
                fos.write(bufE);
            }
            fis.close();
            fos.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public NoConcurrentTreeMap(String f1, String f2){
        try {
            fis = new FileInputStream(f1);
            fos = new FileOutputStream(f2);
            in = f1;
            bufIn = new byte[BUF_SIZE];
            bufOut = new byte[BUF_SIZE];
        } catch (IOException e) { e.printStackTrace();}
    }

}


