package dev.buffers.experimenting;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SortBytes {

    public static final byte b0 = -128;
    public static final byte b1 = -85;
    public static final byte b2 = -41;
    public static final byte b3 = 2;
    public static final byte b4 = 46;
    public static final byte b5 = 89;
    public static final byte b6 = 127;

    public static final int BUF_SIZE = 1 << 16;
    public static final int NO_BYTES = 1750000*4;

    FileInputStream fis;
    FileOutputStream fos;

    public static void main(String[] args) {
        SortBytes sortBytes = new SortBytes();

        long time;
        time = System.currentTimeMillis();
        int test = 17;
        sortBytes.run("test-suite/test" + test + "a.dat", "test-suite/test" + test + "b.dat");
        System.out.println("time: " + (System.currentTimeMillis() - time));

    }


    public void run(String filename, String fOut) {

        byte[] bytes = new byte[NO_BYTES];
        byte buf[] = new byte[BUF_SIZE];
        byte boundlower = 0;
        byte boundupper = 0;
        int n; //int i;
        int index = 0;
        byte x;
        int loc;
        try {
            fos = new FileOutputStream(fOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (int j = 0; j < 6; j++) {
            index = 0;

            switch (j) {
                case 0: boundlower = b0;boundupper = b1;break;
                case 1: boundlower = b1 + 1;boundupper = b2;break;
                case 2: boundlower = b2 + 1;boundupper = b3;break;
                case 3: boundlower = b3 + 1;boundupper = b4;break;
                case 4: boundlower = b4 + 1;boundupper = b5;break;
                case 5: boundlower = b5 + 1;boundupper = b6;break;
            }

            try {
                fis = new FileInputStream(filename);

                while ((n = fis.read(buf)) != -1) {
                    for (int i = 0; i < n; i+=4) {
                        x = buf[i];

                        if (boundlower <= x && x <= boundupper) {
                            //System.out.println("reached");
                            bytes[index] = x; bytes[index+1] = buf[i+1];
                            bytes[index+2] = buf[i+2]; bytes[index+3] = buf[i+3];
                            index+=4;
                        }
                    }

                }
                fis.close();
            } catch (IOException e) {
                System.err.println(e);
            }

            if (index > 0) {
                DPQBsort.dpQsort(bytes, 0, index-4);
            }

            loc = 0;
            for (int k = 0; k < index; k+=4) {
                if (loc == BUF_SIZE) {
                    try { fos.write(buf); } catch (IOException e) { e.printStackTrace(); }
                    loc = 0;
                }
                buf[loc] = bytes[k];buf[loc + 1] = bytes[k+1];buf[loc + 2] = bytes[k+2];buf[loc + 3] = bytes[k+3];

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

    }

}
