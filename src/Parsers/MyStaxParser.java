package Parsers;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 *class parse xml document with Gun item using StAX principle
 */
public class MyStaxParser extends  GunParser{
    String tagContent = null;
    XMLInputFactory factory = XMLInputFactory.newInstance();
    //reader of xml document user want to parse
    XMLStreamReader reader;

    //guns.Item will be contained data about all guns from @"Gun.xml"
    public Gun guns;

    private void initParse(String xmlDocument) throws Exception{
        reader = factory.createXMLStreamReader(
                ClassLoader.getSystemResourceAsStream(xmlDocument));
    }
    /**
     * parse Gun item and store in gunList
     * @param xmlDocument user want to parse
     * @throws XMLStreamException
     */
    public void parsing(String xmlDocument) throws Exception {
        initParse(xmlDocument);
        while (reader.hasNext()) {
            int event = reader.next();
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    if ("item".equals(reader.getLocalName())) {
                        //if we read TTC data
                        if(currentGunTTC != null)
                            currentGunItem.setTTC(currentGunTTC);
                        //we haven't read any data so we need to create TTC
                        else currentGunTTC = gunFactory.createGunItemTTC();
                        //the same as TTC previously
                        if(currentGunItem != null)
                            guns.item.add(currentGunItem);
                        else currentGunItem =  gunFactory.createGunItem();
                        //get if
                        currentGunItem.setId(reader.getAttributeValue(0));
                    }
                    if ("Gun".equals(reader.getLocalName())){
                            guns = gunFactory.createGun();
                            guns.getItem();
                        }
                    break;

                case XMLStreamConstants.CHARACTERS:
                    tagContent = reader.getText().trim();
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    switch (reader.getLocalName()) {
                        case "item":
                            currentGunItem = gunFactory.createGunItem();
                            break;
                        //For all other end tags the employee has to be updated.
                        case "Model":
                            currentGunItem.setModel(tagContent);
                            break;
                        case "Handy":
                            currentGunItem.setHandy(tagContent);
                            break;
                        case "Origin":
                            currentGunItem.setOrigin(tagContent);
                            break;
                        case "Shooting":
                            currentGunTTC.setDistance(tagContent);
                            break;
                        case "EffectiveDistance":
                            currentGunTTC.setEffectiveDistance(
                                        Short.parseShort(tagContent));
                            break;
                        case "Clip":
                            currentGunTTC.setClip(tagContent);
                            break;
                        case "Optics":
                            currentGunTTC.setOptics(tagContent);
                            break;
                        case "Material":
                            currentGunItem.setMaterial(tagContent);
                    }
                    break;

                case XMLStreamConstants.START_DOCUMENT:
                    break;
            }

        }
    }
}