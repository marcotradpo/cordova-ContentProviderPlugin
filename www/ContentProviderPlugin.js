function ContentProviderPlugin() {
}

var pluginMethods = [
  "insertUser",
  "updateUser",
  "queryUser"
];

pluginMethods.forEach(function (pluginMethod) {
  ContentProviderPlugin.prototype[pluginMethod] = function (jsonArg, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "ContentProviderPlugin", pluginMethod, [jsonArg]);
  };
});

ContentProviderPlugin.install = function () {
  console.log('Content Provider user plugin');
  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.contentproviderplugin = new ContentProviderPlugin();
  return window.plugins.contentproviderplugin;
};

cordova.addConstructor(ContentProviderPlugin.install);
