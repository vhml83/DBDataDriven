package com.titanium.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class RegisterUsers {
	WebDriver webdriver;
	String baseUrl = "http://newtours.demoaut.com/";
	String xpathLoc = ".//*[contains(text(),'Note: Your user name is')]";
	String jdbcDriver = "com.mysql.jdbc.Driver";
	String dataBaseURL = "jdbc:mysql://localhost:3306/demo";
	String userDB = "root";
	String passwordDB = "";
	Connection conn = null;
	Statement stmt;
	ResultSet resultSet;
	
	public WebDriver getDriver(){
		if(webdriver == null){
			System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"\\drivers\\chromedriver.exe");	
			webdriver = new ChromeDriver();
		}
		return webdriver;
	}

	public void navigateTo(){
		getDriver().get(baseUrl);
		getDriver().manage().window().maximize();
		getDriver().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}

	@DataProvider(name = "MySQL-provider")
	public String [][] mySQL_Data() 
	{
		int rowCount = 0;
		int columnCount = 0;
		String myData[][] = null;
		
		try{
			Class.forName(jdbcDriver);
			conn = DriverManager.getConnection(dataBaseURL, userDB, passwordDB);
			stmt = conn.createStatement();
			resultSet = stmt.executeQuery("SELECT * FROM demo.userinfo;");
			ResultSetMetaData rsMeta = resultSet.getMetaData();
			columnCount = rsMeta.getColumnCount();
			
			while(resultSet.next()){
				rowCount++;
			}
			
			myData = new String[rowCount][columnCount];
			resultSet.beforeFirst();
			
			for(int row = 0; row < rowCount; row++) {
				resultSet.next();
				for(int col = 1; col <= columnCount; col++) {
					myData[row][col-1] = resultSet.getString(col);
				}
			}
			
			stmt.close();
			conn.close();
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		return myData;
	}
	
	@BeforeTest
	public void setUp() throws Exception {
		navigateTo();
	}

	@AfterTest
	public void tearDown() throws Exception {
		getDriver().quit();
	}

	@BeforeMethod
	public void clickRegister(){
		getDriver().findElement(By.linkText("REGISTER")).click();
	}

	@AfterMethod
	public void verifyUserRegistered(){
		System.out.println(webdriver.findElement(By.xpath(xpathLoc)).getText());
	}

	@Test(dataProvider = "MySQL-provider")
	public void registerUser(String ... userinfo){

		//Adding Contact Information
		getDriver().findElement(By.name("firstName")).sendKeys(userinfo[1]);
		getDriver().findElement(By.name("lastName")).sendKeys(userinfo[2]);
		getDriver().findElement(By.name("phone")).sendKeys(userinfo[3]);
		getDriver().findElement(By.id("userName")).sendKeys(userinfo[4]);

		//Adding Mailing Information
		getDriver().findElement(By.name("address1")).sendKeys(userinfo[5]);
		getDriver().findElement(By.name("city")).sendKeys(userinfo[6]);
		getDriver().findElement(By.name("state")).sendKeys(userinfo[7]);
		getDriver().findElement(By.name("postalCode")).sendKeys(userinfo[8]);
		getDriver().findElement(By.name("country")).sendKeys(userinfo[9]);

		//Adding User Information
		getDriver().findElement(By.id("email")).sendKeys(userinfo[10]);
		getDriver().findElement(By.name("password")).sendKeys(userinfo[11]);
		getDriver().findElement(By.name("confirmPassword")).sendKeys(userinfo[11]);
		getDriver().findElement(By.name("confirmPassword")).submit();
	}
}