package com.github.garaz.vkloader;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author GaraZ
 */
public class ProfileXmlAdapter extends XmlAdapter<ProfileObj, ProfileObj>{

    @Override
    public ProfileObj marshal(ProfileObj v) throws Exception {
        if (v.isSaveToken()) {
            v.setToken(Crypt.encrypt(v.getToken()));
        } else {
            v.setToken(null);
        }
        return v;
    }

    @Override
    public ProfileObj unmarshal(ProfileObj v) throws Exception {
        v.setToken(Crypt.dencrypt(v.getToken()));
        return v;
    }
    
}
