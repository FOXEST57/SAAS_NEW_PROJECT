-- 1. Table COUNTRY (référencé par CITY.cnt_id)
INSERT INTO country (cnt_name)
VALUES
    ('France');

-- 2. Table POSTAL_CODE
INSERT INTO postal_code (p_code_name)
VALUES
    ('69000'),
    ('59000'),
    ('67000'),
    ('44000'),
    ('57000'),
    ('69001');

-- 3. Table CITY (référence COUNTRY)
INSERT INTO city (city_name, cnt_id)
VALUES
    ('Lyon', 1),
    ('Lille', 1),
    ('Strasbourg', 1),
    ('Nantes', 1),
    ('Metz', 1);

-- 4. Table POSTAL_CODE_CITY (référence POSTAL_CODE et CITY)
INSERT INTO postal_code_city (pcode_id, city_id)
VALUES
    (1,1),
    (2,2),
    (3,3),
    (4,4),
    (5,5),
    (6,1);

-- 5. Table TVA (référencée par ARTICLE.tva_id)
INSERT INTO tva (tva_name, tva_taux)
VALUES
    ('taux normal', 0.2),
    ('taux intermédiaire', 0.1),
    ('taux particulier', 0.085),
    ('taux réduit', 0.055),
    ('taux super-réduit', 0.021);

-- 6. Table CATEGORY (référencée par ARTICLE.category_id)
INSERT INTO category (cat_name,cat_slug, cat_parent_id)
VALUES
    ('High-Tech','high-tech', NULL),
    ('Mobilier','mobilier', NULL),
    ('Accessoire informatique','accessoire-informatique',1),
    ('Moniteur','moniteur', 1),
    ('Mode','mode', NULL),
    ('Beauté & Santé','beaute-sante', NULL),
    ('Jeux & Jouets','jeux-jouets', NULL),
    ('Bricolage','bricolage', NULL),
    ('Auto & Moto','auto-moto', NULL);

-- 7. Table ADDRESS (référence POSTAL_CODE_CITY)
INSERT INTO address (add_number, add_street, add_complement, pcode_id, city_id)
VALUES
    ('12', 'rue de la paix', '', '1', '1'),
    ('6', 'rue des lilas', '', '5', '5'),
    ('33 bis', 'avenue de Paris', '', '3', '3');

-- 8. Table SUPPLIER (référencée par ARTICLE.supplier_id)
INSERT INTO supplier (spl_name, spl_email, spl_phone, address_id)
VALUES
    ('HP France', 'contact@hp.fr', '+33102030402', 1),
    ('Lenovo France', 'contact@lenovo.fr', '+33102030403', 2),
    ('Asus France', 'contact@asus.fr', '+33102030404', 2),
    ('Logitech France', 'contact@logitech.fr', '+33102030405', 1);

-- 9. Table MAKER (référencée par MAKER_REFERENCE.maker_id)
INSERT INTO maker (mkr_name,mkr_email,mkr_phone, address_id)
VALUES
    ('Fabricant 1','fabricant1@gmail.com','+33618765635',1),
    ('Fabricant 2', 'fabricant2@gmail.com', '+33619775736',2),
    ('Fabricant 3', 'fabricant3@gmail.com', '+33620785837',1),
    ('Fabricant 4', 'fabricant4@gmail.com', '+33621795938',1);

-- 10. Table ARTICLE (référence TVA et CATEGORY uniquement — pas de supplier_id direct)
INSERT INTO article (art_reference, art_name, art_description, art_price_exclude_taxes,art_create_date, art_update_date, tva_id)
VALUES
    ('REF-001', 'Clavier mécanique', 'Clavier mécanique switchs rouges, idéal pour le gaming.', 79.99,current_date, current_date, 1),
    ('REF-002', 'Souris ergonomique', 'Souris sans fil ergonomique pour réduire la fatigue du poignet.', 39.90,  current_date, current_date, 1),
    ('REF-003', 'Écran 27 pouces', 'Écran IPS 27 pouces 144Hz, parfait pour le travail et le jeu.', 229.00, current_date, current_date, 1),
    ('REF-004', 'Casque audio', 'Casque circum-aural avec réduction de bruit active.', 119.50, current_date, current_date, 3),
    ('REF-005', 'Hub USB-C', 'Hub USB-C 7 ports compatible Mac et Windows.', 29.99,  current_date, current_date, 2);

