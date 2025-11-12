plugins {
    id("java")
    id("template-processing")
}

// Configure template processing
templateProcessing {
    // Configure DestinationRule template processing
    template("destinationRule") {
        templatePath.set("templates/DestinationRuleTemplate.yaml")
        outputDir.set("generated-manifests/destination-rules")
        outputFilePattern.set("{name}DestinationRule.yaml")

        // Add replacements using typed configuration
        replacement {
            name = "serviceA"
            placeholder("from", "serviceAValue")
            placeholder("to", "serviceBValue")
        }

        replacement {
            name = "serviceB"
            placeholder("from", "serviceCValue")
            placeholder("to", "serviceDValue")
        }

        // Alternative: using map syntax (as shown in the example)
        replacement(mapOf(
            "name" to "serviceC",
            "from" to "v1.0",
            "to" to "v2.0"
        ))
    }

    // Configure VirtualService template processing
    template("virtualService") {
        templatePath.set("templates/VirtualServiceTemplate.yaml")
        outputDir.set("generated-manifests/virtual-services")
        outputFilePattern.set("{name}VirtualService.yaml")

        replacement {
            name = "serviceA"
            placeholder("from", "v1")
            placeholder("to", "v2")
        }

        replacement {
            name = "serviceB"
            placeholder("from", "canary")
            placeholder("to", "stable")
        }
    }
}
