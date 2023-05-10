public class Main {
    /*
    * mvn dependency:copy-dependencies -Dmdep.copyPom=true
    * mvn dependency:copy-dependencies -Dmdep.prependGroupId=true -Dmdep.copyPom=true
    * mvn dependency:copy-dependencies -Dmdep.prependGroupId=true -Dmdep.copyPom=true -DoutputDirectory=dependencies
    * */
    private static final PomJarFile pomJarFile = new PomJarFile();

    public static void main(String[] args){
        String repositoryId = System.getProperty("repositoryId");
        String urlNexus = System.getProperty("urlNexus");
        String srcDir = System.getProperty("srcDir");

        for (String arg : args) {
            if (repositoryId == null && arg.startsWith("-DrepositoryId=")) repositoryId = arg.substring(15);
            if (urlNexus == null && arg.startsWith("-DurlNexus=")) urlNexus = arg.substring(11);
            if (srcDir == null && arg.startsWith("-DsrcDir=")) srcDir = arg.substring(9);
        }

        if (repositoryId == null) repositoryId = "localnexus";
        if (urlNexus == null) urlNexus = "http://localnexus/repository/mvn-local/";
        if (srcDir == null) srcDir = System.getProperty("user.home") + "/dependency";

        System.out.println("Source directory is: " + srcDir);
        System.out.println("Nexus url is: " + urlNexus);

        //XmlFile xmlFile = new XmlFile(urlNexus, srcDir);
        pomJarFile.workWithDirectory(repositoryId, urlNexus, srcDir);
        System.out.println(" Program completed successfully!");
    }

}