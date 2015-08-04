
package org.sumanta.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * This class provides generic methods to do DB Operation and execute queries.
 * 
 */
public class PersistenceManager {

    @PersistenceContext(unitName="PManager")
    private EntityManager entityManager;

   /* @Inject
    private Logger logger;*/

    private static final String ID = "id";
    private static final String PATH_DELIMITER = ".";
    private static final int INITIAL_INDEX = 0;

    /**
     * Returns entity manager.
     * 
     * @return the entityManager
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * The method for creating an entity in database
     * 
     * @param entity
     *            Object of any JPA Entity that can be persistable.
     * @throws PersistenceException
     *             Parent Exception in JPA. Thrown when there are any DB Errors while persisting.
     */
    public <T> void createEntity(final T entity) throws PersistenceException {
       // logger.debug("Persisting entity {}", entity.getClass().getSimpleName());

        entityManager.persist(entity);
    }

    /**
     * The method for finding an entity in database
     * 
     * @param entityClass
     *            Class of JPA Entity on which this operation to be done.
     * @param id
     *            Unique Id using which entity need to be fetched.
     * @return JPA Entity fetched from DB.
     * @throws PersistenceException
     *             Parent Exception in JPA. Thrown when there are any DB Errors while persisting.
     */
    public <T> T findEntity(final Class<T> entityClass, final long id) throws PersistenceException {
        //logger.debug("Finding entity {} by ID {}", entityClass, id);

        final T entityResponse = entityManager.find(entityClass, id);
        return entityResponse;
    }

    /**
     * The method for finding the entity using name
     * 
     * @param entityClass
     *            Class of JPA Entity on which this operation to be done.
     * @param name
     *            Unique Name using which entity need to be fetched.
     * @param namePath
     *            Path of the 'name' attribute in JPAEntity.
     * @return JPA Entity fetched from DB.
     * @throws PersistenceException
     *             Parent Exception in JPA. Thrown when there are any DB Errors while persisting.
     */
    public <T> T findEntityByName(final Class<T> entityClass, final String name, final String namePath) throws PersistenceException {
        //logger.debug("Finding entity {} by name {}", entityClass, name);

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        final CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);

        final Root<T> entity = criteriaQuery.from(entityClass);
        final Path<T> path = getPathFromString(namePath, entity);
        final Expression<String> nameExpression = (Expression<String>) path;
        criteriaQuery.where(criteriaBuilder.equal(criteriaBuilder.lower(nameExpression), name.toLowerCase()));

        final TypedQuery<T> query = entityManager.createQuery(criteriaQuery);

