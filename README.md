Group Members:
Derek Foster    504146063
Rebecca Hubeny	104133091

Slip Time:
(i) This project is late by 1 day.
(ii) Both partners now have 3 remaining slip days.

LAB 2 REPORT

To implement aggregates, we had to do implement the different classes for IntegerAggregator and StringAggregator. IntegerAggregator was more difficult, as initially we tried to implement only just one HashMap for both "group by" and "count" operations, but after adding a second HashMap to split them up the design became a lot easier. By using the two HashMaps, we easily went through each case (SUM, AVG, MAX, MIN, COUNT) to merge the groups.
For both Integer and String Aggregator, we created our own function getTupleDesc(), similar to the one we were required to do in Aggregate.java. This allowed us to do the same thing in Aggregate.java, which was set the tuple descriptor for the given tuple. Both Integer and String Aggregator's iterator were implemented similarly, by systematically adding tuples with the appropriate tuple descriptor given from getTupleDesc(); the only difference was in IntegerAggregator we also dealt with the AVG operation.

For Aggregate.java, we followed the design hint of implementing it depending on if it was going to be an IntegerAggregator or a StringAggregator. We also broke it up into the child passed in, and an aggregated child that was initalized and opened when the open() function was called. This allowed us to implement functions such as fetchNext() more easily, since we already had the aggregated child when we needed it. Most functions were easy to implement once we conceptually had the right member variables to work with, but coming to this conceptual standpoint was rather challenging.

For HeapPage.java and HeapFile.java, we implemented the rest of HeapPage first since the insertTuple and deleteTuple functions of HeapFile use the HeapPage functions of the same prototype. Also, most of HeapPage was previously done from what was given to us and Lab 1, so all we had to implement was the dirty functions and insert and delete tuple; for the dirty functions, we used the preexisting member variable _dirty to mark the tId as dirty or not. By manipulating the i value in the markSlotUsed function, we either set or cleared the bit value in the member variable header[] to represent if it was used or not. After that, insert and deleting tuples just consisted of placing them in the tuples member array, and then marking or unmarking them accordingly.
For HeapFile.java, it was quite a bit more tricky. Although "not necessary", we had to implement writePage; despite giving us some initial problems, it worked once we implemented it similarly to how we did in readPage, just this time not returning anything. Deleting a tuple wasn't too bad; we just had to find the page Id and page of that tuple, and delete the tuple. The part that was almost overlooked was testing to make sure it was a valid tuple to delete, hence we put the deleting tuple code within an if statement. Inserting a tuple on the otherhand, was arguably one of the most challenging functions in this lab. We tried creating an auxiliary function to help, but it ended adding more complexity than it was worth; what ended up working, though, was adding a completely new member variable in m_bufferPool. this allowed us to access the bufferpool to find the page via the tid. By looping to find the first such page that had an empty slot, we could insert the tuple, or otherwise create a new page.

Overall the lab took us again about a week, but this time a lot more time dedicated in the end to fine-tune our functions to get all tests to work, hence taking an extra day.

___________________________________________________________________
LAB 1 REPORT

Tuple.java and TupleDesc.java were not difficult to implement. The most difficult
part was understanding the differences between java and C++. There were not many
design choices to make aside from the types of structures used to store TDItems
and Tuples. We also implemented the methods in BufferPool.java necessary for
lab 1. The most difficult part was deciding how to store all the necessary
information, but we ended up storing a new class called page element using the
page id as the key.

Originally Catalog.java was quite hard to implement. Most this project was even more difficult due to our prior background in C++ and not in Java, but once we realized the similarities it was a lot easier. Catalog became much easier to implement once we started using a Map between each key and table; by using Map, it provided a natural way to access everything with the Tables that we needed to.

A more challenging problem with this project was having to implement an iterator over the DbFileIterator. Conceptually we had a hard time understanding interfaces and 'implements', but once we understood it and how it worked the entire thing became easier. By making an entire new class that implements a Heap File iterator and a Heap Page iterator over the Database File iterator, that portion of lab 1 became more clear after that.

The project itself didn't take a great deal of time, but we spent a lot of time debugging. We had an Out of Bounds exception being thrown that affected a lot of other functions (BufferPool.getPage(), HeapFileIterator, etc.) that was difficult to find but when we found it everything finally worked. Overall the entire lab took us a week. We definitely learned the importance of throwing exceptions and tracing them back. 
