# Self Notes
- The artifact for this library is hosted on OSS Nexus repository, which is synced to Maven. The details related to OSS repository e.g. snapshot url, etc. can be found in the pom.xml file
- Other information like credentials for Nexus Repository manager, GPG executable et. is specified in maven settings.xml stored at some secure location somewhere
- Credentials for Nexus repository manager (https://oss.sonatype.org/) is same as that for JIRA account
- Jira ticket raised initially for creating the repository for group id (com.github.easy-develop) is https://issues.sonatype.org/browse/OSSRH-34302
- For making the "Release", version in pom.xml should not end in SNAPSHOT. So, the "master" branch has version like, "1.0.0-SNAPSHOT" and for release, a separate branch is made which has "SNAPSHOT" removed from the version (e.g. "1.0.0")
- Command used for release is something like:
mvn -s ../properties/src/main/resources/maven_settings.xml -P release clean deploy