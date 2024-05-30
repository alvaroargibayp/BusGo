package udc.psi.busgo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class StringUtils {
    //Descomprime un string de base64
    public static String decompress(String str) throws IOException {
        if (str == null || str.isEmpty()) {
            return str;
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(str));
        GZIPInputStream gis = new GZIPInputStream(byteArrayInputStream);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[256];
        int n;
        while ((n = gis.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        gis.close();
        return out.toString();
    }
    //Comprime un string a base64
    public static String compress(String str) throws IOException {
        if (str == null || str.isEmpty()) {
            return str;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes());
        gzip.close();
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }
}