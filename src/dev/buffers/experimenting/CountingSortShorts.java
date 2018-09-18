package dev.buffers.experimenting;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CountingSortShorts {

    public static final int BUF_SIZE = 1<<16;
    public static final int ARR_SIZE = 1000;
    private static short[] arr;
    private static byte[] buf;

    public static final void run(String fin, String fout){

        FileInputStream fis;
        FileOutputStream fos;

        //short[] arr = new short[ARR_SIZE];
        arr = new short[ARR_SIZE];
        int n; int bytesRead; int x; int pos;
        //byte[] buf = new byte[BUF_SIZE];
        buf = new byte[BUF_SIZE];

        try {
            fis = new FileInputStream(fin);

            while ((n = fis.read(buf)) != -1) {
                bytesRead = n / 4;
                for (int i = 0; i < bytesRead; i++) {
                    pos = 4*i;

                    /*
                    x = (((int) buf[pos + 3]) & 255)
                            | ((((int) buf[pos + 2]) & 255) << 8)
                            | ((((int) buf[pos + 1]) & 255) << 16)
                            | ((((int) buf[pos]) & 255) << 24);

                    arr[x] = (short)(arr[x]+1);
                    */
                    x = (((int)buf[pos]) & 255) << 24
                            | ((((int) buf[pos + 1]) & 255) << 16)
                            | ((((int) buf[pos + 2]) & 255) << 8)
                            | ((((int) buf[pos + 3]) & 255));
                    arr[x] = (short)(arr[x]+1);
                }
            }

            fos = new FileOutputStream(fout);
            int i; int j; int loc = 0;
            byte b1; byte b2; byte b3; byte b4;

            int length = arr.length;

            for(i = 0; i < length; i++){
                x = Short.toUnsignedInt(arr[i]);
                if(x > 0) {
                    b1 = (byte) (i >> 24); b2 = (byte) (i >> 16); b3 = (byte) (i >> 8); b4 = (byte) (i);
                    for (j = 0; j < x; j++) {

                        if(loc == BUF_SIZE){
                            try { fos.write(buf); } catch (IOException e) { e.printStackTrace(); }
                            loc = 0;
                        }

                        buf[loc] = b1;
                        buf[loc + 1] = b2;
                        buf[loc + 2] = b3;
                        buf[loc + 3] = b4;

                        loc += 4;
                    }
                }
            }

            if(loc > 0) {
                try { fos.write(buf, 0, loc); } catch (IOException e) { e.printStackTrace(); }
            }


        } catch (IOException e) {
            System.err.println(e);
        }

        arr = null;
        buf = null;

    }


    public static void main(String[] args){
        CountingSortInts countingSort = new CountingSortInts();

        long time;
        time = System.currentTimeMillis();
        countingSort.run("test-suite/test" + 11 + "a.dat", "test-suite/test" + 11 + "b.dat");
        System.out.println("time: " + (System.currentTimeMillis() - time));

    }

}
