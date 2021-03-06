package com.marklogic.mgmt;

import com.marklogic.mgmt.util.PropertySource;
import com.marklogic.mgmt.util.PropertySourceFactory;

public class DefaultManageConfigFactory extends PropertySourceFactory implements ManageConfigFactory {

    public DefaultManageConfigFactory() {
        super();
    }

    public DefaultManageConfigFactory(PropertySource propertySource) {
        super(propertySource);
    }

    @Override
    public ManageConfig newManageConfig() {
        ManageConfig c = new ManageConfig();

        String mlUsername = getProperty("mlUsername");
        String mlPassword = getProperty("mlPassword");

        String prop = getProperty("mlManageHost");
        if (prop != null) {
            logger.info("Manage host: " + prop);
            c.setHost(prop);
        } else {
            prop = getProperty("mlHost");
            if (prop != null) {
                logger.info("Manage host: " + prop);
                c.setHost(prop);
            }
        }

        prop = getProperty("mlManagePort");
        if (prop != null) {
            logger.info("Manage port: " + prop);
            c.setPort(Integer.parseInt(prop));
        }

        prop = getProperty("mlManageUsername");
        if (prop != null) {
            logger.info("Manage username: " + prop);
            c.setUsername(prop);
        } else if (mlUsername != null) {
            logger.info("Manage username: " + mlUsername);
            c.setUsername(mlUsername);
        }

        prop = getProperty("mlManagePassword");
        if (prop != null) {
            c.setPassword(prop);
        } else if (mlPassword != null) {
            c.setPassword(mlPassword);
        }

	    prop = getProperty("mlManageScheme");
	    if (prop != null) {
		    logger.info("Manage scheme: " + prop);
		    c.setScheme(prop);
	    }

	    prop = getProperty("mlManageSimpleSsl");
	    if (prop != null) {
		    logger.info("Use simple SSL for Manage app server: " + prop);
		    c.setConfigureSimpleSsl(Boolean.parseBoolean(prop));
	    }

	    prop = getProperty("mlManageCleanJsonPayloads");
	    if (prop != null) {
	    	logger.info("Cleaning Management API JSON payloads: " + prop);
	    	c.setCleanJsonPayloads(Boolean.parseBoolean(prop));
	    }

	    prop = getProperty("mlSecurityUsername");
	    if (prop != null) {
	    	logger.info("Manage user with security role: " + prop);
	    	c.setSecurityUsername(prop);
	    }
	    else {
		    prop = getProperty("mlAdminUsername");
		    if (prop != null) {
			    logger.info("mlAdminUsername is deprecated; please use mlSecurityUsername instead; Manage user with security role: " + prop);
			    c.setSecurityUsername(prop);
		    } else if (mlUsername != null) {
			    logger.info("Manage user with security role: " + mlUsername);
			    c.setSecurityUsername(mlUsername);
		    } else {
			    c.setSecurityUsername(c.getUsername());
		    }
	    }

	    prop = getProperty("mlSecurityPassword");
	    if (prop != null) {
	    	c.setSecurityPassword(prop);
	    } else {
		    prop = getProperty("mlAdminPassword");
		    if (prop != null) {
		    	logger.info("mlAdminPassword is deprecated; please use mlSecurityPassword instead");
			    c.setSecurityPassword(prop);
		    } else if (mlPassword != null) {
			    c.setSecurityPassword(mlPassword);
		    } else {
			    c.setSecurityPassword(c.getPassword());
		    }
	    }

        return c;
    }

}
