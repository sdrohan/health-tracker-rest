package ie.setu.selenium

import ie.setu.config.JavalinConfig
import ie.setu.helpers.TestDatabaseConfig
import ie.setu.helpers.populateActivityTable
import ie.setu.helpers.populateUserTable
import io.javalin.Javalin
import io.javalin.testtools.JavalinTest
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.openqa.selenium.*
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import java.time.Duration

@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class AddUserTest {

    private lateinit var driver: WebDriver
    private var acceptNextAlert = true
    private val verificationErrors = StringBuilder()
    private lateinit var js: JavascriptExecutor

    private lateinit var app: Javalin // Javalin app instance

    companion object {
        @BeforeAll
        @JvmStatic
        fun setupInMemoryDatabase() {
            TestDatabaseConfig.connect()  // same approach as API tests
        }
    }

    @BeforeEach
    fun setUp() {
        //Reset the h2 database and give it starting users and activities
        TestDatabaseConfig.reset()
        seedTestData()

        //Update to use headless mode using a different approach
        val options = ChromeOptions()
        options.addArguments("--headless=new")
        options.addArguments("--no-sandbox")
        options.addArguments("--disable-dev-shm-usage")
        driver = ChromeDriver(options)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60))
        js = driver as JavascriptExecutor
    }

    @Test
    fun testAddUser() {
        app = JavalinConfig().getJavalinService()
        JavalinTest.test(app) { _, client ->
           transaction {
                driver.get(client.origin)
                driver.findElement(By.linkText("More Details...")).click()
                driver.findElement(By.xpath("//main[@id='main-vue']/div/div/div/div/div/div/div/div[2]/button")).click()
                driver.findElement(By.name("name")).apply {
                    click()
                    clear()
                    sendKeys("Lisa Simpson")
                }
                driver.findElement(By.name("email")).apply {
                    click()
                    clear()
                    sendKeys("lisa@simpson.com")
                }
                driver.findElement(By.xpath("//main[@id='main-vue']/div/div/div/div/div/div[2]/button")).click()
                driver.findElement(By.linkText("Lisa Simpson (lisa@simpson.com)")).click()
            }
       }
    }

    @AfterEach
    fun tearDown() {
        driver.quit()
        val verificationErrorString = verificationErrors.toString()
        if (verificationErrorString.isNotEmpty()) {
            throw AssertionError(verificationErrorString)
        }
    }

    private fun isElementPresent(by: By): Boolean {
        return try {
            driver.findElement(by)
            true
        } catch (e: NoSuchElementException) {
            false
        }
    }

    private fun isAlertPresent(): Boolean {
        return try {
            driver.switchTo().alert()
            true
        } catch (e: NoAlertPresentException) {
            false
        }
    }

    private fun closeAlertAndGetItsText(): String {
        val alertText = try {
            val alert = driver.switchTo().alert()
            if (acceptNextAlert) {
                alert.accept()
            } else {
                alert.dismiss()
            }
            alert.text
        } finally {
            acceptNextAlert = true
        }
        return alertText
    }

    private fun seedTestData() {
        transaction {
            populateUserTable()
            populateActivityTable()
        }
    }
}
