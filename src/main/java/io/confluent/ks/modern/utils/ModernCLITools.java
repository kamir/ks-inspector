package io.confluent.ks.modern.utils;

import io.confluent.ks.modern.parser.DomainParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Modern utilities replacing CLITools functionality
 * Provides file and path utilities for modern ks-inspector.
 */
public class ModernCLITools {
    
    private static final Logger logger = LoggerFactory.getLogger(ModernCLITools.class);
    
    /**
     * Check if a path is a domain file
     * Modern replacement for CLITools.isDomainFile()
     */
    public static boolean isDomainFile(Path path) {
        return DomainParser.isDomainFile(path);
    }
    
    /**
     * Validate file exists and is readable
     */
    public static boolean isValidFile(Path path) {
        if (!Files.exists(path)) {
            logger.warn("File does not exist: {}", path);
            return false;
        }
        
        if (!Files.isRegularFile(path)) {
            logger.warn("Path is not a regular file: {}", path);
            return false;
        }
        
        if (!Files.isReadable(path)) {
            logger.warn("File is not readable: {}", path);
            return false;
        }
        
        return true;
    }
    
    /**
     * Validate directory exists and is readable
     */
    public static boolean isValidDirectory(Path path) {
        if (!Files.exists(path)) {
            logger.warn("Directory does not exist: {}", path);
            return false;
        }
        
        if (!Files.isDirectory(path)) {
            logger.warn("Path is not a directory: {}", path);
            return false;
        }
        
        if (!Files.isReadable(path)) {
            logger.warn("Directory is not readable: {}", path);
            return false;
        }
        
        return true;
    }
    
    /**
     * Get file extension
     */
    public static String getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1).toLowerCase() : "";
    }
    
    /**
     * Check if file has specific extension
     */
    public static boolean hasExtension(Path path, String... extensions) {
        String fileExtension = getFileExtension(path);
        for (String ext : extensions) {
            if (fileExtension.equals(ext.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Log file information for debugging
     */
    public static void logFileInfo(Path path) {
        if (Files.exists(path)) {
            try {
                logger.debug("File info for: {}", path);
                logger.debug("  Exists: {}", Files.exists(path));
                logger.debug("  Is Regular File: {}", Files.isRegularFile(path));
                logger.debug("  Is Directory: {}", Files.isDirectory(path));
                logger.debug("  Is Readable: {}", Files.isReadable(path));
                logger.debug("  Is Writable: {}", Files.isWritable(path));
                logger.debug("  Size: {} bytes", Files.size(path));
                logger.debug("  Extension: {}", getFileExtension(path));
            } catch (Exception e) {
                logger.warn("Error getting file info for: {}", path, e);
            }
        } else {
            logger.debug("File does not exist: {}", path);
        }
    }
}