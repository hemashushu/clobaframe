clobaframe
==========

web application common components, includes:

 * blob store - store and fetch the big binary object to/from cloud service.
 * caching - simplified the cache access.
 * io - abstract the local file and web i/o stream.
 * media - load the image, audio and video file, and parse the meta data and provides some common functions such as image scaling.
 * query - a SQL like query tool for the object collection.
 * web resource - manage the web resources (i.e. image/css/js).

Unit test
---------

1. Check out source code to any folder.

2. Make the following folder and change the folder owner to the current user:

    $ sudo mkdir /var/lib/clobaframe
    
    $ sudo chown `whoami` /var/lib/clobaframe

3. Run the unit test with Apache Maven:

    $ mvn clean test

Install library
---------------

Install clobaframe into Apache Maven local repository:

    $ mvn clean javadoc:jar source:jar install

Manual
------

See "doc/manual_zh-CN.pdf" (some kind of old).
