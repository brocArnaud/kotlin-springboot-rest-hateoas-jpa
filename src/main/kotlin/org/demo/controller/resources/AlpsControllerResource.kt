package org.demo.controller.resources

import org.springframework.hateoas.alps.Alps
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
open class AlpsControllerResource(var alps: Alps)