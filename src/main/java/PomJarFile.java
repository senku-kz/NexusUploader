import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public class PomJarFile {
    private final RunMvnFromJava runMvnFromJava = new RunMvnFromJava();
    private final XmlFile xmlFile = new XmlFile();

    public PomJarFile() {
    }

    public void runMvnCommand(String repositoryId, String urlNexus, String pomFile, String jarFile){
        /*
            mvn deploy:deploy-file \
                -DgroupId=<group-id> \
                -DartifactId=<artifact-id> \
                -Dversion=<version> \
                -Dpackaging=<type-of-packaging> \
                -Dfile=<path-to-file> \
                -DrepositoryId=<id-to-map-on-server-section-of-settings.xml> \
                -Durl=<url-of-the-repository-to-deploy>

            mvn deploy:deploy-file -DgeneratePom=false
                -DrepositoryId=localnexus
                -Durl=http://localnexus/repository/mvn-local/
                -DpomFile=/home/user/dependency/ch.qos.logback.logback-classic-1.4.7.pom
                -Dfile=/home/user/dependency/ch.qos.logback.logback-classic-1.4.7.jar
        */

        try{
            String cmd = String.format("mvn deploy:deploy-file -DgeneratePom=false " +
                            "-DrepositoryId=%s " +
                            "-Durl=%s " +
                            "-DpomFile==%s " +
                            "-Dfile=%s",
                    repositoryId,
                    urlNexus,
                    pomFile,
                    jarFile);

            System.out.println(cmd);

            // Run and get the output.
            String outlist[] = runMvnFromJava.runCommand(cmd);
            // Print the output to screen character by character.
            // Safe and not very inefficient.
            for (String el:outlist){
                System.out.println(el);
            }
        } catch (IOException e) {
            System.err.println(e);
        }

    }

    public void workWithDirectory(String repositoryId, String urlNexus, String srcDir){
        Set<String> fileSet = xmlFile.getPomSet(srcDir);

        Iterator<String> itr = fileSet.iterator();
        while (itr.hasNext()) {
            System.out.println("===========================================");
            String srcFilePom = srcDir + "/" + itr.next();
            System.out.println(srcFilePom);

            String srcFileJar = srcFilePom.substring(0,srcFilePom.length()-3) + "jar";
            boolean c = new File(srcFileJar).isFile();
            System.out.printf("%s\t%s%n", c, srcFileJar);
            if (c) {
                this.runMvnCommand(repositoryId, urlNexus, srcFilePom, srcFileJar);
            }
        }
        System.out.println("===========================================");

    }

}
