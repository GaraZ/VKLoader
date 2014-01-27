package com.github.garaz.vkloader;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 *
 * @author GaraZ
 */
public class SiteObj {
    @XmlElement
    private URI uri;
    @XmlElement
    private List<String> masks;
        
    public SiteObj() {
        uri = null;
        masks = new ArrayList();
    }
        
    public SiteObj(URI uri, List<String> masks) {
        this.uri = uri;
        this.masks = masks;
    }
    
    public void setURI(String string) throws URISyntaxException {  
        uri = SiteManager.verifyUrl(string);
    }
        
    public void setMasks(List list) {
        masks = list;
    }
        
    public URI getURI() {
        return uri;
    }
        
    public List<String> getMasks() {
        return masks;
    }
    
    SiteObj readFromXML(Element locRoot) throws URISyntaxException {
        uri = SiteManager.verifyUrl(locRoot.valueOf("@URI"));
        masks = new ArrayList();
        List<Node> list  = locRoot.selectNodes("Mask");
        for(Node node: list) {
            masks.add(node.getText());
        }
        return this;
    }
    
    void writeToXML(Element locRoot) {
        Element element = locRoot.addElement("Site").addAttribute("URI", uri.toString());
        int size = masks.size();
        for(int i = 0; i < size; i++ ) {
            Element mask = element.addElement("Mask");
            mask.addText(masks.get(i));
        }
    }
    
    @Override
    public String toString() {
        return uri.toString();
    }
}