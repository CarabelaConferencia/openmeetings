/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.data.user.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.OpenJPAQuery;
import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.IDataProviderDao;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.basic.dao.OmTimeZoneDao;
import org.apache.openmeetings.persistence.beans.adresses.Adresses;
import org.apache.openmeetings.persistence.beans.user.Users;
import org.apache.openmeetings.utils.DaoHelper;
import org.apache.openmeetings.utils.crypt.ManageCryptStyle;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * CRUD operations for {@link Users}
 * 
 * @author swagner, solomax
 * 
 */
@Transactional
public class UsersDao implements IDataProviderDao<Users> {

	private static final Logger log = Red5LoggerFactory.getLogger(
			UsersDao.class, OpenmeetingsVariables.webAppRootKey);

	public final static String[] searchFields = {"lastname", "firstname", "login", "adresses.email"};

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ManageCryptStyle cryptManager;
	@Autowired
	private ConfigurationDao configDao;
	@Autowired
	private OmTimeZoneDao omTimeZoneDaoImpl;
	@Autowired
	private StateDao stateDaoImpl;

	/**
	 * Get a new instance of the {@link Users} entity, with all default values
	 * set
	 * 
	 * @param currentUser
	 *            the timezone of the current user is copied to the new default
	 *            one (if the current user has one)
	 * @return
	 */
	public Users getNewUserInstance(Users currentUser) {
		Users user = new Users();
		user.setSalutations_id(1L); // TODO: Fix default selection to be
									// configurable
		user.setLevel_id(1L);
		user.setLanguage_id(configDao.getConfValue(
				"default_lang_id", Long.class, "1"));
		user.setOmTimeZone(omTimeZoneDaoImpl.getDefaultOmTimeZone(currentUser));
		user.setForceTimeZoneCheck(false);
		user.setSendSMS(false);
		user.setAge(new Date());
		Adresses adresses = new Adresses();
		adresses.setStates(stateDaoImpl.getStateById(1L));
		user.setAdresses(adresses);
		user.setStatus(1);
		user.setShowContactData(false);
		user.setShowContactDataToContacts(false);

		return user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.openmeetings.data.OmDAO#get(int, int)
	 */
	public List<Users> get(int first, int count) {
		TypedQuery<Users> q = em.createNamedQuery("getNondeletedUsers", Users.class);
		q.setFirstResult(first);
		q.setMaxResults(count);
		return q.getResultList();
	}

	public List<Users> get(String search, int start, int count, String sort) {
		TypedQuery<Users> q = em.createQuery(DaoHelper.getSearchQuery("Users", "u", search, true, false, sort, searchFields), Users.class);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.openmeetings.data.OmDAO#count()
	 */
	public long count() {
		// get all users
		TypedQuery<Long> q = em.createNamedQuery("countNondeletedUsers", Long.class);
		return q.getSingleResult();
	}

	public long count(String search) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("Users", "u", search, true, true, null, searchFields), Long.class);
		return q.getSingleResult();
	}
	
