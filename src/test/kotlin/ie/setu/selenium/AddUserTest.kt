package ie.setu.selenium

import ie.setu.config.DbConfig
import ie.setu.config.JavalinConfig
import io.javalin.Javalin
import io.javalin.testtools.JavalinTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

class AddUserTest {
    private lateinit var app: Javalin
    private lateinit var driver: WebDriver
    private var wait: WebDriverWait? = null
    private var vars: Map<String, Any>? = null
    var js: JavascriptExecutor? = null

    @Before
    fun setUp() {
        //Connect to the remote database
        DbConfig().getDbConnection()
        driver = ChromeDriver()
        js = driver as JavascriptExecutor
        vars = HashMap()
    }

    @After
    fun tearDown() {
        driver.quit()
    }

    @Test
    fun addUser() {
        app = JavalinConfig().getJavalinService()
        wait = WebDriverWait(driver, Duration.ofSeconds(30))

        JavalinTest.test(app) { _, client ->

            //implicit wait for elements to be loaded
          //  driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
            driver.get(client.origin)
            driver.findElement(By.linkText("More Details...")).click()

           run {
                val element: WebElement = driver.findElement(By.cssSelector(".btn:nth-child(1)"))
                val builder: Actions = Actions(driver)
                builder.moveToElement(element).perform()
            }

            run {
                val element: WebElement = driver.findElement(By.tagName("body"))
                val builder: Actions = Actions(driver)
                builder.moveToElement(element, 0, 0).perform()
            }

            driver.findElement(By.cssSelector(".fa-plus")).click()
            driver.findElement(By.name("name")).click()
            driver.findElement(By.name("name")).sendKeys("Homer Simpson")
            driver.findElement(By.name("email")).sendKeys("homer@simpson.com")
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
            driver.findElement(By.linkText("Homer Simpson (homer@simpson.com)")).click()

            wait!!.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[title='Delete']")))
            driver.findElement(By.cssSelector(".fas")).click()
            assertThat(
                driver.switchTo().alert().getText(),
                CoreMatchers.`is`<String>("Do you really want to delete?")
            )
            driver.switchTo().alert().accept()
        }
    }
}