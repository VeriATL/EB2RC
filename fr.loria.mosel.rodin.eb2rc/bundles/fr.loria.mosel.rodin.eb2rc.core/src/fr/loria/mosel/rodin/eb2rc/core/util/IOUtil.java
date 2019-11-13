package fr.loria.mosel.rodin.eb2rc.core.util;


import static java.util.Objects.nonNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;


/**
 * Utility methods for working with {@link URI}s.
 */
public final class IOUtil {

    private IOUtil() {
        throw new IllegalStateException("This class should not be initialized");
    }

    /**
     * Returns the absolute path of the platform-based {@code uri}.
     *
     * @param uri the URI for which to find the absolute path
     *
     * @return the absolute path of the URI
     */
    public static String abs(URI uri) {
    	
    	if(!Platform.isRunning()){
    		// for standalone application
			return uri.toFileString();
		}
    	
        return Paths.get(
                ResourcesPlugin.getWorkspace()
                        .getRoot()
                        .getFile(new org.eclipse.core.runtime.Path(uri.toPlatformString(true)))
                        .getRawLocation()
                        .toOSString()).toString();
    }

    /**
     * Returns the filename of the {@code uri} with a new {@code suffix}.
     *
     * @param uri    the uri
     * @param suffix the new suffix of the filename.
     *
     * @return the filename of the {@code uri}. If {@code suffix} is {@code null}, returns the filename without its
     * extension.
     */
    public static String fn(URI uri, String suffix) {
        return fn(uri.trimFileExtension().lastSegment(), suffix);
    }

    /**
     * Returns the filename of the {@code uri} with a new {@code suffix}.
     *
     * @param uri    the literal representation of the uri
     * @param suffix the new suffix of the filename.
     *
     * @return the filename of the {@code uri}. If {@code suffix} is {@code null}, returns the filename without its
     * extension.
     */
    public static String fn(String uri, String suffix) {
        return uri.concat(nonNull(suffix) ? suffix : "");
    }

    /**
     * Copies the content of the {@code source} {@link URI} to the {@code target} {@link URI}.
     *
     * @param source the URI to copy
     * @param target the URI of the copy
     *
     * @return the {@code target} URI
     */
    public static URI copy(URI source, URI target) {
        try {
            URIConverter uriConverter = new ExtensibleURIConverterImpl();
            uriConverter.createInputStream(source);
            uriConverter.createOutputStream(target);

            try (InputStream inputStream = uriConverter.createInputStream(source); OutputStream outputStream = uriConverter.createOutputStream(target)) {
                try (ReadableByteChannel inputChannel = Channels.newChannel(inputStream); WritableByteChannel outputChannel = Channels.newChannel(outputStream)) {
                    final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
                    while (inputChannel.read(buffer) != -1) {
                        buffer.flip();
                        outputChannel.write(buffer);
                        buffer.compact();
                    }
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        outputChannel.write(buffer);
                    }
                }
            }
            return target;
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
   
	/**
	 * find all file names without extension within given URI, not recurvie.
	 * */
	public static ArrayList<String> allNames(URI src) {
		String folder = abs(src);
		ArrayList<String> r = new ArrayList<String>();

		File f = new File(folder);
		for (final File file : f.listFiles()) {
			r.add(getBaseName(file.getName()));
		}
		return r;

	}
	
	
	/**
	 * find all folder names within given URI, not recurvie.
	 * */
	public static ArrayList<String> allFolders(URI src) {
		String folder = abs(src);
		ArrayList<String> r = new ArrayList<String>();

		File f = new File(folder);
		for (final File file : f.listFiles()) {
			if(file.isDirectory()) {
				r.add(file.getName());
			}	
		}

		return r;

	}
	
	
	public static String getBaseName(String fileName) {
	    int index = fileName.lastIndexOf('.');
	    if (index == -1) {
	        return fileName;
	    } else {
	        return fileName.substring(0, index);
	    }
	}

	
	public static void deleteFile(URI target) throws RuntimeException{

		String folder = Paths.get(
                ResourcesPlugin.getWorkspace()
                        .getRoot()
                        .getFile(new org.eclipse.core.runtime.Path(target.toPlatformString(true)))
                        .getRawLocation()
                        .toOSString()).toString();
                     
        File f = new File(folder);
        if(f.exists()){
        	if(f.isFile()){
        		f.delete();
        	}
        }
	}
	
	private static Path createFileWithDir(String directory, String filename) {
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();
        return Paths.get(directory + File.separatorChar + filename);
    }
	
	public static void write(IPath dir, String filename, String s) throws IOException {
		Path p = createFileWithDir(dir.toOSString(), filename);
		Files.write(p, s.getBytes("utf-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}

}
