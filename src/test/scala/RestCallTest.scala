import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.json4s.native.Serialization
import org.json4s.{DefaultFormats, Formats}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import sttp.client3.{basicRequest, SimpleHttpClient}
import sttp.model.Uri

class RestCallTest extends AnyFlatSpec with BeforeAndAfterEach {
  implicit val formats: Formats = DefaultFormats
  private val client: SimpleHttpClient = SimpleHttpClient()
  private val hostname = "localhost"
  private val port = 8080
  private val path = s"/api/v1/movies"
  private val url = s"http://$hostname:$port$path"

  private val wireMockServer = new WireMockServer(
    wireMockConfig()
      .port(port)
  )

  override def beforeEach: Unit =
    wireMockServer.start()

  override def afterEach: Unit =
    wireMockServer.stop()

  "client" should "send proper request" in {
    val movies: Map[String, String] = Map(
      "1" -> "The Shawshank Redemption",
      "2" -> "The Godfather",
      "3" -> "The Dark Knight"
    )

    setupWiremockResponse(
      aResponse()
        .withHeader("Content-Type", "application/json")
        .withBody(Serialization.write(movies))
        .withStatus(200)
    )

    callGetMovies()

    verifyPathHasBeenCalled(path)
  }

  "client" should "send proper request with json file source for mock" in {
    setupWiremockResponse(
      aResponse()
        .withHeader("Content-Type", "application/json")
        .withBodyFile("jokes.json")
        .withStatus(200)
    )

    callGetMovies()
    verifyPathHasBeenCalled(path)
  }

  private def setupWiremockResponse(response: ResponseDefinitionBuilder): StubMapping =
    wireMockServer.stubFor(
      get(urlPathEqualTo(path))
        .willReturn(
          response
        )
    )

  private def verifyPathHasBeenCalled(path: String) =
    wireMockServer.verify(
      getRequestedFor(urlPathEqualTo(path))
        .withHeader("Content-Type", containing("application/json"))
    )

  def callGetMovies(): Unit =
    Uri
      .parse(url)
      .map(uri =>
        client.send(
          basicRequest
            .header("Content-Type", "application/json")
            .get(uri)
        )
      )
}
