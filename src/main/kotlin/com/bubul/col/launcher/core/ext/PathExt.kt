package com.bubul.col.launcher.core.ext

import java.nio.file.Files
import java.nio.file.Path

/**
 * Wraps Files.exists
 */
fun Path.exists() : Boolean = Files.exists(this)

/**
 * Wraps Files.isDirectory
 */
fun Path.isFile() : Boolean = !Files.isDirectory(this)

/**
 * Delete a Path object
 */
fun Path.delete() : Boolean {
    return if(isFile() && exists()){
        //Actual delete operation
        Files.delete(this)
        true
    } else {
        false
    }
}