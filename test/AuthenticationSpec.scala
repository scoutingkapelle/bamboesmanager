import java.util.UUID
import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.api.{Environment, LoginInfo}
import com.mohiva.play.silhouette.test._
import models.User
import net.codingwell.scalaguice.ScalaModule
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.CSRFTokenHelper._
import play.api.test.{FakeRequest, PlaySpecification, WithApplication}
import utils.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Test case for the [[controllers.Authentication]] class.
  */
class AuthenticationSpec extends PlaySpecification with Mockito {
  sequential

  /*"The `index` action" should {
    "redirect to login page if user is unauthorized" in new Context {
      new WithApplication(application) {
        val Some(redirectResult) = route(app, FakeRequest()
          .withAuthenticator[User](LoginInfo("invalid", "invalid"))
        )

        status(redirectResult) must be equalTo SEE_OTHER

        val redirectURL = redirectLocation(redirectResult).getOrElse("")
        redirectURL must contain(routes.SignInController.view().toString)

        val Some(unauthorizedResult) = route(app, addCSRFToken(FakeRequest(GET, redirectURL)))

        status(unauthorizedResult) must be equalTo OK
        contentType(unauthorizedResult) must beSome("text/html")
        contentAsString(unauthorizedResult) must contain("Silhouette - Sign In")
      }
    }

    "return 200 if user is authorized" in new Context {
      new WithApplication(application) {
        val Some(result) = route(app, addCSRFToken(FakeRequest(routes.ApplicationController.index())
          .withAuthenticator[User](identity))
        )

        status(result) must beEqualTo(OK)
      }
    }
  }*/

  /**
    * The context.
    */
  trait Context extends Scope {

    /**
      * A fake Guice module.
      */
    class FakeModule extends AbstractModule with ScalaModule {
      override def configure(): Unit = {
        bind[Environment[DefaultEnv]].toInstance(env)
      }
    }

    /**
      * An identity.
      */
    val identity: User = User(
      id = UUID.randomUUID(),
      name = "John Doe",
      email = "jdoe@example.com"
    )

    /**
      * A Silhouette fake environment.
      */
    implicit val env: Environment[DefaultEnv] = new FakeEnvironment[DefaultEnv](Seq(LoginInfo("credentials", identity.email) -> identity))

    /**
      * The application.
      */
    lazy val application: Application = new GuiceApplicationBuilder()
      .overrides(new FakeModule)
      .build()
  }
}
