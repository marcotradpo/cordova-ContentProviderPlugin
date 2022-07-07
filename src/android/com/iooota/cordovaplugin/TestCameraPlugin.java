package com.iooota.cordovaplugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ContentProviderPlugin extends CordovaPlugin {
	private String WRONG_PARAMS = "Wrong parameters.";
	private String UNKNOWN_ERROR = "Unknown error.";
	private String URITEST = "uri di prova";



	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		final JSONArray methodArgs = args;
		final CallbackContext callback = callbackContext;
		final JSONObject queryArgs = methodArgs.getJSONObject(0);

		if (queryArgs == null) {
			callback.error(WRONG_PARAMS);
			return false;
		}

		if (action.equals("queryWebSocket") ) {
			cordova.getThreadPool().execute(new Runnable() {
			public void run() {
				if (action.equals("queryWebSocket")) {
					
					JSONObject resultUri = new JSONObject();

					try {
						resultUri.put("userResultUri", URITEST);
					} catch (JSONException e) {
						e.printStackTrace();
						callback.error(UNKNOWN_ERROR);
						return;
					}

					callback.success(resultUri);
				}
			};
			});
			return true;
		}

		return false;
	}

}
