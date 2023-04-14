# API Development with OpenAPI: spec-after, spec-first and spec-only

## A bit about OpenAPI
OpenAPI, formerly known as Swagger, is a specification for describing and documenting RESTful APIs. It provides a 
standardized format for defining API endpoints, request/response structures, authentication, and other metadata, 
making it easier for developers to understand and build applications that interact with the APIs with specs. OpenAPI is 
also machine-readable, which allows for automated generation of client libraries, testing tools, and documentations.  

The OpenAPI Specification has gained remarkable popularity and adoption across the software industry in recent years,
becoming the de facto standard for defining and documenting RESTful APIs. This growth can be attributed to
the benefits it offers, such as common language for API contract and documentation, interoperability, and automation in 
API development and consumption. The extensive ecosystem of tools and libraries supporting the OpenAPI Specification 
further enhances its appeal, enabling organizations to streamline their API lifecycle management, from design and 
documentation to testing and implementation. As the specification continues to evolve and improve, its adoption is 
expected to keep growing, solidifying its position as an essential component in modern API-driven software development.

The latest version of the OpenAPI Specification, 3.1.0, can be found at https://spec.openapis.org/oas/v3.1.0. This 
version introduces incremental improvements and several new features compared to version 3.0.3. The two enhancements 
that I like the most are: (1) better alignment with JSON Schema, which streamlines schema definition and validation, 
and (2) the addition of 'summary' and 'description' support, which significantly improves human-readability. I highly 
recommend that anyone working with RESTful APIs or Microservices Architecture takes the time to explore this update. 

## How to leverage OpenAPI
For those of use in the space of API-driven software development, the question is how we can use OpenAPI. 

In situations where RESTful APIs were developed before the introduction and widespread adoption of the OpenAPI 
Specification, it is still possible to retroactively document these APIs using the OpenAPI format. By doing so, 
organizations can benefit from improved consistency, maintainability, and automation in their API development and 
consumption processes. To achieve this, developers can manually create an OpenAPI document that accurately describes 
the existing API's endpoints, request/response structures, authentication mechanisms, and other metadata. Once the 
OpenAPI document is in place, it can serve as the single source of truth for API documentation, client library 
generation, and testing, ensuring that the API's capabilities are well-understood and easily accessible to developers 
and other stakeholders. This is what I called the 'spec-after' approach.  

In contrast, the "spec-first" approach, also known as the "API-first" or "design-first" approach, prioritizes the 
creation of an OpenAPI specification at the beginning of the API development process. This strategy emphasizes the 
importance of planning and documenting the API before any implementation takes place. This method involves defining the 
API's endpoints, request/response structures, authentication mechanisms, and other metadata using the OpenAPI format as 
the initial step. The benefits of this approach include improved consistency, collaboration, and maintainability across 
the entire API development lifecycle. By establishing a clear and well-structured API contract upfront, it ensures that 
both frontend and backend developers have a shared understanding of the API's capabilities and requirements. 
Additionally, the OpenAPI document can serve as the foundation for generating interactive documentation, client 
libraries, server stubs, and testing tools, significantly streamlining the development process and reducing the 
likelihood of errors and inconsistencies. Overall, the API-first approach fosters better communication, faster 
development, and a more robust, reliable, and maintainable API.

One step further, there is "spec-only" approach, I believe. This approach presents a unique method that seamlessly 
integrates API design and implementation. This approach relies on a service framework that offers configuration-driven 
extension points and pluggable components that implement these extension points. By utilizing OpenAPI extensions, the 
specification not only defines the API contract but also provides implementation instructions to the framework in the 
form of OpenAPI extensions. This streamlined process enables developers to focus on designing the API contract while 
the service framework, guided by the embedded instructions, takes care of the implementation details. As a result, the 
"spec-only" approach promotes a more efficient and maintainable development process, ultimately leading to 
higher-quality APIs that adhere to best practices and design principles at both API contract layer and implementation 
layer. The "spec-only" approach to developing RESTful APIs with OpenAPI specification is fundamentally an 
extension-driven method. This approach hinges on the use of OpenAPI extensions, which are crucial for integrating API 
design and implementation. Without these extensions, the "spec-only" approach would not be feasible. By leveraging 
OpenAPI extensions, developers can embed implementation instructions directly into the API contract, allowing the 
service framework to automatically generate the necessary implementation based on the provided guidance on the fly. 
This extension-driven process enables a more efficient and cohesive development experience, ensuring that the API 
design and implementation remain closely aligned throughout the entire lifecycle.

