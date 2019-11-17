# To do list

## Weave Service to-do-list

### Service Extension Cores

#### ~~Security core~~  
- {AuthenticationControl,Authenticator,Authentication}
- {AuthorizationControl,Authorizer,Authorization}

#### View core  
- {ViewControl,ViewTransformer,Object}

### Weave Service Framework Core
1. ~~Integrate with security core, goal is to support ServiceMetadata level SecurityControl.~~  
[General design](https://github.com/aftersound/weave/docs/control-driven-service-security.gliffy) is to 
- to plug Authenticator/Authorizer extensions into Spring Security filter
- inject ServiceMetadata level security control into Spring Security filter.

2. ~~Status Code handling~~  

3. ~~DataClientRegistry and DataClientManager fine-tune data client lifecycle management~~

4. ServiceMetadata.id pattern matching

5. Introduce namespace

6. Integrate with cache core (in progress)  
  - create/destroy cache on changes in/of ServiceMetadata
  - integrate with {GuavaCacheControl, GuavaCacheFactory, Cache}, which needs to be implemented as extension

7. Integer with view core

8. Enable HTTPS by default

9. Error handling for auth and auth failure

### Weave Service Extensions

Check TODO.md in [weave-managed-extensions](https://github.com/aftersound/weave-managed-extensions).

## Weave Batch to-do-list

### Weave Batch Extension Cores

### Weave Batch Framework Core
1. introduce namespace

### Weave Batch Extensions

Check TODO.md in [weave-managed-extensions](https://github.com/aftersound/weave-managed-extensions).

## Common to-do-list

### Common Extension Cores

### Common Extensions

Check TODO.md in [weave-managed-extensions](https://github.com/aftersound/weave-managed-extensions).
