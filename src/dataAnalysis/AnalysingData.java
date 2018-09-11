package dataAnalysis;

import dataAnalysis.ReadIn.*;
import dataAnalysis.readSpeeds.*;

import java.io.IOException;

public class AnalysingData {

    //This class is for messing about for reading in the data and looking at it etc.
    private String f1;
    private String f2;
    private String fx;

    // REMEMBER WHEN RUNNING THE CODE
    // YOU NEED TO LIMIT THE SIZE OF RAM ALLOWED SEE THE EXAMPLE THEY GIVE FOR THE PROGRAM PARAMETERS
    // VARY IT AND SEE HOW THIS AFFECTS YOUR PERFORMANCE.


    public AnalysingData(String f1, String f2){
        this.f1 = f1;
        this.f2 = f2;
        System.out.println("analysis");
    }

    public void run() throws IOException{

        for(int i = 1; i <= 17; i++){
            fx = "test-suite/test" + i + "a.dat";
            System.out.println("\nTEST " + i);
            //testReadSpeed();
            // I CONCLUDED byte array byte buffer was fastest, may need to check the method used,
            // i think the read method, maybe to specify a cap on the data i don't know.


            //memoryAvailable();
            //fileSizeEst(); //getting the file size takes no time at all. Noice.
            testIntegerValues();
            //testBucketValues();
            //testLinearBuckets();
            //readIn();
        }

    }

    public void fileSizeEst(){
        ReadFile readFile = new ReadFile();
        readFile.readF(fx);
    }

    public void testReadSpeed() throws IOException {
        // Tests read speeds of different reader inputs,
        // to just read the whole file and perform a comparison on each digit

        int bufferSize = 1<<16;
        long time = System.currentTimeMillis();

        System.out.println("Using a Byte buffer reader");
        ByteBufferInput byteBufferInput = new ByteBufferInput();
        byteBufferInput.run(fx, bufferSize);
        long timeBBR = System.currentTimeMillis() - time;
        System.out.println("-------------\n");
/*
        time = System.currentTimeMillis();
        System.out.println("Using a CUSTOM READER");
        CustomReader customReader = new CustomReader();
        customReader.run(fx);
        System.out.print("time: ");
        System.out.println(System.currentTimeMillis() - time);
        System.out.println("-------------\n\n");

    // it needs to actually check when its got to the end of the file and then stop.

*/

        System.out.println("Using a RAW BYTE ARRAY");
        time = System.currentTimeMillis();
        RawByteArrayRead rawByteArrayRead = new RawByteArrayRead();
        rawByteArrayRead.run(fx);
        long timeRBA = System.currentTimeMillis() - time;
        System.out.println("-------------\n");

        System.out.println("Using a Byte Array and Byte BUffer");
        time = System.currentTimeMillis();
        ByteArrayAndByteBuffer byteArrayAndByteBuffer = new ByteArrayAndByteBuffer();
        byteArrayAndByteBuffer.run(fx);
        long timeBABR = System.currentTimeMillis() - time;
        System.out.println("-------------\n");

        System.out.println("Using a DIRECT BUFFER");
        time = System.currentTimeMillis();
        DirectBuffer directBuffer = new DirectBuffer();
        directBuffer.run(fx);
        long timeDB = System.currentTimeMillis() - time;
        System.out.println("-------------\n");

        System.out.println("Using a standard file read");
        time = System.currentTimeMillis();
        StandardFileRead standardFileRead = new StandardFileRead();
        standardFileRead.run(fx);
        long timeSFR = System.currentTimeMillis() - time;
        System.out.println("-------------\n\n");

        System.out.println("time:\nByte buffer reader: " + timeBBR + "\nRaw byte array: " + timeRBA + "\nByte array, byte buffer: " + timeBABR + "\nDirect Buffer: " + timeDB +"\nStandard file read: " + timeSFR);
    }

    public void testIntegerValues() throws IOException{
        ByteArrayAndByteBuffer byteArrayAndByteBuffer = new ByteArrayAndByteBuffer();
        byteArrayAndByteBuffer.runAnalysis(fx);
    }

    public void memoryAvailable() {
        Runtime r = Runtime.getRuntime();
        long freeMem = r.freeMemory();
        long maxMem = r.maxMemory();
        long totalMem = r.totalMemory();

        long allocatedMem = totalMem - freeMem;
        long presFreeMem = maxMem - allocatedMem;
        System.out.println("Allocated Memory in JVM:                " + allocatedMem);
        System.out.println("present free memory:                    " + presFreeMem);
    }

    public void testBucketValues() throws IOException{
        ByteArrayAndByteBuffer byteArrayAndByteBuffer = new ByteArrayAndByteBuffer();
        byteArrayAndByteBuffer.runBuckets(fx);
    }

    public void testLinearBuckets() throws IOException{
        ByteArrayAndByteBuffer byteArrayAndByteBuffer = new ByteArrayAndByteBuffer();
        byteArrayAndByteBuffer.runEvenBuckets(fx);
    }

    public void readIn(){
        //ReadFile readFile = new ReadFile();
        //readFile.readF(fx);


        long time;
        time = System.currentTimeMillis();
        DirectBufferThreaded concurrentQueueTest = new DirectBufferThreaded(fx);
        concurrentQueueTest.run();
        System.out.println("\ntime: concurrent: " + (System.currentTimeMillis() - time));

        time = System.currentTimeMillis();
        DirectBufferReadIn directBufferReadIn = new DirectBufferReadIn();
        directBufferReadIn.run(fx);
        System.out.println("\ntime: standard: " + (System.currentTimeMillis() - time));
    }

}
