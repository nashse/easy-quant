package com.zyf.common.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HTTP工具类
 *
 * @author yuanfeng.z
 * @date 2020/7/27 21:44
 */
public class HttpUtil {

	/**
	 * doGet
	 * 
	 * @param url
	 * @return
	 */
	public static String doGet(String url)
	{
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet get = new HttpGet(url);
		String body = null;
		try
		{
			HttpResponse httpresponse = httpclient.execute(get);
			HttpEntity entity = httpresponse.getEntity();
			body = EntityUtils.toString(entity);
		} catch (ClientProtocolException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fmtOut(url, body);
		return body;
	}

	/**
	 * doPost
	 * 
	 * @param url
	 * @param parms
	 * @return
	 */
	public static String doPost(String url, Map<String, String> parms)
	{
		CookieStore cookieStore = new BasicCookieStore();
		//cookieStore.addCookie(cookie);
		HttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore)
				//todo 代理，注意上线打包关闭
//				.setProxy(new HttpHost("127.0.0.1", 7890))
				.build();
		HttpPost post = new HttpPost(url);
		String body = null;
		try
		{
			List<NameValuePair> pairs = new ArrayList<>();
			for (String key : parms.keySet())
			{
				pairs.add(new BasicNameValuePair(key, parms.get(key)));
			}
			post.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
			HttpResponse httpresponse = httpClient.execute(post);
			HttpEntity responseEntity = httpresponse.getEntity();
			body = EntityUtils.toString(responseEntity);
			return body;
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void fmtOut(String url, String ret)
	{
		System.out.println("==============start=================");
		System.out.println(String.format("request url=%s", url));
		System.out.println("methd:GET");
		System.out.println(String.format("response %s", ret));
		System.out.println("==============end===================");
	}

	public static void fmtOut(String url, String parms, String ret)
	{
		System.out.println("==============start=================");
		System.out.println(String.format("request url=%s", url));
		System.out.println("methd:POST");
		System.out.println(String.format("parms: %s", parms));
		System.out.println(String.format("response %s", ret));
		System.out.println("==============end===================");
	}
}
