-- 1. Table POSTAL_CODE (référencée par CITY.postal_code_id)
INSERT INTO postal_code (pcode_name)
VALUES
    ('69000'),
    ('59000'),
    ('67000'),
    ('44000'),
    ('57000');

-- 2. Table CITY (référence POSTAL_CODE, et référencée par CUSTOMER.city_id)
INSERT INTO city (city_name, postal_code_id)
VALUES
    ('Lyon', 1),
    ('Lille', 2),
    ('Strasbourg', 3),
    ('Nantes', 4),
    ('Metz', 5);

-- 3. Table TVA (référencée par ARTICLE.tva_id)
INSERT INTO tva (tva_name, tva_taux)
VALUES
    ('taux normal', 0.2),
    ('taux intermédiaire', 0.1),
    ('taux particulier', 0.085),
    ('taux réduit', 0.055),
    ('taux super-réduit', 0.021);

-- 4. Table CATEGORY (référencée par ARTICLE.category_id)
INSERT INTO category (cat_name, cat_parent_id)
VALUES
    ('High-Tech', NULL),
    ('Mobilier', NULL),
    ('Accessoire informatique',1),
    ('Moniteur', 1),
    ('Mode', NULL),
    ('Beauté & Santé', NULL),
    ('Jeux & Jouets', NULL),
    ('Bricolage', NULL),
    ('Auto & Moto', NULL);

-- 5. Table SUPPLIER (référencée par ARTICLE.supplier_id)
INSERT INTO supplier (spl_name, spl_email, spl_phone, spl_address)
VALUES
    ('HP France', 'contact@hp.fr', '+33102030402', '12 Avenue de la République, Lyon'),
    ('Lenovo France', 'contact@lenovo.fr', '+33102030403', '25 Rue Nationale, Lille'),
    ('Asus France', 'contact@asus.fr', '+33102030404', '8 Boulevard Victor Hugo, Strasbourg'),
    ('Logitech France', 'contact@logitech.fr', '+33102030405', '15 Rue du Commerce, Nantes');

-- 4. Table ARTICLE (référence TVA et SUPPLIER, doit donc être insérée en dernier)
INSERT INTO article (art_reference, art_name, art_description, art_price_exclude_taxes, art_stock, tva_id, category_id)
VALUES
    ('REF-001', 'Clavier mécanique', 'Clavier mécanique switchs rouges, idéal pour le gaming.', 79.99, 25, 1, 1),
    ('REF-002', 'Souris ergonomique', 'Souris sans fil ergonomique pour réduire la fatigue du poignet.', 39.90, 40, 1, 1),
    ('REF-003', 'Écran 27 pouces', 'Écran IPS 27 pouces 144Hz, parfait pour le travail et le jeu.', 229.00, 12, 1, 1),
    ('REF-004', 'Casque audio', 'Casque circum-aural avec réduction de bruit active.', 119.50, 18, 3, 1),
    ('REF-005', 'Hub USB-C', 'Hub USB-C 7 ports compatible Mac et Windows.', 29.99, 50, 2,1);

-- 5. Table ARTICLE_SUPPLIER (article, supplier, art_spl_reference, art_spl_stock)
INSERT INTO article_supplier (article_id, supplier_id, art_spl_reference, art_spl_stock)
VALUES
        (1, 1, 'TC-USB-64', 120),
        (1, 2, 'OP-USB-64', 80),
        (2, 3, 'FP-CHAIR-ERG', 40),
        (3, 1, 'TC-SCREEN-27', 25),
        (4, 2, 'OP-PAPER-A4', 400);