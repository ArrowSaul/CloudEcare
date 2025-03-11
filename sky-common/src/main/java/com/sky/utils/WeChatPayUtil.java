package com.sky.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.properties.WeChatProperties;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * 微信支付工具类
 */
@Component
public class WeChatPayUtil {

    //微信支付下单接口地址
    public static final String JSAPI = "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi";

    //申请退款接口地址
    public static final String REFUNDS = "https://api.mch.weixin.qq.com/v3/refund/domestic/refunds";

    @Autowired
    private WeChatProperties weChatProperties;

    /**
     * 获取调用微信接口的客户端工具对象
     *
     * @return
     */
    private CloseableHttpClient getClient() {
        PrivateKey merchantPrivateKey = null;
        try {
            // 尝试多种路径加载证书文件
            String privateKeyFileName = weChatProperties.getPrivateKeyFilePath();
            String certFileName = weChatProperties.getWeChatPayCertFilePath();
            
            // 先尝试从应用根目录加载
            File privateKeyFile = new File(privateKeyFileName);
            File certFile = new File(certFileName);
            
            // 如果在根目录找不到，尝试从多个可能的路径查找
            if (!privateKeyFile.exists()) {
                File workingDir = new File(".");
                System.out.println("当前工作目录: " + workingDir.getAbsolutePath());
                
                // 尝试从/app路径加载
                privateKeyFile = new File("/app/" + privateKeyFileName);
                certFile = new File("/app/" + certFileName);
                
                // 如果还是找不到，尝试从类路径加载
                if (!privateKeyFile.exists()) {
                    System.out.println("尝试从类路径加载证书");
                    try {
                        ClassPathResource privateKeyResource = new ClassPathResource(privateKeyFileName);
                        ClassPathResource certResource = new ClassPathResource(certFileName);
                        
                        merchantPrivateKey = PemUtil.loadPrivateKey(privateKeyResource.getInputStream());
                        X509Certificate x509Certificate = PemUtil.loadCertificate(certResource.getInputStream());
                        List<X509Certificate> wechatPayCertificates = Arrays.asList(x509Certificate);
                        
                        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                                .withMerchant(weChatProperties.getMchid(), weChatProperties.getMchSerialNo(), merchantPrivateKey)
                                .withWechatPay(wechatPayCertificates);
                                
                        return builder.build();
                    } catch (Exception e) {
                        System.err.println("从类路径加载证书失败: " + e.getMessage());
                    }
                    
                    // 列出所有可能的路径以便调试
                    System.out.println("在以下目录查找证书文件:");
                    System.out.println("1. " + new File(privateKeyFileName).getAbsolutePath());
                    System.out.println("2. " + new File("/app/" + privateKeyFileName).getAbsolutePath());
                    
                    throw new FileNotFoundException("无法找到证书文件，已尝试多种路径");
                }
            }
            
            // 记录文件路径和是否存在，方便排查问题
            System.out.println("privateKeyFile路径: " + privateKeyFile.getAbsolutePath() + ", 文件存在: " + privateKeyFile.exists());
            System.out.println("certFile路径: " + certFile.getAbsolutePath() + ", 文件存在: " + certFile.exists());
            
            //merchantPrivateKey商户API私钥，如何加载商户API私钥请看常见问题
            merchantPrivateKey = PemUtil.loadPrivateKey(new FileInputStream(privateKeyFile));
            //加载平台证书文件
            X509Certificate x509Certificate = PemUtil.loadCertificate(new FileInputStream(certFile));
            //wechatPayCertificates微信支付平台证书列表。你也可以使用后面章节提到的"定时更新平台证书功能"，而不需要关心平台证书的来龙去脉
            List<X509Certificate> wechatPayCertificates = Arrays.asList(x509Certificate);

            WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                    .withMerchant(weChatProperties.getMchid(), weChatProperties.getMchSerialNo(), merchantPrivateKey)
                    .withWechatPay(wechatPayCertificates);

            // 通过WechatPayHttpClientBuilder构造的HttpClient，会自动的处理签名和验签
            CloseableHttpClient httpClient = builder.build();
            return httpClient;
        } catch (Exception e) {
            // 增强错误日志输出，便于排查
            System.err.println("获取微信支付客户端失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 发送post方式请求
     *
     * @param url
     * @param body
     * @return
     */
    private String post(String url, String body) throws Exception {
        CloseableHttpClient httpClient = getClient();

        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
        httpPost.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        httpPost.addHeader("Wechatpay-Serial", weChatProperties.getMchSerialNo());
        httpPost.setEntity(new StringEntity(body, "UTF-8"));

        CloseableHttpResponse response = httpClient.execute(httpPost);
        try {
            String bodyAsString = EntityUtils.toString(response.getEntity());
            return bodyAsString;
        } finally {
            httpClient.close();
            response.close();
        }
    }

    /**
     * 发送get方式请求
     *
     * @param url
     * @return
     */
    private String get(String url) throws Exception {
        CloseableHttpClient httpClient = getClient();

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
        httpGet.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        httpGet.addHeader("Wechatpay-Serial", weChatProperties.getMchSerialNo());

        CloseableHttpResponse response = httpClient.execute(httpGet);
        try {
            String bodyAsString = EntityUtils.toString(response.getEntity());
            return bodyAsString;
        } finally {
            httpClient.close();
            response.close();
        }
    }

    /**
     * jsapi下单
     *
     * @param orderNum    商户订单号
     * @param total       总金额
     * @param description 商品描述
     * @param openid      微信用户的openid
     * @return
     */
    private String jsapi(String orderNum, BigDecimal total, String description, String openid) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appid", weChatProperties.getAppid());
        jsonObject.put("mchid", weChatProperties.getMchid());
        jsonObject.put("description", description);
        jsonObject.put("out_trade_no", orderNum);
        jsonObject.put("notify_url", weChatProperties.getNotifyUrl());

        JSONObject amount = new JSONObject();
        amount.put("total", total.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).intValue());
        amount.put("currency", "CNY");

        jsonObject.put("amount", amount);

        JSONObject payer = new JSONObject();
        payer.put("openid", openid);

        jsonObject.put("payer", payer);

        String body = jsonObject.toJSONString();
        return post(JSAPI, body);
    }

    /**
     * 小程序支付
     *
     * @param orderNum    商户订单号
     * @param total       金额，单位 元
     * @param description 商品描述
     * @param openid      微信用户的openid
     * @return
     */
    public JSONObject pay(String orderNum, BigDecimal total, String description, String openid) throws Exception {
        try {
            //统一下单，生成预支付交易单
            String bodyAsString = jsapi(orderNum, total, description, openid);
            //解析返回结果
            JSONObject jsonObject = JSON.parseObject(bodyAsString);
            System.out.println("微信支付统一下单结果:" + jsonObject);

            String prepayId = jsonObject.getString("prepay_id");
            if (prepayId != null) {
                String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
                String nonceStr = RandomStringUtils.randomNumeric(32);
                ArrayList<Object> list = new ArrayList<>();
                list.add(weChatProperties.getAppid());
                list.add(timeStamp);
                list.add(nonceStr);
                list.add("prepay_id=" + prepayId);
                //二次签名，调起支付需要重新签名
                StringBuilder stringBuilder = new StringBuilder();
                for (Object o : list) {
                    stringBuilder.append(o).append("\n");
                }
                String signMessage = stringBuilder.toString();
                byte[] message = signMessage.getBytes();

                Signature signature = Signature.getInstance("SHA256withRSA");
                
                // 尝试多种路径加载
                String privateKeyFileName = weChatProperties.getPrivateKeyFilePath();
                File privateKeyFile = new File(privateKeyFileName);
                
                if (!privateKeyFile.exists()) {
                    privateKeyFile = new File("/app/" + privateKeyFileName);
                    if (!privateKeyFile.exists()) {
                        try {
                            ClassPathResource privateKeyResource = new ClassPathResource(privateKeyFileName);
                            signature.initSign(PemUtil.loadPrivateKey(privateKeyResource.getInputStream()));
                        } catch (Exception e) {
                            throw new FileNotFoundException("无法找到证书文件: " + privateKeyFileName);
                        }
                    } else {
                        signature.initSign(PemUtil.loadPrivateKey(new FileInputStream(privateKeyFile)));
                    }
                } else {
                    signature.initSign(PemUtil.loadPrivateKey(new FileInputStream(privateKeyFile)));
                }
                
                signature.update(message);
                String packageSign = Base64.getEncoder().encodeToString(signature.sign());

                //构造数据给微信小程序，用于调起微信支付
                JSONObject jo = new JSONObject();
                jo.put("timeStamp", timeStamp);
                jo.put("nonceStr", nonceStr);
                jo.put("package", "prepay_id=" + prepayId);
                jo.put("signType", "RSA");
                jo.put("paySign", packageSign);

                return jo;
            }
            return jsonObject;
        } catch (Exception e) {
            System.err.println("微信支付调用异常: " + e.getMessage());
            e.printStackTrace();
            // 返回带错误信息的JSON对象
            JSONObject errorJson = new JSONObject();
            errorJson.put("code", "ERROR");
            errorJson.put("message", e.getMessage());
            return errorJson;
        }
    }

    /**
     * 申请退款
     *
     * @param outTradeNo    商户订单号
     * @param outRefundNo   商户退款单号
     * @param refund        退款金额
     * @param total         原订单金额
     * @return
     */
    public String refund(String outTradeNo, String outRefundNo, BigDecimal refund, BigDecimal total) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("out_trade_no", outTradeNo);
        jsonObject.put("out_refund_no", outRefundNo);

        JSONObject amount = new JSONObject();
        amount.put("refund", refund.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).intValue());
        amount.put("total", total.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).intValue());
        amount.put("currency", "CNY");

        jsonObject.put("amount", amount);
        jsonObject.put("notify_url", weChatProperties.getRefundNotifyUrl());

        String body = jsonObject.toJSONString();

        //调用申请退款接口
        return post(REFUNDS, body);
    }
}
