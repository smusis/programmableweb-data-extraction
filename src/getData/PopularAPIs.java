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
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;


public class PopularAPIs {
	private static Connection connect = null;
	private static Statement statement = null;
	private static PreparedStatement preparedStatement = null;
	private static ResultSet resultSet = null;
	public static TreeMap<String,Integer> APIList= new TreeMap<String, Integer>();
	public static void main(String[] args) throws Exception {
		getAPIs();
		getPopAPIs();

		//shuffleData();
	}

	//Get all the APIs
	public static void getAPIs() throws ClassNotFoundException, SQLException{
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
			String url=resultSet.getString("url");
			String name=resultSet.getString("name");
			System.out.println(name);
			APIList.put(name.trim(), 0);
		}

	}

	//Get Popular APIs i.e., used by more mashups
	public static void getPopAPIs() throws SQLException, ClassNotFoundException, IOException {
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

			for(int i=0;i<split.length;i++){
				if(APIList.containsKey(split[i])){
					APIList.put(split[i], APIList.get(split[i])+1);
				}
			}
		}

		ValueComparator bvc =  new ValueComparator(APIList);
		TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);
		sorted_map.putAll(APIList);
		System.out.println("Popular APIs");
		System.out.println("Popular APIs "+sorted_map);

		
		getScore100APIs(APIList);
	}

	//Get Popularity scores for 100 APIs
	public static void getScore100APIs(TreeMap<String,Integer> APIList) throws IOException, ClassNotFoundException, SQLException{
		ArrayList<String> APIs=new ArrayList<String>();
		final BufferedReader br = new BufferedReader(new FileReader("E:/Research Projects/ICSM 2014 project/Data/100APIs.txt")); 
		String line;
		while ((line = br.readLine()) != null) {
			APIs.add(line.trim());
		}


		//Get links between API url & name
		TreeMap<String, String> APILinks=new TreeMap<String, String>();
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
			String url=resultSet.getString("url");
			String apiS=resultSet.getString("name");
			APILinks.put(url.trim(), apiS.trim());
		}


		
		for(String str:APIs){
			String apiName=APILinks.get(str);
			//System.out.println(apiName);
			if(APIList.containsKey(apiName)){
				System.out.println(APIList.get(apiName));
			}
		}
		//System.out.println(sorted_map);
	}


	//Get Popular APIs i.e., used by more mashups
	public static void shuffleData() throws SQLException, ClassNotFoundException, FileNotFoundException {
		ArrayList<String> URLs=new ArrayList<String>();

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
			URLs.add(url);
		}



		Random rnd=new Random(98308742);
		Collections.shuffle(URLs, rnd);
		System.out.println("Shuffled APIs");

		PrintWriter writer=new PrintWriter("E:/Research Projects/ICSM 2014 project/Data/ShuffledAPIs");
		for(String str:URLs){
			writer.write(str+"\n");
		}
		writer.close();
	}
}

class ValueComparator implements Comparator<String> {

	Map<String, Integer> base;
	public ValueComparator(TreeMap<String, Integer> fmap) {
		this.base = fmap;
	}

	// Note: this comparator imposes orderings that are inconsistent with equals.    
	public int compare(String a, String b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}