package Trading_Engine;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class myDatabaseTest {
	private static myDatabase myTestDB;

	@BeforeClass
	public static void setup(){
		myTestDB = new myDatabase();
		myTestDB.deleteAllTables();
	}

	@Test
	public void testInsertSmallData() {
		URL url = getClass().getResource("smalldata.csv");
		File file = new File(url.getPath());
		myTestDB.insertAll(file);
		try {
			ResultSet result = myTestDB.getResultSet("SELECT count(*) FROM all_list;");
			Assert.assertEquals("Error in select count(*)!", 2059, result.getInt(1));
			result.close();
		} catch (SQLException e) {
			System.out.println("Error in testEmptyTable : " + e);
		}
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));
			ResultSet result1 = myTestDB.getResultSet("SELECT * FROM all_list;");
			br.readLine();
			String st,stmp = "";
			float tmp1,tmp2 = 0;
			while ((st=br.readLine())!=null && result1.next()){
				String[] insertElement = st.split(",");
				stmp = result1.getString(5);
				Assert.assertEquals("Error in br.type == result1.type!", stmp, insertElement[3]);
				if(insertElement[3].equalsIgnoreCase("ENTER")){
					tmp1 = Float.parseFloat(insertElement[4]);
					tmp2 = result1.getFloat(6);
					//System.out.println("x" + tmp1 + " == " + tmp2 + "x");
					Assert.assertEquals("Error in st == result1!", tmp1, tmp2, 0.001);
				}
			}
			result1.close();
			br.close();
		}catch (Exception e){
			System.out.println("Error in br.readline() : " + e);
		}

	}


	@AfterClass
	public static void close(){
		myTestDB.closeDatabase();
	}
}