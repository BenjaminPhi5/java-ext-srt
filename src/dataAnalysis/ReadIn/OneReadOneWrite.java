package dataAnalysis.ReadIn;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class OneReadOneWrite {

    ReadWriteLock readWriteLock;
    byte[] buffer;

    private class Writer extends Thread {

        FileInputStream fis;

        Writer() {
            try {
                fis = new FileInputStream(filename);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        public void run(){
            try {
                byte buf[] = new byte[1<<16];
                int n;
                while ((n = fis.read(buf)) != -1) {
                    readWriteLock.writeLock().lock();
                    buffer = buf.clone();
                    readWriteLock.writeLock().unlock();
                }
                fis.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }

    }

    private class Reader extends Thread {

        byte[] buf;
        int local_max = 0, local_min = Integer.MAX_VALUE, local_total = 0;
        int x;

        public void run(){
            readWriteLock.readLock().lock();
            buf = buffer.clone();
            readWriteLock.readLock().unlock();
            local_total += buf.length/4;
            for(int i = 0; i <  buf.length/ 4; i++){
                x  = (((int) buf[4*i+3]) & 255)
                        |   ((((int) buf[4*i+2]) & 255) << 8)
                        |   ((((int) buf[4*i+1]) & 255) << 16)
                        |   ((((int) buf[4*i]) & 255) << 24);
                if(x < local_min)
                    local_min = x;
                if(x > local_max)
                    local_max = x;
            }
        }

    }

    public int smallest;
    public int biggest;
    public int total;
    String filename;
    Reader r;
    Writer w;

    public OneReadOneWrite(String filename) {
        readWriteLock = new ReentrantReadWriteLock();
        buffer = new byte[0];
        smallest = Integer.MAX_VALUE;
        biggest = 0;
        total = 0;
        this.filename = filename;
        r = new Reader();
        w = new Writer();
    }

    public void run(){

        w.start();
        r.start();

        try {
            w.join();
        } catch (InterruptedException e) {
        }

        try {
            total += r.local_total;
            if(r.local_max > biggest)
                biggest = r.local_max;
            if(r.local_min < smallest)
                smallest = r.local_min;

            r.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
