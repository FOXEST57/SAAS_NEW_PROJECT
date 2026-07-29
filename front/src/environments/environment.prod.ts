/**
 * Configuration de production.
 *
 * Renseignez ici l'URL publique de votre API Spring Boot.
 * Le backend expose déjà `@CrossOrigin` sur tous ses contrôleurs.
 */
export const environment = {
  production: true,
  apiBaseUrl: 'http://localhost:8080',
  banApiUrl: 'https://api-adresse.data.gouv.fr',
  company: {
    name: 'Klimafact SARL',
    tagline: 'Climatisation & Chauffage',
    address: '12 rue de la Paix, 69001 Lyon',
    siret: '000 000 000 00000',
    tvaNumber: 'FR00000000000',
    email: 'contact@klimafact.fr',
    phone: '+33 4 00 00 00 00',
    iban: 'FR76 0000 0000 0000 0000 0000 000',
  },
};
