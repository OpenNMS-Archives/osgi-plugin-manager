# Simple script to create repositories in Nexus

# see https://repository.sonatype.org/nexus-restlet1x-plugin/default/docs/index.html -->
# http://www.sonatype.org/nexus/2015/08/13/using-the-rest-api-in-nexus-2/ -->
# 

curl -v -H  POST http://admin:admin123@localhost:28081/nexus/service/local/repositories \
    -H "Content-Type: application/xml" \
    --data " \
<repository> \
  <data> \
    <id>osgi-plugins</id> \
    <name>osgi-plugins</name> \
    <exposed>true</exposed> \
    <repoType>hosted</repoType> \
    <repoPolicy>RELEASE</repoPolicy> \
    <providerRole>org.sonatype.nexus.proxy.repository.Repository</providerRole> \
    <provider>maven2</provider> \
    <format>maven2</format> \
  </data> \
</repository> "

curl -v -H  POST http://admin:admin123@localhost:28081/nexus/service/local/repositories \
    -H "Content-Type: application/xml" \
    --data " \
<repository> \
  <data> \
    <id>osgi-plugins-snapshots</id> \
    <name>osgi-plugins-snapshots</name> \
    <exposed>true</exposed> \
    <repoType>hosted</repoType> \
    <repoPolicy>SNAPSHOT</repoPolicy> \
    <providerRole>org.sonatype.nexus.proxy.repository.Repository</providerRole> \
    <provider>maven2</provider> \
    <format>maven2</format> \
  </data> \
</repository> "

curl -v -H  POST http://admin:admin123@localhost:28081/nexus/service/local/repositories \
    -H "Content-Type: application/xml" \
    --data " \
<repository> \
  <data> \
    <id>osgi-plugins-licence-specs-snapshots</id> \
    <name>osgi-plugins-licence-specs-snapshots</name> \
    <exposed>true</exposed> \
    <repoType>hosted</repoType> \
    <repoPolicy>SNAPSHOT</repoPolicy> \
    <providerRole>org.sonatype.nexus.proxy.repository.Repository</providerRole> \
    <provider>maven2</provider> \
    <format>maven2</format> \
  </data> \
</repository> "


curl -v -H  POST http://admin:admin123@localhost:28081/nexus/service/local/repositories \
    -H "Content-Type: application/xml" \
    --data " \
<repository> \
  <data> \
    <id>osgi-plugins-licence-specs</id> \
    <name>osgi-plugins-licence-specs</name> \
    <exposed>true</exposed> \
    <repoType>hosted</repoType> \
    <repoPolicy>RELEASE</repoPolicy> \
    <providerRole>org.sonatype.nexus.proxy.repository.Repository</providerRole> \
    <provider>maven2</provider> \
    <format>maven2</format> \
  </data> \
</repository> "
