package modules

import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides}
import models.daos.{PasswordInfoDAO, UserDAO}
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.ws.WSClient
import play.api.mvc.SessionCookieBaker
import play.silhouette.api.crypto._
import play.silhouette.api.repositories.AuthInfoRepository
import play.silhouette.api.services._
import play.silhouette.api.util._
import play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import play.silhouette.crypto.{JcaCrypter, JcaCrypterSettings}
import play.silhouette.impl.authenticators._
import play.silhouette.impl.providers._
import play.silhouette.impl.util._
import play.silhouette.password.{BCryptPasswordHasher, BCryptSha256PasswordHasher}
import play.silhouette.persistence.daos.DelegableAuthInfoDAO
import play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import utils.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * The Guice module which wires all Silhouette dependencies.
 */
class SilhouetteModule extends AbstractModule with ScalaModule {

  /**
   * Configures the module.
   */
  override def configure(): Unit = {
    bind[Silhouette[DefaultEnv]].to[SilhouetteProvider[DefaultEnv]]
    bind[DelegableAuthInfoDAO[PasswordInfo]].to[PasswordInfoDAO]
    bind[CacheLayer].to[PlayCacheLayer]
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[PasswordHasher].toInstance(new BCryptPasswordHasher)
    bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator(includeRemoteAddress = false))
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())
  }

  /**
   * Provides the HTTP layer implementation.
   *
   * @param client Play's WS client.
   * @return The HTTP layer implementation.
   */
  @Provides
  def provideHTTPLayer(client: WSClient): HTTPLayer = new PlayHTTPLayer(client)

  /**
   * Provides the Silhouette environment.
   *
   * @param userService          The user service implementation.
   * @param authenticatorService The authentication service implementation.
   * @param eventBus             The event bus instance.
   * @return The Silhouette environment.
   */
  @Provides
  def provideEnvironment(userService: UserDAO,
                         authenticatorService: AuthenticatorService[SessionAuthenticator],
                         eventBus: EventBus): Environment[DefaultEnv] = {
    Environment[DefaultEnv](
      userService,
      authenticatorService,
      Seq(),
      eventBus
    )
  }

  /**
   * Provides the crypter for the authenticator.
   *
   * @param configuration The Play configuration.
   * @return The crypter for the authenticator.
   */
  @Provides
  @Named("authenticator-crypter")
  def provideAuthenticatorCrypter(configuration: Configuration): Crypter = {
    val config = configuration.underlying.as[JcaCrypterSettings]("silhouette.authenticator.crypter")
    new JcaCrypter(config)
  }

  /**
   * Provides the auth info repository.
   *
   * @param passwordInfoDAO The implementation of the delegable password auth info DAO.
   * @return The auth info repository instance.
   */
  @Provides
  def provideAuthInfoRepository(passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo]): AuthInfoRepository = {
    new DelegableAuthInfoRepository(passwordInfoDAO)
  }

  /**
   * Provides the authenticator service.
   *
   * @param crypter              The crypter implementation.
   * @param fingerprintGenerator The fingerprint generator implementation.
   * @param configuration        The Play configuration.
   * @param clock                The clock instance.
   * @return The authenticator service.
   */
  @Provides
  def provideAuthenticatorService(@Named("authenticator-crypter") crypter: Crypter,
                                  fingerprintGenerator: FingerprintGenerator,
                                  sessionCookieBaker: SessionCookieBaker,
                                  configuration: Configuration,
                                  clock: Clock): AuthenticatorService[SessionAuthenticator] = {
    val config = configuration.underlying.as[SessionAuthenticatorSettings]("silhouette.authenticator")
    val authenticatorEncoder = new CrypterAuthenticatorEncoder(crypter)
    new SessionAuthenticatorService(config, fingerprintGenerator, authenticatorEncoder, sessionCookieBaker, clock)
  }

  /**
   * Provides the password hasher registry.
   *
   * @return The password hasher registry.
   */
  @Provides
  def providePasswordHasherRegistry(): PasswordHasherRegistry = {
    PasswordHasherRegistry(new BCryptSha256PasswordHasher(), Seq(new BCryptPasswordHasher()))
  }

  /**
   * Provides the credentials provider.
   *
   * @param authInfoRepository     The auth info repository implementation.
   * @param passwordHasherRegistry The password hasher registry.
   * @return The credentials provider.
   */
  @Provides
  def provideCredentialsProvider(authInfoRepository: AuthInfoRepository,
                                 passwordHasherRegistry: PasswordHasherRegistry): CredentialsProvider = {
    new CredentialsProvider(authInfoRepository, passwordHasherRegistry)
  }

  @Provides
  def providePasswordInfoDAO(dbConfigProvider: DatabaseConfigProvider): PasswordInfoDAO = {
    new PasswordInfoDAO(dbConfigProvider)
  }
}
