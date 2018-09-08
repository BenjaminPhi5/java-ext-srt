package dataAnalysis.readSpeeds;

import java.io.*;

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
            int cnt = 0;
            int n;
            while ((n = fis.read(buf)) != -1) {
                for (int i = 0; i < n; i++) {
                    if(buf[i] < 0){
                        negatives++;
                    }
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

