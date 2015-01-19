package com.kopiright.vkopi.lib.visual;

import java.io.File;
import java.util.EventListener;

public interface FileProductionListener extends EventListener {
	 
  /**
   * This notification tells listeners that
   * the report file is produced and the download process should
   * start
   */
  public void fileProduced(File file);
}
