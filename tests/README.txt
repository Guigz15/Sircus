This text explains the deployment of the tests that have been done.

First the integration tests are in the controller package. Concerning units tests, they are in the model package.

To launch the application without launching the tests, you have to modify the pom.xml file.

Where it is written:
 <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>3.0.0-M5</version>
        <configuration>
            <skipTests>false</skipTests>
        </configuration>
</plugin>

Replace the value of "skipTests" with true.

