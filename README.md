Java Best Practices UI
======================
A demonstration of best practices for Java multithreaded programming and user interfaces.

Why?

Few developers write good multithreaded server code, and the state of multithreaded user
interface programming is even worse. The book, The "Java Concurrency in Practice" has an important
chapter on multithreaded UI programming, however the examples and source files don't provide a
complete, working example, so I distilled the ideas from the book into a complete, yet simple
working application example using adaptations of source code and ideas from:
"Concurrent Programming in Java" by Brian Goetz, and "Effective Java" 2nd Edition by Joshua Bloch.

Working Demo Features:
---------------------
* Simple Computation with Cancel
* Multiple Computations
* Memoized Computations
* Shutdown gracefull and exit

To Run the Demo:
---------------
* mvn package
* cd target
* java -jar Java-Best-Practices-UI-1.0-dist.jar


Implementations of Java Concurrency in Practice concepts of:
------------------------------------------------------------
* ThreadPools
* Logging Singleton
* Executor Framework
* Shutdown hook with an orderly shutdown of services
* Examples of using the Executor and Completion services frameworks for responsiveness
and cancelling a long running tasks

Implementations of Effective Java features:
------------------------------------------
* Enum used as a singleton
* Builder pattern
* Generic collections, data structures

Other Best Practices:
--------------------
* Using MigLayout to simplify Swing layouts
* Initialization via System Properties
* Use of FindBugs static analysis, Cobertura code coverage

TODO 
Using documents located in a package path of testing; e.g. XML validation
Launching Wizard - because you can't change the memory & thread sizing at runtime
About screen with profile & application versioning information.