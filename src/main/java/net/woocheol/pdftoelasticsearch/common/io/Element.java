package net.woocheol.pdftoelasticsearch.common.io;

/**
 * Created by social on 16. 5. 23.
 */
public interface Element {
    public abstract void accept(Visitor visitor);
}
