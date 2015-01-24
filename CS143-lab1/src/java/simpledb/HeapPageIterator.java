package simpledb;

import java.util.*;
import java.io.*;
import java.math.BigInteger;

public class HeapPageIterator<Tuple> implements Iterator<Tuple>{
    
    //member variables
    ArrayList<Tuple> m_tuple;
    Iterator<Tuple> m_it;
    
    //constructor
    public HeapPageIterator(ArrayList<Tuple> tuples) {
        this.m_tuple = tuples;
        this.m_it = tuple.iterator();
    }

    @Override
    public boolean hasNext() 
    {
        return m_it.hasNext();
    }

    @Override
    public Tuple next() 
    {
        return m_it.next();
    }

    @Override
    public void remove() 
    {
      throw new UnsupportedOperationException("remove");
    }
}
