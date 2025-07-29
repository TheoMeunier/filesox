package tmeunier.fr.exceptions.common

import tmeunier.fr.exceptions.core.ApiException

class UnauthorizedException : ApiException(
    "UNAUTHORIZED",
    "You are not authorized to perform this action.",
)