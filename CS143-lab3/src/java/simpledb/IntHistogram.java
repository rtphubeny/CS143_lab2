package simpledb;
import simpledb.Predicate.Op;

/** A class to represent a fixed-width histogram over a single integer-based field.
 */
public class IntHistogram {

        private int m_min;
        private int m_max;
        private int m_numBuckets;
        private int[] m_histogram;
        private int m_bucketSize;
        private int m_numTuples;
    /**
     * Create a new IntHistogram.
     * 
     * This IntHistogram should maintain a histogram of integer values that it receives.
     * It should split the histogram into "buckets" buckets.
     * 
     * The values that are being histogrammed will be provided one-at-a-time through the "addValue()" function.
     * 
     * Your implementation should use space and have execution time that are both
     * constant with respect to the number of values being histogrammed.  For example, you shouldn't 
     * simply store every value that you see in a sorted list.
     * 
     * @param buckets The number of buckets to split the input value into.
     * @param min The minimum integer value that will ever be passed to this class for histogramming
     * @param max The maximum integer value that will ever be passed to this class for histogramming
     */
    public IntHistogram(int buckets, int min, int max) {
        this.m_min = min;
        this.m_max = max;
        this.m_numBuckets = buckets;
        m_histogram = new int[buckets];
        m_bucketSize = (int) Math.ceil( (double)( (max - min + 1)/buckets) );
        m_numTuples = 0;
        // some code goes here
    }

    private int bucketIndex(int v)
    {
        //WHAT IF BUCKET SIZE = 0?
        return ((v - m_min)/m_bucketSize);
    }

    private int bucketMax(int bucketIndex) 
    {
        return (m_min + (bucketIndex + 1) * m_bucketSize - 1);
    }

    private int bucketMin(int bucketIndex)
    {
        return (m_min + bucketIndex * m_bucketSize);
    }

    /**
     * Add a value to the set of values that you are keeping a histogram of.
     * @param v Value to add to the histogram
     */
    public void addValue(int v) {
    	for(int i = 0; i < m_numBuckets; i++) 
        {
            if((v < (m_min + (i+1) * m_bucketSize)) && (v >= (m_min + i * m_bucketSize))) 
            {
                m_histogram[i]++;
                m_numTuples++;
                return;
            }
        }
        // some code goes here
    }

    /**
     * Estimate the selectivity of a particular predicate and operand on this table.
     * 
     * For example, if "op" is "GREATER_THAN" and "v" is 5, 
     * return your estimate of the fraction of elements that are greater than 5.
     * 
     * @param op Operator
     * @param v Value
     * @return Predicted selectivity of this particular operator and value
     */
    public double estimateSelectivity(Predicate.Op op, int v) {
        int index = bucketIndex(v);
        double estTuples = 0.0;

        if (op == Op.EQUALS || op == Op.NOT_EQUALS)
        {
            for(int i = 0; i < m_numBuckets; i++) 
            {
                if((v < (m_min + (i+1) * m_bucketSize)) && (v >= (m_min + i * m_bucketSize))) 
                {
                    if (op == Op.EQUALS)
                        estTuples = (double) (m_histogram[i]/m_bucketSize);
                    else
                        estTuples = 1 - (double) (m_histogram[i]/m_bucketSize);
                    break;
                }
            }
        }

        if (op == Op.GREATER_THAN || op == Op.GREATER_THAN_OR_EQ)
        {
            if (v < m_min)
                estTuples = m_numTuples;
            else if (v > m_max)
                estTuples = 0.0;
            else {
                double fract;
                if (op == Op.GREATER_THAN)
                    fract = (bucketMax(index) - v) / (double) m_bucketSize;
                else
                    fract = (bucketMax(index) - v + 1) / (double) m_bucketSize;
                
                estTuples = m_histogram[index] * fract;
                
                for (int i = index + 1; i < m_histogram.length; i++)
                    estTuples += m_histogram[i];
            }
        }

        if (op == Op.LESS_THAN || op == Op.LESS_THAN_OR_EQ)
        {
            if (v < m_min)
                estTuples = 0.0;
            else if (v > m_max)
                estTuples = m_numTuples;
            else {
                double fract;
                if (op == Op.LESS_THAN)
                    fract = (v - bucketMin(index)) / (double) m_bucketSize;
                else
                    fract = (v - bucketMin(index) + 1) / (double) m_bucketSize;

                estTuples = m_histogram[index] * fract;
                
                for (int i = 0; i < index; i++)
                    estTuples += m_histogram[i];
            }
        }

    	// some code goes here
        return estTuples / m_numTuples;
    }
    
    /**
     * @return
     *     the average selectivity of this histogram.
     *     
     *     This is not an indispensable method to implement the basic
     *     join optimization. It may be needed if you want to
     *     implement a more efficient optimization
     * */
    public double avgSelectivity()
    {
        // some code goes here
        return 1.0;
    }
    
    /**
     * @return A string describing this histogram, for debugging purposes
     */
    public String toString() {
        // some code goes here
        return String.format("min=%d, max=%d, bucketSize=%d, bucketNum=%d, numberOfTuples=%d\n", m_min, m_max, m_bucketSize, m_histogram.length, m_numTuples);
    }
}
