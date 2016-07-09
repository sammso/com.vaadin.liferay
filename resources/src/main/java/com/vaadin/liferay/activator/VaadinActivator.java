package com.vaadin.liferay.activator;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.Portlet;

import com.vaadin.liferay.VaadinWebResource;
import com.vaadin.osgi.portlet.VaadinOSGiPortlet;
import com.vaadin.osgi.OSGiUIProvider;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.vaadin.ui.UI;

public class VaadinActivator implements BundleActivator, 
	ServiceTrackerCustomizer<UI, UI> {

	public void start(BundleContext bundleContext) throws Exception {
		_portletServiceRegistration = new HashMap<ServiceReference<UI>, ServiceRegistration<Portlet>>();
		_serviceTracker  = new ServiceTracker<UI, UI>(
				bundleContext, UI.class, this);
		_serviceTracker.open(); 
		if (_log.isDebugEnabled()) {
			_log.debug("started");
		}		
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Iterator<ServiceRegistration<Portlet>> iterator = 
			_portletServiceRegistration.values().iterator();
		
		while(iterator.hasNext()) {
			iterator.next().unregister();
		}
	
		_portletServiceRegistration.clear();

		_portletServiceRegistration = null;
		
		if (_serviceTracker!=null) {
			_serviceTracker.close();
			_serviceTracker=null;
		}
		if (_log.isDebugEnabled()) {
			_log.debug("stopped");
		}	
	}
	

	@Override
	public UI addingService(ServiceReference<UI> uiServiceReference) {
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		BundleContext bundleContext = bundle.getBundleContext();
		
		ServiceObjects<UI> serviceObjects = 
			bundleContext.getServiceObjects(uiServiceReference);
				
		OSGiUIProvider osgiUIProvider = new OSGiUIProvider(serviceObjects);
		
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		
		copyProperty(
			uiServiceReference, properties, 
			"com.liferay.portlet.display-category", "category.sample");
	
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
		
		Class uiClass = osgiUIProvider.getUIClass(null);
	
		ServiceRegistration<Portlet> serviceRegistration = 
			bundleContext.registerService(
				Portlet.class, new VaadinOSGiPortlet(osgiUIProvider), 
				properties);

		_portletServiceRegistration.put(
			uiServiceReference, serviceRegistration);
		
		if (_log.isDebugEnabled()) {
			_log.debug(
				"portlet created for " + 
					osgiUIProvider.getUIClass(null).getName());
		}
		
		return osgiUIProvider.getDefaultUI();
	}
	
	public void copyProperty(
			ServiceReference<UI> serviceReference, 
			Dictionary<String, Object> properties, 
			String key, Object defaultValue) {

		Object value = serviceReference.getProperty(key);
		if (value!=null) {
			properties.put(key, value);
		}
		else if (value==null && defaultValue!=null) {
			properties.put(key, defaultValue);
		}
	}

	@Override
	public void modifiedService(ServiceReference<UI> serviceReference, UI ui) {
		// Ignore
	}

	@Override
	public void removedService(ServiceReference<UI> uiServiceReference, UI ui) {
		ServiceRegistration<Portlet> serviceRegistration = 
			_portletServiceRegistration.get(uiServiceReference);
		_portletServiceRegistration.remove(uiServiceReference);	
		
		serviceRegistration.unregister();
		
		if (_log.isDebugEnabled()) {
			_log.debug(
				"portlet unregistered for " + ui.getClass().getName());
		}		
	}
	
	private Log _log = LogFactoryUtil.getLog(VaadinActivator.class);
	private ServiceTracker<UI, UI> _serviceTracker;
	private Map<ServiceReference<UI>, ServiceRegistration<Portlet>> 
		_portletServiceRegistration;
}