# Set up

1. Download and install a JDK 8 JVM (minimum supported version 8u131)
2. Download and install IntelliJ Community Edition (supported versions 2017.x and 2018.x)
3. Open IntelliJ. From the splash screen, click `Import Project`, select the `Project`
folder and click `Open`
4. Select `Import project from external model > Gradle > Next > Finish`
5. Click `File > Project Structure…` and select the Project SDK (Oracle JDK 8, 8u131+)

    i. Add a new SDK if required by clicking `New…` and selecting the JDK’s folder

6. Open the `Project` view by clicking `View > Tool Windows > Project`
7. Run the test in `src/test/java/java_bootcamp/ProjectImportedOKTest.java`. It should pass!

# Building project

In the gradle console type command `gradle deployNodes1 deployNodes2`.

Or in IntelliJ Gradle window run tasks deployNodess1 and deployNodes2 
in Project/tasks/other gradle commands folder.

# Running project

In the file runNodes.bat set JAVA_HOME variable to java 1.8 location
and SOURCEBUILD to the location of Project. Finally run runNodes.bat
file in command console.