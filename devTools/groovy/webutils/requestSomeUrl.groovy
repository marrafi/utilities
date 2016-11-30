import java.io.BufferedReader;
import java.io.InputStreamReader;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


		String url = "someurl";

		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);

		// add request header
		request.addHeader("User-Agent", "someuseragent")
		HttpResponse response = client.execute(request);

		println "Sending 'GET' request to URL : " + url
		println "Response Code : " +response.getStatusLine().getStatusCode())
		headers = response.getAllHeaders();
		headerString = ""
		for ( header in headers){
			headerString += header.toString()+ " "
		}
		println "Response headers :" + headerString
		BufferedReader rd = new BufferedReader(
                       new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		println result.toString()