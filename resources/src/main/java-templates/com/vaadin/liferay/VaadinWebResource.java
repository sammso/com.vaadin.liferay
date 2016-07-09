package com.vaadin.liferay;

/**
 * This class contains constant to find current resources
 * @author Sampsa Sohlman
 */
public abstract class VaadinWebResource {
	public static final String JAVAX_PORTLET_RESOURCES_INIT_PARAM = 
		"javax.portlet.init-param.vaadin.resources.path";
	public static final String JAVAX_PORTLET_RESOURCES_INIT_VALUE = 
		"/o/vaadin${vaadin.version}";
	public static final String JAVAX_PORTLET_RESOURCES_PATH = 
		JAVAX_PORTLET_RESOURCES_INIT_PARAM + "=" + 
		JAVAX_PORTLET_RESOURCES_INIT_VALUE;
}
