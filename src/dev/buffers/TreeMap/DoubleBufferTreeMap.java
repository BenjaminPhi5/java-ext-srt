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

public class DoubleBufferTreeMap {
    /*

    byte[] bufIn1; byte[] bufIn2; byte bi1count; byte bi2count; int i1size; int i2size;
    byte[] bufOut1; //byte[] bufOut2; boolean bo1fill; boolean bo2fill; int o1size; int o2size;
    boolean done = false;
    HashTreeProcessor processorRead; HashTreeProcessor processorWrite;
    private static final int BUF_SIZE = 1<<16;
    String filenameIn; String filenameOut;

    private class FileRead extends Thread{
        FileInputStream fis;
        String filename;
        Function<Integer, Boolean> function;

        FileRead(String f){
             this.filename = f;
        }

        public void run() {

            int n; int x; boolean bufChoice = true;
            for (int i = 0; i < 2; i++) {
                try {
                    fis = new FileInputStream(filename);

                    synchronized(bufIn1){
                        i1size = fis.read(bufIn1);
                        bufIn1.notify();
                        n = i1size;
                    }
                    bufChoice = false;
                    while (n != -1) {
                        if (bufChoice){
                            synchronized (bufIn1){
                                i1size = fis.read(bufIn1);
                                bi1count++;
                                bufIn1.notify();
                            }
                            n = i1size;
                        } else {
                            synchronized (bufIn2){
                                i2size = fis.read(bufIn2);
                                bi2count++;
                                bufIn2.notify();
                            }
                            n = i2size;
                        }
                        bufChoice = !bufChoice;
                    }
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            done = true;
        }
    }

    private class ProcessRead extends Thread{
        int x;

        public void run() {
            // this has duplicate code, which really could be removed with more branching or an array or something.
            boolean bufChoice = true;
            byte b1prev = 0; // these are so that you know a new buffer has been written, your nor just reading in the old one.
            byte b2prev = 0;
            Function<Integer, Boolean>[] fs = new Function[2];
            Function<Integer, Boolean> neg = x -> (x <= 0);
            Function<Integer, Boolean> pos = x -> (x > 0);
            fs[0] =neg; fs[1] = pos; byte index = -1;
            while (!done) {
                while (true) {
                    processorRead = new HashTreeProcessor();
                    index++;
                    if (bufChoice) {
                        synchronized (bufIn1) {
                            while (bi1count == b1prev) {
                                try {
                                    bufIn1.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            b1prev = bi1count;
                            if (i1size == -1)
                                break;
                            update(bufIn1, i1size, processorRead, fs[index]);
                        }
                    } else {
                        synchronized (bufIn2) {
                            while (bi2count == b2prev) {
                                try {
                                    bufIn2.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            b2prev = bi2count;
                            if (i2size == -1)
                                break;
                            update(bufIn1, i1size, processorRead, fs[index]);
                        }
                    }
                    bufChoice = !bufChoice;
                }
                // Now that have broken out of the loop, need to update the processor to the next one, and
                // the write thread needs to be notified and to switch to the next processor object.
                // when the writer switches, it needs to check if the startwrite value has been incremented, otherwise needs
                // to wait on it.
                synchronized (processorWrite) { // startwrite may be non-final but only create one if it, so its fine.
                    processorWrite = processorRead; // writer then checks this against its own reference.
                    processorWrite.notify();
                }
            }
            processorWrite = null;
        }

        private void update(byte[] buf, int n, Processor p, Function<Integer, Boolean> f){
            for (int i = 0; i < n / 4; i++) {
                x = (((int) buf[4 * i + 3]) & 255)
                        | ((((int) buf[4 * i + 2]) & 255) << 8)
                        | ((((int) buf[4 * i + 1]) & 255) << 16)
                        | ((((int) buf[4 * i]) & 255) << 24);

                // select the negatives then the positives
                if (f.apply(x)) { // could using a switch and case statement be quicker than java8 functions?
                    p.add(x); // change this to update to the next processor
                                            // once the next read section is ready.
                }
            }

        }

    }

    private class FileWrite extends Thread{
        //this will then write ready made buffers to the file b.
        FileOutputStream fos; // WRITE OUT method of choice

        // this class donesnt need the filename as a variable, as it only needs to create fos once.

        public void run(){

        }


    }

    private class ProcessWrite extends Thread{
        // needs to access processor element -1 from the array (while the reader i accessing
        // processor 1, i need to access processor 0 you see).
        // once done with a processor, set it to null so it can be garbage collected

        String f;
        ProcessWrite(String f){
            this.f = f;
        }

        public void run(){
            byte localSw = 0;
            HashTreeProcessor p = processorWrite;
            FileOutputStream fos = null;
            try { fos = new FileOutputStream(f); } catch (FileNotFoundException e) { e.printStackTrace(); }
            while (true) {
                if (processorWrite == null){
                    break;
                }
                synchronized (processorWrite) {
                    while (processorWrite == p){
                        try { processorWrite.wait(); } catch (InterruptedException e) { e.printStackTrace(); }
                    }
                    p = (HashTreeProcessor) processorWrite;
                }
                // now work only on p;
                 //Only finish work on this if using build in concurrency data structures doesn't work


                int i; byte[] result = new byte[4]; int bufSize = 0; byte[] buf = bufOut1;
                TreeMap<Integer, Byte> values = p.getValues();
                Map.Entry<Integer, Byte> x = values.pollFirstEntry();
                //boolean bufChoice = false;
                while (x != null){

                     i = x.getKey();
                    result[0] = (byte) (i >> 24); result[1] = (byte) (i >> 16);
                    result[2] = (byte) (i >> 8); result[3] = (byte) (i);
                    for(int j = 0; j < x.getValue(); j++) {
                        buf[bufSize] = result[0]; buf[1+bufSize] = result[1];
                        buf[2+bufSize] = result[2]; buf[3+bufSize] = result[3];
                        bufSize+= 4;
                        if (bufSize == BUF_SIZE) {
                            bufSize = 0;
                            try { fos.write(buf); } catch (IOException e) { e.printStackTrace(); }
                        }
                    }
                    x = values.pollFirstEntry();

                }
                if(bufSize > 0) {
                    byte[] bufE = new byte[bufSize]; //copy the last buffer, probably smaller than bufsize, to the file.
                    for (int k = 0; k < bufSize; k++) {
                        bufE[k] = buf[k];
                    }
                    try { fos.write(bufE); } catch (IOException e) { e.printStackTrace(); }
                }


            }
        }

    }



    public DoubleBufferTreeMap(String f1, String f2){
        bufIn1 = new byte[BUF_SIZE]; bufOut1 = new byte[BUF_SIZE];
        bufIn2 = new byte[BUF_SIZE];
        bi1count = 0; bi2count = 0;
        filenameIn = f1; filenameOut = f2;
        processorWrite = new HashTreeProcessor();
    }

    public void run(){
        // need to initialise the multiple processors array or whatever and see how that goes. nice.
        // and then get it to tell the writer threads to start and all that jazz (well, wake them up i suppose)
        // and they need to block when theres nothing to write.

        FileRead fileRead = new FileRead(filenameIn);
        ProcessRead processRead = new ProcessRead();
        ProcessWrite processWrite = new ProcessWrite(filenameOut);

        fileRead.start();
        processRead.start();
        processWrite.start();

        try {
            fileRead.join();
            processRead.join();
            processWrite.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    // To run this thing, essentially it should just be a case of run all the processors and then break.
*/
}
