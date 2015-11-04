Some configurations which are relevant to the project

custom.properties

This is a modified version of custom properties in OpenNMS 16
It will need to be updated for OpenNMS 17
The following packages have been added to the property
org.osgi.framework.system.packages.extra=

        org.opennms.netmgt.events.api;version=16.0.0,\
        org.opennms.netmgt.events.api.annotations;version=16.0.0,\

        com.sun.jersey.api.core.servlet;version=1.18.1,\
        com.sun.jersey.spi.container.servlet;version=1.18.1,\
        com.sun.jersey.spi.scanning.servlet;version=1.18.1,\
        com.sun.jersey.server.impl.container.servlet;version=1.18.1,\
        javax.ws.rs;version=1.1.1,\
        javax.ws.rs.core;version=1.1.1,\
        javax.ws.rs.ext;version=1.1.1,\
        com.sun.jersey.core.provider.jaxb;version=1.18.1,\
        com.sun.jersey.core.impl.provider.header;version=1.18.1,\
        com.sun.jersey.impl;version=1.18.1,\
        com.sun.jersey.core.util;version=1.18.1,\
        com.sun.jersey.core.provider;version=1.18.1,\
        com.sun.jersey.core.spi.scanning;version=1.18.1,\
        com.sun.jersey.api.provider.jaxb;version=1.18.1,\
        com.sun.jersey.core.header;version=1.18.1,\
        com.sun.jersey.core.impl.provider.xml;version=1.18.1,\
        com.sun.jersey.core.header.reader;version=1.18.1,\
        com.sun.jersey.core.osgi;version=1.18.1,\
        com.sun.jersey.spi;version=1.18.1,\
        com.sun.jersey.spi.inject;version=1.18.1,\
        com.sun.jersey.localization;version=1.18.1,\
        com.sun.jersey.core.spi.scanning.uri;version=1.18.1,\
        com.sun.jersey.core.spi.component;version=1.18.1,\
        com.sun.jersey.core.spi.factory;version=1.18.1,\
        com.sun.jersey.core.spi.component.ioc;version=1.18.1,\
        com.sun.jersey.api.representation;version=1.18.1,\
        com.sun.jersey.core.reflection;version=1.18.1,\
        com.sun.jersey.core.impl.provider.entity;version=1.18.1,\
        com.sun.jersey.spi.service;version=1.18.1,\
        com.sun.jersey.api.uri;version=1.18.1,\
        com.sun.jersey.api;version=1.18.1,\
        com.sun.jersey.api.container.filter;version=1.18.1,\
        com.sun.jersey.api.container;version=1.18.1,\
        com.sun.jersey.api.container.httpserver;version=1.18.1,\
        com.sun.jersey.api.core;version=1.18.1,\
        com.sun.jersey.api.model;version=1.18.1,\
        com.sun.jersey.api.view;version=1.18.1,\
        com.sun.jersey.api.wadl.config;version=1.18.1,\
        com.sun.jersey.server.impl.wadl;version=1.18.1,\
        com.sun.jersey.server.impl.model.parameter.multivalued;version=1.18.1,\
        com.sun.jersey.server.impl.model.parameter;version=1.18.1,\
        com.sun.jersey.server.impl.application;version=1.18.1,\
        com.sun.jersey.server.impl.component;version=1.18.1,\
        com.sun.jersey.server.impl.provider;version=1.18.1,\
        com.sun.jersey.server.impl.template;version=1.18.1,\
        com.sun.jersey.server.wadl.generators.resourcedoc.model;version=1.18.1,\
        com.sun.jersey.server.impl.resource;version=1.18.1,\
        com.sun.jersey.server.impl.monitoring;version=1.18.1,\
        com.sun.jersey.server.impl.modelapi.annotation;version=1.18.1,\
        com.sun.jersey.server.impl.container;version=1.18.1,\
        com.sun.jersey.server.wadl;version=1.18.1,\
        com.sun.jersey.server.impl.model.method.dispatch;version=1.18.1,\
        com.sun.jersey.server.impl;version=1.18.1,\
        com.sun.jersey.server.wadl.generators.resourcedoc;version=1.18.1,\
        com.sun.jersey.server.impl.container.httpserver;version=1.18.1,\
        com.sun.jersey.server.impl.container.filter;version=1.18.1,\
        com.sun.jersey.server.wadl.generators.resourcedoc.xhtml;version=1.18.1,\
        com.sun.jersey.server.impl.uri.rules;version=1.18.1,\
        com.sun.jersey.server.spi.component;version=1.18.1,\
        com.sun.jersey.server.probes;version=1.18.1,\
        com.sun.jersey.server.wadl.generators;version=1.18.1,\
        com.sun.jersey.server.impl.modelapi.validation;version=1.18.1,\
        com.sun.jersey.server.impl.model.method;version=1.18.1,\
        com.sun.jersey.server.impl.model;version=1.18.1,\
        com.sun.jersey.server.impl.uri.rules.automata;version=1.18.1,\
        com.sun.jersey.server.impl.uri;version=1.18.1,\
        com.sun.jersey.server.impl.inject;version=1.18.1,\
        com.sun.jersey.spi.container;version=1.18.1,\
        com.sun.jersey.spi.dispatch;version=1.18.1,\
        com.sun.jersey.spi.monitoring;version=1.18.1,\
        com.sun.jersey.spi.resource;version=1.18.1,\
        com.sun.jersey.spi.scanning;version=1.18.1,\
        com.sun.jersey.spi.template;version=1.18.1,\
        com.sun.jersey.spi.uri.rules;version=1.18.1,\
        com.sun.research.ws.wadl;version=1.18.1,\
        jersey.repackaged.org.objectweb.asm;version=1.18.1
        