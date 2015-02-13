Group Members:
Derek Foster    504146063
Rebecca Hubeny	104133091

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
