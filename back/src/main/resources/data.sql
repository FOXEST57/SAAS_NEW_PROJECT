-- ============================================================
-- DATA.SQL - Jeu de données complet pour SAAS Facturation
-- IDs auto-générés par H2 IDENTITY
-- ============================================================

-- COUNTRY
INSERT INTO country (cnt_name) VALUES
    ('France'),
    ('Belgique'),
    ('Luxembourg');

-- POSTAL_CODE
INSERT INTO postal_code (p_code_name) VALUES
    ('57100'),
    ('57000'),
    ('67000'),
    ('75001'),
    ('59000'),
    ('1000'),
    ('L-1111');

-- CITY
INSERT INTO city (cnt_id, city_name) VALUES
    (1, 'Thionville'),
    (1, 'Metz'),
    (1, 'Strasbourg'),
    (1, 'Paris'),
    (1, 'Lille'),
    (2, 'Bruxelles'),
    (3, 'Luxembourg');

-- POSTAL_CODE_CITY
INSERT INTO postal_code_city (city_id, pcode_id) VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 5),
    (6, 6),
    (7, 7);

-- ADDRESS
INSERT INTO address (city_id, pcode_id, add_complement, add_number, add_street) VALUES
    (1, 1, NULL, '12', 'Rue du Commerce'),
    (2, 2, 'Bâtiment A', '25', 'Rue Serpenoise'),
    (3, 3, NULL, '8', 'Rue des Orfèvres'),
    (4, 4, '1er étage', '45', 'Rue de Rivoli'),
    (5, 5, NULL, '17', 'Rue Nationale'),
    (6, 6, NULL, '10', 'Avenue Louise'),
    (7, 7, NULL, '3', 'Rue du Fort Bourbon');

-- TVA
INSERT INTO tva (tva_name, tva_taux) VALUES
    ('TVA normale', 0.20),
    ('TVA intermédiaire', 0.10),
    ('TVA réduite', 0.055);

-- CATEGORY
INSERT INTO category (cat_parent_id, cat_name, cat_slug) VALUES
    (NULL, 'Informatique', 'informatique'),
    (1, 'Claviers', 'claviers'),
    (1, 'Souris', 'souris'),
    (1, 'Écrans', 'ecrans'),
    (NULL, 'Bureautique', 'bureautique');

-- Customer Creator :
INSERT INTO customer (add_id, corp_corporation_id, ctm_creation_date, ctm_modification_date, ctm_email, ctm_first_name, ctm_last_name, ctm_phone, password, account_type) VALUES
(1, NULL, '2026-01-10T09:00:00', '2026-01-10T09:00:00', 'john.doe@email.com', 'John', 'Doe', '0611223344', '$2a$10$buK.PnKNJgZex1l0MGDX5OJMKHX7elvrE05KboGVs95EETucrcigq', 'OWNER');

-- CORPORATION
INSERT INTO corporation (address_add_id, owner_ctm_id, corp_creation_date, corp_modification_date, corp_email, corp_iban, corp_name, corp_phone, corp_pre_ref_invoice, corp_pre_ref_quote, corp_siret, corp_tag, corp_tva) VALUES
    (4, 1, '2026-01-05T08:30:00', '2026-01-05T08:30:00', 'contact@techsolutions.fr', 'FR7612345678901234567890123', 'Tech Solutions', '0387556677', 'FAC', 'DEV', '12345678901234', 'TECH', 'FR12345678901');

-- CUSTOMER
INSERT INTO customer (add_id, corp_corporation_id, ctm_creation_date, ctm_modification_date, ctm_email, ctm_first_name, ctm_last_name, ctm_phone, password, account_type) VALUES
    (1, NULL, '2026-01-05T08:00:00', '2026-01-05T08:00:00', 'admin@saas-facturation.fr', 'Admin', 'System', '0644556677', '$2a$10$abcdefghijklmnopqrstuv', 'ADMIN'),
    (2, 1, '2026-01-12T10:00:00', '2026-01-12T10:00:00', 'alice.martin@email.com', 'Alice', 'Martin', '0622334455', '$2a$10$abcdefghijklmnopqrstuv', 'USER'),
    (3, 1, '2026-01-15T11:00:00', '2026-01-15T11:00:00', 'paul.dupont@email.com', 'Paul', 'Dupont', '0633445566', '$2a$10$abcdefghijklmnopqrstuv', 'USER');


-- CORPORATION_CUSTOMERS
INSERT INTO corporation_customers (corporations_corp_id, customers_ctm_id) VALUES
    (1, 1),
    (1, 2),
    (1, 3);

