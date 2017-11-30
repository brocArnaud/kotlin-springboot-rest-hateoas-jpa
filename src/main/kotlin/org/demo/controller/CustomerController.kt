package org.demo.controller

import org.demo.controller.assembler.CustomerResourceAssembler
import org.demo.controller.criteria.CustomerCriteria
import org.demo.controller.resources.CustomerResource
import org.demo.entity.Customer
import org.demo.service.CustomerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedResources
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = "/customers", produces = arrayOf("application/hal+json"))
class CustomerController(val customerService: CustomerService) {

	@Autowired
	lateinit var pageAssember: PagedResourcesAssembler<Customer>

	private var customerAssembler: CustomerResourceAssembler = CustomerResourceAssembler()

	@GetMapping("/init")
	fun init() {
		return customerService.init()
	}


//	@GetMapping
//	fun search(): HttpEntity<MutableList<CustomerResource>> {
//		var customers = customerService.search()
//		var customersDto: MutableList<CustomerResource> = mutableListOf<CustomerResource>()
//		for (item in customers) {
//			var customerDto = CustomerResource(item.firstName, item.lastName, item.id);
//			customerDto.add(linkTo(CustomerController::class.java).slash(item.id).withSelfRel())
//			customersDto.add(customerDto)
//		}
//		return ResponseEntity<MutableList<CustomerResource>>(customersDto, HttpStatus.OK)
//	}

	@GetMapping
			//	fun search(pageable: Pageable): HttpEntity<PagedResources<CustomerResource>> {
	fun search(pageable: Pageable, criteria: CustomerCriteria): HttpEntity<PagedResources<CustomerResource>> {
		var result: Page<Customer> = customerService.search(pageable, criteria)
		return ResponseEntity<PagedResources<CustomerResource>>(pageAssember.toResource(result, customerAssembler), HttpStatus.OK)
	}

	@GetMapping("/{id}")
	fun get(@PathVariable("id") id: Long): HttpEntity<CustomerResource> {
		var customer = customerService.get(id)
		println("===========> customer.firstName : " + customer.firstName)
		println("===========> customer.lastName : " + customer.lastName)
		println("===========> customer.id : " + customer.id)
		var customerDto = CustomerResource(customer.firstName, customer.lastName, customer.id);
		println("===========> customerDto.firstName : " + customerDto.firstName)
		println("===========> customerDto.lastName : " + customerDto.lastName)
		println("===========> customerDto.id : " + customerDto.id)
		customerDto.add(linkTo(methodOn(CustomerController::class.java).get(id)).withSelfRel())
		return ResponseEntity<CustomerResource>(customerDto, HttpStatus.OK)
	}
}