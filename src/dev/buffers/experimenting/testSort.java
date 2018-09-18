package dev.buffers.experimenting;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;


public class testSort {

    public static final int b0 = Integer.MIN_VALUE;
    public static final int b1 = -1417140587;
    public static final int b2 = -687496445;
    public static final int b3 = 43169891;
    public static final int b4 = 773465171;
    public static final int b5 = 1503894278;
    public static final int b6 = Integer.MAX_VALUE;

    public static final int BUF_SIZE = 1 << 16;
    public static final int NO_INTS = 1750000;

    public static void main(String[] args) {
        testSort testSort = new testSort();

        long time;
        time = System.currentTimeMillis();
        int test = 16;
        testSort.run("test-suite/test" + test + "a.dat", "test-suite/test" + test + "b.dat", false);
        System.out.println("time: " + (System.currentTimeMillis() - time));

    }


    public static final void run(String filename, String fOut, boolean multRead) {

        RandomAccessFile fis;
        RandomAccessFile fos = null;
        File a = new File(filename); File b = new File(fOut);

        int[] ints = new int[NO_INTS];
        byte buf[] = new byte[BUF_SIZE];
        int boundlower = 0;
        int boundupper = 0;
        int n; //int i;
        int bytesRead;
        int index = 0;
        int x;
        int loc;
        try {
            fos = new RandomAccessFile(b, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int runs = (multRead) ? 6 : 1;

        for (int j = 0; j < runs; j++) {
            index = 0;

            switch (j) {
                case 0:
                    boundlower = b0;
                    boundupper = b1;
                    break;
                case 1:
                    boundlower = b1 + 1;
                    boundupper = b2;
                    break;
                case 2:
                    boundlower = b2 + 1;
                    boundupper = b3;
                    break;
                case 3:
                    boundlower = b3 + 1;
                    boundupper = b4;
                    break;
                case 4:
                    boundlower = b4 + 1;
                    boundupper = b5;
                    break;
                case 5:
                    boundlower = b5 + 1;
                    boundupper = b6;
                    break;
            }

            try {
                fis = new RandomAccessFile(a, "r");
                int pos;

                while ((n = fis.read(buf)) != -1) {
                    bytesRead = n / 4;
                    if(multRead) {

                        for (int i = 0; i < bytesRead; i++) {
                            pos = 4 * i;
                            x = (((int) buf[pos + 3]) & 255)
                                    | ((((int) buf[pos + 2]) & 255) << 8)
                                    | ((((int) buf[pos + 1]) & 255) << 16)
                                    | ((((int) buf[pos]) & 255) << 24);

                            if (boundlower <= x && x <= boundupper) {
                                ints[index] = x;
                                index++;
                            }
                        }
                    } else {
                        for (int i = 0; i < bytesRead; i++) {
                            pos = 4 * i;
                            x = (((int) buf[pos + 3]) & 255)
                                    | ((((int) buf[pos + 2]) & 255) << 8)
                                    | ((((int) buf[pos + 1]) & 255) << 16)
                                    | ((((int) buf[pos]) & 255) << 24);

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
                DPQsort.dpQsort(ints, 0, index-1);
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

    }
}