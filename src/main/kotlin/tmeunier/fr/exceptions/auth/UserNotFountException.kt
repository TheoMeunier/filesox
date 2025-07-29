package tmeunier.fr.exceptions.auth

import tmeunier.fr.exceptions.core.ApiException

class UserNotFountException(email: String) : ApiException(
    "INVALID_CREDENTIALS",
    "Invalid email or password for user: $email",
)