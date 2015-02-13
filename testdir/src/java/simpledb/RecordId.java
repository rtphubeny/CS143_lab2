package simpledb;

import java.io.Serializable;

/**
 * A RecordId is a reference to a specific tuple on a specific page of a
 * specific table.
 */
public class RecordId implements Serializable {

    private static final long serialVersionUID = 1L;

	private PageId m_pid;
	private int m_tupleNo;
    /**
     * Creates a new RecordId referring to the specified PageId and tuple
     * number.
     * 
     * @param pid
     *            the pageid of the page on which the tuple resides
     * @param tupleno
     *            the tuple number within the page.
     */
    public RecordId(PageId pid, int tupleno) {
	this.m_pid = pid;
	this.m_tupleNo = tupleno;        
	// some code goes here
    }

    /**
     * @return the tuple number this RecordId references.
     */
    public int tupleno() {
        // some code goes here
        return m_tupleNo;
    }

    /**
     * @return the page id this RecordId references.
     */
    public PageId getPageId() {
        // some code goes here
        return m_pid;
    }

    /**
     * Two RecordId objects are considered equal if they represent the same
     * tuple.
     * 
     * @return True if this and o represent the same tuple
     */
    @Override
    public boolean equals(Object o) {
	if (o instanceof RecordId)
	{
		RecordId rObj = (RecordId)o;
		if (rObj.getPageId().equals(this.getPageId()) && rObj.tupleno() == this.tupleno())
			return true;
	}
	return false;       
	// some code goes here
    }

    /**
     * You should implement the hashCode() so that two equal RecordId instances
     * (with respect to equals()) have the same hashCode().
     * 
     * @return An int that is the same for equal RecordId objects.
     */
    @Override
    public int hashCode() {
        return Integer.parseInt(String.valueOf(this.getPageId().hashCode()) + String.valueOf(this.tupleno()));
	// some code goes here
    }

}