-- 11. Table SUPPLIER_REFERENCE (article, supplier, spl_ref_reference,supplier_price, spl_ref_stock)
INSERT INTO supplier_reference (article_id, supplier_id, spl_ref_reference, spl_ref_sell_price, spl_ref_stock, status)
VALUES
    (1, 1, 'TC-USB-64', 120.00, 120, 'RECEIVED'),
    (1, 2, 'OP-USB-64', 80.00, 120, 'PENDING'),
    (2, 3, 'FP-CHAIR-ERG', 40.00, 120, 'RECEIVED'),
    (3, 1, 'TC-SCREEN-27', 25.00, 120, 'CANCELLED'),
    (4, 2, 'OP-PAPER-A4', 400.00, 120, 'PENDING');

-- 12. Table MAKER_REFERENCE (clé composite article_id + maker_id)
INSERT INTO maker_reference (article_id, maker_id, art_mkr_reference, art_mkr_stock, art_mkr_sell_price, status)
VALUES
    (1, 1, 'MKR-CLAV-001',1,1.00, 'RECEIVED'),
    (2, 2, 'MKR-SOUR-002',10,10.00, 'RECEIVED'),
    (3, 1, 'MKR-ECR-003',2,2.00, 'PENDING'),
    (4, 3, 'MKR-CASQ-004',20,20.00, 'PENDING');

--13. Table ARTICLE_CATEGORY (clé composite article_id + category_id)
INSERT INTO article_category (article_id, category_id)
VALUES
    (1, 3),
    (2, 3),
    (3, 4),
    (4, 3),
    (5, 3);

-- 14. Table ACCOUNT_TYPE (référencée par ACCOUNT.account_type_id)
INSERT INTO account_type (acc_type_libelle)
VALUES
    ('superadmin'),
    ('admin'),
    ('user'),
    ('superuser');

-- Mot de passe Dev : Azerty123!
-- 15. Table CUSTOMER (référence ADDRESS)
INSERT INTO customer (ctm_first_name, ctm_last_name, ctm_email, password, ctm_phone, add_id, acc_type_id)
VALUES
    ('John', 'Doe', 'john.doe@email.com','$2a$10$Yc9vvYfNt6s2kmA3AWwzYuW5Z6lxMxxRyox4Hzb1IAbq74BvP9RW6', '+33754156322', 1, 2),
    ('Jane', 'Doe', 'jane.doe@email.com','$2a$10$Yc9vvYfNt6s2kmA3AWwzYuW5Z6lxMxxRyox4Hzb1IAbq74BvP9RW6', '+33758965410', 1, 3),
    ('Lily', 'Smith', 'lily.smith@email.com','$2a$10$Yc9vvYfNt6s2kmA3AWwzYuW5Z6lxMxxRyox4Hzb1IAbq74BvP9RW6', '0654125532', 2, 4);

-- 16. Table CART (référence CUSTOMER)
INSERT INTO cart (crt_ref, crt_status, crt_create_date, crt_last_modifie_date, customer_id)
VALUES
    ('CART-0001', 'OPEN', current_date, current_date, 1),
    ('CART-0002', 'VALIDATED', current_date, current_date, 1),
    ('CART-0003', 'OPEN', current_date, current_date, 2),
    ('CART-0004', 'OPEN', current_date, current_date, 3);

-- 17. Table ORDER_LINE (clé composite article_id + cart_id)
INSERT INTO order_line (article_id, cart_id, ord_ln_quantity)
VALUES
    (1, 1, 2),
    (2, 1, 1),
    (3, 2, 1),
    (4, 3, 3),
    (5, 3, 1),
    (1, 4, 4);