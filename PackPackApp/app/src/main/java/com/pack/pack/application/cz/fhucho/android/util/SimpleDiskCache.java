package com.pack.pack.application.cz.fhucho.android.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//import com.google.common.io.ByteStreams;
//import com.google.common.io.Closeables;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class SimpleDiskCache {

    private static final int VALUE_IDX = 0;
    private static final int METADATA_IDX = 1;
    private static final List<File> usedDirs = new ArrayList<File>();

    private DiskLruCache diskLruCache;
    private int mAppVersion;

    private static SimpleDiskCache instance;

    private static final Object lock = new Object();

    private SimpleDiskCache(File dir, int appVersion, long maxSize) throws IOException {
        mAppVersion = appVersion;
        diskLruCache = DiskLruCache.open(dir, appVersion, 2, maxSize);
    }

    static synchronized SimpleDiskCache open(File dir, int appVersion, long maxSize)
            throws IOException {
        return open0(dir, appVersion, maxSize);
    }

    private static SimpleDiskCache open0(File dir, int appVersion, long maxSize)
            throws IOException {
        if (instance == null) {
            instance = new SimpleDiskCache(dir, appVersion, maxSize);
            if (usedDirs.contains(dir)) {
                throw new IllegalStateException("Cache dir " + dir.getAbsolutePath() + " was used before.");
            }

            usedDirs.add(dir);
        }
        return instance;
    }

    static synchronized SimpleDiskCache reOpen(File dir, int appVersion, long maxSize)
            throws IOException {
        instance = null;
        return open(dir, appVersion, maxSize);
    }

    /************************************************************************************************/
    static synchronized boolean enforceMaxSizeLimit(Context context, long maxSize, long maxTolerentSize) {
        File cacheDir = context.getCacheDir();
        long currentSize = computeTotalCacheSize(cacheDir);
        if(currentSize >= maxTolerentSize) {
            cleanupCache(cacheDir, currentSize, false);
            return true;
        } else {
            long bytesToDelete = currentSize - maxSize;
            if(bytesToDelete > 0) {
                cleanupCache(cacheDir, bytesToDelete, true);
                currentSize = computeTotalCacheSize(cacheDir);
                bytesToDelete = currentSize - maxSize;
            }
            if(bytesToDelete > 0) {
                cleanupCache(cacheDir, bytesToDelete, false);
            }
            return false;
        }
    }

    private static void cleanupCache(File dir, long bytes, boolean selectiveDelete) {
        long bytesDeleted = 0;
        File[] files = dir.listFiles();

        for (File file : files) {
            bytesDeleted += file.length();
            if(!file.exists())
                continue;
            if(file.isDirectory())
                continue;
            if(file.getName().contains("journal") && selectiveDelete)
                continue;
            if(selectiveDelete) {
                long t0 = file.lastModified();
                long t1 = System.currentTimeMillis();
                int diff = (int) ((((t1 - t0) / 1000) / 60) / 60);
                if (diff >= 4) { // 8 Hour ago cached image (Less likely to be used, force remove from cache to minimize memory usage)
                    file.delete();
                }
            } else {
                file.delete();
            }

            if (bytesDeleted >= bytes) {
                break;
            }
        }
    }

    private static long computeTotalCacheSize(File dir) {
        long size = 0;
        File[] files = dir.listFiles();

        for (File file : files) {
            if (file.isFile()) {
                size += file.length();
            }
        }

        return size;
    }
    /************************************************************************************************/

    public static SimpleDiskCache getInstance() {
        return instance;
    }

    /**
     ** User should be sure there are no outstanding operations.
     ** @throws IOException
     *
     */
    public void clear() throws IOException {
        File dir = diskLruCache.getDirectory();
        long maxSize = diskLruCache.getMaxSize();
        diskLruCache.delete();
        diskLruCache = DiskLruCache.open(dir, mAppVersion, 2, maxSize);
    }

    public DiskLruCache getCache() {
        return diskLruCache;
    }

    /*public void remove(String key) throws IOException {
        diskLruCache.remove(key);
    }*/

    public InputStreamEntry getInputStream(String key) throws IOException {
        DiskLruCache.Snapshot snapshot = diskLruCache.get(toInternalKey(key));
        if (snapshot == null) return null;
        return new InputStreamEntry(snapshot, readMetadata(snapshot));
    }

   /* private boolean writeBitmapToFile( Bitmap bitmap, DiskLruCache.Editor editor )
            throws IOException, FileNotFoundException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream( editor.newOutputStream( 0 ), 8 * 1024 );
            return bitmap.compress( Bitmap.CompressFormat.JPEG, 100, out );
        } finally {
            if ( out != null ) {
                out.close();
            }
        }
    }*/

    public void put(String key, Bitmap bitmap) throws IOException {
        OutputStream outputStream =  null;
        boolean success = false;
        try {
            outputStream = openStream(key);
            success = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            if(success) {
                outputStream.flush();
            }
        } finally {
            diskLruCache.flush();
            if(outputStream != null) {
                outputStream.close();
            }
        }
        BitmapEntry e = getBitmap(key);
        if(e == null) {

        }
    }

    public BitmapEntry getBitmap(String key) throws IOException {
        DiskLruCache.Snapshot snapshot = diskLruCache.get(toInternalKey(key));
        if (snapshot == null) return null;

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(VALUE_IDX));
            return new BitmapEntry(bitmap, readMetadata(snapshot));
        } finally {
            snapshot.close();
        }
    }

    public long getSize() {
        if(diskLruCache != null) {
            return diskLruCache.size();
        }
        return 0;
    }

    public long getMaxSize() {
        if(diskLruCache != null) {
            return diskLruCache.getMaxSize();
        }
        return 0;
    }

    public void flush() throws IOException {
        if(diskLruCache != null) {
            diskLruCache.flush();
        }
    }

    public void evict(String key) throws IOException {
        if(diskLruCache != null) {
            diskLruCache.remove(key);
        }
    }

    public StringEntry getString(String key) throws IOException {
        DiskLruCache.Snapshot snapshot = diskLruCache.get(toInternalKey(key));
        if (snapshot == null) return null;

        try {
            return new StringEntry(snapshot.getString(VALUE_IDX), readMetadata(snapshot));
        } finally {
            snapshot.close();
        }
    }

    public <T> T getArray(String key, Class<T> type) throws IOException {
        DiskLruCache.Snapshot snapshot = diskLruCache.get(toInternalKey(key));
        if (snapshot == null) return null;

        try {
            long ttl = Long.valueOf(snapshot.getString(VALUE_IDX));
            if (ttl > new Date().getTime()) {
                return type.cast(readArray(snapshot));
            } //else throw new IOException("ttl:" + ttl + "<" + new Date().getTime());
        } finally {
            snapshot.close();
        }
        return null;
    }

    public boolean contains(String key) throws IOException {
        DiskLruCache.Snapshot snapshot = diskLruCache.get(toInternalKey(key));
        if (snapshot == null) return false;

        snapshot.close();
        return true;
    }

    public OutputStream openStream(String key) throws IOException {
        return openStream(key, new HashMap<String, Serializable>());
    }

    public OutputStream openStream(String key, Map<String, ? extends Serializable> metadata) throws IOException {
        DiskLruCache.Editor editor = diskLruCache.edit(toInternalKey(key));
        if (editor != null) {
            try {
                //if (metadata != null && metadata.size() > 0)
                // IllegalStateException: Newly created entry didn't create value for index 1 at com.jakewharton.DiskLruCache.completeEdit(DiskLruCache.java:476)
                writeMetadata(metadata, editor);
                BufferedOutputStream bos = new BufferedOutputStream(editor.newOutputStream(VALUE_IDX));
                return new CacheOutputStream(bos, editor);
            } catch (IOException e) {
                editor.abort();
                throw e;
            }
        } //else Log.e("SimpleDiskCahe", "openStream editor=null");
        return null;
    }

    public OutputStream openStream(String key, Object[] obj) throws IOException {
        DiskLruCache.Editor editor = diskLruCache.edit(toInternalKey(key));
        if (editor != null) {
            try {
                writeArray(obj, editor);
                BufferedOutputStream bos = new BufferedOutputStream(editor.newOutputStream(VALUE_IDX));
                return new CacheOutputStream(bos, editor);
            } catch (IOException e) {
                editor.abort();
                throw e;
            }
        } //else Log.e("SimpleDiskCahe", "openStream editor=null");
        return null;
    }

    public OutputStream openStream(String key, Object obj) throws IOException {
        DiskLruCache.Editor editor = diskLruCache.edit(toInternalKey(key));
        if (editor != null) {
            try {
                writeArray(obj, editor);
                BufferedOutputStream bos = new BufferedOutputStream(editor.newOutputStream(VALUE_IDX));
                return new CacheOutputStream(bos, editor);
            } catch (IOException e) {
                editor.abort();
                throw e;
            }
        } //else Log.e("SimpleDiskCahe", "openStream editor=null");
        return null;
    }

    public void put(String key, InputStream is) throws IOException {
        put(key, is, new HashMap<String, Serializable>());
    }

    private byte[] createBuffer() {
        return new byte[8192];
    }

    private <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
        }

    private long copy(InputStream from, OutputStream to) throws IOException {
        checkNotNull(from);
        checkNotNull(to);

        byte[] buf = createBuffer();
        long total = 0;
        while (true) {
            int r = from.read(buf);
            if (r == -1) {
                break;
            }
            to.write(buf, 0, r);
            total += r;
        }
        return total;
    }

    public void put(String key, InputStream is, Map<String, Serializable> annotations) throws IOException {
        OutputStream os = null;
        try {
            os = openStream(key, annotations);
            //IOUtils.copy(is, os);
            //ByteStreams.copy(is, os);
            copy(is, os);
        } finally {
            if (os != null) os.close();
        }
    }

    public void put(String key, String value) throws IOException {
        put(key, value, new HashMap<String, Serializable>());
    }

    public void put(String key, String value, Map<String, ? extends Serializable> annotations) throws IOException {
        OutputStream cos = null;
        try {
            cos = openStream(key, annotations);
            cos.write(value.getBytes());
        } finally {
            if (cos != null) cos.close();
        }

    }

    public void put(String key, String value, Object[] array) throws IOException {
        OutputStream cos = null;
        try {
            cos = openStream(key, array);
            cos.write(value.getBytes());
        } finally {
            if (cos != null) cos.close();
        }
    }

    public void put(String key, String value, Object obj)
            throws IOException {
        OutputStream cos = null;
        try {
            cos = openStream(key, obj);
            cos.write(value.getBytes());
        } finally {
            if (cos != null) cos.close();
        }
    }

    private void writeMetadata(Map<String, ? extends Serializable> metadata, DiskLruCache.Editor editor) throws IOException {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new BufferedOutputStream(editor.newOutputStream(METADATA_IDX)));
            oos.writeObject(metadata);
        } finally {
            oos.close();
            //IOUtils.closeQuietly(oos);
            //Closeables.close(oos, true);
        }
    }

    private Map<String, Serializable> readMetadata(DiskLruCache.Snapshot snapshot) throws IOException {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new BufferedInputStream(snapshot.getInputStream(METADATA_IDX)));
            @SuppressWarnings("unchecked")
            Map<String, Serializable> annotations = (Map<String, Serializable>) ois.readObject();
            return annotations;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            ois.close();
            //IOUtils.closeQuietly(ois);
            //Closeables.close(ois, true);
        }
    }

    private void writeArray(Object array[], DiskLruCache.Editor editor) throws IOException {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new BufferedOutputStream(editor.newOutputStream(METADATA_IDX)));
            oos.writeObject(array);
        } finally {
            oos.close();
            //IOUtils.closeQuietly(oos);
            //Closeables.close(oos, true);
        }
    }

    private void writeArray(Object array, DiskLruCache.Editor editor) throws IOException {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new BufferedOutputStream(editor.newOutputStream(METADATA_IDX)));
            oos.writeObject(array);
        } finally {
            oos.close();
            //IOUtils.closeQuietly(oos);
            //Closeables.close(oos, true);
        }
    }

    private <T> T[] readArray(DiskLruCache.Snapshot snapshot)
            throws IOException {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new BufferedInputStream(snapshot.getInputStream(METADATA_IDX)));
            @SuppressWarnings("unchecked")
            T[] annotations = (T[]) ois.readObject();
            return annotations;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            ois.close();
            //IOUtils.closeQuietly(ois);
            //Closeables.close(ois, true);
        }
    }

    private String toInternalKey(String key) {
        return md5(key);
    }

    private String md5(String s) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes("UTF-8"));
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            return bigInt.toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError();
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError();
        }
    }

    private class CacheOutputStream extends FilterOutputStream {

        private final DiskLruCache.Editor editor;
        private boolean failed = false;

        private CacheOutputStream(OutputStream os, DiskLruCache.Editor editor) {
            super(os);
            this.editor = editor;
        }

        @Override
        public void close() throws IOException {
            IOException closeException = null;
            try {
                super.close();
            } catch (IOException e) {
                closeException = e;
            }

            if (failed) {
                editor.abort();
            } else {
                editor.commit();
            }

            if (closeException != null) throw closeException;
        }

        @Override
        public void flush() throws IOException {
            try {
                super.flush();
            } catch (IOException e) {
                failed = true;
                throw e;
            }
        }

        @Override
        public void write(int oneByte) throws IOException {
            try {
                super.write(oneByte);
            } catch (IOException e) {
                failed = true;
                throw e;
            }
        }

        @Override
        public void write(byte[] buffer) throws IOException {
            try {
                super.write(buffer);
            } catch (IOException e) {
                failed = true;
                throw e;
            }
        }

        @Override
        public void write(byte[] buffer, int offset, int length) throws IOException {
            try {
                super.write(buffer, offset, length);
            } catch (IOException e) {
                failed = true;
                throw e;
            }
        }
    }

    public static class InputStreamEntry {
        private final DiskLruCache.Snapshot snapshot;
        private final Map<String, Serializable> metadata;

        public InputStreamEntry(DiskLruCache.Snapshot snapshot, Map<String, Serializable> metadata) {
            this.metadata = metadata;
            this.snapshot = snapshot;
        }

        public InputStream getInputStream() {
            return snapshot.getInputStream(VALUE_IDX);
        }

        public Map<String, Serializable> getMetadata() {
            return metadata;
        }

        public void close() {
            snapshot.close();

        }

    }

    public static class BitmapEntry {
        private final Bitmap bitmap;
        private final Map<String, Serializable> metadata;

        public BitmapEntry(Bitmap bitmap, Map<String, Serializable> metadata) {
            this.bitmap = bitmap;
            this.metadata = metadata;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public Map<String, Serializable> getMetadata() {
            return metadata;
        }
    }

    public static class StringEntry {
        private final String string;
        private final Map<String, Serializable> metadata;

        public StringEntry(String string, Map<String, Serializable> metadata) {
            this.string = string;
            this.metadata = metadata;
        }

        public String getString() {
            return string;
        }

        public Map<String, Serializable> getMetadata() {
            return metadata;
        }
    }
}
