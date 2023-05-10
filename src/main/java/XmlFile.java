import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XmlFile {
    private String urlNexus;
    private String srcDir;

    private RunMvnFromJava runMvnFromJava = new RunMvnFromJava();

    public XmlFile(String urlNexus, String srcDir) {
        this.urlNexus = urlNexus;
        this.srcDir = srcDir;
        PomAttributes pomAttributes;

        Set<String> fileSet = this.getPomSet(srcDir);

        Iterator<String> itr = fileSet.iterator();
        while (itr.hasNext()) {
            System.out.println("===========================================");
            String srcFilePom = srcDir + "/" + itr.next().toString();
            System.out.println(srcFilePom);
            pomAttributes = this.getAttribute(srcFilePom);

            String srcFileJar = srcFilePom.substring(0,srcFilePom.length()-3) + "jar";
            boolean c = new File(srcFileJar).isFile();
            System.out.println(String.format("%s\t%s", c, srcFileJar));
            if (c) {
                this.runMvnCommand(this.urlNexus, srcFileJar, pomAttributes);
            }
        }
        System.out.println("===========================================");

    }

    public Document readXMLDocumentFromFile(String fileNameWithPath) throws Exception {

        //Get Document Builder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        //Build Document
        Document document = builder.parse(new File(fileNameWithPath));

        //Normalize the XML Structure; It's just too important !!
        document.getDocumentElement().normalize();

        return document;
    }
    public Set<String> listFilesUsingJavaIO(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    public Set<String> getPomSet(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .filter(file -> file.getName().endsWith(".pom"))
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    public PomAttributes getAttribute(String srcFile){
        PomAttributes pomAttributes = new PomAttributes();
        try {
            Document document = this.readXMLDocumentFromFile(srcFile);
            Element rootElement = document.getDocumentElement();

            //groupId = this.getAttributeByElement("groupId", rootElement);
            //artifactId = this.getAttributeByElement("artifactId", rootElement);
            //version = this.getAttributeByElement("version", rootElement);

            pomAttributes.setGroupId(this.getAttributeByElement("groupId", rootElement));
            pomAttributes.setArtifactId(this.getAttributeByElement("artifactId", rootElement));
            pomAttributes.setVersion(this.getAttributeByElement("version", rootElement));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        //System.out.println(groupId + "\t" + artifactId + "\t" + version);
        System.out.println(String.format("GroupId=%s\tArtifactId=%s\tVersion=%s",
                pomAttributes.getGroupId(),
                pomAttributes.getArtifactId(),
                pomAttributes.getVersion()));
//        this.runMvnCommand(this.urlNexus, srcFile, groupId, artifactId, version);
        return pomAttributes;
    }
    public String getAttributeByElement(String expression, Element rootElement){
        String res = null;
                NodeList nodeList = rootElement.getElementsByTagName(expression);
        if (nodeList.getLength()>1){
//            System.out.println("Find two or more same nodes for one expression: " + expression);
            res = this.getAttributeByNodeInRoot(expression, rootElement);
            if (res == null){
                res = this.getAttributeByNodeInParent(expression, rootElement);
            }
//            System.out.println(res);
        } else if (nodeList.getLength()==1) {
//            System.out.println(nodeList.item(0).getTextContent());
            res = nodeList.item(0).getTextContent();
        } else {
            System.out.println("Can't find expression: " + expression);
        }
        return res;
    }

    public String getAttributeByNodeInRoot(String expression, Element rootElement){
        NodeList nodeList = rootElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node nNode = nodeList.item(i);
            if (nNode instanceof Element) {
                if (expression.equals(nNode.getNodeName())){
                    return nNode.getTextContent();
                }
            }
        }
        return null;
    }

    public String getAttributeByNodeInParent(String expression, Element rootElement){
//        System.out.println("getAttributeByNodeInParent");
        NodeList nodeList = rootElement.getElementsByTagName("parent");
        if (nodeList.getLength()==1){
            NodeList nodeListParent = nodeList.item(0).getChildNodes();
            for (int i = 0; i < nodeListParent.getLength(); i++) {
                Node nNode = nodeListParent.item(i);
                if (nNode instanceof Element) {
                    if (expression.equals(nNode.getNodeName())){
                        return nNode.getTextContent();
                    }
                }
            }
        }
        return null;
    }

    public void runMvnCommand(String urlNexus, String srcFile, PomAttributes pomAttributes){
        /*
            mvn deploy:deploy-file \
                -DgroupId=<group-id> \
                -DartifactId=<artifact-id> \
                -Dversion=<version> \
                -Dpackaging=<type-of-packaging> \
                -Dfile=<path-to-file> \
                -DrepositoryId=<id-to-map-on-server-section-of-settings.xml> \
                -Durl=<url-of-the-repository-to-deploy>
        */

        try{
            String cmd = String.format("mvn deploy:deploy-file " +
                    "-DgroupId=%s " +
                    "-DartifactId=%s " +
                    "-Dversion=%s " +
                    "-Dpackaging=jar " +
                    "-Dfile=%s " +
                    "-DrepositoryId=nexus " +
                    "-Durl=%s",
                    pomAttributes.getGroupId(),
                    pomAttributes.getArtifactId(),
                    pomAttributes.getVersion(),
                    srcFile, urlNexus);
            // Run and get the output.
            String outlist[] = runMvnFromJava.runCommand(cmd);
            // Print the output to screen character by character.
            // Safe and not very inefficient.
            for (int i = 0; i < outlist.length; i++)
                System.out.println(outlist[i]);
        } catch (IOException e) {
            System.err.println(e);
        }

    }
}
