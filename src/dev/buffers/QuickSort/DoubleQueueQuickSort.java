package dev.buffers.QuickSort;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

public class DoubleQueueQuickSort {

    // Using two concurrent linked queues.
    // When done, put a a byte array of length 1 on the queue, as no integer byte arrays are of length one;
    // Then, use the code from the concurrent linked queue and see if it works. nice.

    ConcurrentLinkedQueue<byte[]> inQueue;
    ConcurrentLinkedQueue<ArrayList<Integer>> processQueue;
    ConcurrentLinkedQueue<byte[]> outQueue;
    //Boolean writeStart;
    String fIn;
    String fOut;
    ArrayList<Integer> endProcessor;
    private static final int BUF_SIZE = 1 << 16;
    private static final byte NO_BUFFERS = 15; // replace the 5 in code with the no of buffers if nessesary
    // NOTE if code doesnt work may need to increase No_Buffers

    private class FileReader extends Thread {

        // Im going to hope that it never gets more than 10 buffers ahead, and so
        // dont have to keep creating new buffer objects;
        //byte[][] buffers;

        public void run(){
            //byte[][] buffers = new byte[NO_BUFFERS][BUF_SIZE];
            byte[] endRead = new byte[1];
            byte[] end = new byte[2];
            byte buf[]; int index = 0;

            FileInputStream fis = null;
            int n = 0;
            long time;

            for(int i = 0; i < 2; i++){
                try { fis = new FileInputStream(fIn); } catch (FileNotFoundException e) { e.printStackTrace(); }

                buf = new byte[BUF_SIZE];
                //buf = buffers[index];
                try { n = fis.read(buf); } catch (IOException e) { e.printStackTrace(); }

                while (n != -1) {

                    if(n== BUF_SIZE)
                        inQueue.offer(buf);
                    else {
                        byte[] lastBuf = new byte[n];
                        for(int j = 0; j < n; j++){
                            lastBuf[j] = buf[j];
                        }
                        inQueue.offer(lastBuf);
                    }

                    //buf = new byte[BUF_SIZE];
                    //index = (index+1)%NO_BUFFERS;
                    //buf = buffers[index];


                    time = System.nanoTime();
                    try { n = fis.read(buf); } catch (IOException e) { e.printStackTrace(); }
                    System.out.println("r time: " + (System.nanoTime() - time));


                }
                //writeStart = true; // most do before offering the end buffer
                inQueue.offer(endRead);
            }
            inQueue.offer(end);
        }

    }

    private class InProcessor extends Thread {

        public void run(){
            byte[] buf;
            ArrayList<Integer> processor = new ArrayList<>();
            Function<Byte, Boolean> neg = x -> (x <= 0); Function<Byte, Boolean> pos = x -> (x > 0);
            Function<Byte, Boolean> f = neg;
            long time;

            while ((buf = inQueue.poll()) == null || buf.length!= 2) {

                if (buf != null) {
                    if(buf.length == 1) {
                            /*
                            synchronized (writeStart) {
                                if(writeStart)
                                    writeStart.notifyAll();
                            }
                            */

                        processQueue.offer(processor);
                        processor = new ArrayList<>();
                        f = pos;
                    } else {
                        time = System.nanoTime();
                        update(buf, processor, f);
                        System.out.println("p time: " + (System.nanoTime() - time));
                    }

                } else {
                    Thread.yield();
                }
            }
            inQueue.offer(new byte[2]);
        }

        public void update(byte[] buf, ArrayList<Integer> p, Function<Byte, Boolean> f) {
            //System.out.println("start buffer");
            //int n = buf.length/4;
            int n = buf.length;
            int x;
            for(int i = 0; i < n; i+= 4) {

                if(f.apply(buf[i])) {

                    x = (((int) buf[i + 3]) & 255)
                            | ((((int) buf[i + 2]) & 255) << 8)
                            | ((((int) buf[i + 1]) & 255) << 16)
                            | ((((int) buf[i]) & 255) << 24);
                    p.add(x);
                }

            }
        }

    }

    private class OutProcessor extends Thread {

        public void run(){
            //byte[][] buffers = new byte[NO_BUFFERS][BUF_SIZE];
            /*
            synchronized (writeStart) { // check to see if time to start running.
                if(!writeStart) {
                    try { writeStart.wait(); } catch (InterruptedException e) { e.printStackTrace(); }
                }
            }
            */


            ArrayList<Integer> p;
            while ((p = processQueue.poll()) == null || p != endProcessor) {
                if (p != null) {
                    outputBuffers(p);
                } else {
                    //System.out.println("p null");
                    Thread.yield();
                }
            }
            processQueue.offer(endProcessor);

        }

        public void outputBuffers(ArrayList<Integer> p){

            Collections.sort(p);
            byte[] buf = new byte[BUF_SIZE];
            int index = 0;

            for(int i : p) {

                buf[index] = (byte) (i >> 24);
                buf[index + 1] = (byte) (i >> 16);
                buf[index + 2] = (byte) (i >> 8);
                buf[index + 3] = (byte) (i);

                index += 4;

                if (index == BUF_SIZE) {
                    outQueue.offer(buf);
                    index = 0;
                    buf = new byte[BUF_SIZE];
                }
            }

            if(index > 0) {
                byte[] bufE = new byte[index]; //copy the last buffer, probably smaller than bufsize, to the file.
                for (int i = 0; i < index; i++) {
                    bufE[i] = buf[i];
                }
                outQueue.offer(bufE);
            }
        }

    }

    private class FileWriter extends Thread {

        public void run(){
            FileOutputStream fos = null;
            try { fos = new FileOutputStream(fOut); } catch (FileNotFoundException e) { e.printStackTrace(); }

            // check if time to start writing yet
            /*
            synchronized (writeStart) {
                if(!writeStart){
                    try { writeStart.wait(); } catch (InterruptedException e) { e.printStackTrace(); }
                }
            }
            */

            byte[] buf;
            while ((buf = outQueue.poll()) == null || buf.length != 1) {
                if (buf != null) {

                    try { fos.write(buf); } catch (IOException e) { e.printStackTrace(); }

                } else {
                    Thread.yield();
                }
            }
            outQueue.offer(new byte[1]);

        }

    }

    public DoubleQueueQuickSort(String f1, String f2) {
        fIn = f1;
        fOut = f2;
        inQueue = new ConcurrentLinkedQueue<>();
        processQueue = new ConcurrentLinkedQueue<>();
        outQueue = new ConcurrentLinkedQueue<>();
        endProcessor = new ArrayList<>();
        //writeStart = false;

    }

    public void run(){
        FileReader fileReader = new FileReader();

        InProcessor inProcessor = new InProcessor();
        fileReader.start();
        inProcessor.start();

        OutProcessor outProcessor = new OutProcessor();
        outProcessor.start();

        FileWriter fileWriter = new FileWriter();
        fileWriter.start();


        try {
            fileReader.join();
            inQueue.offer(new byte[2]);

            inProcessor.join();
            processQueue.offer(endProcessor);
            //processQueue.offer(null); see if offering null will work

            outProcessor.join();
            outQueue.offer(new byte[1]);

            fileWriter.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

