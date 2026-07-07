# Architecture Governance

## Overview
Reporta Tu Calle is a municipal civic crowdsourcing platform that enables citizens to report infrastructure issues with geolocation and photo evidence. The system automatically deduplicates near-identical reports using spatial criteria and generates work tickets. Municipal operators can prioritize, track, and update these tickets to resolution. The architecture enforces clean boundaries using a modular monolith design with strict domain isolation and facade-only communication.

## Modules
- **admin**: Frontend feature providing municipal supervisors with tools to manage operators, configure system parameters, and view dashboards.
- **auth**: Handles backend user registration, credentials authentication, session management, and JWT token issuance.
- **category**: Manages report categories, metadata configuration (names, markers, colors), and mapping rules for optimization algorithms.
- **map**: Frontend feature managing map components, coordinate capture, and visual rendering of report markers.
- **media**: Manages local or cloud file uploads for photo evidence, validating file sizes and formats.
- **optimization**: Computes optimized routes for supervisor inspection routes using routing, maximum flow, or connectivity algorithms.
- **report**: Manages the lifecycle of citizen reports, tickets, status transitions, geo-spatial deduplication, and endorsements.
- **user**: Manages citizen and supervisor profiles, including phone numbers, personal metadata, and account links.

## Architecture Rules
- **Module Isolation**: Each module is self-contained. Implementation details (JPA entities, repositories, internal services) must be kept package-private or private.
- **Database Schema Ownership**: Each module owns its database schema exclusively. Entities must declare `@Table(schema = "<module_schema>")`. No cross-schema reads/writes are allowed.
- **Facade-Only Communication**: Modules must communicate with each other exclusively through public Facades or application APIs.
- **Forbidden Cross-Module Access**: Direct dependency, import, or instantiation of internal components (e.g. services, JPA repositories, or entities) of other modules is strictly forbidden.

## Module Map
- **auth** &rarr; `com.reportatucalle.modules.auth.application.api.AuthFacade`
- **category** &rarr; `com.reportatucalle.modules.category.application.api.CategoryFacade`
- **report** &rarr; `com.reportatucalle.modules.report.application.api.ReportFacade`
- **user** &rarr; `com.reportatucalle.modules.user.application.api.UserFacade`
- **media** &rarr; `com.reportatucalle.modules.media.application.service.MediaService`
- **optimization** &rarr; `com.reportatucalle.modules.optimization.application.service.RouteOptimizationService`
- **admin** &rarr; Frontend Module (`frontend/src/features/admin`)
- **map** &rarr; Frontend Module (`frontend/src/features/map`)
