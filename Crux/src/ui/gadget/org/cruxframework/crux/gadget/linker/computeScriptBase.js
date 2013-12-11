/** Determine our own script's URL from the manifest's 
 * This function produces one side-effect, it sets base to the module's
 * base url.
 */
function computeScriptBase() {
  var base = $wnd.gadgets.util.getUrlParameters()['url'];
  base = base.substring(0,  base.lastIndexOf('/') + 1);
  return base;
}
