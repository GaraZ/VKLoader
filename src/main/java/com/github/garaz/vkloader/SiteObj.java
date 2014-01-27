package com.github.garaz.vkloader;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;

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
    
    @Override
    public String toString() {
        return uri.toString();
    }
}