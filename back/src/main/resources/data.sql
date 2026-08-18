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
INSERT INTO article (art_reference, art_name, art_description, art_price_exclude_taxes,is_Active,art_create_date, art_update_date, tva_id)
VALUES
    ('ref-001', 'Clavier mécanique', 'Clavier mécanique switchs rouges, idéal pour le gaming.', 79.99,true,current_date, current_date, 1),
    ('ref-002', 'Souris ergonomique', 'Souris sans fil ergonomique pour réduire la fatigue du poignet.', 39.90, false ,current_date, current_date, 1),
    ('ref-003', 'Écran 27 pouces', 'Écran IPS 27 pouces 144Hz, parfait pour le travail et le jeu.', 229.00, false,current_date, current_date, 1),
    ('ref-004', 'Casque audio', 'Casque circum-aural avec réduction de bruit active.', 119.50, false,current_date, current_date, 3),
    ('ref-005', 'Hub USB-C', 'Hub USB-C 7 ports compatible Mac et Windows.', 29.99,true,current_date, current_date, 2);

-- 11. Table SUPPLIER_REFERENCE (article, supplier, spl_ref_reference,supplier_price, spl_ref_stock)
INSERT INTO supplier_reference (article_id, supplier_id, spl_ref_reference, spl_ref_sell_price, spl_ref_stock, status, spl_ref_create_date, spl_ref_update_date)
VALUES
    (1, 1, 'TC-USB-64', 120.00, 120, 'RECEIVED', current_date, current_date),
    (1, 2, 'OP-USB-64', 80.00, 120, 'RECEIVED', current_date, current_date),
    (2, 3, 'FP-CHAIR-ERG', 40.00, 120, 'ACCEPTED', current_date, current_date),
    (3, 1, 'TC-SCREEN-27', 25.00, 120, 'CANCELLED', current_date, current_date),
    (4, 2, 'OP-PAPER-A4', 400.00, 120, 'PENDING', current_date, current_date);

-- 12. Table MAKER_REFERENCE (clé composite article_id + maker_id)
INSERT INTO maker_reference (article_id, maker_id, art_mkr_reference, art_mkr_stock, art_mkr_sell_price, status, art_mkr_create_date, art_mkr_update_date)
VALUES
    (1, 1, 'MKR-CLAV-001',1,1.00, 'ACCEPTED', current_date, current_date),
    (2, 2, 'MKR-SOUR-002',10,10.00, 'RECEIVED', current_date, current_date),
    (3, 1, 'MKR-ECR-003',2,2.00, 'PENDING', current_date, current_date),
    (4, 3, 'MKR-CASQ-004',20,20.00, 'PENDING', current_date, current_date);

--13. Table ARTICLE_CATEGORY (clé composite article_id + category_id)
INSERT INTO article_category (article_id, category_id)
VALUES
    (1, 3),
    (2, 3),
    (3, 4),
    (4, 3),
    (5, 3);


-- Mot de passe Dev : Azerty123!
-- 14. Table CUSTOMER (référence ADDRESS)
INSERT INTO customer (ctm_first_name, ctm_last_name, ctm_email, password, ctm_phone, add_id, account_type)
VALUES
    ('John', 'Doe', 'john.doe@email.com','$2a$10$Yc9vvYfNt6s2kmA3AWwzYuW5Z6lxMxxRyox4Hzb1IAbq74BvP9RW6', '+33754156322', 1, 'USER'),
    ('Jane', 'Doe', 'jane.doe@email.com','$2a$10$Yc9vvYfNt6s2kmA3AWwzYuW5Z6lxMxxRyox4Hzb1IAbq74BvP9RW6', '+33758965410', 1, 'OWNER'),
    ('Lily', 'Smith', 'lily.smith@email.com','$2a$10$Yc9vvYfNt6s2kmA3AWwzYuW5Z6lxMxxRyox4Hzb1IAbq74BvP9RW6', '0654125532', 2, 'EMPLOYEE');

-- 15. Table CART (référence CUSTOMER)
INSERT INTO cart (crt_ref, crt_status, crt_create_date, crt_last_modifie_date, creator_id,receiver_email)
VALUES
    ('CART-0001', 'OPEN', current_date, current_date, 1, 'receiver@email.com'),
    ('CART-0002', 'VALIDATED', current_date, current_date, 1,'receiver@email.com'),
    ('CART-0003', 'OPEN', current_date, current_date, 2,'receiver@email.com'),
    ('CART-0004', 'OPEN', current_date, current_date, 3,'receiver@email.com');

-- 16. Table ORDER_LINE (clé composite article_id + cart_id)
INSERT INTO order_line (article_id, cart_id, ord_ln_quantity)
VALUES
    (1, 1, 2),
    (2, 1, 1),
    (3, 2, 1),
    (4, 3, 3),
    (5, 3, 1),
    (1, 4, 4);

