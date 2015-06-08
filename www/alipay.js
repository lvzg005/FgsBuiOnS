var exec = require('cordova/exec');

module.exports = {
    
    isInstalled: function (onSuccess, onError) {
        exec(onSuccess, onError, "Alipay", "checkAccount", []);
    },

    /**
     * pay 
     *
     * @example
     * <code>
     * Alipay.aliPay({
     *    		ouTradeNo:'',
	 *    		ip: ''
     * 		}, function () {
     *     		alert("Success");
     * 		}, function (reason) {
     *     		alert("Failed: " + reason);
     * 		});
     * </code>
     */  
    wxPay: function(message, onSuccess, onError) {
        exec(onSuccess, onError, "Alipay", "aliPay", [message]);
    }
};
