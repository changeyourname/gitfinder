gitfinder
=========

Java code to crawl and process information available on GitHub repositories

Quick and useful for generating an up-to-date list of registered users
on github and then list the associated repositories.

This is a command line application. To run, you will need to provide a 
github login/password. To ensure privacy, the information is not saved into
the local folder where the project files are located.

Running for the first time
==========================

From the command line, type:

```java -jar gitfinder users```

On the first run you will be asked for the GitHub username and password. Please 
be sure to type the correct details. If you need to correct the details, then 
you should call the "reset" option as mentioned on example #3.

Each indexing command creates a text file on the same folder where you are 
running this of software.

When you want to finish, just press "CTRL+C" or wait for the program to conclude 
running on its own.


Usage examples
==============

Example #1 - creating a list of users:

```java -jar gitfinder users```


Example #2 - creating a list of repositories:

```java -jar gitfinder repositories```


Example #3 - forget the github username/password:

```java -jar gitfinder reset```


Special notes
=============

Normally is not easy to run any of these commands without interruption when 
running from your desktop computer.

This happens because domestic Internet connections can fail every now and then, or your computer 
gets shut down after some hours of inactivity. Typically this would force you to restart all over again.

This software is prepared to be resilient and will try to keep connections running 
for as long as possible. If something fails, you can then restart from the last 
indexed information and avoid starting from scratch.

If you're using Linux, there is a neat way to follow the progress in overall:

```wc -l users.txt```

This command counts the lines, which in our case are the number of users or repositories that were already indexed.


Compilation
===========

We use NetBeans with JDK (Java Development Kit) 6 to compile the software.

All dependencies are included on the project. No special configuration 
is needed.


License
=======
Free software, released under the European Public License without the Appendix
section. If you need a different licensing regime to match your own code, you're 
welcome to get in contact.


Thanks
======
Thanks go out for the development team of https://github.com/jcabi/jcabi for
making available the Java-API wrapper and to https://github.com/yegor256 for his blazzing-fast
support to make the wrapper work in top shape.

Special thanks to https://github.com/ for making this API available, you guys rock!

