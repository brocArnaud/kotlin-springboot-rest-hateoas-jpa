package org.demo.service

import org.demo.entity.Customer
import org.demo.repository.CustomerRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root
import org.apache.commons.lang3.StringUtils
import org.demo.controller.resources.CustomerResource

@Service
class CustomerServiceImpl(val customerRepository: CustomerRepository) : CustomerService {

	override fun search(): List<Customer> {
		return customerRepository.findAll()
	}

	override fun get(id: Long): Customer {
		return customerRepository.getOne(id)
	}


	override fun search(pageable: Pageable): Page<Customer> {
//		var spec: Specification<Customer> = CustomerSpecification(CustomerResource())
//		spec,
		var customers: Page<Customer> = customerRepository.findAll( pageable)
		return customers
	}

	override fun delete(id: Long) {
		return customerRepository.deleteById(id)
	}

	override fun create(customer: Customer): Customer {
		return customerRepository.save(customer)
	}

	override fun save(customer: Customer): Customer {
		return customerRepository.save(customer)
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

	class CustomerSpecification(val criteria: CustomerResource) : Specification<Customer> {

		override fun toPredicate(root: Root<Customer>, query: CriteriaQuery<*>, builder: CriteriaBuilder): Predicate {
			var predicates: MutableList<Predicate> = mutableListOf<Predicate>()
			if (StringUtils.isNotBlank(criteria.firstName)) predicates.add(builder.like(builder.lower(root.get("firstName")), criteria.firstName.toLowerCase() + "%"));

			return andTogether(predicates, builder)
		}

		private fun andTogether(predicates: MutableList<Predicate>, builder: CriteriaBuilder): Predicate {
			return builder.and(*predicates.toTypedArray());
		}
	}
}