package org.demo.controller.assembler

import org.demo.controller.CustomerController
import org.demo.controller.resources.CustomerResource
import org.demo.entity.Customer
import org.springframework.hateoas.mvc.ResourceAssemblerSupport
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo

@RestController
@RequestMapping(value = "/customers")
class CustomerResourceAssembler : ResourceAssemblerSupport<Customer, CustomerResource> {
	constructor() : super(CustomerController::class.java, CustomerResource::class.java)

	override fun toResource(customer: Customer): CustomerResource {
		var resource: CustomerResource = CustomerResource(customer.firstName, customer.lastName, customer.id)
		
		resource.add(linkTo(CustomerController::class.java).slash(customer.id).withSelfRel());
		resource.firstName = customer.firstName
		resource.lastName = customer.lastName
		return resource
	}
}