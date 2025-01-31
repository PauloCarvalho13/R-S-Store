package pt.rs

/**
 * Generic repository interface for basic CRUD operations
 */
interface Repository<T> {
    /**
     * Create a new entity or update an existing entity
     * @param entity the entity to be created
     */
    fun save(entity: T)

    /**
     * Find all entities
     * @return a list with all entities or an empty list if there are no entities
     */
    fun findAll(): List<T>

    /**
     * Find an entity by its id
     * @param id the id of the entity
     * @return the entity with the given id or null if the entity does not exist
     */
    fun findById(id: Int): T?


    /**
     * Delete an entity
     * @param id the id of the entity to be deleted
     */
    fun delete(id:Int)

    /**
     * Clear all entities
     */
    fun clear()
}