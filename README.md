clobaframe
==========

a web application supports components that provides:

 * blob store - store and fetch the big binary object to/from cloud service.
 * caching - simplied the caching access interface.
 * io - a core component, to abstract the data stream.
 * media - load the image, audio and video, and parse the meta data and provides some common functions such as scale image.
 * query - a SQL like query tool for collection.
 * web resource - manage the (image/css/js) web resources.

Unit test
---------

1. Check out source code to any folder.

2. Make the following folder and change the folder owner to the current user:

    # mkdir /var/lib/clobaframe
    # chown `whoami` /var/lib/clobaframe

3. Run the unit test with Apache Maven:

    $ mvn clean test

Install library
---------------

Install clobaframe into Apache Maven local repository:

    $ mvn clean javadoc:jar source:jar install

Manual
------

See "doc/manual_zh-CN.pdf".
