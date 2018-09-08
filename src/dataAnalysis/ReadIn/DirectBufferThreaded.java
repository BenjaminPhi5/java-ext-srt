package dataAnalysis.ReadIn;

import com.sun.org.apache.regexp.internal.RE;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class DirectBufferThreaded {

    int gSmallest = Integer.MAX_VALUE;
    int gBiggest = 0;
    int gTotal = 0;
    Boolean lockBuff;
    Boolean lockData;
    FileInputStream fis;

    public class BufferProcessor extends Thread {

        int total;
        int smallest;
        int biggest;
        public void run() {
            try {
                byte buf[] = new byte[1 << 16];
                int x;
                int n;
                synchronized (lockBuff) {
                    n = fis.read(buf);
                }
                while (n != -1) {

                    for (int i = 0; i < n / 4; i++) {
                        x = (((int) buf[4 * i + 3]) & 255)
                                | ((((int) buf[4 * i + 2]) & 255) << 8)
                                | ((((int) buf[4 * i + 2]) & 255) << 16)
                                | ((((int) buf[4 * i + 2]) & 255) << 24);
                        total++;
                        if (x > biggest)
                            biggest = x;
                        if (x < smallest)
                            smallest = x;
                    }

                    synchronized (lockData){
                        gTotal += total;
                        if (biggest > gBiggest)
                            gBiggest = biggest;
                        if (smallest < gSmallest)
                            gSmallest = smallest;
                    }

                    synchronized (lockBuff) {
                        n = fis.read(buf);
                    }
                }
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    public DirectBufferThreaded(String filename) {
        lockBuff = true;
        lockData = true;
        try {
            fis = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        ArrayList<BufferProcessor> processors = new ArrayList<>();
        processors.add(new BufferProcessor());
        processors.add(new BufferProcessor());

        for(BufferProcessor p : processors){
            p.start();
        }

        for(BufferProcessor p : processors){
            try {
                p.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("smallest: " + gSmallest + "\t");
        //System.out.print("biggest: " + gBiggest + "\t");
        //System.out.print("total: " + gTotal);
    }

}
