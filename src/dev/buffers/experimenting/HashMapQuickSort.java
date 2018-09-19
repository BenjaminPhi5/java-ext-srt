package dev.buffers.experimenting;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class HashMapQuickSort {

    public static final int BUF_SIZE = 1 << 16;
    public static final int ARR_SIZE = 1000;
    private static HashMap<Integer, Short> map;
    private static byte[] buf;

    public static final void run(String fin, String fout) {

        FileInputStream fis;
        FileOutputStream fos;

        //short[] arr = new short[ARR_SIZE];
        map = new HashMap<>();
        int n;
        int bytesRead;
        int x;
        int pos;
        //byte[] buf = new byte[BUF_SIZE];
        buf = new byte[BUF_SIZE];

        try {
            fis = new FileInputStream(fin);

            while ((n = fis.read(buf)) != -1) {
                bytesRead = n / 4;
                for (int i = 0; i < bytesRead; i++) {
                    pos = 4 * i;


                    x = (((int) buf[pos]) & 255) << 24
                            | ((((int) buf[pos + 1]) & 255) << 16)
                            | ((((int) buf[pos + 2]) & 255) << 8)
                            | ((((int) buf[pos + 3]) & 255));

                    //arr[x] = (short)(arr[x]+1);
                    map.put(x, (short) (map.getOrDefault(x, (short) 0) + 1));
                }
            }

            fos = new FileOutputStream(fout);
            int i;
            int j;
            int loc = 0;
            byte b1;
            byte b2;
            byte b3;
            byte b4;

            Integer[] keys = (Integer[]) map.keySet().toArray();
            Arrays.sort(keys);
            int length = keys.length;
            int reps;

            for (i = 0; i < length; i++) {
                x = keys[i];
                b1 = (byte) (x >> 24);
                b2 = (byte) (x >> 16);
                b3 = (byte) (x >> 8);
                b4 = (byte) (x);
                reps = Short.toUnsignedInt(map.get(x));
                for (j = 0; j < reps; j++) {

                    if (loc == BUF_SIZE) {
                        try {
                            fos.write(buf);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        loc = 0;
                    }

                    buf[loc] = b1;
                    buf[loc + 1] = b2;
                    buf[loc + 2] = b3;
                    buf[loc + 3] = b4;

                    loc += 4;
                }
            }

            if (loc > 0) {
                try {
                    fos.write(buf, 0, loc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        } catch (IOException e) {
            System.err.println(e);
        }

        map = null;
        buf = null;

    }
}
