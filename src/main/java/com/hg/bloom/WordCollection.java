package com.hg.bloom;

/**
 * User: rexsheng
 * Date: Mar 31, 2008
 */
public interface WordCollection {
    boolean contains(String word);

    int getN();

    boolean add(int i);

    boolean add(String i);
}
