package com.servustech.eduson.utils.filestorage;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.net.URLConnection;

public class FileStorageUtils {

    public static void setResponseContentType(HttpServletResponse response, HttpHeaders responseHeaders, String fileName) {
        try {
            String mimeType = URLConnection.guessContentTypeFromName(fileName);
            responseHeaders.setContentType(MediaType.valueOf(mimeType));
            response.setContentType(mimeType);

        } catch (Exception e) {
            responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }
    }

    public static String[] removeCommas(String[] strings){
        for (int i=0; i<strings.length; i++){
            if (strings[i]!=null && !strings[i].isEmpty()){
                strings[i] = strings[i].replace(",", " ");
            }
        }

        return strings;
    }

}
