package simpledb;
import java.io.IOException;

/**
 * Inserts tuples read from the child operator into the tableid specified in the
 * constructor
 */
public class Insert extends Operator {

    private static final long serialVersionUID = 1L;
    private TransactionId _tid;
    private DbIterator _child;
    private int _tableid;
    private boolean _fetchNextCalled = false;

    /**
     * Constructor.
     * 
     * @param t
     *            The transaction running the insert.
     * @param child
     *            The child operator from which to read tuples to be inserted.
     * @param tableid
     *            The table in which to insert tuples.
     * @throws DbException
     *             if TupleDesc of child differs from table into which we are to
     *             insert.
     */
    public Insert(TransactionId t,DbIterator child, int tableid)
            throws DbException {
        // some code goes here
	this._tid = t;
	this._child = child;
	this._tableid = tableid;
	if (!(child.getTupleDesc().equals(Database.getCatalog().getTupleDesc(tableid))))
		throw new DbException("tupledesc of child != tupledesc of table");
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
	Type[] typeAr = new Type[] { Type.INT_TYPE };
	String[] fieldAr = new String[] { "Number of Inserted Tuples" };
	return new TupleDesc(typeAr, fieldAr);
    }

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
	this._child.open();
	super.open();
    }

    public void close() {
        // some code goes here
	super.close();
	this._child.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
	this._child.rewind();
    }

    /**
     * Inserts tuples read from child into the tableid specified by the
     * constructor. It returns a one field tuple containing the number of
     * inserted records. Inserts should be passed through BufferPool. An
     * instances of BufferPool is available via Database.getBufferPool(). Note
     * that insert DOES NOT need check to see if a particular tuple is a
     * duplicate before inserting it.
     * 
     * @return A 1-field tuple containing the number of inserted records, or
     *         null if called more than once.
     * @see Database#getBufferPool
     * @see BufferPool#insertTuple
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // some code goes here
	if (this._fetchNextCalled)
		return null;
	int nInserted = 0;
	// insert tuple
	while (this._child.hasNext()){
		nInserted ++;
		Tuple t = this._child.next();
		try{
			Database.getBufferPool().insertTuple(this._tid, this._tableid, t);
		} catch(IOException e){}
	}
    	this._fetchNextCalled = true;

	// create tuple for return
	Tuple return_t = new Tuple(this.getTupleDesc());
	IntField f = new IntField(nInserted);
	return_t.setField(0, f);
	return return_t;
    }

    @Override
    public DbIterator[] getChildren() {
        // some code goes here
	return new DbIterator[] { this._child };
    }

    @Override
    public void setChildren(DbIterator[] children) {
        // some code goes here
	if (this._child != children[0])
		this._child = children[0];
    }
}
