package com.vaadin.osgi.liferay;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.vaadin.ui.UI;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceObjects;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;

@Component(immediate = true)
public class VaadinPortletProvider {

	@Activate
	void activate(ComponentContext componentContext) {
		try {
			BundleContext bundleContext = componentContext.getBundleContext();

			Filter filter = bundleContext.createFilter(
				String.format("(objectClass=%s)", UI.class.getName()));

			_portletUIServiceTrackerCustomizer = 
				new PortletUIServiceTrackerCustomizer();
		
			_serviceTracker  = new ServiceTracker<UI, ServiceObjects<UI>>(
					bundleContext, filter, _portletUIServiceTrackerCustomizer);
			
			_serviceTracker.open();

			if (_log.isInfoEnabled()) {
				_log.info("VaadinPortletProvider activated");
			}
		} 
		catch (InvalidSyntaxException e) {
			_log.error(e);
		}
	}

	@Deactivate
	void deactivate() {	
		if (_serviceTracker!=null) {
			_serviceTracker.close();
			_portletUIServiceTrackerCustomizer.cleanPortletRegistrations();
			_portletUIServiceTrackerCustomizer=null;
		}
		
		if (_log.isInfoEnabled()) {
			_log.info("VaadinPortletProvider deactivated");
		}		
	}
	
	private Log _log = LogFactoryUtil.getLog(VaadinPortletProvider.class);
	private ServiceTracker<UI, ServiceObjects<UI>> _serviceTracker;
	private PortletUIServiceTrackerCustomizer _portletUIServiceTrackerCustomizer;
}
