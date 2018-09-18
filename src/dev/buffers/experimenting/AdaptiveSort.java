package dev.buffers.experimenting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class AdaptiveSort {

    private static int[] ints;
    private static byte[] buf;

    public static final int BUF_SIZE = 1 << 16;
    public static final double R = 0.837;

    public static void main(String[] args) {
        AdaptiveSort adaptiveSort = new AdaptiveSort();

        long time;
        time = System.currentTimeMillis();
        int test = 17;
        //adaptiveSort.run("test-suite/test" + test + "a.dat", "test-suite/test" + test + "b.dat", true);
        System.out.println("time: " + (System.currentTimeMillis() - time));

    }


    public static final void run(String filename, String fOut, boolean multRead, long length) {

        // need to get the amount of memory and also say the filesize and try working things out that way.
        // work out the amount of boundaries needed, and then add one more.
        // so filesize / max possible buffer size;

        RandomAccessFile fis;
        RandomAccessFile fos = null;
        File a = new File(filename); File b = new File(fOut);
        ForkJoinQsort sorter;


        //byte buf[] = new byte[BUF_SIZE];
        buf = new byte[BUF_SIZE];
        int boundlower = 0;
        int boundupper = 0;
        int n; //int i;
        int bytesRead;
        int index = 0;
        int x;
        int loc;
        //int[] ints;

        int bound = 0;

        try {
            fos = new RandomAccessFile(b, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //System.gc();
        Runtime r = Runtime.getRuntime();
        double free = r.freeMemory()*R;
        int divs = (int)Math.ceil(length/(free));
        int bufSize = (int)Math.floor(free/4);

        //System.out.println("number of divisions "+Math.ceil(a.length()/(r.freeMemory()*R)));
        //System.out.println("max buffer size is: "+ Math.floor(r.freeMemory()*R/4));
        //System.out.println("test: "+ filename);

        if(divs <= 1){
            multRead = false;
            ints = new int[(int)length/4];
        } else {
            bound = Integer.MAX_VALUE/(divs/2);
            ints = new int[bufSize];
        }
        short runs = (multRead) ? (short)divs : 1;


        for (int j = 0; j < runs; j++) {
            index = 0;

            if(j == 0) {
                boundlower = Integer.MIN_VALUE;
                boundupper = boundlower + bound;
            }
            else {
                boundlower = boundupper+1;
                if(j == runs-1){
                    boundupper = Integer.MAX_VALUE;
                } else {
                    boundupper = boundlower + bound -1;
                }
            }



            try {
                fis = new RandomAccessFile(a, "r");
                int pos;

                while ((n = fis.read(buf)) != -1) {
                    bytesRead = n / 4;
                    if(multRead) {

                        for (int i = 0; i < bytesRead; i++) {
                            pos = 4 * i;

                            x = (((int)buf[pos]) & 255) << 24
                                    | ((((int) buf[pos + 1]) & 255) << 16)
                                    | ((((int) buf[pos + 2]) & 255) << 8)
                                    | ((((int) buf[pos + 3]) & 255));


                            if (boundlower <= x && x <= boundupper) {
                                ints[index] = x;
                                index++;
                            }
                        }
                    } else {
                        for (int i = 0; i < bytesRead; i++) {
                            pos = 4 * i;

                            x = (((int)buf[pos]) & 255) << 24
                                    | ((((int) buf[pos + 1]) & 255) << 16)
                                    | ((((int) buf[pos + 2]) & 255) << 8)
                                    | ((((int) buf[pos + 3]) & 255));

                            ints[index] = x;
                            index++;
                        }
                    }
                }
                fis.close();
            } catch (IOException e) {
                System.err.println(e);
            }

            if (index > 0) {
                //Arrays.sort(ints, 0, index);
                //DPQsort.dpQsort(ints, 0, index-1);
                //ThreadedDPQsort sorter = new ThreadedDPQsort();
                //sorter.dpQsort(ints, 0, index-1);
                sorter = new ForkJoinQsort();
                sorter.run(ints, 0, index-1);
                //dualPivotQuicksort(ints, 0, index-1, 3);
            }


            loc = 0;
            for (int k = 0; k < index; k++) {
                x = ints[k];
                //System.out.println("x: "+ x);
                if (loc == BUF_SIZE) {
                    try {
                        fos.write(buf);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    loc = 0;
                }
                buf[loc] = (byte) (x >> 24);
                buf[loc + 1] = (byte) (x >> 16);
                buf[loc + 2] = (byte) (x >> 8);
                buf[loc + 3] = (byte) (x);

                loc += 4;

            }

            if (loc > 0) {
                try {
                    fos.write(buf, 0, loc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        //Path temp = a.toPath();
        //b.renameTo(a);

        //Alg finished, set relevant values to null to save space.
        buf = null; ints = null; sorter = null; fis = null; fos = null;
        r = null;

    }
}
