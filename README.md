# Template Processing Convention Plugin

A Gradle convention plugin for processing templates with placeholders in multi-project builds. This plugin allows you to create multiple manifest files from templates by replacing placeholders with configured values.

## Features

- **Typed Configuration**: Strongly-typed, extensible configuration DSL
- **Multiple Templates**: Support for processing multiple different templates in a single submodule
- **Flexible Placeholders**: Replace placeholders in format `@name@` with configured values
- **Automatic File Generation**: Generate separate files for each configuration from a single template
- **Gradle Integration**: Integrates seamlessly with the standard `processResources` task

## Project Structure

```
prg/
├── buildSrc/                                    # Convention plugin implementation
│   ├── build.gradle.kts
│   └── src/main/kotlin/
│       ├── TemplateProcessingExtension.kt       # Typed configuration classes
│       └── TemplateProcessingPlugin.kt          # Plugin implementation
├── config-module/                               # Example module using the plugin
│   ├── build.gradle.kts
│   └── src/main/resources/
│       └── templates/
│           ├── DestinationRuleTemplate.yaml     # Example template
│           └── VirtualServiceTemplate.yaml      # Example template
├── settings.gradle.kts
└── build.gradle.kts
```

## Usage

### 1. Apply the Plugin

In your module's `build.gradle.kts`:

```kotlin
plugins {
    id("java")
    id("template-processing")
}
```

### 2. Configure Templates

Use the `templateProcessing` extension to configure your templates:

```kotlin
templateProcessing {
    // Configure first template
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

        // Alternative: using map syntax
        replacement(mapOf(
            "name" to "serviceC",
            "from" to "v1.0",
            "to" to "v2.0"
        ))
    }

    // Configure second template
    template("virtualService") {
        templatePath.set("templates/VirtualServiceTemplate.yaml")
        outputDir.set("generated-manifests/virtual-services")
        outputFilePattern.set("{name}VirtualService.yaml")

        replacement {
            name = "serviceA"
            placeholder("from", "v1")
            placeholder("to", "v2")
        }
    }
}
```

### 3. Create Templates

Create your templates in `src/main/resources` with placeholders in `@name@` format:

**Example: DestinationRuleTemplate.yaml**
```yaml
apiVersion: networking.istio.io/v1beta1
kind: DestinationRule
metadata:
  name: @name@-destination-rule
  namespace: default
spec:
  host: @name@.default.svc.cluster.local
  subsets:
    - name: @from@
      labels:
        version: @from@
    - name: @to@
      labels:
        version: @to@
```

### 4. Run the Tasks

The plugin automatically creates tasks for each template configuration:

```bash
# List available template processing tasks
gradle tasks --group="template processing"

# Run specific template processing task
gradle processDestinationRuleTemplate

# Run all template processing tasks
gradle processResources
```

## Configuration Reference

### Template Configuration

Each template configuration has the following properties:

- `templatePath`: Path to the template file relative to resources directory
  - Example: `"templates/DestinationRuleTemplate.yaml"`

- `outputDir`: Output directory for processed files (relative to build directory)
  - Example: `"generated-manifests/destination-rules"`

- `outputFilePattern`: Output file name pattern (use `{name}` for the replacement name)
  - Example: `"{name}DestinationRule.yaml"`

### Replacement Configuration

Each replacement creates one output file:

- `name`: Identifier used in the output filename and automatically replaced in `@name@` placeholders

- `placeholders`: Map of custom placeholder names to their values
  - Configured using `placeholder(key, value)` method or map syntax

## Generated Output

For the example configuration above, running the tasks generates:

```
config-module/build/generated-manifests/
├── destination-rules/
│   ├── serviceADestinationRule.yaml
│   ├── serviceBDestinationRule.yaml
│   └── serviceCDestinationRule.yaml
└── virtual-services/
    ├── serviceAVirtualService.yaml
    └── serviceBVirtualService.yaml
```

Each file has all placeholders replaced with the configured values.

## Testing

Run the example:

```bash
# Clean and run all template processing
gradle clean :config-module:processResources

# Verify generated files
find config-module/build/generated-manifests -name "*.yaml"
```

## Key Features

1. **Not Bound to File Location**: Template paths and output directories are fully configurable
2. **Typed Configuration**: Compile-time safety with Kotlin DSL
3. **Extensible**: Easy to add new templates or modify existing ones
4. **Multiple Templates**: Process different template types in the same module
5. **Gradle Integration**: Works seamlessly with standard Gradle lifecycle

## How It Works

1. Plugin applies to the project and ensures Java plugin is present
2. Creates a `templateProcessing` extension for configuration
3. For each template configuration, registers a task named `process{TemplateName}Template`
4. Tasks are configured to depend on `processResources`
5. When executed, each task:
   - Reads the template file
   - For each replacement configuration:
     - Replaces `@name@` with the replacement name
     - Replaces all custom `@placeholder@` values
     - Writes to a separate output file

## Extension Points

The plugin is designed to be extensible. You can:

1. Add custom placeholder processing logic
2. Integrate with other plugins or build processes
3. Customize output file naming patterns
4. Add validation or pre/post-processing hooks
