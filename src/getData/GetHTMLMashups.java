package getData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;

public class GetHTMLMashups {
	private static Connection connect = null;
	private static Statement statement = null;
	private static PreparedStatement preparedStatement = null;
	private static ResultSet resultSet = null;

	public static void main(String[] args) throws Exception {

		//getMashupAll();
		//getIndividualMashups();
		getMashupSummaryNAll();
		//createMashupFiles();
	}

	//Get & store all the mashups overall info
	public static void getMashupAll() throws NoSuchAlgorithmException, KeyManagementException, IOException{

		for(int i=1;i<=372;i++){

			URL url=new URL("http://www.programmableweb.com/mashups/directory/"+i);
			SSLContext sslctx = SSLContext.getInstance("TLS");
			TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
				public X509Certificate[] getAcceptedIssuers(){return null;}
				public void checkClientTrusted(X509Certificate[] certs, String authType){}
				public void checkServerTrusted(X509Certificate[] certs, String authType){}
			}};
			sslctx.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sslctx.getSocketFactory());
			//HttpsURLConnection yc = (HttpsURLConnection)url.openConnection();
			URLConnection yc = url.openConnection();
			BufferedReader in = null;
			try{
				in = new BufferedReader(
						new InputStreamReader(
								yc.getInputStream()));
			}
			catch(Exception e) {
				continue;
			}

			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("E:/Research Projects/ICSM 2014 project/Data/MashupList/"+i+".html")));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
			{
				bw.write(inputLine+"\n");
			}

			in.close();
			bw.close();
		}
	}

	//use the stored files to get the hyper links of all the mashups
	public static void getIndividualMashups() throws Exception {
		try {

			ArrayList<String> allMashups=new ArrayList<String>();
			final File folder = new File("E:/Research Projects/ICSM 2014 project/Data/MashupList");
			final List<File> fileList = Arrays.asList(folder.listFiles());
			//Get names of all mashups
			for(int i=0;i<fileList.size();i++)
			{
				String line="";
				StringBuilder builder=new StringBuilder();
				final BufferedReader br=new BufferedReader(new FileReader(fileList.get(i)));
				while((line=br.readLine())!=null)
				{
					builder.append(line);
				}

				int count=StringUtils.countMatches(builder, "<a href=\"/mashup/");

				int start=builder.indexOf("<a href=\"/mashup/");
				int end=builder.indexOf("\">",start);
				String mashup=builder.substring(start, end).replace("<a href=\"", "");

				System.out.println(mashup);
				if(!allMashups.contains(mashup)){
					allMashups.add(mashup);
				}

				//System.out.println(count);
				int tagStart=builder.indexOf("<a href=\"/mashup/",end);
				for(int j=0;j<count-1;j++){							
					//System.out.println(temp.indexOf("\"tag\" >",tagStart)+" "+temp.indexOf("</a>",tagStart));
					mashup=builder.substring(builder.indexOf("<a href=\"/mashup/",tagStart),builder.indexOf("\">",tagStart)).replace("<a href=\"", "");
					tagStart=builder.indexOf("<a href=\"/mashup/",builder.indexOf("\">",tagStart));

					if(!mashup.contains("popnew")){
						System.out.println(mashup);
					}

					if(!allMashups.contains(mashup)){
						allMashups.add(mashup);
					}
				}
				//System.exit(1);
			}


			//Fetch HTML pages of mashups
			for(String str:allMashups){

				URL url=new URL("http://www.programmableweb.com"+str);
				SSLContext sslctx = SSLContext.getInstance("TLS");
				TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
					public X509Certificate[] getAcceptedIssuers(){return null;}
					public void checkClientTrusted(X509Certificate[] certs, String authType){}
					public void checkServerTrusted(X509Certificate[] certs, String authType){}
				}};
				sslctx.init(null, trustAllCerts, new SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sslctx.getSocketFactory());
				//HttpsURLConnection yc = (HttpsURLConnection)url.openConnection();
				URLConnection yc = url.openConnection();
				BufferedReader in = null;
				try{
					in = new BufferedReader(
							new InputStreamReader(
									yc.getInputStream()));
				}
				catch(Exception e) {
					continue;
				}

				BufferedWriter bw = new BufferedWriter(new FileWriter(new File("E:/Research Projects/ICSM 2014 project/Data/MashupData/"+
						str.substring(1, str.length()).replace("/", "_").replace("|", "__")+".html")));

				String inputLine;
				while ((inputLine = in.readLine()) != null)
				{
					bw.write(inputLine+"\n");
				}

				in.close();
				bw.close();
			}

		}catch (Exception e) {

			throw e;
		}
	}

	//Get Summary & other data from saved HTML pages
	public static void getMashupSummaryNAll() throws Exception{
		final File folder = new File("E:/Research Projects/ICSM 2014 project/Data/MashupData");
		final List<File> fileList = Arrays.asList(folder.listFiles());

		//Mashups retained for historical purpose must be removed
		ArrayList<String> removeFiles=new ArrayList<String>();
		for(int i=0;i<fileList.size();i++)
		{
			final BufferedReader br=new BufferedReader(new FileReader(fileList.get(i)));
			String line="";
			StringBuilder builder=new StringBuilder();
			while((line=br.readLine())!=null)
			{
				builder.append(line);
				
			}

			if(builder.toString().contains("This profile is retained for historical purposes only.")){
				removeFiles.add(fileList.get(i).toString());
			}
		}
		System.out.println("Remove Files"+removeFiles.size());

		for(int i=0;i<fileList.size();i++)
		{

			//If mashup is old, don't save it
			if(!removeFiles.contains(fileList.get(i).toString())){

				System.out.println(fileList.get(i));
				String line="";
				final BufferedReader br=new BufferedReader(new FileReader(fileList.get(i)));
				String APIs="",tags="",title="",desc="";
				StringBuilder builder=new StringBuilder();
				while((line=br.readLine())!=null)
				{
					builder.append(line);
				}

				//if(builder.contains("<title>")){
				title=builder.substring(builder.indexOf("<title>"),builder.indexOf("</title>")).replace("<title>", "")
						.replace("ProgrammableWeb Mashup Detail", "").replace("-", "").trim().replace("&#039;", "'").replace("&#64257;", "");
				//}
				System.out.println(title);
				if(title.isEmpty()){
					title="";
				}
				//if(line.contains("<h2>Description</h2>")){
				//System.out.println(fileList.get(i));
				desc=builder.substring(builder.indexOf("<p>",builder.indexOf("<h2>Description</h2>")),builder.indexOf("</p>",builder.indexOf("<h2>Description</h2>"))).
						replace("<p>", "").replace("&#039;", "'").replace("&#64257;", "");
				//}
				System.out.println(desc);

				//if(line.contains("<dt>APIs</dt>")){
				if(builder.toString().contains("\"><dt>APIs</dt>")&&builder.toString().contains("<dl class=")){
					String temp=builder.substring(builder.indexOf("<dd",builder.indexOf("<dt>APIs</dt>")),builder.indexOf("</dl>",builder.indexOf("<dd",builder.indexOf("<dt>APIs</dt>"))));
					int count=org.apache.commons.lang3.StringUtils.countMatches(builder.substring(builder.indexOf("<dt>APIs</dt>"), builder.indexOf("</dl>",builder.indexOf("<dt>APIs</dt>"))),"</a>");

					int intStart=temp.indexOf("\">");
					int intFin=temp.indexOf("</a>");
					if(!(intStart==-1&&intFin==-1)){
						//System.out.println(fileList.get(i));
						System.out.println(intStart+" "+intFin);
						String tag=temp.substring(temp.indexOf("\">"),temp.indexOf("</a>",intFin)).replaceAll("\">", "");
						APIs=APIs+"---"+tag;
						System.out.println(tag);

						int tagStart=temp.indexOf("\">",intFin);
						for(int j=0;j<count-1;j++){
							//System.out.println(temp.indexOf("\"tag\" >",tagStart)+" "+temp.indexOf("</a>",tagStart));
							tag=temp.substring(temp.indexOf("\">",tagStart),temp.indexOf("</a>",tagStart)).replaceAll("\">", "");
							tagStart=temp.indexOf("\">",temp.indexOf("</a>",tagStart));
							System.out.println(tag);
							APIs=APIs+"---"+tag;
							System.out.println("APIs "+APIs);
						}

					}
				}
				//}

				//if(line.contains("<dt>Tags</dt>")){
				if(builder.toString().contains("<dt>Tags</dt>")&&builder.toString().contains("<dl class=")){
					String temp=builder.substring(builder.indexOf("<dd",builder.indexOf("<dt>Tags</dt>")),builder.indexOf("</dl>",builder.indexOf("<dd",builder.indexOf("<dt>Tags</dt>"))));
					int count=org.apache.commons.lang3.StringUtils.countMatches(builder.substring(builder.indexOf("<dt>Tags</dt>"), builder.indexOf("</dl>",builder.indexOf("<dt>Tags</dt>"))),"</a>");

					int intStart=temp.indexOf("\"tag\">");
					int intFin=temp.indexOf("</a>");
					if(!(intStart==-1&&intFin==-1)){
						//System.out.println(fileList.get(i));
						System.out.println(intStart+" "+intFin);
						String tag=temp.substring(temp.indexOf("\"tag\">"),temp.indexOf("</a>",intFin)).replaceAll("\"tag\">", "");
						tags=tags+"---"+tag;
						System.out.println(tag);

						int tagStart=temp.indexOf("\"tag\">",intFin);
						for(int j=0;j<count-1;j++){							
							//System.out.println(temp.indexOf("\"tag\" >",tagStart)+" "+temp.indexOf("</a>",tagStart));
							tag=temp.substring(temp.indexOf("\"tag\">",tagStart),temp.indexOf("</a>",tagStart)).replaceAll("\"tag\">", "");
							tagStart=temp.indexOf("\"tag\">",temp.indexOf("</a>",tagStart));
							System.out.println(tag);
							tags=tags+"---"+tag;
							System.out.println("Tags "+tags);
						}

					}
				}
				//}
				//System.exit(1);
				String[] split=fileList.get(i).toString().split("\\\\");
				String fileName=split[split.length-1].replace(".html", "");
				String apiData=fileName+"==="+title+"==="+desc+"==="+APIs+"==="+tags;
				updateallMashupData(apiData);
			}
		}
	}

	//Update all the data related to Mashups such as description, tags, formats
	public static void updateallMashupData(String apiData) throws Exception {
		try {

			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost/programmableweb","root","mysql");

			//Get Max Key
			String sql="select max(Id) from mashup_alldata";
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement
					.executeQuery(sql);


			int max=0;
			while (resultSet.next()) {
				max=resultSet.getInt(1);
				//System.out.println(resultSet.getInt(1));
			}

			String[] split=apiData.split("===");

			/*System.out.println(split[6]);
				System.out.println("Split "+split[6].substring(3,split[6].length()));
				System.exit(1);*/

			//Insert API data

			System.out.println(apiData);
			System.out.println(split.length);
			sql="Insert into mashup_alldata values (?,?,?,?,?,?)";
			// Statements allow to issue SQL queries to the database
			preparedStatement=connect.prepareStatement(sql);
			preparedStatement.setInt(1, ++max);
			preparedStatement.setString(2, "http://www.programmableweb.com/"+split[0].replace("_", "/").replace("__", "|"));

			if(split.length>1){
				if(!(split[1].isEmpty()||split[1].equals(""))){
					preparedStatement.setString(3, split[1]);}
				else{
					preparedStatement.setString(3, "");
				}
			}
			else{
				preparedStatement.setString(3, "");
			}

			if(split.length>2){
				if(!(split[2].isEmpty()||split[2].equals(""))){
					preparedStatement.setString(4, split[2]);}
				else{
					preparedStatement.setString(4, "");
				}
			}
			else{
				preparedStatement.setString(4, "");
			}

			if(split.length>3){
				if(!(split[3].isEmpty()||split[3].equals(""))){
					preparedStatement.setString(5, split[3].substring(3,split[3].length()));}
				else{
					preparedStatement.setString(5, "");
				}
			}
			else{
				preparedStatement.setString(5, "");
			}

			if(split.length>4){
				if(!(split[4].isEmpty()||split[4].equals(""))){
					preparedStatement.setString(6, split[4].substring(3,split[4].length()));}
				else{
					preparedStatement.setString(6, "");
				}
			}
			else{
				preparedStatement.setString(6, "");
			}

			// Result set get the result of the SQL query
			preparedStatement
			.executeUpdate();

			// writeResultSet(resultSet);
			connect.close();



		} catch (Exception e) {
			throw e;
		}
	}

	//Fetch data from DB & write to file: desc, summary, cate, tags
	public static void createMashupFiles() throws Exception{
		try {

			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost/programmableweb","root","mysql");

			//Get Max Key
			String sql="select * from mashup_alldata";
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement
					.executeQuery(sql);

			while (resultSet.next()) {
				String url=resultSet.getString("url");
				String description=resultSet.getString("description");
				String title=resultSet.getString("title");
				String APIs=resultSet.getString("APIs");
				String tags=resultSet.getString("tags");

				String[] split=url.split("/");
				System.out.println(split[split.length-1]);

				BufferedWriter bw = new BufferedWriter(new FileWriter(new File("E:/Research Projects/ICSM 2014 project/Data/MashupFilteredData/mashup_"+split[split.length-1]+".txt")));
				bw.write("Title:\n");
				bw.write(title+"\n\n");
				bw.write("Description:\n");
				bw.write(description+"\n\n");
				bw.write("APIs:\n");
				bw.write(APIs+"\n\n");
				bw.write("Tags:\n");
				bw.write(tags);
				bw.close();
			}
		}catch (Exception e) {
			throw e;
		}
	}
}
