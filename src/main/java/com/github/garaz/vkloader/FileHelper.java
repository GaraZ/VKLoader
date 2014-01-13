package com.github.garaz.vkloader;

import java.io.*;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author GaraZ
 */
public class FileHelper {
    static final List<String> MASKS_LIST = new ArrayList(Arrays.asList(".jpg", ".png", ".jpeg", ".bmp"));
    private final List<File> filesList;
    private final List<File> selFilesList;

    public FileHelper() {
        filesList = new ArrayList();
        selFilesList = new ArrayList();
    }
    
    void initContent(File dir) {
        filesList.clear();
        if (dir.isDirectory()) {
            String[] arrDir = dir.list();
            for (String string : arrDir) {
                File file = new File(dir.getAbsolutePath()
                        .concat(File.separator)
                        .concat(string));  
                if (file.isFile()) {
                    for(String filter: MASKS_LIST) {
                        if (file.toString().toLowerCase().endsWith(filter)) {
                            filesList.add(file);
                            break;
                        }
                    }
                }
            }
        }
    }
    
    List<File> getHarmonizeSelFiles() {
        ListIterator iterSel = selFilesList.listIterator();
        ListIterator iter = filesList.listIterator();
        boolean fl = false;
        while(iterSel.hasNext()) {
            Object objSel = iterSel.next();
            while(iter.hasNext()) {
                fl = false;
                Object obj = iter.next();
                if (objSel.equals(obj)) {
                    fl =true;
                    break;
                }
            }
            if (fl == true) {
                iter.remove();
            } else {
                iterSel.remove();
            }
        }
        return selFilesList;
    }
    
    List<File> getContList() {
        return filesList;
    }
    
    void removeFile(File file) {
        filesList.remove(file);
        file.delete();
    }
    
    void removeAllFiles() {
        for (File file: filesList) {
            file.delete();
        }
        filesList.clear();
    }
    
    void selectFile(File file) {
        if (file == null) return;
        selFilesList.add(file);
        filesList.remove(file);
    }
    
    void unselectFile(File file) {
        if (file == null) return;
        filesList.add(file);
        selFilesList.remove(file);
    }
    
    static synchronized <N extends InputStream, T extends OutputStream> void streamCopy(N  in, T out) throws IOException {
        int bytes;
        while ((bytes = in.read()) != -1){
            out.write(bytes);
        }
        out.flush();
    }
    
    synchronized void write(String fileName, byte[] bytes) throws FileNotFoundException, IOException {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(bytes);
        }
    }
    
    static boolean imgValidation(URI uri) {
        if (MASKS_LIST.isEmpty()) return true;
        for (String string : MASKS_LIST) {
            if (uri.getPath().endsWith(string)) {
                return true;
            }
        }
        return false;
    }
    
    void moveToArchive(File archDir, File source) {
        GregorianCalendar calendar = new GregorianCalendar();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String dirNow = df.format(calendar.getInstance().getTime());
        File customDir = archDir.toPath().resolve(dirNow).toFile();
        customDir.mkdirs();
        source.renameTo(customDir.toPath().resolve(source.getName()).toFile());
    }
   
    void saveImages(Map<URI, byte[]> imgMap, String dir) throws IOException {
        for (Entry<URI, byte[]> entry :imgMap.entrySet()) {
            String extension = "";
            int i = entry.getKey().toString().lastIndexOf('.');
            if (i > 0) {
                extension = entry.getKey().toString().substring(i);
            }
            StringBuilder title = new StringBuilder();
            title.append(dir)
                .append(File.separator)
                .append(String.valueOf(Math.abs(entry.getKey().hashCode())))
                .append(extension);
            write(title.toString(), entry.getValue());
        }
    }
    
    void savePages(Map<URI, byte[]> siteMap, String dir) throws IOException {
        for (Entry<URI, byte[]> entry :siteMap.entrySet()) {
            StringBuilder title = new StringBuilder();
            title.append(dir)
                .append(File.separator)
                .append(entry.getKey().getHost())
                .append(entry.getKey().getPath().replaceAll("/", "_"))
                .append(".txt");
            write(title.toString(), entry.getValue());
        }
    }
    
    boolean changeSelListPos(File selFile, int lvl) {
        if (selFile == null) return false;
        if (selFilesList.size()>1) {
            int actLvlList = selFilesList.indexOf(selFile);
            if((actLvlList+lvl >= 0) && (actLvlList+lvl <= selFilesList.size()-1)){
                File file = selFilesList.get(actLvlList+lvl);
                selFilesList.set(actLvlList+lvl, selFile);
                selFilesList.set(actLvlList, file);
                return true;
            }
        }
        return false;
    }
}
