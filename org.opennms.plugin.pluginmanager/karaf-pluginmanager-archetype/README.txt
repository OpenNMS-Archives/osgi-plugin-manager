This project provides a maven archetype to generate licences for use with the licence manager

To build the licence archetype generator use 
mvn clean install

After Building the archetype, to create a project move to an empty folder and use a command like

mvn archetype:generate -DarchetypeCatalog=local -DarchetypeGroupId=org.opennms.plugins \
-DarchetypeArtifactId=karaf-pluginmanager-archetype \
-DarchetypeVersion=1.0-SNAPSHOT \
-DgroupId=org.opennms.plugins \
-DartifactId=myproject \
-Dpackage=org.opennms.plugins.myproject.packagename
 
 Where
-DarchetypeVersion=xxx is the version of this archetype (e.g. 1.0.2)
-DgroupId=org.opennms.plugins is the maven group id of your generated project
-DartifactId=myproject is the maven artifact id of your generated project
-Dpackage=org.opennms.plugins.myproject.packagename is the route java package in which the generated licence artifacts will be placed

