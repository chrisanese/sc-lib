package de.fu.mi.scuttle.lib.util;

public class MimeTypeUtil {

    public static String mimeTypeForFileExtension(String fileExtension) {
        if (fileExtension == null) {
            return "";
        }
        switch (fileExtension.toLowerCase()) {
        case "png":
            return "image/png";
        case "gif":
            return "image/gif";
        case "jpeg":
        case "jpg":
            return "image/jpeg";
        case "css":
            return "text/css";
        case "json":
            return "application/json";
        case "js":
            return "application/javascript";
        case "htm":
        case "html":
            return "text/html";
        case "txt":
            return "text/plain";
        case "xml":
            return "application/xml";
        }
        return "application/octet-stream";
    }
}
