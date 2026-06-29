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

-- 6. Table ARTICLE (référence TVA et SUPPLIER, doit donc être insérée en dernier)
INSERT INTO article (art_reference, art_name, art_description, art_price_exclude_taxes, art_stock, tva_id, supplier_id, category_id)
VALUES
    ('REF-001', 'Clavier mécanique', 'Clavier mécanique switchs rouges, idéal pour le gaming.', 79.99, 25, 1, 1, 1),
    ('REF-002', 'Souris ergonomique', 'Souris sans fil ergonomique pour réduire la fatigue du poignet.', 39.90, 40, 2, 1, 1),
    ('REF-003', 'Écran 27 pouces', 'Écran IPS 27 pouces 144Hz, parfait pour le travail et le jeu.', 229.00, 12, 1, 2, 1),
    ('REF-004', 'Casque audio', 'Casque circum-aural avec réduction de bruit active.', 119.50, 18, 3, 3, 1),
    ('REF-005', 'Hub USB-C', 'Hub USB-C 7 ports compatible Mac et Windows.', 29.99, 50, 2, 3,1);

-- 7. Table CUSTOMER (référence CITY)
INSERT INTO customer (ctm_first_name, ctm_last_name, ctm_email, ctm_phone, ctm_address, city_id)
VALUES
INSERT INTO customer (ctm_first_name, ctm_last_name, ctm_email, ctm_phone, ctm_address, city_id)
VALUES
    ('Jean', 'Dupont', 'jean.dupont@example.com', '0612345678', '12 Rue de la République', 1),
    ('Marie', 'Martin', 'marie.martin@example.com', '0623456789', '45 Avenue des Champs', 2),
    ('Pierre', 'Bernard', 'pierre.bernard@example.com', '0634567890', '78 Boulevard de la Paix', 3),
    ('Sophie', 'Durand', 'sophie.durand@example.com', '0745678901', '32 Rue des Fleurs', 4),
    ('Luc', 'Moreau', 'luc.moreau@example.com', '0356789012', '15 Allée des Tilleuls', 5),
    ('Anne', 'Lefèvre', 'anne.lefevre@example.com', '0667890123', '9 Rue du Commerce', 1);