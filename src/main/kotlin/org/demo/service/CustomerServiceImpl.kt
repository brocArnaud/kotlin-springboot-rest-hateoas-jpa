package org.demo.service

import org.apache.commons.lang3.StringUtils
import org.demo.controller.criteria.CustomerCriteria
import org.demo.entity.Customer
import org.demo.repository.CustomerRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.Optional
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

@Service
class CustomerServiceImpl(val customerRepository: CustomerRepository) : CustomerService {

	override fun get(id: Long): Optional<Customer> {
		return customerRepository.findById(id)
	}

	override fun search(pageable: Pageable, criteria: CustomerCriteria): Page<Customer> {
		return customerRepository.findAll(CustomerSpecification(criteria), pageable)
	}

	override fun delete(id: Long): Boolean {
		if (customerRepository.findById(id) != null) {
			customerRepository.deleteById(id)
			return true;
		}
		return false;
	}

	override fun create(customer: Customer): Customer {
		return customerRepository.save(customer)
	}

	override fun save(customer: Customer): Boolean {
		var pk: Long = customer.id;
		if (customerRepository.findById(pk) != null) {
			customerRepository.save(customer);
			return true;
		}
		return false;
	}

	override fun exist(customer: Customer): Boolean {
		return customerRepository.existsById(customer.id)
	}

	override fun init() {
		customerRepository.save(Customer("AAA", "lil1"))
		customerRepository.save(Customer("aabb", "lil2"))
		customerRepository.save(Customer("ccD", "lil3"))
		customerRepository.save(Customer("ccD", "lil4"))
		customerRepository.save(Customer("ccD", "lil5"))
		customerRepository.save(Customer("ccD", "lil6"))
		customerRepository.save(Customer("ccD", "lil7"))
		customerRepository.save(Customer("ccD", "lil8"))
		customerRepository.save(Customer("ccD", "lil9"))
		customerRepository.save(Customer("ccD", "lil10"))
		customerRepository.save(Customer("ccD", "lil11"))
		customerRepository.save(Customer("ccD", "lil12"))
	}

	class CustomerSpecification(val criteria: CustomerCriteria) : Specification<Customer> {

		override fun toPredicate(root: Root<Customer>, query: CriteriaQuery<*>, builder: CriteriaBuilder): Predicate {
			var predicates: MutableList<Predicate> = mutableListOf<Predicate>()
			if (StringUtils.isNotBlank(criteria.firstName)) predicates.add(builder.like(builder.lower(root.get("firstName")), criteria.firstName?.toLowerCase() + "%"));
			if (StringUtils.isNotBlank(criteria.lastName)) predicates.add(builder.like(builder.lower(root.get("lastName")), criteria.lastName?.toLowerCase() + "%"));
			return andTogether(predicates, builder)
		}

		private fun andTogether(predicates: MutableList<Predicate>, builder: CriteriaBuilder): Predicate {
			return builder.and(*predicates.toTypedArray());
		}
	}
}