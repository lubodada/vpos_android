package com.ven.pos.Util;

import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;

import android.util.Log;

import com.ven.pos.GlobalContant;
import com.ven.pos.alipay.util.AlipayCore;
import com.ven.pos.alipay.util.httpClient.HttpProtocolHandler;
import com.ven.pos.alipay.util.httpClient.HttpRequest;
import com.ven.pos.alipay.util.httpClient.HttpResponse;
import com.ven.pos.alipay.util.httpClient.HttpResultType;

/* *
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class HttpConnection {

    private static Map<String, String> buildRequestPara(
            Map<String, String> sParaTemp) {
        // 除去数组中的空值和签名参数
        Map<String, String> sPara = AlipayCore.paraFilter(sParaTemp);
        // 生成签名结果
        return sPara;
    }

    /**
     * 建立请求，以模拟远程HTTP的POST请求方式构造并获取支付宝的处理结果
     * 如果接口中没有上传文件参数，那么strParaFileName与strFilePath设置为空值 如：buildRequest("",
     * "",sParaTemp)
     *
     * @param strParaFileName 文件类型的参数名
     * @param strFilePath     文件路径
     * @param sParaTemp       请求参数数组
     * @return 支付宝处理结果
     * @throws Exception
     */
    public static String buildRequest(String Url, Map<String, String> sParaTemp)
            throws Exception {
        // 待请求参数数组
        Map<String, String> sPara = buildRequestPara(sParaTemp);

        HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler
                .getInstance();

        HttpRequest request = new HttpRequest(HttpResultType.BYTES);
        // 设置编码集
        request.setCharset(GlobalContant.instance().aliConfig.input_charset);

        request.setParameters(generatNameValuePair(sPara));
        // request.setUrl(Url+"_input_charset="+GlobalContant.instance().aliConfig.input_charset);
        request.setUrl(Url);
        Log.e("request", String.valueOf(request));
        HttpResponse response = httpProtocolHandler.execute(request, "", "");
        if (response == null) {
            return null;
        }

        String strResult = response.getStringResult();

        return strResult;
    }

    /**
     * MAP类型数组转换成NameValuePair类型
     *
     * @param properties MAP类型数组
     * @return NameValuePair类型数组
     */
    private static NameValuePair[] generatNameValuePair(
            Map<String, String> properties) {
        NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            nameValuePair[i++] = new NameValuePair(entry.getKey(),
                    entry.getValue());
        }

        return nameValuePair;
    }

}
