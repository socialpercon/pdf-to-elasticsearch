package net.woocheol.pdftoelasticsearch.common.io;

import java.util.Iterator;

/**
 * Created by social on 16. 5. 23.
 */
public abstract class Entry implements Element{
    public abstract String getName();
    public abstract int getSize();

    public Entry add(Entry entry) throws FileTreatmentException {
        throw new FileTreatmentException();
    }

    public Iterator iterator() throws FileTreatmentException {
        throw new FileTreatmentException();
    }

    public String toString() {
        return getName() + " (" + getSize() + ")";
    }
}
