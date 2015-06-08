var exec = require('cordova/exec');

module.exports = {
    
    isInstalled: function (onSuccess, onError) {
		alert(xxxxx);
        exec(onSuccess, onError, "AliPay", "checkAccount", []);
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
    aliPay: function(message, onSuccess, onError) {
        exec(onSuccess, onError, "AliPay", "aliPay", [message]);
    }
};
