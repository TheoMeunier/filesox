package tmeunier.fr.exceptions.auth

import tmeunier.fr.exceptions.core.ApiException

class UserAlreadyExistFountException(email: String) : ApiException(
    "INVALID_CREDENTIALS",
    "User with email $email already exists.",
)