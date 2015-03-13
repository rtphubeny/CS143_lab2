package simpledb;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TableStats represents statistics (e.g., histograms) about base tables in a
 * query. 
 * 
 * This class is not needed in implementing lab1 and lab2.
 */
public class TableStats {

    private static final ConcurrentHashMap<String, TableStats> statsMap = new ConcurrentHashMap<String, TableStats>();

    static final int IOCOSTPERPAGE = 1000;

    public static TableStats getTableStats(String tablename) {
        return statsMap.get(tablename);
    }

    public static void setTableStats(String tablename, TableStats stats) {
        statsMap.put(tablename, stats);
    }
    
    public static void setStatsMap(HashMap<String,TableStats> s)
    {
        try {
            java.lang.reflect.Field statsMapF = TableStats.class.getDeclaredField("statsMap");
            statsMapF.setAccessible(true);
            statsMapF.set(null, s);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public static Map<String, TableStats> getStatsMap() {
        return statsMap;
    }

    public static void computeStatistics() {
        Iterator<Integer> tableIt = Database.getCatalog().tableIdIterator();

        System.out.println("Computing table stats.");
        while (tableIt.hasNext()) {
            int tableid = tableIt.next();
            TableStats s = new TableStats(tableid, IOCOSTPERPAGE);
            setTableStats(Database.getCatalog().getTableName(tableid), s);
        }
        System.out.println("Done.");
    }

    /**
     * Number of bins for the histogram. Feel free to increase this value over
     * 100, though our tests assume that you have at least 100 bins in your
     * histograms.
     */
    static final int NUM_HIST_BINS = 100;

    private int m_tid; //table id
    private int m_costPerPage;
    private int m_numTuples;
    private TupleDesc m_td; //tuple descriptor
    private HeapFile m_file;
    
    private HashMap<String, Object> m_histograms;
    private HashMap<String, Integer> m_mins;
    private HashMap<String, Integer> m_maxs;

    /**
     * Create a new TableStats object, that keeps track of statistics on each
     * column of a table
     * 
     * @param tableid
     *            The table over which to compute statistics
     * @param ioCostPerPage
     *            The cost per page of IO. This doesn't differentiate between
     *            sequential-scan IO and disk seeks.
     */
    public TableStats(int tableid, int ioCostPerPage) {
        // For this function, you'll have to get the
        // DbFile for the table in question,
        // then scan through its tuples and calculate
        // the values that you need.
        // You should try to do this reasonably efficiently, but you don't
        // necessarily have to (for example) do everything
        // in a single scan of the table.
        // some code goes here

        m_tid = tableid;
        m_costPerPage = ioCostPerPage;
        m_file = (HeapFile) Database.getCatalog().getDatabaseFile(tableid);
        m_td = m_file.getTupleDesc();
        m_numTuples = 0;

        m_mins = new HashMap<String, Integer>();
        m_maxs = new HashMap<String, Integer>();
        m_histograms = new HashMap<String, Object>();

        Transaction trans = new Transaction();
        TransactionId transId = trans.getId();
        DbFileIterator it = m_file.iterator(transId);

        try {
            it.open();
            while (it.hasNext())
            {
                Tuple tup = it.next();
                for (int i = 0; i < m_td.numFields(); i++)
                {
                    String fname = m_td.getFieldName(i);
                    Type ftype = m_td.getFieldType(i);

                    if (ftype.equals(Type.INT_TYPE))
                    {
                        int val = ((IntField) tup.getField(i)).getValue();
                        if (!m_mins.containsKey(fname) || val < m_mins.get(fname))
                            m_mins.put(fname, val);

                        if (!m_maxs.containsKey(fname) || val > m_maxs.get(fname))
                            m_maxs.put(fname, val);
                    }
                }
            }

            it.rewind();

            for (String key : m_mins.keySet()) {
                int val = m_mins.get(key);
                IntHistogram hist = new IntHistogram(NUM_HIST_BINS, m_mins.get(key), m_maxs.get(key));
                m_histograms.put(key, hist);
            }

            while (it.hasNext())
            {
                Tuple tup = it.next();
                m_numTuples++;

                for (int i = 0; i < m_td.numFields(); i++) 
                {
                    String fname = m_td.getFieldName(i);
                    Type ftype = m_td.getFieldType(i);

                    if (ftype.equals(Type.INT_TYPE)) 
                    { //INT_TYPE
                        int val = ((IntField) tup.getField(i)).getValue();
                        IntHistogram hist = (IntHistogram) m_histograms.get(fname);
                        hist.addValue(val);
                        m_histograms.put(fname, hist);
                    } 
                    else 
                    { //STRING_TYPE
                        String val = ((StringField) tup.getField(i)).getValue();
                        StringHistogram hist;
                        if (m_histograms.containsKey(fname)) 
                        {//histograms contains StringHistogram
                            hist = (StringHistogram) m_histograms.get(fname);
                            hist.addValue(val);
                        } 
                        else 
                        {
                            hist = new StringHistogram(NUM_HIST_BINS);
                            hist.addValue(val);
                        }

                        m_histograms.put(fname, hist);
                    }
                }
            }
        }
        catch (DbException e) {
            e.printStackTrace();
        }
        catch (TransactionAbortedException e) {
            e.printStackTrace();
        }

    }
    /**
     * Estimates the cost of sequentially scanning the file, given that the cost
     * to read a page is costPerPageIO. You can assume that there are no seeks
     * and that no pages are in the buffer pool.
     * 
     * Also, assume that your hard drive can only read entire pages at once, so
     * if the last page of the table only has one tuple on it, it's just as
     * expensive to read as a full page. (Most real hard drives can't
     * efficiently address regions smaller than a page at a time.)
     * 
     * @return The estimated cost of scanning the table.
     */
    public double estimateScanCost() {
        // some code goes here
        return m_file.numPages()*m_costPerPage;
    }

    /**
     * This method returns the number of tuples in the relation, given that a
     * predicate with selectivity selectivityFactor is applied.
     * 
     * @param selectivityFactor
     *            The selectivity of any predicates over the table
     * @return The estimated cardinality of the scan with the specified
     *         selectivityFactor
     */
    public int estimateTableCardinality(double selectivityFactor) {
        // some code goes here
        return (int) Math.ceil(m_numTuples*selectivityFactor);
    }

    /**
     * The average selectivity of the field under op.
     * @param field
     *        the index of the field
     * @param op
     *        the operator in the predicate
     * The semantic of the method is that, given the table, and then given a
     * tuple, of which we do not know the value of the field, return the
     * expected selectivity. You may estimate this value from the histograms.
     * */
    public double avgSelectivity(int field, Predicate.Op op) {
        // some code goes here
        return 1.0;
    }

    /**
     * Estimate the selectivity of predicate <tt>field op constant</tt> on the
     * table.
     * 
     * @param field
     *            The field over which the predicate ranges
     * @param op
     *            The logical operation in the predicate
     * @param constant
     *            The value against which the field is compared
     * @return The estimated selectivity (fraction of tuples that satisfy) the
     *         predicate
     */
    public double estimateSelectivity(int field, Predicate.Op op, Field constant) {
        // some code goes here
        if (m_td.getFieldType(field) == Type.INT_TYPE) 
        {
            int val = ((IntField) constant).getValue();
            IntHistogram hist = (IntHistogram) m_histograms.get(m_td.getFieldName(field));

            return hist.estimateSelectivity(op, val);
        } 
        else 
        {
            String val = ((StringField) constant).getValue();
            StringHistogram hist = (StringHistogram) m_histograms.get(m_td.getFieldName(field));

            return hist.estimateSelectivity(op, val);
        }

    }

    /**
     * return the total number of tuples in this table
     * */
    public int totalTuples() {
        // some code goes here
        return m_numTuples;
    }

}
