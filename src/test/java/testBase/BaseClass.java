package testBase;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;  //Log4j
import org.apache.logging.log4j.Logger;   //Log4j
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

public class BaseClass {
	
public WebDriver driver;   //made driver unstatic since parallel tests not executing properly
public Logger logger;   //Log4j
public Properties p;
	
	@SuppressWarnings("deprecation")
	@BeforeClass(groups={"Sanity","Regression","Master"})
	@Parameters({"os", "browser"})
	public void setup(String os, String br) throws IOException
	{
		//Loading config.properties file
		FileReader file = new FileReader("./src//test//resources//config.properties");
		p=new Properties();
		p.load(file);
		
		logger = LogManager.getLogger(this.getClass());
		
		//For Remote execution
		if(p.getProperty("execution_env").equalsIgnoreCase("remote"))
		{
			DesiredCapabilities cap = new DesiredCapabilities();
			
			//OS
			if(os.equalsIgnoreCase("windows"))
			{
				cap.setPlatform(Platform.WIN10);
			}
			else if(os.equalsIgnoreCase("mac"))
			{
				cap.setPlatform(Platform.MAC);
			}
			else
			{
				System.out.println("OS not matching");
				return;
			}
			
			//Browser
			switch(br.toLowerCase())
			{
				case "chrome": cap.setBrowserName("chrome");  break;
				case "firefox": cap.setBrowserName("firefox");  break;
				default: System.out.println("Browser not matching");  return;			
			}
			
			driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"),cap);
		}
		
		//For local execution
		if(p.getProperty("execution_env").equalsIgnoreCase("local"))
		{
			switch(br.toLowerCase())
			{
			case "chrome": driver = new ChromeDriver(); break;
			case "firefox": driver = new FirefoxDriver(); break;
			case "edge": driver = new EdgeDriver(); break;
			default: System.out.println("Invalid browser name..."); return;
			}
		}
						
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.manage().window().maximize();
		
		driver.get(p.getProperty("appURL"));    //Reading url from properties file
	}
	
	@AfterClass(groups={"Sanity","Regression","Master"})
	public void tearDown()
	{
		driver.quit();
	}
	
	
	public String randomString()
	{
		@SuppressWarnings("deprecation")
		String generatedString = RandomStringUtils.randomAlphabetic(7);
		return generatedString;
	}
	
	public String randomNumber()
	{
		@SuppressWarnings("deprecation")
		String generatedNumber = RandomStringUtils.randomNumeric(10);
		return generatedNumber;
	}
	
	public String randomAlphaNumeric()
	{
		@SuppressWarnings("deprecation")
		String generatedString = RandomStringUtils.randomAlphabetic(3);
		@SuppressWarnings("deprecation")
		String generatedNumber = RandomStringUtils.randomNumeric(3);
		return (generatedString+"$"+generatedNumber);
	}
	
	public String captureScreen(String tname) throws IOException
	{
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());   //creating timestamp
		
		TakesScreenshot takesScreenshot = (TakesScreenshot) driver;   //initiating driver to takesScreenshot
		File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);   //storing captured screenshot in sourcefile
		
		String targetFilePath = System.getProperty("user.dir")+"\\screenshots\\"+ tname + "_" + timeStamp + ".png";  //creating target file path to store screenshot
		File targetFile = new File(targetFilePath);  //creating target file at target path 
		
		sourceFile.renameTo(targetFile);  //copying source file to target file
		
		return targetFilePath;
			
	}

}
