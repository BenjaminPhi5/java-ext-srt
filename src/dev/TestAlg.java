package dev;

import dev.buffers.SingleBufferTreeMap;

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

            System.out.println("The checksum is: "+checkSum(fout)+"\n");
        }

        //FilePrinter fp = new FilePrinter();
        //fp.run("test-suite/test" + 13 + "a.dat");
        //System.out.println();
        //fp.run("test-suite/test" + 13 + "b.dat");

    }

    private void runLargeRange(){
        long time;

        time= System.currentTimeMillis();
        SingleBufferTreeMap singleBuffer = new SingleBufferTreeMap(fin, fout);
        singleBuffer.run();
        System.out.println("time SingleBufferTreeMap: " + (System.currentTimeMillis()-time));

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
