package simpledb;
import java.util.*;

/**
 * Knows how to compute some aggregate over a set of StringFields.
 */
public class StringAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;

    private int m_gbField;
    private Type m_gbFieldType;
    private int m_aField;
    private Op m_op;
    private HashMap<Field, Integer> m_group;
    private String m_fieldName="";
    private String m_groupFieldName="";

    /**
     * Aggregate constructor
     * @param gbfield the 0-based index of the group-by field in the tuple, or NO_GROUPING if there is no grouping
     * @param gbfieldtype the type of the group by field (e.g., Type.INT_TYPE), or null if there is no grouping
     * @param afield the 0-based index of the aggregate field in the tuple
     * @param what aggregation operator to use -- only supports COUNT
     * @throws IllegalArgumentException if what != COUNT
     */

    public StringAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        if (what != Op.COUNT)   //we only want COUNT
            throw new IllegalArgumentException("op only supports COUNT");

        m_gbField = gbfield;
        m_gbFieldType = gbfieldtype;
        m_aField = afield;
        m_op = what;

        m_group = new HashMap<Field, Integer>();
        // some code goes here
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the constructor
     * @param tup the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        Field key; //group by field for tup
        int curAVal;// current aaggregate value for key
        m_fieldName = tup.getTupleDesc().getFieldName(m_aField);
        
        //determining key
        if (m_gbField == Aggregator.NO_GROUPING) {
            key = new IntField(Aggregator.NO_GROUPING);
        } else {
            key = tup.getField(m_gbField);
            m_groupFieldName = tup.getTupleDesc().getFieldName(m_gbField);

        }   
    
        //does not contain key yet
        if (!m_group.containsKey(key))
            m_group.put(key, 0);

        curAVal = m_group.get(key);

        curAVal++;
        m_group.put(key, curAVal);
        // some code goes here
    }

    /**
     * Create a DbIterator over group aggregate results.
     *
     * @return a DbIterator whose tuples are the pair (groupVal,
     *   aggregateVal) if using group, or a single (aggregateVal) if no
     *   grouping. The aggregateVal is determined by the type of
     *   aggregate specified in the constructor.
     */
    //nearly the same as integer aggregator
    public DbIterator iterator() {
        return new DbIterator() {

            TupleDesc resultTd;
            Iterator<Map.Entry<Field, Integer>> it;
            boolean isOpen;

            @Override
            public void open() throws DbException, TransactionAbortedException {
                resultTd = isGroupAggregator() ?
                        new TupleDesc(new Type[] { m_gbFieldType, Type.INT_TYPE }) :
                        new TupleDesc(new Type[] { Type.INT_TYPE });

                it = m_groupResult.entrySet().iterator();
                isOpen = true;
            }

            @Override
            public boolean hasNext() throws DbException, TransactionAbortedException {
                if (!isOpen)
                    return false;
                return it.hasNext();
            }

            @Override
            public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException {
                if (!hasNext())
                    throw new NoSuchElementException("no more tuples");

                Map.Entry<Field, Integer> entry = it.next();
                if (m_gbField != Aggregator.NO_GROUPING)
                    return makeResultTuple(entry.getKey(), entry.getValue());
                else
                    return makeResultTuple(entry.getValue());
            }

            @Override
            public void rewind() throws DbException, TransactionAbortedException {
                close();
                open();
            }

            @Override
            public TupleDesc getTupleDesc() {
                return resultTd;
            }

            @Override
            public void close() {
                isOpen = false;
            }

            // make result tuple for grouping aggregator
            private Tuple makeResultTuple(Field groupField, int aggValue) {
                Tuple result = new Tuple(resultTd);
                result.setField(0, groupField);
                result.setField(1, new IntField(aggValue));
                return result;
            }

            // make result tuple for no grouping aggregator
            private Tuple makeResultTuple(int aggValue) {
                Tuple result = new Tuple(resultTd);
                result.setField(0, new IntField(aggValue));
                return result;
            }
        };

        /*ArrayList<Tuple> tupleList = new ArrayList<Tuple>(); //list of tuples
        TupleDesc descriptor = this.getTupleDesc();

        if (m_gbField == Aggregator.NO_GROUPING) {
            for (Field key : m_group.keySet()) {
                int val = m_group.get(key);
                
                Tuple tuple = new Tuple(descriptor);
                tuple.setField(0, new IntField(val));
                tupleList.add(tuple);
            }
        }
        else {
            for (Field key : m_group.keySet()) {
                int val = m_group.get(key);
                
                Tuple tuple = new Tuple(descriptor);
                tuple.setField(0, key);
                tuple.setField(1, new IntField(val));
                tupleList.add(tuple);
            }
        }
        
        return new TupleIterator(descriptor, tupleList);*/
        // some code goes here
        //throw new UnsupportedOperationException("please implement me for lab2");
    }

    //helper function to get the tupleDesc, same as integer aggregator
    public TupleDesc getTupleDesc() {
        Type[] fType;   //field type
        String[] fName; //field name

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
