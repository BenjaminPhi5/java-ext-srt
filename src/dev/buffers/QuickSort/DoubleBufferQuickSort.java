package dev.buffers.QuickSort;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;

public class DoubleBufferQuickSort {

    public static final int BUFFER_SIZE = 1<<16;

    public void run(String f1, String f2) throws IOException {

        ArrayList<Integer> arr;
        //int[] arr = new int[2000000];
        int index = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        RandomAccessFile f;
        RandomAccessFile w = new RandomAccessFile(f2, "rw");

        for (int i = 0; i < 2; i++) {
            f = null;
            arr = null;
            arr = new ArrayList<>();

            try {
                //System.out.print("run: "+i+"\ttest: "+ f1);
                 f = new RandomAccessFile(f1, "rw");

                int bytesRead = f.read(buffer);

                if(i ==0){
                    while (bytesRead != -1) {

                        int n = bytesRead;
                        int x;
                        for(int j = 0; j < n; j+= 4) {

                            if(buffer[j] < 0) {

                                x = (((int) buffer[j + 3]) & 255)
                                        | ((((int) buffer[j + 2]) & 255) << 8)
                                        | ((((int) buffer[j + 1]) & 255) << 16)
                                        | ((((int) buffer[j]) & 255) << 24);
                                arr.add(x);
                            }

                        }
                        /*

                        ByteBuffer wrapped = ByteBuffer.wrap(buffer);
                        for (int j = 0; j < bytesRead / 4; j++) {
                            if (i < 0) {
                                arr.add(wrapped.getInt());
                                //arr[index] = wrapped.getInt();
                                //arr.add(i);
                                //index++;
                            }
                        }
                        */
                        bytesRead = f.read(buffer);

                    }
                }else{
                    while (bytesRead != -1) {


                        int n = bytesRead;
                        int x;
                        for(int j = 0; j < n; j+= 4) {

                            if(buffer[j] < 0) {

                                x = (((int) buffer[j + 3]) & 255)
                                        | ((((int) buffer[j + 2]) & 255) << 8)
                                        | ((((int) buffer[j + 1]) & 255) << 16)
                                        | ((((int) buffer[j]) & 255) << 24);
                                arr.add(x);
                            }

                        }


                        /*
                        ByteBuffer wrapped = ByteBuffer.wrap(buffer);
                        for (int j = 0; j < bytesRead / 4; j++) {
                            if (i >= 0) {
                                arr.add(wrapped.getInt());
                                //arr.add(i);
                            }
                        }

                        */

                        bytesRead = f.read(buffer);


                    }
                }


                Collections.sort(arr);
                // write out the array to a buffer here!!!!
                // test for speed now and see if good. should use less memory.


            } catch (EOFException e) {
                //I dont think this catch needs to be in anymore wont happen due to while loop
                System.out.println("end of file error not handled");
                return;
            }
        }
    }

}
