package simpledb;

/** Unique identifier for HeapPage objects. */
public class HeapPageId implements PageId {

	private int m_tabId;
	private int m_pageNo;
    /**
     * Constructor. Create a page id structure for a specific page of a
     * specific table.
     *
     * @param tableId The table that is being referenced
     * @param pgNo The page number in that table.
     */
    public HeapPageId(int tableId, int pgNo) {
	this.m_tabId = tableId;
	this.m_pageNo = pgNo;        
	// some code goes here
    }

    /** @return the table associated with this PageId */
    public int getTableId() {
        // some code goes here
        return m_tabId;
    }

    /**
     * @return the page number in the table getTableId() associated with
     *   this PageId
     */
    public int pageNumber() {
        // some code goes here
        return m_pageNo;
    }

    /**
     * @return a hash code for this page, represented by the concatenation of
     *   the table number and the page number (needed if a PageId is used as a
     *   key in a hash table in the BufferPool, for example.)
     * @see BufferPool
     */
    public int hashCode() {
	Long lcode= Long.parseLong(String.valueOf(m_tabId) + String.valueOf(m_pageNo));
	int code = lcode.hashCode();
	return code;        
	// some code goes here
    }

    /**
     * Compares one PageId to another.
     *
     * @param o The object to compare against (must be a PageId)
     * @return true if the objects are equal (e.g., page numbers and table
     *   ids are the same)
     */
    public boolean equals(Object o) {
        if (o instanceof HeapPageId)
	{
		HeapPageId hObj = (HeapPageId)o;
		if (hObj.getTableId() == this.getTableId() && hObj.pageNumber() == this.pageNumber())
			return true;
	}
	// some code goes here
        return false;
    }

    /**
     *  Return a representation of this object as an array of
     *  integers, for writing to disk.  Size of returned array must contain
     *  number of integers that corresponds to number of args to one of the
     *  constructors.
     */
    public int[] serialize() {
        int data[] = new int[2];

        data[0] = getTableId();
        data[1] = pageNumber();

        return data;
    }

}
