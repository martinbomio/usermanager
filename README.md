UserManager
====================
Manages users, implementing functions to login, register, delete, and update users within UMFlix. 

To use the project, follow the next steps:

1) Install the following maven projects before starting: 
modelstorage (https://github.com/marshhxx/modelstorage) 
authenticationhandler (https://github.com/haretche2/autenticationhandler)
 
2) Download sources for UserManager from git repository: 
git pull https://github.com/martinbomio/usermanager/

3) Depending on what you need to do, follow one of the next:
	
	
	A) To add a dependency to UserManager on another project:

		A.1) Run in command line, where the sources have been downloaded:
			mvn clean install 

		A.2) Add dependency to pom.xml, in dependencies copy: 
			<dependency>
    				<groupId>usermanager</groupId>
    				<artifactId>usermanager</artifactId>
    				<version>1.0-SNAPSHOT</version>		
			</dependency>
	
	
	B) To use it as an ejb or a webapp within tomEE:

		B.1) Change the pom.xml: under the tag <version>1.0-SNAPSHOT</version> add one of the following:
			To use it as an ejb:
    				<packaging>ejb</packaging>
			To use it as a webapp:
				<packaging>war</packaging>

		B.2) Run command: mvn clean install
		
		B.3) The correspondent jar or war file will be generated under ./target folder
		
		B.4) When running the ejb or webapp, make sure that the libraries which scope is “provided” in the dependencies are available in tomEE, and that those that are not provided and are included with the file do not create conflicts with already loaded libraries.
