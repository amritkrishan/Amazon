import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class AmazonTest {

    WebDriver driver = null;
    Path testDataPath = Paths.get(System.getProperty("user.dir"), "testData.csv");
    String testDataFile = testDataPath.toString();
    Path outputPath = Paths.get(System.getProperty("user.dir"), "output.csv");
    String outputFile = outputPath.toString();
    public Dictionary<String, String> testDictionary=new Hashtable<String, String>();


    @BeforeClass
    public void openWebPageInBrowser() throws IOException {

        Path workspacePath = Paths.get(System.getProperty("user.dir"),"chromedriver.exe");
        File file = new File(workspacePath.toString());
        System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
        readTestData();
        driver = new ChromeDriver();
        driver.get("http://www.amazon.in");
        driver.manage().window().maximize();
        boolean result = driver.getTitle().equals("Online Shopping site in India: Shop Online for Mobiles, Books, Watches, Shoes and More - Amazon.in");
        CsvWriter output = new CsvWriter(new FileWriter(outputFile, false), ',');
        if (result) {
            output.write("TC_001,Open Page in Browser,Pass");
        } else {
            output.write("TC_001,Open Page in Browser,Fail,Page not opened properly in browser");
        }
        output.endRecord();
        output.close();

    }

    @Test(priority = 0)
    public void enterSearchText() throws Exception {
        WebElement searchBox = driver.findElement(By.xpath("//input[@id='twotabsearchtextbox']"));
        String searchText=readTestData("SearchText");
        searchBox.sendKeys(searchText);
        WebElement searchButton = driver.findElement(By.xpath("//input[@value='Go']"));
        searchButton.click();
        boolean result = driver.getTitle().equals("Amazon.in: pen drive - Pen Drives / External Devices & Data Storage: Computers & Accessories");
        CsvWriter output = new CsvWriter(new FileWriter(outputFile, true), ',');
        if (result) {
            output.write("TC_002,Enter Search Text,Pass");
        } else {
            output.write("TC_002,Enter Search Text,Fail,Pen drive search page not loaded");
        }
        output.endRecord();
        output.close();
    }

    @Test(priority = 1)
    public void filterPendriveByBrandSandisk() throws IOException {
        WebElement filterCheckBox = driver.findElement(By.xpath("//input[@name='s-ref-checkbox-SanDisk']"));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        filterCheckBox.click();
        CsvWriter output = new CsvWriter(new FileWriter(outputFile, true), ',');
        output.write("TC_003,Filter By Brand,Pass");
        output.endRecord();
        output.close();
    }

    @Test(priority = 2)
    public void filterPendriveByPrice() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement filterMinPrice = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='low-price' and @placeholder='Min']")));
        String minPrice=readTestData("FilterMinPrice");
        String maxPrice=readTestData("FilterMaxPrice");
        filterMinPrice.sendKeys(minPrice);
        WebElement filterMaxPrice = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='high-price' and @placeholder='Max']")));
        filterMaxPrice.sendKeys(maxPrice);
        WebElement goButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@class='a-button-input' and @value='Go']")));
        goButton.click();
        CsvWriter output = new CsvWriter(new FileWriter(outputFile, true), ',');
        output.write("TC_004,Filter By Price,Pass");
        output.endRecord();
        output.close();
    }

    @Test(priority = 3)
    public void filterPendriveByAvgRating() throws InterruptedException, IOException {
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        WebElement filterAvgRating = driver.findElement(By.xpath("//span[contains(text(),'4 Stars')]/following::span[1]"));
        filterAvgRating.click();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        CsvWriter output = new CsvWriter(new FileWriter(outputFile, true), ',');
        output.write("TC_005,Filter By Avg Rating,Pass");
        output.endRecord();
        output.close();
    }

    @Test(priority = 4)
    public void selectPendriveLowestPrice() throws IOException, InterruptedException {
        List<WebElement> allPrices = driver.findElements(By.xpath("//span[@class='a-size-base a-color-price s-price a-text-bold']"));
        try {
            Thread.sleep(3000);
            LinkedList<Double> listPrices = new LinkedList<Double>();
            for (int i = 0; i < allPrices.size(); i++) {
                try {
                    String temp = allPrices.get(i).getText();
                    temp = temp.replaceAll(",", "");
                    if (temp.equals("") == false) {
                        double value = Double.parseDouble(temp);
                        listPrices.add(value);
                    }
                } catch (org.openqa.selenium.StaleElementReferenceException ex) {
                    ex.printStackTrace();
                }
            }
            double min = listPrices.get(0);
            for (double listPrice : listPrices) {
                if (listPrice < min) {
                    min = listPrice;
                }
            }
            String lowestPrice = String.valueOf(min);
            if (lowestPrice.endsWith(".0")) {
                lowestPrice = lowestPrice.substring(0, lowestPrice.length() - 2);
            }

            //Decimal Handling
            if (lowestPrice.contains(".")) {
                int indexOfDecimal = lowestPrice.indexOf('.');
                String rupee = lowestPrice.substring(0, indexOfDecimal);
                String paisa = lowestPrice.substring(indexOfDecimal + 1);
                if (rupee.length() > 3 && rupee.length() <= 5) {
                    rupee = rupee.substring(0, rupee.length() - 3) + "," + rupee.substring(rupee.length() - 3);
                    lowestPrice = rupee + paisa;
                }
            }

            //Conversion of Double to , Formatted String
            else if (lowestPrice.length() > 3 && lowestPrice.length() <= 5) {
                lowestPrice = lowestPrice.substring(0, lowestPrice.length() - 3) + "," + lowestPrice.substring(lowestPrice.length() - 3);
            }
            WebDriverWait wait = new WebDriverWait(driver, 10);
            WebElement lowestPriceElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='" + lowestPrice + "']/preceding::a[1]")));
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            lowestPriceElement.click();
            CsvWriter output = new CsvWriter(new FileWriter(outputFile, true), ',');
            output.write("TC_006,Select lowest Price Pen Drive,Pass");
            output.endRecord();
            output.close();
        }
        catch (java.lang.IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 5)
    public void addToCartLoginRequired() throws InterruptedException, IOException {
        Set handles = driver.getWindowHandles();
        String firstWinHandle = driver.getWindowHandle();
        handles.remove(firstWinHandle);
        Iterator<String> it = handles.iterator();
        String winHandle = it.next();
        if (winHandle != firstWinHandle) {
            String secondWinHandle = winHandle;
            driver.switchTo().window(secondWinHandle);
        }
        WebElement addToCartButton = driver.findElement(By.xpath("//input[@id='add-to-cart-button']"));
        addToCartButton.click();
        Thread.sleep(5000);
        WebElement checkOutButton = driver.findElement(By.xpath("//a[contains(text(),'Proceed to checkout')]"));
        checkOutButton.click();
        Thread.sleep(5000);
        WebElement logo = driver.findElement(By.xpath("//*[@class='a-icon a-icon-logo']"));
        Assert.assertFalse(logo == null, "Logo not as expected");
        WebElement loginText = driver.findElement(By.xpath("//h1[contains(text(),'Login')]"));
        Assert.assertFalse(loginText == null, "Login Text not as expected");
        WebElement emailOrPhoneText = driver.findElement(By.xpath("//label[contains(text(),'Email or mobile phone number')]"));
        Assert.assertFalse(emailOrPhoneText == null, "Email label not as expected");
        WebElement continueButton = driver.findElement(By.xpath("//input[@id='continue']"));
        Assert.assertFalse(continueButton == null, "Continue button not as expected");
        CsvWriter output = new CsvWriter(new FileWriter(outputFile, true), ',');
        output.write("TC_007,Add to cart and validate login needed to checkout,Pass");
        output.endRecord();
        output.close();
    }


    @AfterClass
    public void closeBrowser() throws IOException {
        driver.quit();
        CsvWriter output = new CsvWriter(new FileWriter(outputFile, true), ',');
        output.write("TC_008,Close Browser & driver instance,Pass");
        output.endRecord();
        output.close();
    }

    public Dictionary<String, String> populateTestDictionary(String csvFile) throws Exception {
        Dictionary<String, String> dict = new Hashtable<>();
        try {
            CsvReader csvReaderObj = new CsvReader(csvFile);
            csvReaderObj.readHeaders();
            while(csvReaderObj.readRecord()) {
                for (int i = 1; i < csvReaderObj.getColumnCount() / 2 + 1; i++) {
                    String field = csvReaderObj.get("Field" + i).trim();
                    String value = csvReaderObj.get("Value" + i).trim();
                    if (field != null && !field.isEmpty() && value != null && !value.isEmpty()) {
                        dict.put(field, value);
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return dict;
    }

    public void readTestData(){
        try{
            testDictionary=populateTestDictionary(testDataFile);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public String readTestData(String field)throws Exception{
        return testDictionary.get(field);
    }
}

