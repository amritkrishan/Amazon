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

import java.util.*;
import java.util.concurrent.TimeUnit;


public class AmazonTest {

    WebDriver driver = null;

    @BeforeClass
    public void openWebPageInBrowser(){
        driver = new ChromeDriver();
        driver.get("http://www.amazon.in");
        driver.manage().window().maximize();

    }

    @Test(priority=0)
    public void enterSearchText(){
        WebElement searchBox = driver.findElement(By.xpath("//input[@id='twotabsearchtextbox']"));
        searchBox.sendKeys("pen drive");
        WebElement searchButton = driver.findElement(By.xpath("//input[@value='Go']"));
        searchButton.click();
    }

    @Test(priority=1)
    public void filterPendriveByBrandSandisk(){
        WebElement filterCheckBox = driver.findElement(By.xpath("//input[@name='s-ref-checkbox-SanDisk']"));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        filterCheckBox.click();
    }

    @Test(priority=2)
    public void filterPendriveByPrice(){
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement filterMinPrice = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='low-price' and @placeholder='Min']")));
        filterMinPrice.sendKeys("300");
        WebElement filterMaxPrice = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='high-price' and @placeholder='Max']")));
        filterMaxPrice.sendKeys("1000");
        WebElement goButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@class='a-button-input' and @value='Go']")));
        goButton.click();
    }

    @Test(priority=3)
    public void filterPendriveByAvgRating() throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(3,TimeUnit.SECONDS);
        WebElement filterAvgRating=driver.findElement(By.xpath("//span[contains(text(),'4 Stars')]/following::span[1]"));
        filterAvgRating.click();
    }

    @Test(priority=4)
    public void selectPendriveLowestPrice() throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(10,TimeUnit.SECONDS);
        List<WebElement> allPrices=driver.findElements(By .xpath("//span[@class='a-size-base a-color-price s-price a-text-bold']"));
        Thread.sleep(2000);
        List<Double> listPrices=new LinkedList<Double>();
        for(WebElement price: allPrices){
            String temp=price.getText();
            temp=temp.replaceAll(",","");
            if(temp.equals("")==false) {
                double value=Double.parseDouble(temp);
                listPrices.add(value);
            }
        }
        double min=listPrices.get(0);
        for(double listPrice : listPrices){
            if(listPrice<min){
                min=listPrice;
            }
        }
        String lowestPrice=String.valueOf(min);
        if(lowestPrice.endsWith(".0")){
            lowestPrice=lowestPrice.substring(0,lowestPrice.length()-2);
        }

        //Decimal Handling
        if(lowestPrice.contains(".")){
            int indexOfDecimal=lowestPrice.indexOf('.');
            String rupee=lowestPrice.substring(0,indexOfDecimal);
            String paisa=lowestPrice.substring(indexOfDecimal+1);
            if(rupee.length()>3 && rupee.length()<=5){
                rupee=rupee.substring(0,rupee.length()-3)+","+rupee.substring(rupee.length()-3);
                lowestPrice=rupee+paisa;
            }
        }

        //Conversion of Double to , Formatted String
        else if(lowestPrice.length()>3 && lowestPrice.length()<=5) {
            lowestPrice=lowestPrice.substring(0,lowestPrice.length()-3)+","+lowestPrice.substring(lowestPrice.length()-3);
        }
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement lowestPriceElement=wait.until(ExpectedConditions.visibilityOfElementLocated(By .xpath("//span[text()='"+lowestPrice+"']/preceding::a[1]")));
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        lowestPriceElement.click();
    }

    @Test(priority = 5)
    public void addToCartLoginRequired() throws InterruptedException {
        Set handles = driver.getWindowHandles();
        String firstWinHandle = driver.getWindowHandle();
        handles.remove(firstWinHandle);
        Iterator<String> it=handles.iterator();
        String winHandle=it.next();
        if (winHandle!=firstWinHandle) {
            String secondWinHandle = winHandle;
            driver.switchTo().window(secondWinHandle);
        }
        WebElement addToCartButton=driver.findElement(By .xpath("//input[@id='add-to-cart-button']"));
        addToCartButton.click();
        Thread.sleep(5000);
        WebElement checkOutButton=driver.findElement(By .xpath("//a[contains(text(),'Proceed to checkout')]"));
        checkOutButton.click();
        Thread.sleep(5000);
        WebElement logo=driver.findElement(By .xpath("//*[@class='a-icon a-icon-logo']"));
        Assert.assertFalse(logo==null,"Logo not as expected");
        WebElement loginText=driver.findElement(By .xpath("//h1[contains(text(),'Login')]"));
        Assert.assertFalse(loginText==null,"Login Text not as expected");
        WebElement emailOrPhoneText=driver.findElement(By .xpath("//label[contains(text(),'Email or mobile phone number')]"));
        Assert.assertFalse(emailOrPhoneText==null,"Email label not as expected");
        WebElement continueButton=driver.findElement(By .xpath("//input[@id='continue']"));
        Assert.assertFalse(continueButton==null,"Continue button not as expected");
    }

    @AfterClass
    public void closeBrowser(){
        driver.quit();
    }
}
