package com.seadde.alipay.cordova;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class Alipay2 extends CordovaPlugin {
	
	@Override
	public boolean execute(String action, JSONArray args,CallbackContext callbackContext) throws JSONException {
		
		if (action.equals("aliPay")) {
			// aliPay pay
			return aliPay(args, callbackContext);
		} else if(action.equals("checkAccount")){
			
			return checkAccount(callbackContext);
		}
		
		return super.execute(action, args, callbackContext);
	}
	
	private boolean aliPay(JSONArray args,CallbackContext callbackContext) throws JSONException {
		callbackContext.success("aliPay-true");
		return true;
	}
	
	private boolean checkAccount(CallbackContext callbackContext) {
		callbackContext.success("checkAccount-true");
		return true;
	}
}
