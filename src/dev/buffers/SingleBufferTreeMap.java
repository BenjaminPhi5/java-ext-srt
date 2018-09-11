package dev.buffers;

import dev.processors.HashTreeProcessor;
import dev.processors.Processor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

public class SingleBufferTreeMap {

    byte[] bufIn;
    byte[] bufOut;
    private static final int BUF_SIZE = 1<<16;
    String filenameIn; String filenameOut;
    FileOutputStream fos; // WRITE OUT method of choice

    private class FileRead extends Thread{

        byte[] buf;
        FileInputStream fis;
        HashTreeProcessor p;
        Function<Integer, Boolean> function;

        FileRead(String f){
            try {

                fis = new FileInputStream(f); // READ IN METHOD OF CHOICE

                buf = bufIn;

            } catch (FileNotFoundException e) { e.printStackTrace(); }
        }

        public void setup(Function<Integer, Boolean> function){

                this.function = function;
                p = new HashTreeProcessor();

        }

        public void run(){

            int n; int x; HashTreeProcessor lp = p;
            try {
                while ((n = fis.read(buf)) != -1) {


                    //READ IN METHOD OF CHOICE
                    for (int i = 0; i < n / 4; i++) {
                        x = (((int) buf[4 * i + 3]) & 255)
                                | ((((int) buf[4 * i + 2]) & 255) << 8)
                                | ((((int) buf[4 * i + 1]) & 255) << 16)
                                | ((((int) buf[4 * i]) & 255) << 24);
                        // END OF READ IN CHOICE



                        // select the negatives then the positives
                        if (function.apply(x)){
                            lp.add(x);
                        }


                    }
                }
                fis.close();
            } catch (IOException e) { e.printStackTrace(); }
        }

    }

    private class FileWriter extends Thread{

        byte[] buf;
        HashTreeProcessor p;

        FileWriter(String f){

            buf = bufOut;
        }

        public void setup(HashTreeProcessor p){

                this.p = p; // uses the processor passed in from input processing.
        }

        public void run(){

            TreeMap<Integer, Byte> values = p.getValues();
            Map.Entry<Integer, Byte> x = values.pollFirstEntry();
            buf = bufOut; int bufSize = 0; byte[] result = new byte[4]; int r;
            try{

                // USING THE TREE MAP DATA STRUCTURE
                // writes to the buffer one int at a time from the data structure
                // write the buffer when reaches max buffer size
                // writes the left over smaller buffer if there is one.
                while (x != null){
                    r = x.getKey();
                    result[0] = (byte) (r >> 24); result[1] = (byte) (r >> 16);
                    result[2] = (byte) (r >> 8); result[3] = (byte) (r);
                    for(int i = 0; i < x.getValue(); i++) {
                        buf[bufSize] = result[0]; buf[1+bufSize] = result[1]; buf[2+bufSize] = result[2]; buf[3+bufSize] = result[3];
                        bufSize+= 4;
                        if (bufSize == BUF_SIZE) {
                            bufSize = 0;
                            fos.write(buf);
                        }
                    }
                    x = values.pollFirstEntry();

                }
                if(bufSize > 0) {
                    byte[] bufE = new byte[bufSize]; //copy the last buffer, probably smaller than bufsize, to the file.
                    for (int i = 0; i < bufSize; i++) {
                        bufE[i] = buf[i];
                    }
                    fos.write(bufE);
                }


            } catch (IOException e) { e.printStackTrace(); }

        }

    }

    public SingleBufferTreeMap(String f1, String f2){
        bufIn = new byte[BUF_SIZE];
        bufOut = new byte[BUF_SIZE];
        this.filenameIn = f1; this.filenameOut = f2;
        try { fos = new FileOutputStream(filenameOut); } catch (FileNotFoundException e) { e.printStackTrace(); }
    }

    public void run(){
        try {
            Function<Integer, Boolean> neg = x -> (x <= 0);
            Function<Integer, Boolean> pos = x -> (x > 0);

            FileRead reader = new FileRead(filenameIn);
            reader.setup(neg);

            // run the first half
            reader.start();
            reader.join();

            //write the first half
            HashTreeProcessor p = (HashTreeProcessor) reader.p;
            FileWriter writer = new FileWriter(filenameOut);
            writer.setup(p);
            writer.start();

            //read second half
            reader = new FileRead(filenameIn);
            reader.setup(pos);
            reader.start();

            writer.join();
            reader.join();

            // write the second half
            p = (HashTreeProcessor) reader.p;
            writer = new FileWriter(filenameOut);
            writer.setup(p);
            writer.start();
            writer.join();

        } catch (InterruptedException e ) { e.printStackTrace();}

    }


}
