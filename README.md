gitfinder
=========

Java code to crawl and process information available on GitHub repositories

Quick and useful for generating an up-to-date list of registered users
on github and then list the associated repositories.

This is a command line application. To run, you will need to provide a 
github login/password. To ensure privacy, the information is not saved into
the local folder where the project files are located.

You should already get a good performance using only one machine. You have
available a distributed version that permits to connect multiples machines to
process a given task. Just choose a stable machine to be your server (look on examples),
then start each client pointed on the direction of the server. Each client will
then ask for tasks on the server machine, process the tasks and then provide the
results.

The distributed version is particularly useful to speed up the indexing of repositories which 
are very time consuming on a single machine and/or github account.


Downloading / preparation
=========================

The easiest way is:
- Step 1: download the zip file made available on https://github.com/triplecheck/gitfinder
- Step 2: extract all files to a folder on disk
- Step 3: open the terminal (or command prompt)
- Step 4: move to the folder that you extracted, get inside the "run" folder
- Step 5: follow the instructions on the "Running for the first time"

If not possible to follow the above instructions. Download the code using NetBeans.
Inside Netbeans look for "Team" -> "Github" -> "Clone"
Then type the URL of this project: https://github.com/triplecheck/gitfinder.git

Running for the first time
==========================

From the command line, type:

```java -jar gitfinder users```

On the first run you will be asked for a GitHub username and password. Please 
be sure to type the correct details. If you need to correct the details then 
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

Example #4 - starting the web server (replace 9999 with your preferred port):
```java -jar gitfinder server 9999```

Example #5 - starting the web client (replace 127.0.0.1:9999 with your server address):
```java -jar gitfinder client 127.0.0.1:9999```

Example #6 - seeing the status of a given server (using the web browser):
``` http://127.0.0.1:9999/status ```


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

If you're using NetBeans, we define different profiles to launch the program
with different parameters. This helps us to quickly test between server, client
and other options. Look on the top bar of NetBeans to see which profile you are
launching. In case something doesn't seem to work, I'd be glad to help. Just 
write me an email or add a bug report.

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

