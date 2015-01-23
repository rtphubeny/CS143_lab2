package simpledb;

import java.io.*;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples
 * in no particular order. Tuples are stored on pages, each of which is a fixed
 * size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage
 * constructor.
 * 
 * @see simpledb.HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {

	File m_file;
	TupleDesc m_td;
/*
	interface HeapFileIterator extends java.util.Iterator<Page>{}
	private class HeapFileIterator implements DbFileIterator{
		private int nextIndex = 0;
		public void open(){
		}
		public boolean hasNext(){
			return ;
		}
		public Tuple next(){
		}
		public void rewind(){
		}
		public void close(){
		}
	}
*/
    /**
     * Constructs a heap file backed by the specified file.
     * 
     * @param f
     *            the file that stores the on-disk backing store for this heap
     *            file.
     */
    public HeapFile(File f, TupleDesc td) {
	this.m_file = f;
	this.m_td = td;        
	// some code goes here
    }

    /**
     * Returns the File backing this HeapFile on disk.
     * 
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        // some code goes here
        return m_file;
    }

    /**
     * Returns an ID uniquely identifying this HeapFile. Implementation note:
     * you will need to generate this tableid somewhere ensure that each
     * HeapFile has a "unique id," and that you always return the same value for
     * a particular HeapFile. We suggest hashing the absolute file name of the
     * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
     * 
     * @return an ID uniquely identifying this HeapFile.
     */
    public int getId() {
	return m_file.getAbsoluteFile().hashCode();        
	// some code goes here
        //throw new UnsupportedOperationException("implement this");
    }

    /**
	
     * Returns the TupleDesc of the table stored in this DbFile.
     * 
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        return m_td;
	// some code goes here
        //throw new UnsupportedOperationException("implement this");
    }

    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        // some code goes here
	RandomAccessFile raf;
	try{
		raf = new RandomAccessFile(this.m_file, "r");
	} catch(FileNotFoundException e){
		throw new IllegalArgumentException("no file");
	}
		int off = pid.pageNumber();
		int len = BufferPool.getPageSize();
		byte [] b = new byte [len];
	try{
		raf.read(b, off, len);
	} catch(IOException e){
		throw new IllegalArgumentException("page does not exist");
	}
	try{
		return new HeapPage(new HeapPageId( pid.getTableId(), off), b);
	} catch(IOException e){
		throw new IllegalArgumentException("cannot make page");
	}

    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // some code goes here
        // not necessary for lab1
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        // some code goes here
        return (int)Math.ceil(m_file.length()/BufferPool.PAGE_SIZE);
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for lab1
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for lab1
    }

    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        // some code goes here
	return null;
    }
}

