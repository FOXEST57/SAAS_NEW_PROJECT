/**
 * Configuration de production.
 *
 * `apiBaseUrl` est **vide** : les appels partent donc en relatif (`/article`,
 * `/logIn`…). C'est ce qu'il faut lorsque l'application compilée est servie par
 * Spring Boot lui-même, depuis `src/main/resources/static/` — front et API
 * partagent alors la même origine, et la question du CORS ne se pose plus.
 *
 * Si vous préférez héberger le front ailleurs (nginx, IIS, un CDN), remettez
 * ici l'URL publique de l'API — par exemple `https://api.klimafact.fr` — et
 * assurez-vous que cette origine est autorisée dans `SecurityConfig`.
 */
export const environment = {
  production: true,
  apiBaseUrl: '',
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
