package org.demo.controller.resources

import org.springframework.hateoas.ResourceSupport

open class CustomerResource(var firstName: String, var lastName: String, var id: Long) : ResourceSupport() 