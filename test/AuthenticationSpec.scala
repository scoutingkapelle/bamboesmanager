package controllers

import java.util.UUID

import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.api.{Environment, LoginInfo}
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import com.mohiva.play.silhouette.test._
import models.User
import net.codingwell.scalaguice.ScalaModule
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.concurrent.Execution.Implicits._
import play.api.test.{FakeRequest, PlaySpecification, WithApplication}

/**
  * Test case for the [[controllers.Application]] class.
  */
class AuthenticationSpec extends PlaySpecification with Mockito {
  sequential

  "The `index` action" should {
    "redirect to login page if user is unauthorized" in new Context {
      new WithApplication(application) {
        val Some(redirectResult) = route(FakeRequest(routes.Application.dashboard())
          .withAuthenticator[SessionAuthenticator](LoginInfo("invalid", "invalid"))
        )

        status(redirectResult) must be equalTo SEE_OTHER

        val redirectURL = redirectLocation(redirectResult).getOrElse("")
        redirectURL must contain(routes.Application.signIn().toString())

        val Some(unauthorizedResult) = route(FakeRequest(GET, redirectURL))

        status(unauthorizedResult) must be equalTo OK
        contentType(unauthorizedResult) must beSome("text/html")
        contentAsString(unauthorizedResult) must contain("Inloggen")
      }
    }

    "return 200 if user is authorized" in new Context {
      new WithApplication(application) {
        val Some(result) = route(FakeRequest(routes.Application.index())
          .withAuthenticator[SessionAuthenticator](LoginInfo("credentials", identity.email)
        ))

        status(result) must beEqualTo(OK)
      }
    }
  }

  /**
    * The context.
    */
  trait Context extends Scope {

    /**
      * A fake Guice module.
      */
    class FakeModule extends AbstractModule with ScalaModule {
      def configure() = {
        bind[Environment[User, SessionAuthenticator]].toInstance(env)
      }
    }

    /**
      * An identity.
      */
    val identity = User(
      id = UUID.randomUUID(),
      name = "John Doe",
      email = "jdoe@example.com"
    )

    /**
      * A Silhouette fake environment.
      */
    implicit val env: Environment[User, SessionAuthenticator] = new FakeEnvironment[User, SessionAuthenticator](Seq(LoginInfo("credentials", identity.email) -> identity))

    /**
      * The application.
      */
    lazy val application = new GuiceApplicationBuilder()
      .overrides(new FakeModule)
      .build()
  }
}
