package com.github.garaz.vkloader;

import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.dom4j.Element;

/**
 *
 * @author GaraZ
 */
public class ProfileObj {
    private Object login;
    private String token;
    private boolean saveToken;
    
    public ProfileObj() {
        this.login = null;
    }
    
    public ProfileObj(Object login) {
        this.login = login;
    }
      
    Object getLogin() {
        return login;
    }
    
    String getToken() {
        return token;
    }
    
    boolean getSaveToken() {
        return saveToken;
    }
    
    void setLogin(Object login) {
        this.login = login;
    }
    
    void setToken(String token, boolean saveToken) {
        this.token = token;
        this.saveToken = saveToken;
    }
    
    void setSaveToken(boolean saveToken) {
        this.saveToken = saveToken;
    }
    
    ProfileObj readFromXML(Element locRoot) throws Exception {
            login = locRoot.valueOf("@Login");
        try {
            token = Crypt.dencrypt(locRoot.valueOf("@Token"));
        } catch(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | 
                IllegalBlockSizeException | BadPaddingException | IllegalArgumentException |
                UnsupportedEncodingException | UnknownHostException | SocketException e) {
            throw new Exception("Profile "
                    .concat(String.valueOf(login))
                    .concat(" is uncorrect")
                    .concat(" Error: ")
                    .concat(e.getMessage()));
        }
        return this;
    }
    
    void writeToXML(Element locRoot) throws Exception {
        try {
            String tokenVal;
            if (saveToken == true) {
                tokenVal = Crypt.encrypt(token);
            } else {
                tokenVal = new String();
            }
            locRoot.addElement("Profile")
                .addAttribute("Login", String.valueOf(login))
                .addAttribute("Token", tokenVal);
        } catch(UnsupportedEncodingException | NoSuchAlgorithmException | 
                NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | 
                BadPaddingException | UnknownHostException | SocketException e) {
            throw new Exception("Profile "
                    .concat(String.valueOf(login))
                    .concat(" is uncorrect")
                    .concat(" Error: ")
                    .concat(e.getMessage()));
        }
    }
    
    @Override
    public String toString() {
        return String.valueOf(login);
    }
    
    @Override
    public int hashCode() {
        return login.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return this.hashCode() == obj.hashCode();
    }
}
