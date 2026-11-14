# Software Requirements Specification (SRS) - ShareSphere

## 1. Functional Requirements Overview
- **FR-1:** User Onboarding & RBAC (User, Delivery Partner, Administrator)
- **FR-2:** Item Cataloging & Spatial Discovery (Search, Proximity, Categories)
- **FR-3:** Atomic Booking & Concurrency Arbitration (ACID-compliant date window locking)
- **FR-4:** Multi-Modal Fulfillment (Self-pickup vs Crowdsourced Delivery Partner)
- **FR-5:** Verifiable Handover Protocol (Pre/Post Condition Photography & Numeric OTP Handover)
- **FR-6:** Governance, Dispute Resolution & Bilateral Rating Subsystem

## 2. Non-Functional Requirements
- **Performance:** API response times under 200ms for p95 requests.
- **Security:** Argon2id/bcrypt password hashing, short-lived JWT tokens, CORS whitelist, Helmet headers, and input sanitization.
- **Data Integrity:** Foreign key referential integrity with strict transactional guarantees.
- **Portability:** Containerized microservices via Docker and Docker Compose.
