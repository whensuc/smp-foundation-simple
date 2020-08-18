package cn.com.dyg.work.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import cn.com.dyg.work.common.BusinessException;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;



public class HttpClientRequest {

	public static final String TYPE_FORM = "form";
	public static final String TYPE_JSON = "json";
	public static final String TYPE_XML = "xml";

	private static HttpClientRequest  instance;
	private CloseableHttpClient httpClient;
	private RequestConfig requestConfig;
	private HttpClientRequest(){
		if (httpClient == null) {
			PoolingHttpClientConnectionManager clientConnectionManager = new PoolingHttpClientConnectionManager();
			clientConnectionManager.setValidateAfterInactivity(2000);//检测有效连接的间隔
			clientConnectionManager.setMaxTotal(1000);//设定连接池最大数量
			clientConnectionManager.setDefaultMaxPerRoute(100);//设定默认单个路由的最大连接数（由于本处只使用一个路由地址所以设定为连接池大小）
			//httpClient = HttpClients.createMinimal(clientConnectionManager);
			httpClient=HttpClients.custom().setConnectionManager(clientConnectionManager).build();
			requestConfig = RequestConfig.custom()
					.setConnectionRequestTimeout(10000).setSocketTimeout(10000)
					.setConnectTimeout(10000).build();

		}
	};
	public static HttpClientRequest  newInstance(){
		if(instance==null){
			synchronized (HttpClientRequest.class) {
				if(instance == null) {
					instance = new HttpClientRequest();
				}
			}
		}
		return instance;
	}


	public String get(String url)throws BusinessException {
		//CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		HttpGet httpget = null;
        try{
		httpget = new HttpGet(url);
		// 执行http请求
		response = httpClient.execute(httpget);
		resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        }catch(Exception e){
        	throw new BusinessException(e.getMessage());
        }finally{
        	close(response,httpget);
        }
        return resultString;
	}

	public String get(String url,Map<String,String> headerparam)throws BusinessException{
		//CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		HttpGet httpget = null;
		try{
		httpget = new HttpGet(url);
		for(String key : headerparam.keySet()){
			httpget.addHeader(key,headerparam.get(key));
		}
		response = httpClient.execute(httpget);
		resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        }catch(Exception e){
        	throw new BusinessException(e.getMessage());
        }finally{
			close(response,httpget);
        }
        return resultString;
	}

	public byte[] getImage(String url,Map<String,String> headerparam)throws BusinessException{
		//CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		HttpGet httpget = null;
        try{
		httpget = new HttpGet(url);
		for(String key : headerparam.keySet()){
			httpget.addHeader(key,headerparam.get(key));
		}
		response = httpClient.execute(httpget);
		return EntityUtils.toByteArray(response.getEntity());
        }catch(Exception e){
        	throw new BusinessException(e.getMessage());
        }finally{
			close(response,httpget);
        }
	}

