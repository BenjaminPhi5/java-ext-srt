package dataAnalysis.ReadIn;

import java.io.FileInputStream;
import java.io.IOException;

public class DirectBufferReadIn {

    public  void run(String filename) {
        int min = Integer.MAX_VALUE;
        int max = 0;
        int total = 0;
        try {
            FileInputStream fis =
                    new FileInputStream(filename);
            //byte buf[] = new byte[2048];
            byte buf[] = new byte[1<<16];
            int cnt = 0;
            int[] ints;
            int n;
            while ((n = fis.read(buf)) != -1) {
                ints = new int[n/4];
                for(int i = 0; i < n / 4; i++){
                    ints[i] = (((int) buf[4*i+3]) & 255)
                            |   ((((int) buf[4*i+2]) & 255) << 8)
                            |   ((((int) buf[4*i+2]) & 255) << 16)
                            |   ((((int) buf[4*i+2]) & 255) << 24);

                }
            }
            fis.close();
            System.out.println(cnt);
        }
        catch (IOException e) {
            System.err.println(e);
        }
    }

}
