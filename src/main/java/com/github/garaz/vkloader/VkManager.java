package com.github.garaz.vkloader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author GaraZ
 */
public class VkManager {
    static final String APP_ID = "4014076";
    //static final String GROUP_ID = "52042075";
    private String token;
    
    String getToken() {
        return token;
    }
    
    void setToken(String token) {
        this.token = token;
    }
    
    void launchBrowser(URI uri) throws IOException {
        Desktop desktop;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(uri);
            }
        }
    }
    
    <T extends HttpRequestBase> void addUriRequestValues(T request,NameValuePair... aValue) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(request.getURI());
        for(NameValuePair value:aValue){
            uriBuilder.addParameter(value.getName(), value.getValue());
        }
            request.setURI(uriBuilder.build());
    }
    
    String getWallUploadServer(CloseableHttpClient httpClient, String groupId) 
            throws URISyntaxException, IOException, VkAPIException, IllegalStateException {
        String location = "https://api.vk.com/method/photos.getWallUploadServer";
        HttpPost httppost = new HttpPost(location);     
        addUriRequestValues(httppost,
             new BasicNameValuePair("group_id", groupId),
             new BasicNameValuePair("access_token", token)
        );
        try (CloseableHttpResponse response = httpClient.execute(httppost)) {
            HttpEntity entity = response.getEntity();
            if (entity != null){
                JsonElement jsonElement = new JsonParser().parse(EntityUtils.toString(entity));
                JsonObject  jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject.has("error")) {
                    throw new VkAPIException(
                            jsonObject.getAsJsonObject("error").get("error_msg").getAsString());
                } else {
                    jsonObject = jsonObject.getAsJsonObject("response");
                    return jsonObject.get("upload_url").getAsString();
                }
            }
        }
        return null;
    }
    
    Map uploadContent(CloseableHttpClient httpClient, String address, File file) 
            throws IOException, VkAPIException, IllegalStateException {
        Map<String, String> map = new HashMap();
        HttpPost httppost = new HttpPost(address);
        HttpEntity entity = MultipartEntityBuilder.create()
                .addBinaryBody("photo", file, ContentType.MULTIPART_FORM_DATA,
                        file.getName()).build();
        httppost.setEntity(entity);
        try(CloseableHttpResponse response = httpClient.execute(httppost)) {
            entity = response.getEntity();
            if (entity != null) {
                JsonElement jsonElement = new JsonParser().parse(EntityUtils.toString(entity));
                JsonObject  jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject.has("error")) {
                    throw new VkAPIException(
                            jsonObject.getAsJsonObject("error").get("error_msg").getAsString());
                }
                jsonObject.get("response");
                map.put("server", jsonObject.get("server").getAsString());
                map.put("photo", jsonObject.get("photo").getAsString());
                map.put("hash", jsonObject.get("hash").getAsString());
            }
        }
        return map;
    }
    
    String putToServer(CloseableHttpClient httpClient, Map<String, String> map, 
            String groupId) throws URISyntaxException, IOException, 
            VkAPIException, IllegalStateException {
        HttpPost httppost = new HttpPost("https://api.vk.com/method/photos.saveWallPhoto");     
        addUriRequestValues(httppost,
             new BasicNameValuePair("server", map.get("server")),
             new BasicNameValuePair("photo", map.get("photo")),
             new BasicNameValuePair("hash", map.get("hash")),
             new BasicNameValuePair("gid", groupId),
             new BasicNameValuePair("access_token", token)
        );
        try(CloseableHttpResponse response = httpClient.execute(httppost)) {
            HttpEntity entity = response.getEntity();
            if (entity != null){
                JsonElement jsonElement = new JsonParser().parse(EntityUtils.toString(entity));
                JsonObject  jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject.has("error")) {
                    throw new VkAPIException(
                            jsonObject.getAsJsonObject("error").get("error_msg").getAsString());
                }
                JsonArray jsonArray = jsonObject.getAsJsonArray("response");
                int size = jsonArray.size();
                for (int i = 0; i < size; i++) {
                    jsonObject = jsonArray.get(i).getAsJsonObject();
                    if (jsonObject.has("id")) {
                        return jsonObject.get("id").getAsString();
                    }
                }
            }
        }
        return null;
    }
    
    String putToWall(CloseableHttpClient httpClient, String id, String groupId,
            String message)
            throws IOException, URISyntaxException, VkAPIException, IllegalStateException {
        HttpPost httppost = new HttpPost("https://api.vk.com/method/wall.post");     
        addUriRequestValues(httppost,
             new BasicNameValuePair("owner_id", "-".concat(groupId)),
             new BasicNameValuePair("from_group", "1"),
             new BasicNameValuePair("message", message),
             new BasicNameValuePair("attachments", id),
             new BasicNameValuePair("access_token", token)
        );
        try(CloseableHttpResponse response = httpClient.execute(httppost)) {
            HttpEntity entity = response.getEntity();
            if (entity != null){
                JsonElement jsonElement = new JsonParser().parse(EntityUtils.toString(entity));
                JsonObject  jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject.has("error")) {
                    throw new VkAPIException(
                            jsonObject.getAsJsonObject("error").get("error_msg").getAsString());
                }
                return jsonObject.getAsJsonObject("response").get("post_id").getAsString();
            }
        }
        return null;
    }
    
    String upload(File file, String groupId, String message) throws URISyntaxException, 
            IOException, VkAPIException, IllegalStateException {
        try(CloseableHttpClient httpClient =  HttpClients.createDefault()) {
            String answer = getWallUploadServer(httpClient, groupId);
            Map map = uploadContent(httpClient, answer, file);
            answer = putToServer(httpClient, map, groupId);
            return putToWall(httpClient, answer, groupId, message);
        }
    }
}
