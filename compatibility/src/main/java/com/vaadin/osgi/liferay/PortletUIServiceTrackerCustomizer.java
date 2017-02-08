package com.vaadin.osgi.liferay;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.vaadin.osgi.liferay.resources.VaadinWebResource;
import com.vaadin.ui.UI;

import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.Portlet;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

class PortletUIServiceTrackerCustomizer implements 
	ServiceTrackerCustomizer<UI, ServiceObjects<UI>> {
	
	PortletUIServiceTrackerCustomizer() {
	}
	
	@Override
	public ServiceObjects<UI> addingService(
			ServiceReference<UI> uiServiceReference) {

		Bundle bundle = uiServiceReference.getBundle();
		BundleContext bundleContext = bundle.getBundleContext();
		
		ServiceObjects<UI> serviceObjects = 
			bundleContext.getServiceObjects(uiServiceReference);
		
		OSGiUIProvider osgiUIProvider = new OSGiUIProvider(serviceObjects);
		
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		
		copyProperty(
			uiServiceReference, properties, 
			"com.liferay.portlet.display-category", "category.vaadin");
	
		copyProperty(
			uiServiceReference, properties,
			"javax.portlet.name", osgiUIProvider.getPortletName());
	
		copyProperty(
			uiServiceReference, properties, "javax.portlet.display-name", 
			osgiUIProvider.getDefaultDisplayName());
		
		copyProperty(
			uiServiceReference, properties, "javax.portlet.security-role-ref",
			new String[] {"power-user", "user"});
		
		copyProperty(
			uiServiceReference, properties, 
			VaadinWebResource.JAVAX_PORTLET_RESOURCES_INIT_PARAM, 
			VaadinWebResource.JAVAX_PORTLET_RESOURCES_INIT_VALUE);
	
		ServiceRegistration<Portlet> serviceRegistration = 
			bundleContext.registerService(
				Portlet.class, new VaadinOSGiPortlet(osgiUIProvider), 
				properties);

		_portletServiceRegistration.put(
			uiServiceReference, serviceRegistration);
		
		if (_log.isInfoEnabled()) {
			_log.info(
				"VaadinPortlet " + bundle.getVersion() + 
				" portlet created for " + 
				osgiUIProvider.getUIClass(null).getName());
		}
		
		return serviceObjects;
	}
	@Override
	public void modifiedService(ServiceReference<UI> serviceReference, 
			ServiceObjects<UI> ui) {
		
		_log.info("modifiedService");

	}

	@Override
	public void removedService(ServiceReference<UI> uiServiceReference, 
			ServiceObjects<UI> ui) {

		ServiceRegistration<Portlet> serviceRegistration = 
			_portletServiceRegistration.get(uiServiceReference);

		_portletServiceRegistration.remove(uiServiceReference);	
		
		serviceRegistration.unregister();
		if (_log.isInfoEnabled()) {
			_log.info(
				"Vaadin portlet unregistered for " + ui.getClass().getName() + 
				" left: " + _portletServiceRegistration.size());
		}
	}
	
	public void cleanPortletRegistrations() {
		
		Collection<ServiceRegistration<Portlet>> portletRegistrations = 
			_portletServiceRegistration.values();
		
		if (_log.isDebugEnabled()) {
			_log.debug("cleanPortletRegistrations count: " + 
				portletRegistrations.size());
		}
		
		Iterator<ServiceRegistration<Portlet>> iterator = 
			portletRegistrations.iterator();
		
		while(iterator.hasNext()) {
			iterator.next().unregister();
		}
	
		_portletServiceRegistration.clear();
		_portletServiceRegistration = null;
	}
	
	public void copyProperty(ServiceReference<UI> serviceReference, 
			Dictionary<String, Object> properties, String key,
			Object defaultValue) {

		Object value = serviceReference.getProperty(key);

		if (value != null) {
			properties.put(key, value);
		} else if (value == null && defaultValue != null) {
			properties.put(key, defaultValue);
		}
	}		
	
	private Map<ServiceReference<UI>, ServiceRegistration<Portlet>> 
		_portletServiceRegistration = 
			new HashMap<ServiceReference<UI>, ServiceRegistration<Portlet>>();
	
	private Log _log = 
		LogFactoryUtil.getLog(PortletUIServiceTrackerCustomizer.class);
}
