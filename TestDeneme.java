package com.qa;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestDeneme extends MyDriver {

    private WebDriverWait wait;
    private List<String> combinedDataList;

    @BeforeClass
    public void setUp() throws IOException {
        super.setUp();  
        driver.get(Constants.BASE_URL1);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        combinedDataList = new ArrayList<>();
    }

    @Test
    public void ticketSearchTest() throws InterruptedException {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fromTrainInput")));
        WebElement fromTrainInput = driver.findElement(By.id("fromTrainInput"));
        fromTrainInput.sendKeys("ankara");
        fromTrainInput.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("gidis-2503")));
        WebElement dropdownItem = driver.findElement(By.id("gidis-2503"));
        dropdownItem.click();

        wait.until(ExpectedConditions.elementToBeClickable(By.name("Tren varış")));
        WebElement arrivalInput = driver.findElement(By.name("Tren varış"));
        arrivalInput.sendKeys("Kars, Kars");
        arrivalInput.click();
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("donus-4594")));
        WebElement dropdownItem1 = driver.findElement(By.id("donus-4594"));
        dropdownItem1.click();

        WebElement seferAraButton = driver.findElement(By.className("btnSeferSearch"));
        seferAraButton.click();

        for (int i = 0; i < 60; i++) {

            String buttonId = "btnSlickGidis" + i;
            String year = "2024";

            try {
                Thread.sleep(3000);
                WebElement button = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(buttonId)));
    
                WebElement dayElement = wait.until(ExpectedConditions.visibilityOf(button.findElement(By.className("day"))));
                String day = dayElement.getText().trim();
    
                WebElement monthElement = wait.until(ExpectedConditions.visibilityOf(button.findElement(By.className("month"))));
                String month = monthElement.getText().trim();
    
                WebElement weekDayElement = wait.until(ExpectedConditions.visibilityOf(button.findElement(By.className("weekDay"))));
                String weekDay = weekDayElement.getText().trim();

    
                WebElement imgArea = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("imgArea")));
    
                WebElement disabledSeatElement = wait.until(ExpectedConditions.visibilityOf(imgArea.findElement(By.className("disabledSeat"))));
                String disabledSeatStr = disabledSeatElement.getText().trim();
    
                WebElement emptySeatElement = wait.until(ExpectedConditions.visibilityOf(imgArea.findElement(By.className("emptySeat"))));
                String emptySeatStr = emptySeatElement.getText().trim();
    
                WebElement emptyBedElement = wait.until(ExpectedConditions.visibilityOf(imgArea.findElement(By.className("emptyBed"))));
                String emptyBedStr = emptyBedElement.getText().trim();
    
                
                int disabledSeat = parseToInt(disabledSeatStr);
                int emptySeat = parseToInt(emptySeatStr);
                int emptyBed = parseToInt(emptyBedStr);
    
                
                int totalSeats = (disabledSeat > 0 ? disabledSeat : 0) + (emptySeat > 0 ? emptySeat : 0) + (emptyBed > 0 ? emptyBed : 0);
    
                String formattedDate = day + "-" + month + "-" + year + "-" + weekDay;
                String combinedData = formattedDate + " | Toplam Koltuklar: " + totalSeats + " (" + emptySeatStr + " boş koltuk), (" + emptyBedStr + " boş yatak), (" + disabledSeatStr + " kullanılamayan)";
                combinedDataList.add(combinedData);
    
            } catch (Exception e) {
                System.out.println("Veri çekme hatası: " + e.getMessage());
            }

            try {
                List<WebElement> nextButtons = driver.findElements(By.className("slick-next"));
                if (nextButtons.isEmpty()) {
                    System.out.println("Sonraki sayfa düğmesi bulunamadı. Döngü sonlandırılıyor.");
                    break;
                }
        
                WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(nextButtons.get(0)));
                nextButton.click();
        
                
                wait.until(ExpectedConditions.invisibilityOf(nextButton));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("slick-next")));
            } catch (Exception e) {
                System.out.println("Daha fazla sayfa yok veya bir hata oluştu: " + e.getMessage());
                break;
            }
            
            Thread.sleep(3000);
        }
        
        
        System.out.println("Veri Listesi:");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("veri_listesi.txt"))) {
            for (String data : combinedDataList) {
                System.out.println(data); 
                writer.write(data);
                writer.newLine(); 
            }
            System.out.println("Veriler 'veri_listesi.txt' dosyasına kaydedildi.");
        } catch (IOException e) {
            System.err.println("Dosya yazılırken bir hata oluştu: " + e.getMessage());
        }
    }


    
    private int parseToInt(String str) {
        try {
            return Integer.parseInt(str.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
