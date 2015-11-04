# The OSGi Licence Manager  org.opennms.licencemanager

This project builds an OSGi licence manager bundle which can be deployed in a Karaf container.

The OSGi Licence Manager provides services to make it easy for a vendor/developer to use a shopping cart
 to create licences for a customer account. It also provides services to make it easy for
 customer/cient OSGi modules to request and verify a locally stored licence.
 
 Both vendor and client services are provided by the same bundle but the intention is that separate instances
 are installed in the Vendor's and Client's systems.

## Vendor Services for Licence Provider Shopping Cart
* Licence Publisher - provides an API for a shopping cart to request the generation of a new licence
* Product Publisher - provides an api for OSGi bundles to describe their product
function for the shopping cart in human readable form

The Licence and Product Publisher services provide commands which can be accessed through the Karaf command line
- licence-pub:createlicence         creates a new licence for given metadata xml (surround xml in 'quotes')
- licence-pub:getlicencemetadata    Gets the licence metadata for installed licence specification for a given product id
- licence-pub:getlicencespec        Gets the licence specification for a given product id
- licence-pub:list                  lists licence metadata for installed licence specifications (i.e. not the keys)
- licence-pub:listspecs             lists installed licence specifications
- product-pub:addproductspec        Adds product spec to product publisher for productId
- product-pub:getproductspec        gets product spec from product publisher for productId
- product-pub:list                  lists product specifications installed in product publisher
- product-pub:removeproductspec     removes product specification from product publisher

#### Services for Customer/Client's Karaf Container
* Licence Manager - provides an api for OSGi bundles to verify a licence in a customer's container
* Product Register - provides an api for installed OSGi bundles to describe their product
function in human readable form

The licence manager provides commands which can be accessed through the Karaf command line
- licence-mgr:addlicence            adds licence for productId
- licence-mgr:checksumforstring     creates and adds a checksum to a given string
- licence-mgr:getlicence            returns licence string installed for productId
- licence-mgr:getsystemid           Get System Instance ID installed for licence manager
- licence-mgr:list                  Lists installed licences
- licence-mgr:makesystemid          Make and install a new System Instance (systemId) for licence manager
- licence-mgr:removelicence         Removes licence for selected productId
- licence-mgr:setsystemid           Set Instance ID of Karaf licence manager
- product-reg:addproductspec        adds product spec to product registry for productId
- product-reg:getproductspec        gets product spec from  product registry for productId
- product-reg:list                  lists product specifications installed in product registry
- product-reg:removeproductspec     removes product specification from product register

## ReST and HTML test page
The module provides a ReST interface which reflects the commands available through the command line.

This is accessed at

'http://-domain-:-port-/pluginmgr/rest

For example the following get command lists the installed products

http://localhost:8181/licencemgr/rest/product-reg/list

A test page is provided which illustrates the use of the ReST interface.

(The source of this page includes javascript examples for handling post XML messages).

http://localhost:8181/licencemgr/diagnostics/licence-mgr-rest-diagnostics.html

## Installation in stand alone Karaf

1. Install Karaf
Download and install the apache-karaf-2.3.9 Karaf distribution

(http://karaf.apache.org/index/community/download/karaf-2.3.9-release.html)

The plugin should work with later distributions but is tested against this one because it is the version used in OpenNMS.

2. Start Karaf
Once installed cd to the /bin directory and start Karaf in a terminal window using the command

```
karaf
```

You should see the Karaf command prompt in the terminal

3. install the http and http-whiteboard services
In the command window type

```
  features:install http
  features:install http-whiteboard
```
  
Navigate to http://localhost:8181/ and you will see a 404 error. This is OK



2. Install dependencies
Drop the following Jersey jars into the karaf /deploy directory (Again these match versions used in OpenNMS)
* jersey-core-1.18.jar
* jersey-server-1.18.1.jar
* jersey-servlet-1.18.jar

These can be picked up from the maven repo here 
* http://mvnrepository.com/artifact/com.sun.jersey/jersey-core/1.18
* http://mvnrepository.com/artifact/com.sun.jersey/jersey-server/1.18.1
* http://mvnrepository.com/artifact/com.sun.jersey/jersey-servlet/1.18

once deployed, in the Karaf consol type
```
list
```
You should see the three bundles listed as state Active

3. Install Licence Manager
Drop the org.opennms.licencemanager-VERSION.jar into the deploy directory

in the Karaf Consol type
```
list
```
you should see
```
karaf@root> list
START LEVEL 100 , List Threshold: 50
   ID   State         Blueprint      Level  Name
 54   Active                            80  jersey-core (1.18.0)
   55   Active                          80  jersey-server (1.18.1)
   56   Active                          80  jersey-servlet (1.18.0)
   86   Active        Created           80  org.opennms.licencemanager (1.0.0.SNAPSHOT)

```

4. Use Test Page

Navigate to http://localhost:8181/pluginmgr/diagnostics/licence-mgr-rest-diagnostics.html
to see a list of commands

5. Other notes 
When compiling this module, the tests can run very slow if there is not enough entropy in the build machine. 
This became apparent when switching to java 8 on a centos 7 VM

Java tries to start the Cypher packages using the settings in 
'${java.home}/jre/lib/security/java.security' 

which point to the linux random source
'securerandom.source=file:/dev/./urandom'

Unfortunately on a virtual machine, this may not generate enough random data. 
Entropy levels can be seen using 
'cat /proc/sys/kernel/random/entropy_avail' 

The problem can be solved by installing and starting the havegrd package from EPEL
See instructions here: https://www.digitalocean.com/community/tutorials/how-to-setup-additional-entropy-for-cloud-servers-using-haveged