# Spec-only API development using Weave

Weave, the open source project I have been worked on for a while, offers such a "spec-only" approach to API development. 

It has a highly extensible framework supporting various extension points: ServiceExecutor, ComponentFactory, 
ProcessorFactory, and more. All functionalities and capabilities are introduced through extensions, ensuring a 
extension-driven development process.

Weave-based APIs are entirely declarative and driven by specifications. API lifecycle are managed by performing CRUD 
operations on OpenAPI specs. For a given service application instance, its APIs are wholly defined and driven by the 
OpenAPI spec it processes. This approach guarantees consistent and seamless API design and implementation, with no 
exceptions!

Let's dive into one example which demonstrates "spec-only" approach using Weave.

# Examples

This example showcases various extension points and illustrates how an API is realized using the OpenAPI specification 
within the Weave framework. By leveraging these extension points, the framework seamlessly translates the API design 
into an efficient and consistent implementation, demonstrating the power and flexibility of the "spec-only" approach.

## Design OpenAPI spec
```
{
    "openapi": "3.1.0",
    "info": {
        "title": "Greeting API",
        "description": "Demonstrate basic extension points and declarative API and basic extensions points",
        "contact": {
            "name": "Stanley Xu",
            "email": "aftersound@gmail.com"
        },
        "version": "1.0.0"
    },
    "paths": {
        "/greet/{name}": {
            "get": {
                "summary": "greet a person or entity by name",
                "description": "Greet service without auth and rate limit",
                "operationId": "greet",
                "parameters": [
                    {
                        "name": "name",
                        "in": "path",
                        "description": "the name of person or entity to greet",
                        "required": true,
                        "schema": {
                            "type": "string"
                        }
                    }
                ],
                "responses": {
                    "200": {
                        "description": "greeting happens",
                        "content": {
                            "application/json": {
                                "schema": {
                                    "$ref": "#/components/schemas/Greeting"
                                }
                            }
                        }
                    },
                    "500": {
                        "description": "internal error",
                        "content": {
                            "application/json": {
                                "schema": {
                                    "$ref": "#/components/schemas/Messages"
                                }
                            }
                        }
                    }
                },
                "extensions": {
                    "x-weave-execution-control": {
                        "type": "GreetingService",
                        "greetingWords": [
                            "您好",
                            "Hello",
                            "¡Hola",
                            "Aloha",
                            "Bonjour",
                            "Hallo",
                            "Ciao",
                            "こんにちは",
                            "안영하세요"
                        ]
                    }
                }
            }
        },
        "/greet1/{name}": {
            "get": {
                "summary": "greet a person or entity by name",
                "description": "Greet service with auth but without rate limit",
                "operationId": "greet",
                "parameters": [
                    {
                        "name": "name",
                        "in": "path",
                        "description": "the name of person or entity to greet",
                        "required": true,
                        "schema": {
                            "type": "string"
                        }
                    }
                ],
                "responses": {
                    "200": {
                        "description": "greeting happens",
                        "content": {
                            "application/json": {
                                "schema": {
                                    "$ref": "#/components/schemas/Greeting"
                                }
                            }
                        }
                    },
                    "500": {
                        "description": "internal error",
                        "content": {
                            "application/json": {
                                "schema": {
                                    "$ref": "#/components/schemas/Messages"
                                }
                            }
                        }
                    }
                },
                "extensions": {
                    "x-weave-auth-control": {
                        "type": "Demo",
                        "userCredentials": {
                            "user": "W6ph5Mm5Pz8GgiULbPgzG37mj9g="
                        }
                    },
                    "x-weave-execution-control": {
                        "type": "GreetingService",
                        "greetingWords": [
                            "您好",
                            "Hello",
                            "¡Hola",
                            "Aloha",
                            "Bonjour",
                            "Hallo",
                            "Ciao",
                            "こんにちは",
                            "안영하세요"
                        ]
                    }
                }
            }
        },
        "/greet2/{name}": {
            "get": {
                "summary": "greet a person or entity by name",
                "description": "Greet service without auth but with rate limit",
                "operationId": "greet",
                "parameters": [
                    {
                        "name": "name",
                        "in": "path",
                        "description": "the name of person or entity to greet",
                        "required": true,
                        "schema": {
                            "type": "string"
                        }
                    }
                ],
                "responses": {
                    "200": {
                        "description": "greeting happens",
                        "content": {
                            "application/json": {
                                "schema": {
                                    "$ref": "#/components/schemas/Greeting"
                                }
                            }
                        }
                    },
                    "500": {
                        "description": "internal error",
                        "content": {
                            "application/json": {
                                "schema": {
                                    "$ref": "#/components/schemas/Messages"
                                }
                            }
                        }
                    }
                },
                "extensions": {
                    "x-weave-rate-limit-control": {
                        "type": "Demo"
                    },
                    "x-weave-execution-control": {
                        "type": "GreetingService",
                        "greetingWords": [
                            "您好",
                            "Hello",
                            "¡Hola",
                            "Aloha",
                            "Bonjour",
                            "Hallo",
                            "Ciao",
                            "こんにちは",
                            "안영하세요"
                        ]
                    }
                }
            }
        },
        "/greet3/{name}": {
            "get": {
                "summary": "greet a person or entity by name",
                "description": "Greet service with both auth and rate limit",
                "operationId": "greet",
                "parameters": [
                    {
                        "name": "name",
                        "in": "path",
                        "description": "the name of person or entity to greet",
                        "required": true,
                        "schema": {
                            "type": "string"
                        }
                    }
                ],
                "responses": {
                    "200": {
                        "description": "greeting happens",
                        "content": {
                            "application/json": {
                                "schema": {
                                    "$ref": "#/components/schemas/Greeting"
                                }
                            }
                        }
                    },
                    "500": {
                        "description": "internal error",
                        "content": {
                            "application/json": {
                                "schema": {
                                    "$ref": "#/components/schemas/Messages"
                                }
                            }
                        }
                    }
                },
                "extensions": {
                    "x-weave-auth-control": {
                        "type": "Demo",
                        "userCredentials": {
                            "user": "W6ph5Mm5Pz8GgiULbPgzG37mj9g="
                        }
                    },
                    "x-weave-rate-limit-control": {
                        "type": "Demo"
                    },
                    "x-weave-execution-control": {
                        "type": "GreetingService",
                        "greetingWords": [
                            "您好",
                            "Hello",
                            "¡Hola",
                            "Aloha",
                            "Bonjour",
                            "Hallo",
                            "Ciao",
                            "こんにちは",
                            "안영하세요"
                        ]
                    }
                }
            }
        }
    },
    "components": {
        "schemas": {
            "Greeting": {
                "type": "object",
                "properties": {
                    "greeting": {
                        "type": "string",
                        "example": "Hello, Weave"
                    }
                }
            },
            "Messages": {
                "type": "object",
                "properties": {
                    "messages": {
                        "type": "array",
                        "items": {
                            "$ref": "#/components/schemas/Message"
                        }
                    }
                }
            },
            "Message": {
                "required": [
                    "id",
                    "severity",
                    "category",
                    "message"
                ],
                "type": "object",
                "properties": {
                    "id": {
                        "type": "integer",
                        "format": "int64"
                    },
                    "severity": {
                        "type": "string",
                        "enum": [
                            "Error",
                            "Warning"
                        ]
                    },
                    "category": {
                        "type": "string",
                        "enum": [
                            "System",
                            "Application",
                            "Service",
                            "Request"
                        ]
                    },
                    "message": {
                        "type": "string"
                    }
                }
            }
        }
    },
    "extensions": {
        "x-weave-extensions": [
            {
                "group": "COMPONENT_FACTORY",
                "baseType": "io.aftersound.weave.component.ComponentFactory",
                "types": []
            },
            {
                "group": "VALUE_FUNC_FACTORY",
                "baseType": "io.aftersound.weave.common.ValueFuncFactory",
                "types": [
                    "io.aftersound.weave.service.request.ParamValueFuncFactory",
                    "io.aftersound.weave.value.CommonValueFuncFactory"
                ]
            },
            {
                "group": "AUTH_HANDLER",
                "baseType": "io.aftersound.weave.service.security.AuthHandler",
                "types": [
                    "io.aftersound.weave.sample.extension.service.security.DemoAuthHandler"
                ]
            },
            {
                "group": "RATE_LIMIT_EVALUATOR",
                "baseType": "io.aftersound.weave.service.rl.RateLimitEvaluator",
                "types": [
                    "io.aftersound.weave.sample.extension.service.rl.DemoRateLimitEvaluator"
                ]
            },
            {
                "group": "SERVICE_EXECUTOR",
                "baseType": "io.aftersound.weave.service.ServiceExecutor",
                "types": [
                    "io.aftersound.weave.sample.extension.service.GreetingServiceExecutor"
                ]
            }
        ],
        "x-weave-components": []
    }
}
```

