package ie.setu.selenium

import ie.setu.config.ServerConfig
import ie.setu.helpers.TestDB
import ie.setu.helpers.populateActivityTable
import ie.setu.helpers.populateUserTable
import io.javalin.Javalin
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.*
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import java.time.Duration

class AddUserTest {
    private lateinit var driver: WebDriver
    private var baseUrl: String? = null
    private var acceptNextAlert = true
    private val verificationErrors = StringBuffer()
    var js: JavascriptExecutor? = null
    private lateinit var app: Javalin // Javalin app instance

    companion object {
        @BeforeAll
        @JvmStatic
        fun setupInMemoryDatabase() {
            TestDB.connect()
        }
    }

    @BeforeEach
    fun setUp() {
        // Reset the h2 database and give it starting users and activities
        TestDB.reset()
        seedTestData()
        // Another approach for headless chrome
        val options = ChromeOptions()
            .addArguments("--headless=new")
            .addArguments("--no-sandbox")
            .addArguments("--disable-dev-shm-usage")
        driver = ChromeDriver(options)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60))
        js = driver as JavascriptExecutor
    }

    @Test
    @Throws(Exception::class)
    fun testAddUserSuccessfullyAddsToDatabase() {
        app = ServerConfig().startJavalinService()
        driver.get("http://localhost:${app.port()}/")
        driver.findElement(By.id("users-more-details")).click()
        driver.findElement(By.id("add-button")).click()
        driver.findElement(By.name("name")).click()
        driver.findElement(By.name("name")).clear()
        driver.findElement(By.name("name")).sendKeys("Lisa Simpson")
        driver.findElement(By.name("email")).click()
        driver.findElement(By.name("email")).clear()
        driver.findElement(By.name("email")).sendKeys("lisa@simpson.com")
        driver.findElement(By.name("email")).click()
        driver.findElement(By.xpath("//body")).click()
        driver.findElement(By.xpath("//main[@id='main-vue']/div/div/div/div/div/div[2]/button")).click()
        driver.findElement(By.linkText("Lisa Simpson (lisa@simpson.com)")).click()
    }


    @AfterEach
    fun tearDown() {
        if (::driver.isInitialized) {
            driver.quit()
        }
        if (::app.isInitialized) {
            app.stop()
        }
        val verificationErrorString = verificationErrors.toString()
        if (verificationErrorString.isNotEmpty()) {
            throw AssertionError(verificationErrorString)
        }
    }

    /*private fun isElementPresent(by: By?): Boolean {
        try {
            driver.findElement(by)
            return true
        } catch (e: NoSuchElementException) {
            return false
        }
    }*/

    private val isAlertPresent: Boolean
        get() {
            try {
                driver.switchTo().alert()
                return true
            } catch (e: NoAlertPresentException) {
                return false
            }
        }

    private fun closeAlertAndGetItsText(): String? {
        try {
            val alert: Alert = driver.switchTo().alert()
            val alertText: String? = alert.getText()
            if (acceptNextAlert) {
                alert.accept()
            } else {
                alert.dismiss()
            }
            return alertText
        } finally {
            acceptNextAlert = true
        }
    }

    private fun seedTestData() {
        transaction {
            populateUserTable()
            populateActivityTable()
        }
    }

}