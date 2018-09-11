package dev;

import dev.buffers.TreeMap.NoConcurrentTreeMap;
import dev.buffers.TreeMap.SingleBufferTreeMap;
import dev.buffers.TreeMap.WholeFileTreeMap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TestAlg {

    private String fin;
    private String fout;

    public void run(){

        for(int i = 13; i <= 16; i++){
            fin = "test-suite/test" + i + "a.dat";
            fout = "test-suite/test" + i + "b.dat";
            System.out.println("TEST: " + i);


            runLargeRange();
        }

        //FilePrinter fp = new FilePrinter();
        //fp.run("test-suite/test" + 13 + "a.dat");
        //System.out.println();
        //fp.run("test-suite/test" + 13 + "b.dat");

    }

    private void runLargeRange(){
        long time;

        time= System.currentTimeMillis();
        NoConcurrentTreeMap noConcurrentTreeMap = new NoConcurrentTreeMap(fin, fout);
        noConcurrentTreeMap.run();
        System.out.print("time NoConcurrentTreeMap: " + (System.currentTimeMillis()-time));
        System.out.println("\tThe checksum is: "+checkSum(fout));

        time= System.currentTimeMillis();
        WholeFileTreeMap wholeFileTreeMap = new WholeFileTreeMap(fin, fout);
        wholeFileTreeMap.run();
        System.out.print("time WholeFileTreeMap: " + (System.currentTimeMillis()-time));
        System.out.println("\tThe checksum is: "+checkSum(fout));

        time= System.currentTimeMillis();
        SingleBufferTreeMap singleBuffer = new SingleBufferTreeMap(fin, fout);
        singleBuffer.run();
        System.out.print("time SingleBufferTreeMap: " + (System.currentTimeMillis()-time));
        System.out.println("\tThe checksum is: "+checkSum(fout));


        System.out.println();
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

    private static String byteToHex(byte b) {
        String r = Integer.toHexString(b);
        if (r.length() == 8) {
            return r.substring(6);
        }
        return r;
    }
}
