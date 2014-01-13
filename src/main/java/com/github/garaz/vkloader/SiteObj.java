/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.garaz.vkloader;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 *
 * @author roman.petakh
 */
public class SiteObj {
    private URI uri;
    private List<String> masks;
        
    public SiteObj() {
        uri = null;
        masks = new ArrayList();
    }
        
    public SiteObj(URI uri, List<String> masks) {
        this.uri = uri;
        this.masks = masks;
    }
    
    void setURI(String string) throws URISyntaxException {  
        uri = SiteHelper.verifyUrl(string);
    }
        
    void setMasks(List list) {
        masks = list;
    }
        
    URI getURI() {
        return uri;
    }
        
    List<String> getMasks() {
        return masks;
    }
    
    SiteObj readFromXML(Element locRoot) throws URISyntaxException {
        uri = SiteHelper.verifyUrl(locRoot.valueOf("@URI"));
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