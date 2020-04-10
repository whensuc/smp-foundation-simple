package cn.com.dyg.work.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
