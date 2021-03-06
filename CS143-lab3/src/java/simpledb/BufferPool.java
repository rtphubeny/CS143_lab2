package simpledb;

import java.io.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;

/**
 * BufferPool manages the reading and writing of pages into memory from
 * disk. Access methods call into it to retrieve pages, and it fetches
 * pages from the appropriate location.
 * <p>
 * The BufferPool is also responsible for locking;  when a transaction fetches
 * a page, BufferPool checks that the transaction has the appropriate
 * locks to read/write the page.
 * 
 * @Threadsafe, all fields are final
 */
public class BufferPool {
    /** Bytes per page, including header. */
    public static final int PAGE_SIZE = 4096;

    private static int pageSize = PAGE_SIZE;
    
    /** Default number of pages passed to the constructor. This is used by
    other classes. BufferPool should use the numPages argument to the
    constructor instead. */
    public static final int DEFAULT_PAGES = 50;

    public class pageElement{
	//value of tid with lock or 0 if none
	public int lock;
	public Page page;
	public Permissions perm;
	public PageId pe_pid;
	public pageElement(int l, Page p, Permissions permissions, PageId pid){
		this.lock = l;
		this.page = p;
		this.perm = permissions;
		this.pe_pid = pid;
	}
    }
    private final int max_num_pages;
    private final ConcurrentHashMap<Integer, pageElement> Pages = new ConcurrentHashMap<Integer, pageElement>();

    /**
     * Creates a BufferPool that caches up to numPages pages.
     *
     * @param numPages maximum number of pages in this buffer pool.
     */
    public BufferPool(int numPages) {
        // some code goes here
	max_num_pages = numPages;
    }
    
    public static int getPageSize() {
      return pageSize;
    }
    
    // THIS FUNCTION SHOULD ONLY BE USED FOR TESTING!!
    public static void setPageSize(int pageSize) {
    	BufferPool.pageSize = pageSize;
    }

    /**
     * Retrieve the specified page with the associated permissions.
     * Will acquire a lock and may block if that lock is held by another
     * transaction.
     * <p>
     * The retrieved page should be looked up in the buffer pool.  If it
     * is present, it should be returned.  If it is not present, it should
     * be added to the buffer pool and returned.  If there is insufficient
     * space in the buffer pool, an page should be evicted and the new page
     * should be added in its place.
     *
     * @param tid the ID of the transaction requesting the page
     * @param pid the ID of the requested page
     * @param perm the requested permissions on the page
     */
    public  Page getPage(TransactionId tid, PageId pid, Permissions perm)
        throws TransactionAbortedException, DbException {
        // some code goes here
	// if in buffer pool
	if (this.Pages.containsKey(pid.hashCode())){
		pageElement _pageElement = this.Pages.get(pid.hashCode());
		// if locked by another transaction
	//	if (_pageElement.lock > 0 && _pageElement.lock != tid.hashCode())
	//		throw new TransactionAbortedException();
	//	else
			return _pageElement.page;
	}

	// if not in buffer pool
	// get page and create page element
	Page _page = Database.getCatalog().getDatabaseFile(pid.getTableId()).readPage(pid);
	if (_page == null)
		throw new DbException("page does not exist");
	pageElement _pageElement = new pageElement(tid.hashCode(), _page, perm, pid);
	
	// if buffer pool full
	if (Pages.size() >= this.max_num_pages)
		evictPage();

	this.Pages.put(pid.hashCode(), _pageElement);
	return _page;
    }

    /**
     * Releases the lock on a page.
     * Calling this is very risky, and may result in wrong behavior. Think hard
     * about who needs to call this and why, and why they can run the risk of
     * calling it.
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param pid the ID of the page to unlock
     */
    public  void releasePage(TransactionId tid, PageId pid) {
        // some code goes here
        // not necessary for lab1|lab2
	/*
	if (this.Pages.containsKey(pid.hashCode())){
		if (this.Pages.get(pid.hashCode()).lock == tid);
			this.Pages.get(pid.hashCode()).lock = -1;
	}*/
    }

    /**
     * Release all locks associated with a given transaction.
     *
     * @param tid the ID of the transaction requesting the unlock
     */
    public void transactionComplete(TransactionId tid) throws IOException {
        // some code goes here
        // not necessary for lab1|lab2
    }

