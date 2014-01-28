package com.github.garaz.vkloader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.JFrame;
import org.apache.commons.validator.UrlValidator;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 *
 * @author GaraZ
 */
public class SiteManager {
    private Logger logger = LogManager.getLogger(App.class.getName());
    private Map<URI, byte[]> pageMap;
    private Map<URI, byte[]> imgMap;
    private ExceptionLoggerForm dExcLogForm;
    private ExecutorService executor;
    
    class ParsePage implements Runnable {
        private final CloseableHttpClient httpClient;
        private final HttpGet httpget;
        private final HtmlCleaner cleaner;
        private final List<String> pageMasks;
        
        public ParsePage(URI uri, List<String> pageMasks) {
            this.httpClient = HttpClients.createDefault();
            this.httpget = new HttpGet(uri);
            this.pageMasks = pageMasks;
            this.cleaner = new HtmlCleaner();
            CleanerProperties props = cleaner.getProperties();
            props.setTranslateSpecialEntities(true);
            props.setTransResCharsToNCR(true);
            props.setOmitComments(true);
        }

        @Override
        public void run() {
            try {
                parse(httpClient, httpget, cleaner, pageMasks);
            } catch (IOException e) {
                logger.error(e);
                dExcLogForm.put(e.getMessage());
            }
        }
    }

    public SiteManager(JFrame frame) {
        pageMap = Collections.synchronizedMap(new HashMap());
        imgMap = Collections.synchronizedMap(new HashMap());
        dExcLogForm = new ExceptionLoggerForm(frame);
    }
    
    public Map<URI, byte[]> getPages() {
        return pageMap;
    }
    
    public Map<URI, byte[]> getImages() {
        return imgMap;
    }
    
    Map<URI, String> parseTags(TagNode root, String elName, String atrName) {
        Map<URI, String> map = new HashMap();
        TagNode[] tagNodeArr = root.getElementsByName(elName, true);
        int length = tagNodeArr.length;
        for(int i = 0; i < length; i++) {
            String atr = tagNodeArr[i].getAttributeByName(atrName);
            String text = tagNodeArr[i].getText().toString();
            try {
                URI uri = new URI(atr);
                String prevVal = map.put(uri, text);
                if (prevVal != null) {
                     map.put(uri, prevVal.concat("|").concat(text));
                }
            } catch (URISyntaxException e) {
                dExcLogForm.put(atr.concat(" is uncorect. Caption: ").concat(text));
            }
        }
        return map;
    }
    
    static boolean contentValidation(URI uri) {
        int length = FileManager.MASKS.length;
        for (int i = 0 ; i < length; i++) {
            if (uri.getPath().endsWith(FileManager.MASKS[i])) {
                return true;
            }
        }
        return false;
    }
    
    List<URI> getLinks(TagNode root, List<String> maskList) {
        List<URI> list = new ArrayList();
        List<URI> imgList = new ArrayList();
        imgList.addAll(parseTags(root, "img", "src").keySet());
        for (URI uri : imgList) {
            if (contentValidation(uri)) {
                list.add(uri);
            }
        }
        if (!maskList.isEmpty()) {
            Map<URI, String> map = parseTags(root, "a", "href");
            for (Entry<URI, String> entry : map.entrySet()) {
                for (String string : maskList) {
                    if (entry.getValue().indexOf(string) != -1) {
                        list.add(entry.getKey());
                        break;
                    }
                }
            }
        }
        return list;
    }
    
    void parse(CloseableHttpClient httpClient, HttpGet httpget, 
            HtmlCleaner cleaner, List<String> maskList) throws IOException {
        if (Thread.currentThread().isInterrupted()) return;
        URI uri = httpget.getURI();
        dExcLogForm.put(uri.toString());
        try (CloseableHttpResponse response = httpClient.execute(httpget)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String contType = response.getFirstHeader("Content-Type").getValue();
                if (contType.toLowerCase().indexOf("image")  != -1) {
                    try {
                        imgMap.put(uri, EntityUtils.toByteArray(entity));
                    } catch (IOException e) {
                        logger.error(e);
                        dExcLogForm.put(e.getMessage());
                    }
                } else if (contType.toLowerCase().indexOf("text")  != -1) {
                    try {
                        byte[] bytes = EntityUtils.toByteArray(entity);
                        pageMap.put(uri, bytes);
                        String charset;
                        ContentType contentType = ContentType.getOrDefault(entity);
                        if (contentType.getCharset() != null) {
                            charset = contentType.getCharset().name();
                        } else {
                            charset = findCharset(bytes, cleaner);
                        }
                        if (charset != null) {
                            cleaner.getProperties().setCharset(charset);
                        }
                        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
                            List<URI> list = getLinks(cleaner.clean(bis), maskList);
                            for (URI pageUri: list) {
                                httpget.setURI(uri.resolve(pageUri));
                                parse(httpClient, httpget, cleaner, new ArrayList());
                            }
                        }
                    } catch(IOException e) {
                        logger.error(e);
                        dExcLogForm.put(e.getMessage());
                    }
                }                    
            }
        }
    }    
    
    static URI verifyUrl(String string) throws URISyntaxException {
        if (!string.toLowerCase().startsWith("http://") &&
                !string.toLowerCase().startsWith("htts://")) {
            string = "http://".concat(string);
        }
        String[] schemes = {"http","https"}; 
        UrlValidator urlValidator = new UrlValidator(schemes);
        if (!urlValidator.isValid(string)) {
            throw new URISyntaxException("URI is uncorrect", string);
        }
        return new URI(string);
    }
    
    String findCharset(byte[] bytes, HtmlCleaner cleaner) {
        String charset = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
            TagNode meta = cleaner.clean(bis).findElementByAttValue("http-equiv", "Content-Type", true, false);
            if (meta != null) {
                String content = meta.getAttributeByName("content");
                int encodingStart = content.indexOf("charset=");
                if (encodingStart != -1) {
                    charset = content.substring(encodingStart + 8).trim();
                } 
            }
        } catch (IOException e) {
            logger.error(e);
            dExcLogForm.put(e.getMessage());
        }
        return charset;
    }

    void runSitesDownload(List<SiteObj> sitesList) throws 
            IOException, InterruptedException, ExecutionException {
        pageMap.clear();
        imgMap.clear();
        dExcLogForm.clean();
        dExcLogForm.setVisible(true);
        int size = sitesList.size();
        executor = Executors.newFixedThreadPool(size);
        try {
        List<Future> futures = new ArrayList();
        for (int i = 0; i < size; i++) {
            Runnable worker = new ParsePage( 
                   sitesList.get(i).getURI(), sitesList.get(i).getMasks());
            futures.add(executor.submit(worker));
        }
        
        for(Future future : futures) {
            future.get();
        }
        } finally {
           executor.shutdown(); 
        }
    }
    
    void stop() {
        executor.shutdownNow();
    }
}
