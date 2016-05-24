package com.shephertz.cumbari.sync;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class JSONParser {

	private InputStream is = null;
	private String json = "";

	public JSONParser()
	{
		 is = null; 
		 json = ""; 
	}
	//{"NewPassword":"0OPtikln","email":"91-8860832504@backstage.yappyapps.com","ConfirmPassword":"0OPtikln"}
		public String getJSONFromUrl(String url, JSONObject postValue) {

		// Making HTTP request
		try {
			// defaultHttpClient
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 20000);
			HttpConnectionParams.setSoTimeout(httpParameters, 20000);
			
			//HttpConnectionParams.set
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpPost httpPost = new HttpPost(url);
			
			StringEntity se = new StringEntity(postValue.toString());
			httpPost.setEntity(se);
			
			httpPost.setHeader("Content-type", "application/json");
			
			HttpResponse httpResponse = httpClient.execute(httpPost);
			
			HttpEntity httpEntity = httpResponse.getEntity();
			
			is = httpEntity.getContent();

		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			
		} catch (ClientProtocolException e)
		{
			e.printStackTrace();
			
		} catch (IOException e) 
		{
			e.printStackTrace();
			
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		try 
		{
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			is.close();
			
			json = sb.toString();
			
			System.out.println(json);
			if(json.startsWith("\""))
			{
				json = json.substring(1, json.length()-1);
			}

			json = removeBackSlash(json);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}


		
		
		
		return json;

	}
	
	
	
	public String getJSONFromUrlForUpdateBand(String url, JSONObject postValue ,String token) {

		// Making HTTP request
		try {
			// defaultHttpClient
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 30000);
			HttpConnectionParams.setSoTimeout(httpParameters, 30000);
			
			//HttpConnectionParams.set
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpPost httpPost = new HttpPost(url);
			
			StringEntity se = new StringEntity(postValue.toString());
			httpPost.setHeader("Content-type", "application/json");
			httpPost.setHeader("Authorization", "Bearer" + token);
			
			
			httpPost.setEntity(se);
			
			
       

			HttpResponse httpResponse = httpClient.execute(httpPost);
			
			HttpEntity httpEntity = httpResponse.getEntity();
			
			is = httpEntity.getContent();

		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			
		} catch (ClientProtocolException e)
		{
			e.printStackTrace();
			
		} catch (IOException e) 
		{
			e.printStackTrace();
			
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		try 
		{
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			is.close();
			
			json = sb.toString();
			
			if(json.startsWith("\""))
			{
				json = json.substring(1, json.length()-1);
			}
			
			json = removeBackSlash(json);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return json;

	}
	
	
	
	
	
	public String getJSONFromUrlPostMethod(String url, String token) 
	{

		// Making HTTP request
		try {
			// defaultHttpClient
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
			HttpConnectionParams.setSoTimeout(httpParameters, 20000);
			
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpPost httpGet = new HttpPost(url);
			httpGet.setHeader("Content-type", "application/json");
			httpGet.setHeader("Authorization", "Bearer " +token);
			
			
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e)
		{
			//e.printStackTrace();
			
		} catch (ClientProtocolException e) 
		{
			//e.printStackTrace();
			
		} catch (IOException e) {
			//e.printStackTrace();
			
		}

		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			//Log.e("Buffer Error", "Error converting result " + e.toString());
		}
		

		if(json.startsWith("\""))
		{
			json = json.substring(1, json.length()-1);
		}
		
		json = removeBackSlash(json);
		
		return json;

	}
	
	
	public String getJSONFromUrlGetMethod(String url)
	{

		// Making HTTP request
		try {
			// defaultHttpClient
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
			HttpConnectionParams.setSoTimeout(httpParameters, 20000);
			
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("Content-type", "application/json");

			
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e)
		{
			//e.printStackTrace();
			
		} catch (ClientProtocolException e) 
		{
			//e.printStackTrace();
			
		} catch (IOException e) {
			//e.printStackTrace();
			
		}

		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			//Log.e("Buffer Error", "Error converting result " + e.toString());
		}
		

		if(json.startsWith("\""))
		{
			json = json.substring(1, json.length()-1);
		}
		
		json = removeBackSlash(json);
		
		return json;

	}





	public String getJSONFromUrlGetMethod(String url,String accessToken)
	{

		// Making HTTP request
		try {
			// defaultHttpClient
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
			HttpConnectionParams.setSoTimeout(httpParameters, 20000);

			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("Content-type", "application/json");
			httpGet.setHeader("Authorization", "Bearer "+accessToken);


			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e)
		{
			//e.printStackTrace();

		} catch (ClientProtocolException e)
		{
			//e.printStackTrace();

		} catch (IOException e) {
			//e.printStackTrace();

		}

		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			//Log.e("Buffer Error", "Error converting result " + e.toString());
		}


		if(json.startsWith("\""))
		{
			json = json.substring(1, json.length()-1);
		}

		json = removeBackSlash(json);

		return json;

	}
	
	public String getJSONFromUrl(String url, String postValue) {

		// Making HTTP request
		try {
			// defaultHttpClient
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 20000);
			HttpConnectionParams.setSoTimeout(httpParameters, 20000);
			
			//HttpConnectionParams.set
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpPost httpPost = new HttpPost(url);
			
			StringEntity se = new StringEntity(postValue.toString());
			httpPost.setEntity(se);
			
			HttpResponse httpResponse = httpClient.execute(httpPost);
			
			HttpEntity httpEntity = httpResponse.getEntity();
			
			is = httpEntity.getContent();

		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			
		} catch (ClientProtocolException e)
		{
			e.printStackTrace();
			
		} catch (IOException e) 
		{
			e.printStackTrace();
			
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		try 
		{
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			is.close();
			
			json = sb.toString();
			
			if(json.startsWith("\""))
			{
				json = json.substring(1, json.length()-1);
			}
			
			json = removeBackSlash(json);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return json;

	}
	

	public String removeBackSlash(String st) {

	    StringBuilder sb = new StringBuilder(st.length());

	    for (int i = 0; i < st.length(); i++) {
	        char ch = st.charAt(i);
	        if (ch == '\\') {
	            char nextChar = (i == st.length() - 1) ? '\\' : st
	                    .charAt(i + 1);
	            // Octal escape?
	            if (nextChar >= '0' && nextChar <= '7') {
	                String code = "" + nextChar;
	                i++;
	                if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
	                        && st.charAt(i + 1) <= '7') {
	                    code += st.charAt(i + 1);
	                    i++;
	                    if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
	                            && st.charAt(i + 1) <= '7') {
	                        code += st.charAt(i + 1);
	                        i++;
	                    }
	                }
	                sb.append((char) Integer.parseInt(code, 8));
	                continue;
	            }
	            switch (nextChar) {
	            case '\\':
	                ch = '\\';
	                break;
	            case 'b':
	                ch = '\b';
	                break;
	            case 'f':
	                ch = '\f';
	                break;
	            case 'n':
	                ch = '\n';
	                break;
	            case 'r':
	                ch = '\r';
	                break;
	            case 't':
	                ch = '\t';
	                break;
	            case '\"':
	                ch = '\"';
	                break;
	            case '\'':
	                ch = '\'';
	                break;
	            // Hex Unicode: u????
	            case 'u':
	                if (i >= st.length() - 5) {
	                    ch = 'u';
	                    break;
	                }
	                int code = Integer.parseInt(
	                        "" + st.charAt(i + 2) + st.charAt(i + 3)
	                                + st.charAt(i + 4) + st.charAt(i + 5), 16);
	                sb.append(Character.toChars(code));
	                i += 5;
	                continue;
	            }
	            i++;
	        }
	        sb.append(ch);
	    }
	    return sb.toString();
	}

}