package br.com.org.jswitch.cfg;

import java.io.File;
/**
 * Class to represent a FileChangeListener abstraction
 * @author Anderson
 *
 */
public interface FileChangeListener {
  /**
   * Invoked when a file changes.
   * 
   * @param fileName
   *          name of changed file.
   */
  public void fileChanged(File file);
}