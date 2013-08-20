/*
 * CriteriaTransformer
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
 package org.arcam.cyberadmin.dao.core.el4j.services.persistence.hibernate.criteria;


import java.util.Iterator;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.arcam.cyberadmin.dao.core.el4j.services.search.QueryObject;
import org.arcam.cyberadmin.dao.core.el4j.services.search.criterias.AbstractCriteria;
import org.arcam.cyberadmin.dao.core.el4j.services.search.criterias.AndCriteria;
import org.arcam.cyberadmin.dao.core.el4j.services.search.criterias.ComparisonCriteria;
import org.arcam.cyberadmin.dao.core.el4j.services.search.criterias.Criteria;
import org.arcam.cyberadmin.dao.core.el4j.services.search.criterias.LikeCriteria;
import org.arcam.cyberadmin.dao.core.el4j.services.search.criterias.NotCriteria;
import org.arcam.cyberadmin.dao.core.el4j.services.search.criterias.OrCriteria;
import org.arcam.cyberadmin.dao.core.el4j.services.search.criterias.Order;

/**
 *
 * This class transforms the EL4J Criteria of a given <code>QueryObject</code>
 * into the corresponding Hibernate DetachedCriteria.
 *
 * @author Alex Mathey (AMA)
 * @author Philipp Oser (POS)
 */
public class CriteriaTransformer {

	private static Logger s_logger = LoggerFactory.getLogger(CriteriaTransformer.class);
	
	/**
	 * Hide default constructor.
	 */
	protected CriteriaTransformer() { };
	
	/**
	 * Transforms EL4J Criteria of the given <code>QueryObject</code> into the
	 * corresponding Hibernate DetachedCriteria.
	 *
	 * @param query
	 *            the query object whose criteria will be transformed
	 * @param domainObjectClass
	 *            the class of the domain object for which the Hibernate
	 *            criteria will be generated
	 * @return the Hibernate criteria corresponding to the
	 *         <code>QueryObject</code>'s EL4J criteria.
	 */
	public static DetachedCriteria transform(QueryObject query,
		Class<?> domainObjectClass) {
		
		// Hibernate criteria for the domain object.
		DetachedCriteria hibernateCriteria
			= DetachedCriteria.forClass(domainObjectClass);
		
		// List of EL4J criteria.
		List<Criteria> el4jCriteriaList = query.getCriteriaList();
		
		// Conversion from EL4J criteria to Hibernate criteria.
		Iterator<Criteria> it = el4jCriteriaList.iterator();
		
		while (it.hasNext()) {
			Criteria currentEl4jCriteria = (Criteria) it.next();
			
			Criterion hibernateCriterion =
				el4jCriteria2HibernateCriterion(currentEl4jCriteria);
			if (hibernateCriterion != null) {
				hibernateCriteria.add(hibernateCriterion);
			}
		}
		
		addOrderConstraints(hibernateCriteria, query);
		
		return hibernateCriteria;
	}

	protected static void addOrderConstraints (DetachedCriteria hibernateCriteria,
		QueryObject query) {
		
		List<Order> orderConstraints = query.getOrderConstraints();
		for (Order o : orderConstraints){
			if (o.isAscending()) {
				hibernateCriteria.addOrder(org.hibernate.criterion.Order.asc(o.getPropertyName()));
			} else {
				hibernateCriteria.addOrder(org.hibernate.criterion.Order.desc(o.getPropertyName()));
			}
		}
		
	}
	
	
	/**
	 * Converts EL4J Criteria to Hibernate Criterion.
	 * @param criteria
	 * @return the converted Criterion
	 */
	protected static Criterion el4jCriteria2HibernateCriterion(Criteria criteria) {
		Criterion criterion = null;
		
		if (criteria instanceof OrCriteria) {
			Junction combination = Restrictions.disjunction();
			
			addCriteriaListToJunction(((OrCriteria) criteria).getCriterias(), combination);
			criterion = combination;
		} else if (criteria instanceof AndCriteria) {
			Junction combination = Restrictions.conjunction();
			
			addCriteriaListToJunction(((AndCriteria) criteria).getCriterias(), combination);
			criterion = combination;
		} else if (criteria instanceof NotCriteria) {
			Criteria innerCriteria = ((NotCriteria) criteria).getCriteria();
			criterion = Restrictions.not(el4jCriteria2HibernateCriterion(innerCriteria));
		} else if (criteria instanceof AbstractCriteria) {
			AbstractCriteria abstractCrit = (AbstractCriteria) criteria;
			
			String currentCriteriaField = abstractCrit.getField();
			Object currentCriteriaValue = abstractCrit.getValue();

			if (criteria instanceof LikeCriteria) {
				LikeCriteria currentEl4jLikeCriteria = (LikeCriteria) criteria;
				if (currentEl4jLikeCriteria.isCaseSensitive().booleanValue()) {
					criterion = Restrictions.like(currentCriteriaField,
						currentCriteriaValue);
				} else {
					criterion = Restrictions.like(currentCriteriaField,
						currentCriteriaValue).ignoreCase();
				}
			} else if (criteria instanceof ComparisonCriteria) {
				String operator = ((ComparisonCriteria) criteria).getOperator();
				if (operator.equals("=")) {
					criterion = Restrictions.eq(currentCriteriaField,
										currentCriteriaValue);
				} else if (operator.equals("<")) {
					criterion = Restrictions.lt(currentCriteriaField,
						currentCriteriaValue);
				} else if (operator.equals("<=")) {
					criterion = Restrictions.le(currentCriteriaField,
						currentCriteriaValue);
				} else if (operator.equals(">")) {
					criterion = Restrictions.gt(currentCriteriaField,
						currentCriteriaValue);
				} else if (operator.equals(">=")) {
					criterion = Restrictions.ge(currentCriteriaField,
						currentCriteriaValue);
				} else if (operator.equals("!=")) {
					criterion = Restrictions.ne(currentCriteriaField,
						currentCriteriaValue);
				} else {
					s_logger.info(" Operator not handled " + operator);
				}
				
			} else {
				s_logger.info(" Criteria not handled " + criteria);
			}
		} else {
			s_logger.info(" Criteria not handled " + criteria);
		}
		return criterion;
	}

	/**
	 * @param currentEl4jCriteria
	 * @param combination
	 */
	protected static void addCriteriaListToJunction(
		List<Criteria> criterias, Junction combination) {
		for (Criterion c : apply2HibernateCriterion(criterias)) {
			combination.add(c);
		}
	}
	
	/**
	 * Apply operator (from functional programming)
	 * @param criterias must not be null
	 * @return
	 */
	protected static Criterion[] apply2HibernateCriterion(List<Criteria> criterias ){
		Criterion[] result = new Criterion[criterias.size()];
		
		for (int i = 0; i < criterias.size(); i++) {
			result[i] = el4jCriteria2HibernateCriterion(criterias.get(i));
		}
		return result;
	}
	
}
