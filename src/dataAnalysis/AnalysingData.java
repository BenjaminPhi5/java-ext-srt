package dataAnalysis;

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
         testReadSpeed();
        }

    }


    public void testReadSpeed() throws IOException {
        // Tests read speeds of different reader inputs,
        // to just read the whole file and perform a comparison on each digit

        int bufferSize = 1<<16;
        long time = System.currentTimeMillis();

        System.out.println("Using a Byte buffer reader");
        ByteBufferInput byteBufferInput = new ByteBufferInput();
        byteBufferInput.run(fx, bufferSize);
        System.out.print("time: ");
        System.out.println(System.currentTimeMillis() - time);
        System.out.println("-------------\n\n");
/*
        System.out.println("Using a CUSTOM READER");
        CustomReader customReader = new CustomReader();
        customReader.run(fx);
        System.out.print("time: ");
        System.out.println(System.currentTimeMillis() - time);
        System.out.println("-------------\n\n");

    // it needs to actually check when its got to the end of the file and then stop.

*/

        System.out.println("Using a standard file read");
        StandardFileRead standardFileRead = new StandardFileRead();
        standardFileRead.run(fx);
        System.out.print("\ntime: ");
        System.out.println(System.currentTimeMillis() - time);
        System.out.println("-------------\n\n");
    }

}
