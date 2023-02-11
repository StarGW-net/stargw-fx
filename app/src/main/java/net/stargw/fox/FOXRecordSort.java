package net.stargw.fox;

import java.io.Serializable;
import java.util.Comparator;

public class FOXRecordSort implements Comparator<String>, Serializable {
    // (new Comparator<String>() {
    @Override
    public int compare(String o1, String o2) {
        return o2.toLowerCase().compareTo(o1.toLowerCase());

    }


}
