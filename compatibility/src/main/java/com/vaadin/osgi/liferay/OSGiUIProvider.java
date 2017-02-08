package com.vaadin.osgi.liferay;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.vaadin.server.ClientConnector.DetachEvent;
import com.vaadin.server.ClientConnector.DetachListener;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

import org.osgi.framework.ServiceObjects;

public class OSGiUIProvider extends UIProvider {
	
	public OSGiUIProvider(ServiceObjects<UI> serviceObjects) {
		super();
		_serviceObjects = serviceObjects;
		// How to find out better way to register
		UI ui = serviceObjects.getService();
		_uiClass = (Class<UI>)ui.getClass();
		serviceObjects.ungetService(ui);
	}

	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
		return _uiClass;
	}
	
	@Override
	public UI createInstance(UICreateEvent event) {
		final ServiceObjects<UI> serviceObjects = _serviceObjects;
		final UI ui = serviceObjects.getService();
		
		if (_log.isDebugEnabled()) {
			_log.debug("unregistered UI " + ui.toString());
		}
		ui.addDetachListener(new DetachListener() {
			@Override
			public void detach(DetachEvent event) {
				serviceObjects.ungetService(ui);
				if (_log.isDebugEnabled()) {
					_log.debug("unregistered UI " + ui.toString());
				}	
			}
		} );
		return ui;
	}
	
	public String getDefaultDisplayName() {
		String name = _uiClass.getName();
		int beginIndex = name.lastIndexOf(".");
		beginIndex = beginIndex > 0 ? beginIndex + 1 : 0;
		
		name = name.substring(beginIndex);
		
		return name;
	}
	
	public String getPortletName() {
		return _uiClass.getName();		
	}

	private Log _log = LogFactoryUtil.getLog(OSGiUIProvider.class);
	private ServiceObjects<UI> _serviceObjects;
	private Class<UI> _uiClass;
}
