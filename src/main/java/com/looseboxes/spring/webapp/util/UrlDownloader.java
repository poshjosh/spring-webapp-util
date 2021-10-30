package com.looseboxes.spring.webapp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.util.Optional;

/**
 * @author hp
 */
public class UrlDownloader {

    private final Logger log = LoggerFactory.getLogger(UrlDownloader.class);

    private final UrlProbe urlProbe;
    private final Path dir;

    public UrlDownloader(UrlProbe urlProbe) {
        this.urlProbe = urlProbe;
        this.dir = Paths.get(System.getProperty("java.io.tmpdir")).toAbsolutePath().normalize();
    }

    public Optional<Path> downloadByName(String imageUrl, String name) {
        if(imageUrl != null) {
            final String fname = getFilename(imageUrl, name);
            return downloadIfNoneExists(imageUrl, fname);
        }else{
            return Optional.empty();
        }
    }

    private String getFilename(String imageUrl, String name) {
        final String fname;
        if(name.indexOf('.') != -1) { // has extension
            fname = name;
        }else {
            final String fnameWithoutExt = name.replaceAll("\\s", "").replaceAll("\\W", "-");
            final String ext = getImageExtention(imageUrl, null);
            fname = ext == null ? fnameWithoutExt : fnameWithoutExt + '.' + ext;
        }
        return fname;
    }

    private String getImageExtention(String url, String resultIfNone) {
        final String u = url.toLowerCase();
        if(u.endsWith(".jpg")) {
            return "jpg";
        }else if(u.endsWith(".jpeg")) {
            return "jpeg";
        }else if(u.endsWith(".gif")) {
            return "gif";
        }else if(u.endsWith(".png")) {
            return "png";
        }else if(u.endsWith(".tiff")) {
            return "tiff";
        }else{
            String contentType = null;
            try {
                contentType = getContentType(url, null);
            }catch(IOException e) {
                log.warn("Error access ContentType of " + url, e);
            }
            if(contentType == null) {
                return resultIfNone;
            }else{
                final int n = contentType.lastIndexOf('/');
                if(n == -1) {
                    return contentType;
                }else{
                    return contentType.substring(n + 1);
                }
            }
        }
    }

    private String getContentType(String url, String resultIfNone) throws IOException {
        try {
            String contentType = urlProbe.getContentType(new URL(url));
            return contentType == null || contentType.isEmpty() ? resultIfNone : contentType;
        }catch(MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Path> downloadIfNoneExists(String url, String fname) {
        return downloadIfNoneExists(url, dir.resolve(fname));
    }

    public Optional<Path> downloadIfNoneExists(String url, Path target) {

        if(Files.exists(target)) {

            return Optional.of(target);

        }else{

            boolean success = downloadTo(url, target);

            return success ? Optional.of(target) : Optional.empty();
        }
    }

    public boolean downloadTo(String url, Path to) {
        return this.downloadTo(url, to, 8 * 1024,
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING
//                    StandardOpenOption.DELETE_ON_CLOSE   - This caused FileNotFoundException
        );
    }

    private boolean downloadTo(String url, Path to, int bufferSize, OpenOption... options) {

        try(InputStream in = new URL(url).openStream()){

            ByteArrayOutputStream out = new ByteArrayOutputStream(bufferSize);

            byte [] buffer = new byte[bufferSize];

            while(in.read(buffer) != -1) {
                out.write(buffer);
            }

            byte [] data = out.toByteArray();

            Path target = Files.write(to, data, options);

            log.debug("Downloaded {} KB image to: {}, from: {}", data.length/1000, target, url);

            return true;

        }catch(IOException e) {

            log.warn("Exception downloading image from: " + url, e);

            return false;
        }
    }
}
