Group Members:
Derek Foster	
Rebecca Hubeny	104133091

Tuple.java and TupleDesc.java were not difficult to implement. The most difficult
part was understanding the differences between java and C++. There were not many
design choices to make aside from the types of structures used to store TDItems
and Tuples. I also implemented the methods in BufferPool.java necessary for
lab 1. The most difficult part was deciding how to store all the necessary
information, but I ended up storing a new class called page element using the
page id as the key.

Originally Catalog.java was quite hard to implement. Most this project was even more difficult due to my prior background in C++ and not in Java, but once I realized the similarities it was a lot easier. Catalog became much easier to implement once I started using a Map between each key and table; by using Map, it provided a natural way to access everything with the Tables that I needed to.

A more challenging problem with this project was having to implement an iterator over the DbFileIterator. Conceptually I had a hard time understanding interfaces and 'implements', but once I understood it and how it worked the entire thing became easier. By making an entire new class that implements a Heap File iterator over the Database File iterator, that portion of lab 1 became more clear after that.
