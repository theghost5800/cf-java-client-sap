package org.cloudfoundry.client.lib.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.cloudfoundry.client.lib.io.DynamicZipInputStream.Entry;
import org.springframework.util.Assert;

public class FileZipEntries {
    private static InputStream EMPTY_STREAM = new InputStream() {

        @Override
        public int read() throws IOException {
            return -1;
        }
    };

    /**
     * Entries to be written.
     */
    private Iterator<Entry> entries;

    /**
     * The current entry {@link InputStream}.
     */
    private InputStream entryStream = EMPTY_STREAM;

    /**
     * The underlying ZIP stream.
     */
    private ZipOutputStream zipStream;
    private Path tmpFile;
    private FileOutputStream fileOutputStream;

    public FileZipEntries(Iterable<Entry> entries, String fileName) {
        Assert.notNull(entries, "Entries must not be null");
        createTmpFile(fileName);
        createFileOutputStream();
        this.zipStream = new ZipOutputStream(fileOutputStream);
        this.entries = entries.iterator();
        System.out.println(MessageFormat.format("Save entries in zip:{0}", tmpFile.getFileName()
            .toString()));
    }

    private void createTmpFile(String fileName) {
        try {
            this.tmpFile = Files.createTempFile(fileName, "");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void createFileOutputStream() {
        try {
            this.fileOutputStream = new FileOutputStream(this.tmpFile.toFile());
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void storeAllEntries() throws IOException {
        while (entries.hasNext()) {
            Entry entry = entries.next();
            zipStream.putNextEntry(new UtcAdjustedZipEntry(entry.getName()));
            entryStream = entry.getInputStream();
            if (entryStream == null) {
                entryStream = EMPTY_STREAM;
            }

            // // If no files were added to the archive add an empty one
            // if (fileCount == 0) {
            // fileCount++;
            // zipStream.putNextEntry(new UtcAdjustedZipEntry("__empty__"));
            // entryStream = EMPTY_STREAM;
            // }
            saveEntry();
            if (entryStream != EMPTY_STREAM) {
                zipStream.closeEntry();
                entryStream.close();
                entryStream = EMPTY_STREAM;
            }
        }
        // No more entries, close and flush the stream
        zipStream.flush();
        zipStream.close();
        fileOutputStream.close();
    }

    private void saveEntry() throws IOException {
        IOUtils.copyLarge(entryStream, zipStream);
    }

    public InputStream getInputStream() {
        try {
            return new FileZipInputStream(tmpFile.toFile());
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private class FileZipInputStream extends FileInputStream {

        public FileZipInputStream(File tmpFile) throws FileNotFoundException {
            super(tmpFile);
        }

        @Override
        public void close() throws IOException {
            super.close();
            zipStream.close();
            IOUtils.closeQuietly(fileOutputStream);
            Files.delete(tmpFile);
            System.out.println(MessageFormat.format("Close stream called and deleted file is:{0}", tmpFile.getFileName()
                .toString()));
        }
    }
}
