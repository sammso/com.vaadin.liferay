package com.vaadin.osgi.liferay;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.vaadin.osgi.liferay.resources.VaadinWebResource;
import com.vaadin.ui.UI;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.Portlet;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

@Component(immediate = true)
public class VaadinPortletProvider {

	@Activate
	void activate(ComponentContext componentContext) {
		_serviceTracker  = new ServiceTracker<UI, UI>(
				componentContext.getBundleContext(), UI.class,  new UIServiceTrackerCustomizer());
		
		_serviceTracker.open(); 
		
		if (_log.isDebugEnabled()) {
			_log.debug("started");
		}
	}


	@Deactivate
	void deactivate() {
		
		if (_serviceTracker!=null) {
			_log.error("_serviceTracker.close() - start");		
			_serviceTracker.close();
			_log.error("_serviceTracker.close() - end");		
			_serviceTracker=null;
		}
		_log.error("stopped");		
	}
		

	private class UIServiceTrackerCustomizer implements ServiceTrackerCustomizer<UI, UI> {
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
			
			if (_log.isDebugEnabled()) {
				
				_log.debug(
					"Vaadin " + bundle.getVersion() + " portlet created for " + 
						osgiUIProvider.getUIClass(null).getName());
			}
			
			return osgiUIProvider.getDefaultUI();
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
			
			_log.warn(
				"portlet unregistered for " + ui.getClass().getName() + " left: " + _portletServiceRegistration.size());
		}
		
		/**
		 * DO we need this?
		 */
		public void cleanPortletRegistrations() {
			Iterator<ServiceRegistration<Portlet>> iterator = 
					_portletServiceRegistration.values().iterator();
			
			while(iterator.hasNext()) {
				iterator.next().unregister();
			}
		
			_portletServiceRegistration.clear();
		}
		

		public void copyProperty(ServiceReference<UI> serviceReference, Dictionary<String, Object> properties, String key,
				Object defaultValue) {

			Object value = serviceReference.getProperty(key);
			if (value != null) {
				properties.put(key, value);
			} else if (value == null && defaultValue != null) {
				properties.put(key, defaultValue);
			}
		}		
		
		private Map<ServiceReference<UI>, ServiceRegistration<Portlet>> _portletServiceRegistration = new HashMap<ServiceReference<UI>, ServiceRegistration<Portlet>>();
	}
	
	private Log _log = LogFactoryUtil.getLog(VaadinPortletProvider.class);
	private ServiceTracker<UI, UI> _serviceTracker;
}

