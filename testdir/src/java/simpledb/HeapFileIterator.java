package simpledb;

import java.io.*;
import java.util.*;

public class HeapFileIterator implements DbFileIterator {
        
        //member variables
        private Iterator<Tuple> m_it;
        private TransactionId m_tid;
        private int m_pageNo;
        private HeapFile m_file;
         
        //constructor
        public HeapFileIterator(TransactionId tid, HeapFile f) 
        {
            this.m_tid = tid;
            this.m_file=f;     
        }
            
        @Override
        public void open() throws DbException, TransactionAbortedException 
        {
            m_pageNo = 0;
            m_it = getTuples(m_pageNo).iterator();
        }

        @Override
        public boolean hasNext() throws DbException, TransactionAbortedException 
        {
            if(m_it == null)
                return false;
            if(m_it.hasNext())
                return true;
            else if (m_pageNo < m_file.numPages()-1)
            {
                //more pages to iterate
                if(getTuples(m_pageNo + 1).size() != 0)
                    return true;
                else
                    return false;
            } 
            else
                return false;
        }

        @Override
        public Tuple next() throws DbException, TransactionAbortedException,
                NoSuchElementException 
        {
            if(m_it == null) //dont want null tuple
                throw new NoSuchElementException("tuple is null");
            
            if(m_it.hasNext()) //available tuples
                return m_it.next();

            else if(!m_it.hasNext() && m_pageNo < m_file.numPages()-1) 
            {   //tuples are on next page
                m_pageNo++;
                m_it = getTuples(m_pageNo).iterator();
                if (m_it.hasNext())
                    return m_it.next();
                else
                    throw new NoSuchElementException("No more Tuples");
            } 
            else
                throw new NoSuchElementException("No more Tuples");
        }

        @Override
        public void rewind() throws DbException, TransactionAbortedException 
        {
            close();
            open();
        }

        @Override
        public void close() 
        {
            m_it = null;
        }

        //get a list of tuples 
        private List<Tuple> getTuples(int pgNum) throws TransactionAbortedException, DbException
        {
            PageId pId = new HeapPageId(m_file.getId(), pgNum);
            Page p = Database.getBufferPool().getPage(m_tid, pId, Permissions.READ_ONLY);
                            
            List<Tuple> tuples = new ArrayList<Tuple>();
            //get tuples from file
            HeapPage hp = (HeapPage)p;
            Iterator<Tuple> it = hp.iterator();
            while(it.hasNext())
                tuples.add(it.next());
            return  tuples;
        }

    }
