package dev.buffers.experimenting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class SingleReadSort {

    public static final int BUF_SIZE = 1 << 16;
    private static byte[] buf;
    private static int[] ints;

    public static final void run(String filename, String fOut) {

        RandomAccessFile fis;
        RandomAccessFile fos = null;
        File a = new File(filename); File b = new File(fOut);
        ForkJoinQsort sorter;


        //byte buf[] = new byte[BUF_SIZE];
        buf = new byte[BUF_SIZE];
        int n; //int i;
        int bytesRead;
        int index = 0;
        int x;
        int loc;

        //System.out.println("number of divisions "+Math.ceil(a.length()/(r.freeMemory()*R)));
        //System.out.println("max buffer size is: "+ Math.floor(r.freeMemory()*R/4));

        //int[] ints = new int[(int)a.length()/4];
        ints = new int[(int)a.length()/4];
        int bound = 0;

        try {
            fos = new RandomAccessFile(b, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        index = 0;

        try {
            fis = new RandomAccessFile(a, "r");
            int pos;
            while ((n = fis.read(buf)) != -1) {
                bytesRead = n / 4;
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
            fis.close();
        } catch (IOException e) {
            System.err.println(e);
        }

        if (index > 0) {
            sorter = new ForkJoinQsort();
            sorter.run(ints, 0, index-1);
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

        //Path temp = a.toPath();
        //b.renameTo(a);

        //Alg finished, set relevant values to null to save space.
        buf = null; ints = null; sorter = null; fis = null; fos = null;

    }

}
