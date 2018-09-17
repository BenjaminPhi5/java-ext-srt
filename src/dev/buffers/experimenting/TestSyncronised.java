package dev.buffers.experimenting;

import java.io.*;
import java.util.Arrays;

public class TestSyncronised {

    public static final int BUF_SIZE = 1<<16;
    static byte[] buf1;
    static byte[] buf2;
    static boolean b1fill; static boolean b2fill;
    static int b1no;
    static int b2no;
    String fin; String fout;

    private class FileIO extends Thread{

        public void run(){

            RandomAccessFile fis;
            RandomAccessFile fos = null;

            int n;
            boolean bChoice = true;
            try { fos = new RandomAccessFile(fout, "rw"); } catch (FileNotFoundException e) { e.printStackTrace(); }

            for (int j = 0; j < 6; j++) {
                n = 0;

                try {
                    fis = new RandomAccessFile(fin, "r");
                    /*
                    synchronized (buf1){
                        while (pending > 1){
                            buf1.wait();
                        }
                        n = fis.read(buf1);
                        b1no = n;
                        pending++;
                        buf1.notify();
                    }
                    */

                    while(n!= -1){
                        if(bChoice){
                            synchronized (buf1){
                                if (b1fill){
                                    buf1.wait();
                                }
                                n = fis.read(buf1);
                                b1no = n;
                                b1fill = true;
                                buf1.notify();
                            }
                        } else {
                            synchronized (buf2){
                                if (b2fill){
                                    buf2.wait();
                                }
                                n = fis.read(buf2);
                                b2no = n;
                                b2fill = true;
                                buf2.notify();
                            }
                        }
                        bChoice = !bChoice;
                    }


                    fis.close();

                // then need to do the thing where threads wait to be at the same point.
                // from then after process buffer has buffer 1 ready, get started on writing.

                    /*
                    bChoice = true;
                    while(n!=-1){
                        if(bChoice){
                            synchronized (buf1){
                                n = b1no;
                                if(n > 0)
                                    fos.write(buf1, 0, b1no);
                            }
                        } else {
                            synchronized (buf2){
                                n = b2no;
                                if(n > 0)
                                    fos.write(buf2, 0, b2no);
                            }
                        }
                        bChoice = !bChoice;
                    }
                    */
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
            boolean bChoice = true;

            for (int j = 0; j < 6; j++) {
                index = 0; n = 0;

                switch (j) {
                    case 0: boundlower = b0; boundupper = b1;break; case 1: boundlower = b1 + 1;boundupper = b2;break;
                    case 2: boundlower = b2 + 1;boundupper = b3;break; case 3: boundlower = b3 + 1;boundupper = b4;break;
                    case 4: boundlower = b4 + 1;boundupper = b5;break;case 5: boundlower = b5 + 1;boundupper = b6;break;
                }

                try {



                    while (n != -1) {
                        //System.out.println("here again");
                        if(bChoice){
                            synchronized (buf1){
                                if (!b1fill){
                                    //System.out.println("here1");
                                    buf1.wait();
                                }
                                n = b1no;
                                if(n!= -1)
                                    index = getInts(ints, buf1, b1no, index, boundlower, boundupper);
                                b1fill = false;
                                buf1.notify();

                            }
                        } else {
                            synchronized (buf2){
                                //System.out.println("here2");
                                if (!b2fill){
                                    buf2.wait();
                                }
                                n = b2no;
                                if(n!=-1)
                                    index = getInts(ints, buf2, b2no, index, boundlower, boundupper);
                                b2fill = false;
                                buf2.notify();

                            }
                        }
                        //System.out.println("got out");
                        bChoice = !bChoice;
                    }
                } catch (InterruptedException e) {
                    System.err.println(e);
                }

            }

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
                    //System.out.println("x: "+ x);
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
        b1fill = false;
        b2fill = false;

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

        for(int i = 0; i < 100; i++) {

            long time;
            time = System.currentTimeMillis();
            int test = 17;
            testSyncronised.run("test-suite/test" + test + "a.dat", "test-suite/test" + test + "b.dat");
            System.out.println("time: " + (System.currentTimeMillis() - time));
        }

    }


    // NO RACE CONDITION YES BEN I LOVE YOU GREAT

}