        T Object = null;
        final List<T> Objects = query.getResultList();
        if (Objects.size() != 0) {
            Object = Objects.get(0);
        }
        return Object;
    }

    /**
     * The method for finding the entity based on id and name
     * 
     * @param entityClass
     *            Class of JPA Entity on which this operation to be done.
     * @param id
     *            Unique Id using which entity need to be fetched.
     * @param name
     *            Unique Name using which entity need to be fetched.
     * @param namePath
     *            Path of the 'name' attribute in JPAEntity.
     * @return JPA Entity fetched from DB.
     * @throws PersistenceException
     *             Parent Exception in JPA. Thrown when there are any DB Errors while persisting.
     */
    public <T> T findEntityByIdAndName(final Class<T> entityClass, final long id, final String name, final String namePath) throws PersistenceException {
        //logger.debug("Finding entity {} by name {}", entityClass, name);

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        final CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);

        final Root<T> entity = criteriaQuery.from(entityClass);
        final Path<T> path = getPathFromString(namePath, entity);
        final Expression<String> nameExpression = (Expression<String>) path;
        criteriaQuery.where(criteriaBuilder.and(criteriaBuilder.equal(entity.get(ID), id), criteriaBuilder.equal(criteriaBuilder.lower(nameExpression), name.toLowerCase())));

        final TypedQuery<T> query = entityManager.createQuery(criteriaQuery);

        T Object = null;
        final List<T> Objects = query.getResultList();
        if (Objects.size() != 0) {
            Object = Objects.get(0);
        }
        return Object;
    }

    /**
     * The method for retrieving all the entities
     * 
     * @param entityClass
     *            Class of JPA Entity on which this operation to be done.
     * @return list of JPA entities fetched from DB.
     * 
     * @throws PersistenceException
     *             Parent Exception in JPA. Thrown when there are any DB Errors while persisting.
     */
    public <T> List<T> getAllEntityItems(final Class<T> entityClass) throws PersistenceException {
        //logger.debug("Getting all entities {}", entityClass);

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        final Root<T> entity = criteriaQuery.from(entityClass);

        criteriaQuery.select(entity);
        final TypedQuery<T> query = entityManager.createQuery(criteriaQuery);

        return query.getResultList();
    }

    /**
     * The method for updating an entity
     * 
     * @param entity
     *            JPA Entity with updated values.
     * @return Updated JPA Entity
     * @throws PersistenceException
     *             Parent Exception in JPA. Thrown when there are any DB Errors while persisting.
     */
    public <T> T updateEntity(final T entity) throws PersistenceException {
        //logger.debug("Updating entity {}", entity.getClass().getSimpleName());
        final T createdEntity = entityManager.merge(entity);
        entityManager.flush();
        return createdEntity;
    }

    /**
     * The method for deleting an entity
     * 
     * @param entity
     *            JPA Entity record that is to be deleted from DB.
     * @throws PersistenceException
     *             Parent Exception in JPA. Thrown when there are any DB Errors while persisting.
     */
    public <T> void deleteEntity(final T entity) throws PersistenceException {
        //logger.debug("Deleting Entity", entity.getClass().getSimpleName());
        entityManager.remove(entity);
        entityManager.flush();
    }

    /**
     * The method for finding the entity using WHERE Condition with multiple attributes.
     * 
     * @param entityClass
     *            Class of JPA Entity on which this operation to be done.
     * @param input
     *            {@link java.util.Map} containing attribute and its value that are to be used in WHERE condition in query.
     * @return JPA Entity fetched from DB.
     * @throws PersistenceException
     *             Parent Exception in JPA. Thrown when there are any DB Errors while persisting.
     */
    public <T> T findEntityWhere(final Class<T> entityClass, final Map<String, Object> input) throws PersistenceException {
        //logger.debug("Finding entity {} by inputParams {}", entityClass, input);

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);

        final Root<T> entity = criteriaQuery.from(entityClass);

        final List<Predicate> predicatesList = new ArrayList<Predicate>();
        for (final String key : input.keySet()) {
            final Expression<String> fieldExpression = entity.get(key);
            Predicate predicate = null;
            if (input.get(key) instanceof String) {
                predicate = criteriaBuilder.equal(criteriaBuilder.lower(fieldExpression), input.get(key).toString().toLowerCase());
            } else {
                predicate = criteriaBuilder.equal(fieldExpression, input.get(key));
            }
            predicatesList.add(predicate);
        }

        if (predicatesList.size() > 1) {
            criteriaQuery.where(criteriaBuilder.and(predicatesList.toArray(new Predicate[predicatesList.size()])));
        } else {
            criteriaQuery.where(predicatesList.toArray(new Predicate[predicatesList.size()]));
        }
        final TypedQuery<T> query = entityManager.createQuery(criteriaQuery);

        T Object = null;
        final List<T> Objects = query.getResultList();
        if (Objects.size() != INITIAL_INDEX) {
            Object = Objects.get(INITIAL_INDEX);
        }
        return Object;
    }

    /**
     * Find entities by attributes
     * 
     * @param String
     *            Query for getting the of the entity to be retrieved.
     * @param attributes
     *            Attributes map containing entity property names and values.
     * @return list of entities which matches the attributes.
     */
    public <T> List<T> findEntitiesByAttributes(final String strQuery, final Map<String, Object> attributes) throws PersistenceException {
        final Query query = entityManager.createQuery(strQuery);
        for (final String s : attributes.keySet()) {
            final Object object = attributes.get(s);
            if (object != null) {
                query.setParameter(s, object);
            }
        }
        final List<T> objects = query.getResultList();
        return objects;
    }

    /**
     * The method for finding the entity using IN Condition with multiple attributes.
     * 
     * @param entityClass
     *            Class of JPA Entity on which this operation to be done.
     * @param input
     *            {@link java.util.Map} containing attribute and its value that are to be used in WHERE condition in query.
     * @return list of JPA Entities fetched from DB.
     * @throws PersistenceException
     *             Parent Exception in JPA. Thrown when there are any DB Errors while persisting.
     */
    public <T> List<T> findEntitiesWhere(final Class<T> entityClass, final Map<String, Object> input) throws PersistenceException {
        //logger.debug("Finding entity {} by inputParams {}", entityClass, input);

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);

        final Root<T> entity = criteriaQuery.from(entityClass);

        final List<Predicate> predicatesList = new ArrayList<Predicate>();
        for (final String key : input.keySet()) {
            final Expression<String> fieldExpression = entity.get(key);
            Predicate predicate = null;
            if (input.get(key) instanceof String) {
                predicate = criteriaBuilder.equal(criteriaBuilder.lower(fieldExpression), input.get(key).toString().toLowerCase());
            } else {
                predicate = criteriaBuilder.equal(fieldExpression, input.get(key));
            }
            predicatesList.add(predicate);
        }

        if (predicatesList.size() > 1) {
            criteriaQuery.where(criteriaBuilder.and(predicatesList.toArray(new Predicate[predicatesList.size()])));
        } else {
            criteriaQuery.where(predicatesList.toArray(new Predicate[predicatesList.size()]));
        }
        final TypedQuery<T> query = entityManager.createQuery(criteriaQuery);

        final List<T> Objects = query.getResultList();

        return Objects;
    }

    /**
     * The method for finding the entity using equals Condition with attributes.
     * 
     * @param entityClass
     *            Class of JPA Entity on which this operation to be done.
     * @param input
     *            {@link java.util.Map} containing attribute and its value that are to be used in WHERE condition in query.
     * @return list of entities which matches the attributes.
     * @throws PersistenceException
     */
    public <T> List<T> findEntitiesListWhere(final Class<T> entityClass, final Map<String, Object> input) throws PersistenceException {
        //logger.debug("Finding entity {} by inputParams {}", entityClass, input);

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);

        final Root<T> entity = criteriaQuery.from(entityClass);

        final List<Predicate> predicatesList = new ArrayList<Predicate>();
        for (final String key : input.keySet()) {
            Expression<String> fieldExpression;
            if (key.contains(".")) {
                fieldExpression = getExpressionFromString(key, entity);
            } else {
                fieldExpression = entity.get(key);
            }
            final Predicate predicate = criteriaBuilder.equal(fieldExpression, input.get(key));
            predicatesList.add(predicate);
        }

        if (predicatesList.size() > 1) {
            criteriaQuery.where(criteriaBuilder.and(predicatesList.toArray(new Predicate[predicatesList.size()])));
        } else {
            criteriaQuery.where(predicatesList.toArray(new Predicate[predicatesList.size()]));
        }
        final TypedQuery<T> query = entityManager.createQuery(criteriaQuery);

        final List<T> Objects = query.getResultList();

        return Objects;
    }

    private <T> Expression<String> getExpressionFromString(final String fieldPath, final Root<T> entity) {
        final StringTokenizer stringTokenizer = new StringTokenizer(fieldPath, PATH_DELIMITER);
        Path<T> path = entity.get(stringTokenizer.nextElement().toString());
        Expression<String> expression = null;
        while (stringTokenizer.hasMoreElements()) {
            final String element = stringTokenizer.nextElement().toString();
            if (stringTokenizer.hasMoreElements()) {
                path = path.get(element);
            } else {
                expression = path.get(element);
            }
        }
        return expression;
    }

    /**
     * The method for finding the entity using IN Condition with multiple attributes.
     * 
     * @param entityClass
     *            Class of JPA Entity on which this operation to be done.
     * @param input
     *            {@link java.util.Map} containing attribute and its value that are to be used in IN condition in query.
     * @param fieldPath
     *            Path of Field name in JPA Entity separated by '.'
     * @return {@link java.util.List} of JPA Entities fetched from DB.
     * @throws PersistenceException
     *             Parent Exception in JPA. Thrown when there are any DB Errors while persisting.
     */
    public <T> List<T> findEntityIN(final Class<T> entityClass, final List<String> inputParams, final String fieldPath) throws PersistenceException {
        //logger.debug("Finding entity {} by inputParams {}", entityClass, inputParams);

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        final Root<T> entity = criteriaQuery.from(entityClass);
        final Path<T> path = getPathFromString(fieldPath, entity);
        criteriaQuery.where(path.in(inputParams));

        final TypedQuery<T> query = entityManager.createQuery(criteriaQuery);

        return query.getResultList();
    }

    /**
     * Find entities by attributes
     * 
     * @param entityClass
     *            Class of the entity to be retrieved.
     * @param attributes
     *            Attributes map containing entity property names and values.
     * @return list of entities which matches the attributes.
     */

    public <T> List<T> findEntitiesByAttributes(final Class<T> entityClass, final Map<String, Object> attributes) throws PersistenceException {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<T> cq = cb.createQuery(entityClass);
        final Root<T> entity = cq.from(entityClass);

        final List<Predicate> predicates = new ArrayList<Predicate>();
        for (final String s : attributes.keySet()) {

            final Object object = attributes.get(s);
            if (entity.get(s) != null && attributes.get(s) != null) {
                if (object instanceof String || object instanceof Boolean || object instanceof Integer) {
                    predicates.add(cb.equal((Expression) entity.get(s), attributes.get(s)));
                } else if (object instanceof List) {
                    predicates.add(entity.get(s).in(object));
                }
            }
        }
        cq.where(predicates.toArray(new Predicate[] {}));
        final TypedQuery<T> q = entityManager.createQuery(cq);
        final List<T> results = q.getResultList();
        return results;
    }

    private <T> Path<T> getPathFromString(final String fieldPath, final Root<T> entity) {
        final StringTokenizer stringTokenizer = new StringTokenizer(fieldPath, PATH_DELIMITER);
        Path<T> path = entity.get(stringTokenizer.nextElement().toString());
        while (stringTokenizer.hasMoreElements()) {
            path = path.get(stringTokenizer.nextElement().toString());
        }
        return path;
    }

    /**
     * Find entities by attributes
     * 
     * @param entityClass
     *            Class of the entity list
     * @param String
     *            Query for getting the of the entity to be retrieved.
     * @param attributes
     *            Attributes map containing entity property names and values.
     * @return list of entities which matches the attributes.
     */
    public <T> List<T> findEntitiesByAttributes(final Class<T> entityClass, final String strQuery, final Map<String, Object> attributes) {
        final Query query = entityManager.createQuery(strQuery);
        for (final String s : attributes.keySet()) {
            final Object object = attributes.get(s);
            if (object != null) {
                query.setParameter(s, object);
            }
        }
        final List<T> objects = query.getResultList();
        return objects;
    }
}
