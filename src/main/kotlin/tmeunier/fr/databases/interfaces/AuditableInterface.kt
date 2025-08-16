package tmeunier.fr.databases.interfaces

interface AuditableInterface {
    fun getAuditId(): String?
    fun getAuditName(): String
    fun getEntityType(): String
}