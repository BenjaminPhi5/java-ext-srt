package dev.buffers.experimenting;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Random;

public class DPQBsort {

    public static void dpQsort(byte[] arr, int start, int end) {
        // here take start to point to the first byte of the first int, and
        // end to point to the first byte of the last int.

        int p; int q;

        int len = end-start;
        if (len < 108) { // insertion sort for tiny array
            //System.out.print("called\n");
            for (int i = start + 4; i <= end; i+=4) {
                for (int j = i; j > start && lessBB(arr, j, j-4); j-=4) {
                    swap(arr, j, j - 4);
                }
            }
            return;
        }



        // else double pivot quick sort
        if(lessBB(arr, end, start)){
            swap(arr, start, end);
        }

        //p= arr[start]; q = arr[end];

        int l = start+4;
        int g = end-4;

        for(int k = l; k<= g; k+=4){
            //System.out.print("k: "+k);

            // if element is less than left pointer
            if(lessBB(arr, k, start)){
                swap(arr, k, l); // pointer to less than left pointer moves up by one
                l+=4;
            }

            // if element between left point and right pointer dont care
            // as already in right place

            // if element >= the right pivot.
            else if(lessBB(arr, end, k)){

                // may be able to move where the right pivot section starts first
                while(k<g && lessBB(arr, end, g)) {
                    g-=4;
                }

                // at this point, know arr[k] is greater than rp, so stick it behind where g points
                swap(arr, k, g);
                g-=4;
                //subsequently move the >= rp sectin down by one

                // check if new element at arr[k] the old g isnt now in wrong section (its currently in the between
                // lp and rp section, but may infact be less than the rp
                if(lessBB(arr, k, start)){
                    swap(arr, k, l);
                    l+=4;
                }
            }
        }
        // put pivots into positions
        swap(arr, l-4, start);
        swap(arr, g+4, end);



        //recursively call the quick sort now
        dpQsort(arr, start, l-8);
        dpQsort(arr, l, g);
        dpQsort(arr, g+8, end);

    }

    private static void swap( byte[] a, int i, int j) {
        byte temp = a[i]; a[i] = a[j]; a[j] = temp;
        temp = a[i+1]; a[i+1] = a[j+1]; a[j+1] = temp;
        temp = a[i+2]; a[i+2] = a[j+2]; a[j+2] = temp;
        temp = a[i+3]; a[i+3] = a[j+3]; a[j+3] = temp;
    }

    public static boolean lessBB(byte[] arr, int locx, int locy){
        int a; int b;
        a = arr[locx]; b = arr[locy];

        if(a < 0 && b >= 0) return true;
        else if (a >=0 && b < 0) return false;

        if(a < b)
            return true;
        else if (a > b)
            return false;

        for(int i = 1; i < 4; i++){
            a = arr[locx+i]&0xff; b = arr[locy+i]&0xff;
            if(a < b)
                return true;
            else if (a > b)
                return false;
        }
        return false; // the numbers are equal
        /*
        // compares if one byte[] is less than another byte[]

        byte a; byte b; a = arr[locx]; b = arr[locy]; boolean my = false; boolean ac;
        if(a < 0 && b >= 0) return true;
        else if (a >=0 && b < 0) return false;

        if(a < b) {
            //return true;
            my = true;
        }
        else if (a > b){
            //return false;
            my = false;
        } else {

            for (int i = 1; i < 4; i++) {
                a = arr[locx + i];
                b = arr[locy + i];
                if (a < b) {
                    //return true;
                    my = true;break;
                }
                else if (a > b) {
                    //return false;
                    my = false; break;
                }
            }
            //return false; // the numbers are equal
        }

        byte[] a1 = new byte[]{arr[locx], arr[locx+1], arr[locx+2], arr[locx+3]};
        byte[] b1 = new byte[]{arr[locy], arr[locy+1], arr[locy+2], arr[locy+3]};
        ac =  ByteBuffer.wrap(a1).getInt() < ByteBuffer.wrap(b1).getInt();
        if(ac != my){
            System.out.println("a: "+ ByteBuffer.wrap(a1).getInt()+"\tb:" + ByteBuffer.wrap(b1).getInt());
        }
        return ac;
        */
    }

    public static final byte[] shifts = new byte[]{24, 16, 8, 0};

    public static boolean lessBI(byte[] arr, int loc, int y){
        //compares if a byte[] is less than an int;
        byte a; int b; a = arr[loc]; b = y >> 24;
        if(a < 0 && b >= 0) return true;
        else if (a >=0 && b < 0) return false;

        for(int i = 1; i < 4; i++){
            a = arr[i+loc]; b = y>>shifts[i];
            if(a < b)
                return true;
            else if (a > b)
                return false;
        }
        return false; // the numbers are equal
    }

    public static boolean lessIB(byte[] arr, int loc, int y){
        //compares if a byte[] is less than an int;
        byte b; int a; b = arr[loc]; a = y >> 24;
        if(a < 0 && b >= 0) return true;
        else if (a >=0 && b < 0) return false;

        for(int i = 1; i < 4; i++){
            b = arr[i+loc]; a = y>>shifts[i];
            if(a < b)
                return true;
            else if (a > b)
                return false;
        }
        return false; // the numbers are equal
    }

    public static byte[] intToBytes( final int i ) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
    }

    public static int bytesToInt(byte[] a){
        return ByteBuffer.wrap(a).getInt();
    }

    public static void main(String[] args){
        byte[] bytes = new byte[400]; byte[] next = new byte[4];
        Random random = new Random(); int x;
        for(int i = 0; i < 100; i++){
            x = random.nextInt();
            System.out.print(", "+x);
            next = intToBytes(x);
            //System.out.print("b " + next[3]);
            for(int j = 0; j < 4; j++){
                bytes[i*4+j] = next[j];
            }
            //System.out.print(", " + bytes[i*4+3]);
        }
        System.out.println("\n------");
        System.out.print("bytes: 3: " + bytes[3] + "\n");
        dpQsort(bytes, 0, 396);


        byte[] a = new byte[4];
        for(int i = 0; i < bytes.length; i+= 4){
            a[0] = bytes[i]; a[1] = bytes[i+1]; a[2] = bytes[i+2]; a[3] = bytes[i+3];
            System.out.print(", " + bytesToInt(a));
            //System.out.print(", " +bytes[i+3]);
        }
        System.out.println();


        //System.out.print(Arrays.asList(ByteBuffer.wrap(bytes).asIntBuffer().array()));
    }

}
