package simpledb;

import java.util.*;

/**
 * SeqScan is an implementation of a sequential scan access method that reads
 * each tuple of a table in no particular order (e.g., as they are laid out on
 * disk).
 */
public class SeqScan implements DbIterator {

    private static final long serialVersionUID = 1L;

    private int _tableID;
    private String _tableAlias;
    private TransactionId _transactionID;
    private DbFileIterator it;

    /**
     * Creates a sequential scan over the specified table as a part of the
     * specified transaction.
     * 
     * @param tid
     *            The transaction this scan is running as a part of.
     * @param tableid
     *            the table to scan.
     * @param tableAlias
     *            the alias of this table (needed by the parser); the returned
     *            tupleDesc should have fields with name tableAlias.fieldName
     *            (note: this class is not responsible for handling a case where
     *            tableAlias or fieldName are null. It shouldn't crash if they
     *            are, but the resulting name can be null.fieldName,
     *            tableAlias.null, or null.null).
     */
    public SeqScan(TransactionId tid, int tableid, String tableAlias) {
        // some code goes here
	this._tableID = tableid;
	this._tableAlias = tableAlias;
	this._transactionID = tid;
    }

    /**
     * @return
     *       return the table name of the table the operator scans. This should
     *       be the actual name of the table in the catalog of the database
     * */
    public String getTableName() {
        // some code goes here
	return Database.getCatalog().getTableName(this._tableID);
    }
    
    /**
     * @return Return the alias of the table this operator scans. 
     * */
    public String getAlias()
    {
        // some code goes here
        return this._tableAlias;
    }

    /**
     * Reset the tableid, and tableAlias of this operator.
     * @param tableid
     *            the table to scan.
     * @param tableAlias
     *            the alias of this table (needed by the parser); the returned
     *            tupleDesc should have fields with name tableAlias.fieldName
     *            (note: this class is not responsible for handling a case where
     *            tableAlias or fieldName are null. It shouldn't crash if they
     *            are, but the resulting name can be null.fieldName,
     *            tableAlias.null, or null.null).
     */
    public void reset(int tableid, String tableAlias) {
        // some code goes here
	this._tableID = tableid;
	this._tableAlias = tableAlias;
    }

    public SeqScan(TransactionId tid, int tableid) {
        this(tid, tableid, Database.getCatalog().getTableName(tableid));
    }

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
	this.it = Database.getCatalog().getDatabaseFile(this._tableID).iterator(this._transactionID);
	it.open();
    }

    /**
     * Returns the TupleDesc with field names from the underlying HeapFile,
     * prefixed with the tableAlias string from the constructor. This prefix
     * becomes useful when joining tables containing a field(s) with the same
     * name.
     * 
     * @return the TupleDesc with field names from the underlying HeapFile,
     *         prefixed with the tableAlias string from the constructor.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        TupleDesc underlying_td = Database.getCatalog().getTupleDesc(this._tableID);
	Type[] t = new Type[underlying_td.numFields()];
	String[] s = new String[underlying_td.numFields()];

	for (int i = 0; i < underlying_td.numFields(); i++){
		t[i] = underlying_td.getFieldType(i);
		s[i] = this._tableAlias + underlying_td.getFieldName(i);
	}
	return new TupleDesc(t, s);
    }

    public boolean hasNext() throws TransactionAbortedException, DbException {
        // some code goes here
	if (it == null)
		return false;
	if (it.hasNext())
		return true;
	return false;
    }

    public Tuple next() throws NoSuchElementException,
            TransactionAbortedException, DbException {
        // some code goes here
	if (it == null)
		throw new NoSuchElementException("iterator is null");
	if (it.hasNext())
		return it.next();
	throw new NoSuchElementException("no more tuples");
    }

    public void close() {
        // some code goes here
	it.close();
	it = null;
    }

    public void rewind() throws DbException, NoSuchElementException,
            TransactionAbortedException {
        // some code goes here
	close();
	open();
    }
}
