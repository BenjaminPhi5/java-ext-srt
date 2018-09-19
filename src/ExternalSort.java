import dataAnalysis.AnalysingData;
import dev.TestAlg;
import dev.buffers.experimenting.CountingSortShorts;
import dev.buffers.experimenting.HashMapQuickSort;
import dev.buffers.experimenting.testSort;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ExternalSort {

    public static void sort(String f1, String f2) throws FileNotFoundException, IOException {

        RandomAccessFile fis = new RandomAccessFile(f1, "r");
        long l = fis.length();

        if(l<2)
            return;
        else if(l < 100){
            dev.buffers.experimenting.SingleReadSort.run(f1, f2);
        } else {
            // read in first 10 values, see what theyre like. depending, get counting sort
            int[] analysis = range(fis);

            if(analysis[0] < 1000){
                if(analysis[1] > 1000 || analysis[2] < 0){
                    HashMapQuickSort.run(f1, f2);
                } else {
                    CountingSortShorts.run(f1, f2);
                }
            } else{
                // if size less than 6 mb, read in whole file once, else do 6 way split.
                if(l <= 6000000) {
                    dev.buffers.experimenting.AdaptiveSort.run(f1, f2, false, l);
                }
                else {
                    dev.buffers.experimenting.AdaptiveSort.run(f1, f2, true, l);
                }
            }
        }


    }

    private static int[] range(RandomAccessFile fis){
        byte[] buf = new byte[1<<16];
        int bytesRead = 0;
        try { bytesRead = fis.read(buf) / 4; } catch (IOException e) { e.printStackTrace(); }
        int pos; int x; int max = Integer.MIN_VALUE; int min = Integer.MAX_VALUE;
        for (int i = 0; i < bytesRead; i++) {
            pos = 4 * i;

            x = (((int)buf[pos]) & 255) << 24
                    | ((((int) buf[pos + 1]) & 255) << 16)
                    | ((((int) buf[pos + 2]) & 255) << 8)
                    | ((((int) buf[pos + 3]) & 255));

            if(x < min)
                min = x;
            if(x > max)
                max = x;
        }
        min = min/1000 * 1000;
        System.out.println("rounded down min: "+ min);
        max = ((max + 99) / 100 ) * 100;
        System.out.println("rounded up max" + max);
        int range = max - min;
        if(range < 0){
            range*= -1;
        }
        return new int[]{range, max, min};
    }


    //below is all their stuff for the checksum

    private static String byteToHex(byte b) {
        String r = Integer.toHexString(b);
        if (r.length() == 8) {
            return r.substring(6);
        }
        return r;
    }

    public static String checkSum(String f) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            DigestInputStream ds = new DigestInputStream(
                    new FileInputStream(f), md);
            byte[] b = new byte[512];
            while (ds.read(b) != -1)
                ;

            String computed = "";
            for(byte v : md.digest())
                computed += byteToHex(v);

            return computed;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "<error computing checksum>";
    }

    public static void main(String[] args) throws Exception {
        String f1 = args[0];
        String f2 = args[1];

        //AnalysingData analysingData = new AnalysingData(f1, f2);
        //analysingData.run();

        long time; time = System.currentTimeMillis();
        for(int i = 1; i <= 17; i++){
            System.out.println("test: " + i);
            sort("test-suite/test" + i + "a.dat", "test-suite/test" + i + "b.dat");
        }
        System.out.println("time: "+ (System.currentTimeMillis() - time));

        TestAlg testAlg = new TestAlg();
        testAlg.run();

        //sort(f1, f2);

    }
}