package com.seadde.alipaycordovaplugin;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Message;

import com.alipay.sdk.app.PayTask;


public class Alipay extends CordovaPlugin{

	public static final String ERROR_ALI_NOT_INSTALLED = "此终端不存在支付宝认证账号";
	
	//商户PID
	public static final String PARTNER = "";
	//商户收款账号
	public static final String SELLER = "";
	//商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "";
	//支付宝公钥
	public static final String RSA_PUBLIC = "";
	
	public static final String NOTIFY_URL = "";
	
	public static CallbackContext currentCallbackContext;
	
	private Activity activity = cordova.getActivity();
	
	@Override
	public boolean execute(String action, JSONArray args,final CallbackContext callbackContext) throws JSONException {
		
		if (action.equals("aliPay")) {
			// aliPay pay
			return aliPay(args, callbackContext);
		} else if(action.equals("checkAccount")){
			
			return checkAccount(callbackContext);
			//callbackContext.success("checkAccount--true");
			//currentCallbackContext = callbackContext;
			//return true;
		}
		
		return super.execute(action, args, callbackContext);
	}
	
	protected boolean aliPay(JSONArray args,final CallbackContext callbackContext) throws JSONException{
		final JSONObject params = args.getJSONObject(0);
		
		String outTradeNo = params.getString("guarantNo");
		// 订单
		String orderInfo = getOrderInfo("手机延保-测试", "手机延保的详细描述-测试", "0.01",outTradeNo);
		
		// 对订单做RSA 签名
		String sign = sign(orderInfo);
		
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
		
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(activity);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);
				callbackContext.success(result);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
		
		//callbackContext.success("true");
		
		currentCallbackContext = callbackContext;
		
		return true;
		
	}
	
	
	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getOrderInfo(String subject, String body, String price,String outTradeNo) {
		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + outTradeNo + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + NOTIFY_URL
				+ "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		//orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}
	
	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}
	
	
	protected boolean checkAccount(CallbackContext callbackContext){
		
		try {
			PayTask payTask = new PayTask(activity);
			callbackContext.success("payTask init success.");
			
			if (payTask == null) {
				callbackContext.error("payTask is null.");
				return false;
			}
			Boolean result = payTask.checkAccountIfExist();
			
			if (result == null) {
				callbackContext.error("result is null.");
				return false;
			}
			
			if (result) {
				callbackContext.error(ERROR_ALI_NOT_INSTALLED);
				return false;
			} else {
				callbackContext.success("true");
			}
			
		} catch(Exception e) {
			callbackContext.error("payTask is null." + e.getMessage());
			return false; 
		}
		
		callbackContext.success("checkAccount--true");
		currentCallbackContext = callbackContext;
		return true;
	}
}
