package org.demo.service

import org.demo.entity.Customer
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.demo.controller.resources.CustomerResource

@Service
interface CustomerService {
	fun init()
	fun search(): List<Customer>
	fun search(pageable: Pageable): Page<Customer>
//	fun search(pageable: Pageable, criteria: CustomerResource): Page<Customer>
	fun get(id: Long): Customer
	fun delete(id: Long)
	fun create(customer: Customer): Customer
	fun save(customer: Customer): Customer
}