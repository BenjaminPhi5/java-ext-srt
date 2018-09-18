package dev.buffers.experimenting;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ThreadedDPQsort {

    static int[] arr;
    private static final int THRESH = 1000;
    Sorter s1; Sorter s2; Sorter s3;

    public void dpQsort(int a[], int start, int end) {

        arr = a;
        int p;
        int q;
        int temp;

        int len = end - start;
        if (len < 27) { // insertion sort for tiny array
            for (int i = start + 1; i <= end; i++) {
                for (int j = i; j > start && arr[j] < arr[j - 1]; j--) {
                    temp = arr[j];
                    arr[j] = arr[j - 1];
                    arr[j - 1] = temp;
                }
            }
            return;
        }

        // else double pivot quick sort
        if (arr[start] > arr[end]) {
            temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;
        }

        p = arr[start];
        q = arr[end];

        int l = start + 1;
        int g = end - 1;

        for (int k = l; k <= g; k++) {

            // if element is less than left pointer
            if (arr[k] < p) {
                temp = arr[k];
                arr[k] = arr[l];
                arr[l] = temp;
                l++;// pointer to less than left pointer moves up by one
            }

            // if element between left point and right pointer dont care
            // as already in right place

            // if element >= the right pivot.
            else if (arr[k] > q) {

                // may be able to move where the right pivot section starts first
                while (k < g && arr[g] > q) {
                    g--;
                }

                // at this point, know arr[k] is greater than rp, so stick it behind where g points
                temp = arr[k];
                arr[k] = arr[g];
                arr[g] = temp;
                g--;
                //subsequently move the >= rp sectin down by one

                // check if new element at arr[k] the old g isnt now in wrong section (its currently in the between
                // lp and rp section, but may infact be less than the rp
                if (arr[k] < p) {
                    temp = arr[k];
                    arr[k] = arr[l];
                    arr[l] = temp;
                    l++;
                }
            }
        }
        // put pivots into positions
        temp = arr[l - 1];
        arr[l - 1] = arr[start];
        arr[start] = temp;
        temp = arr[g + 1];
        arr[g + 1] = arr[end];
        arr[end] = temp;

        //recursively call the quick sort now
        //use three threads if range is greater than 1000;
        if(len >= THRESH){
            s1 = new Sorter(start, l-2);
            s2 = new Sorter(l, g);
            s3 = new Sorter(g+2, end);
            s1.start();s2.start();s3.start();

            try {
                s1.join(); s2.join(); s3.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {

            dpQsort(arr, start, l - 2);
            //System.out.println("after: " + j + ", " + g);
            dpQsort(arr, l, g);
            dpQsort(arr, g + 2, end);
        }

    }


    private class Sorter extends Thread{

        int s; int e;

        Sorter(int s, int e){
            this.s = s;
            this.e = e;
        }

        public void run() {
            sort(s, e);
        }

        public void sort(int start, int end){

            int p; int q; int temp;

            int len = end-start;
            if (len < 27) { // insertion sort for tiny array
                for (int i = start + 1; i <= end; i++) {
                    for (int j = i; j > start && arr[j] < arr[j - 1]; j--) {
                        temp = arr[j];arr[j] = arr[j-1];arr[j-1] = temp;
                    }
                }
                return;
            }

            // else double pivot quick sort
            if(arr[start] > arr[end]){
                temp = arr[start];arr[start] = arr[end];arr[end] = temp;
            }

            p= arr[start]; q = arr[end];

            int l = start+1;
            int g = end-1;

            for(int k = l; k<= g; k++){

                // if element is less than left pointer
                if(arr[k] < p){
                    temp = arr[k];arr[k] = arr[l];arr[l] = temp;
                    l++;// pointer to less than left pointer moves up by one
                }

                // if element between left point and right pointer dont care
                // as already in right place

                // if element >= the right pivot.
                else if(arr[k] > q){

                    // may be able to move where the right pivot section starts first
                    while(k<g && arr[g] > q) {
                        g--;
                    }

                    // at this point, know arr[k] is greater than rp, so stick it behind where g points
                    temp = arr[k];arr[k] = arr[g];arr[g] = temp;
                    g--;
                    //subsequently move the >= rp sectin down by one

                    // check if new element at arr[k] the old g isnt now in wrong section (its currently in the between
                    // lp and rp section, but may infact be less than the rp
                    if(arr[k] < p){
                        temp = arr[k];arr[k] = arr[l];arr[l] = temp;
                        l++;
                    }
                }
            }
            // put pivots into positions
            temp = arr[l-1];arr[l-1] = arr[start];arr[start] = temp;
            temp = arr[g+1];arr[g+1] = arr[end];arr[end] = temp;

            //recursively call the quick sort now
            //System.out.print("before: " + j + ", " + g);

            sort(start, l-2);
            //System.out.println("after: " + j + ", " + g);
            sort(l, g);
            sort(g+2, end);

        }


    }

}

