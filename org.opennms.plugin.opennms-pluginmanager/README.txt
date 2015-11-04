OpenNMS Vaadin based plugin manager

to install in OpenNMS
open ssh consol

ssh -p 8101 admin@localhost

opennms> features:addurl mvn:org.opennms.plugins/org.opennms.plugin.opennms-pluginmanager/17.0.0-SNAPSHOT/xml/features
opennms> features:install org.opennms.plugin.opennms-pluginmanager
