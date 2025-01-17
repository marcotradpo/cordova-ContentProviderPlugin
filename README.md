## cordova-ContentProviderPlugin

A generic Cordova plugin for querying Content Providers on Android devices.

Through Cordova Repo (stable):

    cordova plugin add com.iooota.cordovaplugin.contentproviderplugin

Through Github Repo (latest):

    cordova plugin add https://github.com/iooota/cordova-ContentProviderPlugin

## Example Usage

    window.plugins.testcameraplugin.queryWebSocket({
    	}, function (data) {
    		console.log(JSON.stringify(data));
    	}, function (err) {
    		console.log("error query");
    	});

#### Licence

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
