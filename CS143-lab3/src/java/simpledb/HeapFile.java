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

	private File m_file;
	private TupleDesc m_td;
    private BufferPool m_bufferPool;
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
    this.m_bufferPool = Database.getBufferPool();      
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
	try{
	       RandomAccessFile raf = new RandomAccessFile(m_file, "r");
            int offset = pid.pageNumber()*BufferPool.PAGE_SIZE;
            byte[] arr = new byte[BufferPool.PAGE_SIZE];

		    if (raf.skipBytes(offset) != offset)  
			    throw new IllegalArgumentException();
		    
            raf.seek(offset);
            raf.read(arr, 0, BufferPool.PAGE_SIZE);  //read page
            raf.close();  
            return new HeapPage((HeapPageId)pid, arr);         
        }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            throw new IllegalArgumentException();
    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // some code goes here
        try {
            PageId pid = page.getId();  //get page id

            RandomAccessFile raf = new RandomAccessFile(m_file,"rw"); //create new heap file
            int offset = pid.pageNumber()*BufferPool.PAGE_SIZE;

            if (raf.skipBytes(offset) != offset) {
                throw new IllegalArgumentException();
            }

            raf.seek(offset);
            raf.write(page.getPageData()); //write data to correct place in page
            raf.close();          
        }
        catch (IOException e){
            e.printStackTrace();
        }
        // not necessary for lab1
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        // some code goes here
        return (int)Math.ceil(m_file.length()/BufferPool.getPageSize());
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // some code goes here
                HeapPage p = null;
                boolean isFound = false;

                for (int pageNo = 0; pageNo < numPages(); pageNo++) {
                    p = (HeapPage) m_bufferPool.getPage(tid, new HeapPageId(getId(), pageNo), Permissions.READ_WRITE);
                    if (p.getNumEmptySlots() > 0) {
                        isFound = true;
                        break;
                    }
                }

                
                if (isFound) {    //if found, mark dirty
                    p.insertTuple(t);
                    p.markDirty(true, tid);
                }
                else {  //no page found, allocate new page and append to disk
                    p = new HeapPage(new HeapPageId(getId(), numPages()), HeapPage.createEmptyPageData());
                    p.insertTuple(t);
                    this.writePage(p);
                }

                ArrayList<Page> updatedPages = new ArrayList<Page>();
                updatedPages.add(p);
                return updatedPages;
        // not necessary for lab1
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        // some code goes here
	if (t.getRecordId() != null && t.getRecordId().getPageId().getTableId() == this.getId()){
		RecordId rId = t.getRecordId();
        	PageId pId = rId.getPageId();
        	Page p = m_bufferPool.getPage(tid, pId, Permissions.READ_WRITE);
        	HeapPage hp = (HeapPage)p;
        	hp.deleteTuple(t);
		hp.markDirty(true, tid);
	
		ArrayList<Page> updatedPages = new ArrayList<Page>();
		updatedPages.add(hp);
        
        	return updatedPages;
	}
	throw new DbException("problem deleting tuple");
        // not necessary for lab1
    }

    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        // some code goes here
	return new HeapFileIterator(tid, this);
    }
}

