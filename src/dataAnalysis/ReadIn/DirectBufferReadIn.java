package dataAnalysis.ReadIn;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class DirectBufferReadIn {

    public  void run(String filename) {
        int smallest = Integer.MAX_VALUE;
        int biggest = 0;
        int total = 0;
        try {
            FileInputStream fis =
                    new FileInputStream(filename);
            //byte buf[] = new byte[2048];
            byte buf[] = new byte[1<<16];
            int cnt = 0;
            int x;
            int n;
            //long time = System.nanoTime();
            while ((n = fis.read(buf)) != -1) {
                //System.out.println("time to read:    " + (System.nanoTime() - time));
                //time = System.nanoTime();
                for(int i = 0; i < n / 4; i++){
                    x = (((int) buf[4*i+3]) & 255)
                            |   ((((int) buf[4*i+2]) & 255) << 8)
                            |   ((((int) buf[4*i+2]) & 255) << 16)
                            |   ((((int) buf[4*i+2]) & 255) << 24);

                    total ++;
                    if(x > biggest)
                        biggest = x;
                    if(x < smallest)
                        smallest = x;
                }
                //System.out.println("time to process: " + (System.nanoTime() - time));
                //time = System.nanoTime();

            }
            fis.close();
            //System.out.println("smallest: " + smallest +"\t");
            //System.out.print("biggest: " + biggest+"\t");
            //System.out.print("total: " + total);
        }
        catch (IOException e) {
            System.err.println(e);
        }
    }

}
