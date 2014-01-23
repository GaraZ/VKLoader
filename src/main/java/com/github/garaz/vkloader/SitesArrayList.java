package com.github.garaz.vkloader;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 *
 * @author GaraZ
 */
public class SitesArrayList<E extends SiteObj> extends ArrayList<E> implements XmlList<E> {
    
    @Override
    public void writeToXML(Element locRoot) {
        Element element = locRoot.addElement("Sites");
        int size = size();
        for(int i = 0; i < size; i++ ) {
            this.get(i).writeToXML(element);
        }
    }

    @Override
    public SitesArrayList readFromXML(Element locRoot) {
        clear();
        List<Node> list =locRoot.selectNodes("Sites/*");
        for(Node node :list) {
            try {
                add((E) new SiteObj().readFromXML((Element) node));
            } catch (URISyntaxException e) {
                // ignore
            }
        }
        return this;
    }    
}