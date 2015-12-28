package Parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
/**
  *class parse xml document with Gun item using SAX principle
  */
public class MySAXParser extends GunParser {
    SAXParserFactory parserFactor;
    SAXParser parser;
    SAXHandler handler;

    public void initParse(String xmlDocument) throws Exception {
        //initialize document and SAX handler
        parserFactor = SAXParserFactory.newInstance();
        handler = new SAXHandler();
    }

    /**
     * method parse data from xmlDocument
     *
     * @param xmlDocument user want to parse
     * @throws Exception
     */
    public void parsing(String xmlDocument) throws Exception {
        initParse(xmlDocument);
        parser = parserFactor.newSAXParser();
        parser.parse(ClassLoader.getSystemResourceAsStream(xmlDocument),
                handler);
    }

    /**
     * The Handler for SAX Events.
     */
    class SAXHandler extends DefaultHandler {
        String tagContent = null;

        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            tagContent = String.copyValueOf(ch, start, length).trim();
        }

        @Override
        //Triggered when the start of tag is found.
        public void startElement(String uri, String localName,
                                 String qName, Attributes attributes)
                throws SAXException {

            switch (qName) {
                //Create a new Employee object when the start tag is found
                case "item":
                    if(currentGunTTC != null)
                        currentGunItem.setTTC(currentGunTTC);
                    else currentGunTTC = gunFactory.createGunItemTTC();
                    if(currentGunItem == null)
                        currentGunItem = gunFactory.createGunItem();
                    if(guns == null) {
                        guns = gunFactory.createGun();
                        guns.getItem();
                        currentGunItem.setId(attributes.getValue("id"));
                    }
                    else currentGunItem.setId(attributes.getValue("id"));
                    break;
            }
        }

        @Override
        public void endElement(String uri, String localName,
                               String qName) throws SAXException {
            switch (qName) {
                //Add the employee to list once end tag is found
                case "item":
                    guns.item.add(currentGunItem);
                    currentGunItem = gunFactory.createGunItem();
                    currentGunTTC = gunFactory.createGunItemTTC();
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
        }


    }

}