-- ARTICLE
INSERT INTO article (tva_id, art_price_exclude_taxes, is_active, art_create_date, art_update_date, art_description, art_name, art_reference) VALUES
    (1, 79.99, TRUE, '2026-01-20T09:00:00', '2026-01-20T09:00:00', 'Clavier mécanique rétroéclairé', 'Clavier mécanique', 'ref-001'),
    (1, 39.90, TRUE, '2026-01-20T09:05:00', '2026-01-20T09:05:00', 'Souris ergonomique sans fil', 'Souris ergonomique', 'ref-002'),
    (1, 249.90, TRUE, '2026-01-20T09:10:00', '2026-01-20T09:10:00', 'Écran 27 pouces QHD', 'Écran 27 pouces', 'ref-003'),
    (1, 129.00, TRUE, '2026-01-20T09:15:00', '2026-01-20T09:15:00', 'Casque audio professionnel', 'Casque audio', 'ref-004'),
    (1, 19.90, TRUE, '2026-01-20T09:20:00', '2026-01-20T09:20:00', 'Tapis de souris grand format', 'Tapis de souris', 'ref-005');

-- ARTICLE_CATEGORY
INSERT INTO article_category (article_id, category_id) VALUES
    (1, 2),
    (2, 3),
    (3, 4),
    (4, 1),
    (5, 3);

-- MAKER
INSERT INTO maker (address_id, mkr_email, mkr_phone, mkr_name) VALUES
    (5, 'contact@techmaker.fr', '0320556677', 'TechMaker'),
    (6, 'contact@prodevices.be', '023456789', 'ProDevices');

-- SUPPLIER
INSERT INTO supplier (address_id, spl_email, spl_name, spl_phone) VALUES
    (5, 'sales@hardware-supplier.fr', 'Hardware Supplier', '0320112233'),
    (7, 'sales@lux-supply.lu', 'Lux Supply', '26112233');

-- MAKER_REFERENCE
INSERT INTO maker_reference (article_id, maker_id, art_mkr_reference)
VALUES
    (1, 1, 'MKR-CLAV-001'),
    (2, 2, 'MKR-SOUR-002'),
    (3, 1, 'MKR-ECR-003'),
    (4, 3, 'MKR-CASQ-004');

-- SUPPLIER_REFERENCE
INSERT INTO supplier_reference (article_id, supplier_id, spl_ref_reference)
VALUES
    (1, 1, 'TC-USB-64'),
    (1, 2, 'OP-USB-64'),
    (2, 3, 'FP-CHAIR-ERG'),
    (3, 1, 'TC-SCREEN-27'),
    (4, 2, 'OP-PAPER-A4');

-- DELIVERY
INSERT INTO delivery (dlv_quantity, dlv_buying_price, dlv_status, dlv_created_date, dlv_updated_date, spl_article_id, supplier_id, mkr_article_id, maker_id)
VALUES
    (3, 60.00, 'ACCEPTED', current_date, current_date, 1, 1, null, null),
    (2, 55.99, 'ACCEPTED', current_date, current_date, 1, 2, null, null),
    (10, 29.99, 'PENDING', current_date, current_date, 2, 3, null, null),
    (5, 199.99, 'ACCEPTED', current_date, current_date, 3, 1, null, null),
    (4, 85.99, 'ACCEPTED', current_date, current_date, 4, 2, null, null),
    (6, 58.90, 'ACCEPTED', current_date, current_date, null, null, 1, 1),
    (1, 25.90, 'ACCEPTED', current_date, current_date, null, null, 2, 2),
    (1, 200.00, 'PENDING', current_date, current_date, null, null, 3, 1),
    (2, 99.99, 'ACCEPTED', current_date, current_date, null, null, 4, 3);

-- INVENTORY
INSERT INTO inventory (article_art_id, inv_date, inv_stock) VALUES
    (1, '2026-02-01T08:00:00', 25),
    (2, '2026-02-01T08:00:00', 40),
    (3, '2026-02-01T08:00:00', 15),
    (4, '2026-02-01T08:00:00', 20),
    (5, '2026-02-01T08:00:00', 100);

-- CART
INSERT INTO cart (creator_id, crt_create_date, crt_last_modifie_date, parent_quote_id, crt_ref, receiver_email, crt_status) VALUES
    (1, '2026-02-10T09:00:00', '2026-02-10T09:00:00', NULL, 'PAN-2026-0001', 'john.doe@email.com', 'VALIDATED'),
    (2, '2026-02-11T10:00:00', '2026-02-11T10:00:00', NULL, 'PAN-2026-0002', 'alice.martin@email.com', 'VALIDATED'),
    (3, '2026-02-12T11:00:00', '2026-02-12T11:00:00', NULL, 'PAN-2026-0003', 'paul.dupont@email.com', 'OPEN');

-- ORDER_LINE
INSERT INTO order_line (article_id, cart_id, ord_ln_quantity) VALUES
    (1, 1, 2),
    (2, 1, 1),
    (3, 2, 1),
    (4, 2, 1),
    (5, 3, 3);

