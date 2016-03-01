Liferay 7 / Vaadin 7 compatibility pack
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

~~~
cp org.liferay.vaadin7.compatibilitypack/target/org.liferay.vaadin7.compatibilitypack.distribution-<version>.lpkg -d <replace-this-to-your-liferay7-home>/deploy 
~~~

Configuring Web Resources
-------------------------

```java
@Component(
		immediate = true,
		property = {
			"com.liferay.portlet.display-category=category.<your category>",
			"com.liferay.portlet.instanceable=true",
			"javax.portlet.display-name=<your portlet name>",
			"javax.portlet.init-param.UI=<your UI class name>",
			"javax.portlet.security-role-ref=power-user,user",
			VaadinWebResource.JAVAX_PORTLET_RESOURCES_PATH
		},
		service = javax.portlet.Portlet.class
	)
public class Portlet extends VaadinPortlet {
}
```

the VaadinWebResource.JAVAX_PORTLET_RESOURCES_PATH contains location of the required web resources of used Vaadin version.
