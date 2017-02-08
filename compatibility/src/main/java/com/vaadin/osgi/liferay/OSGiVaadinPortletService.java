package com.vaadin.osgi.liferay;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinPortlet;
import com.vaadin.server.VaadinPortletService;
import com.vaadin.server.VaadinPortletSession;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;

public class OSGiVaadinPortletService extends VaadinPortletService {

	public OSGiVaadinPortletService(VaadinPortlet portlet, 
			DeploymentConfiguration deploymentConfiguration, 
			OSGiUIProvider osgiUIProvider)
		throws ServiceException {
		
		super(portlet, deploymentConfiguration);
		_osgiUIProvider = osgiUIProvider;
	}

	@Override
	protected VaadinSession createVaadinSession(VaadinRequest request) 
		throws ServiceException {
		
		VaadinSession vaadinSession = new VaadinPortletSession(this);
		vaadinSession.addUIProvider(_osgiUIProvider);
		
		return vaadinSession;
	}
	
	private OSGiUIProvider _osgiUIProvider;
}
