package dev;

import dev.buffers.QuickSort.DoubleBufferQuickSort;
import dev.buffers.QuickSort.DoubleQueueQuickSort;
import dev.buffers.TreeMap.*;
import dev.processors.HashTreeProcessor;

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

        for(int i = 1; i <= 17; i++){
            fin = "test-suite/test" + i + "a.dat";
            fout = "test-suite/test" + i + "b.dat";
            System.out.println("TEST: " + i);
            System.out.println(checkSum(fout)); System.out.println();


            //runLargeRange();
            //doubleBufferTest();

            //try { printFile("test-suite/test" + i + "a.dat"); } catch (IOException e) { e.printStackTrace(); }
            //System.out.println("---");
            //try { printFile("test-suite/test" + i + "b.dat"); } catch (IOException e) { e.printStackTrace(); }
        }

        //FilePrinter fp = new FilePrinter();
        //fp.run("test-suite/test" + 13 + "a.dat");
        //System.out.println();
        //fp.run("test-suite/test" + 13 + "b.dat");

    }

    private void runLargeRange(){
        long time;

        for(int i = 0; i < 5; i++ ) {

            time = System.currentTimeMillis();
            NoConcurrentTreeMap noConcurrentTreeMap = new NoConcurrentTreeMap(fin, fout);
            noConcurrentTreeMap.run();
            System.out.print("time NoConcurrentTreeMap: " + (System.currentTimeMillis() - time));
            System.out.println("\tThe checksum is: " + checkSum(fout));

            time = System.currentTimeMillis();
            WholeFileTreeMap wholeFileTreeMap = new WholeFileTreeMap(fin, fout);
            wholeFileTreeMap.run();
            System.out.print("time WholeFileTreeMap: " + (System.currentTimeMillis() - time));
            System.out.println("\tThe checksum is: " + checkSum(fout));

            time = System.currentTimeMillis();
            SingleBufferTreeMap singleBuffer = new SingleBufferTreeMap(fin, fout);
            singleBuffer.run();
            System.out.print("time SingleBufferTreeMap: " + (System.currentTimeMillis() - time));
            System.out.println("\tThe checksum is: " + checkSum(fout));


            System.out.println();
        }
    }

    private void doubleBufferTest(){
        for(int i = 0; i < 1; i++) {
/*
            long time = System.currentTimeMillis();
            DoubleQueueTreeMap doubleQueueTreeMap = new DoubleQueueTreeMap(fin, fout);
            doubleQueueTreeMap.run();
            System.out.print("time DoubleQueueTreeMap: " + (System.currentTimeMillis() - time));
            System.out.println("\tThe checksum is: " + checkSum(fout));
*/
/*
            long time = System.currentTimeMillis();
            DoubleQueueQuickSort doubleQueueQuickSort = new DoubleQueueQuickSort(fin, fout);
            doubleQueueQuickSort.run();
            System.out.print("time DoubleQuickSort: " + (System.currentTimeMillis() - time));
            System.out.println("\tThe checksum is: " + checkSum(fout));
            */

            long time = System.currentTimeMillis();
            DoubleBufferQuickSort doubleQueueQuickSort = new DoubleBufferQuickSort();
            try { doubleQueueQuickSort.run(fin, fout); } catch (IOException e) { e.printStackTrace(); }
            System.out.print("time DoubleQuickSort: " + (System.currentTimeMillis() - time));
            System.out.println("\tThe checksum is: " + checkSum(fout));
        }
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

    private void printFile(String f1) throws IOException {
        int n; int newInt;
        FileInputStream fis = new FileInputStream(f1);
        byte[] bufIn = new byte[1<<16];
        while ((n = fis.read(bufIn)) != -1) {
            //READ IN METHOD OF CHOICE
            for (int i = 0; i < n / 4; i++) {
                newInt = (((int) bufIn[4 * i + 3]) & 255)
                        | ((((int) bufIn[4 * i + 2]) & 255) << 8)
                        | ((((int) bufIn[4 * i + 1]) & 255) << 16)
                        | ((((int) bufIn[4 * i]) & 255) << 24);
                // END OF READ IN CHOICE

                System.out.print(", " + newInt);


            }
        }
        System.out.println();
        fis.close();
    }
}
