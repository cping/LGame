package loon.build.packer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    public static void writeStreamToZipEntry(ZipOutputStream zipOutputStream, ZipEntry zipEntry, InputStream inputStream) throws IOException {
        zipOutputStream.putNextEntry(zipEntry);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            zipOutputStream.write(buffer, 0, length);
        }
        zipOutputStream.closeEntry();
    }

    public static void writeByteArrayToZipEntry(ZipOutputStream zipOutputStream, ZipEntry zipEntry, byte[] bytesToWrite) throws IOException {
        zipOutputStream.putNextEntry(zipEntry);
        byte[] buffer = new byte[1024];
        ByteArrayInputStream bis = new ByteArrayInputStream(bytesToWrite);
        int length;
        while ((length = bis.read(buffer)) > 0) {
            zipOutputStream.write(buffer, 0, length);
        }
        zipOutputStream.closeEntry();
    }

}
