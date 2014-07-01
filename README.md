gitfinder
=========

Java code to crawl and process information available on the GitHub repositories

At the moment it is used for generating an up-to-date list of registered users
on github and list of associated repositories.

This is a command line application. To run, you will need to provide a 
github login/password. To ensure privacy, the information is not saved into
the local folder where the project files are located.

Running for the first time
==========================

From the command line, type:
```java -jar users```

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

More likely you won't be able to run any of these commands without interruption when 
running from a home computer.

Normally, domestic Internet connection will fail every now and then, or the computer 
gets shut down after some hours of inactivity. Typically you'd need to restart all over again.

This code is prepared to be resilient. It will try to keep connections running 
for as long as possible, and if something fails it will restart from the last 
indexed information so that you don't need to start from scratch.

If you're using linux, there is a neat way to follow the progress in overall:

```wc -l users.txt```

This command counts the lines, which in our case are the number of users or repositories that were already indexed.


Compilation
===========

To compile, you can use NetBeans with JDK (Java Development Kit) 6.

All dependencies needed are included on the project. No special configuration 
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