-- 17. Table INVENTORY (référence ARTICLE)
INSERT INTO inventory (article_art_id, inv_stock, inv_date)
VALUES
    (1, 100, '2024-06-01'),
    (2, 150, '2024-06-01'),
    (3, 200, '2024-06-01'),
    (4, 250, '2024-06-01'),
    (5, 300, '2024-06-01');

-- 18. Table QUOTE (référence CART)
INSERT INTO quote (
    qot_expiration_date,
    cart_crt_id,
    creator_id,
    qot_created_date,
    qot_modified_date,
    qot_parent_qot_id,
    qot_number,
    qot_pathpdf,
    receiver_email,
    qot_status
)
VALUES
    (
                CURRENT_DATE + 30,
                1,
                1,
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP,
                NULL,
                'DEV-2026-0001',
                'DEV-2026-0001.pdf',
                'john.doe@email.com',
                'ACCEPTED'
    ),
    (
                CURRENT_DATE + 30,
                2,
                1,
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP,
                NULL,
                'DEV-2026-0002',
                'DEV-2026-0002.pdf',
                'jane.doe@email.com',
                'ACCEPTED'
    ),
    (
                CURRENT_DATE + 30,
                3,
                2,
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP,
                NULL,
                'DEV-2026-0003',
                'DEV-2026-0003.pdf',
                'lily.smith@email.com',
                'ACCEPTED'
    );

-- 19. Table QUOTE_LINE (référence QUOTE)
INSERT INTO quote_line (
    qot_ln_priceht,
    qot_ln_quantity,
    tva_rate,
    quote_qot_id,
    article_name,
    article_ref
)
VALUES
    (79.99, 2, 0.20, 1, 'Clavier mécanique', 'ref-001'),
    (39.90, 1, 0.20, 1, 'Souris ergonomique', 'ref-002'),
    (229.00, 1, 0.20, 2, 'Écran 27 pouces', 'ref-003'),
    (119.50, 2, 0.20, 3, 'Casque audio', 'ref-004');

-- 20. Table COMMAND (référence QUOTE)
INSERT INTO command (
    cmd_create_date,
    cmd_modified_date,
    creator_id,
    quote_id,
    receiver_email,
    cmd_status,
    cmd_reference
)
VALUES
    (
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP,
                1,
                1,
                'john.doe@email.com',
                'ACCEPTED',
                'TEST1'
    ),
    (
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP,
                1,
                2,
                'jane.doe@email.com',
                'ACCEPTED',
                'TEST2'
    ),
    (
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP,
                2,
                3,
                'lily.smith@email.com',
                'ACCEPTED',
                'TEST3'
    );

-- 21. Table INVOICE (référence COMMAND)
INSERT INTO invoice (
    command_cmd_id,
    creator_id,
    invoice_created_date,
    invoice_modified_date,
    invoice_number,
    invoice_pathpdf,
    receiver_email,
    invoice_status
)
VALUES
    (
        1,
        1,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'FAC-2026-0001',
        'FAC-2026-0001.pdf',
        'john.doe@email.com',
        'PARTIALLY_PAID'
    ),
    (
        2,
        1,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'FAC-2026-0002',
        'FAC-2026-0002.pdf',
        'jane.doe@email.com',
        'ISSUED'
    ),
    (
        3,
        2,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'FAC-2026-0003',
        'FAC-2026-0003.pdf',
        'lily.smith@email.com',
        'PAID'
    );

-- 22. Table INVOICE_LINE (référence INVOICE)
INSERT INTO invoice_line (
    inv_ln_priceht,
    inv_ln_quantity,
    tva_rate,
    invoice_invoice_id,
    article_name,
    article_ref
)
VALUES
    (79.99, 2, 0.20, 1, 'Clavier mécanique', 'ref-001'),
    (39.90, 1, 0.20, 1, 'Souris ergonomique', 'ref-002'),
    (229.00, 1, 0.20, 2, 'Écran 27 pouces', 'ref-003'),
    (119.50, 2, 0.20, 3, 'Casque audio', 'ref-004');

-- 23. Table PAYMENT (référence INVOICE)
-- Facture 1 : deux paiements
-- 100 € d'acompte par virement
-- 99,88 € de solde par carte bancaire
INSERT INTO payment (
    pay_account,
    pay_amount,
    invoice_id,
    pay_created_date,
    pay_type
)
VALUES
    (
        true,
        100.00,
        1,
        CURRENT_TIMESTAMP,
        'BANK_TRANSFER'
    ),
    (
        false,
        5.00,
        1,
        CURRENT_TIMESTAMP,
        'CREDIT_CARD'
    );

-- Facture 2 : aucun paiement
-- Permet de tester une facture sans paiement.

-- Facture 3 : paiement intégral par prélèvement
INSERT INTO payment (
    pay_account,
    pay_amount,
    invoice_id,
    pay_created_date,
    pay_type
)
VALUES
    (
        false,
        239.00,
        3,
        CURRENT_TIMESTAMP,
        'DIRECT_DEBIT'
    );