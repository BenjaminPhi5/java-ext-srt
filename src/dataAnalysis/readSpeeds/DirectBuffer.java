package dataAnalysis.readSpeeds;

import java.io.FileInputStream;
import java.io.IOException;

public class DirectBuffer {

    public  void run(String filename) {
        /*
        if (args.length != 1) {
            System.err.println("missing filename");
            System.exit(1);
        }
        */
        int negatives = 0;
        try {
            FileInputStream fis =
                    new FileInputStream(filename);
            //byte buf[] = new byte[2048];
            byte buf[] = new byte[1<<16];
            int[] ints;
            //int cnt = 0;
            int n;
            while ((n = fis.read(buf)) != -1) {
                ints = new int[n/4];
                for(int i = 0; i < n / 4; i++){
                    ints[i] = (((int) buf[4*i+3]) & 255)
                            |   ((((int) buf[4*i+2]) & 255) << 8)
                            |   ((((int) buf[4*i+1]) & 255) << 16)
                            |   ((((int) buf[4*i]) & 255) << 24);
                    if(ints[i] < 0){
                        negatives++;
                    }
                }
            }
            fis.close();
        }
        catch (IOException e) {
            System.err.println(e);
        }
    }
}

