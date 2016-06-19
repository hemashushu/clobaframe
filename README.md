Clobaframe
==========
Common components for desktop and web application.
Provides the base components such as I/O, settings, IoC and resource etc
that an application need, it also abstracts the underlay service that runs on cloud platforms,
such as VPS, Google Cloud Platform, Amazon Web Service, make it possible to migrate
application between these cloud platforms.

*Features:*

* common: common functions.
* setting: application settings, writable.
* ioc: mini IoC to improve the small desktop application start up performance.
* io: abstract the local file and web I/O stream.
* resource: automaticatly manage the app/web resources (i.e. image/css/js),   
  includes the web resource version update/compress/css and js minify/server  
  side cache/client side cache control.
* blobstore: store the big binary object (user uploaded content) to local storage or cloud service.
* query: a SQL like query tool for collections.
* cache: simplified the cache access.
* media: load/parse/process the image, audio and video file.
* mail: abstract the mail sender.
* search: full text index.


![family](https://github.com/ivarptr/clobaframe/raw/master/doc/figure-family.jpg)

Unit test
---------
1. Install Git, JDK, Apache Maven.
2. Check out source code to any folder.
3. Change into the source folder and run the unit test with Apache Maven:

```
    $ mvn clean test
```

Install library
---------------
Install clobaframe into Apache Maven local repository:

```
    $ mvn clean javadoc:jar source:jar install
```

Release
-------

[v2.6 release notes](https://github.com/ivarptr/clobaframe/tree/master/doc/release-2.6_zh-CN.md)



