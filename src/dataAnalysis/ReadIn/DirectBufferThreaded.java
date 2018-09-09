package dataAnalysis.ReadIn;

import com.sun.org.apache.regexp.internal.RE;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DirectBufferThreaded {

    byte[] buf1 = new byte[BUF_SIZE];
    byte[] buf2 = new byte[BUF_SIZE];
    int total = 0;
    int biggest = 0;
    int smallest = Integer.MAX_VALUE;
    static final int BUF_SIZE = 1<<16;
    static FileInputStream fis;
    BufferReader thread1 = new BufferReader(1);
    BufferReader thread2 = new BufferReader(2);

    private class BufferReader extends Thread{

        int index;
        int smallest = Integer.MAX_VALUE;
        int biggest = 0;
        int total = 0;

        BufferReader(int index){
            this.index = index;
        }

        public  void run() {
            try {
                int l_t = 0;
                int l_s = Integer.MAX_VALUE;
                int l_b = 0;
                int cnt = 0;
                int x;
                int n;
                byte buf[];

                if (index == 1)
                    buf = buf1;
                else
                    buf = buf2;

                while(true) {
                    synchronized (buf1) {
                        synchronized (buf2) {
                            n = fis.read(buf);
                        }
                        buf1.notifyAll();
                    }
                    if(n==-1)
                        break;

                    for(int i = 0; i < n / 4; i++){
                        x = (((int) buf[4*i+3]) & 255)
                                |   ((((int) buf[4*i+2]) & 255) << 8)
                                |   ((((int) buf[4*i+2]) & 255) << 16)
                                |   ((((int) buf[4*i+2]) & 255) << 24);

                        l_t ++;
                        if(x > l_b)
                            l_b = x;
                        if(x < l_s)
                            l_s = x;
                    }
                }
                total += l_t;
                if(l_b > biggest)
                    biggest = l_b;
                if(l_s < smallest)
                    smallest = l_s;

            }
            catch (IOException e) {
                System.err.println(e);
            }
        }

    }

    public DirectBufferThreaded(String filename){
        try {
            fis = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void run(){

        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
            total += thread1.total;
            total += thread2.total;
            if(thread1.biggest > biggest)
                biggest = thread1.biggest;
            if(thread2.biggest > biggest)
                biggest = thread2.biggest;
            if(thread1.smallest > smallest)
                smallest = thread1.smallest;
            if(thread2.smallest > smallest)
                smallest = thread2.smallest;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
