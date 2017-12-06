package org.demo.service

import org.demo.controller.criteria.CustomerCriteria
import org.demo.entity.Customer
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.Optional

interface CustomerService {
	fun init()
	fun search(pageable: Pageable, criteria: CustomerCriteria): Page<Customer>
	fun get(id: Long): Optional<Customer>
	fun delete(id: Long): Boolean
	fun create(customer: Customer): Customer
	fun save(customer: Customer): Boolean
	fun exist(customer: Customer): Boolean
}