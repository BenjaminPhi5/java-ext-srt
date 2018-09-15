package dev.processors;

import java.util.SortedMap;
import java.util.TreeMap;

public class HashTreeProcessor extends Processor {

    Byte current;
    private TreeMap<Integer, Byte> values;

    public  HashTreeProcessor(){
        values = new TreeMap<>();
    }

    public HashTreeProcessor(boolean x){}

    public TreeMap<Integer, Byte> getValues(){
        return values;
    }

    //@Override
    public void add(int x) {
        //System.out.println("here: " + values);
        //byte currentValue = values.getOrDefault(x, (byte)0);
        //values.replace(x, currentValue, (byte)(currentValue+1));


        current = values.put(x, (byte)1);
        if(current != null){
            values.put(x, (byte)(current+1));
        }
        //System.out.println("here: " + values);
    }
}
