# The OSGi Karaf Plugin Manager  
org.opennms.plugin.pluginmanager.karaf-pluginmanager

To install the plugin manager stand alone in a karaf instance use the following steps.

(This works based upon apache-karaf-2.4.0 - the version used in OpenNMS Horizon 18)

a) edit <karaf home>/etc/org.ops4j.pax.mvn.cfg

b)  add the opennms repository line
~~~~
(http://maven.opennms.org/content/groups/opennms.org-release)
~~~~
to the 
~~~~
org.ops4j.pax.url.mvn.repositories
~~~~
property as below

~~~~
org.ops4j.pax.url.mvn.repositories= \
    http://repo1.maven.org/maven2@id=central, \
    https://maven.java.net/content/groups/public/, \
    http://svn.apache.org/repos/asf/servicemix/m2-repo@id=servicemix, \
    http://repository.springsource.com/maven/bundles/release@id=springsource.release, \
    http://repository.springsource.com/maven/bundles/external@id=springsource.external, \
    https://oss.sonatype.org/content/repositories/releases/@id=sonatype, \
    http://maven.opennms.org/content/groups/opennms.org-release
~~~~

c) From the karaf consol type the following commands
(konsol is opened by default if you start karaf using '<karaf home>/bin/karaf')

~~~~
karaf@root>features:addurl mvn:org.opennms.plugins/org.opennms.plugin.pluginmanager.karaf-pluginmanager/1.0.2/xml/features
karaf@root> features:install org.opennms.plugin.pluginmanager.karaf-pluginmanager
Licence Manager Starting
Licence Manager successfully loaded licences from file=/home/admin/devel/karaf/apache-karaf-2.4.0/./etc/pluginLicenceData.xml
Licence Manager system set to not load remote licences
Licence Manager Started
Plugin Manager Rest App starting.
Registered Product Specification for productId=org.opennms.plugin.pluginmanager.karaf-pluginmanager/1.0.2
Plugin Manager Starting
Plugin Manager Successfully loaded historic data from file=/home/admin/devel/karaf/apache-karaf-2.4.0/./etc/pluginManifestData.xml
Plugin Manager Started
karaf@root> 
~~~~

d) browse to http://localhost:port/admin/plugin-manager

note that port is by default 8181 and is set in 
~~~~
<karaf home>/etc/org.ops4j.pax.web.cfg
~~~~