-- QUOTE
INSERT INTO quote (cart_crt_id, creator_id, qot_created_date, qot_expiration_date, qot_modified_date, qot_parent_qot_id, qot_number, qot_pathpdf, receiver_email, qot_status) VALUES
    (1, 1, '2026-02-10T09:30:00', '2026-03-10', '2026-02-10T09:30:00', NULL, 'DEV-2026-0001', 'DEV-2026-0001.pdf', 'john.doe@email.com', 'ACCEPTED'),
    (2, 2, '2026-02-11T10:30:00', '2026-03-11', '2026-02-11T10:30:00', NULL, 'DEV-2026-0002', 'DEV-2026-0002.pdf', 'alice.martin@email.com', 'ACCEPTED'),
    (3, 3, '2026-02-12T11:30:00', '2026-03-12', '2026-02-12T11:30:00', NULL, 'DEV-2026-0003', NULL, 'paul.dupont@email.com', 'PENDING');

-- QUOTE_LINE
INSERT INTO quote_line (qot_ln_priceht, qot_ln_quantity, tva_rate, quote_qot_id, article_name, article_ref) VALUES
    (79.99, 2, 0.20, 1, 'Clavier mécanique', 'ref-001'),
    (39.90, 1, 0.20, 1, 'Souris ergonomique', 'ref-002'),
    (249.90, 1, 0.20, 2, 'Écran 27 pouces', 'ref-003'),
    (129.00, 1, 0.20, 2, 'Casque audio', 'ref-004'),
    (19.90, 3, 0.20, 3, 'Tapis de souris', 'ref-005');

-- COMMAND
INSERT INTO command (cmd_create_date, cmd_modified_date, cmd_reference, creator_id, quote_id, receiver_email, cmd_status) VALUES
    ('2026-02-20T09:00:00', '2026-02-20T09:00:00', 'CDE-2026-0001', 1, 1, 'john.doe@email.com', 'DELIVERED'),
    ('2026-02-21T10:00:00', '2026-02-21T10:00:00', 'CDE-2026-0002', 2, 2, 'alice.martin@email.com', 'ACCEPTED'),
    ('2026-02-22T11:00:00', '2026-02-22T11:00:00', 'CDE-2026-0003', 3, 3, 'paul.dupont@email.com', 'PENDING');

-- INVOICE
INSERT INTO invoice (command_cmd_id, creator_id, invoice_created_date, invoice_modified_date, invoice_number, invoice_pathpdf, receiver_email, invoice_status) VALUES
    (1, 1, '2026-02-20T09:30:00', '2026-02-20T09:30:00', 'FAC-2026-0001', 'FAC-2026-0001.pdf', 'john.doe@email.com', 'PARTIALLY_PAID'),
    (2, 2, '2026-02-21T10:30:00', '2026-02-21T10:30:00', 'FAC-2026-0002', 'FAC-2026-0002.pdf', 'alice.martin@email.com', 'SENT');

-- INVOICE_LINE
INSERT INTO invoice_line (inv_ln_priceht, inv_ln_quantity, tva_rate, invoice_invoice_id, article_name, article_ref) VALUES
    (79.99, 2, 0.20, 1, 'Clavier mécanique', 'ref-001'),
    (39.90, 1, 0.20, 1, 'Souris ergonomique', 'ref-002'),
    (249.90, 1, 0.20, 2, 'Écran 27 pouces', 'ref-003'),
    (129.00, 1, 0.20, 2, 'Casque audio', 'ref-004');

-- PAYMENT
INSERT INTO payment (pay_account, pay_amount, invoice_id, pay_created_date, pay_type) VALUES
    (FALSE, 100.00, 1, '2026-02-25T10:00:00', 'BANK_TRANSFER'),
    (FALSE, 50.00, 1, '2026-03-01T10:00:00', 'CREDIT_CARD'),
    (TRUE, 100.00, 2, '2026-02-26T11:00:00', 'BANK_TRANSFER'),
    (FALSE, 100.00, 2, '2026-03-02T12:00:00', 'DIRECT_DEBIT');

-- INVITATION
INSERT INTO invitation (corporation_id, customer_id, inv_creation_date, inv_expiration_date, inv_email, inv_token, invitation_type, used) VALUES
    (1, NULL, '2026-03-01T09:00:00', '2026-03-08T09:00:00', 'new.customer@email.com', 'token-customer-001', 'CUSTOMER', FALSE),
    (1, NULL, '2026-03-02T09:00:00', '2026-03-09T09:00:00', 'employee@techsolutions.fr', 'token-employee-001', 'EMPLOYEE', FALSE);

-- REFERENCE_COUNTER
INSERT INTO reference_counter (crp_id, counter, object_type) VALUES
    (1, 3, 'CART'),
    (1, 3, 'QUOTE'),
    (1, 3, 'COMMAND'),
    (1, 2, 'INVOICE');
