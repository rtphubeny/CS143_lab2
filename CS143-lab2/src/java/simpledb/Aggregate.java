package simpledb;

import java.util.*;

/**
 * The Aggregation operator that computes an aggregate (e.g., sum, avg, max,
 * min). Note that we only support aggregates over a single column, grouped by a
 * single column.
 */
public class Aggregate extends Operator {

    private static final long serialVersionUID = 1L;

	private DbIterator m_child;
	private DbIterator m_aChild;	//aggregated child
	int m_aField;
	int m_gField;

	Aggregator.Op m_op;
	private Aggregator m_agg;
	private Iterator<Tuple> m_it;

    /**
     * Constructor.
     * 
     * Implementation hint: depending on the type of afield, you will want to
     * construct an {@link IntAggregator} or {@link StringAggregator} to help
     * you with your implementation of readNext().
     * 
     * 
     * @param child
     *            The DbIterator that is feeding us tuples.
     * @param afield
     *            The column over which we are computing an aggregate.
     * @param gfield
     *            The column over which we are grouping the result, or -1 if
     *            there is no grouping
     * @param aop
     *            The aggregation operator to use
     */
    public Aggregate(DbIterator child, int afield, int gfield, Aggregator.Op aop) {
	this.m_child = child;
	this.m_aField = afield;
	this.m_gField = gfield;
	m_op = aop;

	Type gbType = null;
	Type aType = m_child.getTupleDesc().getFieldType(m_aField);

	if (m_gField != Aggregator.NO_GROUPING)
		gbType = m_child.getTupleDesc().getFieldType(m_gField);


	if (aType == Type.INT_TYPE)
		m_agg = new IntegerAggregator(m_gField, gbType, m_aField, m_op);
	else
		m_agg = new StringAggregator(m_gField, gbType, m_aField, m_op);
	// some code goes here
    }

    /**
     * @return If this aggregate is accompanied by a groupby, return the groupby
     *         field index in the <b>INPUT</b> tuples. If not, return
     *         {@link simpledb.Aggregator#NO_GROUPING}
     * */
    public int groupField() {
	// some code goes here
	return m_gField;
    }

    /**
     * @return If this aggregate is accompanied by a group by, return the name
     *         of the groupby field in the <b>OUTPUT</b> tuples If not, return
     *         null;
     * */
    public String groupFieldName() {
	if (m_gField == Aggregator.NO_GROUPING)
		return null;
	else
		return m_child.getTupleDesc().getFieldName(m_gField);	
	// some code goes here
    }

    /**
     * @return the aggregate field
     * */
    public int aggregateField() {
	// some code goes here
	return m_aField;
    }

    /**
     * @return return the name of the aggregate field in the <b>OUTPUT</b>
     *         tuples
     * */
    public String aggregateFieldName() {
	// some code goes here
	return m_child.getTupleDesc().getFieldName(m_aField);
    }

    /**
     * @return return the aggregate operator
     * */
    public Aggregator.Op aggregateOp() {
	// some code goes here
	return m_op;
    }

    public static String nameOfAggregatorOp(Aggregator.Op aop) {
	return aop.toString();
    }

    public void open() throws NoSuchElementException, DbException,
	    TransactionAbortedException {
	m_child.open();
	while (m_child.hasNext())
		m_agg.mergeTupleIntoGroup(m_child.next());

	m_aChild = m_agg.iterator();
	m_aChild.open();

	super.open();
	// some code goes here
    }

    /**
     * Returns the next tuple. If there is a group by field, then the first
     * field is the field by which we are grouping, and the second field is the
     * result of computing the aggregate, If there is no group by field, then
     * the result tuple should contain one field representing the result of the
     * aggregate. Should return null if there are no more tuples.
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
	if (m_aChild.hasNext())
		return m_aChild.next();
	// some code goes here
	return null;
    }

    public void rewind() throws DbException, TransactionAbortedException {
	m_child.rewind();
	m_aChild.rewind();
	// some code goes here
    }

    /**
     * Returns the TupleDesc of this Aggregate. If there is no group by field,
     * this will have one field - the aggregate column. If there is a group by
     * field, the first field will be the group by field, and the second will be
     * the aggregate value column.
     * 
     * The name of an aggregate column should be informative. For example:
     * "aggName(aop) (child_td.getFieldName(afield))" where aop and afield are
     * given in the constructor, and child_td is the TupleDesc of the child
     * iterator.
     */
    public TupleDesc getTupleDesc() {
	Type[] fType;
	String[] fName;

	if (m_aField == Aggregator.NO_GROUPING) {
		fType = new Type[1];
		fName = new String[1];

		fType[0] = m_child.getTupleDesc().getFieldType(m_aField);
		fName[0] = m_op.toString() + "(" + m_child.getTupleDesc().getFieldName(m_aField) + ")";
	}
	else {
		fType = new Type[2];
		fName = new String[2];

		fType[0] = m_child.getTupleDesc().getFieldType(m_gField);
		fType[1] = m_child.getTupleDesc().getFieldType(m_aField);

		fName[0] = m_child.getTupleDesc().getFieldName(m_gField);
		fName[1] = m_op.toString() + "(" + m_child.getTupleDesc().getFieldName(m_aField) + ")";
	}
	// some code goes here
	return new TupleDesc(fType, fName);
    }

    public void close() {
	m_child.close();
	m_aChild.close();

	super.close();	
	// some code goes here
    }

    @Override
    public DbIterator[] getChildren() {
	// some code goes here
	return new DbIterator[] {
		this.m_child
	};
    }

    @Override
    public void setChildren(DbIterator[] children) {
	if (this.m_child != children[0])
		this.m_child = children[0];	
	// some code goes here
    }
    
}
