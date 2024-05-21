package ie.setu.selenium

import ie.setu.config.DbConfig
import ie.setu.config.JavalinConfig
import io.javalin.Javalin
import io.javalin.testtools.JavalinTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

class AddUserTest {

    private lateinit var app: Javalin
    private var wait: WebDriverWait? = null

    @BeforeEach
    fun setup() {
        //Connect to the remote database
        DbConfig().getDbConnection()
    }

    @AfterEach
    fun teardown() {
    }

    @Test
    fun addUser() {
        app = JavalinConfig().getJavalinService()

        JavalinTest.test(app) { _, client ->

            // Set up Firefox WebDriver
            val options = FirefoxOptions()
            options.addArguments("--headless") // Run Firefox in headless mode
            val driver = FirefoxDriver(options)

            wait = WebDriverWait(driver, Duration.ofSeconds(30))

            driver.get(client.origin)

            driver.findElement(By.linkText("More Details...")).click()

            driver.findElement(By.cssSelector("button[title='Add']")).click()
            driver.findElement(By.name("name")).click()
            driver.findElement(By.name("name")).sendKeys("Lisa Simpson")
            driver.findElement(By.name("email")).click()
            driver.findElement(By.name("email")).sendKeys("lisa@simpson.com")
            driver.findElement(By.cssSelector(".card-body > .btn")).click()
            run {
                val element: WebElement = driver.findElement(By.cssSelector(".card-body > .btn"))
                val builder = Actions(driver)
                builder.moveToElement(element).perform()
            }
            run {
                val element: WebElement = driver.findElement(By.tagName("body"))
                val builder = Actions(driver)
                builder.moveToElement(element, 0, 0).perform()
            }

            // Wait for the presence of list-group-item(s) by checking if at least one exists
            wait!!.until(ExpectedConditions.presenceOfElementLocated(By.name("list-group")))
            wait!!.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.name("list-group-item"), 0))

            //Click on the new user and verify we are brought to the user profile page
            driver.findElement(By.linkText("Lisa Simpson (lisa@simpson.com)")).click()
            assertThat(driver.pageSource).contains("<template id=\"user-profile\">")

            wait!!.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[title='Delete']")))
            driver.findElement(By.cssSelector("button[title='Delete']")).click()
            //driver.findElement(By.cssSelector(".list-group-item:nth-child(8) .fas")).click()
            assertThat(
                driver.switchTo().alert().text
            ).isEqualTo("Do you really want to delete?")
            driver.switchTo().alert().accept()

            driver.quit()
        }
    }

}