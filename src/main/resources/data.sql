-- 1. Table TVA (référencée par ARTICLE.tva_id)
INSERT INTO tva (tva_name, tva_taux)
VALUES
    ('taux normal', 0.2),
    ('taux intermédiaire', 0.1),
    ('taux particulier', 0.085),
    ('taux réduit', 0.055),
    ('taux super-réduit', 0.021);

-- 2. Table CATEGORY (référencée par ARTICLE.category_id)
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

-- 3. Table SUPPLIER (référencée par ARTICLE.supplier_id)
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

-- 5. Table SUPPLIER_REFERENCE (article, supplier, spl_ref_reference, spl_ref_stock)
INSERT INTO supplier_reference (article_id, supplier_id, spl_ref_reference, spl_ref_stock)
VALUES
        (1, 1, 'TC-USB-64', 120),
        (1, 2, 'OP-USB-64', 80),
        (2, 3, 'FP-CHAIR-ERG', 40),
        (3, 1, 'TC-SCREEN-27', 25),
        (4, 2, 'OP-PAPER-A4', 400);