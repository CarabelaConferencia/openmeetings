package org.apache.openmeetings.data.oauth;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.data.IDataProviderDao;
import org.apache.openmeetings.persistence.beans.user.oauth.OAuthServer;
import org.apache.openmeetings.utils.DaoHelper;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OAuth2Dao implements IDataProviderDao<OAuthServer> {

	public final static String[] searchFields = {"name"};
	@PersistenceContext
	private EntityManager em;
	
	public List<OAuthServer> getEnabledOAuthServers() {
		TypedQuery<OAuthServer> query = em.createNamedQuery("getEnabledOAuthServers", OAuthServer.class);
		return query.getResultList();
	}
	
	public OAuthServer get(long id) {
		TypedQuery<OAuthServer> query = em.createNamedQuery("getOAuthServerById", OAuthServer.class);
		query.setParameter("id", id);
		OAuthServer result = null;
		try {
			result = query.getSingleResult();
		} catch (NoResultException e) {}
		return result;
	}

	public List<OAuthServer> get(int start, int count) {
		TypedQuery<OAuthServer> query = em.createNamedQuery("getAllOAuthServers", OAuthServer.class);
		query.setFirstResult(start);
		query.setMaxResults(count);
		return query.getResultList();
	}

	public List<OAuthServer> get(String search, int start, int count, String order) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("OAuthServer", "s", search, true, false, null, searchFields), Long.class);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return null;
	}

	public long count() {
		TypedQuery<Long> q = em.createNamedQuery("countOAuthServers", Long.class);
		return q.getSingleResult();
	}

	public long count(String search) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("OAuthServer", "s", search, true, true, null, searchFields), Long.class);
		return q.getSingleResult();
	}

	public OAuthServer update(OAuthServer server, Long userId) {
		if (server.getId() == null) {
			em.persist(server);
		} else {
			server = em.merge(server);
		}
		return server;
	}

	public void delete(OAuthServer server, Long userId) {
		server.setDeleted(true);
		update(server, userId);
	}

}
