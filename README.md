UserManager
====================
Manages users, implementing functions to login, register, delete, and update users within UMFlix. 

To use the project, follow the next steps:

1) Install the following maven projects before starting: 
modelstorage (https://github.com/marshhxx/modelstorage) 
authenticationhandler (https://github.com/haretche2/autenticationhandler)
 
2) Download sources for UserManager from git repository: 
git pull https://github.com/martinbomio/usermanager/
	
3) To add a dependency to UserManager on another project:

	3.1) Run in command line, where the sources have been downloaded:
			mvn clean install 

	3.2) Add dependency to pom.xml, in dependencies copy: 
		<dependency>
 			<groupId>usermanager</groupId>
   			<artifactId>usermanager</artifactId>
			<version>1.0-SNAPSHOT</version>		
		</dependency>
	
4) To use the jar in another context run "mvn clean install" the correspondent file will be generated under ./target folder
	

5) To use it as a webapp:

	5.1) Change the pom.xml: under the tag <version>1.0-SNAPSHOT</version> add:
			<packaging>war</packaging>

	5.2) Run command: mvn clean install
		
	5.3) The correspondent war file will be generated under ./target folder

	

Note: when used, make sure that the libraries which scope is “provided” in the dependencies are available, and that those that are not provided and are included with the file do not create conflicts with already loaded libraries.
