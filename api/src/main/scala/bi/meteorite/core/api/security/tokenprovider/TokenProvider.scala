/*
 * Copyright 2016 OSBI Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bi.meteorite.core.api.security.tokenprovider

import bi.meteorite.core.api.security.exceptions.TokenProviderException
import javax.servlet.http.HttpServletRequest

import scala.collection.immutable.TreeMap

trait TokenProvider {
  /**
    * Generates a new token for the specified set of token attributes. First of all a signature is created from the set
    * of attributes using a private key, only known by this token provider. The attributes are converted to a single
    * String and together with the signature this forms the unencrypted token. Finally, the token is encrypted using aAES
    * encryption method, again using a private key only known by this token provider. This encrypted token is returned.
    * Note that to this list of attributes always a nonce and a timestamp are added. Therefore attributes with names
    * NONCE or TIMESTAMP are preserved and should not be used. A TokenProviderException is thrown in case the map of
    * attributes already contains these attributes.
    *
    * @param attributes The attributes to create the token for. May be empty or null, in which case the token is
    *                   generated from only a nonce and a timestamp.
    * @return The generated encrypted token
    * @throws TokenProviderException In case an unexpected error occurred during token generation.
    */
  @throws(classOf[TokenProviderException])
  def generateToken(attributes: TreeMap[String, String]): String

  /**
    * Verifies if the token is valid. If the token is valid, a map of attributes is returned which are included in the
    * token. This is the same list as provided when the token was generated, but with additional nonce and timestamp
    * attributes. If the token is invalid, a InvalidTokenException is thrown.
    *
    * @param encryptedToken The encrypted token to verify
    * @return List of original attributes, plus nonce and timestamp attributes.
    * @throws TokenProviderException In case an unexpected error occurred during token generation.
    */
  @throws(classOf[TokenProviderException])
  def verifyToken(encryptedToken: String): TreeMap[String, String]

  /**
    * Invalidates the specified token. A token that has been invalidated cannot be used anymore.
    *
    * @param encryptedToken the token to invalidate.
    */
  def invalidateToken(encryptedToken: String)

  def getTokenFromRequest(request: HttpServletRequest): String
}


/**
  * The Token Provider.
  */
object TokenProvider {
  /**
    * The PID of the configuration of this service.
    */
  val PID: String = "org.amdatu.security.tokenprovider"
  /**
    * Name of the cookie that stores an Amdatu token.
    */
  val TOKEN_COOKIE_NAME: String = "saiku_token"
  /**
    * Name under which the nonce parameter is stored.
    */
  val NONCE: String = "token_nonce"
  /**
    * Name under which the timestamp parameter is stored.
    */
  val TIMESTAMP: String = "token_timestamp"
  /**
    * Name under which the tenant parameter is stored.
    */
  val TENANTID: String = "tenantid"
  /**
    * Name under which a username parameter can be stored (this is optional).
    */
  val USERNAME: String = "token_username"
  /**
    * Comma delimited role list (this is optional).
    */
  val ROLES: String = "token_rolelist"
}
