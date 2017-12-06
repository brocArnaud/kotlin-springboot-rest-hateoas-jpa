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
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.hateoas.Link
import org.springframework.core.type.filter.AssignableTypeFilter
import org.springframework.hateoas.ResourceSupport
import org.springframework.context.annotation.ScannedGenericBeanDefinition
import org.springframework.util.MultiValueMap
import java.util.LinkedList
import org.springframework.hateoas.alps.Alps
import org.springframework.hateoas.alps.Alps.AlpsBuilder
import org.demo.controller.resources.AlpsControllerResource
import org.springframework.hateoas.alps.Descriptor
import org.springframework.hateoas.alps.Type
import org.springframework.hateoas.alps.Doc
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.springframework.core.GenericTypeResolver
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

@RestController
@RequestMapping(value = "/profile", produces = arrayOf("application/alps+json"))
class ProfileController() {


	@GetMapping
	fun get(): ResponseEntity<ResourceSupport> {
		// Detect all controller
		println("DANS PROFILE GET BASIC")
		var scanner: ClassPathScanningCandidateComponentProvider = ClassPathScanningCandidateComponentProvider(false);
		// Scan all rest controller in the application
		scanner.addIncludeFilter(AnnotationTypeFilter(RestController::class.java))
		// Exclude profile controller
		scanner.addExcludeFilter(AssignableTypeFilter(ProfileController::class.java))
		val result: MutableList<Link> = arrayListOf()
		result.add(linkTo(ProfileController::class.java).withSelfRel())
		for (beanDefinition in scanner.findCandidateComponents("org.demo.controller")) {
			val scanned: ScannedGenericBeanDefinition = beanDefinition as ScannedGenericBeanDefinition
			System.out.println("=========> classe : " + scanned::class);
			System.out.println("=========> bean class name : " + scanned.getBeanClassName());

			var path: String = ""
			for (item in scanned.getMetadata().getAnnotationTypes()) {
				System.out.println("=========> annotation found : " + item);
				if (item == RequestMapping::class.java.getCanonicalName()) {
					var attrs: MultiValueMap<String, Any> = scanned.getMetadata().getAllAnnotationAttributes(item)

					for (entry in attrs) {
						val attr = entry.key
						if (attr == "value") {
							val attrValue = entry.value
							println("=====> attrValue : " + attrValue)
							println("=====> attrValue class: " + attrValue::class)
							println("=====> attrValue get: " + attrValue.get(0))
							println("=====> attrValue class get: " + attrValue.get(0)::class)
							println("=====> attrValue get: " + attrValue.get(0).javaClass)
							System.out.println("=========> ipath matched ! ");
							path = ((attrValue.get(0) as Array<String>)[0]).replace("/", "")
						}
					}
				}
			}
			result.add(linkTo(this::class.java).slash(path).withRel(path))
		}
		var resourceSupport: ResourceSupport = ResourceSupport()
		resourceSupport.add(result)
		return ResponseEntity<ResourceSupport>(resourceSupport, HttpStatus.OK)
//		return null;
	}

	@GetMapping("customers")
	fun getCustomers(): ResponseEntity<AlpsControllerResource> {
		var descList: MutableList<Descriptor> = arrayListOf()
		// Resolve customer representation
		var link: Link = linkTo(methodOn(this::class.java).getCustomers()).withSelfRel()
		val customerRepName: String = "customer-representation"

		var customerFieldDescList: MutableList<Descriptor> = arrayListOf()
		for (field in CustomerResource::class.java.getDeclaredFields()) {
			println("field name :" + field.name + " | " + field.type)
			var doc: Doc = Doc.builder().value("Type : " + field.type).build()
			var fieldDesc: Descriptor = Descriptor.builder().name(field.name).type(Type.SEMANTIC).doc(doc).build()
			customerFieldDescList.add(fieldDesc)
		}
		var customerDesc: Descriptor = Descriptor.builder().id(customerRepName).href(link.href).descriptors(customerFieldDescList).build()

		descList.add(customerDesc)
		// Resolve descriptor for each method annotate with requestMapping annotation
		for (method in CustomerController::class.java.getDeclaredMethods()) {
			var resolvedAlpsType: Type = Type.SEMANTIC
			var createDescriptor: Boolean = false
			for (annotation in method.getDeclaredAnnotations()) {
				when (annotation.annotationClass.simpleName) {
					GetMapping::class.java.simpleName -> {
						resolvedAlpsType = Type.SAFE
						createDescriptor = true
					}
					PutMapping::class.java.simpleName, DeleteMapping::class.java.simpleName -> {
						resolvedAlpsType = Type.IDEMPOTENT
						createDescriptor = true
					}
					PostMapping::class.java.simpleName -> {
						resolvedAlpsType = Type.UNSAFE
						createDescriptor = true
					}
					else -> Type.SEMANTIC
				}
			}
			if (createDescriptor) {
				println("==========> method name :" + method.name + " |  " + method.returnType + " | genericReturnType : " + method.genericReturnType)
				// extract return type and resolve rt alps field :
				// if return is CustomerRessource ==> we point to #customer-representation defined in first descriptor
				// if return is java.lang.void ==> no rt
				// if resukt contains PagedResource we point to #customer-representation and add extra descriptor for pagination idiom
				var typeFullString: String = method.genericReturnType.toString()
				var pagedResource: Boolean = false
				var descParameterList: MutableList<Descriptor>? = null

				for (param in method.parameters) {
					// Ignore request body
					println("param0 :" + param)
					println("param : " + param.getName() + "param type :" + param.getType() + "parametrized type :" + param.getParameterizedType())
				}
				var rt: String? = null
				if (typeFullString.contains(PagedResources::class.java.name)) {
					pagedResource = true
					// extract type
					var typeStringClass: String = typeFullString.substring(typeFullString.lastIndexOf("<") + 1, typeFullString.length - 2)
					println("typeStringClass :" + typeStringClass)
					if (typeStringClass == CustomerResource::class.java.name) {
						rt = "#" + customerRepName
					}
				} else {
					var typeStringClass: String = typeFullString.substring(typeFullString.lastIndexOf("<") + 1, typeFullString.length - 1)
					println("typeStringClass :" + typeStringClass)
					if (typeStringClass == CustomerResource::class.java.name) {
						rt = "#" + customerRepName
					}
				}
				var methodDesc: Descriptor = Descriptor.builder().id(method.getName() + "-customer").name(method.getName()).rt(rt).type(resolvedAlpsType).build()
				descList.add(methodDesc)
			}
		}

		var alps: Alps = Alps.alps().descriptors(descList).build()
		var result: AlpsControllerResource = AlpsControllerResource(alps)
		return ResponseEntity<AlpsControllerResource>(result, HttpStatus.OK)
	}

}