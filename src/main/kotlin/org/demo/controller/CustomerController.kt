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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.hateoas.ExposesResourceFor

@RestController
@ExposesResourceFor(Customer::class)
@RequestMapping(value = "/customers", produces = arrayOf("application/hal+json"))
class CustomerController(val customerService: CustomerService) {

	@Autowired
	lateinit var pageAssember: PagedResourcesAssembler<Customer>

	private var customerAssembler: CustomerResourceAssembler = CustomerResourceAssembler()

	@GetMapping("/init")
	fun init() {
		return customerService.init()
	}

	@GetMapping
	fun search(pageable: Pageable, criteria: CustomerCriteria): ResponseEntity<PagedResources<CustomerResource>> {
		var result: Page<Customer> = customerService.search(pageable, criteria)
		return ResponseEntity<PagedResources<CustomerResource>>(pageAssember.toResource(result, customerAssembler), HttpStatus.OK)
	}

	@GetMapping("/{id}")
	fun get(@PathVariable("id") id: Long): ResponseEntity<CustomerResource> {
		var customer = customerService.get(id)
		if (!customer.isPresent) return ResponseEntity<CustomerResource>(null, HttpStatus.NOT_FOUND)
		var customerDto = customerAssembler.toResource(customer.get())
		return ResponseEntity<CustomerResource>(customerDto, HttpStatus.OK)
	}

	@PostMapping(consumes = arrayOf("application/json"))
	fun create(@RequestBody customer: Customer): ResponseEntity<CustomerResource> {
		if (customerService.exist(customer))
			return ResponseEntity<CustomerResource>(null, HttpStatus.CONFLICT)
		else
			return ResponseEntity<CustomerResource>(customerAssembler.toResource(customerService.create(customer)), HttpStatus.OK);
	}

	@PutMapping("/{id}", consumes = arrayOf("application/json"))
	fun update(@PathVariable("id") id: Long, @RequestBody customer: Customer): ResponseEntity<Void> {
		// force the id (use the id provided by the URL)
		customer.id = id
		if (customerService.save(customer))
			return ResponseEntity<Void>(HttpStatus.OK)
		else
			return ResponseEntity<Void>(HttpStatus.NOT_FOUND)
	}

	@DeleteMapping("/{id}")
	fun delete(@PathVariable("id") id: Long): ResponseEntity<Void> {
		if (customerService.delete(id))
			return ResponseEntity<Void>(HttpStatus.NO_CONTENT)
		else
			return ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}
}