package Parsers;

/**
 * Created by HR 24.10.2014.
 */

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.File;
import java.io.FileWriter;
import java.util.Collections;

abstract class GunParser{
    //guns.Item will be contained data about all guns from @"Gun.xml"
    public Gun guns;
    //factory for creating Gun object
    protected ObjectFactory gunFactory = new ObjectFactory();
    //current Gun item parsing at the moment
    protected static Gun.Item currentGunItem;
    //current Gun item TTC attribute parsing at the moment
    protected static Gun.Item.TTC currentGunTTC;

    /**
     * method for initializing any
     * instance or smth else
     * @param xmlDocument
     * @throws Exception
     */
    private void initParse(String xmlDocument) throws Exception{};

    /**
     * main method for parsing @param
     * @param xmlDocument
     * @throws Exception
     */
    public abstract void parsing(String xmlDocument)
                            throws Exception;
}
/**
  *class parse xml document with Gun item using DOM principle
  */
class DOMParser extends GunParser{
    //xml document user want to parse
    Document document;

    /**
     * get content from xml tree node TTC
     * @param node of xml tree
     */
    private void parseTTC(Node node){
        NodeList childNodes = node.getChildNodes();
        int childNodesLenght = childNodes.getLength();
        for (int j = 0; j < childNodesLenght; ++j) {
            Node tagNode = childNodes.item(j);
            //Identifying the child tag of gunItemTTC encountered.
            if (tagNode instanceof Element) {
                String content = tagNode.getLastChild().
                        getTextContent().trim();
                switch (tagNode.getNodeName()) {
                    case "Shooting":
                        currentGunTTC.setDistance(content);
                        break;
                    case "EffectiveDistance":
                        currentGunTTC.setEffectiveDistance(
                                        Short.parseShort(content));
                        break;
                    case "Clip":
                        currentGunTTC.setClip(content);
                        break;
                    case "Optics":
                        currentGunTTC.setOptics(content);
                }
            }
        }
    }
    /**
     * get content from xml tree node item
     * @param node of xml tree
     */
    private void getContentGunAttributes(Node node) {
        //Identifying the child tag of gun encountered.
        if (node instanceof Element) {
            String content = node.getLastChild().
                    getTextContent().trim();
            switch (node.getNodeName()) {
                case "Model":
                    currentGunItem.setModel(content);
                    break;
                case "Handy":
                    currentGunItem.setHandy(content);
                    break;
                case "Origin":
                    currentGunItem.setOrigin(content);
                    break;
                case "TTC":
                    //this node - TTC complex type(hard to parse here)
                    currentGunTTC = gunFactory.createGunItemTTC();
                    parseTTC(node);
                    currentGunItem.setTTC(currentGunTTC);
                    break;
                case "Material":
                    currentGunItem.setMaterial(content);
            }
        }
    }

    /**
     * parse Gun item and store in GunList
     * @param node of xml tree
     */
    private void getContentGun(Node node) {
        //if node(tag) contains data we need to fetch
        if (node instanceof Element) {
            if(guns.item == null)
                guns.getItem();
            //gunItem will be contained data about ONE gun from @"Gun.xml"
            currentGunItem = gunFactory.createGunItem();
            currentGunItem.setId(node.getAttributes().
                             getNamedItem("id").getNodeValue());
            NodeList childNodes = node.getChildNodes();
            int childNodesLenght = childNodes.getLength();
            for (int j = 0; j < childNodesLenght; ++j) {
                Node tagNode = childNodes.item(j);
                getContentGunAttributes(tagNode);
            }
            //add one gun structure(item)
            guns.item.add(currentGunItem);
        }
    }

    /**
     * initialize xml document
     * @param xmlDocument user want to Parse
     * @throws Exception
     */
    private void initParse(String xmlDocument) throws Exception {
        //Get the DOM Builder Factory
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();

        //Get the DOM Builder
        DocumentBuilder builder = factory.newDocumentBuilder();

        //Load and Parse the XML document
        //document contains the complete XML as a Tree.
        document = builder.parse(
                ClassLoader.getSystemResourceAsStream(xmlDocument));

    }

    /**
     * method parse data from xmlDocument
     * @param xmlDocument user want to parse
     * @throws Exception
     */
    public void parsing(String xmlDocument) throws Exception {
        //Iterating through the nodes and extracting the data.
        initParse(xmlDocument);
        //get xml nodes (tags)
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        int nodeListLenght = nodeList.getLength();
        if(nodeListLenght != 0)
            guns = gunFactory.createGun();
        for (int i = 0; i < nodeListLenght; ++i) {
            //We have encountered an <item> tag.
            Node node = nodeList.item(i);
            //get data
            getContentGun(node);
        }
    }
}

public class ParserDemonstrate {
    public static void main(String[] arg){
        DOMParser domParser = new DOMParser();
        MySAXParser saxParser = new MySAXParser();
        MyStaxParser staxParser = new MyStaxParser();

        try {
            domParser.parsing("gun.xml");
            print(domParser.guns, "DOM parser" +
                    "\n*******************************");
        } catch (Exception e) {
        e.printStackTrace();
        }
        try{
            saxParser.parsing("gun.xml");
            print(saxParser.guns, "Sax parser" +
                    "\n*******************************");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try{
            staxParser.parsing("gun.xml");
            print(staxParser.guns, "StAX parser" +
                    "\n********************************");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //sorting by effective distance
        Collections.sort(staxParser.guns.getItem(), new GunComparator());
        //staxParser.guns.getItem().sort(new GunComparator());
        try {
            print(staxParser.guns, "Stax sorted" +
                    "\n********************************");
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    public static void print(Gun gun, String comment)
            throws  Exception{
        File file = new File("gun.txt");
        FileWriter writer = new FileWriter(file, true);
        writer.write(comment + "\n");
        System.out.println(comment);
        List<Gun.Item> gunItem = gun.getItem();
        for(Gun.Item g : gunItem){
            //File
            writer.write("Номер " + g.getId() + '\n');
            writer.write("Модель " + g.getModel() + '\n');
            writer.write("Країна " + g.getOrigin() + '\n');
            writer.write("Використання " + g.getHandy() + '\n');
            writer.write("Дальність бою " + g.getTTC().getDistance() + '\n');
            writer.write("Ефективна дальність " +
                           g.getTTC().getEffectiveDistance() + '\n');
            writer.write("Обойма " + g.getTTC().getClip() + '\n');
            writer.write("Оптика " + g.getTTC().getOptics() + '\n');
            writer.write("Матеріал " + g.getMaterial() + '\n');
            writer.write('\n');
            //standart
            
            System.out.println("Номер " + g.getId());
            System.out.println("Модель " + g.getModel());
            System.out.println("Країна " + g.getOrigin());
            System.out.println("Використання " + g.getHandy());
            System.out.println("Дальність бою " + g.getTTC().getDistance());
            System.out.println("Ефективна дальність " +
                           g.getTTC().getEffectiveDistance());
            System.out.println("Обойма " + g.getTTC().getClip());
            System.out.println("Оптика " + g.getTTC().getOptics());
            System.out.println("Матеріал " + g.getMaterial());
            System.out.println("");
        }
        writer.close();
    }
}
