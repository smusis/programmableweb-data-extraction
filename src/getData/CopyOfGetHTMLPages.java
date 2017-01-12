package getData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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
import org.apache.commons.lang3.*;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mysql.jdbc.StringUtils;

public class CopyOfGetHTMLPages {
	private static Connection connect = null;
	private static Statement statement = null;
	private static PreparedStatement preparedStatement = null;
	private static ResultSet resultSet = null;

	public static void main(String[] args) throws Exception {
		//Get List of all API
		//getProjectList();

		//Get API data
		//getAPIData();

		//Get all API Data: Desc, categories, tags etc..
		//getAPISummaryNAll();

		createAPIFiles();
	}

	//Get the list of APIs for programmableweb.com
	public static void getProjectList() throws Exception{
		final File folder = new File("E:/Research Projects/ICSM 2014 project/Data/APIProjectList");
		final List<File> fileList = Arrays.asList(folder.listFiles());

		ArrayList<String> apiData=new ArrayList<String>();
		for(int i=0;i<fileList.size();i++)
		{
			String line="";
			boolean present=false;
			final BufferedReader br=new BufferedReader(new FileReader(fileList.get(i)));
			while((line=br.readLine())!=null)
			{
				if(line.contains("<table class=\"listTable mB15\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" summary=\"API\" id=\"apis\">"))
				{
					present=true;
				}
				//System.out.println(present);
				if(present){
					if(line.contains("</table>")){
						break;
					}
					if(line.contains("<td>")&&line.contains("</td>")){
						int start=line.indexOf("<td>");
						int end=line.indexOf("</td>",start);

						String link=line.substring(start, end);
						//System.out.println(link);
						String url=link.substring(link.indexOf("href="), link.indexOf("\">",link.indexOf("href="))).replaceAll("href=\"", "");
						String name=link.substring(link.indexOf("\">"), link.indexOf("</a>",link.indexOf("\">"))).replace("\">", "").replace("&#039;", "'");
						System.out.println(url);
						System.out.println(name);

						String desc=line.substring(line.indexOf("<td>",end),line.indexOf("</td>",line.indexOf("<td>",end))).replace("<td>", "").replace("&#039;", "'");

						int thirdTD=line.indexOf("</td>",line.indexOf("<td>",end));
						String cate=line.substring(line.indexOf("<td>",thirdTD),line.indexOf("</td>",line.indexOf("<td>",thirdTD))).replace("<td>", "");

						int fourthTD=line.indexOf("</td>",line.indexOf("<td>",thirdTD));
						String updated=line.substring(line.indexOf("<td>",fourthTD),line.indexOf("</td>",line.indexOf("<td>",fourthTD))).replace("<td>", "");
						System.out.println(desc);
						System.out.println(cate);
						System.out.println(updated);
						//System.exit(1);
						apiData.add(url+"=="+name+"=="+desc+"=="+cate+"=="+updated);
					}
				}
			}
		}

		updateProjectList(apiData);
	}

	//Save API list data in sql
	public static void updateProjectList(ArrayList<String> apiData) throws Exception {
		try {
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost/programmableweb","root","mysql");

			//Get Max Key
			String sql="select max(Id) from api_all";
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement
					.executeQuery(sql);


			int max=0;
			while (resultSet.next()) {
				max=resultSet.getInt(1);
			}


			for(String str:apiData)
			{
				String[] split=str.split("==");

				//Insert API data

				sql="Insert into api_all values (?,?,?,?,?,?)";
				// Statements allow to issue SQL queries to the database
				preparedStatement=connect.prepareStatement(sql);
				preparedStatement.setInt(1, max++);
				preparedStatement.setString(2, "http://www.programmableweb.com"+split[0]);
				preparedStatement.setString(3, split[1]);
				preparedStatement.setString(4, split[2]);
				preparedStatement.setString(5, split[3]);
				preparedStatement.setString(6, split[4]);

				// Result set get the result of the SQL query
				preparedStatement
				.executeUpdate();
			}
			// writeResultSet(resultSet);
			connect.close();



		} catch (Exception e) {
			throw e;
		}
	}