	public List<Users> get(String search) {
		TypedQuery<Users> q = em.createQuery(DaoHelper.getSearchQuery("Users", "u", search, true, false, null, searchFields), Users.class);
		return q.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.data.OmDAO#update(org.apache.openmeetings.persistence
	 * .beans.OmEntity, long)
	 */
	public Users update(Users u, long userId) {
		if (u.getUser_id() == null) {
			u.setStarttime(new Date());
			em.persist(u);
		} else {
			u.setUpdatetime(new Date());
			u =	em.merge(u);
		}
		return u;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.data.OmDAO#delete(org.apache.openmeetings.persistence
	 * .beans.OmEntity, long)
	 */
	public void delete(Users u, long userId) {
		deleteUserID(u.getUser_id());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.openmeetings.data.OmDAO#get(long)
	 */
	public Users get(long user_id) {
		if (user_id > 0) {
			try {
				TypedQuery<Users> query = em.createQuery(
						"select c from Users as c where c.user_id = :user_id",
						Users.class);
				query.setParameter("user_id", user_id);

				Users users = null;
				try {
					users = query.getSingleResult();
				} catch (NoResultException ex) {
				}
				return users;
			} catch (Exception ex2) {
				log.error("getUser", ex2);
			}
		} else {
			log.info("[getUser] " + "Info: No USER_ID given");
		}
		return null;
	}

	public Long deleteUserID(long userId) {
		try {
			if (userId != 0) {
				Users us = get(userId);
				us.setDeleted(true);
				us.setUpdatetime(new Date());
				us.setSipUser(null);
				Adresses adr = us.getAdresses();
				if (adr != null) {
					adr.setDeleted(true);
				}

				if (us.getUser_id() == null) {
					em.persist(us);
				} else {
					if (!em.contains(us)) {
						em.merge(us);
					}
				}
				return us.getUser_id();
			}
		} catch (Exception ex2) {
			log.error("[deleteUserID]", ex2);
		}
		return null;
	}

	public List<Users> getAllUsers() {
		try {
			TypedQuery<Users> q = em.createNamedQuery("getNondeletedUsers", Users.class);
			return q.getResultList();
		} catch (Exception ex2) {
			log.error("[getAllUsers] ", ex2);
		}
		return null;
	}

	public List<Users> getAllUsersDeleted() {
		try {
			TypedQuery<Users> q = em.createNamedQuery("getAllUsers",
					Users.class);
			@SuppressWarnings("unchecked")
			OpenJPAQuery<Users> kq = OpenJPAPersistence.cast(q);
			kq.getFetchPlan().addFetchGroup("backupexport");
			return kq.getResultList();
		} catch (Exception ex2) {
			log.error("[getAllUsersDeleted] ", ex2);
		}
		return null;
	}

	/**
	 * check for duplicates
	 * 
	 * @param DataValue
	 * @return
	 */
	public boolean checkUserLogin(String DataValue) {
		try {
			TypedQuery<Users> query = em
					.createQuery(
							"select c from Users as c where c.login = :DataValue AND c.deleted <> :deleted",
							Users.class);
			query.setParameter("DataValue", DataValue);
			query.setParameter("deleted", true);
			int count = query.getResultList().size();

			if (count != 0) {
				return false;
			}
		} catch (Exception ex2) {
			log.error("[checkUserData]", ex2);
		}
		return true;
	}

	public Users getUserByName(String login) {
		try {
			String hql = "SELECT u FROM Users as u "
					+ " where u.login = :login" + " AND u.deleted <> :deleted";
			TypedQuery<Users> query = em.createQuery(hql, Users.class);
			query.setParameter("login", login);
			query.setParameter("deleted", true);
			Users us = null;
			try {
				us = query.getSingleResult();
			} catch (NoResultException ex) {
			}
			return us;
		} catch (Exception e) {
			log.error("[getUserByAdressesId]", e);
		}
		return null;
	}

	public Users getUserByEmail(String email) {
		try {
			String hql = "SELECT u FROM Users as u "
					+ " where u.adresses.email = :email"
					+ " AND u.deleted <> :deleted";
			TypedQuery<Users> query = em.createQuery(hql, Users.class);
			query.setParameter("email", email);
			query.setParameter("deleted", true);
			Users us = null;
			try {
				us = query.getSingleResult();
			} catch (NoResultException ex) {
			}
			return us;
		} catch (Exception e) {
			log.error("[getUserByAdressesId]", e);
		}
		return null;
	}

	public Object getUserByHash(String hash) {
		try {
			if (hash.length() == 0)
				return new Long(-5);
			String hql = "SELECT u FROM Users as u "
					+ " where u.resethash = :resethash"
					+ " AND u.deleted <> :deleted";
			TypedQuery<Users> query = em.createQuery(hql, Users.class);
			query.setParameter("resethash", hash);
			query.setParameter("deleted", true);
			Users us = null;
			try {
				us = query.getSingleResult();
			} catch (NoResultException ex) {
			}
			if (us != null) {
				return us;
			} else {
				return new Long(-5);
			}
		} catch (Exception e) {
			log.error("[getUserByAdressesId]", e);
		}
		return new Long(-1);
	}

	public Object resetPassByHash(String hash, String pass) {
		try {
			Object u = this.getUserByHash(hash);
			if (u instanceof Users) {
				Users us = (Users) u;
				us.updatePassword(cryptManager, configDao, pass);
				us.setResethash("");
				update(us, 1L);
				return new Long(-8);
			} else {
				return u;
			}
		} catch (Exception e) {
			log.error("[getUserByAdressesId]", e);
		}
		return new Long(-1);
	}

	/**
	 * @param search
	 * @return
	 */
	public Long selectMaxFromUsersWithSearch(String search) {
		try {

			String hql = "select count(c.user_id) from Users c "
					+ "where c.deleted = false " + "AND ("
					+ "lower(c.login) LIKE :search "
					+ "OR lower(c.firstname) LIKE :search "
					+ "OR lower(c.lastname) LIKE :search " + ")";

			// get all users
			TypedQuery<Long> query = em.createQuery(hql, Long.class);
			query.setParameter("search", StringUtils.lowerCase(search));
			List<Long> ll = query.getResultList();
			log.info("selectMaxFromUsers" + ll.get(0));
			return ll.get(0);
		} catch (Exception ex2) {
			log.error("[selectMaxFromUsers] ", ex2);
		}
		return null;
	}

	/**
	 * Returns true if the password is correct
	 * 
	 * @param userId
	 * @param password
	 * @return
	 */
	public boolean verifyPassword(Long userId, String password) {
		TypedQuery<Long> query = em.createNamedQuery("checkPassword",
				Long.class);
		query.setParameter("userId", userId);
		query.setParameter("password", cryptManager.getInstanceOfCrypt()
				.createPassPhrase(password));
		return query.getResultList().get(0) == 1;

	}
}
