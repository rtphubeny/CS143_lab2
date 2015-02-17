package simpledb;

import java.io.IOException;

/**
 * The delete operator. Delete reads tuples from its child operator and removes
 * them from the table they belong to.
 */
public class Delete extends Operator {

    private static final long serialVersionUID = 1L;
    private TransactionId _tid;
    private DbIterator _child;

    /**
     * Constructor specifying the transaction that this delete belongs to as
     * well as the child to read from.
     * 
     * @param t
     *            The transaction this delete runs in
     * @param child
     *            The child operator from which to read tuples for deletion
     */
    public Delete(TransactionId t, DbIterator child) {
        // some code goes here
	this._tid = t;
	this._child = child;
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
        Type[] typeAr = new Type[] { Type.INT_TYPE };
        String[] fieldAr = new String[] { "Number of Deleted Tuples" };
        return new TupleDesc(typeAr, fieldAr);

    }

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
	this._child.open();
	super.open();
    }

    public void close() {
        // some code goes here
	this._child.close();
	super.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
	this._child.rewind();
    }

    /**
     * Deletes tuples as they are read from the child operator. Deletes are
     * processed via the buffer pool (which can be accessed via the
     * Database.getBufferPool() method.
     * 
     * @return A 1-field tuple containing the number of deleted records.
     * @see Database#getBufferPool
     * @see BufferPool#deleteTuple
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // some code goes here
	int nDeleted = 0;
	try{
		while (this._child.hasNext()){
			Tuple t = this._child.next();
			nDeleted++;
			try{
				Database.getBufferPool().deleteTuple(this._tid, t);
			}catch(IOException e){}
		}
		// create tuple for return
                Tuple return_t = new Tuple(this.getTupleDesc());
                IntField f = new IntField(nDeleted);
                return_t.setField(0, f);
                return return_t;
	}catch(TransactionAbortedException e){return null;}catch(DbException e){return null;}
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