	public String post(String url,Map<String,String> headerparam,Map<String,String> params)throws BusinessException{

		//CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		HttpPost httpPost = new HttpPost(url);
		try{

//			RequestConfig config = RequestConfig.custom().setConnectTimeout(1000) //连接超时时间
//					.setConnectionRequestTimeout(1000) //从连接池中取的连接的最长时间
//					.setSocketTimeout(10 *1000) //数据传输的超时时间
					//.setStaleConnectionCheckEnabled(true) //提交请求前测试连接是否可用
//					.build();
//			httpPost.setConfig(config);
			Optional.ofNullable(headerparam).ifPresent(h->{
				for(String key : headerparam.keySet()){
					httpPost.addHeader(key,headerparam.get(key));
				}
			});
			// 创建参数列表
			if (params != null) {
				List<NameValuePair> paramList = new ArrayList<NameValuePair>();
				for (String key : params.keySet()) {
					paramList.add(new BasicNameValuePair(key, params.get(key)));
				}
				// 模拟表单
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList,"utf-8");
				httpPost.setEntity(entity);
			}
			// 执行http请求
			response = httpClient.execute(httpPost);

			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
		}catch(Exception e){
			throw new BusinessException(e.getMessage());
		}finally{
			close(response,httpPost);
		}
		return resultString;
	}

	public String post(String url,Map<String,String> params)throws BusinessException{

		//CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		HttpPost httpPost = null;
		try{
			httpPost = new HttpPost(url);
			// 创建参数列表
			if (params != null) {
				List<NameValuePair> paramList = new ArrayList<NameValuePair>();
				for (String key : params.keySet()) {
					paramList.add(new BasicNameValuePair(key, params.get(key)));
				}
				// 模拟表单
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList,"utf-8");
				httpPost.setEntity(entity);
			}
			// 执行http请求
			response = httpClient.execute(httpPost);

			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
		}catch(Exception e){
			throw new BusinessException(e.getMessage());
		}finally{
			close(response,httpPost);
		}
		return resultString;
	}



	public  String postJson(String url, String json) {
		//CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		HttpPost httpPost = null;
		try {
			// 创建Http Post请求
			httpPost = new HttpPost(url);
			// 创建请求内容
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			httpPost.setEntity(entity);
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(e.getMessage());
		}finally{
			close(response,httpPost);
        }
		return resultString;
	}
	public  String postJson(String url, String json,Map<String,String> headerparam) {
		//CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		HttpPost httpPost = null;
		try {
			// 创建Http Post请求
			httpPost = new HttpPost(url);
			for(String key : headerparam.keySet()){
				httpPost.addHeader(key,headerparam.get(key));
			}
			// 创建请求内容
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			httpPost.setEntity(entity);
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}finally{
			close(response,httpPost);
        }
		return resultString;
	}

	public  String putJson(String url, String json,Map<String,String> headerparam) {
		//CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		HttpPut httpPut = null;
		try {
			// 创建Http Put请求
			httpPut = new HttpPut(url);
			for(String key : headerparam.keySet()){
				httpPut.addHeader(key,headerparam.get(key));
			}
			// 创建请求内容
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			httpPut.setEntity(entity);
			// 执行http请求
			response = httpClient.execute(httpPut);
			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}finally{
			close(response,httpPut);
		}
		return resultString;
	}

	public String delete(String url,Map<String,String> headerparam)throws BusinessException{

		//CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		HttpDelete httpDel = null;
		try{
			httpDel = new HttpDelete(url);
			// 创建参数列表
			if (headerparam != null) {
				for(String key : headerparam.keySet()){
					httpDel.addHeader(key,headerparam.get(key));
				}
				// 模拟表单
				//UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList,"utf-8");
				//httpPost.setEntity(entity);
			}
			// 执行http请求
			response = httpClient.execute(httpDel);
			if(response!=null&&response.getEntity()!=null){
				resultString = EntityUtils.toString(response.getEntity(), "utf-8");
			}

		}catch(Exception e){
			throw new BusinessException(e.getMessage());
		}finally{
			close(response,httpDel);
		}
		return resultString;
	}

	public String deleteJson(String url,String json,Map<String,String> headerparam)throws BusinessException{

		//CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		MyHttpDelete httpDel = null;
		try{
			httpDel = new MyHttpDelete(url);
			// 创建参数列表
			if (headerparam != null) {
				for(String key : headerparam.keySet()){
					httpDel.addHeader(key,headerparam.get(key));
				}
				StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
				httpDel.setEntity(entity);

			}
			// 执行http请求
			response = httpClient.execute(httpDel);
			if(response!=null&&response.getEntity()!=null){
				resultString = EntityUtils.toString(response.getEntity(), "utf-8");
			}

		}catch(Exception e){
			throw new BusinessException(e.getMessage());
		}finally{
			close(response,httpDel);
		}
		return resultString;
	}

	public  String postXml(String url, String xml,Map<String,String> headerparam) {
		//CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		HttpPost httpPost = new HttpPost(url);
		try {
			// 创建Http Post请求
			RequestConfig config = RequestConfig.custom().setConnectTimeout(1000) //连接超时时间
					.setConnectionRequestTimeout(1000) //从连接池中取的连接的最长时间
					.setSocketTimeout(10 *1000) //数据传输的超时时间
					//.setStaleConnectionCheckEnabled(true) //提交请求前测试连接是否可用
					.build();
			httpPost.setConfig(config);
			Optional.ofNullable(headerparam).ifPresent(h->{
				for(String key : headerparam.keySet()){
					httpPost.addHeader(key,headerparam.get(key));
				}
			});

			// 创建请求内容
			StringEntity entity = new StringEntity(xml, ContentType.APPLICATION_XML);
			httpPost.setEntity(entity);
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(e.getMessage());
		}finally{
			close(response,httpPost);
		}
		return resultString;
	}

	public  String  request(String url,String method,Map<String,String> headerparam,
							String type,Map<String,String> formparam,String bodyparam) {
		String result = null;
		if("GET".equalsIgnoreCase(method)) {
			result = get(url, headerparam);
		}else if("POST".equalsIgnoreCase(method)) {
			switch(type) {
				case TYPE_FORM:
					result = post(url,headerparam,formparam);
					break;
				case TYPE_JSON:
					result = postJson(url,bodyparam,headerparam);
					break;
				case TYPE_XML:
					result = postXml(url,bodyparam,headerparam);
					break;
			}
		}else if("PUT".equalsIgnoreCase(method)) {
			result = putJson(url,bodyparam,headerparam);
		}else if("DELETE".equalsIgnoreCase(method)) {
			result = delete(url,headerparam);
		}
		return result;
	}

	private void close(CloseableHttpResponse response,HttpRequestBase request){
		try {
			if(response.getStatusLine().getStatusCode()!=200)request.abort();
			if(response!=null)
				response.close();
		}catch (IOException e){
			e.printStackTrace();
		}

	}

}
