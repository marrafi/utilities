import java.io.BufferedReader;
import java.io.InputStreamReader;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


		String url = "http://solr-master:8983/solr/master_outiz_Product/stringsuggest?q=vetement&qt=%2Fstringsuggest&spellcheck.dictionary=fr&spellcheck.q=vetement&wt=javabin&version=2";

		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);

		// add request header
		request.addHeader("Connection", "Keep-Alive");
		request.addHeader("User-Agent", "Solr[org.apache.solr.client.solrj.impl.HttpSolrServer] 1.0")
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