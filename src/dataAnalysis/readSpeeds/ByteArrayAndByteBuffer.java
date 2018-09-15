package dataAnalysis.readSpeeds;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class ByteArrayAndByteBuffer {

    private static final int BUFFER_SIZE = 1<<16;
    private static HashMap<Integer, Integer> maxes;
    private static HashMap<Integer, Integer> mins;

    public void run(String f1) throws IOException {

        try {
            RandomAccessFile f = new RandomAccessFile(f1, "rw");
            int negatives = 0;

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = f.read(buffer);

            while(bytesRead != -1){
                if(bytesRead > BUFFER_SIZE){
                    System.out.println("boi you got an error here, you need to cap the amount it reads in okay");
                }

                ByteBuffer wrapped = ByteBuffer.wrap(buffer);
                for(int i = 0; i < bytesRead/4; i++){
                    if(wrapped.getInt() < 0){
                        negatives++;
                    }
                }

                bytesRead = f.read(buffer);

            }

        } catch (EOFException e){
            //I dont think this catch needs to be in anymore wont happen due to while loop
            System.out.println("end of file error not handled");
            return;
        }
    }

    public void runAnalysis(String f1) throws IOException {

        try {
            //RandomAccessFile f = new RandomAccessFile(f1, "rw");
            FileInputStream fis =
                    new FileInputStream(f1);
            HashMap<Integer, Integer> repetitions = new HashMap<>();
            int negatives = 0;
            int lowest = Integer.MAX_VALUE;
            int smallest = Integer.MAX_VALUE;
            int highest = Integer.MIN_VALUE;
            int current;
            int total = 0;

            byte[] buffer = new byte[BUFFER_SIZE];
            //int bytesRead = f.read(buffer);
            int bytesRead = fis.read(buffer);

            while(bytesRead != -1){
                if(bytesRead > BUFFER_SIZE){
                    System.out.println("boi you got an error here, you need to cap the amount it reads in okay");
                }

                ByteBuffer wrapped = ByteBuffer.wrap(buffer);
                for(int i = 0; i < bytesRead/4; i++){
                    current = wrapped.getInt();
                    total++;
                    if(current < lowest){
                        lowest = current;
                    }
                    if(current > highest){
                        highest = current;
                    }

                    repetitions.put(current, repetitions.getOrDefault(current, 0) + 1);

                    if(current < 0){
                        negatives++;
                        current *= -1;
                    }
                    if(current < smallest){
                        smallest = current;
                    }
                }

                //bytesRead = f.read(buffer);
                bytesRead = fis.read(buffer);

            }

            HashMap<Integer, Integer> values = calcMinMaxReps(repetitions);

            System.out.println("min reps is: " + values.get(1) + "\tvalue: " + values.get(0));
            System.out.println("max reps is: " + values.get(3) + "\tvalue: " + values.get(1));
            System.out.println("unique values: " + values.get(4) + "\treps: " + (total - values.get(4)));

            System.out.println("total: " + total + "\tnegatives: " + negatives);
            System.out.println("lowest: " + lowest + "\thighest: " + highest +"\tsmallest:"+smallest);

        } catch (EOFException e){
            //I dont think this catch needs to be in anymore wont happen due to while loop
            System.out.println("end of file error not handled");
            return;
        }
    }

    private HashMap<Integer, Integer> calcMinMaxReps(HashMap<Integer, Integer> repetitions){
        int valueMin = 0;
        int repsMin = Integer.MAX_VALUE;
        int valueMax = 0;
        int repsMax = 0;
        int uniqueVals = repetitions.keySet().size();

        for(Integer i : repetitions.keySet()) {
            int reps = repetitions.get(i);

            if (reps < repsMin) {
                repsMin = reps;
                valueMin = i;
            }

            else if (reps > repsMax) {
                repsMax = reps;
                valueMax = i;
            }

        }

        HashMap<Integer, Integer> values = new HashMap<>();
        values.put(0, valueMin);
        values.put(1, repsMin);
        values.put(2, valueMax);
        values.put(3, repsMax);
        values.put(4, uniqueVals);

        return values;

    }

    public void runBuckets(String f1) throws IOException {
        maxes = new HashMap<>();
        mins = new HashMap<>();
        try {
            RandomAccessFile f = new RandomAccessFile(f1, "rw");
            HashMap<Integer, Integer> buckets = new HashMap<>();
            buckets.put(0, 0); // 0 - 9.
            buckets.put(1, 0); // 10 - 99.
            buckets.put(2, 0); // 100 - 999.
            buckets.put(3, 0); // 1000 - 9999.
            buckets.put(4, 0); // 10000 - 99999.
            buckets.put(5, 0); // 100000 - 999999.
            buckets.put(6, 0); // 1000000 - 9999999.
            buckets.put(7, 0); // 10000000 - 99999999.
            buckets.put(8, 0); // 100000000 - 999999999.
            buckets.put(9, 0); // 1000000000 - MAX.

            buckets.put(12, 0); // 0 - 9. //NEGATIVES BELOW
            buckets.put(13, 0); // 10 - 99.
            buckets.put(14, 0); // 100 - 999.
            buckets.put(15, 0); // 1000 - 9999.
            buckets.put(16, 0); // 10000 - 99999.
            buckets.put(17, 0); // 100000 - 999999.
            buckets.put(18, 0); // 1000000 - 9999999.
            buckets.put(19, 0); // 10000000 - 99999999.
            buckets.put(20, 0); // 100000000 - 999999999.
            buckets.put(21, 0); // 1000000000 - MAX.



            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = f.read(buffer);

            while(bytesRead != -1){
                if(bytesRead > BUFFER_SIZE){
                    System.out.println("boi you got an error here, you need to cap the amount it reads in okay");
                }

                ByteBuffer wrapped = ByteBuffer.wrap(buffer);
                for(int i = 0; i < bytesRead/4; i++){
                    int x = wrapped.getInt();
                    int neg = 0;
                    if(x < 0){
                        neg = 12;
                        x *= -1;
                    }
                    bucketValues(buckets, neg, x);
                }

                bytesRead = f.read(buffer);

            }

            System.out.printf("Buckets (powers of 10) POS:\n |0-1|:%d\n  |1-2|:%d\n  |2-3|:%d\n  |3-4|:%d\n  |4-5|:%d\n  |5-6|:%d\n  |6-7|:%d\n  |7-8|:%d\n  |8-9|:%d\n |9-MAX|:%d\n",buckets.get(0),buckets.get(1),buckets.get(2),buckets.get(3),buckets.get(4),buckets.get(5),buckets.get(6),buckets.get(7),buckets.get(8),buckets.get(9));
            System.out.printf("Buckets (powers of 10) NEG:\n |0-1|:%d\n  |1-2|:%d\n  |2-3|:%d\n  |3-4|:%d\n  |4-5|:%d\n  |5-6|:%d\n  |6-7|:%d\n  |7-8|:%d\n  |8-9|:%d\n |9-MAX|:%d\n",buckets.get(12),buckets.get(13),buckets.get(14),buckets.get(15),buckets.get(16),buckets.get(17),buckets.get(18),buckets.get(19),buckets.get(20),buckets.get(21));
            System.out.println(maxes);
            System.out.println(mins);

        } catch (EOFException e){
            //I dont think this catch needs to be in anymore wont happen due to while loop
            System.out.println("end of file error not handled");
            return;
        }
    }

    public void runEvenBuckets(String f1){
        int p1=0, p2=0, p3=0, p4=0, p5=0, p6=0;
        int lowest = Integer.MIN_VALUE; int biggest = Integer.MAX_VALUE;
        int size = (Integer.MIN_VALUE/3)*-1; int sizeP = Integer.MAX_VALUE/3;

        // < | | 0|0 | | >
        try{
            RandomAccessFile f = new RandomAccessFile(f1, "rw");

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = f.read(buffer);

            while(bytesRead != -1){
                if(bytesRead > BUFFER_SIZE){
                    System.out.println("boi you got an error here, you need to cap the amount it reads in okay");
                }

                ByteBuffer wrapped = ByteBuffer.wrap(buffer);
                for(int i = 0; i < bytesRead/4; i++){
                    int x = wrapped.getInt();
                    if(lowest < x  && x <= (lowest+size))
                        p1++;
                    else if((lowest+size) < x  && x <= (lowest+size*2))
                        p2++;
                    else if((lowest+size*2) < x  && x <= 0)
                        p3++;
                    else if(0 < x  && x <= sizeP)
                        p4++;
                    else if(sizeP < x && x <= sizeP*2)
                        p5++;
                    else if(sizeP*2 < x && x <= biggest)
                        p6++;
                    //System.out.print(", " + x);
                }

                bytesRead = f.read(buffer);

            }

        } catch (IOException e){
            e.printStackTrace();
        }
        System.out.println();
        System.out.println("p1\tp2\tp3\tp4\tp5\tp6");
        System.out.println(p1+"\t"+p2+"\t"+p3+"\t"+p4+"\t"+p5+"\t"+p6);
        System.out.println("total: " + (p1+p2+p3+p4+p5+p6));

    }

    private void bucketValues(HashMap<Integer, Integer> buckets, int neg, int x){
        if(x < 9){
            buckets.put(neg, buckets.get(neg)+1);
            bucketRanges(0, x, neg);
        } else if(x < 99) {
            buckets.put(neg + 1, buckets.get(neg+1)+1);
            bucketRanges(1, x, neg);
        } else if(x < 999) {
            buckets.put(neg + 2, buckets.get(neg+2)+1);
            bucketRanges(2, x, neg);
        } else if(x < 9999) {
            buckets.put(neg + 3, buckets.get(neg+3)+1);
            bucketRanges(3, x, neg);
        } else if(x < 99999) {
            buckets.put(neg + 4, buckets.get(neg+4)+1);
            bucketRanges(4, x, neg);
        } else if(x < 999999) {
            buckets.put(neg + 5, buckets.get(neg+5)+1);
            bucketRanges(5, x, neg);
        } else if(x < 9999999) {
            buckets.put(neg + 6, buckets.get(neg+6)+1);
            bucketRanges(6, x, neg);
        } else if(x < 99999999) {
            buckets.put(neg + 7, buckets.get(neg+7)+1);
            bucketRanges(7, x, neg);
        } else if(x < 999999999) {
            buckets.put(neg + 8, buckets.get(neg+8)+1);
            bucketRanges(8, x, neg);
        } else if(x < Integer.MAX_VALUE) {
            buckets.put(neg + 9, buckets.get(neg + 9) + 1);
            bucketRanges(9, x, neg);
        }
    }

    public void bucketRanges(int bucket, int value, int neg){
        if (value > maxes.getOrDefault(bucket+neg, (neg > 0) ? Integer.MIN_VALUE : 0))
            maxes.put(bucket+neg, value);
        else if (value < maxes.getOrDefault(bucket+neg, (neg > 0) ? 0 : Integer.MAX_VALUE))
            mins.put(bucket+neg, value);

    }

    public void getSmallestRange(String f1) throws IOException {

        try {
            RandomAccessFile f = new RandomAccessFile(f1, "rw");
            int range = Integer.MAX_VALUE;

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = f.read(buffer);

            while(bytesRead != -1){
                if(bytesRead > BUFFER_SIZE){
                    System.out.println("boi you got an error here, you need to cap the amount it reads in okay");
                }

                ByteBuffer wrapped = ByteBuffer.wrap(buffer);
                int prev = wrapped.getInt(); int x;
                for(int i = 0; i < bytesRead/4 -1; i++){
                    x = wrapped.getInt();
                    if(x - prev < range && x!=prev){
                        range = x-prev;
                    }
                    prev = x;
                }

                bytesRead = f.read(buffer);

            }
            System.out.println("smallest range: " + range);

        } catch (EOFException e){
            //I dont think this catch needs to be in anymore wont happen due to while loop
            System.out.println("end of file error not handled");
            return;
        }
    }


}
