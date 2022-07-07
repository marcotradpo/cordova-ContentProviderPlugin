function TestCameraPlugin() {
}

var pluginMethods = [
  "queryWebSocket"
];

pluginMethods.forEach(function (pluginMethod) {
  TestCameraPlugin.prototype[pluginMethod] = function (jsonArg, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "TestCameraPlugin", pluginMethod, [jsonArg]);
  };
});

TestCameraPlugin.install = function () {
  console.log('TestCameraPlugin user plugin');
  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.testcameraplugin = new TestCameraPlugin();
  return window.plugins.testcameraplugin;
};

cordova.addConstructor(TestCameraPlugin.install);
