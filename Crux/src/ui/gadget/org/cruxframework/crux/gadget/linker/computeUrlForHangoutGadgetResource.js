function computeUrlForResource(resource) {
  var cacheSpec;
  if (resource.match(/\.cache\.(js|html)$/)) {
        // Allow GWT resources marked named as cacheable to be cached for one year.
        cacheSpec = {refreshInterval:31536000};
  }
  /* Prepend anything that is not a fully qualified URL with the module base URL */
  if (!resource.match(/^[a-zA-Z]+:\/\//)) {
          resource = "__DEPLOY_URL__" + __MODULE_FUNC__.__moduleName + "/" + resource;
  }
  return $wnd.gadgets.io.getProxyUrl(resource, cacheSpec);
}