    /** Return true if the specified transaction has a lock on the specified page */
    public boolean holdsLock(TransactionId tid, PageId p) {
        // some code goes here
        // not necessary for lab1|lab2
        return false;
    }

    /**
     * Commit or abort a given transaction; release all locks associated to
     * the transaction.
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param commit a flag indicating whether we should commit or abort
     */
    public void transactionComplete(TransactionId tid, boolean commit)
        throws IOException {
        // some code goes here
        // not necessary for lab1|lab2
    }

    /**
     * Add a tuple to the specified table on behalf of transaction tid.  Will
     * acquire a write lock on the page the tuple is added to and any other 
     * pages that are updated (Lock acquisition is not needed for lab2). 
     * May block if the lock(s) cannot be acquired.
     * 
     * Marks any pages that were dirtied by the operation as dirty by calling
     * their markDirty bit, and updates cached versions of any pages that have 
     * been dirtied so that future requests see up-to-date pages. 
     *
     * @param tid the transaction adding the tuple
     * @param tableId the table to add the tuple to
     * @param t the tuple to add
     */
    public void insertTuple(TransactionId tid, int tableId, Tuple t)
        throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        // not necessary for lab1
	Database.getCatalog().getDatabaseFile(tableId).insertTuple(tid, t);
    }

    /**
     * Remove the specified tuple from the buffer pool.
     * Will acquire a write lock on the page the tuple is removed from and any
     * other pages that are updated. May block if the lock(s) cannot be acquired.
     *
     * Marks any pages that were dirtied by the operation as dirty by calling
     * their markDirty bit, and updates cached versions of any pages that have 
     * been dirtied so that future requests see up-to-date pages. 
     *
     * @param tid the transaction deleting the tuple.
     * @param t the tuple to delete
     */
    public  void deleteTuple(TransactionId tid, Tuple t)
        throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        // not necessary for lab1
	int tableId = t.getRecordId().getPageId().getTableId();
	Database.getCatalog().getDatabaseFile(tableId).deleteTuple(tid, t);
    }

    /**
     * Flush all dirty pages to disk.
     * NB: Be careful using this routine -- it writes dirty data to disk so will
     *     break simpledb if running in NO STEAL mode.
     */
    public synchronized void flushAllPages() throws IOException {
        // some code goes here
        // not necessary for lab1
	Iterator i = Pages.values().iterator();
	while (i.hasNext()){
		pageElement pe = (pageElement) i.next();
		flushPage(pe.pe_pid);
	}
    }

    /** Remove the specific page id from the buffer pool.
        Needed by the recovery manager to ensure that the
        buffer pool doesn't keep a rolled back page in its
        cache.
    */
    public synchronized void discardPage(PageId pid) {
        // some code goes here
        // only necessary for lab5
    }

    /**
     * Flushes a certain page to disk
     * @param pid an ID indicating the page to flush
     */
    private synchronized  void flushPage(PageId pid) throws IOException {
        // some code goes here
        // not necessary for lab1
	//write dirty page to disk and mark it as not dirty while leaving it in buffer pool
	pageElement pe = Pages.get(pid.hashCode());
	Page page = pe.page;

	TransactionId tid = page.isDirty();
	if (tid != null){
		Database.getCatalog().getDatabaseFile(pid.getTableId()).writePage(page);
		page.markDirty(false, tid);
	}
    }

    /** Write all pages of the specified transaction to disk.
     */
    public synchronized  void flushPages(TransactionId tid) throws IOException {
        // some code goes here
        // not necessary for lab1|lab2
    }

    /**
     * Discards a page from the buffer pool.
     * Flushes the page to disk to ensure dirty pages are updated on disk.
     */
    private synchronized  void evictPage() throws DbException {
        // some code goes here
        // not necessary for lab1
	if (Pages.size() < 1)
		throw new DbException("nothing to evict");
	Iterator i = Pages.values().iterator();
	while (i.hasNext()){
		pageElement pe = (pageElement) i.next();
		if (pe.page.isDirty() != null){
			try{
				flushPage(pe.pe_pid);
			} catch (IOException e) {}
		}
		Pages.remove(pe.pe_pid.hashCode());
		break;
	}
    }

}
