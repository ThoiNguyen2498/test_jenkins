package com.cogover.template.server.repository.mysql.base;

import com.cogover.exception.DbException;
import com.cogover.template.server.config.DbConfigLoader;
import com.cogover.template.server.repository.common.DbRepository;
import com.cogover.template.server.repository.common.IdGenerator;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.*;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Map;

public class MySqlRepository<T> implements DbRepository<T> {

    protected final IdGenerator idGenerator;

    @Getter
    @Setter
    protected Class<T> entityClass;

    @Getter
    @Setter
    protected String prefix;

    /**
     * Lấy ở: DbConfigLoader
     */
    @Getter
    protected String dbName;

    public MySqlRepository(String dbName, String prefix, Class<T> entityClass) {
        this.dbName = dbName;
        this.prefix = prefix;
        this.entityClass = entityClass;
        this.idGenerator = new IdGenerator(prefix, this);
    }

    public void insert(String mysqlId, T object) throws DbException {
        SessionFactory sessionFactory = DbConfigLoader.getSessionFactory(mysqlId, dbName);
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(object);
            tx.commit();
        } catch (Exception e) {
            throw new DbException(e.getMessage(), e.getCause());
        }
    }

    public List<T> insertMany(String mysqlId, List<T> list) throws DbException {
        for (T record : list) {
            insert(mysqlId, record);
        }
        return list;
    }

    public T findOne(String mysqlId, String sql, Map<String, Object> params) throws DbException {
        SessionFactory sessionFactory = DbConfigLoader.getSessionFactory(mysqlId, dbName);
        try (Session session = sessionFactory.openSession()) {
            Query<T> query = session.createQuery(sql, entityClass);
            params.keySet().forEach(key -> query.setParameter(key, params.get(key)));
            //dung lenh getSingleResult() se vang exception => ko muon xu ly nhu vay
            List<T> list = query.list();
            if (list.isEmpty()) {
                return null;
            }

            return list.getFirst();
        } catch (Exception e) {
            throw new DbException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Lay theo ID, thuong se can where them workspaceId de bao mat
     */
    public T findOneByWorkspaceAndId(String mysqlId, String workspaceId, String id) throws DbException {
        return findOne(
                mysqlId,
                "from " + entityClass.getName() + " where workspaceId=:workspaceId and id=:id",
                Map.of("workspaceId", workspaceId, "id", id)
        );
    }

    /**
     * De cho ID generator dung; bt ko dung ham nay
     */
    public T findOneById(String mysqlId, String id) throws DbException {
        SessionFactory sessionFactory = DbConfigLoader.getSessionFactory(mysqlId, dbName);
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from " + entityClass.getName() + " where id=:id", entityClass)
                    .setParameter("id", id)
                    .getSingleResultOrNull();
        } catch (Exception e) {
            throw new DbException(e.getMessage(), e.getCause());
        }
    }

    public List<T> findAll(String mysqlId, String sql, Map<String, Object> params) throws DbException {
        return findAll(mysqlId, sql, 10000, params);
    }

    public List<T> findAllByWorkspaceId(String mysqlId, String workspaceId) throws DbException {
        return findAll(
                mysqlId,
                "from " + entityClass.getName() + " where workspaceId=:workspaceId",
                10000,
                Map.of("workspaceId", workspaceId)
        );
    }

    public List<T> findAll(String mysqlId, String sql, int max, Map<String, Object> params) throws DbException {
        SessionFactory sessionFactory = DbConfigLoader.getSessionFactory(mysqlId, dbName);
        try (Session session = sessionFactory.openSession()) {
            Query<T> query = session.createQuery(sql, entityClass);
            params.keySet().forEach(key -> query.setParameter(key, params.get(key)));
            return query.setMaxResults(max).list();
        } catch (Exception e) {
            throw new DbException(e.getMessage(), e.getCause());
        }
    }

    public void update(String mysqlId, T object) throws DbException {
        SessionFactory sessionFactory = DbConfigLoader.getSessionFactory(mysqlId, dbName);
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(object);
            tx.commit();
        } catch (Exception e) {
            throw new DbException(e.getMessage(), e.getCause());
        }
    }

    public int update(String mysqlId, String sql, Map<String, Object> params) throws DbException {
        SessionFactory sessionFactory = DbConfigLoader.getSessionFactory(mysqlId, dbName);
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            Query<?> query = session.createQuery(sql);
            params.keySet().forEach(key -> query.setParameter(key, params.get(key)));
            int count = query.executeUpdate();

            tx.commit();

            return count;
        } catch (Exception e) {
            throw new DbException(e.getMessage(), e.getCause());
        }
    }

    public void delete(String mysqlId, T object) throws DbException {
        SessionFactory sessionFactory = DbConfigLoader.getSessionFactory(mysqlId, dbName);
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(object);
            tx.commit();
        } catch (Exception e) {
            throw new DbException(e.getMessage(), e.getCause());
        }
    }

}
