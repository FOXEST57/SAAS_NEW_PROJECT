INSERT INTO tva (tva_name,tva_taux) VALUES ("taux normal",0.2);
INSERT INTO tva (tva_taux) VALUES ("taux intermédiaire",0.1);
INSERT INTO tva (tva_taux) VALUES ("taux particulier", 0.085);
INSERT INTO tva (tva_taux) VALUES ("taux réduit",0.055);
INSERT INTO tva (tva_taux) VALUES ("taux super-réduit",0.021);

INSERT INTO article (art_reference, art_name, art_description, art_price_exclude_taxes, art_stock, tva_id) VALUES
    ('REF-001', 'Clavier mécanique', 'Clavier mécanique switchs rouges, idéal pour le gaming.', 79.99, 25, 1);

INSERT INTO article (art_reference, art_name, art_description, art_price_exclude_taxes, art_stock, tva_id) VALUES
    ('REF-002', 'Souris ergonomique', 'Souris sans fil ergonomique pour réduire la fatigue du poignet.', 39.90, 40, 2);

INSERT INTO article (art_reference, art_name, art_description, art_price_exclude_taxes, art_stock, tva_id) VALUES
    ('REF-003', 'Écran 27 pouces', 'Écran IPS 27 pouces 144Hz, parfait pour le travail et le jeu.', 229.00, 12, 1);

INSERT INTO article (art_reference, art_name, art_description, art_price_exclude_taxes, art_stock, tva_id) VALUES
    ('REF-004', 'Casque audio', 'Casque circum-aural avec réduction de bruit active.', 119.50, 18, 3);

INSERT INTO article (art_reference, art_name, art_description, art_price_exclude_taxes, art_stock, tva_id) VALUES
    ('REF-005', 'Hub USB-C', 'Hub USB-C 7 ports compatible Mac et Windows.', 29.99, 50, 2);

INSERT INTO SUPPLIER
(SPL_NAME, SPL_EMAIL, SPL_PHONE, SPL_ADRESS)
VALUES
    ('HP France', 'contact@hp.fr', '+33102030402', '12 Avenue de la République, Lyon');
INSERT INTO SUPPLIER
(SPL_NAME, SPL_EMAIL, SPL_PHONE, SPL_ADRESS)
VALUES
    ('Lenovo France', 'contact@lenovo.fr', '+33102030403', '25 Rue Nationale, Lille');
INSERT INTO SUPPLIER
(SPL_NAME, SPL_EMAIL, SPL_PHONE, SPL_ADRESS)
VALUES
    ('Asus France', 'contact@asus.fr', '+33102030404', '8 Boulevard Victor Hugo, Strasbourg');
INSERT INTO SUPPLIER
(SPL_NAME, SPL_EMAIL, SPL_PHONE, SPL_ADRESS)
VALUES
    ('Logitech France', 'contact@logitech.fr', '+33102030405', '15 Rue du Commerce, Nantes');