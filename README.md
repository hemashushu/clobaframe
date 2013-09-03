clobaframe
==========

a web application framework provides:

 * blob store - store and fetch the big binary object to/from cloud service.
 * caching - simplied the caching access interface.
 * io - a core model, to abstract the data stream.
 * media - load and get the meta data of image, audio, video.
 * query - a SQL like query tool for object collection.
 * web resource - handle the image/css/js web resources automatically.

Unit test
-----------------

1. Check out source code to any folder.

2. Make the following folder and change the folder owner to the current user:
    /var/lib/clobaframe

3. Run the unit test with Apache Maven:
    $ mvn clean test

Install library
---------------

To install library into Apache Maven local repository, after success to unit test, then run:
    $ mvn clean javadoc:jar source:jar install -DskipTests=true

Manual
------

See "doc/manual_zh-CN.pdf".