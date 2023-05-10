import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PomJarFile {
    public void workWithDirectory(String repositoryId, String urlNexus, String srcDir){
        Set<String> fileSet = this.getPomSet(srcDir);

        for (String filename : fileSet) {
            System.out.println("===========================================");
            String srcFilePom = srcDir + "/" + filename;
            System.out.println(srcFilePom);

            String srcFileJar = srcFilePom.substring(0, srcFilePom.length() - 3) + "jar";
            boolean c = new File(srcFileJar).isFile();
            System.out.printf("%s\t%s%n", c, srcFileJar);
            if (c) {
                this.runMvnCommand(repositoryId, urlNexus, srcFilePom, srcFileJar);
            }
        }
        System.out.println("===========================================");

    }


    public Set<String> getPomSet(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .filter(name -> name.endsWith(".pom"))
                .collect(Collectors.toSet());
    }


    public void runMvnCommand(String repositoryId, String urlNexus, String pomFile, String jarFile){
        /*
            mvn deploy:deploy-file -DgeneratePom=false \
                -DrepositoryId=<id-to-map-on-server-section-of-settings.xml> \
                -Durl=<url-of-the-repository-to-deploy> \
                -DpomFile=<path-to-pom-file>  \
                -Dfile=<path-to-file>

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
                            "-DpomFile=%s " +
                            "-Dfile=%s",
                    repositoryId,
                    urlNexus,
                    pomFile,
                    jarFile);

            System.out.println(cmd);

            // Run and get the output.
            String[] outList = RunMvnFromJava.runCommand(cmd);

            // Print the output to screen character by character.
            // Safe and not very inefficient.
            for (String el:outList){
                System.out.println(el);
            }
        } catch (IOException e) {
            System.err.println(e);
        }

    }

}
