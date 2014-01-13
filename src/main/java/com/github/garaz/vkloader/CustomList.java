package com.github.garaz.vkloader;

import java.util.List;
import org.dom4j.Element;

/**
 *
 * @author GaraZ
 */
public interface CustomList<E extends Object>  extends List<E> {
    void writeToXML(Element locRoot) throws Exception;
    CustomList readFromXML(Element locRoot) throws Exception;
}
