package com.github.garaz.vkloader;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author GaraZ
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlJavaTypeAdapter(ProfileXmlAdapter.class)
public class ProfileObj {
    @XmlElement
    private String login;
    @XmlElement
    private String token;
    
    private boolean saveToken;
    
    public ProfileObj() {
        this.login = null;
    }
    
    public ProfileObj(String login) {
        this.login = login.trim();
    }
      
    public String getLogin() {
        return login;
    }
    
    public String getToken() {
        return token;
    }
    
    public boolean isSaveToken() {
        return saveToken;
    }
    
    public void setLogin(String login) {
        this.login = login.trim();
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public void setToken(String token, boolean saveToken) {
        this.token = token;
        this.saveToken = saveToken;
    }
    
    public void setSaveToken(boolean saveToken) {
        this.saveToken = saveToken;
    }
    
    @Override
    public String toString() {
        return login.trim();
    }
}
