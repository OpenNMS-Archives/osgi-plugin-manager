This project provides a maven archetype to generate licences for use with the licence manager

To build the licence archetype generator use 
mvn clean install

After Building the archetype, to create a project move to an empty folder and use

mvn archetype:generate -DarchetypeCatalog=local -DarchetypeGroupId=org.opennms \
-DarchetypeArtifactId=karaf-pluginmanager-archetype \
-DarchetypeVersion=0.0.1-SNAPSHOT \
-DgroupId=org.opennms.project \
-DartifactId=myproject \
-Dpackage=org.opennms.project.packagename
 
 Where -DgroupId=org.opennms.project  is the group id of your generated project
-DartifactId=myproject is the artifact id of your generated project
-Dpackage=org.opennms.project.packagename is the route package in which the generated licence artifacts will be placed