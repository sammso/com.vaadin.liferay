Liferay 7 / Vaadin 7 combatiblity pack
===========================================

Overview
--------
As Liferay 7 does not have Vaadin 7 included, this project provides minimilastic Vaadin 7 installation for Liferay 7

Requirements:
-------------
- Java 7 and Maven 
- Liferay Portal 7 (latest)

To compile:
-----------

~~~
mvn package
~~~

Deploy:
-----------

portal-ext.properties

~~~
vaadin.resources.path=/o/vaadin7
~~~

if you are servicing Vaadin resources from resources bundle.

~~~
vaadin.resources.path=/o/vaadin7
~~~

~~~
cp org.liferay.vaadin7.compatibilitypack/target/org.liferay.vaadin7.compatibilitypack.distribution-<version>.lpkg -d <replace-this-to-your-liferay7-home>/deploy 
~~~
