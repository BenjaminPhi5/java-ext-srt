package dev.buffers.experimenting;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class TestSyncronised {

    private class BoolRef{
        boolean b;
    }

    public static final int BUF_SIZE = 1<<16;
    static byte[] buf1;
    static byte[] buf2;
    static BoolRef b1fill; static BoolRef b2fill;
    CountDownLatch inSwitch;
    CountDownLatch outSwitch;
    static int b1no;
    static int b2no;
    String fin; String fout;

    private class FileIO extends Thread{

        public void run(){

            RandomAccessFile fis;
            RandomAccessFile fos = null;
            Object IOLock;

            int n;
            boolean bChoice;
            try { fos = new RandomAccessFile(fout, "rw"); } catch (FileNotFoundException e) { e.printStackTrace(); }

            byte[] buf; BoolRef fill;

            for (int j = 0; j < 6; j++) {

                try {
                    buf = buf1; fill = b1fill;

                    b1fill.b = false; b2fill.b = false; bChoice = true;
                    fis = new RandomAccessFile(fin, "r");


                    inSwitch.countDown();

                    synchronized (buf1){
                        if(fill.b)
                            buf1.wait();

                        n = fis.read(buf1);
                        b1no = n;
                        fill.b = true;
                        buf1.notify();

                    }
                    bChoice = !bChoice;


                    while(n!= -1) {

                        if (bChoice) {
                            buf = buf1;
                            fill = b1fill;
                        } else {
                            buf = buf2;
                            fill = b2fill;
                        }

                        synchronized (buf) {
                            if (fill.b)
                                buf.wait();

                            n = fis.read(buf);

                            if (bChoice) b1no = n;
                            else b2no = n;

                            fill.b = true;
                            buf.notify();
                        }
                        bChoice = !bChoice;

                    }

                    if (bChoice) {
                        buf = buf1;
                        fill = b1fill;
                    } else {
                        buf = buf2;
                        fill = b2fill;
                    }

                    synchronized (buf){
                        System.out.println("sync");
                        //if(fill.b) buf.wait();
                        if(bChoice) b1no = n;
                        else b2no = n;
                        fill.b = true;
                        buf.notify();
                    }

                    fis.close();


                    outSwitch.await();
                    outSwitch = new CountDownLatch(1);

                    bChoice = true;
                    while(n!=-2){

                        if(bChoice){
                            buf = buf1; fill = b1fill;
                        } else {
                            buf = buf2; fill = b2fill;
                        }
                        synchronized (buf){
                            if(!fill.b)
                                buf.wait();
                            if(bChoice) n = b1no;
                            else n = b2no;

                            if(n!=-2){
                                System.out.println("n: " + n);
                                fos.write(buf, 0, n);
                            }

                            fill.b = false;
                            buf.notify();
                        }
                        bChoice = !bChoice;

                    }

                } catch (IOException | InterruptedException e) {
                    System.err.println(e);
                }


            }

        }

    }

    private class Processor extends Thread{

        public static final int b0 = Integer.MIN_VALUE;
        public static final int b1 = -1417140587;
        public static final int b2 = -687496445;
        public static final int b3 = 43169891;
        public static final int b4 = 773465171;
        public static final int b5 = 1503894278;
        public static final int b6 = Integer.MAX_VALUE;

        public void run(){

            int[] ints = new int[1750000];
            int boundlower = 0;
            int boundupper = 0;
            int index;
            int n;
            boolean bChoice;
            byte[] buf; BoolRef fill;

            for (int j = 0; j < 6; j++) {
                index = 0; n = 0; bChoice = true;

                switch (j) {
                    case 0: boundlower = b0; boundupper = b1;break; case 1: boundlower = b1 + 1;boundupper = b2;break;
                    case 2: boundlower = b2 + 1;boundupper = b3;break; case 3: boundlower = b3 + 1;boundupper = b4;break;
                    case 4: boundlower = b4 + 1;boundupper = b5;break;case 5: boundlower = b5 + 1;boundupper = b6;break;
                }

                try {


                    System.out.println("here b: " + inSwitch);

                    inSwitch.await();
                    inSwitch = new CountDownLatch(1);

                    while (n != -1) {
                        // selecting the buffer to update.
                        if(bChoice){
                            buf = buf1; fill = b1fill;
                        } else {
                            buf = buf2; fill = b2fill;
                        }
                        System.out.println("pb");
                        synchronized (buf){
                            System.out.println("here3");
                            if(!fill.b)
                                buf.wait();

                            if(bChoice) n = b1no;
                            else n = b2no;
                            System.out.println("here4, n: "+ n + " " + b1no + " " + b2no);

                            if(n!= -1)
                                index = getInts(ints, buf, n, index, boundlower, boundupper);
                            fill.b = false;
                            buf.notify();
                        }
                        // switching buffer for next read;
                        bChoice = !bChoice;
                    }

                    System.out.println("here2");
                    // Sort the data
                    if(index > 0) {
                        System.out.println("Sort");
                        Arrays.sort(ints, 0, index);
                        //qSort(ints, 0, index);
                    }


                    // prepare to read out the data to disk. nice.
                    bChoice = true; b1fill.b = false; b2fill.b = false;
                    int k = 0;
                    index--; // so that index now index to the last element, (so ints length -1 basically).

                    b1no = 0;
                    k = genBuf(b1fill, buf1, k, index, ints, bChoice);
                    bChoice = !bChoice; b2no = 0;

                    outSwitch.countDown();


                    while(k<=index){
                        System.out.println("here5");

                        if(bChoice){
                            k = genBuf(b1fill, buf1, k, index, ints, bChoice);
                        } else {
                            k = genBuf(b2fill, buf2, k, index, ints, bChoice);
                        }

                        bChoice = !bChoice;
                    }

                    if(bChoice){
                        buf = buf1; fill = b1fill;
                    } else {
                        buf = buf2; fill = b2fill;
                    }

                    synchronized (buf){ // signalling the reading has ended;
                        if(fill.b)
                            buf.wait();
                        if(bChoice) b1no = -2;
                        else b2no = -2;
                        fill.b = true;
                        buf.notify();
                    }

                } catch (InterruptedException e) {
                    System.err.println(e);
                }

            }

        }

        public int genBuf(BoolRef fill, byte[] buf, int k, int index, int[] ints, boolean bChoice){
            int loc = 0; int x;
            synchronized (buf){
                if(fill.b) {
                    try { buf.wait(); } catch (InterruptedException e) { e.printStackTrace(); }
                }

                while(k <= index){
                    if(loc == BUF_SIZE || loc == index){
                        if(bChoice) b1no = loc;
                        else b2no = loc;
                        break;
                    }
                    x = ints[k];
                    buf[loc] = (byte) (x >> 24); buf[loc+1] = (byte) (x >> 16);
                    buf[loc+2] = (byte) (x >> 8); buf[loc+3] = (byte) (x);

                    loc+= 4; k++;
                }
                fill.b = true;
                buf.notify();
            }
            return k;
        }

        public int getInts(int[] arr, byte[] buf, int n, int index, int bl, int bu){

            int bytesRead = n / 4; int pos; int x;
            for (int i = 0; i < bytesRead; i++) {
                pos = 4*i;
                x = (((int) buf[pos+ 3]) & 255)
                        | ((((int) buf[pos + 2]) & 255) << 8)
                        | ((((int) buf[pos + 1]) & 255) << 16)
                        | ((((int) buf[pos]) & 255) << 24);

                if ( bl <= x && x <= bu) {
                    System.out.println("x: "+ x);
                    arr[index] = x;
                    index++;
                }
            }

            return index;
        }

    }

    public void run(String fin, String fout){
        this.fin = fin; this.fout = fout;
        buf1 = new byte[BUF_SIZE];
        buf2 = new byte[BUF_SIZE];
        b1no = 0;
        b2no = 0;
        b1fill = new BoolRef(); b1fill.b = false;
        b2fill = new BoolRef(); b2fill.b = false;
        inSwitch = new CountDownLatch(1);
        outSwitch = new CountDownLatch(1);

        FileIO fileIO = new FileIO();
        Processor processor = new Processor();
        fileIO.start();
        processor.start();

        try {
            fileIO.join(); processor.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args){
        TestSyncronised testSyncronised = new TestSyncronised();

        for(int i = 0; i < 1; i++) {

            long time;
            time = System.currentTimeMillis();
            int test = 13;
            testSyncronised.run("test-suite/test" + test + "a.dat", "test-suite/test" + test + "b.dat");
            System.out.println("time: " + (System.currentTimeMillis() - time));
        }

    }


    // NO RACE CONDITION YES BEN I LOVE YOU GREAT

}
