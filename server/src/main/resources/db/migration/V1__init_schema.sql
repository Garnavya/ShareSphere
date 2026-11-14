-- ShareSphere Database Initialization Migration (Flyway V1)
-- Target: MySQL 8.0+

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    role ENUM('USER', 'DELIVERY_PARTNER', 'ADMIN') NOT NULL DEFAULT 'USER',
    rating DOUBLE NOT NULL DEFAULT 5.0,
    rating_count INT NOT NULL DEFAULT 0,
    avatar_url VARCHAR(500),
    address VARCHAR(500),
    latitude DOUBLE,
    longitude DOUBLE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS items (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(100) NOT NULL,
    item_condition ENUM('BRAND_NEW', 'LIKE_NEW', 'GOOD', 'FAIR') NOT NULL DEFAULT 'GOOD',
    daily_rate DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    security_deposit DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    is_free BOOLEAN NOT NULL DEFAULT FALSE,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    address VARCHAR(500) NOT NULL,
    latitude DOUBLE,
    longitude DOUBLE,
    owner_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_items_owner FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_items_category (category),
    INDEX idx_items_owner_id (owner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS item_images (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    item_id VARCHAR(36) NOT NULL,
    url VARCHAR(1000) NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_item_images_item FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    INDEX idx_item_images_item_id (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS bookings (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    item_id VARCHAR(36) NOT NULL,
    borrower_id VARCHAR(36) NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    total_days INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'AWAITING_HANDOVER', 'ACTIVE', 'RETURN_REQUESTED', 'RETURN_IN_PROGRESS', 'RETURNED', 'COMPLETED', 'CANCELLED', 'REJECTED', 'OVERDUE', 'DISPUTED') NOT NULL DEFAULT 'PENDING',
    fulfillment_type ENUM('SELF_PICKUP', 'DELIVERY_PARTNER') NOT NULL DEFAULT 'SELF_PICKUP',
    pickup_otp VARCHAR(255),
    return_otp VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookings_item FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_borrower FOREIGN KEY (borrower_id) REFERENCES users (id) ON DELETE RESTRICT,
    INDEX idx_bookings_concurrency (item_id, start_date, end_date),
    INDEX idx_bookings_borrower (borrower_id),
    INDEX idx_bookings_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS delivery_tasks (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    booking_id VARCHAR(36) NOT NULL,
    partner_id VARCHAR(36) NULL,
    phase ENUM('OUTBOUND', 'RETURN') NOT NULL DEFAULT 'OUTBOUND',
    status ENUM('PENDING', 'AVAILABLE', 'ASSIGNED', 'ACCEPTED', 'PICKED_UP', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    pickup_address VARCHAR(500) NOT NULL,
    dropoff_address VARCHAR(500) NOT NULL,
    delivery_fee DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    notes TEXT,
    picked_up_at DATETIME NULL,
    delivered_at DATETIME NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_delivery_tasks_booking FOREIGN KEY (booking_id) REFERENCES bookings (id) ON DELETE CASCADE,
    CONSTRAINT fk_delivery_tasks_partner FOREIGN KEY (partner_id) REFERENCES users (id) ON DELETE SET NULL,
    INDEX idx_delivery_booking (booking_id),
    INDEX idx_delivery_partner (partner_id),
    INDEX idx_delivery_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS condition_logs (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    booking_id VARCHAR(36) NOT NULL,
    logged_by_id VARCHAR(36) NOT NULL,
    stage ENUM('PRE_HANDOVER', 'POST_RETURN') NOT NULL,
    notes TEXT,
    image_urls JSON NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_condition_logs_booking FOREIGN KEY (booking_id) REFERENCES bookings (id) ON DELETE CASCADE,
    CONSTRAINT fk_condition_logs_user FOREIGN KEY (logged_by_id) REFERENCES users (id) ON DELETE RESTRICT,
    INDEX idx_condition_booking (booking_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS reviews (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    booking_id VARCHAR(36) NOT NULL,
    reviewer_id VARCHAR(36) NOT NULL,
    reviewee_id VARCHAR(36) NOT NULL,
    overall_rating INT NOT NULL,
    punctuality_rating INT,
    condition_rating INT,
    communication_rating INT,
    comment TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reviews_booking FOREIGN KEY (booking_id) REFERENCES bookings (id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_reviewer FOREIGN KEY (reviewer_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_reviews_reviewee FOREIGN KEY (reviewee_id) REFERENCES users (id) ON DELETE RESTRICT,
    INDEX idx_reviews_booking (booking_id),
    INDEX idx_reviews_reviewee (reviewee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS audit_logs (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    user_id VARCHAR(36) NULL,
    action VARCHAR(100) NOT NULL,
    entity VARCHAR(100) NOT NULL,
    entity_id VARCHAR(36) NULL,
    details JSON NULL,
    ip_address VARCHAR(50) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL,
    INDEX idx_audit_user (user_id),
    INDEX idx_audit_action (action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
