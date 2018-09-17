package dev.buffers.experimenting;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;


public class testSort {

    //public static final int bound = 536870912;
    //public static final int sizeP = Integer.MAX_VALUE/3;
    //public static final int size = (Integer.MIN_VALUE/3);


    public static final int b0 = Integer.MIN_VALUE;
    public static final int b1 = -1417140587;
    public static final int b2 = -687496445;
    public static final int b3 = 43169891;
    public static final int b4 = 773465171;
    public static final int b5 = 1503894278;
    public static final int b6 = Integer.MAX_VALUE;
    //public static final int b7 = 0;
    //public static final int b8 = 0;


/*
    public static final int b0 = Integer.MIN_VALUE;
    public static final int b1 = b0 + bound;
    public static final int b2 = b1+ bound;
    public static final int b3 = b2+bound;
    public static final int b4 = b3+ bound;
    public static final int b5 = b4+ bound;
    public static final int b6 =  b5+bound;
    public static final int b7 = b6+bound;
    public static final int b8 = Integer.MAX_VALUE;
*/

    public static final int BUF_SIZE = 1<<16;

    FileInputStream fis;
    FileOutputStream fos;

    public static void main(String[] args){
        testSort testSort = new testSort();

        long time;
        time = System.currentTimeMillis();
        int test = 17;
        testSort.run("test-suite/test" + test + "a.dat", "test-suite/test" + test + "b.dat");
        System.out.println("time: " + (System.currentTimeMillis() - time));

    }


    public  void run(String filename, String fOut) {

        int[] ints = new int[1750000];
        byte buf[] = new byte[BUF_SIZE];
        int boundlower = 0;
        int boundupper = 0;
        int n; //int i;
        int bytesRead;
        int index = 0;
        int x; int loc;
        try { fos = new FileOutputStream(fOut); } catch (FileNotFoundException e) { e.printStackTrace(); }

        for (int j = 0; j < 6; j++) {
            index = 0;

            switch (j) {
                case 0: boundlower = b0; boundupper = b1;break; case 1: boundlower = b1 + 1;boundupper = b2;break;
                case 2: boundlower = b2 + 1;boundupper = b3;break; case 3: boundlower = b3 + 1;boundupper = b4;break;
                case 4: boundlower = b4 + 1;boundupper = b5;break;case 5: boundlower = b5 + 1;boundupper = b6;break;
                //case 6: boundlower = b6 + 1;boundupper = b7; break;//case 7: boundlower = b7 + 1;boundupper = b8;break;
            }

            try {
                fis = new FileInputStream(filename);
                int pos;

                while ((n = fis.read(buf)) != -1) {
                    bytesRead = n / 4;
                    for (int i = 0; i < bytesRead; i++) {
                        pos = 4*i;
                        x = (((int) buf[pos+ 3]) & 255)
                                | ((((int) buf[pos + 2]) & 255) << 8)
                                | ((((int) buf[pos + 1]) & 255) << 16)
                                | ((((int) buf[pos]) & 255) << 24);

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
                //qSort(ints, 0, index);
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

    private void insSort(int arr[], int start, int end){
        int i = start + 1; int j; int temp;
        while(i < end){
            j = i;
            while(j > start && arr[j-1] > arr[j]){
                temp = arr[j-1]; arr[j-1] = arr[j]; arr[j] = temp;
                j = j-1;
            }
            i = i+1;
        }

    }

    private void qSort(int[] arr, int start, int end){

        if(end-start <= 27){
            insSort(arr,start, end);
            return;
        }

        int pivot = arr[start + (end-start)/2];
        int l = start; int r = end; int temp;
        while(l <= r) {
            while(l <= end && arr[l] < pivot)
                l++;
            while(r >= 0 && arr[r] > pivot)
                r--;
            if(l <= r){
                temp = arr[l]; arr[l] = arr[r]; arr[r] = temp;
                l++; r--;
            }
        }
        if(start < r)
            qSort(arr, start, r);
        if(end > l)
            qSort(arr, l, end);
    }
}
