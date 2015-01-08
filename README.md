clobaframe
==========

Common components for web application.
It abstracts the underlay service that runs on cloud platforms,
such as VPS, Google cloud platform, Amazon web service, enable you to migrate
web application between variable cloud platforms.

Interfaces:

 * query: a SQL like query tool for collections.
 * cache: simplified the cache access.
 * io: abstract the local file and web i/o stream.
 * media: load/parse/process the image, audio and video file.
 * blobstore: store and fetch the big binary object to/from cloud service.
 * webresource: manage the web resources (i.e. image/css/js).
 * dynamodel: build the view model (for MVC).
 * mail: abstract the mail sender.

Unit test
---------

1. Check out source code to any folder.

2. Install and start the memcached service (optional, for complete the cache-memcached unit tests).

3. Run the unit test with Apache Maven:

    $ mvn clean test

Install library
---------------

Install clobaframe into Apache Maven local repository:

    $ mvn clean javadoc:jar source:jar install

