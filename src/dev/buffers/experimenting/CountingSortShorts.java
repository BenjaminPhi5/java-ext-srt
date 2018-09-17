package dev.buffers.experimenting;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class CountingSortShorts {
    FileInputStream fis;
    FileOutputStream fos;
    public static final int BUF_SIZE = 1<<16;
    public static final int ARR_SIZE = 1000;

    public void run(String fin, String fout){

        short[] arr = new short[ARR_SIZE];
        int n; int bytesRead; int x; int pos;
        byte[] buf = new byte[BUF_SIZE];
        //short one = 1;


        try {
            fis = new FileInputStream(fin);

            while ((n = fis.read(buf)) != -1) {
                bytesRead = n / 4;
                for (int i = 0; i < bytesRead; i++) {
                    pos = 4*i;

                    x = (((int) buf[pos + 3]) & 255)
                            | ((((int) buf[pos + 2]) & 255) << 8)
                            | ((((int) buf[pos + 1]) & 255) << 16)
                            | ((((int) buf[pos]) & 255) << 24);

                    arr[x] = (short)(arr[x]+1);
                }
            }
            //fis.close();


            fos = new FileOutputStream(fout);
            int i; int j; int loc = 0;
            byte b1; byte b2; byte b3; byte b4;

            for(i = 0; i < arr.length; i++){
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



    }


    public static void main(String[] args){
        CountingSortInts countingSort = new CountingSortInts();

        long time;
        time = System.currentTimeMillis();
        countingSort.run("test-suite/test" + 11 + "a.dat", "test-suite/test" + 11 + "b.dat");
        System.out.println("time: " + (System.currentTimeMillis() - time));

    }

}
