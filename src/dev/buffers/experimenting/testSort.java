package dev.buffers.experimenting;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;


public class testSort {

    public static final int bound = 536870912;
    //public static final int sizeP = Integer.MAX_VALUE/3;
    //public static final int size = (Integer.MIN_VALUE/3);
    public static final int b0 = Integer.MIN_VALUE;
    public static final int b1 = b0 + bound;
    public static final int b2 = b1+ bound;
    public static final int b3 = b2+bound;
    public static final int b4 = b3+ bound;
    public static final int b5 = b4+ bound;
    public static final int b6 =  b5+bound;
    public static final int b7 = b6+bound;
    public static final int b8 = Integer.MAX_VALUE;

    public static final int BUF_SIZE = 1<<16;

    FileInputStream fis;
    FileOutputStream fos;

    public static void main(String[] args){
        testSort testSort = new testSort();

        long time;
        time = System.currentTimeMillis();
        testSort.run("test-suite/test" + 17 + "a.dat", "test-suite/test" + 17 + "b.dat");
        System.out.println("time: " + (System.currentTimeMillis() - time));

    }


    public  void run(String filename, String fOut) {

        int[] ints = new int[1800000];
        byte buf[] = new byte[BUF_SIZE];
        int boundlower = 0;
        int boundupper = 0;
        int n;
        int bytesRead;
        int index = 0;
        int x; int loc;
        try { fos = new FileOutputStream(fOut); } catch (FileNotFoundException e) { e.printStackTrace(); }

        for (int j = 0; j < 8; j++) {
            index = 0;

            switch (j) {
                case 0: boundlower = b0; boundupper = b1;break; case 1: boundlower = b1 + 1;boundupper = b2;break;
                case 2: boundlower = b2 + 1;boundupper = b3;break; case 3: boundlower = b3 + 1;boundupper = b4;break;
                case 4: boundlower = b4 + 1;boundupper = b5;break;case 5: boundlower = b5 + 1;boundupper = b6;break;
                case 6: boundlower = b6 + 1;boundupper = b7;break;case 7: boundlower = b7 + 1;boundupper = b8;break;
            }

            try {
                fis = new FileInputStream(filename);

                while ((n = fis.read(buf)) != -1) {
                    bytesRead = n / 4;
                    for (int i = 0; i < bytesRead; i++) {

                        x = (((int) buf[4 * i + 3]) & 255)
                                | ((((int) buf[4 * i + 2]) & 255) << 8)
                                | ((((int) buf[4 * i + 1]) & 255) << 16)
                                | ((((int) buf[4 * i]) & 255) << 24);

                        if (boundlower <= x && x <= boundupper) {
                            //System.out.println("x: "+ x);
                            ints[index] = x;
                            index++;
                        }
                    }
                }
                fis.close();
            } catch (IOException e) {
                System.err.println(e);
            }

            if(index > 0) {
                Arrays.sort(ints, 0, index);
            }


            loc = 0;
            for(int k = 0; k < index; k++){
                x = ints[k];
                //System.out.println("x: "+ x);
                if(loc == BUF_SIZE){
                    try { fos.write(buf); } catch (IOException e) { e.printStackTrace(); }
                    loc = 0;
                }
                buf[loc] = (byte) (x >> 24); buf[loc+1] = (byte) (x >> 16);
                buf[loc+2] = (byte) (x >> 8); buf[loc+3] = (byte) (x);

                loc += 4;

            }

            if(loc > 0) {
                try { fos.write(buf, 0, loc); } catch (IOException e) { e.printStackTrace(); }
            }

        }

    }

}