	//Read API name & Get Data for each API
	public static void getAPIData() throws Exception {
		try {

			final File folder = new File("E:/Research Projects/ICSM 2014 project/Data/APIData");
			final List<File> fileList = Arrays.asList(folder.listFiles());

			ArrayList<String> apiPresent=new ArrayList<String>();
			ArrayList<String> apiData=new ArrayList<String>();
			for(int i=0;i<fileList.size();i++)
			{
				String[] splitName=fileList.get(i).toString().split("\\\\");
				apiPresent.add(splitName[splitName.length-1].replace(".html", ""));
				//System.out.println(splitName[splitName.length-1].replace(".html", ""));
			}


			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost/programmableweb","root","mysql");

			//Get Max Key
			String sql="select * from api_all";
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement
					.executeQuery(sql);

			while (resultSet.next()) {

				URL url=new URL(resultSet.getString(2));
				String[] split=url.toString().split("/");
				System.out.println(url);
				if(url.toString().equals("http://www.programmableweb.com/api/box.net-embedit")
						||url.toString().equals("http://www.programmableweb.com/api/e*trade")||
						url.toString().equals("http://www.programmableweb.com/api/hotfile")){
					//System.out.println("true");
					continue;
				}

				//System.out.println("after");

				if(!apiPresent.contains(split[3]+"_"+split[4])){
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

					System.out.println(split[3]+"_"+split[4]);

					BufferedWriter bw = new BufferedWriter(new FileWriter(new File("E:/Research Projects/ICSM 2014 project/Data/APIData/"+split[3]+"_"+split[4]+".html")));

					String inputLine;
					while ((inputLine = in.readLine()) != null)
					{
						bw.write(inputLine+"\n");
					}

					in.close();
					bw.close();
				}
			}

		}
		catch (Exception e) {

			throw e;
		}
	}

