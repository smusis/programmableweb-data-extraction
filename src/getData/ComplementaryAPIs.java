package getData;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class ComplementaryAPIs {
	private static Connection connect = null;
	private static Statement statement = null;
	private static PreparedStatement preparedStatement = null;
	private static ResultSet resultSet = null;
	public static ArrayList<String> fileNames1= new ArrayList<String>();
	public static ArrayList<String> fileNames2= new ArrayList<String>();
	public static Integer test[][]=new Integer[1][1];
	//public static Integer test[][]=new Integer[11362][11362];
	public static void main(String[] args) throws Exception {

		//Run to get complementary APIs (which are used together)
		/*getAPIs();
		getMashupLinks();*/

		//countLinks(); //For getting the count of the links

		//countComplementaryLinks(); //For getting the distinct links

		categoriseAPIs();

		//getComplementaryAPIsFaster();
	}

	//Get all the APIs
	public static void getAPIs() throws ClassNotFoundException, SQLException, IOException{

		//Get all the APIs (Total 11362)
		TreeMap<String,String> allAPIs=new TreeMap<String, String>();
		final BufferedReader br = new BufferedReader(new FileReader("E:/Research Projects/ICSM 2014 project/Data/LinksURL-Name.txt")); 
		String line; 
		while ((line = br.readLine()) != null) { 
			String[] split=line.split(",");
			allAPIs.put(split[0].trim(),split[1].trim());
		}


		//Get filtered APIs (Total 9883)
		ArrayList<String> APIList =new ArrayList<String>();
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
			String name=resultSet.getString("url").trim();
			APIList.add(name.trim());
		}

		for(String str:APIList)
		{
			fileNames1.add(allAPIs.get(str));
		}
		fileNames2=fileNames1;
		System.out.println(APIList.size());
		System.out.println(fileNames2.size());
		System.exit(1);

		for(int i=0;i<test.length;i++)
		{
			for(int j=0;j<test[i].length;j++)
			{
				test[i][j]=0;
			}
		}

	}

	//Get links between APIs & store in matrix 
	public static void getMashupLinks() throws SQLException, ClassNotFoundException, IOException {
		ArrayList<String> existingMashup=new ArrayList<String>();
		final BufferedReader br = new BufferedReader(new FileReader("E:/Research Projects/ICSM 2014 project/Data/filtered_mashups.txt")); 
		String line; 
		while ((line = br.readLine()) != null) { 
			existingMashup.add(line.trim());
		}

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
			String apiS=resultSet.getString("APIs");
			System.out.println(apiS);
			String[] split=apiS.split("---");
			System.out.println(split.length);

			/*for(int i=0;i<split.length;i++){
				System.out.println("Splits "+split[i].trim());
			}*/
			System.out.println(url);

			if(existingMashup.contains(url)){
				System.out.println("Contains");
				for(int i=0;i<split.length;i++){
					//System.out.println("Split "+split[i].trim());
					for(int j=0;j<split.length;j++){
						if(!(split[i].trim().equals(split[j].trim()))){
							System.out.println(split[i].trim()+"   "+split[j].trim());
							if(fileNames1.contains(split[i].trim())&&fileNames1.contains(split[j].trim())){
								test[fileNames1.indexOf(split[i].trim())][fileNames2.indexOf(split[j].trim())]=
										test[fileNames1.indexOf(split[i].trim())][fileNames2.indexOf(split[j].trim())]+1;
							}
						}
					}
				}
			}
		}
	}

	//Count links between APIs & store in file
	public static void countLinks() throws FileNotFoundException{

		ArrayList<String>links=new ArrayList<String>();
		System.out.println("Count Links");

		PrintWriter writer=new PrintWriter("E:/Research Projects/ICSM 2014 project/Data/LinksbtAPIs");
		for(int i=0;i<fileNames1.size();i++){
			for(int j=0;j<fileNames1.size();j++){
				if(!(fileNames1.get(i).equals(fileNames1.get(j)))){
					String name=fileNames1.get(i)+"---"+fileNames1.get(j);
					String altName=fileNames1.get(j)+"---"+fileNames1.get(i);
					if(!(links.contains(name)||links.contains(altName))){
						System.out.println(name);
						if(test[fileNames1.indexOf(fileNames1.get(i))][fileNames1.indexOf(fileNames1.get(j))]>0){
							links.add(name);
							writer.write(name+","+ test[fileNames1.indexOf(fileNames1.get(i))][fileNames1.indexOf(fileNames1.get(j))]+"\n");
						}
					}
				}
			}
		}

		writer.close();
	}

	//Count distinct links between APIs (If 1 API has 5 links with another API, count it as 1) 
	public static void countComplementaryLinks() throws FileNotFoundException{

		ArrayList<String> links=new ArrayList<String>();
		System.out.println("Count Links");


		PrintWriter writer=new PrintWriter("E:/Research Projects/ICSM 2014 project/Data/DistinctLinksbtAPIs");
		for(int i=0;i<fileNames1.size();i++){
			int count=0;
			for(int j=0;j<fileNames1.size();j++){
				if(!(fileNames1.get(i).equals(fileNames1.get(j)))){
					if(test[fileNames1.indexOf(fileNames1.get(i))][fileNames1.indexOf(fileNames1.get(j))]>0){
						count++;
					}
				}
			}
			links.add(fileNames1.get(i)+"---"+count);
		}

		for (String str: links){
			writer.write(str+"\n");
		}
		writer.close();
	}


	//Categorise complementary APIs
	public static void categoriseAPIs() throws IOException{
		TreeMap<String, Integer> links=new TreeMap<String, Integer>();
		final BufferedReader br = new BufferedReader(new FileReader("E:/Research Projects/ICSM 2014 project/Data/DistinctLinksbtAPIs")); 
		String line; 
		int count0=0,count1=0,count1to50=0,count50to100=0,countAbv100=0;
		while ((line = br.readLine()) != null) { 
			String[] split=line.split("---");

			if(Integer.parseInt(split[1])==0){
				count0++;
			}
			if(Integer.parseInt(split[1])==1){
				count1++;
			}
			if(Integer.parseInt(split[1])>1&&Integer.parseInt(split[1])<50){
				count1to50++;
			}
			if(Integer.parseInt(split[1])>=50&&Integer.parseInt(split[1])<100){
				count50to100++;
			}
			if(Integer.parseInt(split[1])>=100){
				countAbv100++;
				System.out.println(split[0]);
			}

		}

		Map.Entry<String, Integer> maxEntry = null;

		/*for (Map.Entry<String, Integer> entry : links.entrySet())
		{
			if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
			{
				maxEntry = entry;
			}
		}*/


		System.out.println(count0+" "+count1+" "+count1to50+" "+count50to100+" "+countAbv100);
	}

	public static void getComplementaryAPIsFaster() throws IOException, ClassNotFoundException, SQLException{
		//Get all the APIs (Total 11362)
		TreeMap<String,String> allAPIs=new TreeMap<String, String>();
		BufferedReader br = new BufferedReader(new FileReader("E:/Research Projects/ICSM 2014 project/Data/LinksURL-Name.txt")); 
		String line; 
		while ((line = br.readLine()) != null) { 
			String[] split=line.split(",");
			allAPIs.put(split[0].trim(),split[1].trim());
		}
		br.close();

		TreeMap<String, String> allAPIList=new TreeMap<String, String>();
		//Get filtered APIs (Total 9883)
		ArrayList<String> APIList =new ArrayList<String>();
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
			String name=resultSet.getString("url").trim();
			APIList.add(name.trim());
		}

		System.out.println(allAPIs.size());
		System.out.println(APIList.size());
		
		for(String str:APIList)
		{
			//fileNames1.add(allAPIs.get(str));
			allAPIList.put(allAPIs.get(str), "");
		}
		System.out.println(allAPIList.size());
		System.exit(1);

		//Get Mashup Data
		ArrayList<String> existingMashup=new ArrayList<String>();
		br = new BufferedReader(new FileReader("E:/Research Projects/ICSM 2014 project/Data/filtered_mashups.txt")); 

		while ((line = br.readLine()) != null) { 
			existingMashup.add(line.trim());
		}

		// This will load the MySQL driver, each DB has its own driver
		Class.forName("com.mysql.jdbc.Driver");
		// Setup the connection with the DB
		connect = DriverManager
				.getConnection("jdbc:mysql://localhost/programmableweb","root","mysql");

		//Get Max Key
		sql="select * from mashup_alldata";
		statement = connect.createStatement();
		// Result set get the result of the SQL query
		resultSet = statement
				.executeQuery(sql);

		while (resultSet.next()) {
			String url=resultSet.getString("url");
			String apiS=resultSet.getString("APIs");
			System.out.println(apiS);
			String[] split=apiS.split("---");
			System.out.println(split.length);

			/*for(int i=0;i<split.length;i++){
				System.out.println("Splits "+split[i].trim());
			}*/
			System.out.println(url);

			if(existingMashup.contains(url)){
				for(int i=0;i<split.length;i++){
					for(int j=0;j<split.length;j++){
						if(!(split[i].trim().equals(split[j].trim()))){
							String present=allAPIList.get(split[i]);

							allAPIList.put(split[i],present+";"+split[j]);
						}
					}
				}
			}
		}
		System.out.println(allAPIList.size());
		System.exit(1);
		
		for(Map.Entry<String,String> entry : allAPIList.entrySet()) {
			ArrayList<String> temp=new ArrayList<String>();
			String[] spl=entry.getValue().split(";");
			for(int z=0;z<spl.length;z++){
				if(!spl[z].equals("")&&!temp.contains(spl[z])){
					temp.add(spl[z]);
				}
			}
			System.out.println(entry.getKey()+"---"+temp.size());
		}
	}
}
