function ContentProviderPlugin() {
}

var pluginMethods = [
  "create",
  "query"
];

pluginMethods.forEach(function (pluginMethod) {
  ContentProviderPlugin.prototype[pluginMethod] = function (jsonArg, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "ContentProviderPlugin", pluginMethod, [jsonArg]);
  };
});

ContentProviderPlugin.install = function () {
  console.log('test custom plugin 2-------');
  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.contentproviderplugin = new ContentProviderPlugin();
  return window.plugins.contentproviderplugin;
};

cordova.addConstructor(ContentProviderPlugin.install);
