package simpledb;
import java.util.*;

/**
 * Knows how to compute some aggregate over a set of IntFields.
 */
public class IntegerAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;

    private int m_gbField;
    private Type m_gbFieldType;
    private int m_aField;
    private Op m_op;

    private HashMap<Field, Integer> m_group;
    private HashMap<Field, Integer> m_count;
    private String m_fieldName = "";
    private String m_groupFieldName = "";

    /**
     * Aggregate constructor
     * 
     * @param gbfield
     *            the 0-based index of the group-by field in the tuple, or
     *            NO_GROUPING if there is no grouping
     * @param gbfieldtype
     *            the type of the group by field (e.g., Type.INT_TYPE), or null
     *            if there is no grouping
     * @param afield
     *            the 0-based index of the aggregate field in the tuple
     * @param what
     *            the aggregation operator
     */

    public IntegerAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
	m_gbField = gbfield;
	m_gbFieldType = gbfieldtype;
	m_aField = afield;
	m_op = what;

	m_group = new HashMap<Field, Integer>();
	m_count = new HashMap<Field, Integer>();
        // some code goes here
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the
     * constructor
     * 
     * @param tup
     *            the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
	Field key;

	int val;
	int curAVal; //current aggregate value
	int curCount; //current count value
	m_fieldName = tup.getTupleDesc().getFieldName(m_aField);
	
	if (m_gbField == Aggregator.NO_GROUPING)
		key = new IntField(Aggregator.NO_GROUPING);
	else {
		key = tup.getField(m_gbField);
		m_groupFieldName = tup.getTupleDesc().getFieldName(m_gbField);
	}
	
	val = ((IntField)tup.getField(m_aField)).getValue();

	if (!m_group.containsKey(key))
	{	//m_group does NOT yet contain key
		if (m_op == Op.COUNT || m_op == Op.SUM || m_op == Op.AVG)
			m_group.put(key, 0);
		else if (m_op == Op.MAX)
			m_group.put(key, Integer.MIN_VALUE);
		else if (m_op == Op.MIN)
			m_group.put(key, Integer.MAX_VALUE);

		m_count.put(key, 0);
	}
	curAVal = m_group.get(key);
	curCount = m_count.get(key);


	if ((m_op == Op.MAX && val > curAVal) || (m_op == Op.MIN && val < curAVal))
		curAVal = val;

	if (m_op == Op.COUNT)
		curAVal++;

	if (m_op == Op.SUM)
		curAVal += val;

	if (m_op == Op.AVG) {
		curCount++;
		m_count.put(key, curCount);
		curAVal += val;
	}

	m_group.put(key, curAVal);

	// some code goes here
    }

    /**
     * Create a DbIterator over group aggregate results.
     * 
     * @return a DbIterator whose tuples are the pair (groupVal, aggregateVal)
     *         if using group, or a single (aggregateVal) if no grouping. The
     *         aggregateVal is determined by the type of aggregate specified in
     *         the constructor.
     */
    //nearly the same as string aggregator
    public DbIterator iterator() {
	
	ArrayList<Tuple> tupleList = new ArrayList<Tuple>();	//list of all tuples
	TupleDesc descriptor = this.getTupleDesc();

	if (m_gbField == Aggregator.NO_GROUPING) {
		for (Field key : m_group.keySet()) {
			int val = m_group.get(key);
			if (m_op == Op.AVG)
				val /= m_count.get(key);
			Tuple tuple = new Tuple(descriptor);
			tuple.setField(0, new IntField(val));
			tupleList.add(tuple);
		}
	}
	else {
		for (Field key : m_group.keySet()) {
			int val = m_group.get(key);
			if (m_op == Op.AVG)
				val /= m_count.get(key);
			Tuple tuple = new Tuple(descriptor);
			tuple.setField(0, key);
			tuple.setField(1, new IntField(val));
			tupleList.add(tuple);
		}
	}
	
	return new TupleIterator(descriptor, tupleList);
     
	// some code goes here
        //throw new
       // UnsupportedOperationException("please implement me for lab2");
    }
	//helper function to get the tupleDesc, same as string aggregator
	public TupleDesc getTupleDesc() {
		Type[] fType;	//field type
		String[] fName;	//field name

		if (m_gbField == Aggregator.NO_GROUPING) {
			fType = new Type[1];
			fName = new String[1];

			fType[0] = Type.INT_TYPE;
			fName[0] = m_fieldName;
		}
		else {
			fType = new Type[2];
			fName = new String[2];

			fType[0] = m_gbFieldType;
			fType[1] = Type.INT_TYPE;
			
			fName[0] = m_groupFieldName;
			fName[1] = m_fieldName;
		}
		
		return new TupleDesc(fType, fName);
	}
}
