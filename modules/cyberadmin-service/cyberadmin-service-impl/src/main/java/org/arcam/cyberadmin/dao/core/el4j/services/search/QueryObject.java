/*
 * QueryObject
 * 
 * Project: Cyber Admin
 * 
  * 
 * Copyright (c) 2013, ARCAM - Association de la Region Cossonay-Aubonne-Morges
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification,are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * Neither the name of the ARCAM - Association de la Region 
 * Cossonay-Aubonne-Morges nor the names of its contributors may be used to 
 * endorse or promote products derived from this software without specific 
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
 package org.arcam.cyberadmin.dao.core.el4j.services.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.arcam.cyberadmin.dao.core.el4j.services.persistence.generic.dao.GenericDao;
import org.arcam.cyberadmin.dao.core.el4j.services.persistence.hibernate.criteria.CriteriaTransformer;
import org.arcam.cyberadmin.dao.core.el4j.services.search.criterias.AndCriteria;
import org.arcam.cyberadmin.dao.core.el4j.services.search.criterias.Criteria;
import org.arcam.cyberadmin.dao.core.el4j.services.search.criterias.CriteriaHelper;
import org.arcam.cyberadmin.dao.core.el4j.services.search.criterias.Order;
import org.arcam.cyberadmin.dao.core.el4j.util.codingsupport.Reject;

/**
 * Object to holds criterias to execute queries. A query object can be specified
 * for exactly one class. A query object is an AND-joined set of Criteria
 * objects.  <a>
 *
 * Features: <br>
 *  <ul>
 *   <li> OR-criterias, NOT-criterias, AND-criterias refer to
 *     {@link CriteriaHelper} for some convenience support.
 *   <li> paging support (see methods {@link setFirstResult}
 *         {@link setMaxResults} and {@link setDefaultMaxResults}
 *   <li> ordering support (often required when doing paging)
 *  </ul>
 *
 * Example on how to use this (with paging, ordering and criteria): <br> <br>
 *
 *  <code>
 *   <pre>
 *       // code fragments taken from HibernateKeywordDaoTest
 *
 *       query = new QueryObject();
 *
 *       // criteria is deliberately a bit noisy
 *       query.addCriteria(
 *           or(and(not(new ComparisonCriteria("name","Ghost","!=","String")),
 *                  (or(not(like("name", "%host%")),
 *                      like("name", "%host%"))))));
 *
 *      query.addOrder(Order.desc("name"));
 *      query.setMaxResults(2);
 *      query.setFirstResult(4);
 *
 *      // dao is typically a generic dao implementation
 *      list = dao.findByQuery(query);
 *    </pre>
 *  </code>
 * <a>
 * Sample uses in EL4J: {@link GenericDao}, {@link CriteriaTransformer} <br> <br>
 *
 * @author Martin Zeltner (MZE)
 * @author Philipp Oser (POS)
 */
public class QueryObject implements Serializable {
	/**
	 * The bean class the query object is for.
	 */
	private Class<?> m_beanClass;
	
	/**
	 * The criterias for this query. (They are logically connected with
	 *  AND).
	 */
	private AndCriteria m_criterias = new AndCriteria();
	
	/**
	 * Specifies a general query object.
	 */
	public QueryObject() {
		this(null);
	}

	/**
	 * Specifies the query object for a specific class.
	 *
	 * @param beanClass Is the bean class this query object is made for.
	 */
	public QueryObject(Class<?> beanClass) {
		m_beanClass = beanClass;
	}

	/**
	 * @return Returns the bean class this query object is made for.
	 */
	public Class<?> getBeanClass() {
		return m_beanClass;
	}
	
	/**
	 * Adds the given criteria. The criterias are combined via
	 *  "AND" (it's a logical conjunction of Criterias).
	 *  This method can be used with one or n Criteria(s).
	 *
	 * @param criteria Is the criteria to add.
	 */
	public void addCriteria(Criteria... criteria) {
		Reject.ifNull(criteria);
		if (criteria != null) {
			for (Criteria c : criteria) {
				m_criterias.add(c);
			}
		}
	}
	
	/**
	 * Adds the given criterias.
	 *
	 * @param criterias Are the criterias to add. The criterias are combined via
	 *  "AND" (it's a logical conjunction of Criterias).
	 * @deprecated please use the more versatile {@link addCriteria} method
	 */
	public void addCriterias(Criteria... criterias) {
		Reject.ifNull(criterias);
		for (int i = 0; i < criterias.length; i++) {
			Criteria criteria = criterias[i];
			m_criterias.add(criteria);
		}
	}
	
	/**
	 * @return Returns a list of criterias (all criterias must be valid
	 *   for this query object (they are combined with AND)).
	 */
	public List<Criteria> getCriteriaList() {
		return m_criterias.getCriterias();
	}
	
	/**
	 * @return Returns an array of criterias. (all criterias must be true
	 *   for this query (they are combined with AND)).
	 */
	public Criteria[] getCriterias() {
		List<Criteria> crits = getCriteriaList();
		return crits.toArray(new Criteria[0]);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(){
		return "QueryObject [Type: "+getBeanClass()+
			" Query: "+m_criterias.getSqlWhereCondition()+"]";
	}
	
	public AndCriteria getAndCriterias () {
		return m_criterias;
	}
	
	///////// paging support ////////////////////
	
	/**
	 *  Constant (=-1) to use for property firstResult.
	 * NO_CONSTRAINT means we do not constrain anything
	 */
	public static final int NO_CONSTRAINT = -1;
	
	/**
	 * Default can be updated via static setter
	 *  {@link setDefaultMaxResults}
	 */
	static int s_defaultMaxResults = 100;
	
	protected int m_firstResult;
	
		
	/**
	 * Default value is s_defaultMaxResults
	 */
	protected int m_maxResults = s_defaultMaxResults;

	List<Order> m_orderConstraints = new ArrayList<Order>();
	
	/**
	 * What is the id of the first result we want to get back?
	 * By default there is no constraint on the first result.
	 * The counting starts at 0 (i.e. the first result is 0)!
	 * @param firstResult
	 */
	public void setFirstResult(int firstResult) {
		m_firstResult = firstResult;
	}
	 
	/**
	 * How many results do we want to get back at most?
	 *  (The default can be set via the {@link setDefaultMaxResults}
	 *   method). It defaults to 100. -1 means there is no constraint.
	 * @param maxResults
	 */
	public void setMaxResults(int maxResults) {
		m_maxResults = maxResults;
	}

	/**
	 * @see setFirstResult
	 * @return
	 */
	public int getFirstResult() {
		return m_firstResult;
	}

	/**
	 * @see setMaxResults
	 * @return
	 */
	public int getMaxResults() {
		return m_maxResults;
	}

	/**
	 * @see setDefaultMaxResults
	 * @return
	 */
	public static int getDefaultMaxResults() {
		return s_defaultMaxResults;
	}

	/**
	 * How many results shall we return by default?
	 *   Defaults to 100. -1 stands for no constraint.
	 * @param defaultMaxResults
	 */
	public static void setDefaultMaxResults(int defaultMaxResults) {
		s_defaultMaxResults = defaultMaxResults;
	}
 
	/**
	 * Add an ordering constraint (particularly useful
	 *  when doing paging) <br> <br>
	 *   Example usage: <br>
	 *    <code> query.addOrder(Order.desc("name")); </code>
	 * @param order
	 */
	public void addOrder(Order order){
		m_orderConstraints.add(order);
	}

	/**
	 * Get the list of all defined ordering constraints.
	 * @return
	 */
	public List<Order> getOrderConstraints() {
		return m_orderConstraints;
	}
	
}
