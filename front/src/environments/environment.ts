/**
 * Configuration de développement.
 *
 * `apiBaseUrl` pointe sur `/api`, qui est réécrit vers `http://localhost:8080`
 * par le proxy Angular (voir `proxy.conf.json`). Cela évite tout problème de
 * CORS pendant le développement.
 */
export const environment = {
  production: false,
  apiBaseUrl: '/api',
  /** API Adresse (Base Adresse Nationale) — service public, sans clé d'API. */
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