## Implement the API as defined by the OpenAPI spec

Before you begin, ensure you have the following prerequisites:

- A Docker Hub account
- Docker Desktop installed
- Postman installed

Assuming the specification is saved as "openapi.json" in the current directory, you can execute the following shell 
command to start a service application instance that hosts the Greeting API:

```
docker container run -it -p 8080:8080 -e WEAVE_PROFILE=standard-openapi-provided -e WEAVE_NAMESPACE=demo -e WEAVE_APPLICATION=basics -e WEAVE_OPENAPI_SPEC='BASE64|'$(cat openapi.json|base64 -w 0) aftersound/weave:bundle
```

To check if the service instance is up and running, and to verify the APIs in the OpenAPI 3.1.0 specification or Weave's
runtime specification, use the following commands:
```
curl -H 'Accept: application/json' http://localhost:8080/discovery/openapi
curl -H 'Accept: application/json' http://localhost:8080/discovery/service
```

## Try the API

It's time to try the API.

```
curl -H 'Accept: application/json' http://localhost:8080/greet/aftersound
```

For more advanced examples and demonstrations, please visit the Weave Docker Hub repository at 
https://hub.docker.com/r/aftersound/weave. The starting point demo instruction API showcases a variety of examples that 
emphasize the capabilities and versatility of the Weave framework and its extensions. Featured examples cover areas 
such as: (1) service management planes with centralized OpenAPI spec management, OpenAPI spec search and discovery, 
service instance registration and discovery, heartbeat mechanism, and more; (2) collaborative job execution framework; 
(3) Apache Kafka message production and consumption, and much more.

# What's next

Several enhancements are planned for the future development of Weave, including:

- Integration with processing engines like Apache Flink and Apache Spark, designed as an addon to Weave's collaborative 
job execution framework, further enhancing its data processing capabilities.
- Integration with the Kong API Gateway to streamline API management and deliver added functionality, such as traffic 
routing and load balancing.
- Development of extensions that support popular Authentication Service Providers, simplifying the process for users to 
leverage existing authentication mechanisms within the Weave framework.

# Last but not the least

Thank you for following along with me so far. If you're interested in using Weave for API development, want to learn 
more about it, or have feature requests, please don't hesitate to reach out to me on LinkedIn or via email. I'd be 
happy to help and provide further information.
