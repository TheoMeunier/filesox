package tmeunier.fr.exceptions.auth

import tmeunier.fr.exceptions.core.ApiException

class RefreshTokenExpiredException : ApiException(
    "INVALID_CREDENTIALS",
    "Refresh token has expired, please log in again.",
)