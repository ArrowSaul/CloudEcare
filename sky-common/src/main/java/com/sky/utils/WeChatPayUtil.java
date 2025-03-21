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

import java.io.*;
import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.annotation.PostConstruct;

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

    @PostConstruct
    public void init() {
        try {
            System.out.println("=== 微信支付工具类初始化开始 ===");
            System.out.println("当前工作目录: " + new File(".").getAbsolutePath());
            
            // 验证证书配置
            String privateKeyPath = weChatProperties.getPrivateKeyFilePath();
            String certPath = weChatProperties.getWeChatPayCertFilePath();
            
            System.out.println("私钥路径配置: " + privateKeyPath);
            System.out.println("证书路径配置: " + certPath);
            
            // 尝试初始化客户端
            CloseableHttpClient client = getClient();
            if (client == null) {
                System.err.println("!!! 警告：微信支付客户端初始化失败，支付功能可能无法正常工作 !!!");
            } else {
                System.out.println("微信支付客户端初始化成功");
                client.close();
            }
            
            System.out.println("=== 微信支付工具类初始化完成 ===");
        } catch (Exception e) {
            System.err.println("!!! 微信支付工具类初始化异常 !!!");
            e.printStackTrace();
            // 不抛出异常，允许应用继续启动
        }
    }

    /**
     * 加载证书文件
     * @param filePath 文件路径
     * @return InputStream
     * @throws IOException
     */
    private InputStream loadCertFile(String filePath) throws IOException {
        // 1. 先尝试直接加载
        File file = new File(filePath);
        if (file.exists() && file.canRead() && file.length() > 0) {
            System.out.println("从文件系统直接加载证书: " + file.getAbsolutePath());
            System.out.println("文件大小: " + file.length() + " 字节");
            return new FileInputStream(file);
        }

        // 2. 尝试从/app目录加载
        file = new File("/app/" + new File(filePath).getName());
        if (file.exists() && file.canRead() && file.length() > 0) {
            System.out.println("从/app目录加载证书: " + file.getAbsolutePath());
            System.out.println("文件大小: " + file.length() + " 字节");
            return new FileInputStream(file);
        }

        // 3. 尝试从类路径加载
        try {
            ClassPathResource resource = new ClassPathResource(new File(filePath).getName());
            if (resource.exists() && resource.contentLength() > 0) {
                System.out.println("从类路径加载证书: " + resource.getPath());
                System.out.println("资源大小: " + resource.contentLength() + " 字节");
                return resource.getInputStream();
            }
        } catch (Exception e) {
            System.err.println("从类路径加载证书失败: " + e.getMessage());
        }

        // 4. 输出更详细的错误信息
        StringBuilder error = new StringBuilder("无法找到或读取证书文件: " + filePath + "\n");
        error.append("已尝试以下路径：\n");
        
        file = new File(filePath);
        error.append("1. ").append(file.getAbsolutePath())
             .append(" [存在: ").append(file.exists())
             .append(", 可读: ").append(file.exists() && file.canRead())
             .append(", 大小: ").append(file.exists() ? file.length() : 0).append(" 字节]\n");
        
        file = new File("/app/" + new File(filePath).getName());
        error.append("2. ").append(file.getAbsolutePath())
             .append(" [存在: ").append(file.exists())
             .append(", 可读: ").append(file.exists() && file.canRead())
             .append(", 大小: ").append(file.exists() ? file.length() : 0).append(" 字节]\n");
        
        error.append("3. classpath:").append(new File(filePath).getName()).append("\n");
        
        // 输出当前目录信息
        error.append("\n当前目录 (").append(new File(".").getAbsolutePath()).append(") 内容:\n");
        File currentDir = new File(".");
        if (currentDir.exists() && currentDir.isDirectory()) {
            File[] files = currentDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    error.append("- ").append(f.getName())
                         .append(" [").append(f.isDirectory() ? "目录" : "文件")
                         .append(", ").append(f.length()).append(" 字节]\n");
                }
            }
        }
        
        // 输出/app目录信息
        error.append("\n/app 目录内容:\n");
        File appDir = new File("/app");
        if (appDir.exists() && appDir.isDirectory()) {
            File[] files = appDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    error.append("- ").append(f.getName())
                         .append(" [").append(f.isDirectory() ? "目录" : "文件")
                         .append(", ").append(f.length()).append(" 字节]\n");
                }
            }
        }

        throw new FileNotFoundException(error.toString());
    }

    /**
     * 获取调用微信接口的客户端工具对象
     *
     * @return
     */
    private CloseableHttpClient getClient() {
        try {
            String privateKeyPath = weChatProperties.getPrivateKeyFilePath();
            String certPath = weChatProperties.getWeChatPayCertFilePath();
            
            System.out.println("开始加载证书文件...");
            System.out.println("私钥路径: " + privateKeyPath);
            System.out.println("证书路径: " + certPath);
            
            // 加载私钥
            InputStream privateKeyStream = loadCertFile(privateKeyPath);
            PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(privateKeyStream);
            
            // 加载证书
            InputStream certStream = loadCertFile(certPath);
            X509Certificate x509Certificate = PemUtil.loadCertificate(certStream);
            
            // 验证证书
            if (merchantPrivateKey == null) {
                throw new Exception("私钥加载失败");
            }
            if (x509Certificate == null) {
                throw new Exception("证书加载失败");
            }
            
            // 验证证书有效期
            try {
                x509Certificate.checkValidity();
            } catch (Exception e) {
                throw new Exception("证书已过期或尚未生效: " + e.getMessage());
            }
            
            List<X509Certificate> wechatPayCertificates = Arrays.asList(x509Certificate);

            WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                    .withMerchant(weChatProperties.getMchid(), weChatProperties.getMchSerialNo(), merchantPrivateKey)
                    .withWechatPay(wechatPayCertificates);

            System.out.println("证书加载成功，创建HttpClient实例");
            return builder.build();
        } catch (Exception e) {
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
        if (httpClient == null) {
            throw new Exception("无法创建微信支付客户端，请检查证书配置");
        }

        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
        httpPost.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        httpPost.addHeader("Wechatpay-Serial", weChatProperties.getMchSerialNo());
        httpPost.setEntity(new StringEntity(body, "UTF-8"));

        System.out.println("发送POST请求到: " + url);
        System.out.println("请求体: " + body);

        CloseableHttpResponse response = httpClient.execute(httpPost);
        try {
            String bodyAsString = EntityUtils.toString(response.getEntity());
            System.out.println("收到响应: " + bodyAsString);
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
            System.out.println("开始处理支付请求...");
            System.out.println("订单号: " + orderNum);
            System.out.println("金额: " + total);
            System.out.println("描述: " + description);
            System.out.println("openid: " + openid);

            String bodyAsString = jsapi(orderNum, total, description, openid);
            JSONObject jsonObject = JSON.parseObject(bodyAsString);
            System.out.println("微信支付统一下单结果: " + jsonObject);

            String prepayId = jsonObject.getString("prepay_id");
            if (prepayId != null) {
                String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
                String nonceStr = RandomStringUtils.randomNumeric(32);
                ArrayList<Object> list = new ArrayList<>();
                list.add(weChatProperties.getAppid());
                list.add(timeStamp);
                list.add(nonceStr);
                list.add("prepay_id=" + prepayId);

                StringBuilder stringBuilder = new StringBuilder();
                for (Object o : list) {
                    stringBuilder.append(o).append("\n");
                }
                String signMessage = stringBuilder.toString();
                byte[] message = signMessage.getBytes();

                System.out.println("开始进行签名...");
                Signature signature = Signature.getInstance("SHA256withRSA");
                signature.initSign(PemUtil.loadPrivateKey(loadCertFile(weChatProperties.getPrivateKeyFilePath())));
                signature.update(message);
                String packageSign = Base64.getEncoder().encodeToString(signature.sign());
                System.out.println("签名完成");

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
