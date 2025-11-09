INSERT INTO oauth_clients (client_id, client_secret_hash, description, status)
VALUES (
           'ride-booking-service',
           '$2a$12$cd4b/ioAQGPIC0ZYPAhBWeBrX2VHQYXXfc7e0i6j6ildAAG2PQ3HW', -- bcrypt hash of RbSvc@2025!#secure
           'Ride Booking Microservice for handling trip reservations',
           'ACTIVE'
       );


INSERT INTO oauth_client_roles (client_id, role_id)
VALUES ('ride-booking-service', 4);
