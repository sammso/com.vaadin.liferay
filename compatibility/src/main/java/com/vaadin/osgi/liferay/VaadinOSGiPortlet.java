package com.vaadin.osgi.liferay;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinPortlet;
import com.vaadin.server.VaadinPortletService;

public class VaadinOSGiPortlet extends VaadinPortlet {

	public VaadinOSGiPortlet(OSGiUIProvider uiProvider) {
		_uiProvider = uiProvider;
	}

	@Override
	protected VaadinPortletService createPortletService(
			DeploymentConfiguration deploymentConfiguration)
		throws ServiceException {
		
		try {

			OSGiVaadinPortletService osgiVaadinPortletService = 
				new OSGiVaadinPortletService(this, deploymentConfiguration,
					_uiProvider);
			
			osgiVaadinPortletService.init();

			return osgiVaadinPortletService;
		} catch (Exception e) {
			_log.error(e);
			throw e;
		}
	}

	private OSGiUIProvider _uiProvider;
	private Log _log = LogFactoryUtil.getLog(VaadinOSGiPortlet.class);
}
