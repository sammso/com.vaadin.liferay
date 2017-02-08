Vaadin/ Liferay DXP / 7.0 compatibility pack
===========================================

Overview
--------
As Liferay 7 does not have Vaadin 7 included, this project provides minimilastic Vaadin 7 installation for Liferay 7

Requirements:
-------------
- Java 7 and Maven 
- Liferay DXP / 7.0 (latest)

To compile:
-----------

~~~
mvn package
~~~

*Note due the latest changes at Liferay 7.0 / DXP following deployment package is not complete to be deployed.*

To workaround you need to unzip the ``com.vaadin.liferay.distribution-<version>.lpkg`` and copy to the results to ``<liferay-home>/osgi/modules`` folder.

~~~
unzip org.liferay.vaadin7.compatibilitypack/target/com.vaadin.liferay.distribution-<version>.lpkg -d <replace-this-to-your-liferay7-home>/deploy 
~~~

Creating Vaadin Portlet
-------------------------

Only thing that you need is to create UI Component. 

```java

@Component(scope = ServiceScope.PROTOTYPE, service = com.vaadin.ui.UI.class)
public class CurrentUserUI extends com.vaadin.ui.UI {
	@Override
	protected void init(VaadinRequest request) {
		try {
			User user = _portal.getUser(
				VaadinPortletService.getCurrentPortletRequest());
			if (user==null) {
				setContent(new Label("Non logged-in user change"));
			}
			else {
				setContent(new Label("User change " + user.getFullName()));
			}
		} 
		catch (PortalException e) {
			_log.error(e);
		}
	}

	private Log _log = LogFactoryUtil.getLog(CurrentUserUI.class);

	@Reference(cardinality = ReferenceCardinality.MANDATORY)
	private Portal _portal;
}
```

Example of this you can find from ``example``folder.

Versions
--------
- master branch is now leading branch, but should be compiled against vaadin master branch
-- So to compile master branch compile vaadin first locally
- 7.7.x for 7.7-SNAPSHOT - you need to build vaadin 7.7-SNAPSHOT locally
- 8.0.0-beta2 for Vaadin 8.0.0-beta2
- 8.0.0-beta1 for Vaadin 8.0.0-beta1 (does not work, requires MANIFEST fixes on Vaadin side)
- 7.7.6 for Vaadin 7.7.6

