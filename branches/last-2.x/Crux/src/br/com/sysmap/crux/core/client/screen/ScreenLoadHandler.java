package br.com.sysmap.crux.core.client.screen;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler interface called when screen is loaded.
 */
public interface ScreenLoadHandler extends EventHandler {

  /**
   * Called when Screen is completely loaded.
   */
  void onLoad(ScreenLoadEvent screenLoadEvent);
}