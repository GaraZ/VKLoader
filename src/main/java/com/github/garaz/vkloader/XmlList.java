package com.github.garaz.vkloader;

import java.util.List;
import org.dom4j.Element;

/**
 *
 * @author GaraZ
 */
public interface XmlList<E>  extends List<E> {
    void writeToXML(Element locRoot) throws Exception;
    XmlList readFromXML(Element locRoot) throws Exception;
}
