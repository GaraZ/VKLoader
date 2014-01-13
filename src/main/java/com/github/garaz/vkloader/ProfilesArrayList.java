package com.github.garaz.vkloader;

import java.util.ArrayList;
import java.util.List;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 *
 * @author GaraZ
 */
public class ProfilesArrayList <E extends ProfileObj> extends ArrayList<E> implements CustomList<E> {
    
    @Override
    public void writeToXML(Element locRoot) throws Exception {
        Element element = locRoot.addElement("Profiles");
        int size = size();
        StringBuilder err = new StringBuilder();
        for(int i = 0; i < size; i++ ) {
            try {
                this.get(i).writeToXML(element);
            } catch(Exception e) {
                err.append(e.getMessage())
                        .append(System.getProperty("line.separator"));
            }
            if (!err.toString().isEmpty()) {
                throw new Exception(err.toString());
            }
        }
    }

    @Override
    public ProfilesArrayList readFromXML(Element locRoot) throws Exception {
        clear();
        StringBuilder err = new StringBuilder();
        List<Node> list =locRoot.selectNodes("Profiles/*");
        for(Node node :list) {
            try {
                add((E) new ProfileObj().readFromXML((Element) node));
            } catch(Exception e) {
                err.append(e.getMessage())
                        .append(System.getProperty("line.separator"));
            }
        }
        if (err.length() > 0) {
            throw new Exception(err.toString());
        }
        return this;
    }    
}