	//Get Summary & other data from saved HTML pages
	public static void getAPISummaryNAll() throws Exception{
		final File folder = new File("E:/Research Projects/ICSM 2014 project/Data/APIData");
		final List<File> fileList = Arrays.asList(folder.listFiles());

		
		//APIs retained for historical purpose must be removed
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
			if(!removeFiles.contains(fileList.get(i).toString())){

				System.out.println(fileList.get(i));
				String line="";
				boolean present=false;
				final BufferedReader br=new BufferedReader(new FileReader(fileList.get(i)));

				String desc="",summary="",cate="",tags="",protocols="",dataF="",apiHome="",allData="",title="";
				while((line=br.readLine())!=null)
				{

					if(line.contains("<title>")){
						title=line.substring(line.indexOf("<title>"), line.indexOf("</title>")).replace("<title>", "").replace("&#039;", "'");
					}

					if(line.contains("<div class=\"span-10\"><p>"))
					{
						present=true;
					}
					//System.out.println(present);
					if(present){
						if(line.contains("<p>")){
							int start=line.indexOf("<p>");
							int end=line.indexOf("</span>",start);
							System.out.println(start+"  "+end);
							desc=line.substring(start, end).replaceAll("<span style=\"display:none;\">", "").replaceAll("<p>", "").replace("&#039;", "'")
									.replace("&#64257;", "");

							System.out.println(desc.replaceAll("<span style=\"display:none;\">", ""));
							present=false;
						}
					}
					else if(line.contains("<span style=\"display:none;\"></span>")){
						int start=0;
						if(line.contains("</a>")){start=line.indexOf("</a>");}
						//if(line.contains(";\">")){start=line.indexOf(";\">",line.indexOf("</a>"));}

						int end=line.indexOf("</span>",start);
						System.out.println(start+"  "+end);
						desc=line.substring(start, end).replaceAll("<span style=\"display:none;\">", "").replaceAll("</a>", "").replace("&#039;", "'")
								.replace("&#64257;", "");


					}


					if(line.contains("<dl class=\"")){
						if(line.contains("<dt>Summary</dt>")){summary=line.substring(line.indexOf("<dd"),line.indexOf("</dd>")).replaceAll("<dd>", "").replace("<dd class=\"w220\">", "")
								.replace("&#039;", "'");}
						if(line.contains("<dt>Category</dt>")){
							String temp=line.substring(line.indexOf("<dd"),line.indexOf("</dd>"));
							System.out.println(temp);
							if(temp.contains("<a href=\"")){
								cate=temp.substring(temp.indexOf("\">",temp.indexOf("<a href=")), temp.indexOf("</a>",temp.indexOf("\">"))).replace("\">", "");
							}else{
								cate=temp.substring(temp.indexOf("\">"), temp.indexOf("</a>",temp.indexOf("\">"))).replace("\">", "");
							}
						}
						if(line.contains("<dt>Tags</dt>")){
							String temp=line.substring(line.indexOf("<dd"),line.indexOf("</dd>"));
							int count=org.apache.commons.lang3.StringUtils.countMatches(temp,"</a>");
							int intStart=temp.indexOf("\"tag\" >");
							int intFin=temp.indexOf("</a>");
							if(!(intStart==-1&&intFin==-1)){
								//System.out.println(fileList.get(i));
								System.out.println(intStart+" "+intFin);
								String tag=temp.substring(temp.indexOf("\"tag\" >"),temp.indexOf("</a>",intFin)).replaceAll("\"tag\" >", "");
								tags=tags+"|||"+tag;
								System.out.println(tag);

								int tagStart=temp.indexOf("\"tag\" >",intFin);
								for(int j=0;j<count-1;j++){							
									//System.out.println(temp.indexOf("\"tag\" >",tagStart)+" "+temp.indexOf("</a>",tagStart));
									tag=temp.substring(temp.indexOf("\"tag\" >",tagStart),temp.indexOf("</a>",tagStart)).replaceAll("\"tag\" >", "");
									tagStart=temp.indexOf("\"tag\" >",temp.indexOf("</a>",tagStart));
									System.out.println(tag);
									tags=tags+"|||"+tag;
									System.out.println("Tags "+tags);
								}

							}
						}

						if(line.contains("<dt>Protocols</dt>")){
							String temp=line.substring(line.indexOf("<dd"),line.indexOf("</dd>"));
							int count=org.apache.commons.lang3.StringUtils.countMatches(temp,"</a>");
							int intStart=temp.indexOf("\"nofollow\">");
							int intFin=temp.indexOf("</a>");
							//System.out.println(intStart+" "+intFin);
							String tag=temp.substring(temp.indexOf("\"nofollow\">"),temp.indexOf("</a>",intFin)).replaceAll("\"nofollow\">", "");
							protocols=protocols+"|||"+tag;
							System.out.println(tag);

							int tagStart=temp.indexOf("\"nofollow\">",intFin);
							for(int j=0;j<count-1;j++){							
								//System.out.println(temp.indexOf("\"tag\" >",tagStart)+" "+temp.indexOf("</a>",tagStart));
								tag=temp.substring(temp.indexOf("\"nofollow\">",tagStart),temp.indexOf("</a>",tagStart)).replaceAll("\"nofollow\">", "");
								tagStart=temp.indexOf("\"nofollow\">",temp.indexOf("</a>",tagStart));
								protocols=protocols+"|||"+tag;
								System.out.println(protocols);
							}


						}


						if(line.contains("<dt>Data Formats</dt>")){
							String temp=line.substring(line.indexOf("<dd"),line.indexOf("</dd>"));
							int count=org.apache.commons.lang3.StringUtils.countMatches(temp,"</a>");
							int intStart=temp.indexOf("\"nofollow\">");
							int intFin=temp.indexOf("</a>");
							//System.out.println(intStart+" "+intFin);
							String tag=temp.substring(temp.indexOf("\"nofollow\">"),temp.indexOf("</a>",intFin)).replaceAll("\"nofollow\">", "");
							dataF=dataF+"|||"+tag;
							System.out.println(tag);

							int tagStart=temp.indexOf("\"nofollow\">",intFin);
							for(int j=0;j<count-1;j++){							
								//System.out.println(temp.indexOf("\"tag\" >",tagStart)+" "+temp.indexOf("</a>",tagStart));
								tag=temp.substring(temp.indexOf("\"nofollow\">",tagStart),temp.indexOf("</a>",tagStart)).replaceAll("\"nofollow\">", "");
								tagStart=temp.indexOf("\"nofollow\">",temp.indexOf("</a>",tagStart));
								dataF=dataF+"|||"+tag;
								System.out.println(dataF);
							}

						}

						if(line.contains("<dt>API home</dt>")){
							apiHome=line.substring(line.indexOf("blank\">"), line.indexOf("</a>")).replaceAll("blank\">", "");
							System.out.println(apiHome);
						}
					}

				}
				String[] split=fileList.get(i).toString().split("\\\\");
				String fileName=split[split.length-1].replace(".html", "");
				allData=fileName+"==="+desc+"==="+summary+"==="+cate+"==="+tags+"==="+protocols+"==="+dataF+"==="+apiHome+"==="+title;


				boolean runlater=false;
				String[] escape={""};
				for(int z=0;z<escape.length;z++){
					if(fileName.equals(escape[z])){
						runlater=true;
						break;
					}
				}

				if(!runlater){
					updateallAPIData(allData);
				}
			}
		}
	}

	//Update all the data related to API such as description, tags, formats
	public static void updateallAPIData(String apiData) throws Exception {
		try {

			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost/programmableweb","root","mysql");

			//Get Max Key
			String sql="select max(Id) from api_alldata";
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
			sql="Insert into api_alldata values (?,?,?,?,?,?,?,?,?,?)";
			// Statements allow to issue SQL queries to the database
			preparedStatement=connect.prepareStatement(sql);
			preparedStatement.setInt(1, ++max);
			preparedStatement.setString(2, "http://www.programmableweb.com/"+split[0].replace("_", "/"));
			if(!(split[1].isEmpty()||split[1].equals(""))){
				preparedStatement.setString(3, split[1]);}
			else{
				preparedStatement.setString(3, "");
			}
			if(!(split[2].isEmpty()||split[2].equals(""))){
				preparedStatement.setString(4, split[2]);}
			else{
				preparedStatement.setString(4, "");
			}
			if(!(split[3].isEmpty()||split[3].equals(""))){
				preparedStatement.setString(5, split[3]);}
			else{
				preparedStatement.setString(5, "");
			}
			if(!(split[4].isEmpty()||split[4].equals(""))){
				preparedStatement.setString(6, split[4].substring(3,split[4].length()));}
			else{
				preparedStatement.setString(6, "");
			}
			if(!(split[5].isEmpty()||split[5].equals(""))){
				preparedStatement.setString(7, split[5].substring(3,split[5].length()));}
			else{
				preparedStatement.setString(7, "");
			}
			if(!(split[6].isEmpty()||split[6].equals(""))){
				preparedStatement.setString(8, split[6].substring(3,split[6].length()));}
			else{
				preparedStatement.setString(8, "");
			}

			if(!(split[7].isEmpty()||split[7].equals(""))){
				preparedStatement.setString(9, split[7]);}
			else{
				preparedStatement.setString(9, "");
			}

			if(!(split[8].isEmpty()||split[8].equals(""))){
				preparedStatement.setString(10, split[8]);}
			else{
				preparedStatement.setString(10, "");
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
	public static void createAPIFiles() throws Exception{
		try {

			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost/programmableweb","root","mysql");

			//Get Max Key
			String sql="select * from api_alldata";
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement
					.executeQuery(sql);

			while (resultSet.next()) {
				String url=resultSet.getString("url");
				String description=resultSet.getString("description");
				String summary=resultSet.getString("summary");
				String categories=resultSet.getString("categories");
				String tags=resultSet.getString("tags");
				String title=resultSet.getString("title");

				String[] split=url.split("/");
				System.out.println(split[split.length-1]);

				BufferedWriter bw = new BufferedWriter(new FileWriter(new File("E:/Research Projects/ICSM 2014 project/Data/APIFilteredData/api_"+split[split.length-1]+".txt")));
				bw.write("Title:\n");
				bw.write(title+"\n\n");
				bw.write("Summary:\n");
				bw.write(summary+"\n\n");
				bw.write("Description:\n");
				bw.write(description+"\n\n");
				bw.write("Categories:\n");
				bw.write(categories+"\n\n");
				bw.write("Tags:\n");
				bw.write(tags);
				bw.close();
			}
		}catch (Exception e) {
			throw e;
		}
	}
}
