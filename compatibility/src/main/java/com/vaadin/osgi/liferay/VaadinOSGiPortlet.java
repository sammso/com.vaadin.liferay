package com.vaadin.osgi.liferay;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletException;

import com.vaadin.server.DefaultUIProvider;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinPortlet;
import com.vaadin.server.VaadinSession;

public class VaadinOSGiPortlet extends VaadinPortlet {
	public VaadinOSGiPortlet(UIProvider uiProvider) {
		_uUiProvider = uiProvider;
	}
	
	@Override
	protected void portletInitialized() throws PortletException {
        getService().addSessionInitListener(new SessionInitListener() {

			private static final long serialVersionUID = 1737347072701619179L;

			@Override
            public void sessionInit(SessionInitEvent sessionInitEvent)
                    throws ServiceException {
				
				// Implementation is copied from vaadin-spring
				
                VaadinSession session = sessionInitEvent.getSession();
                List<UIProvider> uiProviders = new ArrayList<UIProvider>(
                        session.getUIProviders());
                for (UIProvider provider : uiProviders) {
                    // use canonical names as these may have been loaded with
                    // different classloaders
                    if (DefaultUIProvider.class.getCanonicalName().equals(
                            provider.getClass().getCanonicalName())) {
                        session.removeUIProvider(provider);
                    }
                }
                
                session.addUIProvider(_uUiProvider);
            }
        });
	}
	
	private UIProvider _uUiProvider;
}
