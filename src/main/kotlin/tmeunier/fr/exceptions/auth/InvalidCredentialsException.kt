package tmeunier.fr.exceptions.auth

import tmeunier.fr.exceptions.core.ApiException

class InvalidCredentialsException : ApiException(
    "INVALID_CREDENTIALS",
    "Invalid email or password",
)