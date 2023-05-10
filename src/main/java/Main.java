import java.util.Properties;

public class Main {
    /*
    * mvn dependency:copy-dependencies -Dmdep.copyPom=true
    * mvn dependency:copy-dependencies -Dmdep.prependGroupId=true -Dmdep.copyPom=true
    * mvn dependency:copy-dependencies -Dmdep.prependGroupId=true -Dmdep.copyPom=true -DoutputDirectory=dependencies
    * */


    public static void main(String[] args){
        String urlNexus = System.getProperty("urlNexus");
        String srcDir = System.getProperty("srcDir");

        if (args.length>0){
            for (String arg:args) {
                if (urlNexus == null && arg.startsWith("-DurlNexus=")) urlNexus = arg.substring(11);
                if (srcDir == null && arg.startsWith("-DsrcDir=")) srcDir = arg.substring(9);
            }
        }

        if (urlNexus == null) urlNexus = "http://localNexus/maven-local/";
        if (srcDir == null) srcDir = System.getProperty("user.home") + "/dependency";

        System.out.println("Source directory is: " + srcDir);
        System.out.println("Nexus url is: " + urlNexus);

        XmlFile xmlFile = new XmlFile(urlNexus, srcDir);
        System.out.println(" Program completed successfully!");
    }
}