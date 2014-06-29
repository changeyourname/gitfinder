gitfinder
=========

Java code to crawl and process information available on the GitHub repositories

At the moment it is used for generating an up-to-date list of registered users
on github and list of associated repositories.

This is a command line application. To run, you will to provide your personal
github login/password. To ensure privacy, the information is not saved into
the local folder where the project files are located.

Running for the first time
==========================

From the command line, type:
java -jar users mylogin mypassword

This command will create a text file "users.txt" containing one user per line.

When you want to finish, just press "CTRL+C"


Usage examples
==============

Example #1 - creating a list of users
java -jar users

Example #2 - creating a list of repositories
java -jar repositories


Special notes
=============

More likely you won't be able to run any of these commands without interruption.

Normally, your Internet connection might fail, the computer gets shut down or some
other related issue. Typically you'd need to restart all over again.

Don't worry, our code is prepared to resume from the last point where it has stopped.

If you're using linux, there is a neat way to follow the progress in overall:

wc -l users.txt

This command counts the lines, which in our case are the number of users or repositories that were already indexed.


Compilation
===========

To compile, I'm using NetBeans with JDK (Java Development Kit) 6


License
=======
Free software, released under the European Public License without the Appendix
section.


Thanks
======
Thanks go out for the development team of https://github.com/jcabi/jcabi for
making available the Java-API wrapper and to @yegor256 for his blazzing-fast
support to make the wrapper work in top shape.

Special thanks to @github for making this API available, you guys rock!

