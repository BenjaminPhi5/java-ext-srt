package dataAnalysis;

import java.io.IOException;

public class AnalysingData {

    //This class is for messing about for reading in the data and looking at it etc.
    private String f1;
    private String f2;

    // REMEMBER WHEN RUNNING THE CODE
    // YOU NEED TO LIMIT THE SIZE OF RAM ALLOWED SEE THE EXAMPLE THEY GIVE FOR THE PROGRAM PARAMETERS
    // VARY IT AND SEE HOW THIS AFFECTS YOUR PERFORMANCE.


    public AnalysingData(String f1, String f2){
        this.f1 = f1;
        this.f2 = f2;
        System.out.println("analysis");
    }

    public void run() throws IOException{

        testReadSpeed();
    }


    public void testReadSpeed() throws IOException {
        // Tests read speeds of different reader inputs,
        // to just read the whole file and perform a comparison on each digit

        int bufferSize = 5000;
        long time = System.currentTimeMillis();

        System.out.println("Using a Byte buffer reader");
        ByteBufferInput byteBufferInput = new ByteBufferInput();
        byteBufferInput.run(f1, bufferSize);
        System.out.print("time: ");
        System.out.println(System.currentTimeMillis() - time);
        System.out.println("-------------\n\n");
    }

}
