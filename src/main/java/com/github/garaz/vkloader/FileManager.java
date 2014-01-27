package com.github.garaz.vkloader;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author GaraZ
 */
public class FileManager {
    static final String[] MASKS = {".jpg", ".png", ".jpeg", ".bmp"};
    private final List<File> filesList;
    private final List<File> selFilesList;

    public FileManager() {
        filesList = new ArrayList();
        selFilesList = new ArrayList();
    }
    
    void initContent(File dir) {
        filesList.clear();
        File[] arrDir = dir.listFiles();
        if (arrDir == null) return;
        for (File file : arrDir) { 
            if (file.isFile()) {
                int length = MASKS.length;
                String name = file.getName().toLowerCase(Locale.getDefault());
                for (int i = 0; i < length; i++) {                     
                    if (name.endsWith(MASKS[i])) {
                        filesList.add(file);
                        break;
                    }
                }
            }
        }
    }
    
    public List<File> getHarmonizeSelFiles() {
        ListIterator<File> iterSel = selFilesList.listIterator();
        ListIterator<File> iter = filesList.listIterator();
        boolean match = false;
        while(iterSel.hasNext()) {
            File fileSel = iterSel.next();
            while(iter.hasNext()) {
                match = false;
                File file = iter.next();
                if (fileSel.equals(file)) {
                    match =true;
                    break;
                }
            }
            if (match) {
                iter.remove();
            } else {
                iterSel.remove();
            }
        }
        return selFilesList;
    }
    
    public List<File> getContList() {
        return filesList;
    }
    
    void removeFile(File file) throws NoSuchFileException, IOException{
        try {
            Files.delete(file.toPath());
        } catch(NoSuchFileException e) {
            filesList.remove(file);
            throw new NoSuchFileException(file.getName().concat(" not found. "));
        }
        filesList.remove(file);
    }
    
    void removeAllFiles() throws IOException {
        ListIterator<File> iter = filesList.listIterator();
        StringBuilder errMessages = new StringBuilder();
        while(iter.hasNext()) {
            File file = iter.next();
            try {
                Files.delete(file.toPath());
                iter.remove();
            } catch(NoSuchFileException e) {
                iter.remove();
                errMessages.append(e.getMessage()).append(" not found. ");
            } catch(IOException ex) {
                errMessages.append(ex.getMessage()).append(" ");
            }
        }
        String errMessagesString = errMessages.toString();
        if (!errMessagesString.isEmpty()) {
            throw new IOException(errMessagesString);
        }
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
    
    synchronized void write(File file, byte[] bytes) throws FileNotFoundException, IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(bytes);
        }
    }
    
    void moveToArchive(File archDir, File source) {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String dirNow = df.format(System.currentTimeMillis());
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
            String name = String.valueOf(Math.abs(entry.getKey().hashCode())).concat(extension);
            File file = new File(dir, name);
            write(file, entry.getValue());
        }
    }
    
    void savePages(Map<URI, byte[]> siteMap, String dir) throws IOException {
        for (Entry<URI, byte[]> entry :siteMap.entrySet()) {
            File file = new File(dir, 
                    entry.getKey().getHost()
                    .concat(entry.getKey().getPath().replaceAll("/", "_"))
                    .concat(".txt")
                    );
            write(file, entry.getValue());
        }
    }
    
    boolean changeSelListPos(File selFile, int lvl) {
        if (selFile == null) return false;
        
        if (selFilesList.size() > 1) {
            int actLvlList = selFilesList.indexOf(selFile);
            if((actLvlList + lvl >= 0) && 
                    (actLvlList + lvl <= selFilesList.size() - 1)) {
                File file = selFilesList.get(actLvlList + lvl);
                selFilesList.set(actLvlList + lvl, selFile);
                selFilesList.set(actLvlList, file);
                return true;
            }
        }
        return false;
    }
}
